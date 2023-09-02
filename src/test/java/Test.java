import java.io.*;
import java.net.DatagramSocketImpl;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public class Test {
    @org.junit.jupiter.api.Test
    public void testConnection() throws IOException {
        ChatServer chatServer = new ChatServer();
        ChatClient chatClient = new ChatClient();

        BufferedReader brTest = new BufferedReader(new FileReader("settings.txt"));
        String text = brTest.readLine();
        String[] strings = text.split(" ");
        ServerSocket server = new ServerSocket(Integer.parseInt(strings[2]));

        InputStreamReader isr = new InputStreamReader(System.in);

        Socket client = new Socket("127.0.0.1", Integer.parseInt(strings[2]));
        DataOutputStream os = new DataOutputStream(client.getOutputStream());
        DataInputStream is = new DataInputStream(client.getInputStream());
        String request = "alex";
        os.writeUTF(request);

        String requestedClientName = "";

        client = server.accept();
        if (client != null) {
            os = new DataOutputStream(client.getOutputStream());
            is = new DataInputStream(client.getInputStream());
            requestedClientName = is.readUTF();


        }

        brTest.close();
        isr.close();

        os.close();
        is.close();

        server.close();
        client.close();

        assertThat(requestedClientName, equalTo(request));
    }

    @org.junit.jupiter.api.Test
    public void testAddToClientList() throws IOException {
        ChatServer chatServer = new ChatServer();
        ChatClient chatClient = new ChatClient();

        BufferedReader brTest = new BufferedReader(new FileReader("settings.txt"));
        String text = brTest.readLine();
        String[] strings = text.split(" ");
        ServerSocket server = new ServerSocket(Integer.parseInt(strings[2]));

        InputStreamReader isr = new InputStreamReader(System.in);

        Socket client = new Socket("127.0.0.1", Integer.parseInt(strings[2]));
        DataOutputStream os = new DataOutputStream(client.getOutputStream());
        DataInputStream is = new DataInputStream(client.getInputStream());
        String request = "alex";
        os.writeUTF(request);

        String requestedClientName = "";

        MyThreadServer messageRouterThread = new MyThreadServer();
        messageRouterThread.start();
        HashMap<String, Socket> clientList = new HashMap<String, Socket>();

        client = server.accept();
        if (client != null) {
            os = new DataOutputStream(client.getOutputStream());
            is = new DataInputStream(client.getInputStream());
            requestedClientName = is.readUTF();

            clientList.put(requestedClientName, client);


            messageRouterThread.clientList.put(requestedClientName, client);

        }

        brTest.close();
        isr.close();

        os.close();
        is.close();

        server.close();
        client.close();

        Set<String> clientsSet = messageRouterThread.clientList.keySet();

        List<String> clientsList = clientsSet.stream().toList();

        assertThat(clientsList, hasSize(1));
    }
}
