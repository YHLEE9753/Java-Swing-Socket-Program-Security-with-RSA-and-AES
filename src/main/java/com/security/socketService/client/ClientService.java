package com.security.socketService.client;

import com.security.keyutil.AES256Center;
import com.security.keyutil.AES256Util;
import com.security.keyutil.RSAUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ClientService {
    // connection data and client data
    private Socket sk;
    private byte[] key;
    private String RSAPublicKey;
    private PublicKey RSAPublicKeyNotStr;
    private String aesKey;
    public Client client;

    // socket for chatting
    public Socket chattingSk;

    // make socket when connection is happen
    public ClientService() throws IOException {
        // socket
        this.sk = new Socket("127.0.0.1", 5050);
        this.client = new Client();
        // update client data
        client.connection = true;
        client.ipAddress = "127.0.0.1";

        // make chatting socket
        this.chattingSk = new Socket("127.0.0.1", 5051);
    }


    // send PublicKey to server
    public String sendPublicKeyToServer(PublicKey publicKey) throws IOException {
        // change Publickey type to byte
        byte[] byte_pubkey = publicKey.getEncoded();
        // change byte array to String using Base64
        String str_key = Base64.getEncoder().encodeToString(byte_pubkey);
        // change String again to byte array
        byte[] encoded = str_key.getBytes();

        // send Public Key to server
        OutputStream output_data = sk.getOutputStream();
        output_data.write(encoded);

        return str_key;
    }

    // get public key from server
    public PublicKey getPublicKeyFromServer() throws IOException {
        // using inputStream to get key
        InputStream input_data = sk.getInputStream();
        // size set 4096
        byte[] receiveBuffer = new byte[4096];
        int size = input_data.read(receiveBuffer);
        // receive data
        String data = new String(receiveBuffer);
        data = data.substring(0, size);

        PublicKey pubKey = null;
        try {
            String publicK = data;
            // change String to PublicKey type by using Base64
            byte[] publicBytes = Base64.getDecoder().decode(publicK);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            // finish
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // set client's server publickey
        RSAPublicKeyNotStr = pubKey;
        client.publicKey = pubKey;

        return pubKey;
    }

    // make AES key
    public void makeAESKeyInClinet() {
        double min = 1;
        double max = 9;
        String randomKey = "";
        // make random number
        for (int i = 0; i < 32; i++) {
            int random = (int) ((Math.random() * (max - min)) + min);
            randomKey = randomKey + String.valueOf(random);
        }
        // get AES Key from AES256 center
        AES256Center aes256Center = new AES256Center(randomKey);
        String aesKey = aes256Center.getKey();
        // set client's AES key
        client.aesKey = aesKey;
    }

    // send encrypted AES key for client
    public void sendAESKeyToServer() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // encrypt aes key using publickey
        String encryptAES = RSAUtil.encryptRSA(client.aesKey, client.publicKey);
        // send key
        OutputStream output_data = sk.getOutputStream();
        output_data.write(encryptAES.getBytes());
    }

    // get encrypted AES Key from server
    public String checkEncryptedAES() throws Exception {
        // using inputStream to get data
        InputStream input_data = sk.getInputStream();
        byte[] receiveBuffer = new byte[4096];
        // get size
        int size = input_data.read(receiveBuffer);
        // get encrypted data
        String data = new String(receiveBuffer);
        data = data.substring(0, size);
        // decrypt encrypted AES Key
        String decryptRSA = RSAUtil.decryptRSA(data, client.myPrivateKey);
        // set client's aeskey
        client.aesKey = decryptRSA;

        return data;

    }

    // send chat to server.
    public String sendChatToServer(String msg, String key) throws Exception {
        int index = client.chatHistory.size() + 1;
        // save chat message.
        client.chatHistory.put("client" + index, msg);
        String encrypt = AES256Util.encrypt(msg, key);
        // save encrypted message
        client.encryptedMsg = encrypt;
        // send chat to server
        OutputStream output_data = chattingSk.getOutputStream();
        output_data.write(encrypt.getBytes());

        return encrypt;
    }

    // getter method
    public Socket getSk() {
        return sk;
    }

    public byte[] getKey() {
        return key;
    }

}

