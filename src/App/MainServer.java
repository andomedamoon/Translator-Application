package zad1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    private static final int MAIN_SERVER_PORT = 1234;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(MAIN_SERVER_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                MainServerThread mainServerThread = new MainServerThread(clientSocket);
                executorService.execute(mainServerThread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
