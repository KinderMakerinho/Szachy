import java.io.*;
import java.net.*;
import java.util.*;

public class ChessServer {
    private static final int PORT = 12345;
    private static final Map<String, PrintWriter> clients = new HashMap<>();

    private static String whitePlayer = null;
    private static String blackPlayer = null;
    private static boolean isWhiteTurn = true; // Kontrola tury (true -> białe, false -> czarne)

    public static void main(String[] args) {
        System.out.println("Serwer szachowy uruchomiony...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Rozsyła wiadomość do wszystkich klientów
    private static synchronized void broadcast(String message) {
        for (PrintWriter out : clients.values()) {
            out.println(message);
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String playerName;  // samo imię gracza, np. "Tomeczek" / "tOMEK"
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Wprowadź swoje imię:");
                String rawName = in.readLine();

                // Usuwamy potencjalne prefiksy/dopiski, by uzyskać czyste imię
                playerName = extractPureName(rawName);
                if (playerName == null || playerName.isEmpty()) {
                    out.println("Błąd formatu nazwy. Rozłączanie...");
                    return;
                }

                synchronized (clients) {
                    // Sprawdzamy, czy imię nie jest zajęte
                    if (clients.containsKey(playerName)) {
                        out.println("Imię już zajęte. Rozłączanie...");
                        return;
                    }
                    clients.put(playerName, out);
                }

                System.out.println(playerName + " dołączył do gry.");
                // Informacja o stanie lobby
                broadcast("LOBBY_STATE:BIAŁY=" + (whitePlayer == null ? "Brak" : whitePlayer)
                        + ",CZARNY=" + (blackPlayer == null ? "Brak" : blackPlayer));

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Odebrano od " + playerName + ": " + message);

                    if (message.startsWith("WHITE:")) {
                        handleWhiteSelection(message);
                    } else if (message.startsWith("BLACK:")) {
                        handleBlackSelection(message);
                    } else if (message.equals("START_GAME")) {
                        handleStartGame();
                    } else if (message.startsWith("MOVE:")) {
                        handleMoveMessage(message, playerName); // Teraz przekazuje playerName poprawnie
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                handleClientDisconnect();
            }
        }

        /**
         * Usuwa prefiksy "WHITE:", "BLACK:", oraz dopisek ", Kolor" z rawName
         * i zwraca samo imię gracza (np. "Tomeczek", "tOMEK").
         */
        private String extractPureName(String rawName) {
            if (rawName == null) return null;
            // Najpierw usuń potencjalne prefiksy "WHITE:" / "BLACK:"
            rawName = rawName.replace("WHITE:", "").replace("BLACK:", "");
            // Usuń ewentualne ", Kolor"
            rawName = rawName.replace(", Kolor", "").trim();
            return rawName;
        }

        // Obsługa wyboru koloru białego
        private void handleWhiteSelection(String rawMessage) {
            synchronized (clients) {
                String extractedName = extractPureName(rawMessage); // np. "Tomeczek"
                if (extractedName == null || extractedName.isEmpty()) {
                    out.println("Błąd formatu WHITE: nazwa gracza jest pusta.");
                    return;
                }

                if (whitePlayer == null) {
                    whitePlayer = extractedName;
                    broadcast("LOBBY_STATE:BIAŁY=" + whitePlayer + ",CZARNY="
                            + (blackPlayer == null ? "Brak" : blackPlayer));
                } else {
                    out.println("Kolor BIAŁY jest już zajęty.");
                }
            }
        }

        // Obsługa wyboru koloru czarnego
        private void handleBlackSelection(String rawMessage) {
            synchronized (clients) {
                String extractedName = extractPureName(rawMessage); // np. "tOMEK"
                if (extractedName == null || extractedName.isEmpty()) {
                    out.println("Błąd formatu BLACK: nazwa gracza jest pusta.");
                    return;
                }

                if (blackPlayer == null) {
                    blackPlayer = extractedName;
                    broadcast("LOBBY_STATE:BIAŁY="
                            + (whitePlayer == null ? "Brak" : whitePlayer)
                            + ",CZARNY=" + blackPlayer);
                } else {
                    out.println("Kolor CZARNY jest już zajęty.");
                }
            }
        }

        // Rozpoczęcie gry
        private void handleStartGame() {
            synchronized (clients) {
                if (whitePlayer != null && blackPlayer != null) {
                    String startMessage = "START_GAME:BIAŁY=" + whitePlayer + ",CZARNY=" + blackPlayer;
                    System.out.println("Rozpoczynam grę: " + startMessage);
                    broadcast(startMessage);

                    // **Nowa linia** - poinformowanie klientów, że zaczyna biały
                    broadcast("TURN:WHITE");
                } else {
                    out.println("Nie można rozpocząć gry – obaj gracze muszą wybrać kolory.");
                }
            }
        }

        private void handleMoveMessage(String message, String playerName) {
            synchronized (clients) {
                String movePart = message.substring("MOVE:".length()).trim();
                boolean isMoveByWhite = playerName.equals(whitePlayer);
                boolean isMoveByBlack = playerName.equals(blackPlayer);

                System.out.println("🔍 Otrzymano ruch od: " + playerName);
                System.out.println("🎯 Obecna tura: " + (isWhiteTurn ? "WHITE" : "BLACK"));

                if (isWhiteTurn && !isMoveByWhite) {
                    sendToPlayer(playerName, "ERROR:Nie jest Twoja tura.");
                    return;
                }
                if (!isWhiteTurn && !isMoveByBlack) {
                    sendToPlayer(playerName, "ERROR:Nie jest Twoja tura.");
                    return;
                }

                // **Najpierw rozsyłamy ruch**
                String color = isWhiteTurn ? "WHITE" : "BLACK";
                broadcast("MOVE:" + color + ":" + movePart);
                System.out.println("✅ Ruch zaakceptowany: " + playerName + " wykonał ruch: " + movePart);

                // **Dopiero teraz zmieniamy turę**
                isWhiteTurn = !isWhiteTurn;

                // Wysłanie informacji o nowej turze
                String nextTurn = isWhiteTurn ? "WHITE" : "BLACK";
                broadcast("TURN:" + nextTurn);
                System.out.println("🔄 Tura zmieniona na: " + nextTurn);
            }
        }





        private void sendToPlayer(String playerName, String message) {
            PrintWriter playerOut = clients.get(playerName);
            if (playerOut != null) {
                playerOut.println(message);
            }
        }

        private void handleClientDisconnect() {
            synchronized (clients) {
                clients.remove(playerName);
                if (playerName != null && playerName.equals(whitePlayer)) {
                    whitePlayer = null;
                } else if (playerName != null && playerName.equals(blackPlayer)) {
                    blackPlayer = null;
                }
                broadcast("LOBBY_STATE:BIAŁY="
                        + (whitePlayer == null ? "Brak" : whitePlayer)
                        + ",CZARNY="
                        + (blackPlayer == null ? "Brak" : blackPlayer));
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}