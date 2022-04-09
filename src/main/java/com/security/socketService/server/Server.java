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
    public PublicKey publicKey;
    public String aesKey;
    public PrivateKey privateKey;
    public Map<String, String> chatHistory = new ConcurrentHashMap<>();
    public String encryptedMsg = "";
    public String encryptedFile = "";

    public PublicKey ClientPublicKey; // client public key
}
