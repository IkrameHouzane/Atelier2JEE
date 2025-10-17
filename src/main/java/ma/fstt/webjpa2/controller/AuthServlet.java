package ma.fstt.webjpa2.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.fstt.webjpa2.model.Internaute;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/logout"})
public class AuthServlet extends HttpServlet {

    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = Persistence.createEntityManagerFactory("mycnx");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            logout(request, response);
        } else {
            showLoginForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/login".equals(path)) {
            login(request, response);
        }
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }

    private void login(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        EntityManager em = emf.createEntityManager();
        try {
            // Étape 1 : Trouver l'utilisateur par email
            Internaute internaute = em.createQuery("SELECT i FROM Internaute i WHERE i.email = :email", Internaute.class)
                    .setParameter("email", email)
                    .getSingleResult();

            // Étape 2 : Vérifier le mot de passe
            if (internaute != null && internaute.getMotDePasse().equals(motDePasse)) {
                // Si l'authentification réussit
                HttpSession session = request.getSession();
                session.setAttribute("internauteId", internaute.getIdInternaute());
                session.setAttribute("internauteNom", internaute.getPrenom() + " " + internaute.getNom());
                session.setAttribute("internauteEmail", internaute.getEmail());

                // Redirection vers la page demandée ou l'accueil
                String redirect = request.getParameter("redirect");
                if (redirect != null && !redirect.isEmpty()) {
                    response.sendRedirect(request.getContextPath() + redirect);
                } else {
                    response.sendRedirect(request.getContextPath() + "/");
                }
            } else {
                // Mot de passe incorrect
                response.sendRedirect(request.getContextPath() + "/login?error=auth");
            }

        } catch (jakarta.persistence.NoResultException e) {
            // Aucun utilisateur trouvé avec cet email
            response.sendRedirect(request.getContextPath() + "/login?error=auth");
        } finally {
            em.close();
        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        session.invalidate(); // Destruction de la session

        response.sendRedirect(request.getContextPath() + "/login?success=logout");
    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
    }
}
