import configs.Config;
import enums.Genre;
import enums.Plateforme;
import models.*;
import services.Mediatheque;
import utils.ConsoleUtils;

import java.util.List;

public class Main {
    static void main() {

        ConsoleUtils.afficherTitre("Démarrage de l'application...");
        ConsoleUtils.attendre(1500);

        Mediatheque mediatheque = new Mediatheque(Config.NOM_MEDIATHEQUE);
        ConsoleUtils.afficherTitre("Initialisation des données...");
        ConsoleUtils.attendre(1000);
        chargerDonneesDemo(mediatheque);

        ConsoleUtils.pause();

        boolean continuer = true;

        while (continuer) {
            ConsoleUtils.nettoyerConsole();
            afficherMenu();
            int choix = ConsoleUtils.lireEntier("Votre choix :");

            ConsoleUtils.nettoyerConsole();

            switch (choix) {
                case 1 -> ajouterMedia(mediatheque);
                case 2 -> listerCatalogue(mediatheque);
                case 3 -> rechercherMedia(mediatheque);
                case 4 -> modifierMedia(mediatheque);
                case 5 -> supprimerMedia(mediatheque);
                case 6 -> inscrireMembre(mediatheque);
                case 7 -> emprunter(mediatheque);
                case 8 -> rendre(mediatheque);
                case 9 -> afficherEmpruntsEnCours(mediatheque);
                case 10 -> afficherStatistiques(mediatheque);
                case 0 -> continuer = false;

                default -> System.out.println("Option invalide");
            }

            if (choix != 0) ConsoleUtils.pause();
        }

        System.out.println("Merci d'avoir visité la médiathèque " + mediatheque.getNom() + " !");
    }

    // Menu

    private static void afficherMenu() {
        System.out.println("————————————————————————————————————————");
        System.out.println(ConsoleUtils.centrer("MÉDIATHÈQUE " + Config.NOM_MEDIATHEQUE.toUpperCase()));
        System.out.println("————————————————————————————————————————");
        System.out.println("1. Ajouter un média");
        System.out.println("2. Lister le catalogue");
        System.out.println("3. Rechercher un média");
        System.out.println("4. Modifier un média");
        System.out.println("5. Supprimer un média");
        System.out.println("————————————————————————————————————————");
        System.out.println("6. Inscrire un membre");
        System.out.println("7. Emprunter");
        System.out.println("8. Rendre");
        System.out.println("9. Emprunts en cours");
        System.out.println("————————————————————————————————————————");
        System.out.println("10. Statistiques");
        System.out.println("0. Quitter");
        System.out.println("————————————————————————————————————————");
    }

    // Méthodes du menu

    private static void ajouterMedia(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Ajouter un média");

        System.out.println("Type de média :");
        System.out.println(" 1. Livre");
        System.out.println(" 2. DVD");
        System.out.println(" 3. Jeu vidéo");
        int type = ConsoleUtils.lireEntier("Votre choix : ", 1, 3);

        String titre = ConsoleUtils.lireTexte("Titre :");
        int annee = ConsoleUtils.lireEntier("Année de sortie :");
        Genre genre = ConsoleUtils.lireGenre("Genre :");

        try {

            Media media = switch (type) {
                case 1 ->
                        new Livre(titre, annee, genre, ConsoleUtils.lireTexte("Auteur :"), ConsoleUtils.lireEntier("Nombre de pages :"), ConsoleUtils.lireTexte("ISBN :"));
                case 2 ->
                        new Dvd(titre, annee, genre, ConsoleUtils.lireTexte("Réalisateur :"), ConsoleUtils.lireEntier("Durée (en minutes) :"));
                default ->
                        new JeuVideo(titre, annee, genre, ConsoleUtils.lirePlateforme("Plateforme :"), ConsoleUtils.lireEntier("PEGI (3, 7, 12, 16 ou 18) :"));
            };

            if (mediatheque.ajouterMedia(media)) {
                System.out.println("OK - Média ajouté : " + media);
            } else {
                System.out.println("KO - Le média n'a pas été ajouté.");
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Refus: " + e.getMessage());
        }
    }

    private static void listerCatalogue(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Catalogue");
        List<Media> catalogue = mediatheque.listerTous();
        System.out.printf("%nIl y a %d média(s) dans le catalogue.%n", catalogue.size());
        ConsoleUtils.afficherListe(catalogue);
    }

    private static void rechercherMedia(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Rechercher");

        System.out.println("  1. Par mot-clé dans le titre");
        System.out.println("  2. Par genre");
        System.out.println("  3. Uniquement les disponibles");

        int mode = ConsoleUtils.lireEntier("Votre choix :", 1, 3);

        List<Media> resultats = switch (mode) {
            case 1 -> mediatheque.rechercher(ConsoleUtils.lireTexte("Mot-clef : "));
            case 2 -> mediatheque.rechercher(ConsoleUtils.lireGenre("Genre : "));
            default -> mediatheque.listerDisponible();
        };

        System.out.println("\n" + resultats.size() + "résultat(s) trouvé(s).\n");
        ConsoleUtils.afficherListe(resultats);
    }

    private static void modifierMedia(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Modifier un média");

        int id = ConsoleUtils.lireEntier("Id : ");
        Media media = mediatheque.rechercherMediaParId(id);

        if (media == null) {
            System.out.println("KO - Aucun média avec l'id " + id);
            return;
        }
        System.out.println("Média sélectionné : " + media);

        String titre = ConsoleUtils.lireTexte("Nouveau titre :");
        int annee = ConsoleUtils.lireEntier("Nouvelle année :");
        Genre genre = ConsoleUtils.lireGenre("Nouveau genre : ");

        try {
            if (mediatheque.modifierMedia(id, titre, annee, genre)) {
                System.out.println("OK - Modifier de media : " + media);
            } else {
                System.out.println("KO - Modification impossible");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("KO - Erreur lors de la modification");
        }
    }

    private static void supprimerMedia(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Suppression");

        int id = ConsoleUtils.lireEntier("Id : ");
        Media media = mediatheque.rechercherMediaParId(id);

        if (media == null) {
            System.out.println("KO - Aucun média avec l'id " + id);
            return;
        }

        System.out.println("Média sélectionné : " + media);

        if (!ConsoleUtils.lireOuiNon("Confirmation de la suppression")) {
            System.out.println("Suppression annulée");
        } else {
            if (mediatheque.supprimerMedia(id)) {
                System.out.println("OK - Média supprimé");
            } else if (!media.isDisponible()) {
                System.out.println("KO - Ce média est actuellement emprunté");
            } else {
                System.out.println("KO - Suppression impossible");
            }
        }

    }

    private static void inscrireMembre(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Inscrire un membre");
        try {
            Membre membre = new Membre(ConsoleUtils.lireTexte("Nom : "), ConsoleUtils.lireTexte("Prenom : "), ConsoleUtils.lireTexte("Email : "));

            if (mediatheque.inscrireMembre(membre)) {
                System.out.println("OK - Membre inscrit : " + membre);
            } else {
                System.out.println("REFUS - Cet email est deja inscrit.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("REFUS - " + e.getMessage());
        }
    }

    private static void emprunter(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Emprunter");
        int idMedia = ConsoleUtils.lireEntier("ID du media : ");
        int idMembre = ConsoleUtils.lireEntier("ID du membre : ");

        if (mediatheque.emprunter(idMedia, idMembre)) {
            Emprunt emprunt = mediatheque.dernierEmprunt();
            System.out.printf("OK - \"%s\" emprunté par %s. Retour prevu le %s.%n", emprunt.getMedia().getTitre(), emprunt.getMembre().nomComplet(), emprunt.getDateRetourPrevue());
            return;
        }

        Media media = mediatheque.rechercherMediaParId(idMedia);
        Membre membre = mediatheque.rechercherMembreParId(idMembre);

        if (media == null) {
            System.out.println("KO - Aucun média avec cet id.");
        } else if (membre == null) {
            System.out.println("KO - Aucun membre avec cet id.");
        } else if (!media.isDisponible()) {
            System.out.println("KO - Ce média est déjà emprunté.");
        } else {
            System.out.printf("KO - Ce membre a déjà %d emprunts en cours.%n", Config.MAX_EMPRUNTS_PAR_MEMBRE);
        }
    }

    private static void rendre(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Rendre");
        int idMedia = ConsoleUtils.lireEntier("Id du média : ");

        double retard = mediatheque.rendre(idMedia);

        if (retard < 0) {
            System.out.println("KO - Aucun emprunt en cours pour ce media.");
        } else if (retard == 0.0) {
            System.out.println("Retour enregistré. Aucun retard.");
        } else {
            System.out.printf("Retour enregistré. Retard : %d jours.%n", (long) retard);
        }
    }

    private static void afficherEmpruntsEnCours(Mediatheque mediatheque) {
        ConsoleUtils.afficherTitre("Emprunts en cours");
        if (ConsoleUtils.lireOuiNon("Filtrer par membre ?")) {
            int idMembre = ConsoleUtils.lireEntier("Id du membre : ");
            Membre membre = mediatheque.rechercherMembreParId(idMembre);
            if (membre == null) {
                System.out.println("KO - Aucun membre avec cet id.");
                return;
            }
            System.out.println("\nEmprunts de " + membre.nomComplet() + " :");
            ConsoleUtils.afficherListe(mediatheque.empruntsEnCours(membre));
        } else {
            ConsoleUtils.afficherListe(mediatheque.empruntsEnCours());
        }
    }

    private static void afficherStatistiques(Mediatheque mediatheque) {
        Statistiques stats = mediatheque.calculerStatistiques();

        System.out.println();
        System.out.println("Statistiques - " + stats.getNomMediatheque());
        System.out.printf("Medias au catalogue : %d (%d livres, %d DVD, %d jeux video)%n", stats.getTotalMedias(), stats.getNbLivres(), stats.getNbDvd(), stats.getNbJeux());
        System.out.printf("Disponibles         : %d%n", stats.getNbDisponibles());
        System.out.printf("Empruntes           : %d%n", stats.getNbEmpruntes());
        System.out.printf("Membres inscrits    : %d%n", stats.getNbMembres());
        if (stats.getGenreDominant() != null) {
            System.out.printf("Genre le plus represente : %s (%d medias)%n", stats.getGenreDominant(), stats.getNbGenreDominant());
        }
    }


    // Méthodes supplémentaires

    private static void chargerDonneesDemo(Mediatheque mediatheque) {

        // Insertion des médias

        mediatheque.ajouterMedias(
                new Livre("L'Épée de vérité : La Première Leçon du Sorcier", 2003, Genre.FANTASY, "Terry Goodkind", 648, "978-2914370332"),
                new Livre("Dune", 1965, Genre.SCIENCE_FICTION, "Frank Herbert", 412, "978-0441172719"),
                new Livre("Le Guide du voyageur galactique", 1979, Genre.SCIENCE_FICTION, "Douglas Adams", 224, "978-0345391803"),
                new Livre("Vingt mille lieues sous les mers", 1870, Genre.AVENTURE, "Jules Verne", 624, "978-2253006329"),
                new Livre("Jurassic Park", 1990, Genre.AVENTURE, "Michael Crichton", 448, "978-0345538987"),
                new Livre("L'Île au trésor", 1883, Genre.AVENTURE, "Robert Louis Stevenson", 288, "978-2253007135"),
                new Livre("Une étude en rouge", 1887, Genre.POLICIER, "Arthur Conan Doyle", 176, "978-2253005278"),
                new Livre("Harry Potter à l'école des sorciers", 1997, Genre.JEUNESSE, "J.K. Rowling", 320, "978-2070584628"),
                new Livre("Le Petit Prince", 1943, Genre.JEUNESSE, "Antoine de Saint-Exupéry", 96, "978-2070612758"),
                new Livre("Battle Royale", 1999, Genre.ACTION, "Koushun Takami", 624, "978-2253122340"),
                new Livre("La Mémoire dans la peau", 1980, Genre.ACTION, "Robert Ludlum", 544, "978-2253170426"),
                new Livre("Masters of Doom", 2003, Genre.DOCUMENTAIRE, "David Kushner", 368, "978-0812971378"),
                new Livre("Une brève histoire du temps", 1988, Genre.DOCUMENTAIRE, "Stephen Hawking", 236, "978-2081512801"),
                new Livre("L'Histoire de Nintendo, Vol. 1", 2008, Genre.DOCUMENTAIRE, "Florent Gorges", 240, "978-2953050110"),

                new Dvd("Matrix", 1999, Genre.SCIENCE_FICTION, "Lana & Lilly Wachowski", 136),
                new Dvd("Le Seigneur des Anneaux : Le Retour du Roi", 2003, Genre.FANTASY, "Peter Jackson", 201),
                new Dvd("Interstellar", 2014, Genre.SCIENCE_FICTION, "Christopher Nolan", 169),
                new Dvd("Blade Runner", 1982, Genre.SCIENCE_FICTION, "Ridley Scott", 117),
                new Dvd("Retour vers le futur", 1985, Genre.SCIENCE_FICTION, "Robert Zemeckis", 116),

                new JeuVideo("The Witcher 3: Wild Hunt", 2015, Genre.FANTASY, Plateforme.PC, 18),
                new JeuVideo("World of Warcraft", 2004, Genre.FANTASY, Plateforme.PC, 12),
                new JeuVideo("Diablo IV", 2023, Genre.FANTASY, Plateforme.PC, 18),
                new JeuVideo("The Legend of Zelda: Breath of the Wild", 2017, Genre.FANTASY, Plateforme.SWITCH, 12),
                new JeuVideo("Cyberpunk 2077", 2020, Genre.SCIENCE_FICTION, Plateforme.PC, 18),
                new JeuVideo("Elden Ring", 2022, Genre.FANTASY, Plateforme.PS5, 16),
                new JeuVideo("Halo Infinite", 2021, Genre.SCIENCE_FICTION, Plateforme.XBOX, 16),
                new JeuVideo("Portal 2", 2011, Genre.SCIENCE_FICTION, Plateforme.PC, 12));

        // Inscription des membres

        mediatheque.inscrireMembre(new Membre("Geerts", "Quentin", "quentin.geerts@bstorm.be"));
        mediatheque.inscrireMembre(new Membre("Bya", "Sébastien", "sebastien.bya@cognitic.be"));
        mediatheque.inscrireMembre(new Membre("Stons", "Philippe", "philippe.stons@cognitic.be"));
        mediatheque.inscrireMembre(new Membre("Ovyn", "Flavian", "flavian.ovyn@bstorm.be"));
        mediatheque.inscrireMembre(new Membre("Strimelle", "Aurélien", "aurelien.strimelle@cognitic.be"));

        // Création d'emprunts
        mediatheque.emprunter(2, 1);   // Quentin (1) emprunte "Dune" (Livre - ID 2)
        mediatheque.emprunter(15, 3);  // Philippe (3) emprunte "Matrix" (DVD - ID 15)
        mediatheque.emprunter(20, 2);  // Sébastien (2) emprunte "The Witcher 3" (Jeu - ID 20)
        mediatheque.emprunter(23, 4);  // Flavian (4) emprunte "Zelda: BotW" (Jeu - ID 23)
        mediatheque.emprunter(5, 5);   // Aurélien (5) emprunte "Jurassic Park" (Livre - ID 5)
        mediatheque.emprunter(17, 1);  // Quentin (1) emprunte "Interstellar" (DVD - ID 17)
        mediatheque.emprunter(24, 2);  // Sébastien (2) emprunte "Cyberpunk 2077" (Jeu - ID 24))

        System.out.println("Importation réussie !");
    }

}
