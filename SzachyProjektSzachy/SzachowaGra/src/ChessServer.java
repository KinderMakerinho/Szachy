import java.io.*;
import java.net.*;

public class ChessServer {
    private static final int PORT = 12345;  // Port serwera
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Serwer uruchomiony na porcie " + PORT);

            // Czekamy na połączenia dwóch graczy
            Socket player1Socket = serverSocket.accept();
            System.out.println("Gracz 1 połączony.");
            Socket player2Socket = serverSocket.accept();
            System.out.println("Gracz 2 połączony.");

            // Tworzymy wątki do obsługi każdego gracza
            new Thread(new ClientHandler(player1Socket, true)).start();  // Gracz 1 (Biały)
            new Thread(new ClientHandler(player2Socket, false)).start(); // Gracz 2 (Czarny)

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Klasa do obsługi komunikacji z każdym graczem
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private boolean isWhitePlayer;  // Zmienna wskazująca, czy to gracz biały
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, boolean isWhitePlayer) {
            this.clientSocket = socket;
            this.isWhitePlayer = isWhitePlayer;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Powitanie gracza
                out.println("Połączono z serwerem. Jesteś " + (isWhitePlayer ? "Białym" : "Czarnym") + " graczem.");

                // Oczekiwanie na wybór koloru od graczy
                String colorChoice = in.readLine();  // Oczekiwanie na wybór koloru
                System.out.println("Gracz wybrał: " + colorChoice);

                // Jeśli gracz wybrał Białego, informujemy drugiego gracza
                if (colorChoice.equals("Biały")) {
                    out.println("Gracz Biały jest gotowy. Czekamy na gracza Czarnego...");
                } else if (colorChoice.equals("Czarny")) {
                    out.println("Gracz Czarny jest gotowy. Czekamy na gracza Białego...");
                }

                // Połączenie graczy: informowanie drugiego gracza o połączeniu pierwszego
                if (isWhitePlayer) {
                    // Czekamy na połączenie drugiego gracza (Czarnego)
                    Socket secondPlayerSocket = serverSocket.accept();
                    new Thread(new ClientHandler(secondPlayerSocket, false)).start(); // Drugi gracz
                    out.println("Gracz 2 połączony. Rozpoczynamy grę!");
                } else {
                    // Czekaj na wiadomość od drugiego gracza (Czarnego)
                    String opponentMessage = in.readLine();
                    System.out.println(opponentMessage);
                    out.println("Gracz 1 połączony. Rozpoczynamy grę!");
                }

                // Główna pętla gry
                String move;
                while ((move = in.readLine()) != null) {
                    System.out.println("Odebrano ruch: " + move + " od gracza " + (isWhitePlayer ? "Białego" : "Czarnego"));

                    // Przekaż ruch do drugiego klienta
                    if (isWhitePlayer) {
                        out.println("Ruch Białego: " + move);  // Przesyłaj ruch białych do czarnego gracza
                    } else {
                        out.println("Ruch Czarnego: " + move);  // Przesyłaj ruch czarnych do białego gracza
                    }
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
}
