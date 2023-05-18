package zad1;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends Application {
	
	private static final String MAIN_SERVER_ADDRESS = "localhost";
	private static final int MAIN_SERVER_PORT = 1234;

public static void main(String[] args) {
    launch(args);
}

@Override
public void start(Stage primaryStage) {
    TextField wordTextField = new TextField();
    wordTextField.setPromptText("Wpisz polskie słowo");

    ComboBox<String> languageComboBox = new ComboBox<>();
    languageComboBox.getItems().addAll("EN", "FR", "IT");
    languageComboBox.setValue("EN");

    TextField portTextField = new TextField();
    portTextField.setPromptText("Wpisz port klienta");

    Button translateButton = new Button("Tłumacz");
    Label translationLabel = new Label();

    translateButton.setOnAction(e -> {
        String word = wordTextField.getText();
        String languageCode = languageComboBox.getValue();

        try {
            int clientPort = Integer.parseInt(portTextField.getText());
            if (clientPort < 1 || clientPort > 65535) {
                translationLabel.setText("Błąd: Nieprawidłowy numer portu (1-65535).");
            } else {
                String translation = getTranslation(word, languageCode, clientPort);
                translationLabel.setText(translation);
            }
        } catch (NumberFormatException ex) {
            translationLabel.setText("Błąd: Numer portu musi być liczbą całkowitą.");
        }
    });

    VBox vbox = new VBox(10, wordTextField, languageComboBox, portTextField, translateButton, translationLabel);
    vbox.setAlignment(Pos.CENTER);
    primaryStage.setScene(new Scene(vbox, 300, 200));
    primaryStage.setTitle("Tłumacz słów");
    primaryStage.show();
}

private String getTranslation(String word, String languageCode, int clientPort) {
    try (Socket mainServerSocket = new Socket(MAIN_SERVER_ADDRESS, MAIN_SERVER_PORT)) {
        PrintWriter out = new PrintWriter(mainServerSocket.getOutputStream(), true);
        out.println(word + "," + languageCode + "," + clientPort);

        ServerSocket clientTranslationSocket = new ServerSocket(clientPort);
        Socket languageServerSocket = clientTranslationSocket.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(languageServerSocket.getInputStream()));
        String translation = in.readLine();

        languageServerSocket.close();
        clientTranslationSocket.close();

        return translation;
    } catch (IOException e) {
        e.printStackTrace();
        return "Błąd: Nie udało się uzyskać tłumaczenia.";
    }
}
}
