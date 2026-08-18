package models;

import enums.Genre;
import enums.Plateforme;

import java.util.Arrays;

public class JeuVideo extends Media {

    private static final int[] PEGI_AUTORISES = {3, 7, 12, 16, 18};

    // Attributs

    private Plateforme plateforme;
    private int pegi;

    // Constructeurs

    public JeuVideo(String titre, int anneeSortie, Genre genre, Plateforme plateforme, int pegi) {
        super(titre, anneeSortie, genre);
        setPlateforme(plateforme);
        setPegi(pegi);
    }


    // Getters / Setters

    public Plateforme getPlateforme() {
        return plateforme;
    }

    public void setPlateforme(Plateforme plateforme) {
        if (plateforme == null) throw new IllegalArgumentException("La plateforme ne peut pas être null.");
        this.plateforme = plateforme;
    }

    public int getPegi() {
        return pegi;
    }

    public void setPegi(int pegi) {
        boolean valide = Arrays.stream(PEGI_AUTORISES).anyMatch(value -> value == pegi);
        if (!valide) throw new IllegalArgumentException("PEGI invalide: valeurs autorisées " + Arrays.toString(PEGI_AUTORISES));
        this.pegi = pegi;
    }

    // Méthodes à redéfinir
    @Override
    public int dureeEmpruntJours() {
        return 14;
    }

    @Override
    public String typeLibelle() {
        return "Jeu vidéo";
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(" | %-7s, PEGI %2d", plateforme, pegi);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
