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
@Table(name = "commande")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande")
    private Long idCommande;

    @Column(name = "date_commande", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCommande;

    @Column(name = "statut", length = 50, nullable = false)
    private String statut; // "EN_ATTENTE", "CONFIRMEE", "EXPEDIEE", "LIVREE", "ANNULEE"

    @Column(name = "total", nullable = false)
    private Double total;

    // Relation Many-to-One avec Internaute
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "internaute_id", nullable = false)
    private Internaute internaute;

    // Relation One-to-Many avec LigneCommande
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LigneCommande> ligneCommandes = new ArrayList<>();

    // Constructeur personnalisé
    public Commande(Internaute internaute, String statut) {
        this.internaute = internaute;
        this.statut = statut;
        this.dateCommande = new Date();
        this.total = 0.0;
    }

    // Méthode pour ajouter une ligne de commande
    public void ajouterLigneCommande(LigneCommande ligneCommande) {
        ligneCommandes.add(ligneCommande);
        ligneCommande.setCommande(this);
        recalculerTotal();
    }

    // Méthode pour recalculer le total
    public void recalculerTotal() {
        this.total = ligneCommandes.stream()
                .mapToDouble(LigneCommande::getSousTotal)
                .sum();
    }

    // Méthode pour confirmer la commande
    public void confirmer() {
        this.statut = "CONFIRMEE";
        // Diminuer les stocks des produits
        for (LigneCommande ligne : ligneCommandes) {
            Produit produit = ligne.getProduit();
            produit.diminuerStock(ligne.getQuantite());
        }
    }

    // Méthode pour annuler la commande
    public void annuler() {
        this.statut = "ANNULEE";
        // Réaugmenter les stocks
        for (LigneCommande ligne : ligneCommandes) {
            Produit produit = ligne.getProduit();
            produit.augmenterStock(ligne.getQuantite());
        }
    }
}