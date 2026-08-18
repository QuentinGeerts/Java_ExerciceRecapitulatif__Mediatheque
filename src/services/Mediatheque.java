package services;

import configs.Config;
import enums.Genre;
import models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mediatheque {

    // Attributs

    private final String nom;
    private final Map<Integer, Media> catalogue = new HashMap<>();
    private final Map<Integer, Membre> membres = new HashMap<>();
    private final List<Emprunt> emprunts = new ArrayList<>();

    // Constructeur

    public Mediatheque(String nom) {
        if (nom == null || nom.isBlank())
            throw new IllegalArgumentException("Le nom de la médiathèque ne peut pas être null ou vide.");
        this.nom = nom.trim();
    }

    // Getters

    public String getNom() {
        return nom;
    }


    // B.1 - Méthodes - CREATE

    public boolean ajouterMedia(Media media) {
        if (media == null) return false;
        if (catalogue.containsKey(media.getId())) return false;
        catalogue.put(media.getId(), media);
        return true;
    }

    public void ajouterMedias(Media... medias) {
        if (medias == null) return;
        for (Media media : medias) {
            ajouterMedia(media);
        }
    }

    public boolean inscrireMembre(Membre membre) {
        if (membre == null) return false;
        if (emailDejaInscrit(membre.getEmail())) return false;

        membres.put(membre.getId(), membre);
        return true;
    }

    private boolean emailDejaInscrit(String email) {
        for (Membre membre : membres.values()) {
            if (membre.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }


    // B.2 - Méthodes - READ

    public Media rechercherMediaParId(int id) {
        return catalogue.get(id);
    }

    public List<Media> listerTous() {
        return new ArrayList<>(catalogue.values());
    }

    public List<Media> rechercher(String motCle) {
        List<Media> resultats = new ArrayList<>();

        if (motCle == null || motCle.isBlank()) return resultats;

        String recherche = motCle.trim().toLowerCase();

        for (Media media : catalogue.values()) {
            if (media.getTitre().toLowerCase().contains(recherche)) {
                resultats.add(media);
            }
        }
        return resultats;
    }

    public List<Media> rechercher(Genre genre) {
        List<Media> resultats = new ArrayList<>();

        if (genre == null) return resultats;

        for (Media media : catalogue.values()) {
            if (media.getGenre() == genre) {
                resultats.add(media);
            }
        }

        return resultats;
    }

    public List<Media> listerDisponible() {
        List<Media> resultats = new ArrayList<>();

        for (Media media : catalogue.values()) {
            if (media.isDisponible()) {
                resultats.add(media);
            }
        }

        return resultats;
    }

    // Pour lister par type actuellement, voici la façon de faire au plus simple
    // Une méthode de chaque type
    // Optimisation → Fournir le type en paramètre qui servira à la place de la classe

    public List<Media> listerLivres() {
        List<Media> resultats = new ArrayList<>();
        for (Media media : catalogue.values()) {
            if (media instanceof Livre) {
                resultats.add(media);
            }
        }
        return resultats;
    }

    public List<Media> listerDvd() {
        List<Media> resultats = new ArrayList<>();
        for (Media media : catalogue.values()) {
            if (media instanceof Dvd) {
                resultats.add(media);
            }
        }
        return resultats;
    }

    public List<Media> listerJeuxVideo() {
        List<Media> resultats = new ArrayList<>();
        for (Media media : catalogue.values()) {
            if (media instanceof JeuVideo) {
                resultats.add(media);
            }
        }
        return resultats;
    }

    // Méthode générique (la notion sera vue plus tard)
    // Permet d'alléger le code ;)

    public <T> List<Media> listerParType(Class<T> type) {
        List<Media> resultats = new ArrayList<>();
        for (Media media : catalogue.values()) {
            if (type.isInstance(media)) {
                resultats.add(media);
            }
        }
        return resultats;
    }

    // B.3 - Méthodes - UPDATE

    public boolean modifierMedia(int id, String nouveauTitre, int nouvelleAnnee, Genre nouveauGenre) {
        Media media = catalogue.get(id);
        if (media == null) return false;

        media.setTitre(nouveauTitre);
        media.setAnneeSortie(nouvelleAnnee);
        media.setGenre(nouveauGenre);

        return true;
    }

    // B.4 - Méthodes - DELETE

    public boolean supprimerMedia(int id) {
        Media media = catalogue.get(id);

        if (media == null) return false;
        if (!media.isDisponible()) return false; // Suppression interdite en cas d'emprunt en cours

        catalogue.remove(id);
        return true;
    }

    // C.1 - Emprunter
    public boolean emprunter(int idMedia, int idMembre) {
        Media media = catalogue.get(idMedia);
        if (media == null) return false;

        Membre membre = membres.get(idMembre);
        if (membre == null) return false;

        if (!media.isDisponible()) return false;

        if (empruntsEnCours(membre).size() >= Config.MAX_EMPRUNTS_PAR_MEMBRE) return false;

        emprunts.add(new Emprunt(media, membre));
        return true;
    }

    // C.2 - Rendre

    public double rendre(int idMedia) {
        Emprunt emprunt = emprunts.get(idMedia);
        if (emprunt == null) return -1;
        return emprunt.cloturer();
    }

    // C.3 - Consulter et statistiques

    public List<Emprunt> empruntsEnCours() {
        List<Emprunt> resultats = new ArrayList<>();
        for (Emprunt emprunt : emprunts) {
            if (emprunt.estEnCours()) {
                resultats.add(emprunt);
            }
        }
        return resultats;
    }

    public List<Emprunt> empruntsEnCours(Membre membre) {
        List<Emprunt> resultats = new ArrayList<>();
        if (membre == null) return resultats;
        for (Emprunt emprunt : empruntsEnCours()) {
            if (membre.equals(emprunt.getMembre())) resultats.add(emprunt);
        }
        return resultats;
    }

    public List<Emprunt> listerRetards() {
        List<Emprunt> resultats = new ArrayList<>();
        for (Emprunt emprunt : emprunts) {
            if (emprunt.estEnRetard()) {
                resultats.add(emprunt);
            }
        }
        return resultats;
    }


    public Emprunt dernierEmprunt() {
        if (emprunts.isEmpty()) return null;
        return emprunts.getLast();
    }

    public Membre rechercherMembreParId(int idMembre) {
        return membres.get(idMembre);
    }

    // <!> dans l'énoncé, il est indiqué que Mediatheque ne peut pas utiliser System.out.println (Partie D - contraintes)
    public Statistiques calculerStatistiques() {
        return new Statistiques(this);
    }
}
