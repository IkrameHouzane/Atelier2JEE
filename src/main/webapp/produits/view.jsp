<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="Détails du Produit" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
        <h2>📦 Détails du Produit</h2>
        <div>
            <a href="${pageContext.request.contextPath}/produit" class="btn btn-secondary">
                ↩️ Retour à la liste
            </a>
            <c:if test="${sessionScope.internauteEmail == 'admin@email.com'}">
                <a href="${pageContext.request.contextPath}/produit?action=edit&id=${produit.idProduit}"
                   class="btn btn-warning">✏️ Modifier</a>
            </c:if>
        </div>
    </div>
    <div class="card-body">
        <div class="row">
            <!-- Image et informations principales -->
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h3><c:out value="${produit.nom}" /></h3>
                    </div>
                    <div class="card-body">
                        <div class="product-image" style="margin-bottom: 1.5rem;">
                            🏷️
                        </div>

                        <div style="margin-bottom: 1.5rem;">
                            <h4 style="color: #667eea; font-size: 2rem;">
                                <c:out value="${produit.prix}" /> €
                            </h4>

                            <c:choose>
                                <c:when test="${produit.estEnStock()}">
                                    <span class="badge" style="background: #28a745; color: white; padding: 0.5rem 1rem;">
                                        ✅ En stock (<c:out value="${produit.stock}" /> unités)
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge" style="background: #dc3545; color: white; padding: 0.5rem 1rem;">
                                        ❌ Rupture de stock
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Formulaire d'ajout au panier -->
                        <c:if test="${not empty sessionScope.internauteId && produit.estEnStock()}">
                            <div class="card" style="background: #f8f9fa;">
                                <div class="card-body">
                                    <h5>🛒 Ajouter au panier</h5>
                                    <form action="${pageContext.request.contextPath}/panier" method="post">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="produitId" value="${produit.idProduit}">

                                        <div style="display: flex; gap: 1rem; align-items: center;">
                                            <div class="form-group" style="margin-bottom: 0;">
                                                <label class="form-label" for="quantite">Quantité :</label>
                                                <input type="number" id="quantite" name="quantite"
                                                       value="1" min="1" max="${produit.stock}"
                                                       class="form-control" style="width: 100px;">
                                            </div>

                                            <button type="submit" class="btn btn-success" style="margin-top: 1.5rem;">
                                                🛒 Ajouter au panier
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${empty sessionScope.internauteId}">
                            <div class="alert alert-warning">
                                🔐 <a href="${pageContext.request.contextPath}/login">Connectez-vous</a> pour ajouter ce produit à votre panier.
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- Informations détaillées -->
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h4>📋 Description</h4>
                    </div>
                    <div class="card-body">
                        <p style="line-height: 1.6; font-size: 1.1rem;">
                            <c:choose>
                                <c:when test="${not empty produit.description}">
                                    <c:out value="${produit.description}" />
                                </c:when>
                                <c:otherwise>
                                    <em>Aucune description disponible pour ce produit.</em>
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>

                <div class="card" style="margin-top: 1.5rem;">
                    <div class="card-header">
                        <h4>📂 Informations techniques</h4>
                    </div>
                    <div class="card-body">
                        <table style="width: 100%;">
                            <tr>
                                <td style="padding: 0.5rem; font-weight: bold; width: 40%;">ID du produit :</td>
                                <td style="padding: 0.5rem;">#<c:out value="${produit.idProduit}" /></td>
                            </tr>
                            <tr>
                                <td style="padding: 0.5rem; font-weight: bold;">Catégorie :</td>
                                <td style="padding: 0.5rem;">
                                    <c:out value="${produit.categorie.nomCategorie}" />
                                    <c:if test="${not empty produit.categorie.description}">
                                        <br>
                                        <small style="color: #666;">
                                            <c:out value="${produit.categorie.description}" />
                                        </small>
                                    </c:if>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 0.5rem; font-weight: bold;">Prix :</td>
                                <td style="padding: 0.5rem; font-weight: bold; color: #667eea;">
                                    <c:out value="${produit.prix}" /> €
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 0.5rem; font-weight: bold;">Stock disponible :</td>
                                <td style="padding: 0.5rem;">
                                    <c:out value="${produit.stock}" /> unités
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 0.5rem; font-weight: bold;">Statut :</td>
                                <td style="padding: 0.5rem;">
                                    <c:choose>
                                        <c:when test="${produit.estEnStock()}">
                                            <span style="color: #28a745;">● Disponible</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #dc3545;">● Rupture de stock</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>

                <!-- Actions admin -->
                <c:if test="${sessionScope.internauteEmail == 'admin@email.com'}">
                    <div class="card" style="margin-top: 1.5rem; border-left: 4px solid #dc3545;">
                        <div class="card-header">
                            <h4>⚙️ Actions administrateur</h4>
                        </div>
                        <div class="card-body">
                            <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                <a href="${pageContext.request.contextPath}/produit?action=edit&id=${produit.idProduit}"
                                   class="btn btn-warning btn-sm">
                                    ✏️ Modifier
                                </a>
                                <a href="${pageContext.request.contextPath}/produit?action=delete&id=${produit.idProduit}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce produit ? Cette action est irréversible.')">
                                    🗑️ Supprimer
                                </a>
                                <a href="${pageContext.request.contextPath}/produit"
                                   class="btn btn-secondary btn-sm">
                                    📦 Tous les produits
                                </a>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Produits similaires (même catégorie) -->
        <c:if test="${not empty produitsSimilaires}">
            <div class="card" style="margin-top: 2rem;">
                <div class="card-header">
                    <h4>🛍️ Produits similaires</h4>
                    <small>Autres produits dans la catégorie <c:out value="${produit.categorie.nomCategorie}" /></small>
                </div>
                <div class="card-body">
                    <div class="product-grid">
                        <c:forEach var="produitSimilaire" items="${produitsSimilaires}">
                            <div class="product-card">
                                <div class="product-image">
                                    🏷️
                                </div>
                                <div class="product-info">
                                    <h5><c:out value="${produitSimilaire.nom}" /></h5>
                                    <p style="color: #666; font-size: 0.9rem; margin: 0.5rem 0;">
                                        <c:out value="${produitSimilaire.description}" />
                                    </p>
                                    <div class="product-price">
                                        <c:out value="${produitSimilaire.prix}" /> €
                                    </div>
                                    <div style="margin-top: 1rem;">
                                        <a href="${pageContext.request.contextPath}/produit?action=view&id=${produitSimilaire.idProduit}"
                                           class="btn btn-primary btn-sm">👁️ Voir</a>
                                        <c:if test="${produitSimilaire.estEnStock() && not empty sessionScope.internauteId}">
                                            <form action="${pageContext.request.contextPath}/panier" method="post" style="display: inline;">
                                                <input type="hidden" name="action" value="add">
                                                <input type="hidden" name="produitId" value="${produitSimilaire.idProduit}">
                                                <input type="hidden" name="quantite" value="1">
                                                <button type="submit" class="btn btn-success btn-sm">🛒</button>
                                            </form>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>

<style>
    .badge {
        display: inline-block;
        padding: 0.5rem 1rem;
        border-radius: 20px;
        font-weight: 600;
        font-size: 0.9rem;
    }

    .product-image {
        height: 250px;
        background: linear-gradient(45deg, #667eea, #764ba2);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 4rem;
        border-radius: 8px;
    }

    .table-details {
        width: 100%;
        border-collapse: collapse;
    }

    .table-details tr {
        border-bottom: 1px solid #eee;
    }

    .table-details td {
        padding: 0.75rem;
        vertical-align: top;
    }

    .table-details tr:last-child {
        border-bottom: none;
    }
</style>

<%@ include file="../includes/footer.jsp" %>