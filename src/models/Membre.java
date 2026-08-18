package models;

import java.time.LocalDate;
import java.util.Objects;

public class Membre {

    // Attributs

    private static int nextId = 1;

    private final int id;
    private final LocalDate dateInscription;
    private String nom;
    private String prenom;
    private String email;

    // Constructeur

    public Membre(String nom, String prenom, String email) {
        this.id = nextId++;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateInscription = LocalDate.now();
    }

    // Getters et setters

    public final int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    // Redéfinition des méthodes
    // Basé sur l'email (deux membres peuvent être des homonymes mais leurs emails doivent être différents)

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Membre membre)) return false;
        return Objects.equals(email, membre.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    public String nomComplet() {
        return nom + " " + prenom;
    }
}
