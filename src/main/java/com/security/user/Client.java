package com.security.user;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    public static int port = 5050;
    public static String ipAddress;
    public static boolean connection = false;
    public static PublicKey publicKey;
    public static String aesKey;
//    public PrivateKey privateKey;
    public static Map<String, String> chatHistory = new ConcurrentHashMap<>();
    public static String encryptedMsg = "";
}
