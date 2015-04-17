package main;

import tools.AddressAssistant;
import tools.ServerLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServer extends Thread {
    final public static int SERVER_PORT = 4444;
    private boolean isRunning = false;
    private PrintWriter messageWriter;
    private String clientAddress;
    ServerSocket serverSocket;

    public static void main(String[] args) {
        new MainFrame();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public TCPServer() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerLogger.logMessage(AddressAssistant.getLocalAddress());
        ServerLogger.logMessage(AddressAssistant.getExternalAddress());
    }

    public void sendMessage(String message){
        if (messageWriter != null && !messageWriter.checkError()) {
            System.out.println("S: Sending: " + message);
            messageWriter.println(message);
            messageWriter.flush();
        }
    }

    public String getClientAddress() {
        return clientAddress != null ? clientAddress : "";
    }

    @Override
    public void run() {
        try {
            ServerLogger.logMessage("Oczekiwanie na połączenie.");
            Socket client = serverSocket.accept();
            client.setKeepAlive(true);

            try {
                System.out.println("Initializing");

                MessageReceiver messageReceiver = new MessageReceiver(this);
                messageWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(client.getInputStream()));

                clientAddress = client.getRemoteSocketAddress().toString();

                System.out.println("Initialized");

                isRunning = true;
                while (isRunning) {
                    System.out.println(inputBuffer.read());
                    /*String message = inputBuffer.readLine();
                    System.out.println("S: Received: " + message);
                    if (message != null) {
                        System.out.println(message);
                        messageReceiver.receiveMessage(new Message(message));
                    }*/
                }

            } finally {
                client.close();
                this.close();
            }
        } catch (IOException exception) {
            ServerLogger.logMessage("Wystąpił błąd połączenia.");
        }
    }

    public void close() {
        isRunning = false;
    }
}
