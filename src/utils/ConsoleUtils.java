package utils;

import enums.Genre;
import enums.Plateforme;
import models.Media;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ConsoleUtils {

    // Attributs

    private static final Scanner sc = new Scanner(System.in);
    private static final int LARGEUR = 40;


    // Méthodes

    public static int lireEntier(String message) {
        while (true) {
            System.out.println(message);
            String saisie = sc.nextLine().trim();
            try {
                return Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Erreur de saisie, entrez un nombre entier");
            }
        }
    }

    public static int lireEntier(String message, int min, int max) {
        while (true) {
            int valeur = lireEntier(message);
            if (valeur >= min && valeur <= max) {
                return valeur;
            }
            System.out.printf("Entrez un nombre entre %d et %d.%n", min, max);
        }
    }

    public static String lireTexte(String message) {
        while (true) {
            System.out.println(message);
            String saisie = sc.nextLine().trim();
            if (!saisie.isEmpty()) {
                return saisie;
            }
            System.out.println("La saisie ne peut pas être vide.");
        }
    }

    public static boolean lireOuiNon(String message) {
        while (true) {
            System.out.println(message + " (o/n) : ");
            String saisie = sc.nextLine().trim().toLowerCase();
            if (saisie.equals("o")) {
                return true;
            }
            if (saisie.equals("n")) {
                return false;
            }

            System.out.println("Répondez par o ou n.");
        }
    }

    public static void afficherTitre(String titre) {
        String barre = "—".repeat(LARGEUR);
        System.out.println();
        System.out.println("⌈" + barre + "⌉");
        System.out.println("|" + centrer(titre.toUpperCase()) + "|");
        System.out.println("⌊" + barre + "⌋");
    }

    /**
     * Permet de centrer un texte par rapport à la largeur par défaut
     */
    public static String centrer(String texte) {
        return centrer(texte, LARGEUR);
    }

    /**
     * Permet de centrer un texte par rapport à une largeur spécifique
     */
    public static String centrer(String texte, int largeur) {
        if (texte == null || texte.length() >= largeur) {
            return texte;
        }

        int margeGauche = (largeur - texte.length()) / 2;
        int margeDroite = largeur - texte.length() - margeGauche;

        return " ".repeat(margeGauche) + texte + " ".repeat(margeDroite);
    }


    public static Genre lireGenre(String message) {
        System.out.println(message);

        Genre[] valeurs = Genre.values();
        for (int i = 0; i < valeurs.length; i++) {
            System.out.printf(" %d. %s%n", i, valeurs[i]);
        }
        int choix = lireEntier("Votre choix :", 0, valeurs.length - 1);
        return valeurs[choix];
    }

    public static Plateforme lirePlateforme(String message) {
        System.out.println(message);

        Plateforme[] valeurs = Plateforme.values();
        for (int i = 0; i < valeurs.length; i++) {
            System.out.printf(" %d. %s%n", i, valeurs[i]);
        }
        int choix = lireEntier("Votre choix :", 1, valeurs.length);
        return valeurs[choix - 1];
    }

    public static void pause() {
        System.out.println("\nAppuyez sur [Entrée] pour continuer...\n");
        sc.nextLine();
    }

    /**
     * Suspend l'exécution du thread courant pendant une durée spécifiée en secondes.
     * <p>
     * Si le thread est interrompu pendant l'attente, l'exception {@link InterruptedException}
     * est capturée et le statut d'interruption du thread est rétabli via {@link Thread#interrupt()}.
     *
     * @param ms le nombre de secondes à attendre (doit être un entier positif ou nul)
     */
    public static void attendre(int ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void nettoyerConsole() {
        // Séquence ANSI pour terminaux modernes et émulateurs
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // Sauts de ligne pour repousser le texte dans la console Run basique d'IntelliJ
        System.out.println("\n".repeat(40));
    }

    public static void afficherListe(List<?> liste) {
        if (liste == null || liste.isEmpty()) {
            System.out.println("Le liste est vide");
            return;
        }

        for (Object element : liste) {
            System.out.println("  - " + element);
        }
    }
}
