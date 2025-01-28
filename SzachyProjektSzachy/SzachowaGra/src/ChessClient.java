import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChessClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Consumer<String> messageHandler;

    public void connectToServer(String serverAddress, Consumer<String> messageHandler) {
        try {
            this.messageHandler = messageHandler; // Przechowaj referencję do handlera wiadomości
            socket = new Socket(serverAddress, 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Połączono z serwerem!");

            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println("Otrzymano od serwera: " + message);
                        messageHandler.accept(message); // Przekaż wiadomość do handlera
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(String message) {
        if (out != null) {
            System.out.println("Wysyłanie do serwera: " + message);
            out.println(message);
        }
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
