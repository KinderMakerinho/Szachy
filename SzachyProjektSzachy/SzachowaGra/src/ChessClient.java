import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class ChessClient extends Application {
    private static final String SERVER_ADDRESS = "localhost"; // Adres serwera
    private static final int SERVER_PORT = 12345;  // Port serwera

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Button connectButton = new Button("Połącz z serwerem");
        connectButton.setOnAction(event -> connectToServer(primaryStage));

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(connectButton);

        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setTitle("Szachy - Klient");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectToServer(Stage primaryStage) {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Odbierz wiadomość powitalną od serwera
            String message = in.readLine();
            System.out.println(message); // Dodatkowa logika reakcji na wiadomość serwera

            // Uruchom okno gry
            showGameWindow(primaryStage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showGameWindow(Stage primaryStage) {
        // Dodaj okno do połączenia z serwerem i rozpocznij etap gry
        Button exampleMoveButton = new Button("Wykonaj ruch");
        exampleMoveButton.setOnAction(event -> {
            out.println("e2e4");  // Przykładowy ruch, może być zmieniony na rzeczywisty ruch z GUI
        });

        VBox gameLayout = new VBox(20);
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.getChildren().add(exampleMoveButton);

        Scene gameScene = new Scene(gameLayout, 400, 400);
        primaryStage.setTitle("Szachy - Gra");
        primaryStage.setScene(gameScene);
        primaryStage.show();
    }
}
