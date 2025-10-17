package ma.fstt.webjpa2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ligne_commande")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne")
    private Long idLigne;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false)
    private Double prixUnitaire;

    // Relation Many-to-One avec Commande
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    @ToString.Exclude
    private Commande commande;

    // Relation Many-to-One avec Produit
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    // Constructeur personnalisé
    public LigneCommande(Commande commande, Produit produit, Integer quantite) {
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrix(); // Garder le prix au moment de la commande
    }

    // Constructeur à partir d'une LignePanier
    public LigneCommande(LignePanier lignePanier, Commande commande) {
        this.commande = commande;
        this.produit = lignePanier.getProduit();
        this.quantite = lignePanier.getQuantite();
        this.prixUnitaire = lignePanier.getPrixUnitaire();
    }

    // Méthode pour calculer le sous-total
    public Double getSousTotal() {
        return this.prixUnitaire * this.quantite;
    }
}