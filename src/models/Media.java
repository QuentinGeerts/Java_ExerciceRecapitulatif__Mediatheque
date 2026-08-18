package models;

import configs.Config;
import enums.Genre;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Media {

    // Attribut de classe
    // Partagé par toutes les instances

    private static int nextId = 1;

    // Attributs d'instance

    private final int id;
    private String titre;
    private int anneeSortie;
    private Genre genre;
    private boolean disponible;

    // Constructeur

    public Media(String titre, int anneeSortie, Genre genre) {
        this.id = nextId++;
        disponible = true;

        // Utilisation des setters pour vérifier les contraintes à la création
        setTitre(titre);
        setAnneeSortie(anneeSortie);
        setGenre(genre);
    }

    // Getters / Setters

    public final int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        if (titre == null || titre.isBlank()) throw new IllegalArgumentException("Le titre ne peut être null ou vide.");
        this.titre = titre.trim();
    }

    public int getAnneeSortie() {
        return anneeSortie;
    }

    public void setAnneeSortie(int anneeSortie) {
        int anneeActuelle = LocalDate.now().getYear();
        if (anneeSortie < Config.MIN_ANNEE_SORTIE || anneeSortie > anneeActuelle)
            throw new IllegalArgumentException(
                    String.format("L'année de sortie doit être comprise entre %d et %d.", Config.MIN_ANNEE_SORTIE, anneeActuelle)
            );
        this.anneeSortie = anneeSortie;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        if (genre == null) throw new IllegalArgumentException("Le genre ne peut pas être null.");
        this.genre = genre;
    }

    public boolean isDisponible() {
        return disponible;
    }

    // Méthodes à redéfinir obligatoirement dans les classes filles

    public abstract int dureeEmpruntJours();

    public abstract String typeLibelle();

    // Accessible uniquement dans le package models

    void marquerEmprunte() {
        disponible = false;
    }

    void marquerDisponible() {
        disponible = true;
    }

    // Redéfinitions
    @Override
    public String toString() {
        String etat = disponible ? "disponible" : "emprunté";
        return String.format("[%2d] %-10s - \"%-60s\" (%4d, %-15s) - %-10s", id, typeLibelle(), titre, anneeSortie, genre, etat);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Media media)) return false;
        return id == media.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
