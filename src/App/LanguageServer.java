package zad1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LanguageServer {
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        Map<String, Integer> languagePorts = new HashMap<>();
        languagePorts.put("EN", 2000);
        languagePorts.put("FR", 2001);
        languagePorts.put("IT", 2002);

        for (Map.Entry<String, Integer> entry : languagePorts.entrySet()) {
            String languageCode = entry.getKey();
            int port = entry.getValue();
            Map<String, String> dictionary = createDictionary(languageCode);
            startLanguageServer(port, dictionary);
        }
    }

    private static Map<String, String> createDictionary(String languageCode) {
        Map<String, String> dictionary = new HashMap<>();
        switch (languageCode) {
            case "EN":
                dictionary.put("dom", "house");
                dictionary.put("pies", "dog");
                dictionary.put("kot", "cat");
                break;
            case "FR":
                dictionary.put("dom", "maison");
                dictionary.put("pies", "chien");
                dictionary.put("kot", "chat");
                break;
            case "IT":
                dictionary.put("dom", "casa");
                dictionary.put("pies", "cane");
                dictionary.put("kot", "gatto");
                break;
        }
        return dictionary;
    }

    private static void startLanguageServer(int port, Map<String, String> dictionary) {
        Runnable serverTask = () -> {
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    LanguageServerThread languageServerThread = new LanguageServerThread(clientSocket, dictionary);
                    executorService.execute(languageServerThread);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                executorService.shutdown();
            }
        };
        new Thread(serverTask).start();
    }
}
