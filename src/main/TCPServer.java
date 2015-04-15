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
            messageWriter.println(message);
            messageWriter.flush();
        }
    }

    @Override
    public void run() {
        try {
            ServerLogger.logMessage("Oczekiwanie na połączenie.");
            Socket client = serverSocket.accept();
            client.setKeepAlive(true);

            try {
                MessageReceiver messageReceiver = new MessageReceiver();
                messageWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(client.getInputStream()));

                Message connectionRequest = new Message(inputBuffer.readLine());
                String clientAddress = client.getRemoteSocketAddress().toString();

                if (messageReceiver.validateConnectionRequest(connectionRequest, clientAddress)) {
                    isRunning = true;
                    while (isRunning) {
                        String message = inputBuffer.readLine();
                        if (message != null) {
                            messageReceiver.receiveMessage(message);
                        }
                    }
                }
            } finally {
                isRunning = false;
                client.close();
            }
        } catch (IOException exception) {
            ServerLogger.logMessage("Wystąpił błąd połączenia.");
        }
    }

    public void close() {
        isRunning = false;
    }
}
