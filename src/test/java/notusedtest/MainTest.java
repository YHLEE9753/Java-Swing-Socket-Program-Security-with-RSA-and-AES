package notusedtest;

import com.security.keyutil.AES256Center;
import com.security.keyutil.RSAUtil;
import com.security.socketService.client.ClientService;
import com.security.socketService.file.FileReceiver;
import com.security.socketService.file.FileSender;
import com.security.socketService.server.ServerService;

import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class MainTest{
    public static void main(String[] args) throws Exception {
//        // 서버 소켓 생성
//        ServerService serverService = new ServerService();
//        FileSender fileSender;
//        FileReceiver fileReceiver;
//
//        while (true) {
//            // 서버에서 RSA Pair, AES 키 생성
//            KeyPair keyPair = RSAUtil.genRSAKeyPair();
//            notusedtest.PublicKey publicKey = keyPair.getPublic();
//            PrivateKey privateKey = keyPair.getPrivate();
//
//            // 클라이언트 생성
//            ClientService clientService = new ClientService();
//
//            // 클라이언트 접속(sock 생성)
//            Socket sock = serverService.makeSock();
//
//            // AES 대칭 키 생성
//            AES256Center aes256Center = new AES256Center();
//            String aesKey = aes256Center.getKey();
//            System.out.println("AES 대칭 키 : " + aesKey);
//
//            // 클라이언트는 공개 키를 서버로부터 받는다
//            System.out.println("public key : " + publicKey);
//            serverService.sendPublicKeyToClient(publicKey);
//            notusedtest.PublicKey publicKeyFromServer = clientService.getPublicKeyFromServer();
//            System.out.println(publicKeyFromServer);
//
//
//            // 서버에서 AES 키를 private 키로 암호화
////            String encryptAES = RSAUtil.encryptRSA(aesKey, publicKey);
////            System.out.println(encryptAES);
//
//            // 클라이언트에서 AES 키를 private 키로 암호화
//            String encryptAES = RSAUtil.encryptRSA(aesKey, publicKey);
//            System.out.println(encryptAES);
//
//            // 클라이언트가 서버에게 암호화된 AES 키를 보낸다.
//            clientService.sendAESKeyToServer();
//            // 클라이언트에게 암호화된 AES 키를 보낸다.
////            serverService.sendAESKeyToClient(encryptAES);
//
//            // 클라이언트는 암호화된 AES 키를 받은 후 RSA 공개 키를 통해 복호화한다
////            String AESFromServer = clientService.checkEncryptedAES();
////            System.out.println(AESFromServer);
////            String clientAESKey = RSAUtil.decryptRSA(AESFromServer, privateKey);
////            System.out.println(clientAESKey);
//
//            // 서버는 암호화된 AES 키를 받은 후 RSA 개인 키를 통해 복호화 한다.
//            serverService.checkEncryptedAES();
//
//
//            // 두개의 키가 대칭인지 확인
////            System.out.println(aesKey.equals(serverAESKey));
//
//            System.out.println("============= 메세지 전송 테스트 =============");
//            // 클라이언트 aes 키 : aesKey
//            // 서버 aes 키 : serverAESKey
//            // server 에서 client 에게 데이터 전송
//            String msg1 = "hi";
////            serverService.sendMsgToClient(msg1, serverAESKey);
//
//
//            // server 에서 전달받은 데이터 client 에서 확인
//            String receiveMsg = clientService.checkMsg(aesKey);
//            System.out.println(receiveMsg);
//
//            // client 에서 server 로 데이터 전송
//            String msg2 = "hi2";
////            clientService.sendMsgToServer(msg2, aesKey);
//
//            // client 에서 전달받은 데이터 server 에서 확인
////            receiveMsg = serverService.CheckMsg(serverAESKey);
//            System.out.println(receiveMsg);
//
//            // 서버는 AES 키를 통해 파일 암호화 후 전송
//
//            // 클라이언트는 AES 키를 통해 파일 복호화 후 확인
//
//            // 클라이언트는 AES 키를 통해 파일 함호화 후 전송
//
//            // 서버는 AES 키를 통해 파일 복호화 후 확인
//
//            // clinet 전송 테스트 파일 불러오기
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
//
//            System.out.println("finish");
//
//            break;
//        }
    }
}
