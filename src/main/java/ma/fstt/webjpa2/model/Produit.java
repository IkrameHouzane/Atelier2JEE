package ma.fstt.webjpa2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long idProduit;

    @Column(name = "nom", length = 200, nullable = false)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "prix", nullable = false)
    private Double prix;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    // Relation Many-to-One avec Categorie
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    // Relation One-to-Many avec LignePanier
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LignePanier> lignePaniers = new ArrayList<>();

    // Relation One-to-Many avec LigneCommande
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LigneCommande> ligneCommandes = new ArrayList<>();

    // Constructeur personnalisé
    public Produit(String nom, String description, Double prix, Integer stock, Categorie categorie) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
        this.categorie = categorie;
    }

    // Méthode utilitaire pour vérifier le stock
    public boolean estEnStock() {
        return this.stock > 0;
    }

    // Méthode pour diminuer le stock
    public void diminuerStock(Integer quantite) {
        if (this.stock >= quantite) {
            this.stock -= quantite;
        } else {
            throw new IllegalArgumentException("Stock insuffisant");
        }
    }

    // Méthode pour augmenter le stock
    public void augmenterStock(Integer quantite) {
        this.stock += quantite;
    }
}
