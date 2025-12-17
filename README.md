# Projet Final - Simulation de Restaurant : (LINK : https://github.com/Yassine3232/Epreuve-Finale-Structure-De-Donnee)

Ceci est le projet final du cours de structures de données.

## Description du projet

Ce programme Java simule un restaurant pendant le rush du midi:
- Des clients arrivent et passent des commandes 🍕
- Un cuisinier prépare les plats dans un thread séparé
- Les clients reçoivent leurs plats ou partent mécontents si l'attente est trop longue 😡
- Toutes les actions sont lues depuis un fichier texte
- Tous les événements sont écrits dans un fichier de logs

## Prérequis

- Java 21 ou plus récent
- Maven installé
- IDE recommandé: IntelliJ IDEA

---

## Installation
```bash
git clone https://github.com/la-sarita/Epreuve_finale_420_311.git
cd Epreuve_finale_420_311
```

## Structure du projet
```
.
├── pom.xml
└── src
    └── main
        └── java
            └── mv
                └── sdd
                    ├── App.java          # Point d'entrée (main)
                    ├── model/            # Les objets (Client, Commande, etc.)
                    ├── sim/              # La simulation
                    │   └── thread/       # Les threads (Cuisinier)
                    ├── io/               # Lecture de fichiers et logs
                    └── utils/            # Utilitaires
```

## Fichiers de test

Le dossier `data` contient des exemples de scénarios pour tester le programme.

## Compilation

À la racine du projet:
```bash
mvn clean package
```

Maven va créer un fichier .jar dans le dossier `target/`.

## Exécution

Le programme nécessite deux arguments:
1. Le chemin du fichier de scénario (entrée)
2. Le chemin du fichier de sortie (logs)

Pour exécuter avec Maven:
```bash
mvn exec:java -Dexec.mainClass="mv.sdd.App" \
  -Dexec.args="data/scenario_1.txt data/sortie_1.txt"
```

Après l'exécution, un fichier `data/sortie_1.txt` sera créé contenant tous les logs de la simulation.

🍕🍔