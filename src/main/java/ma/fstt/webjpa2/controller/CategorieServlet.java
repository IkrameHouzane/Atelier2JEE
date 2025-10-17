package ma.fstt.webjpa2.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.fstt.webjpa2.model.Categorie;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CategorieServlet", urlPatterns = {"/categorie"})
public class CategorieServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("mycnx");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteCategorie(request, response);
                    break;
                default:
                    listCategories(request, response);
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
                    createCategorie(request, response);
                    break;
                case "update":
                    updateCategorie(request, response);
                    break;
                default:
                    listCategories(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = emf.createEntityManager();
        try {
            List<Categorie> categories = em.createQuery(
                    "SELECT c FROM Categorie c ORDER BY c.nomCategorie",
                    Categorie.class
            ).getResultList();

            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/categories/list.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("categorie", new Categorie());
        request.getRequestDispatcher("/categories/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            Categorie categorie = em.find(Categorie.class, id);

            if (categorie == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Catégorie non trouvée");
                return;
            }

            request.setAttribute("categorie", categorie);
            request.getRequestDispatcher("/categories/form.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    private void createCategorie(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            String nomCategorie = request.getParameter("nomCategorie");
            String description = request.getParameter("description");

            Categorie categorie = new Categorie(nomCategorie, description);
            em.persist(categorie);

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/categorie?success=create");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/categorie?error=create");
        } finally {
            em.close();
        }
    }

    private void updateCategorie(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Categorie categorie = em.find(Categorie.class, id);

            if (categorie == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Catégorie non trouvée");
                return;
            }

            categorie.setNomCategorie(request.getParameter("nomCategorie"));
            categorie.setDescription(request.getParameter("description"));

            em.merge(categorie);
            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/categorie?success=update");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/categorie?error=update");
        } finally {
            em.close();
        }
    }

    private void deleteCategorie(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Categorie categorie = em.find(Categorie.class, id);

            if (categorie != null) {
                // Vérifier si la catégorie est utilisée par des produits
                Long countProduits = em.createQuery(
                        "SELECT COUNT(p) FROM Produit p WHERE p.categorie.idCategorie = :id",
                        Long.class
                ).setParameter("id", id).getSingleResult();

                if (countProduits > 0) {
                    response.sendRedirect(request.getContextPath() + "/categorie?error=hasProducts");
                    return;
                }

                em.remove(categorie);
            }

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/categorie?success=delete");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/categorie?error=delete");
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
