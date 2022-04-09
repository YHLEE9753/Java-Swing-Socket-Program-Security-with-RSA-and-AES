package com.security.keyutil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AES256Util {

    // algorithm type
    private static String alg = "AES/CBC/PKCS5Padding";

    // encrypte String plaintext using by AESkey
    public static String encrypt(String text, String key) throws Exception {
        // using iv
        String iv = key.substring(0, 16);
        Cipher cipher = Cipher.getInstance(alg);
        // make key with AES algorithm
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        // do encrypt
        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // encrypte String cipherText using by AESkey
    public static String decrypt(String cipherText, String key) throws Exception {
        // using iv
        String iv = key.substring(0, 16);
        Cipher cipher = Cipher.getInstance(alg);
        // make key with AES algorithm
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        // do decrypt
        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
    }
}
