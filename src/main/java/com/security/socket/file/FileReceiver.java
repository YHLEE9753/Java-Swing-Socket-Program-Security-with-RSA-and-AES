package com.security.socket.file;

import java.io.*;
import java.net.Socket;

public class FileReceiver extends Thread {
    Socket socket;
    DataInputStream dis = null;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;

    public FileReceiver(Socket socket) {
        this.socket = socket;
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
        String result;
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
            byte[] data = new byte[size];
            while((len = dis.read(data)) != -1){
                bos.write(data ,0 ,len);
            }
            bos.flush();
            result = "SUCCESS";

            System.out.println("파일 수신 작업 완료");
            System.out.println("받은 파일 사이즈 : " + file.length());
        }catch (IOException e){
            e.printStackTrace();
            result = "ERROR";
        }finally {
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
