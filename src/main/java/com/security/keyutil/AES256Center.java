package com.security.keyutil;

public class AES256Center {

    // this is center to give AES key
    // get random number and return key
    public static String alg = "AES/CBC/PKCS5Padding";
    private String key;

    public AES256Center(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}