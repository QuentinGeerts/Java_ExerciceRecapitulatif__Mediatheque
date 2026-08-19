# Médiathèque « Le Signet »

Application console en Java permettant de gérer le catalogue, les membres et les emprunts d'une médiathèque. Ce projet est un **exercice récapitulatif** de fin de module, mettant en pratique les notions fondamentales de la programmation orientée objet en Java : héritage, polymorphisme, encapsulation, énumérations, collections et CRUD.

> L'énoncé complet de l'exercice est disponible à la racine du dépôt : `Java - Exercice récapitulatif - Gestion d'une médiathèque.pdf`.

## Contexte

La médiathèque **« Le Signet »** souhaite remplacer son cahier papier par une application. Elle doit gérer :

- un **catalogue de médias** : livres, DVD et jeux vidéo ;
- des **membres inscrits** ;
- des **emprunts** : qui a emprunté quoi, depuis quand, et pour combien de temps encore.

L'exercice consiste à écrire l'application console qui pilote l'ensemble, en respectant un CRUD complet (Create, Read, Update, Delete) sur les médias.

## Fonctionnalités

Au lancement, l'application charge un jeu de données de démonstration (médias, membres, emprunts) puis affiche un menu interactif :

```
========================================
   MÉDIATHÈQUE LE SIGNET
========================================
 1. Ajouter un média
 2. Lister le catalogue
 3. Rechercher un média
 4. Modifier un média
 5. Supprimer un média
----------------------------------------
 6. Inscrire un membre
 7. Emprunter
 8. Rendre
 9. Emprunts en cours
----------------------------------------
10. Statistiques
 0. Quitter
========================================
```

Chaque option correspond à une méthode `static` dédiée dans `Main` ; le `main` ne contient que la boucle et le `switch` (syntaxe fléchée). Conformément à la règle centrale de l'exercice, **`Mediatheque` ne fait aucun `System.out.println`** : le service calcule et renvoie des résultats, `Main` se charge de tout l'affichage.

## Architecture

Le projet respecte l'architecture imposée par l'énoncé, qui sépare *ce que sont les choses* (`models`) de *ce qu'on en fait* (`services`) :

```
src/
├── Main.java              # Point d'entrée et menu (hors des autres packages)
├── configs/
│   └── Config.java        # Constantes static final (nom, règles d'emprunt...)
├── enums/
│   ├── Genre.java          # ACTION, AVENTURE, POLICIER, SCIENCE_FICTION, DOCUMENTAIRE, JEUNESSE, FANTASY
│   └── Plateforme.java     # PS5, XBOX, SWITCH, PC
├── models/
│   ├── Media.java          # Classe abstraite commune à tous les médias
│   ├── Livre.java           # extends Media : auteur, nbPages, isbn
│   ├── Dvd.java              # extends Media : réalisateur, dureeMinutes
│   ├── JeuVideo.java         # extends Media : plateforme, pegi
│   ├── Membre.java          # Membre inscrit à la médiathèque
│   ├── Emprunt.java          # Association média ↔ membre, avec dates
│   └── Statistiques.java     # Agrégat de statistiques calculées
├── services/
│   └── Mediatheque.java    # Le cœur : CRUD + gestion des emprunts
└── utils/
    └── ConsoleUtils.java   # Saisie et affichage console (méthodes static uniquement)
```

## Le modèle

### `Media` (classe abstraite)

| Attribut | Type | Contrainte |
|---|---|---|
| `id` | `int` | auto-incrémenté (`private static int nextId`), `final`, jamais modifiable |
| `titre` | `String` | jamais `null` ni vide |
| `anneeSortie` | `int` | comprise entre une année minimale (1800 dans ce projet) et l'année courante |
| `genre` | `Genre` | jamais `null` |
| `disponible` | `boolean` | `true` à la création ; aucun setter public |

Points clés :
- Deux méthodes abstraites, redéfinies par chaque sous-classe : `dureeEmpruntJours()` et `typeLibelle()`.
- La disponibilité ne peut changer que via `marquerEmprunte()` / `marquerDisponible()`, à **visibilité package** (`models`) : impossible de rendre un média disponible depuis l'extérieur sans passer par la `Mediatheque`.
- `toString()`, `equals()` et `hashCode()` sont redéfinis, basés uniquement sur l'`id`.

### Les trois sous-classes de `Media`

| Classe | Attributs supplémentaires | Durée d'emprunt |
|---|---|---|
| `Livre` | `auteur`, `nbPages` (> 0), `isbn` | 21 jours |
| `Dvd` | `realisateur`, `dureeMinutes` (> 0) | 7 jours |
| `JeuVideo` | `plateforme` (enum), `pegi` ∈ {3, 7, 12, 16, 18} | 14 jours |

Chacune appelle `super(...)` dans son constructeur et délègue à `super.toString()` / `super.equals()` / `super.hashCode()`.

### `Membre`

Identifiant auto-incrémenté, nom, prénom, email et date d'inscription (`LocalDate`). `equals()`/`hashCode()` se basent sur l'**email** (deux homonymes sont acceptés, deux emails identiques non).

### `Emprunt`

Lie un `Media` et un `Membre`. Le constructeur fixe `dateEmprunt` à aujourd'hui et calcule `dateRetourPrevue` grâce au polymorphisme (`media.dureeEmpruntJours()`) — sans jamais savoir de quel type de média il s'agit.

- `estEnCours()` → `dateRetourReelle == null`
- `estEnRetard()` → en cours et date prévue dépassée
- `joursDeRetard()` → nombre de jours de retard (0 si aucun)
- `cloturer()` → enregistre la date de retour réelle, libère le média et renvoie le retard

### `Statistiques`

Calculée à la demande à partir d'une `Mediatheque` (nombre de médias par type, disponibles/empruntés, membres inscrits, genre le plus représenté via une `Map<Genre, Integer>`).

##  Le CRUD (`services/Mediatheque.java`)

| Opération | Méthodes | Comportement |
|---|---|---|
| **Create** | `ajouterMedia`, `ajouterMedias` (varargs), `inscrireMembre` | refuse les `null` et les doublons (id déjà présent, email déjà inscrit) |
| **Read** | `rechercherMediaParId`, `listerCatalogue`, `rechercher(String)`, `rechercher(Genre)`, `listerDisponible`, `listerParType` | `listerCatalogue()` renvoie une **copie** de la collection interne, pour empêcher un appelant de la vider depuis l'extérieur |
| **Update** | `modifierMedia(id, titre, annee, genre)` | ne crée jamais de média si l'id n'existe pas ; passe par les setters du modèle, qui valident (la médiathèque ne réécrit pas les règles) |
| **Delete** | `supprimerMedia(id)` | refuse si l'id n'existe pas ou si le média est actuellement emprunté |

Gestion des emprunts :

- `emprunter(idMedia, idMembre)` : refuse dans l'ordre si le média n'existe pas, si le membre n'existe pas, si le média n'est pas disponible, ou si le membre a déjà atteint `Config.MAX_EMPRUNTS_PAR_MEMBRE` emprunts en cours.
- `rendre(idMedia)` : clôture l'emprunt et renvoie le nombre de jours de retard.
- `empruntsEnCours()` / `empruntsEnCours(Membre)`, `listerRetards()`, `calculerStatistiques()`.

## Règles de gestion (résumé)

- Un membre ne peut avoir plus de **`Config.MAX_EMPRUNTS_PAR_MEMBRE`** (3) emprunts en cours simultanément.
- L'année de sortie d'un média doit être comprise entre `Config.MIN_ANNEE_SORTIE` et l'année en cours.
- Deux membres ne peuvent pas partager le même email.
- Un média déjà emprunté ne peut être ni ré-emprunté, ni supprimé.
- Le PEGI d'un jeu vidéo doit obligatoirement valoir 3, 7, 12, 16 ou 18.

## Auto-contrôle (encapsulation)

L'énoncé impose que certaines lignes, écrites depuis `Main`, **refusent de compiler** :

```java
media.disponible = true;                 // attribut private
media.marquerDisponible();               // visible seulement dans le package models
mediatheque.catalogue.clear();           // collection private
new Media("titre", 2020, Genre.ACTION);  // classe abstraite
```

Et ce test doit passer **sans laisser de trace** (grâce à la copie défensive de `listerCatalogue()`) :

```java
mediatheque.listerCatalogue().clear();
System.out.println(mediatheque.listerCatalogue().size()); // taille inchangée
```

Enfin, `equals()`/`hashCode()` doivent être cohérents :

```java
Set<Media> set = new HashSet<>();
set.add(livre);
set.add(livre);
System.out.println(set.size()); // doit afficher 1, pas 2
```

## Paliers de progression

| Palier | Contenu |
|---|---|
| **Niveau 1 — le socle** | Modèle + CRUD complet (menu réduit aux options 1 à 5) — le minimum évalué |
| **Niveau 2 — la gestion** | Gestion des emprunts/statistiques + menu complet — l'objectif normal de fin de module |

Ce dépôt implémente le **Niveau 2** : le menu complet (options 1 à 10) et la gestion des emprunts, retours et statistiques sont fonctionnels.

## Lancer le projet

Le projet ne dépend d'aucune bibliothèque externe (Java standard uniquement). Il a été développé avec IntelliJ IDEA (fichier `Mediatheque.iml`).

**Depuis IntelliJ IDEA :**
1. Ouvrir le dossier du projet.
2. Exécuter la méthode `main` de la classe `Main`.

**En ligne de commande :**
```bash
cd src
javac Main.java configs/*.java enums/*.java models/*.java services/*.java utils/*.java
java Main
```

> Le projet utilise la syntaxe de méthode `main` sans arguments (`static void main()`), disponible depuis les aperçus de fonctionnalités récents du JDK — une version récente de Java est donc recommandée.

## Documentation utile

- [Tutoriel Java — Collections](https://docs.oracle.com/javase/tutorial/collections/)
- [API `java.util.Map`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Map.html)
- [API `java.util.List`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/List.html)
- [API `java.time.LocalDate`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/time/LocalDate.html)
- [Contrat `equals()` / `hashCode()`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Object.html#equals)
- [Tutoriel Java — Classes abstraites](https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html)