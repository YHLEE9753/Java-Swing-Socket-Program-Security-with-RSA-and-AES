package com.security.socketService.server;

import java.net.InetAddress;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    // information about server
    public int port = 5050;
    public InetAddress ipAddress;
    public boolean connection = false;
    // Server publickey and private key
    public PublicKey publicKey;
    public PrivateKey privateKey;
    // Server AES key
    public String aesKey;
    // history about chatting include client and server
    public Map<String, String> chatHistory = new ConcurrentHashMap<>();

    public String encryptedMsg = "";

    // this is a client key
    public PublicKey ClientPublicKey;
}
