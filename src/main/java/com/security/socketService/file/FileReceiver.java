package com.security.socketService.file;

import com.security.keyutil.AES256Util;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class FileReceiver extends Thread {
    Socket socket;
    DataInputStream dis = null;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    private String aesKey;
    String path;

    PublicKey publicKey;

    public static String decryptedMsg = "";
    public static String staticGetDs = "";
    public static boolean dsEqaul = false;

    public FileReceiver(Socket socket, String aesKey, String path, PublicKey publicKey) {
        this.socket = socket;
        this.aesKey = aesKey;
        this.path = path;
        this.publicKey = publicKey;
    }

    @Override
    public void run() {

        try {
            dis = new DataInputStream(socket.getInputStream());
            String type = dis.readUTF();

            // type 값("file" 또는 "msg")을 기준으로 파일이 전송됐는지 문자열이 전송됐는지 구분
            String result = fileWrite(dis);
            System.out.println(result);

        } catch (IOException e) {
            System.out.println("run() Fail");
            e.printStackTrace();
        }
    }

    private String fileWrite(DataInputStream dis) {
        String result = "fail";
        String filePath = path;

        try {
            System.out.println("파일 수신 작업을 시작합니다.");

            // 파일 명을 전송 받고 파일명 수정
            String fileNm = dis.readUTF();
            System.out.println("파일명 " + fileNm + "을 전송받았습니다.");

            // 파일을 생성하고 파일에 대한 출력 스트림 생성
            File file = new File(filePath + "/" + fileNm);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            System.out.println(fileNm + "파일을 생성");

            int size = 4096;

            // 전체 암호화 진행
            byte[] wholeData = new byte[409600000];
            int wholeLen;
            String wholeString = "";
            int index = 0;
            System.out.println(wholeData);
            while ((wholeLen = dis.read(wholeData)) != -1) {
                String newString = new String(wholeData);
                newString = newString.substring(index, wholeLen);
                System.out.println(newString.length());
                wholeString = wholeString + newString;
                if (wholeLen < 409600000) break;
                ;
            }
            System.out.println("!!");
            System.out.println(index);
            System.out.println("decrypt");
//            System.out.println(wholeString);
            System.out.println("length : " + wholeString.length());
            decryptedMsg = wholeString;
            String decrypt = AES256Util.decrypt(wholeString, aesKey);

            // Digital Signature
            String ds = publicKey.toString();
            int dsSize = ds.length();
            int decryptSize = decrypt.length();

            String getDs = decrypt.substring(decryptSize - dsSize, decryptSize);
            if(getDs.equals(ds)){
                dsEqaul = true;
                staticGetDs = getDs;
            }

            decrypt = decrypt.substring(0, decryptSize - dsSize);

            // resume
            result = decrypt;
            byte[] newData = decrypt.getBytes();

            int count = newData.length / size + 1;
            byte[] data = new byte[size];
            System.out.println("!!");
            for (int i = 0; i < count; i++) {
//                byte[] sendData = Arrays.copyOfRange(newData,i*size,(i+1)*size);
//                bos.write(sendData, 0, size);
//                bos.write(newData, 0, newData.length);
                if (i == count - 1) {
                    bos.write(newData, 0, newData.length);
                } else {
                    bos.write(newData, 0, size);
                }
            }
//            int wholeLen = dis.read(wholeData);
//            System.out.println("==================================");
//            System.out.println(wholeLen);
//            byte[] data = new byte[size];
//            while((len = dis.read(data)) != -1){
//                bos.write(data, 0, len);
//            }

            bos.flush();

            System.out.println("파일 수신 작업 완료");
            System.out.println("받은 파일 사이즈 : " + file.length());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try{
//                bos.close();
//                fos.close();
//                dis.close();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
        }
        return result;
    }
}
