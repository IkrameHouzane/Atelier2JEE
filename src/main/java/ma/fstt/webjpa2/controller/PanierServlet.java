package ma.fstt.webjpa2.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.fstt.webjpa2.model.Panier;
import ma.fstt.webjpa2.model.LignePanier;
import ma.fstt.webjpa2.model.Produit;
import ma.fstt.webjpa2.model.Internaute;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PanierServlet", urlPatterns = {"/panier"})
public class PanierServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("mycnx");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "view";

        try {
            switch (action) {
                case "add":
                    addToCart(request, response);
                    break;
                case "remove":
                    removeFromCart(request, response);
                    break;
                case "update":
                    updateCartItem(request, response);
                    break;
                case "clear":
                    clearCart(request, response);
                    break;
                default:
                    viewCart(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Ajoute un produit au panier
     */
    private void addToCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long produitId = Long.parseLong(request.getParameter("produitId"));
        Integer quantite = Integer.parseInt(request.getParameter("quantite"));

        HttpSession session = request.getSession();
        Long internauteId = (Long) session.getAttribute("internauteId");

        if (internauteId == null) {
            // Rediriger vers la page de connexion
            response.sendRedirect(request.getContextPath() + "/login?redirect=panier");
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Récupérer l'internaute et le produit
            Internaute internaute = em.find(Internaute.class, internauteId);
            Produit produit = em.find(Produit.class, produitId);

            if (produit == null || !produit.estEnStock()) {
                response.sendRedirect(request.getContextPath() + "/produit?error=outOfStock");
                return;
            }

            // Trouver le panier actif de l'internaute
            Panier panier = findOrCreatePanier(em, internaute);

            // Ajouter le produit au panier
            panier.ajouterProduit(produit, quantite);

            em.merge(panier);
            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/panier?success=added");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/produit?error=addToCart");
        } finally {
            em.close();
        }
    }

    /**
     * Trouve ou crée un panier pour l'internaute
     */
    private Panier findOrCreatePanier(EntityManager em, Internaute internaute) {
        // Chercher un panier actif (non converti en commande)
        List<Panier> paniers = em.createQuery(
                        "SELECT p FROM Panier p WHERE p.internaute.idInternaute = :internauteId ORDER BY p.dateCreation DESC",
                        Panier.class
                ).setParameter("internauteId", internaute.getIdInternaute())
                .setMaxResults(1)
                .getResultList();

        if (paniers.isEmpty()) {
            // Créer un nouveau panier
            Panier nouveauPanier = new Panier(internaute);
            em.persist(nouveauPanier);
            return nouveauPanier;
        } else {
            return paniers.get(0);
        }
    }

    /**
     * Affiche le contenu du panier
     */
    private void viewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Long internauteId = (Long) session.getAttribute("internauteId");

        if (internauteId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer le panier actif avec ses lignes
            List<Panier> paniers = em.createQuery(
                            "SELECT p FROM Panier p LEFT JOIN FETCH p.lignePaniers WHERE p.internaute.idInternaute = :internauteId ORDER BY p.dateCreation DESC",
                            Panier.class
                    ).setParameter("internauteId", internauteId)
                    .setMaxResults(1)
                    .getResultList();

            Panier panier = paniers.isEmpty() ? new Panier() : paniers.get(0);

            request.setAttribute("panier", panier);
            request.getRequestDispatcher("/panier/view.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    /**
     * Supprime un article du panier
     */
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long ligneId = Long.parseLong(request.getParameter("ligneId"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            LignePanier ligne = em.find(LignePanier.class, ligneId);

            if (ligne != null) {
                em.remove(ligne);
            }

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/panier?success=removed");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/panier?error=remove");
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour la quantité d'un article
     */
    private void updateCartItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long ligneId = Long.parseLong(request.getParameter("ligneId"));
        Integer nouvelleQuantite = Integer.parseInt(request.getParameter("quantite"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            LignePanier ligne = em.find(LignePanier.class, ligneId);

            if (ligne != null) {
                ligne.mettreAJourQuantite(nouvelleQuantite);
                em.merge(ligne);
            }

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/panier?success=updated");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/panier?error=update");
        } finally {
            em.close();
        }
    }

    /**
     * Vide complètement le panier
     */
    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        Long internauteId = (Long) session.getAttribute("internauteId");

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Trouver le panier actif
            List<Panier> paniers = em.createQuery(
                            "SELECT p FROM Panier p WHERE p.internaute.idInternaute = :internauteId ORDER BY p.dateCreation DESC",
                            Panier.class
                    ).setParameter("internauteId", internauteId)
                    .setMaxResults(1)
                    .getResultList();

            if (!paniers.isEmpty()) {
                Panier panier = paniers.get(0);
                panier.viderPanier();
                em.merge(panier);
            }

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/panier?success=cleared");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/panier?error=clear");
        } finally {
            em.close();
        }
    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
    }
}