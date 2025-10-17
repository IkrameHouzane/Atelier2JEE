<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="Liste des Produits" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
        <h2>📦 Liste des Produits</h2>
        <c:if test="${sessionScope.internauteEmail == 'admin@email.com'}">
            <a href="${pageContext.request.contextPath}/produit?action=new" class="btn btn-primary">
                ➕ Ajouter un produit
            </a>
        </c:if>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty produits}">
                <div style="text-align: center; padding: 2rem;">
                    <p>Aucun produit disponible pour le moment.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="product-grid">
                    <c:forEach var="produit" items="${produits}">
                        <div class="product-card">
                            <div class="product-image">
                                🏷️
                            </div>
                            <div class="product-info">
                                <h3><c:out value="${produit.nom}" /></h3>
                                <p style="color: #666; margin: 0.5rem 0;">
                                    <c:out value="${produit.description}" />
                                </p>
                                <div class="product-price">
                                    <c:out value="${produit.prix}" /> €
                                </div>
                                <div class="product-stock">
                                    Stock : <c:out value="${produit.stock}" /> unités
                                </div>
                                <div style="margin-top: 1rem;">
                                    <c:if test="${produit.estEnStock()}">
                                        <form action="${pageContext.request.contextPath}/panier" method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="add">
                                            <input type="hidden" name="produitId" value="${produit.idProduit}">
                                            <input type="number" name="quantite" value="1" min="1" max="${produit.stock}"
                                                   style="width: 60px; padding: 0.25rem; margin-right: 0.5rem;">
                                            <button type="submit" class="btn btn-success btn-sm">🛒 Ajouter</button>
                                        </form>
                                    </c:if>

                                    <c:if test="${sessionScope.internauteEmail == 'admin@email.com'}">
                                        <div style="margin-top: 0.5rem;">
                                            <a href="${pageContext.request.contextPath}/produit?action=edit&id=${produit.idProduit}"
                                               class="btn btn-warning btn-sm">✏️ Modifier</a>
                                            <a href="${pageContext.request.contextPath}/produit?action=delete&id=${produit.idProduit}"
                                               class="btn btn-danger btn-sm"
                                               onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')">🗑️ Supprimer</a>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>
