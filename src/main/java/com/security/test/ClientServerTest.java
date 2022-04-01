package com.security.test;

import com.security.keyutil.AES256Center;
import com.security.socket.client.ClientService;
import com.security.socket.server.ServerService;

import java.net.Socket;

public class ClientServerTest {
    public static void main(String[] args) throws Exception {
        // 서버 소켓 생성
        ServerService serverService = new ServerService();

        while (true) {
            // 클라이언트 생성
            ClientService clientService = new ClientService();

            // 클라이언트 접속(sock 생성)
            Socket sock = serverService.makeSock();

            // AES Key 생성
            AES256Center aes256 = new AES256Center();
            String key = aes256.getKey();


            // server 에서 client 에게 데이터 전송
            String msg1 = "hi";
            serverService.sendMsgToClient(msg1, key);


            // server 에서 전달받은 데이터 client 에서 확인
            String receiveMsg = clientService.checkMsg(key);
            System.out.println(receiveMsg);

            // client 에서 server 로 데이터 전송
            String msg2 = "hi2";
            clientService.sendMsgToServer(msg2, key);

            // client 에서 전달받은 데이터 server 에서 확인
            receiveMsg = serverService.CheckMsg(key);
            System.out.println(receiveMsg);


            break;
        }
    }
}