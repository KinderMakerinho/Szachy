import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        runGame(args);
    }
 //male zmiany
    public static void runGame(String[] args) {
        Scanner scanner = new Scanner(System.in);


        System.out.print("Podaj imię Gracza 1: ");
        String imie1 = scanner.nextLine();
        System.out.print("Podaj kolor figury Gracza 1 (biały/czarny): ");
        String kolor1 = scanner.nextLine();
        System.out.print("Podaj punkty Gracza 1: ");
        int punkty1 = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Podaj imię Gracza 2: ");
        String imie2 = scanner.nextLine();
        System.out.print("Podaj kolor figury Gracza 2 (biały/czarny): ");
        String kolor2 = scanner.nextLine();
        System.out.print("Podaj punkty Gracza 2: ");
        int punkty2 = scanner.nextInt();


        GRACZE gracz1 = new GRACZE(imie1, kolor1, punkty1);
        GRACZE gracz2 = new GRACZE(imie2, kolor2, punkty2);

        gracz1.saveToFile();
        gracz2.saveToFile();

        gracz1.wyswietlInformacje();
        gracz2.wyswietlInformacje();

        System.out.println("Dane graczy zostały zapisane.");

        Board.launch(args, gracz1, gracz2);
    }
}