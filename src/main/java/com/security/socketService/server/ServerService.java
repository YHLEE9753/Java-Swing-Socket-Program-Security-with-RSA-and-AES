package com.security.socketService.server;

import com.security.keyutil.AES256Center;
import com.security.keyutil.AES256Util;
import com.security.keyutil.RSAUtil;
import com.security.socketService.client.Client;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ServerService {
    // connection info
    final int port = 5050;
    ServerSocket ssk;
    Socket sock;
    byte[] key;
    // server info
    public Server server;

    // chatting info
    int chattingPort = 5051;
    ServerSocket chattingSsk;
    public Socket ChattingSock;


    // make connection
    public ServerService() throws IOException {
        this.ssk = new ServerSocket(port);
        this.server = new Server();
        server.port = port;
        this.key = key;

        // make socket for chatting
        this.chattingSsk = new ServerSocket(chattingPort);
    }

    //make socket
    public Socket makeSock() throws IOException {
        sock = ssk.accept();
        System.out.println("서버와 접속이 되었습니다.");
        System.out.println("Client ip :"+ sock.getInetAddress());
        server.ipAddress = sock.getInetAddress();
        server.connection = true;

        // chat case
        ChattingSock = chattingSsk.accept();

        return sock;
    }


    // send chat message to client
    public String sendChatToClient(String msg, String key) throws Exception {
        // save message in history
        int index = server.chatHistory.size()+1;
        server.chatHistory.put("server"+index, msg);

        // get encrypt message
        String encrypt = AES256Util.encrypt(msg,key);
        server.encryptedMsg = encrypt;
        // send msg to client
        OutputStream output_data = ChattingSock.getOutputStream();
        output_data.write(encrypt.getBytes());

        return encrypt;
    }


    // Make AES key in server
    public void makeAESKeyInServer(){
        double min = 1;
        double max = 9;
        String randomKey = "";
        // make random number
        for(int i = 0;i<32;i++){
            int random = (int) ((Math.random() * (max - min)) + min);
            randomKey = randomKey + String.valueOf(random);
        }
        // get AES key from AES256 center
        AES256Center aes256Center = new AES256Center(randomKey);
        String aesKey = aes256Center.getKey();
        // set server's AES key
        server.aesKey = aesKey;
    }

    // send encrypted AES Key to client
    public void sendAESKeyToClient() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // encrypt AES key using client's public key
        String encryptAES = RSAUtil.encryptRSA(server.aesKey, server.ClientPublicKey);

        // send it to client
        OutputStream output_data = sock.getOutputStream();
        output_data.write(encryptAES.getBytes());
    }


    // get public key from client
    public PublicKey getPublicKeyFromClient() throws IOException {
        // using inputstream to get key
        InputStream input_data = sock.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        // get data
        String data = new String(receiveBuffer);
        data = data.substring(0,size);

        PublicKey pubKey = null;
        try {
            // change it to PublicKey Type
            String publicK = data;
            // using Base64 to change String to byte array
            byte[] publicBytes = Base64.getDecoder().decode(publicK);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // get PublicKey type's key
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // set public key
        server.ClientPublicKey = pubKey;

        return pubKey;
    }

    // send PublicKey to client
    public String sendPublicKeyToClient(PublicKey publicKey) throws IOException {
        // send publickey to byte array
        byte[] byte_pubkey = publicKey.getEncoded();
        // change byte to String using Base64
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
        // change String to byte again
        byte[] encoded = str_key.getBytes();
        // send data using outputstream
        OutputStream output_data = sock.getOutputStream();
        output_data.write(encoded);

        return str_key;
    }


    // get encrypted AES
    public void checkEncryptedAES() throws Exception {
        // using inputstream to get
        InputStream input_data = sock.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        // get data
        String data = new String(receiveBuffer);
        data = data.substring(0,size);
        // decrypt data by his privatekey
        String decryptRSA = RSAUtil.decryptRSA(data, server.privateKey);
        server.aesKey = decryptRSA;

    }

    // getter
    public Socket getSock() {
        return sock;
    }

}
