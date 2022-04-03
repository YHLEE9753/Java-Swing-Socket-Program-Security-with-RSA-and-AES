package com.security.socketService.server;

import com.security.keyutil.AES256Util;
import com.security.keyutil.RSAUtil;
import com.security.user.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class ServerService {
    final int port = 5050;
    ServerSocket ssk;
    Socket sock;
    byte[] key;
    public Server server;

    //서버 소켓을 생성
    public ServerService() throws IOException {
        this.ssk = new ServerSocket(port);
        this.server = new Server();
        server.port = port;
        this.key = key;
    }

    //sock 생성
    public Socket makeSock() throws IOException {
        sock = ssk.accept();
        System.out.println("서버와 접속이 되었습니다.");
        System.out.println("Client ip :"+ sock.getInetAddress());
        server.ipAddress = sock.getInetAddress();
        server.connection = true;
        return sock;
    }

    // PublicKey PrivateKey 생성 후 저장
    public void makePublicKeyAndPrivateKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = RSAUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        server.privateKey = privateKey;
        server.publicKey = publicKey;
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
    public void checkEncryptedAES() throws Exception {
        InputStream input_data = sock.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        String data = new String(receiveBuffer);
        data = data.substring(0,size);
        String decryptRSA = RSAUtil.decryptRSA(data, server.privateKey);
        server.aesKey = decryptRSA;

    }

    //클라이언트와 연결을 하기위한 스트림을 생성한다.
    public void sendMsgToClient(String msg, String key) throws Exception {
        int index = server.chatHistory.size()+1;
        server.chatHistory.put("server"+index, msg);
        String encrypt = AES256Util.encrypt(msg,key);
        server.encryptedMsg = encrypt;

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
        int index = server.chatHistory.size()+1;
        server.chatHistory.put("client"+index, data);
        return data;
    }

    public int getPort() {
        return port;
    }


    public Socket getSock() {
        return sock;
    }

}
