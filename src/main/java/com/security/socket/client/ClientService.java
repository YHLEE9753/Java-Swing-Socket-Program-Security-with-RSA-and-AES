package com.security.socket.client;

import com.security.keyutil.AES256Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClientService {
    private Socket sk;
    private byte[] key;
    private String RSAPublicKey;
    private PublicKey RSAPublicKeyNotStr;
    private String aesKey;

    // 연길시에 소켓이 생성된다.
    public ClientService() throws IOException {
        this.sk = new Socket("127.0.0.1", 5050);
    }

    // 서버로 부터 Public Key 키 확인
    public PublicKey getPublicKeyFromServer() throws IOException {
        InputStream input_data = sk.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        String data = new String(receiveBuffer);
        data = data.substring(0,size);

        PublicKey pubKey = null;
        try {
            String publicK = data;
            byte[] publicBytes = Base64.getDecoder().decode(publicK);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        RSAPublicKeyNotStr = pubKey;
        return pubKey;
    }

    // 암호화된 AES 키를 client 에게 전달
    public void sendAESKeyToServer(String aesKey) throws IOException {
        OutputStream output_data = sk.getOutputStream();
        output_data.write(aesKey.getBytes());
    }

    // 전달받은 encrypted AES 키 확인
    public String checkEncryptedAES() throws Exception {
        InputStream input_data = sk.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        String data = new String(receiveBuffer);
        data = data.substring(0,size);
        return data;

    }


    // 전달받은 msg 확인
    public String checkMsg(String key) throws Exception {
        InputStream input_data = sk.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        String data = new String(receiveBuffer);
        data = AES256Util.decrypt(data.substring(0,size), key);
        return data;

    }

    //서버와 연결을 하기위한 스트림을 생성한다.
    public void sendMsgToServer(String msg, String key) throws Exception {
        String encrypt = AES256Util.encrypt(msg,key);
        OutputStream output_data = sk.getOutputStream();
        output_data.write(encrypt.getBytes());
    }

    public Socket getSk() {
        return sk;
    }

    public byte[] getKey() {
        return key;
    }

    public String getRSAPublicKey() {
        return RSAPublicKey;
    }

    public PublicKey getRSAPublicKeyNotStr() {
        return RSAPublicKeyNotStr;
    }
}

