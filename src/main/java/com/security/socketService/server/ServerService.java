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

        // chat
        ChattingSock = chattingSsk.accept();
        return sock;
    }


    // check message from client
     public String CheckMsg(String key) throws Exception {
        InputStream input_data = ChattingSock.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);

        String data = new String(receiveBuffer);

        data = AES256Util.decrypt(data.substring(0,size),key);
        int index = server.chatHistory.size()+1;
        server.chatHistory.put("client"+index, data);
        return data;
    }

    // send chat message to client
    public String sendChatToClient(String msg, String key) throws Exception {
        // save message in history
        int index = server.chatHistory.size()+1;
        server.chatHistory.put("server"+index, msg);

        // get encrypt message
        String encrypt = AES256Util.encrypt(msg,key);
        server.encryptedMsg = encrypt;
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
        // get AES key
        AES256Center aes256Center = new AES256Center(randomKey);
        String aesKey = aes256Center.getKey();
        server.aesKey = aesKey;
    }

    // send encrypted AES Key to client
    public void sendAESKeyToClient() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encryptAES = RSAUtil.encryptRSA(server.aesKey, server.ClientPublicKey);
        OutputStream output_data = sock.getOutputStream();
        output_data.write(encryptAES.getBytes());
    }


    // create and save PublicKey PrivateKey
    public void makePublicKeyAndPrivateKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = RSAUtil.genRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        server.privateKey = privateKey;
        server.publicKey = publicKey;
    }
    // get public key from client
    public PublicKey getPublicKeyFromClient() throws IOException {
        InputStream input_data = sock.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        String data = new String(receiveBuffer);
        // get data
        data = data.substring(0,size);

        PublicKey pubKey = null;
        try {
            // change it to PublicKey Type
            String publicK = data;
            byte[] publicBytes = Base64.getDecoder().decode(publicK);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // change public key
        server.ClientPublicKey = pubKey;
        return pubKey;
    }

    // send PublicKey to client
    public String sendPublicKeyToClient(PublicKey publicKey) throws IOException {
        // send publickey to byte stream
        byte[] byte_pubkey = publicKey.getEncoded();
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
        byte[] encoded = str_key.getBytes();
        OutputStream output_data = sock.getOutputStream();
        output_data.write(encoded);
        return str_key;
    }

    // send encrypted AES key to client
    public void sendAESKeyToClient(String aesKey) throws IOException {
        OutputStream output_data = sock.getOutputStream();
        output_data.write(aesKey.getBytes());
    }

    // get encrypted AES
    public void checkEncryptedAES() throws Exception {
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

    // send Message to client.
    public void sendMsgToClient(String msg, String key) throws Exception {
        int index = server.chatHistory.size()+1;
        server.chatHistory.put("server"+index, msg);
        String encrypt = AES256Util.encrypt(msg,key);
        server.encryptedMsg = encrypt;

        OutputStream output_data = sock.getOutputStream();
        output_data.write(encrypt.getBytes());
    }


    // getter
    public int getPort() {
        return port;
    }


    public Socket getSock() {
        return sock;
    }

}
