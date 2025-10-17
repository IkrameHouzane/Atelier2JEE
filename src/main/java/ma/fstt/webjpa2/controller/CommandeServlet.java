package ma.fstt.webjpa2.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.fstt.webjpa2.model.Commande;
import ma.fstt.webjpa2.model.LigneCommande;
import ma.fstt.webjpa2.model.Panier;
import ma.fstt.webjpa2.model.LignePanier;
import ma.fstt.webjpa2.model.Internaute;
import ma.fstt.webjpa2.model.Produit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CommandeServlet", urlPatterns = {"/commande"})
public class CommandeServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("mycnx");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    createFromCart(request, response);
                    break;
                case "view":
                    viewCommande(request, response);
                    break;
                case "confirm":
                    confirmCommande(request, response);
                    break;
                case "cancel":
                    cancelCommande(request, response);
                    break;
                case "history":
                    commandHistory(request, response);
                    break;
                default:
                    listCommandes(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "create":
                    createCommande(request, response);
                    break;
                case "updateStatus":
                    updateStatus(request, response);
                    break;
                default:
                    listCommandes(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Affiche la liste de toutes les commandes (pour admin)
     */
    private void listCommandes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer toutes les commandes avec leurs internautes
            List<Commande> commandes = em.createQuery(
                    "SELECT c FROM Commande c JOIN FETCH c.internaute ORDER BY c.dateCommande DESC",
                    Commande.class
            ).getResultList();

            request.setAttribute("commandes", commandes);
            request.getRequestDispatcher("/commandes/list.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    /**
     * Affiche l'historique des commandes d'un internaute
     */
    private void commandHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Long internauteId = (Long) session.getAttribute("internauteId");

        if (internauteId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer les commandes de l'internaute connecté
            List<Commande> commandes = em.createQuery(
                            "SELECT c FROM Commande c WHERE c.internaute.idInternaute = :internauteId ORDER BY c.dateCommande DESC",
                            Commande.class
                    ).setParameter("internauteId", internauteId)
                    .getResultList();

            request.setAttribute("commandes", commandes);
            request.getRequestDispatcher("/commandes/history.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    /**
     * Affiche le formulaire de création de commande à partir du panier
     */
    private void createFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Long internauteId = (Long) session.getAttribute("internauteId");

        if (internauteId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer le panier actif
            List<Panier> paniers = em.createQuery(
                            "SELECT p FROM Panier p LEFT JOIN FETCH p.lignePaniers WHERE p.internaute.idInternaute = :internauteId ORDER BY p.dateCreation DESC",
                            Panier.class
                    ).setParameter("internauteId", internauteId)
                    .setMaxResults(1)
                    .getResultList();

            if (paniers.isEmpty() || paniers.get(0).getLignePaniers().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/panier?error=empty");
                return;
            }

            Panier panier = paniers.get(0);
            request.setAttribute("panier", panier);
            request.setAttribute("internaute", panier.getInternaute());
            request.getRequestDispatcher("/commandes/confirm.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    /**
     * Crée une nouvelle commande à partir du panier
     */
    private void createCommande(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        Long internauteId = (Long) session.getAttribute("internauteId");

        if (internauteId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Récupérer l'internaute
            Internaute internaute = em.find(Internaute.class, internauteId);

            // Récupérer le panier actif
            List<Panier> paniers = em.createQuery(
                            "SELECT p FROM Panier p LEFT JOIN FETCH p.lignePaniers WHERE p.internaute.idInternaute = :internauteId ORDER BY p.dateCreation DESC",
                            Panier.class
                    ).setParameter("internauteId", internauteId)
                    .setMaxResults(1)
                    .getResultList();

            if (paniers.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/panier?error=empty");
                return;
            }

            Panier panier = paniers.get(0);

            // Vérifier le stock avant de créer la commande
            for (LignePanier ligne : panier.getLignePaniers()) {
                Produit produit = ligne.getProduit();
                if (produit.getStock() < ligne.getQuantite()) {
                    response.sendRedirect(request.getContextPath() + "/panier?error=stock&produit=" + produit.getNom());
                    return;
                }
            }

            // Créer la commande
            Commande commande = new Commande(internaute, "EN_ATTENTE");
            em.persist(commande);

            // Convertir les lignes panier en lignes commande
            for (LignePanier lignePanier : panier.getLignePaniers()) {
                LigneCommande ligneCommande = new LigneCommande(lignePanier, commande);
                em.persist(ligneCommande);
                commande.ajouterLigneCommande(ligneCommande);
            }

            // Calculer le total
            commande.recalculerTotal();

            // Vider le panier
            panier.viderPanier();
            em.merge(panier);

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commande.getIdCommande() + "&success=created");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/panier?error=create");
        } finally {
            em.close();
        }
    }

    /**
     * Affiche les détails d'une commande
     */
    private void viewCommande(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long commandeId = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer la commande avec ses lignes et produits
            Commande commande = em.createQuery(
                            "SELECT c FROM Commande c " +
                                    "LEFT JOIN FETCH c.ligneCommandes l " +
                                    "LEFT JOIN FETCH l.produit " +
                                    "LEFT JOIN FETCH c.internaute " +
                                    "WHERE c.idCommande = :id",
                            Commande.class
                    ).setParameter("id", commandeId)
                    .getSingleResult();

            request.setAttribute("commande", commande);
            request.getRequestDispatcher("/commandes/view.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Commande non trouvée");
        } finally {
            em.close();
        }
    }

    /**
     * Confirme une commande (passe de EN_ATTENTE à CONFIRMEE)
     */
    private void confirmCommande(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long commandeId = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Commande commande = em.find(Commande.class, commandeId);

            if (commande == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Commande non trouvée");
                return;
            }

            if (!"EN_ATTENTE".equals(commande.getStatut())) {
                response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&error=invalidStatus");
                return;
            }

            // Confirmer la commande (diminue les stocks)
            commande.confirmer();
            em.merge(commande);

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&success=confirmed");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&error=confirm");
        } finally {
            em.close();
        }
    }

    /**
     * Annule une commande
     */
    private void cancelCommande(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long commandeId = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Commande commande = em.find(Commande.class, commandeId);

            if (commande == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Commande non trouvée");
                return;
            }

            // Annuler la commande (réaugmente les stocks)
            commande.annuler();
            em.merge(commande);

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&success=cancelled");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&error=cancel");
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour le statut d'une commande (pour admin)
     */
    private void updateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long commandeId = Long.parseLong(request.getParameter("id"));
        String nouveauStatut = request.getParameter("statut");

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Commande commande = em.find(Commande.class, commandeId);

            if (commande == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Commande non trouvée");
                return;
            }

            // Validation des statuts possibles
            if (!isValidStatus(nouveauStatut)) {
                response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&error=invalidStatus");
                return;
            }

            // Si on passe à CONFIRMEE, diminuer les stocks
            if ("CONFIRMEE".equals(nouveauStatut) && !"CONFIRMEE".equals(commande.getStatut())) {
                commande.confirmer();
            }
            // Si on annule une commande confirmée, réaugmenter les stocks
            else if ("ANNULEE".equals(nouveauStatut) && "CONFIRMEE".equals(commande.getStatut())) {
                commande.annuler();
            }
            // Simple changement de statut
            else {
                commande.setStatut(nouveauStatut);
            }

            em.merge(commande);
            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&success=statusUpdated");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/commande?action=view&id=" + commandeId + "&error=updateStatus");
        } finally {
            em.close();
        }
    }

    /**
     * Valide qu'un statut est autorisé
     */
    private boolean isValidStatus(String statut) {
        return List.of("EN_ATTENTE", "CONFIRMEE", "EXPEDIEE", "LIVREE", "ANNULEE").contains(statut);
    }

    /**
     * Récupère les statistiques des commandes (pour dashboard admin)
     */
    private void getStats(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = emf.createEntityManager();
        try {
            // Nombre total de commandes
            Long totalCommandes = em.createQuery(
                    "SELECT COUNT(c) FROM Commande c", Long.class
            ).getSingleResult();

            // Chiffre d'affaires total
            Double chiffreAffaires = em.createQuery(
                    "SELECT SUM(c.total) FROM Commande c WHERE c.statut != 'ANNULEE'", Double.class
            ).getSingleResult();

            // Commandes en attente
            Long commandesEnAttente = em.createQuery(
                    "SELECT COUNT(c) FROM Commande c WHERE c.statut = 'EN_ATTENTE'", Long.class
            ).getSingleResult();

            request.setAttribute("totalCommandes", totalCommandes != null ? totalCommandes : 0);
            request.setAttribute("chiffreAffaires", chiffreAffaires != null ? chiffreAffaires : 0.0);
            request.setAttribute("commandesEnAttente", commandesEnAttente != null ? commandesEnAttente : 0);

            request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);

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
