package com.security;

import com.security.socketService.client.ClientService;
import com.security.socketService.file.FileReceiver;
import com.security.socketService.file.FileSender;
import com.security.socketService.server.ServerService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.util.concurrent.atomic.AtomicReference;

public class SecurityApp {
    private JPanel jPanel;
    private JTextField communicationModeTextField;
    private JTextField obtainRelevantInformationForTextField;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JTextField showIfItIsTextField;
    private JButton keyGenerationButton;
    private JButton loadFromAFileButton;
    private JTextField anotherUserSPublicTextField;
    private JTextField chattingInfoTextField;
    private JTextField fileTransferInfoTextField;
    private JTextArea textArea1;
    private JTextField publicPrivateKeyPairTextField;
    private JButton sendFileButton;
    private JTextField chatInfoTextField;
    private JCheckBox clientCheckBox;
    private JCheckBox serverCheckBox;
    private JButton selectClientServerButton;
    private JButton exchangeAESKeyUsingRSAButton;
    private JButton sendPublicKey;
    private JButton sendChatButton;
    private JButton saveIntoAFile;


    static SecurityApp app = new SecurityApp();

    private static ServerService serverService;
    private static ClientService clientService;
    private static FileSender fileSender;
    private static FileReceiver fileReceiver;

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("SecurityApp");
        app = new SecurityApp();
        frame.setContentPane(app.jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


        // client server 연결
        connection();

        // 처음에 server 선택
        app.serverCheckBox.setSelected(true);

        // client, Server Button
        clientServerButtonAction(app);

        // KeyGeneration Button
        keyGenerationButtonAction();

        // sendPublicKeyButton
        sendPublicKeyButtonAction();

        // exchangeAESKey
        exchangeAESKeyAction();

        // send Chat Button
        sendChatButton();

        // send File
        app.sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("");
                String filePath = String.valueOf(file.getAbsoluteFile());
                filePath += "\\src\\main\\java\\com\\security\\filestore";
                String fileNm = "testfile.txt";
                if (serverService.server.aesKey == null| clientService.client.aesKey == null){
                    JOptionPane.showMessageDialog(null, "Make AES key first");
                    return;
                }
                if(!serverService.server.aesKey.equals(clientService.client.aesKey)) {
                    JOptionPane.showMessageDialog(null, "AES key is not same");
                    return;
                }
                if (app.serverCheckBox.isSelected()) {
                    String path1 = filePath + "\\serversend\\FileServerWillSend";
                    fileSender = new FileSender(serverService.getSock(), path1, fileNm, serverService.server.aesKey);
                    fileSender.run();

                    String path2 = filePath + "\\serversend\\FileClientReceive";
                    fileReceiver = new FileReceiver(clientService.getSk(), clientService.client.aesKey, path2);
                    fileReceiver.run();

                    // 클라이언트 생성
                    // 클라이언트 접속(sock 생성)
                    try {
                        clientService = new ClientService();
                        serverService.makeSock();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                }
                if (app.clientCheckBox.isSelected()) {
                    String path3 = filePath + "\\clientsend\\FileClientWillSend";
                    fileSender = new FileSender(clientService.getSk(), path3, fileNm, clientService.client.aesKey);
                    fileSender.run();

                    String path4 = filePath + "\\clientsend\\FileServerReceive";
                    fileReceiver = new FileReceiver(serverService.getSock(), serverService.server.aesKey, path4);
                    fileReceiver.run();

                    // 클라이언트 생성
                    // 클라이언트 접속(sock 생성)
                    try {
                        clientService = new ClientService();
                        serverService.makeSock();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });

        app.loadFromAFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("");
                String filePath = String.valueOf(file.getAbsoluteFile());
                filePath += "\\src\\main\\java\\com\\security\\filestore";
                String fileNm = "testfile.txt";
                if (serverService.server.aesKey == null| clientService.client.aesKey == null){
                    JOptionPane.showMessageDialog(null, "Make AES key first");
                    return;
                }
                if(!serverService.server.aesKey.equals(clientService.client.aesKey)) {
                    JOptionPane.showMessageDialog(null, "AES key is not same");
                    return;
                }
                if (app.serverCheckBox.isSelected()) {
                    filePath += "\\serversend\\FileServerWillSend";
                    fileSender = new FileSender(serverService.getSock(), filePath, fileNm, serverService.server.aesKey);
                    fileSender.run();

                }
                if (app.clientCheckBox.isSelected()) {
                    filePath += "\\clientsend\\FileClientWillSend";
                    fileSender = new FileSender(clientService.getSk(), filePath, fileNm, clientService.client.aesKey);
                    fileSender.run();
                }
            }
        });

        // read File
        app.saveIntoAFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File("");
                String filePath = String.valueOf(file.getAbsoluteFile());
                filePath += "\\src\\main\\java\\com\\security\\filestore";
                if (serverService.server.aesKey == null| clientService.client.aesKey == null){
                    JOptionPane.showMessageDialog(null, "Make AES key first");
                    return;
                }
                if(!serverService.server.aesKey.equals(clientService.client.aesKey)) {
                    JOptionPane.showMessageDialog(null, "AES key is not same");
                    return;
                }
                if (app.serverCheckBox.isSelected()) {
                    filePath += "\\clientsend\\FileServerReceive";
                    fileReceiver = new FileReceiver(serverService.getSock(), serverService.server.aesKey, filePath);
                    fileReceiver.run();


                    // 클라이언트 생성
                    // 클라이언트 접속(sock 생성)
                    try {
                        clientService = new ClientService();
                        serverService.makeSock();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if (app.clientCheckBox.isSelected()) {
                    filePath += "\\serversend\\FileClientReceive";
                    fileReceiver = new FileReceiver(clientService.getSk(), clientService.client.aesKey, filePath);
                    fileReceiver.run();
                }
            }
        });


    }

    public static void connection() throws IOException {
        // 서버 소켓 생성
        serverService = new ServerService();
        // 클라이언트 생성
        clientService = new ClientService();
        // 클라이언트 접속(sock 생성)
        Socket sock = serverService.makeSock();
    }

    public static void makeKeyAndExchange() throws Exception {
        // 서버에서 RSA Pair, AES 키 생성
        serverService.makePublicKeyAndPrivateKey();

        // client 에서 AES 대칭 키 생성
        clientService.makeAESKeyInClinet();

        // client 는 publicKey 를 server 로 부터 받는다.
        serverService.sendPublicKeyToClient(serverService.server.publicKey);
        clientService.getPublicKeyFromServer();

        // client 는 AES 키를 public key 로 암호화 해서 전달

        // 클라이언트가 서버에게 암호화된 AES 키를 보낸다.
        clientService.sendAESKeyToServer();

        // 서버는 암호화된 AES 키를 받은 후 Private 키를 통해 복호화 한다.
        serverService.checkEncryptedAES();

        // 두개의 키가 대칭인지 확인
        System.out.println(serverService.server.aesKey.equals(clientService.client.aesKey));
    }


    private static void makeKey() throws NoSuchAlgorithmException {
        // 서버에서 RSA Pair, AES 키 생성
        serverService.makePublicKeyAndPrivateKey();
    }


    private static void publicKeyExchange() throws IOException {
        // client 는 publicKey 를 server 로 부터 받는다.
        serverService.sendPublicKeyToClient(serverService.server.publicKey);
        clientService.getPublicKeyFromServer();
    }

    private static void aesKeyExchange() throws Exception {
        // client 에서 AES 대칭 키 생성
        clientService.makeAESKeyInClinet();

        // 클라이언트가 서버에게 암호화된 AES 키를 보낸다.
        clientService.sendAESKeyToServer();

        // 서버는 암호화된 AES 키를 받은 후 Private 키를 통해 복호화 한다.
        serverService.checkEncryptedAES();
    }


    private static void sendChatButton() {
        app.sendChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String text = app.textArea1.getText();
                if (serverService.server.aesKey == null| clientService.client.aesKey == null){
                    JOptionPane.showMessageDialog(null, "Make AES key first");
                    return;
                }
                if(!serverService.server.aesKey.equals(clientService.client.aesKey)) {
                    JOptionPane.showMessageDialog(null, "AES key is not same");
                    return;
                }
                if (text.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Write Something!");
                } else if (!serverService.server.aesKey.equals(clientService.client.aesKey)) {
                    JOptionPane.showMessageDialog(null, "AES Key is not same. exchange key agian");
                } else {
                    if (app.serverCheckBox.isSelected()) {

                        try {
                            serverService.sendMsgToClient(text, serverService.server.aesKey);
                            clientService.checkMsg(clientService.client.aesKey);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } else {

                        try {
                            clientService.sendMsgToServer(text, clientService.client.aesKey);
                            serverService.CheckMsg(serverService.server.aesKey);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    showInfo(app);
                }
            }
        });
    }

    private static void exchangeAESKeyAction() {
        app.exchangeAESKeyUsingRSAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    aesKeyExchange();
                    showInfo(app);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (serverService.server.aesKey.equals(clientService.client.aesKey)) {
                    JOptionPane.showMessageDialog(null, "AESKey in server and AESKey in client is same");
                } else {
                    JOptionPane.showMessageDialog(null, "AESKey in server and AESKey in client is diffenrent! make key and exchange again!");

                }
            }
        });
    }

    private static void sendPublicKeyButtonAction() {
        app.sendPublicKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    publicKeyExchange();
                    showInfo(app);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static void keyGenerationButtonAction() {
        app.keyGenerationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    makeKey();
                    showInfo(app);
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private static void clientServerButtonAction(SecurityApp app) {
        app.selectClientServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInfo(app);
            }
        });
    }

    private static void showInfo(SecurityApp app) {
        String ipAddress = clientService.client.ipAddress;
        boolean connection = clientService.client.connection;
        boolean connection1 = serverService.server.connection;
        int port = clientService.client.port;
        app.obtainRelevantInformationForTextField.setText(
                "Client connection : " + connection + "\n"
                        + " Server connection : " + connection1 + "\n"
                        + " Client Ip : " + ipAddress + "\n"
                        + " port number : " + port
        );
        // client 에 선택된 경우
        if (app.clientCheckBox.isSelected()) {
            app.showIfItIsTextField.setText(
                    "Client connection : " + connection
            );
            app.publicPrivateKeyPairTextField.setText(
                    "PublicKeyFromServer : " + clientService.client.publicKey
                            + "AESKey : " + clientService.client.aesKey
            );
            app.anotherUserSPublicTextField.setText(
                    "ServerPublicKey : " + serverService.server.publicKey
            );

            app.chatInfoTextField.setText("encypted message sending by client : " + clientService.client.encryptedMsg);

        }
        // server 에 선택된 경우
        if (app.serverCheckBox.isSelected()) {
            app.showIfItIsTextField.setText(
                    "Server connection : " + connection1
            );
            app.publicPrivateKeyPairTextField.setText(
                    "PublicKey : " + serverService.server.publicKey
                            + " PrivateKey : " + serverService.server.privateKey
                            + " AESKey : " + serverService.server.aesKey
            );
            app.anotherUserSPublicTextField.setText(
                    "ClientPublicKey : " + clientService.client.publicKey
            );

            app.chatInfoTextField.setText("encypted message sending by server : " + serverService.server.encryptedMsg);

        }
        // Message history
        AtomicReference<String> ttt = new AtomicReference<>("");
        serverService.server.chatHistory.forEach((key, value) -> {
            ttt.set(ttt + String.format("%s : %s |", key, value));
        });
        app.chattingInfoTextField.setText(ttt.get());


    }


}
