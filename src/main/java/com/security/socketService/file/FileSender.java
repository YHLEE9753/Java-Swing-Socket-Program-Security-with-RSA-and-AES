package com.security.socketService.file;
import com.security.keyutil.AES256Util;

import java.io.*;
import java.net.Socket;

public class FileSender extends Thread{
    private String filePath;
    private String fileNm;
    private Socket socket;
    private DataOutputStream dos;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private String aesKey;

    public FileSender(Socket socket, String filePath, String fileNm, String aesKey){
        this.socket = socket;
        this.fileNm = fileNm;
        this.filePath = filePath;
        this.aesKey = aesKey;

        try{
            // 데이터 전송용 스트림 생성
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try{
            // 파일 전송을 서버에 알린다.
            dos.writeUTF("file");
            dos.flush();

            // 전송할 파일을 읽어서 Socket Server 에 전송
            String result = fileRead(dos);
            // 암호화 진행

            System.out.println(result);
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                dos.close();
                bis.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private String fileRead(DataOutputStream dos) {
        String result = null;

        try{
            dos.writeUTF(fileNm);

            // 파일을 읽어서 서버에 전송
            File file = new File(filePath + "/" + fileNm);
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);

            int size = 4096;

            // 전체 암호화 진행
            byte[] wholeData = new byte[100000000];
            int wholeLen = bis.read(wholeData);
            String newString = new String(wholeData);
            newString = newString.substring(0, wholeLen);

            String encrypt = AES256Util.encrypt(newString, aesKey);
            System.out.println("encrypt");
//            System.out.println(encrypt);
            System.out.println("length : "+encrypt.length());
            byte[] newData = encrypt.getBytes();
            System.out.println("==================================");
            System.out.println(newData.length);

            int count = newData.length/size + 1;
            System.out.println(count);
            for(int i = 0;i<count;i++){
//                byte[] sendData = Arrays.copyOfRange(newData,i*size,(i+1)*size);
//                dos.write(sendData, 0, newData.length);
                if(i==count-1){
                    dos.write(newData, 0, newData.length);
                }else{
                    dos.write(newData, 0, size);
                }
            }

//            while((len = bis.read(data)) != -1){
//                dos.write(data, 0 ,len);
//            }

            // 서버에 전송
            dos.flush();

            result = "SUCCESS";
        }catch(IOException e){
            e.printStackTrace();
            result = "ERROR";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                fis.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }
}
