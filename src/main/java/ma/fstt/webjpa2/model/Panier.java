package ma.fstt.webjpa2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "panier")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_panier")
    private Long idPanier;

    @Column(name = "date_creation", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    // Relation Many-to-One avec Internaute
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "internaute_id", nullable = false)
    private Internaute internaute;

    // Relation One-to-Many avec LignePanier
    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LignePanier> lignePaniers = new ArrayList<>();

    // Constructeur personnalisé
    public Panier(Internaute internaute) {
        this.internaute = internaute;
        this.dateCreation = new Date(); // Date actuelle
    }

    /**
     * Méthode SÉCURISÉE pour ajouter un produit au panier
     * Version corrigée qui évite les NullPointerException
     */
    public void ajouterProduit(Produit produit, Integer quantite) {
        // Validation des paramètres
        if (produit == null) {
            throw new IllegalArgumentException("Le produit ne peut pas être null");
        }
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        if (produit.getIdProduit() == null) {
            throw new IllegalStateException("Le produit doit être persisté en base de données avant d'être ajouté au panier");
        }

        // Vérifier si le produit est déjà dans le panier
        LignePanier ligneExistante = trouverLigneParProduit(produit);

        if (ligneExistante != null) {
            // Mettre à jour la quantité de la ligne existante
            ligneExistante.setQuantite(ligneExistante.getQuantite() + quantite);
        } else {
            // Créer une nouvelle ligne de panier
            LignePanier nouvelleLigne = new LignePanier(this, produit, quantite);
            lignePaniers.add(nouvelleLigne);
        }
    }

    /**
     * Méthode utilitaire pour trouver une ligne par produit
     */
    private LignePanier trouverLigneParProduit(Produit produit) {
        return lignePaniers.stream()
                .filter(ligne -> {
                    Produit produitLigne = ligne.getProduit();
                    return produitLigne != null &&
                            produitLigne.getIdProduit() != null &&
                            produitLigne.getIdProduit().equals(produit.getIdProduit());
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * Méthode pour calculer le total du panier
     */
    public Double getTotal() {
        if (lignePaniers == null || lignePaniers.isEmpty()) {
            return 0.0;
        }

        return lignePaniers.stream()
                .mapToDouble(ligne -> {
                    if (ligne != null && ligne.getSousTotal() != null) {
                        return ligne.getSousTotal();
                    }
                    return 0.0;
                })
                .sum();
    }

    /**
     * Méthode pour obtenir le nombre total d'articles dans le panier
     */
    public Integer getNombreArticles() {
        if (lignePaniers == null || lignePaniers.isEmpty()) {
            return 0;
        }

        return lignePaniers.stream()
                .mapToInt(ligne -> ligne != null && ligne.getQuantite() != null ? ligne.getQuantite() : 0)
                .sum();
    }

    /**
     * Méthode pour vider complètement le panier
     */
    public void viderPanier() {
        if (lignePaniers != null) {
            lignePaniers.clear();
        }
    }

    /**
     * Méthode pour supprimer un produit spécifique du panier
     */
    public boolean supprimerProduit(Produit produit) {
        if (produit == null || produit.getIdProduit() == null) {
            return false;
        }

        return lignePaniers.removeIf(ligne -> {
            Produit produitLigne = ligne.getProduit();
            return produitLigne != null &&
                    produitLigne.getIdProduit() != null &&
                    produitLigne.getIdProduit().equals(produit.getIdProduit());
        });
    }

    /**
     * Méthode pour mettre à jour la quantité d'un produit
     */
    public boolean mettreAJourQuantite(Produit produit, Integer nouvelleQuantite) {
        if (produit == null || nouvelleQuantite == null || nouvelleQuantite <= 0) {
            return false;
        }

        LignePanier ligne = trouverLigneParProduit(produit);
        if (ligne != null) {
            ligne.setQuantite(nouvelleQuantite);
            return true;
        }
        return false;
    }

    /**
     * Méthode pour vérifier si le panier est vide
     */
    public boolean estVide() {
        return lignePaniers == null || lignePaniers.isEmpty();
    }

    /**
     * Méthode pour obtenir le nombre de produits différents dans le panier
     */
    public Integer getNombreProduitsDifferentes() {
        if (lignePaniers == null) {
            return 0;
        }

        return (int) lignePaniers.stream()
                .filter(ligne -> ligne != null && ligne.getProduit() != null)
                .count();
    }

    /**
     * Méthode pour vérifier si un produit spécifique est dans le panier
     */
    public boolean contientProduit(Produit produit) {
        if (produit == null || produit.getIdProduit() == null) {
            return false;
        }

        return lignePaniers.stream()
                .anyMatch(ligne -> {
                    Produit produitLigne = ligne.getProduit();
                    return produitLigne != null &&
                            produitLigne.getIdProduit() != null &&
                            produitLigne.getIdProduit().equals(produit.getIdProduit());
                });
    }

    /**
     * Méthode pour obtenir la quantité d'un produit spécifique dans le panier
     */
    public Integer getQuantiteProduit(Produit produit) {
        if (produit == null || produit.getIdProduit() == null) {
            return 0;
        }

        LignePanier ligne = trouverLigneParProduit(produit);
        return ligne != null ? ligne.getQuantite() : 0;
    }

    /**
     * Méthode utilitaire pour l'affichage
     */
    public String getResumePanier() {
        if (estVide()) {
            return "Panier vide";
        }

        return String.format("Panier avec %d articles (%d produits différents) - Total: %.2f€",
                getNombreArticles(), getNombreProduitsDifferentes(), getTotal());
    }

    /**
     * Méthode pour valider le panier avant commande
     */
    public boolean estValidePourCommande() {
        if (estVide()) {
            return false;
        }

        // Vérifier que tous les produits sont en stock
        return lignePaniers.stream()
                .allMatch(ligne -> {
                    Produit produit = ligne.getProduit();
                    return produit != null &&
                            produit.estEnStock() &&
                            ligne.getQuantite() <= produit.getStock();
                });
    }

    /**
     * Méthode pour obtenir les produits en rupture de stock dans le panier
     */
    public List<Produit> getProduitsRuptureStock() {
        List<Produit> produitsRupture = new ArrayList<>();

        if (lignePaniers != null) {
            for (LignePanier ligne : lignePaniers) {
                Produit produit = ligne.getProduit();
                if (produit != null && !produit.estEnStock()) {
                    produitsRupture.add(produit);
                }
            }
        }

        return produitsRupture;
    }

    /**
     * Méthode pour obtenir les produits avec stock insuffisant
     */
    public List<Produit> getProduitsStockInsuffisant() {
        List<Produit> produitsStockInsuffisant = new ArrayList<>();

        if (lignePaniers != null) {
            for (LignePanier ligne : lignePaniers) {
                Produit produit = ligne.getProduit();
                if (produit != null && produit.getStock() < ligne.getQuantite()) {
                    produitsStockInsuffisant.add(produit);
                }
            }
        }

        return produitsStockInsuffisant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Panier)) return false;
        Panier panier = (Panier) o;
        return idPanier != null && idPanier.equals(panier.idPanier);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}