package notusedtest;

import com.security.keyutil.RSAUtil;
import com.security.socketService.client.ClientService;
import com.security.socketService.file.FileReceiver;
import com.security.socketService.file.FileSender;
import com.security.socketService.server.ServerService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.PublicKey;

import static com.security.socketService.client.Client.aesKey;

public class LastTest {

    static ServerService serverService;
    static ClientService clientService;

    public static void main(String[] args) throws Exception {
        // 서버 소켓 생성

        // connection;
        serverService = new ServerService();
        clientService = new ClientService();
        serverService.makeSock();

        // server - makeRSAKey;
        KeyPair keyPair = null;
        try {
            keyPair = RSAUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        serverService.server.publicKey = publicKey;
        serverService.server.privateKey = privateKey;

        // client - makeRSAKey;
        keyPair = null;
        try {
            keyPair = RSAUtil.genRSAKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        clientService.client.myPublicKey = publicKey;
        clientService.client.myPrivateKey = privateKey;

        // server - send PublicKey to client
        try {
            String encrypted = serverService.sendPublicKeyToClient(serverService.server.publicKey);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // client - getAndSavePublicKeyFromServer
        try {
            clientService.getPublicKeyFromServer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        File file = new File("");
        String rootPath = String.valueOf(file.getAbsoluteFile());
        rootPath += "\\src\\main\\java\\com\\security\\filestore\\client\\keystorage\\ServerPublicKey.txt";

        try (OutputStream output = new FileOutputStream(rootPath);
        ) {
            String str = String.valueOf(clientService.client.publicKey);
            byte[] by = str.getBytes();
            output.write(by);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // client - sendMyPublicKeyToServer
        try {
            String encrypted = clientService.sendPublicKeyToServer(clientService.client.myPublicKey);

        } catch (IOException ex) {
            ex.printStackTrace();
        }


        // server - getAndSavePublicKeyFromClient
        try {
            serverService.getPublicKeyFromClient();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        file = new File("");
        rootPath = String.valueOf(file.getAbsoluteFile());
        rootPath += "\\src\\main\\java\\com\\security\\filestore\\server\\keystorage\\ClientPublicKey.txt";

        try (OutputStream output = new FileOutputStream(rootPath);
        ) {
            String str = String.valueOf(serverService.server.ClientPublicKey);
            byte[] by = str.getBytes();
            output.write(by);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // server - makeAndSendAESKey
        serverService.makeAESKeyInServer();
        try {
            serverService.sendAESKeyToClient();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        }

        // client - GetAESKeyFromServer
        try {
            clientService.checkEncryptedAES();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("success");
        System.out.println("start send file");


        //            String filePath = "C:/";
//            String fileNm = "test.txt";
//
//            // client 에서 server 로 파일 전송
//            fileSender = new FileSender(clientService.getSk(), filePath, fileNm, aesKey);
//            fileSender.run();
//
//            // server 에서 파일 읽기
////            fileReceiver = new FileReceiver(serverService.getSock(), serverAESKey);
////            fileReceiver.run();
//
//            // 포트 재연결
//            // 클라이언트 생성
//            clientService = new ClientService();
//            // 클라이언트 접속(sock 생성)
//            sock = serverService.makeSock();
//
//            filePath = "C:/";
//            fileNm = "test.txt";
//
//            // server 에서 client 로 파일 전송
//            fileSender = new FileSender(serverService.getSock(), filePath, fileNm, serverAESKey);
//            fileSender.run();
//
//            // client 에서 파일 읽기
//            fileReceiver = new FileReceiver(clientService.getSk(), aesKey);
//            fileReceiver.run();

//        // server - sendFileToClient
//        System.out.println("============ server - sendFileToClient");
//        String filePath = "D:/send";
//        String fileNm = "test.txt";
//        FileSender fileSender1 = new FileSender(serverService.getSock(), filePath, fileNm, serverService.server.aesKey);
//        fileSender1.run();
//
//        System.out.println("============" + serverService.getSock());
//
//        // client - getFileFromServer
//        System.out.println("============ client - getFileFromServer");
//        FileReceiver fileReceiver1 = new FileReceiver(clientService.getSk(), aesKey, "D:/get");
//        fileReceiver1.run();
//
//        System.out.println("server -> client");
//        System.out.println("============" + clientService.getSk());
//        System.out.println("============" + serverService.getSock());
//
//        // check connection
//        filePath = "D:/send";
//        fileNm = "test.txt";
//        FileSender fileSender2 = new FileSender(serverService.getSock(), filePath, fileNm, serverService.server.aesKey);
//        fileSender2.run();
//
//        FileReceiver fileReceiver2 = new FileReceiver(clientService.getSk(), aesKey, "D:/get");
//        fileReceiver2.run();
//
//        System.out.println("success");

        // digital signature


    }
}
