# Java-Swing-Socket-Program-Chat-File-with-RSA-and-AES

Java Swing Socket Program - Chatting program, file sending program, exchange key program using RSA key, AES key and digital signature

## 1.	Class Relation

![image](https://user-images.githubusercontent.com/71916223/162585231-e64aab0c-6b32-4671-a85e-d72011e8e6b1.png)

## 2. Using algorithm 
1. RSA 1024
2. AES 256

## 3. Main functions
1. Connect client and server using socket with each Swing program
2. Make their own public key and private key using RSA algorim
3. Send each public key text to each other 
4. Make AES key and send it by using other's public key and other side get AES key using his own private key
5. Using AES key, they can chat
6. Using AES key, they can send file
7. When send file, attach other side's public key as digital signature and ecrypted by AES key. After other side gets it and decrypt by AES key, other side can compare attached public key and his own public key. If they are same, integrity is saved.

## 4. Run program
![image](https://user-images.githubusercontent.com/71916223/162585522-c77f244d-fa96-4db6-8bd7-b21b62ca7340.png)


### you can see more detail in doc file
