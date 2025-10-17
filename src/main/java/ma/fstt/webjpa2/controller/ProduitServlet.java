package ma.fstt.webjpa2.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.fstt.webjpa2.model.Produit;
import ma.fstt.webjpa2.model.Categorie;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProduitServlet", urlPatterns = {"/produit"})
public class ProduitServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        // Initialisation de l'EntityManagerFactory
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
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteProduit(request, response);
                    break;
                case "view":
                    viewProduit(request, response);
                    break;
                default:
                    listProduits(request, response);
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
                    createProduit(request, response);
                    break;
                case "update":
                    updateProduit(request, response);
                    break;
                default:
                    listProduits(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Affiche la liste de tous les produits
     */
    private void listProduits(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer tous les produits avec leurs catégories
            List<Produit> produits = em.createQuery(
                    "SELECT p FROM Produit p LEFT JOIN FETCH p.categorie ORDER BY p.idProduit",
                    Produit.class
            ).getResultList();

            request.setAttribute("produits", produits);
            request.getRequestDispatcher("/produits/list.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    /**
     * Affiche le formulaire de création d'un produit
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = emf.createEntityManager();
        try {
            // Récupérer toutes les catégories pour le select
            List<Categorie> categories = em.createQuery(
                    "SELECT c FROM Categorie c ORDER BY c.nomCategorie",
                    Categorie.class
            ).getResultList();

            request.setAttribute("categories", categories);
            request.setAttribute("produit", new Produit()); // Produit vide pour le formulaire
            request.getRequestDispatcher("/produits/form.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    /**
     * Affiche le formulaire d'édition d'un produit
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            Produit produit = em.find(Produit.class, id);

            if (produit == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produit non trouvé");
                return;
            }

            // Récupérer toutes les catégories
            List<Categorie> categories = em.createQuery(
                    "SELECT c FROM Categorie c ORDER BY c.nomCategorie",
                    Categorie.class
            ).getResultList();

            request.setAttribute("produit", produit);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/produits/form.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    /**
     * Crée un nouveau produit
     */
    private void createProduit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Récupération des paramètres du formulaire
            String nom = request.getParameter("nom");
            String description = request.getParameter("description");
            Double prix = Double.parseDouble(request.getParameter("prix"));
            Integer stock = Integer.parseInt(request.getParameter("stock"));
            Long categorieId = Long.parseLong(request.getParameter("categorie_id"));

            // Récupération de la catégorie
            Categorie categorie = em.find(Categorie.class, categorieId);

            if (categorie == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Catégorie non trouvée");
                return;
            }

            // Création du produit
            Produit produit = new Produit(nom, description, prix, stock, categorie);
            em.persist(produit);

            em.getTransaction().commit();

            // Redirection vers la liste avec message de succès
            response.sendRedirect(request.getContextPath() + "/produit?success=create");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/produit?error=create");
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour un produit existant
     */
    private void updateProduit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Récupération du produit à modifier
            Produit produit = em.find(Produit.class, id);

            if (produit == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produit non trouvé");
                return;
            }

            // Mise à jour des informations
            produit.setNom(request.getParameter("nom"));
            produit.setDescription(request.getParameter("description"));
            produit.setPrix(Double.parseDouble(request.getParameter("prix")));
            produit.setStock(Integer.parseInt(request.getParameter("stock")));

            Long categorieId = Long.parseLong(request.getParameter("categorie_id"));
            Categorie categorie = em.find(Categorie.class, categorieId);
            produit.setCategorie(categorie);

            em.merge(produit);
            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/produit?success=update");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/produit?error=update");
        } finally {
            em.close();
        }
    }

    /**
     * Supprime un produit
     */
    private void deleteProduit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Produit produit = em.find(Produit.class, id);

            if (produit != null) {
                em.remove(produit);
            }

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/produit?success=delete");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/produit?error=delete");
        } finally {
            em.close();
        }
    }

    /**
     * Affiche les détails d'un produit
     */
    private void viewProduit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            Produit produit = em.find(Produit.class, id);

            if (produit == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Produit non trouvé");
                return;
            }

            request.setAttribute("produit", produit);
            request.getRequestDispatcher("/produits/view.jsp").forward(request, response);

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
