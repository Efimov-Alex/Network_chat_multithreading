package ru.netology;

import java.io.*;

import java.net.*;
import java.util.*;



public class ChatServer {
    private static final Logger logger = Logger.getInstance();
    public ServerSocket server;
    public Socket client = null;
    public DataOutputStream os;
    public DataInputStream is;
    public HashMap<String, Socket> clientList = new HashMap<String, Socket>();

    public static void main(String[] args) {
        ChatServer a = new ChatServer();
        a.doConnections();
    }

    public void doConnections() {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader("settings.txt"));
            String text = brTest.readLine();
            String[] strings = text.split(" ");


            server = new ServerSocket(Integer.parseInt(strings[2]));
            MyThreadServer messageRouterThread = new MyThreadServer();
            messageRouterThread.start();
            while (true) {

                client = server.accept();
                if (client != null) {
                    os = new DataOutputStream(client.getOutputStream());
                    is = new DataInputStream(client.getInputStream());
                    String requestedClientName = is.readUTF();
                    clientList.put(requestedClientName, client);


                    os.writeUTF("#подтверждено");
                    messageRouterThread.clientList.put(requestedClientName, client);
                }
            }
        } catch (Exception e) {
            logger.log("Сервер - Ошибка упс!");
            System.out.println("Ошибка упс!" + e.getMessage());
        }
    }
}

class MyThreadServer extends Thread {
    private static final Logger logger = Logger.getInstance();

    public HashMap<String, Socket> clientList = new HashMap<String, Socket>();
    public DataInputStream is = null;
    public DataOutputStream os = null;

    public void run() {


        String msg = "";
        int i = 0;
        logger.log("Сервер - Сервер чата начинает работу .....");
        System.out.println("Сервер чата начинает работу .....");
        String toClientName = "";
        String clientWantExit = "";
        while (true) {
            try {
                if (clientList != null) {
                    for (String key : clientList.keySet()) {
                        is = new DataInputStream(clientList.get(key).getInputStream());
                        if (is.available() > 0) {
                            msg = is.readUTF();

                            if (msg.equals("/exit")) {
                                clientWantExit = key;
                                continue;
                            }

                            i = msg.indexOf("@");
                            toClientName = msg.substring(i);
                            // System.out.println(msg);
                            // obtained the client name
                            // now write that message to that client
                            if (clientList.containsKey(toClientName)) {
                                os = new DataOutputStream(clientList.get(toClientName).getOutputStream());
                                os.writeUTF(key + ": " + msg.substring(0, i));
                                logger.log(key + ": " + msg.substring(0, i));
                            } else {
                                os = new DataOutputStream(clientList.get(key).getOutputStream());
                                logger.log("Сервер - Сообщение от вервера :Нет такого имени");
                                os.writeUTF("Сообщение от вервера :Нет такого имени");
                            }
                        }
                    }

                    if (clientList.get(clientWantExit) != null) {
                        clientList.get(clientWantExit).close();
                        clientList.remove(clientWantExit);
                        clientWantExit = "";
                    }


                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                logger.log("Сервер - Сервер восстанавливает ошибку инициализации ...");
                logger.log("Сервер - Сервер Запущен ...");
                System.out.println("Сервер восстанавливает ошибку инициализации ...");
                System.out.println("Сервер Запущен ...");
            }
            //goes through all clientList and reads the UTF and Writes to that Client
        }
    }
}
