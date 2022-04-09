package com.security.socketService.file;

import com.security.keyutil.AES256Util;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;

public class FileSender extends Thread {
    // set field to set connection
    private String filePath;
    private String fileNm;
    private Socket socket;
    private DataOutputStream dos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private String aesKey;
    public String encryptedFile;
    public static String encryptedMsg;
    PublicKey publickey;

    // constructor
    public FileSender(Socket socket, String filePath, String fileNm, String aesKey, PublicKey publicKey) {
        this.socket = socket;
        this.fileNm = fileNm;
        this.filePath = filePath;
        this.aesKey = aesKey;
        this.publickey = publicKey;
        System.out.println("!!!?????");
        System.out.println(socket);

        try {
            // make stream to send data
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println(dos);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            // talk to server that i will send file
            dos.writeUTF("file");
            dos.flush();

            // read file and send to Socket server
            String result = fileRead(dos);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // do not close because socket should not be closed
//            try{
////                dos.close();
////                bis.close();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
        }
    }

    private String fileRead(DataOutputStream dos) {
        String result = "";

        try {
            dos.writeUTF(fileNm);

            // read file and send to server
            File file = new File(filePath + "/" + fileNm);
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            int size = 4096;

            // read file
            byte[] wholeData = new byte[409600000];
            while (true) {
                int wholeLen = bis.read(wholeData);
                if (wholeLen == -1) break;
                String newString = new String(wholeData);
                newString = newString.substring(0, wholeLen);

                // Check Digital Signature using other side's public key
                String ds = publickey.toString();
                newString += ds;

                // file encryption
                String encrypt = AES256Util.encrypt(newString, aesKey);
                encryptedFile = encrypt;
                encryptedMsg = encrypt;


                // change data String to byte
                byte[] newData = encrypt.getBytes();

                // check step
                int count = newData.length / size + 1;
                // write data using step
                for (int i = 0; i < count; i++) {
//                byte[] sendData = Arrays.copyOfRange(newData,i*size,(i+1)*size);
//                dos.write(sendData, 0, newData.length);
                    if (i == count - 1) {
                        dos.write(newData, 0, newData.length);
                    } else {
                        dos.write(newData, 0, size);
                    }
                }
            }
            // using flush to send
            dos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // do not close because socket should not be closed
//            try{
//                fis.close();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
        }
        return result;
    }
}
