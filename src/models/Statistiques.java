package models;

import enums.Genre;
import services.Mediatheque;

import java.util.HashMap;
import java.util.Map;

public class Statistiques {
    private final String nomMediatheque;
    private final int totalMedias;
    private final int nbLivres;
    private final int nbDvd;
    private final int nbJeux;
    private final int nbDisponibles;
    private final int nbEmpruntes;
    private final int nbMembres;
    private final Genre genreDominant;
    private final int nbGenreDominant;

    public Statistiques(Mediatheque mediatheque) {
        nomMediatheque = mediatheque.getNom();
        totalMedias = mediatheque.listerTous().size();
        nbMembres = mediatheque.listerTous().size();

        int livres = 0;
        int dvd = 0;
        int jeux = 0;
        int disponibles = 0;
        Map<Genre, Integer> parGenre = new HashMap<>();

        for (Media media : mediatheque.listerTous()) {
            if (media instanceof Livre) livres++;
            else if (media instanceof Dvd) dvd++;
            else if (media instanceof JeuVideo) jeux++;

            if (media.isDisponible()) disponibles++;

            // Combien par genre ? 0 si aucun puis on ajoute 1
            Genre g = media.getGenre();
            parGenre.put(g, parGenre.getOrDefault(g, 0) + 1);
        }

        nbLivres = livres;
        nbDvd = dvd;
        nbJeux = jeux;
        nbDisponibles = mediatheque.listerDisponible().size();
        nbEmpruntes = totalMedias - nbDisponibles;


        // Dominant
        Genre dominant = null;
        int meilleur = 0;

        for (Genre g : parGenre.keySet()) {
            int combien = parGenre.get(g);
            if (combien > meilleur) {
                dominant = g;
                meilleur = combien;
            }
        }

        genreDominant = dominant;
        nbGenreDominant = meilleur;

    }

    // Getters


    public String getNomMediatheque() {
        return nomMediatheque;
    }

    public int getTotalMedias() {
        return totalMedias;
    }

    public int getNbLivres() {
        return nbLivres;
    }

    public int getNbDvd() {
        return nbDvd;
    }

    public int getNbJeux() {
        return nbJeux;
    }

    public int getNbDisponibles() {
        return nbDisponibles;
    }

    public int getNbEmpruntes() {
        return nbEmpruntes;
    }

    public int getNbMembres() {
        return nbMembres;
    }

    public Genre getGenreDominant() {
        return genreDominant;
    }

    public int getNbGenreDominant() {
        return nbGenreDominant;
    }
}
