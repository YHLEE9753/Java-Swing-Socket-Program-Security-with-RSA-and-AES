package com.security.socket.file;

import com.security.keyutil.AES256Util;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;

public class FileReceiver extends Thread {
    Socket socket;
    DataInputStream dis = null;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    private String aesKey;

    public FileReceiver(Socket socket, String aesKey) {
        this.socket = socket;
        this.aesKey =  aesKey;
    }

    @Override
    public void run() {

        try {
            dis = new DataInputStream(socket.getInputStream());
            String type = dis.readUTF();

            // type 값("file" 또는 "msg")을 기준으로 파일이 전송됐는지 문자열이 전송됐는지 구분
            String result = fileWrite(dis);
            System.out.println(result);

        }catch (IOException e){
            System.out.println("run() Fail");
            e.printStackTrace();
        }
    }

    private String fileWrite(DataInputStream dis) {
        String result = null;
        String filePath = "C:/receiver";

        try{
            System.out.println("파일 수신 작업을 시작합니다.");

            // 파일 명을 전송 받고 파일명 수정
            String fileNm = dis.readUTF();
            System.out.println("파일명 " + fileNm + "을 전송받았습니다.");

            // 파일을 생성하고 파일에 대한 출력 스트림 생성
            File file = new File(filePath + "/" + fileNm);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            System.out.println(fileNm + "파일을 생성");

            // 바이트 데이터를 전송받으면서 기록
            int len;
            int size = 4096;

            // 전체 암호화 진행
            byte[] wholeData = new byte[10000000];
            int wholeLen = dis.read(wholeData);
            String newString = new String(wholeData);
            newString = newString.substring(0, wholeLen);
            System.out.println("decrypt");
            System.out.println(newString);
            String decrypt = AES256Util.decrypt(newString, aesKey);
            byte[] newData = decrypt.getBytes();

            int count = wholeLen/size + 1;
            byte[] data = new byte[size];
            for(int i = 0;i<count;i++){
//                byte[] sendData = Arrays.copyOfRange(newData,i*size,(i+1)*size);
//                bos.write(sendData, 0, size);
                  bos.write(newData, 0, newData.length);
            }



//            byte[] data = new byte[size];
//            while((len = dis.read(data)) != -1){
//                System.out.println(data);
//                System.out.println(data.length);
//                String newString = new String(data);
//                String decrypt = AES256Util.decrypt(newString, aesKey);
//                byte[] newData = decrypt.getBytes();
//                bos.write(newData, 0, newData.length);
//                bos.write(data, 0, len);
//            }
            bos.flush();
            result = "SUCCESS";

            System.out.println("파일 수신 작업 완료");
            System.out.println("받은 파일 사이즈 : " + file.length());
        }catch (IOException e){
            e.printStackTrace();
            result = "ERROR";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                bos.close();
                fos.close();
                dis.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }
}
