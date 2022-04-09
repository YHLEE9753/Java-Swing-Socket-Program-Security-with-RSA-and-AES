package com.security.socketService.file;

import com.security.keyutil.AES256Util;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class FileReceiver extends Thread {
    // set field to set connection
    Socket socket;
    DataInputStream dis = null;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
    private String aesKey;
    String path;

    PublicKey publicKey;

    // information to deliever data in swing
    public static String decryptedMsg = "";
    public static String staticGetDs = "";
    // Digital Signature is same or not
    public static boolean dsEqaul = false;

    // Constructor
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

            // get whether data is sent or not
            String result = fileWrite(dis);

        } catch (IOException e) {
            System.out.println("run() Fail");
            e.printStackTrace();
        }
    }

    private String fileWrite(DataInputStream dis) {
        String result = "";
        String filePath = path;

        try {
            System.out.println("파일 수신 작업을 시작합니다.");

            // get file name
            String fileNm = dis.readUTF();
            System.out.println("파일명 " + fileNm + "을 전송받았습니다.");

            // make File and using Stream to send
            File file = new File(filePath + "/" + fileNm);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            System.out.println(fileNm + "파일을 생성");

            // set size
            int size = 4096;

            // this is whole encryption step
            byte[] wholeData = new byte[409600000];
            int wholeLen;
            String wholeString = "";
            int index = 0;
            System.out.println(wholeData);
            // get String until stream is ended
            while ((wholeLen = dis.read(wholeData)) != -1) {
                String newString = new String(wholeData);
                newString = newString.substring(index, wholeLen);
                System.out.println(newString.length());
                wholeString = wholeString + newString;
                if (wholeLen < 409600000) break;
            }

            // this is whole String
            decryptedMsg = wholeString;
            // decrypt whole String
            String decrypt = AES256Util.decrypt(wholeString, aesKey);

            // Digital Signature (using your own public key
            String ds = publicKey.toString();
            // using String of Public Key
            int dsSize = ds.length();
            int decryptSize = decrypt.length();

            // divide digital signature and original text
            String getDs = decrypt.substring(decryptSize - dsSize, decryptSize);
            // check whether integrity is safe.
            if(getDs.equals(ds)){
                dsEqaul = true;
                staticGetDs = getDs;
            }
            decrypt = decrypt.substring(0, decryptSize - dsSize);

            // resume
            result = decrypt;
            // change String to byte array
            byte[] newData = decrypt.getBytes();

            int count = newData.length / size + 1;
            byte[] data = new byte[size];
            // write stream
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

            // using flush to finish
            bos.flush();

            System.out.println("파일 수신 작업 완료");
            System.out.println("받은 파일 사이즈 : " + file.length());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // do not close because socket should not be closed
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
