package com.security.socketService.client;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    // information about client
    public static int port = 5050;
    public static String ipAddress;
    public static boolean connection = false;
    // client publickey and private key
    public static PublicKey myPublicKey;
    public static PrivateKey myPrivateKey;
    // client AES key
    public static String aesKey;
    // history about chatting include client and server
    public static Map<String, String> chatHistory = new ConcurrentHashMap<>();

    public static String encryptedMsg = "";

    // this is a server key
    public static PublicKey publicKey;
}
