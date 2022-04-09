package notusedtest;

import com.security.keyutil.RSAUtil;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class PublicKey {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyPair keyPair = RSAUtil.genRSAKeyPair();
        java.security.PublicKey publicKey1 = keyPair.getPublic();
        System.out.println(publicKey1);
        String s = publicKey1.toString();
        System.out.println(getKey(s));
    }
    public static PublicKey getKey(String key){
        try{
            byte[] byteKey = Base64.getDecoder().decode(key.getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return (PublicKey) kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}