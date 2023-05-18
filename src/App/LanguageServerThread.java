package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class LanguageServerThread extends Thread {
    private Socket clientSocket;
    private Map<String, String> dictionary;

    public LanguageServerThread(Socket clientSocket, Map<String, String> dictionary) {
        this.clientSocket = clientSocket;
        this.dictionary = dictionary;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String[] requestParts = in.readLine().split(",");

            String word = requestParts[0];
            String clientIP = requestParts[1];
            int clientPort = Integer.parseInt(requestParts[2]);

            String translation = dictionary.getOrDefault(word, "Brak tłumaczenia");

            try (Socket translationSocket = new Socket(clientIP, clientPort)) {
                PrintWriter out = new PrintWriter(translationSocket.getOutputStream(), true);
                out.println(translation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}