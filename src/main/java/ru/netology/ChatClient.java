package ru.netology;

import java.io.*;
import java.net.*;


public class ChatClient {
    private static final Logger logger = Logger.getInstance();
    public Socket client = null;
    public DataOutputStream os;
    public DataInputStream is;
    public String clientName = "@Client0";

    public ChatClient() {

    }

    public static void main(String[] args) throws IOException {

        ChatClient a = new ChatClient();
        a.doConnections();

    }

    public void doConnections() throws IOException {
        try {
            BufferedReader brTest = new BufferedReader(new FileReader("settings.txt"));
            String text = brTest.readLine();
            String[] strings = text.split(" ");

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            System.out.println("Введите свое имя <Пожалуйста не используйте пробелы> : ");
            logger.log("Сервер - Введите свое имя <Пожалуйста не используйте пробелы> : ");
            clientName = "@" + br.readLine();
            client = new Socket("127.0.0.1", Integer.parseInt(strings[2]));
            os = new DataOutputStream(client.getOutputStream());
            is = new DataInputStream(client.getInputStream());
            //request as a client
            String request = clientName;
            os.writeUTF(request);
            logger.log(request);
            String response = is.readUTF();
            MyThreadRead read = new MyThreadRead(is);
            MyThreadWrite write = new MyThreadWrite(os, clientName);
            if (response.equals("#подтверждено")) {
                logger.log("Сервер - # Зарегестрирован успешно как " + clientName + " !");
                logger.log("Сервер - Формат сообщения");
                logger.log("Сервер - --------------");
                logger.log("Сервер - Тело сообщения @кому [Тело сообщения может содержать пробелы]");
                System.out.println("# Зарегестрирован успешно как " + clientName + " !");
                System.out.println("Формат сообщения");
                System.out.println("--------------");
                System.out.println("Тело сообщения @кому [Тело сообщения может содержать пробелы]");
                //now run the thread
                read.start();
                write.start();

                read.join();
                write.join();
            } else {
                logger.log("Сервер - # Невозможно подключиться к серверу !");
                System.out.println("# Невозможно подключиться к серверу !");
            }

        } catch (Exception e) {
            logger.log("Сервер - Ошибка упс!");
            System.out.println("Ошибка упс!");
        }
    }
}

class MyThreadRead extends Thread {
    DataInputStream is;

    public MyThreadRead(DataInputStream i) {
        is = i;
    }

    public void run() {
        try {
            String msg = null;
            while (true) {
                msg = is.readUTF();
                if (msg != null)
                    System.out.println(msg);
                msg = null;
            }
        } catch (Exception e) {

        }
    }
}

class MyThreadWrite extends Thread {
    private DataOutputStream os;
    public BufferedReader br;
    public String clientName = "@Client0";

    public MyThreadWrite(DataOutputStream o, String name) {
        os = o;
        clientName = name;
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
        } catch (Exception e) {

        }
    }

    public void run() {
        try {
            while (true) {
                String msg = br.readLine();
                os.writeUTF(msg);
            }
        } catch (Exception e) {

        }
    }
}