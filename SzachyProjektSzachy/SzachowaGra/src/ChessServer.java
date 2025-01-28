// ChessServer.java
import java.io.*;
import java.net.*;
import java.util.*;

public class ChessServer {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clients = new HashMap<>();
    private static String whitePlayer = null;
    private static String blackPlayer = null;

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

    private static synchronized void broadcast(String message) {
        for (PrintWriter out : clients.values()) {
            out.println(message);
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String playerName;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Wprowadź swoje imię:");
                playerName = in.readLine();

                synchronized (clients) {
                    if (clients.containsKey(playerName)) {
                        out.println("Imię już zajęte. Rozłączanie...");
                        return;
                    }
                    clients.put(playerName, out);
                }

                System.out.println(playerName + " dołączył do gry.");
                broadcast("LOBBY_STATE:BIAŁY=" + (whitePlayer == null ? "Brak" : whitePlayer) + ",CZARNY=" + (blackPlayer == null ? "Brak" : blackPlayer));

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Odebrano od " + playerName + ": " + message);

                    if (message.startsWith("WHITE:")) {
                        synchronized (clients) {
                            if (whitePlayer == null) {
                                whitePlayer = playerName;
                                broadcast("LOBBY_STATE:BIAŁY=" + playerName + ",CZARNY=" + (blackPlayer == null ? "Brak" : blackPlayer));
                            } else {
                                out.println("Kolor BIAŁY jest już zajęty.");
                            }
                        }
                    } else if (message.startsWith("BLACK:")) {
                        synchronized (clients) {
                            if (blackPlayer == null) {
                                blackPlayer = playerName;
                                broadcast("LOBBY_STATE:BIAŁY=" + (whitePlayer == null ? "Brak" : whitePlayer) + ",CZARNY=" + playerName);
                            } else {
                                out.println("Kolor CZARNY jest już zajęty.");
                            }
                        }
                    } else if (message.equals("START_GAME")) {
                        synchronized (clients) {
                            if (whitePlayer != null && blackPlayer != null) {
                                broadcast("START_GAME");
                            } else {
                                out.println("Nie można rozpocząć gry – obaj gracze muszą wybrać kolory.");
                            }
                        }
                    }
                    if (message.startsWith("MOVE:")) {
                        synchronized (clients) {
                            broadcast(message); // Przekaż ruch do wszystkich klientów
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (clients) {
                    clients.remove(playerName);
                    if (playerName.equals(whitePlayer)) {
                        whitePlayer = null;
                    } else if (playerName.equals(blackPlayer)) {
                        blackPlayer = null;
                    }
                    broadcast("LOBBY_STATE:BIAŁY=" + (whitePlayer == null ? "Brak" : whitePlayer) + ",CZARNY=" + (blackPlayer == null ? "Brak" : blackPlayer));
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
