package com.mycompany.quizzroom.cli;

import com.mycompany.quizzroom.game.Game;
import com.mycompany.quizzroom.model.Buzzer;
import java.util.*;

public class QuizzRoomCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();

        while (true) {
            System.out.println("\n☰ Bienvenue");
            System.out.println("[1] Ajouter des buzzers.");
            System.out.println("[2] Lister les buzzers.");
            System.out.println("[3] Exit");
            System.out.print("› ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createBuzzers(game, scanner);
                    break;
                case 2:
                    displayBuzzers(game);
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    public static void createBuzzers(Game game, Scanner scanner) {
        System.out.print("Nombre de buzzers : ");
        int n = scanner.nextInt();

        System.out.print("Temps de réactivité (ms): ");
        int reactionTime = scanner.nextInt();

        int currentMaxId = game.getBuzzers().size();

        for (int i = 1; i <= n; i++) {
            int id = currentMaxId + i; // Unique auto-increment ID
            Buzzer buzzer = new Buzzer(id, reactionTime);
            game.addBuzzer(buzzer);
        }
        System.out.println("✔\uFE0F " + n + " buzzers ont été créés.");
    }

    public static void displayBuzzers(Game game) {
        if (game.getBuzzers().isEmpty()) {
            System.out.println("❗ Aucun buzzer n'a été créé.");
        } else {
            System.out.println("\uD83D\uDECE\uFE0F Liste des buzzers :");
            for (Buzzer buzzer : game.getBuzzers()) {
                System.out.println(buzzer);
            }
        }
    }
}


