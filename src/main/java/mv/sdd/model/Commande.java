package mv.sdd.model;

import mv.sdd.utils.Constantes;
import java.util.List;
import java.util.ArrayList;

public class Commande {
    private int id;
    private static int nbCmd = 0;
    private final Client client;
    private EtatCommande etat = EtatCommande.EN_ATTENTE;
    private int tempsRestant; // en minutes simulées
    // TODO : ajouter l'attribut plats et son getter avec le bon type et le choix de
    // la SdD adéquat
    // Attribut plats ajouté
    private final List<Plat> plats;

    // TODO : Ajout du ou des constructeur(s) nécessaires ou compléter au besoin
    // Constructeur complété
    public Commande(Client client, MenuPlat plat) {
        id = ++nbCmd;
        this.client = client;
        this.plats = new ArrayList<>();
        ajouterPlat(plat);
    }

    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public List<Plat> getPlats() {
        return plats;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public int getTempsRestant() {
        return tempsRestant;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    // TODO : Ajoutez la méthode ajouterPlat
    public void ajouterPlat(MenuPlat codePlat) {
        Plat plat = Constantes.MENU.get(codePlat);
        if (plat != null) {
            plats.add(plat);
        }
    }

    // TODO : Ajoutez la méthode demarrerPreparation
    public void demarrerPreparation() {
        this.etat = EtatCommande.EN_PREPARATION;
        this.tempsRestant = calculerTempsPreparationTotal();
    }

    // TODO : Ajoutez la méthode decrementerTempsRestant
    public void decrementerTempsRestant(int minutes) {
        if (etat == EtatCommande.EN_PREPARATION) {
            this.tempsRestant -= minutes;
            if (this.tempsRestant <= 0) {
                this.tempsRestant = 0;
                this.etat = EtatCommande.PRETE;
            }
        }
    }

    // TODO : Ajoutez la méthode estTermineeParTemps
    public boolean estTermineeParTemps() {
        return tempsRestant == 0 && etat == EtatCommande.PRETE;
    }

    // TODO : Ajoutez la méthode calculerTempsPreparationTotal
    public int calculerTempsPreparationTotal() {
        int total = 0;
        for (Plat p : plats) {
            total += p.getTempsPreparation();
        }
        return total;
    }

    // TODO : Ajoutez la méthode calculerMontant
    public double calculerMontant() {
        double total = 0.0;
        for (Plat p : plats) {
            total += p.getPrix();
        }
        return total;
    }
}
