package ma.fstt.webjpa2.model;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Date;

public class TestECommerce {

    public static void main(String[] args) {
        System.out.println("🚀 Démarrage du test E-Commerce JPA...");

        EntityManagerFactory emf = null;
        EntityManager em = null;

        try {
            // 1. Création de l'EntityManagerFactory
            emf = Persistence.createEntityManagerFactory("mycnx");
            em = emf.createEntityManager();

            System.out.println("✅ Connexion à la base de données établie");

            // 2. Début de la transaction
            em.getTransaction().begin();
            System.out.println("✅ Transaction démarrée");

            // 3. CRÉATION DES CATÉGORIES
            System.out.println("\n📂 Création des catégories...");
            Categorie categorieElectronique = new Categorie("Électronique", "Produits électroniques et high-tech");
            Categorie categorieVetements = new Categorie("Vêtements", "Vêtements pour hommes et femmes");

            em.persist(categorieElectronique);
            em.persist(categorieVetements);
            em.flush(); // IMPORTANT: Force la persistance pour avoir les IDs

            System.out.println("✅ Catégories créées: " + categorieElectronique.getNomCategorie() + ", " + categorieVetements.getNomCategorie());

            // 4. CRÉATION DES PRODUITS
            System.out.println("\n📦 Création des produits...");
            Produit smartphone = new Produit("iPhone 15", "Smartphone Apple iPhone 15 Pro", 1299.99, 50, categorieElectronique);
            Produit laptop = new Produit("MacBook Pro", "Ordinateur portable Apple M3", 2499.99, 25, categorieElectronique);
            Produit tshirt = new Produit("T-Shirt Cotton", "T-shirt 100% coton", 29.99, 100, categorieVetements);
            Produit jeans = new Produit("Jeans Slim", "Jeans slim fit délavé", 79.99, 75, categorieVetements);

            em.persist(smartphone);
            em.persist(laptop);
            em.persist(tshirt);
            em.persist(jeans);
            em.flush(); // IMPORTANT: Force la persistance pour avoir les IDs

            System.out.println("✅ Produits créés: " + smartphone.getNom() + ", " + laptop.getNom() + ", " + tshirt.getNom() + ", " + jeans.getNom());

            // 5. CRÉATION D'UN INTERNAUTE
            System.out.println("\n👤 Création d'un internaute...");
            Internaute internaute = new Internaute("Dupont", "Jean", "jean.dupont@email.com", "monpassword", "123 Rue de Paris, 75001");
            Internaute admin = new Internaute("Admin", "System", "admin@email.com", "admin123", "123 Rue Admin, Paris");

            em.persist(internaute);
            em.persist(admin);
            em.flush(); // IMPORTANT: Force la persistance pour avoir les IDs

            System.out.println("✅ Internautes créés: " + internaute.getPrenom() + " " + internaute.getNom() + " et " + admin.getPrenom());

            // 6. CRÉATION D'UN PANIER
            System.out.println("\n🛒 Création d'un panier...");
            Panier panier = new Panier(internaute);
            em.persist(panier);
            em.flush(); // IMPORTANT: Force la persistance pour avoir l'ID

            System.out.println("✅ Panier créé pour: " + panier.getInternaute().getPrenom());

            // 7. AJOUT DE PRODUITS AU PANIER - MÉTHODE CORRIGÉE
            System.out.println("\n📋 Ajout de produits au panier...");

            // Méthode SÉCURISÉE : Création manuelle des lignes panier
            LignePanier ligneSmartphone = new LignePanier(panier, smartphone, 2);
            LignePanier ligneLaptop = new LignePanier(panier, laptop, 1);
            LignePanier ligneTshirt = new LignePanier(panier, tshirt, 3);
            LignePanier ligneJeans = new LignePanier(panier, jeans, 2);

            em.persist(ligneSmartphone);
            em.persist(ligneLaptop);
            em.persist(ligneTshirt);
            em.persist(ligneJeans);

            // Ajouter aux collections
            panier.getLignePaniers().add(ligneSmartphone);
            panier.getLignePaniers().add(ligneLaptop);
            panier.getLignePaniers().add(ligneTshirt);
            panier.getLignePaniers().add(ligneJeans);

            System.out.println("✅ Produits ajoutés au panier:");
            for (LignePanier ligne : panier.getLignePaniers()) {
                System.out.println("   - " + ligne.getProduit().getNom() + " x" + ligne.getQuantite() + " = " + ligne.getSousTotal() + "€");
            }

            System.out.println("💰 Total du panier: " + panier.getTotal() + "€");
            System.out.println("📦 Nombre total d'articles: " + panier.getNombreArticles());

            // 8. CRÉATION D'UNE COMMANDE
            System.out.println("\n📦 Création d'une commande...");
            Commande commande = new Commande(internaute, "EN_ATTENTE");
            em.persist(commande);

            // Conversion des lignes panier en lignes commande
            for (LignePanier lignePanier : panier.getLignePaniers()) {
                LigneCommande ligneCommande = new LigneCommande(lignePanier, commande);
                em.persist(ligneCommande);
                commande.ajouterLigneCommande(ligneCommande);
            }

            commande.recalculerTotal();
            System.out.println("✅ Commande créée:");
            System.out.println("   - ID: " + commande.getIdCommande());
            System.out.println("   - Date: " + commande.getDateCommande());
            System.out.println("   - Statut: " + commande.getStatut());
            System.out.println("   - Total: " + commande.getTotal() + "€");

            // 9. CONFIRMATION DE LA COMMANDE
            System.out.println("\n✅ Confirmation de la commande...");
            commande.confirmer();
            System.out.println("✅ Commande confirmée - Statut: " + commande.getStatut());

            // Vérification des stocks après confirmation
            System.out.println("\n📊 Vérification des stocks après commande:");
            System.out.println("   - " + smartphone.getNom() + ": " + smartphone.getStock() + " unités restantes");
            System.out.println("   - " + laptop.getNom() + ": " + laptop.getStock() + " unités restantes");
            System.out.println("   - " + tshirt.getNom() + ": " + tshirt.getStock() + " unités restantes");
            System.out.println("   - " + jeans.getNom() + ": " + jeans.getStock() + " unités restantes");

            // 10. VALIDATION DE LA TRANSACTION
            em.getTransaction().commit();
            System.out.println("\n✅ Transaction validée avec succès!");

            // 11. TESTS DE LECTURE
            System.out.println("\n🔍 Tests de lecture après commit...");
            testLectureDonnees(em);

        } catch (Exception e) {
            // Gestion des erreurs
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                System.out.println("❌ Erreur - Transaction annulée: " + e.getMessage());
            }
            e.printStackTrace();

        } finally {
            // Fermeture des ressources
            if (em != null) {
                em.close();
                System.out.println("🔒 EntityManager fermé");
            }
            if (emf != null) {
                emf.close();
                System.out.println("🔒 EntityManagerFactory fermé");
            }
        }

        System.out.println("\n🎉 Test E-Commerce JPA terminé avec succès!");
    }

    /**
     * Méthode pour tester la lecture des données après insertion
     */
    private static void testLectureDonnees(EntityManager em) {
        System.out.println("\n--- TESTS DE LECTURE ---");

        // Lecture des catégories
        System.out.println("\n📂 Catégories en base:");
        var categories = em.createQuery("SELECT c FROM Categorie c", Categorie.class).getResultList();
        for (Categorie cat : categories) {
            System.out.println("   - " + cat.getNomCategorie() + " (" + cat.getDescription() + ")");
        }

        // Lecture des produits
        System.out.println("\n📦 Produits en base:");
        var produits = em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList();
        for (Produit prod : produits) {
            System.out.println("   - " + prod.getNom() + " | " + prod.getPrix() + "€ | Stock: " + prod.getStock());
        }

        // Lecture des internautes
        System.out.println("\n👤 Internautes en base:");
        var internautes = em.createQuery("SELECT i FROM Internaute i", Internaute.class).getResultList();
        for (Internaute intern : internautes) {
            System.out.println("   - " + intern.getPrenom() + " " + intern.getNom() + " (" + intern.getEmail() + ")");
        }

        // Lecture des commandes
        System.out.println("\n📦 Commandes en base:");
        var commandes = em.createQuery("SELECT c FROM Commande c", Commande.class).getResultList();
        for (Commande cmd : commandes) {
            System.out.println("   - Commande #" + cmd.getIdCommande() + " | " + cmd.getStatut() + " | Total: " + cmd.getTotal() + "€");
        }

        // Lecture des paniers
        System.out.println("\n🛒 Paniers en base:");
        var paniers = em.createQuery("SELECT p FROM Panier p", Panier.class).getResultList();
        for (Panier pan : paniers) {
            System.out.println("   - Panier #" + pan.getIdPanier() + " | " + pan.getLignePaniers().size() + " articles");
        }
    }
}