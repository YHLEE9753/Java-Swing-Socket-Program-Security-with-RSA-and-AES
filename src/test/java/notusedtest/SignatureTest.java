package notusedtest;

import java.security.*;

public class SignatureTest {
//    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, InvalidKeySpecException {
//        String msg = "하늘에는 달이 없고, 땅에는 바람이 없습니다.\n사람들은 소리가 없고, 나는 마음이 없습니다.\n\n우주는 죽음인가요.\n인생은 잠인가요.";
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//        KeyPair keyPair = generator.generateKeyPair();
//        notusedtest.PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate(); // 전자서명하기
//        Signature signatureA = Signature.getInstance("SHA1withRSA");
//        signatureA.initSign(privateKey);
//        signatureA.update(msg.getBytes());
//        byte[] sign = signatureA.sign();
//        System.out.println("signature : " + ByteUtils.toHexString(sign)); // 전사서명 검증하기
//        String msgB = msg;
//        Signature signatureB = Signature.getInstance("SHA1withRSA");
//        signatureB.initVerify(publicKey);
//        signatureB.update(msgB.getBytes());
//        boolean verifty = signatureB.verify(sign);
//        System.out.println("검증 결과 : " + verifty); // 전사서명 검증하기2
//        System.out.println(publicKey);
//        RSAPublicKeyImpl rs = (RSAPublicKeyImpl) publicKey;
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        notusedtest.PublicKey publicKeyC = keyFactory.generatePublic(new RSAPublicKeySpec(rs.getModulus(), rs.getPublicExponent()));
//        Signature signatureC = Signature.getInstance("SHA1withRSA");
//        signatureC.initVerify(publicKeyC);
//        signatureC.update(msgB.getBytes());
//        boolean veriftyC = signatureC.verify(sign);
//        System.out.println("검증 결과 : " + veriftyC);
//    }
}
