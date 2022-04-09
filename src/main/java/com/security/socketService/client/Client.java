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
    public static PublicKey myPublicKey;
    public static PrivateKey myPrivateKey;
    public static String aesKey;
    public static Map<String, String> chatHistory = new ConcurrentHashMap<>();
    public static String encryptedMsg = "";
    public static String encryptedFile = "";

    // this is a server key
    public static PublicKey publicKey;
}
