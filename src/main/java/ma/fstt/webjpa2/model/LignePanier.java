package ma.fstt.webjpa2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ligne_panier")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne")
    private Long idLigne;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "prix_unitaire", nullable = false)
    private Double prixUnitaire;

    // Relation Many-to-One avec Panier
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panier_id", nullable = false)
    @ToString.Exclude
    private Panier panier;

    // Relation Many-to-One avec Produit
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    // Constructeur personnalisé
    public LignePanier(Panier panier, Produit produit, Integer quantite) {
        this.panier = panier;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrix(); // Garder le prix au moment de l'ajout
    }

    // Méthode pour calculer le sous-total de cette ligne
    public Double getSousTotal() {
        return this.prixUnitaire * this.quantite;
    }

    // Méthode pour mettre à jour la quantité
    public void mettreAJourQuantite(Integer nouvelleQuantite) {
        if (nouvelleQuantite <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive");
        }
        this.quantite = nouvelleQuantite;
    }
}
