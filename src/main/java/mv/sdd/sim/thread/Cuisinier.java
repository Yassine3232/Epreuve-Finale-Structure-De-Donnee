package mv.sdd.sim.thread;

public class Cuisinier implements Runnable {
    private final mv.sdd.sim.Restaurant restaurant;
    private boolean enService = true;
    public mv.sdd.model.Commande commandeEnCours = null;

    public Cuisinier(mv.sdd.sim.Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public void arreter() {
        this.enService = false;
    }

    @Override
    public void run() {
        while (enService) {
            synchronized (restaurant) {
                // Essayer de prendre une commande si libre
                if (commandeEnCours == null) {
                    commandeEnCours = restaurant.retirerProchaineCommande();
                    if (commandeEnCours != null) {
                        commandeEnCours.demarrerPreparation();
                        // Log debut preparation needs access to Restaurant logger or method
                        // We added logDebutPreparation in Restaurant
                        restaurant.logDebutPreparation(commandeEnCours);
                    }
                }

                // Travailler sur la commande
                if (commandeEnCours != null) {
                    commandeEnCours.decrementerTempsRestant(1);
                    if (commandeEnCours.estTermineeParTemps()) {
                        restaurant.marquerCommandeTerminee(commandeEnCours);
                        commandeEnCours = null;
                    }
                }

                try {
                    // Attendre le prochain tick du restaurant
                    restaurant.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    enService = false;
                }
            }
        }
    }
}
