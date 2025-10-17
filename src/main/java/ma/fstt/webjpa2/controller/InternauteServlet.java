package ma.fstt.webjpa2.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.fstt.webjpa2.model.Internaute;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "InternauteServlet", urlPatterns = {"/internaute"})
public class InternauteServlet extends HttpServlet {

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
                    deleteInternaute(request, response);
                    break;
                default:
                    listInternautes(request, response);
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
                    createInternaute(request, response);
                    break;
                case "update":
                    updateInternaute(request, response);
                    break;
                default:
                    listInternautes(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void listInternautes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = emf.createEntityManager();
        try {
            List<Internaute> internautes = em.createQuery(
                    "SELECT i FROM Internaute i ORDER BY i.nom, i.prenom",
                    Internaute.class
            ).getResultList();

            request.setAttribute("internautes", internautes);
            request.getRequestDispatcher("/internautes/list.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("internaute", new Internaute());
        request.getRequestDispatcher("/internautes/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            Internaute internaute = em.find(Internaute.class, id);

            if (internaute == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Internaute non trouvé");
                return;
            }

            request.setAttribute("internaute", internaute);
            request.getRequestDispatcher("/internautes/form.jsp").forward(request, response);

        } finally {
            em.close();
        }
    }

    private void createInternaute(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            String nom = request.getParameter("nom");
            String prenom = request.getParameter("prenom");
            String email = request.getParameter("email");
            String motDePasse = request.getParameter("motDePasse");
            String adresseLivraison = request.getParameter("adresseLivraison");

            Internaute internaute = new Internaute(nom, prenom, email, motDePasse, adresseLivraison);
            em.persist(internaute);

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/internaute?success=create");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/internaute?error=create");
        } finally {
            em.close();
        }
    }

    private void updateInternaute(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Internaute internaute = em.find(Internaute.class, id);

            if (internaute == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Internaute non trouvé");
                return;
            }

            internaute.setNom(request.getParameter("nom"));
            internaute.setPrenom(request.getParameter("prenom"));
            internaute.setEmail(request.getParameter("email"));
            internaute.setMotDePasse(request.getParameter("motDePasse"));
            internaute.setAdresseLivraison(request.getParameter("adresseLivraison"));

            em.merge(internaute);
            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/internaute?success=update");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/internaute?error=update");
        } finally {
            em.close();
        }
    }

    private void deleteInternaute(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = Long.parseLong(request.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Internaute internaute = em.find(Internaute.class, id);

            if (internaute != null) {
                em.remove(internaute);
            }

            em.getTransaction().commit();

            response.sendRedirect(request.getContextPath() + "/internaute?success=delete");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            response.sendRedirect(request.getContextPath() + "/internaute?error=delete");
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
