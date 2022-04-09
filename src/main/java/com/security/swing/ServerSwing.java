package com.security.swing;

import com.security.keyutil.AES256Center;
import com.security.keyutil.AES256Util;
import com.security.keyutil.RSAUtil;
import com.security.socketService.file.FileReceiver;
import com.security.socketService.file.FileSender;
import com.security.socketService.server.ServerService;

import java.awt.EventQueue;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.io.*;
import java.security.*;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;


public class ServerSwing extends JFrame {

    // Swing panel
    private static JPanel contentPane;
    private static JTextField ChattextField;
    private static JLabel IPLabel;
    private static JLabel PortLabel;
    private static JButton ConnectionBtn;
    private static JButton RSAKeyGenerationBtn;
    private static JButton SaveIntoFileBtn;
    private static JButton SendPublicKeyBtn;
    private static JButton LoadFileBtn;
    private static JLabel ConnectionInfo;
    private static JTextField ServerPublicKeyInfo;
    private static JTextField ServerPrivateKeyInfo;
    private static JTextField ServerAESKeyInfo;
    private static JTextField ClientPublicKeyInfo;
    private static JTextField ClientPrivateKeyInfo;
    private static JTextField ClientAESKeyInfo;
    private static JTextArea ChatInfo;
    private static JTextField MsgEncryptInfo;
    private static JTextArea FileEncryptedInfo;
    private static JButton SendFileBtn;
    private static JButton SendMsgBtn;
    private static JButton SendAESKeyBtn;
    private static JButton GETAESKeyBtn;
    private static JButton GetFileBtn;

    public static ServerService serverService;
    private static boolean connect = false;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {


        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // make Frame and set visible
                    ServerSwing frame = new ServerSwing();
                    frame.setVisible(true);

                    connection();
                    makeRSAKey();
                    saveMyRSAKeyToTxt();
                    sendMyPublicKeyToClient();
                    getAndSavePublicKeyFromClient();
                    makeAndSendAESKey();
                    GetAESKeyFromClient();
                    sendChatButtonToClient();
                    sendFile();
                    getFile();

                    // continuously check chatting change per 1 second
                    TimerTask task = new TimerTask() {
                        public void run() {
                            checkRefresh();
                        }
                    };
                    Timer timer = new Timer("Timer");
                    long delay = 3000L;
                    long period = 1000L; // 1 second
                    timer.schedule(task, delay, period);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // get File from client
    private static void getFile() {
        GetFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if server is not connected, return nothing
                if (serverService == null) {
                    return;
                }
                // if AESKey is null, alarm
                if (serverService.server.aesKey == null) {
                    JOptionPane.showMessageDialog(null, "Make AES key first");
                    return;
                }
                // set File directory
                File file = new File("");
                String rootPath = String.valueOf(file.getAbsoluteFile());
                rootPath += "\\src\\main\\java\\com\\security\\filestore\\server\\FileServerReceive";

                // using FileReceiver.java to send file
                FileReceiver fileReceiver = new FileReceiver(serverService.getSock(), serverService.server.aesKey, rootPath, serverService.server.publicKey);
                fileReceiver.run();
                String decryptedMsg = fileReceiver.decryptedMsg;

                // if Digital Signature is same, update Swing
                if (fileReceiver.dsEqaul) {
                    String txt = "";
                    txt += "server receive path : " + rootPath + "\n";
                    String receive = rootPath + "\\src\\main\\java\\com\\security\\filestore\\client\\FileClientWillSend";
                    txt += "client path : " + receive + "\n";
                    txt += "Digital Signature is same \n";
                    txt += "Digital Signature = " + fileReceiver.staticGetDs.toString() + "\n\n";
                    txt += "CipherText of file by AES : " + decryptedMsg;
                    FileEncryptedInfo.setText(txt);
                    // if not, alarm
                } else {
                    String txt = "Digital signature is different. Integrity problem happen!!";
                    FileEncryptedInfo.setText(txt);
                }


            }
        });
    }

    // Send file to client
    private static void sendFile() {
        SendFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if server is not connected, return nothing
                if (serverService == null) {
                    return;
                }
                // if AESKey is null, alarm
                if (serverService.server.aesKey == null) {
                    JOptionPane.showMessageDialog(null, "Make AES key first");
                    return;
                }
                // using JFileChooser to choose file
                JFileChooser chooser = new JFileChooser();

                // set File directory
                File file = new File("");
                String rootPath = String.valueOf(file.getAbsoluteFile());
                rootPath += "\\src\\main\\java\\com\\security\\filestore\\server\\FileServerWillSend";

                chooser.setCurrentDirectory(new File(rootPath));

                int ret = chooser.showOpenDialog(null);

                // if there is no selection in JFileChooser, alarm
                if (ret != JFileChooser.APPROVE_OPTION) {
                    JOptionPane.showMessageDialog(null, "경로를 선택하지 않았습니다");
                    return;
                }
                // set receving root. root is set
                String filePath = chooser.getSelectedFile().getPath();
                String[] split = filePath.split("\\\\");
                String fileNm = split[split.length - 1];
                filePath = filePath.substring(0, filePath.length() - fileNm.length() - 1);

                // using FileSender.java to send file
                FileSender fileSender = new FileSender(serverService.getSock(), filePath, fileNm, serverService.server.aesKey, serverService.server.ClientPublicKey);
                fileSender.run();
                String encryptedMsg = fileSender.encryptedMsg;

                // update Swing with related data
                String txt = "";
                txt += "server path : " + rootPath + "\n";
                String receive = rootPath + "\\src\\main\\java\\com\\security\\filestore\\client\\FileClientReceive";
                txt += "client receive path : " + receive + "\\";
                txt += "Digital Signature = " + serverService.server.ClientPublicKey.toString() + "\n\n";
                txt += "CipherText of file by AES : " + encryptedMsg;
                FileEncryptedInfo.setText(txt);
            }
        });
    }

    // continuously check chatting stream
    private static void checkRefresh() {
        InputStream input_data = null;
        // if there is no stream, nothing happen
        try {
            input_data = serverService.ChattingSock.getInputStream();
        } catch (Exception e) {
            return;
        }
        // if there is stream, get message
        byte[] receiveBuffer = new byte[4096];
        String data = null;
        String encryptData = "";
        // read text msg
        try {
            int size = input_data.read(receiveBuffer);
            data = new String(receiveBuffer);
            encryptData = data;
            // decrypt ciphertext
            data = AES256Util.decrypt(data.substring(0, size), serverService.server.aesKey);
        } catch (Exception e) {
            return;
        }

        // update chatting history with encrypted message and original message
        int index = serverService.server.chatHistory.size() + 1;
        data += "\n" + "(" + encryptData + ")";
        serverService.server.chatHistory.put("client" + index, data);

        // update Swing chat info
        String text = "";
        Iterator<String> keys = serverService.server.chatHistory.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            text += key + " : " + serverService.server.chatHistory.get(key) + "\n";
        }

        ChatInfo.setText("server chat history\n\n" + text);
    }


    // send chat to client
    private static void sendChatButtonToClient() {
        SendMsgBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get text
                String text = ChattextField.getText();
                // if server is not connected, return nothing
                if (serverService.server.aesKey == null) {
                    JOptionPane.showMessageDialog(null, "Make AES key first");
                    return;
                }
                // if AESKey is null, alarm
                if (text.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Write Something!");
                } else {
                    try {
                        // using serverService - sendChatToClient method to send chat
                        String encrypt = serverService.sendChatToClient(text, serverService.server.aesKey);
                        ChattextField.setText("");
                        MsgEncryptInfo.setText(encrypt);

                        // update Swing with related data
                        String text2 = "";
                        Iterator<String> keys = serverService.server.chatHistory.keySet().iterator();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            text2 += key + " : " + serverService.server.chatHistory.get(key) + "\n";
                        }

                        ChatInfo.setText("server chat history\n\n" + text2);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }


    // get AES key from client
    private static void GetAESKeyFromClient() {
        GETAESKeyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // using serverService - checkEncryptedAES method
                    serverService.checkEncryptedAES();
                    // update Swing with related data
                    ServerAESKeyInfo.setText(serverService.server.aesKey);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // make AES Key and send it to client
    private static void makeAndSendAESKey() {
        SendAESKeyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // using serverService - makeAESKeyInServer method
                serverService.makeAESKeyInServer();
                // update Swing with related data
                ServerAESKeyInfo.setText(serverService.server.aesKey);
                try {
                    // using serverService - sendAESKeyToClient method
                    serverService.sendAESKeyToClient();
                } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // get and save publickey from client
    private static void getAndSavePublicKeyFromClient() {
        LoadFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // using serverService - getPublicKeyFromClient method
                    serverService.getPublicKeyFromClient();
                    // update Swing with related data
                    ClientPublicKeyInfo.setText(String.valueOf(serverService.server.ClientPublicKey));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                // save public key data to client local
                File file = new File("");
                String rootPath = String.valueOf(file.getAbsoluteFile());
                rootPath += "\\src\\main\\java\\com\\security\\filestore\\server\\keystorage\\ClientPublicKey.txt";

                // using outputstream to save data
                try (OutputStream output = new FileOutputStream(rootPath);
                ) {
                    String str = String.valueOf(serverService.server.ClientPublicKey);
                    byte[] by = str.getBytes();
                    output.write(by);
                    // alarm that data is saved
                    JOptionPane.showMessageDialog(null, rootPath + " 에 Server 의 publicKey저장되었습니다.");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }

    // send my public key to client
    private static void sendMyPublicKeyToClient() {
        SendPublicKeyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // using clientService - sendPublicKeyToServer method
                    String encrypted = serverService.sendPublicKeyToClient(serverService.server.publicKey);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // save my RSA key (public key and private key)
    private static void saveMyRSAKeyToTxt() {
        SaveIntoFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverService == null) {
                    return;
                }

                // save in server local
                File file = new File("");
                String rootPath = String.valueOf(file.getAbsoluteFile());
                rootPath += "\\src\\main\\java\\com\\security\\filestore\\server\\keystorage\\ServerPublicKey.txt";

                // using outputstream to save data
                try (OutputStream output = new FileOutputStream(rootPath);
                ) {
                    String str = String.valueOf(serverService.server.publicKey);
                    byte[] by = str.getBytes();
                    output.write(by);
                    // alarm that data is saved
                    JOptionPane.showMessageDialog(null, rootPath + " 에 server 의 publicKey저장되었습니다.");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // make RSA key
    private static void makeRSAKey() {
        RSAKeyGenerationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if no connection, nothing happen
                if (serverService == null) {
                    return;
                }
                KeyPair keyPair = null;
                // get RSA key pair
                try {
                    keyPair = RSAUtil.genRSAKeyPair();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                }

                // set server's field with data
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();
                serverService.server.publicKey = publicKey;
                serverService.server.privateKey = privateKey;

                // update Swing with related data
                ServerPublicKeyInfo.setText(String.valueOf(serverService.server.publicKey));
                ServerPrivateKeyInfo.setText(String.valueOf(serverService.server.privateKey));

            }
        });
    }

    // connection
    private static void connection() {
        ConnectionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connect) {
                    // step2
                    // after get reaction from client
                    // you can finish whole connection between client and server
                    try {
                        serverService.makeSock();
                        ConnectionInfo.setText("Connection : Success");
                        IPLabel.setText("IP address : " + serverService.server.ipAddress);
                        PortLabel.setText("Socket info : " + serverService.getSock());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return;
                }
                try {
                    // step1
                    // make new server and update Swing with related data
                    // wait client reaction
                    serverService = new ServerService();
                    ConnectionInfo.setText("Connection : waiting client connection");
                    connect = true;
                    System.out.println(serverService.server.ipAddress);
                    IPLabel.setText("IP address : " + serverService.server.ipAddress);
                    PortLabel.setText("Socket info : " + serverService.getSock());
                } catch (IOException ex) {
                    // this is failure case
                    ConnectionInfo.setText("Connection : Failure");
                    IPLabel.setText("IP address : ");
                    PortLabel.setText("Socket info : ");
                    ex.printStackTrace();
                }
            }
        });
    }


    /**
     * Create the frame.
     */
    public ServerSwing() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 653, 741);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel modeNo = new JLabel("Mode : Server");
        modeNo.setFont(new Font("Yu Gothic", Font.BOLD, 15));
        modeNo.setBounds(12, 10, 111, 83);
        contentPane.add(modeNo);

        IPLabel = new JLabel("IP address : ");
        IPLabel.setBounds(144, 20, 467, 28);
        contentPane.add(IPLabel);

        PortLabel = new JLabel("Socket info : ");
        PortLabel.setBounds(144, 48, 467, 28);
        contentPane.add(PortLabel);

        ConnectionBtn = new JButton("Connection");
        ConnectionBtn.setBounds(12, 91, 115, 23);
        contentPane.add(ConnectionBtn);

        JLabel KeyNo = new JLabel("Key management");
        KeyNo.setFont(new Font("굴림", Font.BOLD, 12));
        KeyNo.setBounds(12, 134, 115, 23);
        contentPane.add(KeyNo);

        RSAKeyGenerationBtn = new JButton("RSA key generation");
        RSAKeyGenerationBtn.setBounds(12, 167, 143, 23);
        contentPane.add(RSAKeyGenerationBtn);

        SaveIntoFileBtn = new JButton("Save into a file");
        SaveIntoFileBtn.setBounds(177, 167, 132, 23);
        contentPane.add(SaveIntoFileBtn);

        SendPublicKeyBtn = new JButton("Send Public Key");
        SendPublicKeyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        SendPublicKeyBtn.setBounds(321, 167, 143, 23);
        contentPane.add(SendPublicKeyBtn);

        LoadFileBtn = new JButton("Load from a file");
        LoadFileBtn.setBounds(476, 167, 151, 23);
        contentPane.add(LoadFileBtn);

        JLabel ServerNo = new JLabel("Server");
        ServerNo.setFont(new Font("굴림", Font.BOLD, 12));
        ServerNo.setBounds(12, 200, 50, 15);
        contentPane.add(ServerNo);

        JLabel PublicNo = new JLabel("Public Key");
        PublicNo.setBounds(12, 225, 85, 15);
        contentPane.add(PublicNo);

        JLabel PrivateNo = new JLabel("Private Key");
        PrivateNo.setBounds(12, 250, 85, 15);
        contentPane.add(PrivateNo);

        JLabel AesNo = new JLabel("AES Key");
        AesNo.setBounds(12, 275, 85, 15);
        contentPane.add(AesNo);

        JLabel ClinetNo = new JLabel("Client");
        ClinetNo.setFont(new Font("굴림", Font.BOLD, 12));
        ClinetNo.setBounds(12, 300, 50, 15);
        contentPane.add(ClinetNo);

        JLabel PublicNo2 = new JLabel("Public Key");
        PublicNo2.setBounds(12, 325, 85, 15);
        contentPane.add(PublicNo2);

        JLabel PrivateNo2 = new JLabel("Private Key");
        PrivateNo2.setBounds(12, 350, 85, 15);
        contentPane.add(PrivateNo2);

        JLabel AesNo2 = new JLabel("AES Key");
        AesNo2.setBounds(12, 375, 85, 15);
        contentPane.add(AesNo2);

        JLabel ChatNo = new JLabel("Chat");
        ChatNo.setFont(new Font("굴림", Font.BOLD, 12));
        ChatNo.setBounds(12, 418, 50, 15);
        contentPane.add(ChatNo);

        ConnectionInfo = new JLabel("Connection : Failure");
        ConnectionInfo.setBounds(144, 95, 483, 15);
        contentPane.add(ConnectionInfo);

        ServerPublicKeyInfo = new JTextField();
        ServerPublicKeyInfo.setBackground(Color.WHITE);
        ServerPublicKeyInfo.setBounds(105, 225, 522, 15);
        contentPane.add(ServerPublicKeyInfo);

        ServerPrivateKeyInfo = new JTextField();
        ServerPrivateKeyInfo.setBackground(Color.WHITE);
        ServerPrivateKeyInfo.setBounds(105, 250, 522, 15);
        contentPane.add(ServerPrivateKeyInfo);

        ServerAESKeyInfo = new JTextField();
        ServerAESKeyInfo.setBackground(Color.WHITE);
        ServerAESKeyInfo.setBounds(105, 275, 522, 15);
        contentPane.add(ServerAESKeyInfo);

        ClientPublicKeyInfo = new JTextField();
        ClientPublicKeyInfo.setBackground(Color.WHITE);
        ClientPublicKeyInfo.setBounds(105, 325, 522, 15);
        contentPane.add(ClientPublicKeyInfo);

        ClientPrivateKeyInfo = new JTextField();
        ClientPrivateKeyInfo.setBackground(Color.WHITE);
        ClientPrivateKeyInfo.setBounds(105, 350, 522, 15);
        contentPane.add(ClientPrivateKeyInfo);

        ClientAESKeyInfo = new JTextField();
        ClientAESKeyInfo.setBackground(Color.WHITE);
        ClientAESKeyInfo.setBounds(105, 375, 522, 15);
        contentPane.add(ClientAESKeyInfo);

        ChatInfo = new JTextArea();
        ChatInfo.setBackground(new Color(255, 255, 255));
        ChatInfo.setBounds(12, 443, 269, 184);
        contentPane.add(ChatInfo);

        ChattextField = new JTextField();
        ChattextField.setBounds(86, 642, 415, 23);
        contentPane.add(ChattextField);
        ChattextField.setColumns(10);

        JLabel writeNo = new JLabel("Write msg");
        writeNo.setBounds(12, 646, 85, 15);
        contentPane.add(writeNo);

        JLabel encryptedNo = new JLabel("encrypted msg");
        encryptedNo.setBounds(12, 671, 96, 15);
        contentPane.add(encryptedNo);

        MsgEncryptInfo = new JTextField();
        MsgEncryptInfo.setBackground(Color.WHITE);
        MsgEncryptInfo.setBounds(110, 671, 507, 15);
        contentPane.add(MsgEncryptInfo);

        JLabel FileNo = new JLabel("File send");
        FileNo.setFont(new Font("굴림", Font.BOLD, 12));
        FileNo.setBounds(321, 418, 85, 15);
        contentPane.add(FileNo);

        JLabel FileNo2 = new JLabel("encryped file stream");
        FileNo2.setBounds(321, 443, 143, 15);
        contentPane.add(FileNo2);

        FileEncryptedInfo = new JTextArea();
        FileEncryptedInfo.setBackground(Color.WHITE);
        FileEncryptedInfo.setBounds(321, 462, 306, 117);
        FileEncryptedInfo.setFont(new Font("Serif", 0, 8));
        contentPane.add(FileEncryptedInfo);


        SendMsgBtn = new JButton("Send msg");
        SendMsgBtn.setBounds(513, 642, 114, 23);
        contentPane.add(SendMsgBtn);

        SendAESKeyBtn = new JButton("Make and send encrypted AES");
        SendAESKeyBtn.setBounds(177, 134, 229, 23);
        contentPane.add(SendAESKeyBtn);

        GETAESKeyBtn = new JButton("Get and decrypt AES Key");
        GETAESKeyBtn.setBounds(418, 134, 209, 23);
        contentPane.add(GETAESKeyBtn);

        SendFileBtn = new JButton("send file");
        SendFileBtn.setBounds(321, 589, 157, 23);
        contentPane.add(SendFileBtn);

        GetFileBtn = new JButton("get file");
        GetFileBtn.setBounds(482, 589, 157, 23);
        contentPane.add(GetFileBtn);
    }
}
