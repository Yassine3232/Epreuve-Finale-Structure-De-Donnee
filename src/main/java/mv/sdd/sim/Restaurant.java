package mv.sdd.sim;

import mv.sdd.io.Action;
import mv.sdd.utils.Logger;
import mv.sdd.model.Client;
import mv.sdd.model.Commande;
import mv.sdd.model.Horloge;
import mv.sdd.model.Stats;
import mv.sdd.model.MenuPlat;
import mv.sdd.model.EtatClient;
import mv.sdd.sim.thread.Cuisinier;
import mv.sdd.utils.Constantes;
import mv.sdd.utils.Formatter;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Restaurant {
    private final Logger logger;
    // TODO : Ajouter les attributs nécessaires ainsi que les getters et les setters
    private Queue<Commande> commandesEnAttente;
    private List<Client> clients;
    private Cuisinier cuisinier;
    private Thread threadCuisinier;
    private Horloge horloge;
    private Stats stats;

    // TODO : Ajouter le(s) constructeur(s)
    public Restaurant(Logger logger) {
        this.logger = logger;
        this.commandesEnAttente = new LinkedList<>();
        this.clients = new ArrayList<>();
        this.horloge = new Horloge();
        this.stats = new Stats(horloge);
    }

    // TODO : implémenter les méthodes suivantes
    public void executerAction(Action action) {
        switch (action.getType()) {
            case DEMARRER_SERVICE:
                demarrerService(action.getParam1(), action.getParam2());
                break;
            case AVANCER_TEMPS:
                avancerTemps(action.getParam1());
                break;
            case AJOUTER_CLIENT:
                ajouterClient(action.getParam1(), action.getParam3(), action.getParam2());
                break;
            case PASSER_COMMANDE:
                passerCommande(action.getParam1(), MenuPlat.valueOf(action.getParam3()));
                break;
            case AFFICHER_ETAT:
                afficherEtat();
                break;
            case AFFICHER_STATS:
                afficherStatistiques();
                break;
            case QUITTER:
                arreterService();
                logger.logLine("Fin de la simulation.");
                break;
        }
    }

    public void demarrerService(int dureeMax, int nbCuisiniers) {
        logger.logLine(String.format(Constantes.DEMARRER_SERVICE, dureeMax, nbCuisiniers));
        cuisinier = new Cuisinier(this);
        threadCuisinier = new Thread(cuisinier);
        threadCuisinier.start();
    }

    public void avancerTemps(int minutes) {
        logger.logLine(String.format(Constantes.AVANCER_TEMPS + " %d min", minutes));
        for (int i = 0; i < minutes; i++) {
            tick();
        }
    }

    public void arreterService() {
        if (cuisinier != null) {
            cuisinier.arreter();
            synchronized (this) {
                this.notifyAll(); // Wake up cook if waiting
            }
            try {
                threadCuisinier.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // TODO : Déclarer et implémenter les méthodes suivantes
    private void tick() {
        horloge.avancerTempsSimule(1);
        diminuerPatienceClients();

        synchronized (this) {
            this.notifyAll();
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void afficherEtat() {
        int servis = 0;
        long nbServis = clients.stream().filter(c -> c.getEtat() == EtatClient.SERVI).count();
        long nbFaches = clients.stream().filter(c -> c.getEtat() == EtatClient.PARTI_FACHE).count();
        int prep = (cuisinier != null && cuisinier.commandeEnCours != null) ? 1 : 0;

        logger.logLine(String.format(Constantes.RESUME_ETAT,
                horloge.getTempsSimule(),
                clients.size(),
                nbServis,
                nbFaches,
                commandesEnAttente.size(),
                prep));

        for (Client c : clients) {
            MenuPlat codePlat = null;
            if (c.getCommande() != null && !c.getCommande().getPlats().isEmpty()) {
                codePlat = c.getCommande().getPlats().get(0).getCode();
            }
            logger.logLine(Formatter.clientLine(c, codePlat));
        }
    }

    public void afficherStatistiques() {
        logger.logLine(stats.toString());
    }

    private void ajouterClient(int id, String nom, int patienceInitiale) {
        Client c = new Client(id, nom, patienceInitiale);
        clients.add(c);
        stats.incrementerTotalClients();
        logger.logLine(String.format(Constantes.EVENT_ARRIVEE_CLIENT, horloge.getTempsSimule(), id, nom,
                patienceInitiale));
    }

    private void passerCommande(int idClient, MenuPlat codePlat) {
        for (Client c : clients) {
            if (c.getId() == idClient && c.getEtat() == EtatClient.EN_ATTENTE) {
                Commande cmd = new Commande(c, codePlat);
                c.setCommande(cmd);
                commandesEnAttente.offer(cmd);

                logger.logLine(String.format(Constantes.EVENT_CMD_CREE,
                        horloge.getTempsSimule(), cmd.getId(), c.getNom(), codePlat));

                stats.incrementerVentesParPlat(codePlat);
                stats.incrementerChiffreAffaires(cmd.calculerMontant());
                return;
            }
        }
    }

    public synchronized Commande retirerProchaineCommande() {
        return commandesEnAttente.poll();
    }

    public void logDebutPreparation(Commande c) {
        logger.logLine(String.format(Constantes.EVENT_CMD_DEBUT,
                horloge.getTempsSimule(), c.getId(), c.calculerTempsPreparationTotal()));
    }

    public synchronized void marquerCommandeTerminee(Commande commande) {
        Client c = commande.getClient();
        if (c.getEtat() != EtatClient.PARTI_FACHE) {
            c.setEtat(EtatClient.SERVI);
            stats.incrementerNbServis();
            logger.logLine(String.format(Constantes.EVENT_CMD_TERMINEE,
                    horloge.getTempsSimule(), commande.getId(), c.getNom()));
        }
    }

    // TODO : implémenter d'autres sous-méthodes qui seront appelées par les
    // méthodes principales
    // pour améliorer la lisibilité des méthodes en les découpant au besoin (éviter
    // les trés longues méthodes)
    // exemple : on peut avoir une méthode diminuerPatienceClients()
    // qui permet de diminuer la patience des clients (appelée par tick())
    private void diminuerPatienceClients() {
        for (Client c : clients) {
            if (c.getEtat() == EtatClient.EN_ATTENTE) {
                c.diminuerPatience(1);
                if (c.getEtat() == EtatClient.PARTI_FACHE) {
                    stats.incrementerNbFaches();
                    logger.logLine(String.format(Constantes.EVENT_CLIENT_FACHE, horloge.getTempsSimule(), c.getNom()));
                }
            }
        }
    }
}
