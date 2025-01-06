import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GRACZE {
    private String imie;
    private String kolor;
    private int punkty;

    // Konstruktor przyjmujący tylko imię
    public GRACZE(String imie) {
        this.imie = imie;
        this.kolor = "";  // Kolor ustawiamy później
        this.punkty = 0;   // Punkty ustawiamy później
    }

    // Getter i Setter dla imienia
    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    // Getter i Setter dla koloru
    public String getKolor() {
        return kolor;
    }

    public void setKolor(String kolor) {
        this.kolor = kolor;
    }

    // Getter i Setter dla punktów
    public int getPunkty() {
        return punkty;
    }

    public void setPunkty(int punkty) {
        this.punkty = punkty;
    }

    // Zapisz gracza do pliku (imie, kolor, punkty)
    public void saveToFile() {
        File file = new File("gracze.txt");
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write("Imię: " + imie + ", Kolor: " + (kolor.isEmpty() ? "Nie wybrany" : kolor) + ", Punkty: " + punkty + "\n");
            System.out.println("Dane gracza zostały zapisane do pliku.");
        } catch (IOException e) {
            System.out.println("Wystąpił błąd podczas zapisywania do pliku.");
            e.printStackTrace();
        }
    }

    // Wyświetl informacje o graczu
    public void wyswietlInformacje() {
        System.out.println("Imię: " + imie);
        System.out.println("Kolor figury: " + (kolor.isEmpty() ? "Nie wybrany" : kolor));
        System.out.println("Punkty: " + punkty);
    }
}
