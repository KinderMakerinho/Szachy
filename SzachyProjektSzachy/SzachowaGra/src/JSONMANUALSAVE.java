import java.io.FileWriter;
import java.io.IOException;

public class JSONMANUALSAVE {
    public static void main(String[] args) {
        // Dane do zapisania
        String player1Name = "Jan";
        String player2Name = "Anna";
        String winner = "Jan";
        String[] moves = {"E2-E4", "E7-E5", "G1-F3"};

        // Tworzymy ręcznie strukturę JSON jako String
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"player1Name\": \"").append(player1Name).append("\",\n");
        json.append("  \"player2Name\": \"").append(player2Name).append("\",\n");
        json.append("  \"winner\": \"").append(winner).append("\",\n");
        json.append("  \"moves\": [\n");
        for (int i = 0; i < moves.length; i++) {
            json.append("    \"").append(moves[i]).append("\"");
            if (i < moves.length - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ]\n");
        json.append("}");

        // Zapisujemy do pliku
        try (FileWriter file = new FileWriter("C:\\Users\\karol\\Desktop\\Szachy\\SzachyProjektSzachy\\SzachowaGra\\src\\zapisywaniedancyh\\game_results.json")) {
            file.write(json.toString());
            System.out.println("Wynik gry zapisany do game_results.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
