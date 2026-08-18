package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {

    // Attributs

    private final Media media;
    private final Membre membre;
    private final LocalDate dateEmprunt;
    private final LocalDate dateRetourPrevue;
    private LocalDate dateRetourReelle;

    // Constructeur

    public Emprunt(Media media, Membre membre) {
        if (media == null) throw new IllegalArgumentException("Le média d'un emprunt ne peut pas être null.");
        if (membre == null) throw new IllegalArgumentException("Le membre d'un emprunt ne peut pas être null.");
        this.media = media;
        this.membre = membre;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = this.dateEmprunt.plusDays(this.media.dureeEmpruntJours());

        media.marquerEmprunte();
    }

    // Getters (lecture seule)

    public Media getMedia() {
        return media;
    }

    public Membre getMembre() {
        return membre;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public LocalDate getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public LocalDate getDateRetourReelle() {
        return dateRetourReelle;
    }

    // Méthodes

    public boolean estEnCours() {
        return dateRetourReelle == null;
    }

    public boolean estEnRetard() {
        return estEnCours() && dateRetourPrevue.isBefore(LocalDate.now());
    }

    public long joursDeRetard() {
        return !estEnRetard() ? 0 : ChronoUnit.DAYS.between(dateRetourPrevue, LocalDate.now());
    }

    public long cloturer() {
        dateRetourReelle = LocalDate.now();
        media.marquerDisponible();
        return joursDeRetard();
    }

    @Override
    public String toString() {
        if (estEnCours()) {
            String suffixe = estEnRetard()
                    ? String.format(" - EN RETARD de %d jour(s)", joursDeRetard())
                    : "";
            return String.format("\"%s\" par %s - emprunte le %s, retour prevu le %s%s",
                    media.getTitre(), membre.nomComplet(),
                    dateEmprunt, dateRetourPrevue, suffixe);
        }
        return String.format("\"%s\" par %s - emprunte le %s, rendu le %s",
                media.getTitre(), membre.nomComplet(), dateEmprunt, dateRetourReelle);
    }
}
