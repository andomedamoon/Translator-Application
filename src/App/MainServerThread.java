package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainServerThread extends Thread {
    private Socket clientSocket;

    public MainServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String[] requestParts = in.readLine().split(",");

            String word = requestParts[0];
            String languageCode = requestParts[1];
            int clientPort = Integer.parseInt(requestParts[2]);

            int languageServerPort;
            switch (languageCode) {
                case "EN":
                    languageServerPort = 2000;
                    break;
                case "FR":
                    languageServerPort = 2001;
                    break;
                case "IT":
                	languageServerPort = 2002;
                	break;
                	default:
                	try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                	out.println("Błąd: Nieprawidłowy kod języka.");
                	}
                	return;
                	}
            try (Socket languageServerSocket = new Socket("localhost", languageServerPort)) {
                PrintWriter out = new PrintWriter(languageServerSocket.getOutputStream(), true);
                out.println(word + "," + clientSocket.getInetAddress().getHostAddress() + "," + clientPort);
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
