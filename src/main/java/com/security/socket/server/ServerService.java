package com.security.socket.server;

import com.security.keyutil.AES256Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;

public class ServerService {
    final int port = 5050;
    ServerSocket ssk;
    Socket sock;
    byte[] key;

    //서버 소켓을 생성
    public ServerService() throws IOException {
        this.ssk = new ServerSocket(port);
        this.key = key;
    }

    //sock 생성
    public Socket makeSock() throws IOException {
        sock = ssk.accept();
        System.out.println("서버와 접속이 되었습니다.");
        System.out.println("Client ip :"+ sock.getInetAddress());
        return sock;
    }

    // PublicKey client 에게 전달
    public void sendPublicKeyToClient(PublicKey publicKey) throws IOException {
        byte[] byte_pubkey = publicKey.getEncoded();
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
        byte[] encoded = str_key.getBytes();
        OutputStream output_data = sock.getOutputStream();
        output_data.write(encoded);
    }

    // 암호화된 AES 키를 client 에게 전달
    public void sendAESKeyToClient(String aesKey) throws IOException {
        OutputStream output_data = sock.getOutputStream();
        output_data.write(aesKey.getBytes());
    }

    // 전달받은 encrypted AES 키 확인
    public String checkEncryptedAES() throws Exception {
        InputStream input_data = sock.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        String data = new String(receiveBuffer);
        data = data.substring(0,size);
        return data;

    }

    //클라이언트와 연결을 하기위한 스트림을 생성한다.
    public void sendMsgToClient(String msg, String key) throws Exception {
        String encrypt = AES256Util.encrypt(msg,key);

        OutputStream output_data = sock.getOutputStream();
        output_data.write(encrypt.getBytes());
    }

    // 전달받은 msg 확인
    public String CheckMsg(String key) throws Exception {
        InputStream input_data = sock.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);

        String data = new String(receiveBuffer);

        data = AES256Util.decrypt(data.substring(0,size),key);
        return data;
    }

    public int getPort() {
        return port;
    }


    public Socket getSock() {
        return sock;
    }

}
