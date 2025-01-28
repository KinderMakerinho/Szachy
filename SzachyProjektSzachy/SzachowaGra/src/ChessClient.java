import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChessClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Consumer<String> messageHandler; // Funkcja obsługi wiadomości z serwera
    private String playerName;  // Nazwa gracza, np. "Tomeczek" lub "tOMEK"
    private String playerColor; // Kolor gracza: "WHITE" lub "BLACK"

    // Połączenie z serwerem
    public void connectToServer(String serverAddress, Consumer<String> messageHandler) {
        try {
            this.messageHandler = messageHandler; // Przechowaj referencję do handlera wiadomości
            socket = new Socket(serverAddress, 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Połączono z serwerem!");

            // Nasłuchiwanie wiadomości od serwera w osobnym wątku
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println("Otrzymano od serwera: " + message);
                        messageHandler.accept(message); // Przekaż wiadomość do handlera w klasie Main
                    }
                } catch (IOException e) {
                    System.out.println("Rozłączono z serwerem.");
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            System.out.println("Nie udało się połączyć z serwerem.");
            e.printStackTrace();
        }
    }

    // Wysyłanie wiadomości do serwera
    public void sendToServer(String message) {
        if (out != null) {
            System.out.println("Wysyłanie do serwera: " + message);
            out.println(message);
        }
    }



    // Rozłączanie klienta
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ustawienie nazwy gracza
    public void setPlayerName(String name) {
        // Tutaj można usunąć ewentualny dopisek ", Kolor" jeśli występuje w pliku gracze.txt
        name = name.replace(", Kolor", "").trim();
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    // Ustawienie koloru gracza
    public void setPlayerColor(String color) {
        this.playerColor = color;
        System.out.println("Ustawiono kolor gracza na: " + color);
    }

    public String getPlayerColor() {
        return playerColor;
    }
}
