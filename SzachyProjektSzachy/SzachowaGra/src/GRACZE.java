import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GRACZE {
    private String imie;
    private String kolor;
    private int punkty;
   // male zmioany
    // Constructor
    public GRACZE(String imie, String kolor, int punkty) {
        this.imie = imie;
        this.kolor = kolor;
        this.punkty = punkty;
    }


    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getKolor() {
        return kolor;
    }

    public void setKolor(String kolor) {
        this.kolor = kolor;
    }

    public int getPunkty() {
        return punkty;
    }

    public void setPunkty(int punkty) {
        this.punkty = punkty;
    }


    public void saveToFile() {
        File file = new File("gracze.txt");

        try (FileWriter writer = new FileWriter(file, true)) {


            writer.write("Imię: " + imie + ", Kolor: " + kolor + ", Punkty: " + punkty + "\n");
            System.out.println("Dane gracza zostały zapisane do pliku.");
        } catch (IOException e) {

            System.out.println("Wystąpił błąd podczas zapisywania do pliku.");
            e.printStackTrace();
        }
    }


    public void wyswietlInformacje() {
        System.out.println("Imię: " + imie);
        System.out.println("Kolor figury: " + kolor);
        System.out.println("Punkty: " + punkty);
    }
}
