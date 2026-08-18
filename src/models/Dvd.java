package models;

import enums.Genre;

public class Dvd extends Media {

    // Attributs
    private String realisateur;
    private int dureeMinutes;

    // Constructeurs
    public Dvd(String titre, int anneeSortie, Genre genre, String realisateur, int dureeMinutes) {
        super(titre, anneeSortie, genre);
        setRealisateur(realisateur);
        setDureeMinutes(dureeMinutes);
    }


    // Getters / Setters


    public String getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(String realisateur) {
        this.realisateur = realisateur;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        if (dureeMinutes <= 0) throw new IllegalArgumentException("La durée ne peut pas être négative ou zéro.");
        this.dureeMinutes = dureeMinutes;
    }

    // Méthodes
    @Override
    public int dureeEmpruntJours() {
        return 7;
    }

    @Override
    public String typeLibelle() {
        return "DVD";
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | de %-30s, %4d min", realisateur, dureeMinutes);
    }
}
