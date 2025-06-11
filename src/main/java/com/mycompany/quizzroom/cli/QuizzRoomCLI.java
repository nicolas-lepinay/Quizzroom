package com.mycompany.quizzroom.cli;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.quizzroom.game.Game;
import com.mycompany.quizzroom.model.Buzzer;
import com.mycompany.quizzroom.model.Question;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class QuizzRoomCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();

        // Charger les questions au démarrage
        loadQuestionsIntoGame(game);

        while (true) {
            System.out.println("\n☰ Bienvenue");
            System.out.println("[1] Ajouter des buzzers.");
            System.out.println("[2] Lister les buzzers.");
            System.out.println("[3] Démarrer la partie.");
            System.out.println("[4] Exit");
            System.out.print("› ");

            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("❗ Veuillez entrer un nombre.");
                scanner.next(); // Nettoyer le buffer
                continue;
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createBuzzers(game, scanner);
                    break;
                case 2:
                    displayBuzzers(game);
                    break;
                case 3:
                    startGame(game, scanner);
                    break;
                case 4:
                    System.out.println("À bientôt !");
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

    private static void loadQuestionsIntoGame(Game game) {
        try (InputStream is = QuizzRoomCLI.class.getResourceAsStream("/questions.json")) {
            if (is == null) {
                System.out.println("❗ Erreur: Fichier questions.json introuvable.");
                return;
            }
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type questionListType = new TypeToken<ArrayList<Question>>(){}.getType();
            List<Question> questions = new Gson().fromJson(reader, questionListType);
            game.loadQuestions(questions);
            System.out.println("✔\uFE0F " + questions.size() + " questions chargées.");
        } catch (Exception e) {
            System.out.println("❗ Erreur lors du chargement des questions: " + e.getMessage());
        }
    }

    public static void sleep(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void displayScores(Game game) {
        System.out.println("\n📊 Tableau des scores :");
        for (Buzzer buzzer : game.getBuzzers()) {
            System.out.printf("Buzzer #%d : %d point(s)\n", buzzer.getId(), game.getScores().get(buzzer.getId()));
            sleep(1000);
        }
    }
    private static Buzzer determineWinner(List<Buzzer> buzzingBuzzers) {
        if (buzzingBuzzers.isEmpty()) return null;
        if (buzzingBuzzers.size() == 1) return buzzingBuzzers.get(0);

        Buzzer winner = null;
        int bestTime = Integer.MAX_VALUE;
        Random rand = new Random();

        System.out.println("... Calcul du buzzer le plus rapide ...");
        for (Buzzer buzzer : buzzingBuzzers) {
            // Le temps de réaction est un nombre aléatoire entre 1 et le max défini (ex: 10ms)
            int actualReactionTime = 1 + rand.nextInt(buzzer.getReactionTime());
            System.out.printf("  - Buzzer #%d a buzzé en %d ms\n", buzzer.getId(), actualReactionTime);
            if (actualReactionTime < bestTime) {
                bestTime = actualReactionTime;
                winner = buzzer;
            }
        }
        return winner;
    }

    private static String getAnswerWithinTime(Scanner scanner, int timeoutSeconds) {
        // Crée un service qui exécutera notre tâche dans un autre thread.
        ExecutorService executor = Executors.newSingleThreadExecutor();

        System.out.printf("Vous avez %d secondes pour répondre... Top chrono !\n› ", timeoutSeconds);

        // On définit la tâche à exécuter : lire la prochaine ligne du scanner.
        Callable<String> readLineTask = scanner::nextLine;

        // On soumet la tâche à l'exécuteur, ce qui nous renvoie un "Future".
        Future<String> future = executor.submit(readLineTask);

        try {
            // On essaie d'obtenir le résultat du Future, mais on n'attend pas plus longtemps que le timeout spécifié.
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // Si le temps est écoulé, une TimeoutException est levée.
            System.out.println("\n⌛ Temps écoulé !");
            future.cancel(true); // On annule la tâche en arrière-plan.
            return null;
        } catch (Exception e) {
            // Gérer d'autres exceptions possibles (InterruptedException, ExecutionException)
            e.printStackTrace();
            future.cancel(true);
            return null;
        } finally {
            // Très important : toujours arrêter l'exécuteur pour libérer les ressources.
            executor.shutdownNow();
        }
    }

    public static void startGame(Game game, Scanner scanner) {
        if (game.getBuzzers().size() < 2) {
            System.out.println("❗ Le nombre de buzzers est insuffisant.");
            return;
        }
        if (game.getQuestions().isEmpty()) {
            System.out.println("❗ Aucune question n'a été chargée. Impossible de démarrer.");
            return;
        }

        displayScores(game);

        System.out.println("\nLa partie commence dans...");

        // Boucle sur chaque question
        for (Question question : game.getQuestions()) {
            game.resetBuzzers();
            boolean questionAnswered = false;

            for (int i = 3; i > 0; i--) {
                System.out.println(i + "...");
                sleep(1000);
            }

            System.out.println("\n" + "─".repeat(50));
            System.out.println("❓ Question : " + question.question());
            //System.out.println("❓ Réponse : " + question.answer());
            System.out.println("─".repeat(50));

            // Boucle de tentative pour la question actuelle
            while (!questionAnswered) {
                System.out.print("\nEntrez les IDs des buzzers : ");
                String[] buzzedIdsStr = scanner.nextLine().split(" ");
                List<Buzzer> potentialPlayers = new ArrayList<>();

                for (String idStr : buzzedIdsStr) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        Buzzer b = game.getBuzzerById(id);
                        if (b != null && b.canBuzz()) {
                            potentialPlayers.add(b);
                        }
                    } catch (NumberFormatException ignored) {}
                }

                if (potentialPlayers.isEmpty()) {
                    System.out.println("Aucun buzzer valide n'a buzzé. Réessayez.");
                    continue;
                }

                // Déterminer le gagnant du buzz
                Buzzer winner = determineWinner(potentialPlayers);
                System.out.printf("\n⚡ Le Buzzer #%d a été le plus rapide ! Il a la main.\n", winner.getId());

                String answer = getAnswerWithinTime(scanner, 10);
                // Vérification de la réponse
                // Si le temps est écoulé, 'answer' sera null.
                if (answer != null && answer.equalsIgnoreCase(question.answer())) {
                    System.out.println("\n✅ Bonne réponse ! +1 point.");
                    game.incrementScore(winner.getId());
                    questionAnswered = true; // On passe à la question suivante
                    displayScores(game);
                } else {
                    // Si la réponse est non-null mais fausse
                    if (answer != null) {
                        System.out.println("\n❌ Mauvaise réponse !");
                    } else {
                        scanner = new Scanner(System.in); // Timeout -> nouveau scanner
                    }
                    // Quoi qu'il arrive (réponse fausse ou temps écoulé), le joueur perd la main.
                    System.out.println("Ce joueur ne peut plus répondre à cette question.");
                    winner.setCanBuzz(false); // Ce joueur est bloqué pour cette question

                    // Vérifier s'il reste des joueurs pouvant répondre
                    boolean canAnyoneElseBuzz = false;
                    for (Buzzer b : game.getBuzzers()) {
                        if (b.canBuzz()) {
                            canAnyoneElseBuzz = true;
                            break;
                        }
                    }
                    if (!canAnyoneElseBuzz) {
                        System.out.println("Personne n'a trouvé la bonne réponse. La réponse était : " + question.answer());
                        questionAnswered = true; // On passe à la question suivante
                    } else {
                        System.out.println("Les autres joueurs peuvent maintenant buzzer.");
                    }
                }
            }
        }
        System.out.println("\n🎉 Fin de la partie ! 🎉");
        displayScores(game);
    }

}


