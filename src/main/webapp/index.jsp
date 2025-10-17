<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="Accueil" />
<%@ include file="includes/header.jsp" %>

<div class="card">
    <div class="card-header">
        <h1>🛍️ Bienvenue sur notre E-Commerce</h1>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty sessionScope.internauteId}">
                <p>Bonjour <strong><c:out value="${sessionScope.internauteNom}" /></strong> !</p>
                <p>Nous sommes ravis de vous revoir. Parcourez notre catalogue et trouvez les meilleurs produits.</p>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <a href="${pageContext.request.contextPath}/produit" class="btn btn-primary">
                        🛒 Voir tous les produits
                    </a>
                    <a href="${pageContext.request.contextPath}/panier" class="btn btn-success">
                        🛍️ Mon panier
                    </a>
                    <a href="${pageContext.request.contextPath}/commande?action=history" class="btn btn-warning">
                        📦 Mes commandes
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <p>Découvrez notre sélection exclusive de produits. Connectez-vous pour profiter de nos services.</p>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <a href="${pageContext.request.contextPath}/produit" class="btn btn-primary">
                        🛒 Voir les produits
                    </a>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-success">
                        🔐 Se connecter
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Statistiques rapides -->
<c:if test="${sessionScope.internauteEmail == 'admin@email.com'}">
    <div class="row">
        <div class="col-6">
            <div class="card">
                <div class="card-header">
                    <h3>📊 Tableau de bord Admin</h3>
                </div>
                <div class="card-body">
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                        <div style="text-align: center; padding: 1rem; background: #e3f2fd; border-radius: 8px;">
                            <h4>Produits</h4>
                            <a href="${pageContext.request.contextPath}/produit" class="btn btn-sm btn-primary">Gérer</a>
                        </div>
                        <div style="text-align: center; padding: 1rem; background: #e8f5e8; border-radius: 8px;">
                            <h4>Catégories</h4>
                            <a href="${pageContext.request.contextPath}/categorie" class="btn btn-sm btn-success">Gérer</a>
                        </div>
                        <div style="text-align: center; padding: 1rem; background: #fff3cd; border-radius: 8px;">
                            <h4>Utilisateurs</h4>
                            <a href="${pageContext.request.contextPath}/internaute" class="btn btn-sm btn-warning">Gérer</a>
                        </div>
                        <div style="text-align: center; padding: 1rem; background: #f8d7da; border-radius: 8px;">
                            <h4>Commandes</h4>
                            <a href="${pageContext.request.contextPath}/commande" class="btn btn-sm btn-danger">Gérer</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<%@ include file="includes/footer.jsp" %>