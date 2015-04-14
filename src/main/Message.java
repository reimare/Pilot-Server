package main;

import java.util.ArrayList;
import java.util.Arrays;


public class Message {
    private final String header;
    private final ArrayList<String> body;

    public Message(String receivedString) {
        String [] splitMessage = receivedString.split(";");

        header = splitMessage[0];
        body = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(splitMessage, 1, splitMessage.length)));
    }

    public String getHeader() {
        return header;
    }

    public ArrayList<String> getBody() {
        return body;
    }
}
