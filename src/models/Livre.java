package models;

import enums.Genre;

public class Livre extends Media {

    // Attributs
    private String auteur;
    private int nbPages;
    private String isbn;

    // Constructeurs
    public Livre(String titre, int anneeSortie, Genre genre, String auteur, int nbPages, String isbn) {
        super(titre, anneeSortie, genre);
        setAuteur(auteur);
        setNbPages(nbPages);
        setIsbn(isbn);
    }

    // Getters / Setters

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        if (nbPages <= 0) throw new IllegalArgumentException("Le nombre de page ne peut pas être négatif.");
        this.nbPages = nbPages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Méthodes à redéfinir du parent
    @Override
    public int dureeEmpruntJours() {
        return 21;
    }

    @Override
    public String typeLibelle() {
        return "Livre";
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(" | de %-30s, %4d pages, ISBN %15s", auteur, nbPages, isbn);
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
