package ma.fstt.webjpa2.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internaute")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Internaute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_internaute")
    private Long idInternaute;

    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "prenom", length = 100, nullable = false)
    private String prenom;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe", length = 255, nullable = false)
    private String motDePasse;

    @Column(name = "adresse_livraison", length = 500)
    private String adresseLivraison;

    // Relation One-to-Many avec Panier
    @OneToMany(mappedBy = "internaute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude  // Évite la récursion infinie dans toString()
    private List<Panier> paniers = new ArrayList<>();

    // Relation One-to-Many avec Commande
    @OneToMany(mappedBy = "internaute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Commande> commandes = new ArrayList<>();

    // Constructeur personnalisé
    public Internaute(String nom, String prenom, String email, String motDePasse, String adresseLivraison) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresseLivraison = adresseLivraison;
    }
}