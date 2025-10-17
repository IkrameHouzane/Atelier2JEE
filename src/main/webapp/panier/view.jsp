<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="Mon Panier" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
        <h2>🛒 Mon Panier</h2>
        <c:if test="${not empty panier.lignePaniers && !panier.lignePaniers.isEmpty()}">
            <a href="${pageContext.request.contextPath}/panier?action=clear"
               class="btn btn-danger"
               onclick="return confirm('Êtes-vous sûr de vouloir vider votre panier ?')">
                🗑️ Vider le panier
            </a>
        </c:if>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty panier.lignePaniers || panier.lignePaniers.isEmpty()}">
                <div style="text-align: center; padding: 3rem;">
                    <div style="font-size: 4rem; margin-bottom: 1rem;">🛒</div>
                    <h3>Votre panier est vide</h3>
                    <p>Découvrez nos produits et ajoutez-les à votre panier !</p>
                    <a href="${pageContext.request.contextPath}/produit" class="btn btn-primary">
                        🏷️ Voir les produits
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="ligne" items="${panier.lignePaniers}">
                    <div class="cart-item">
                        <div style="flex: 2;">
                            <h4><c:out value="${ligne.produit.nom}" /></h4>
                            <p style="color: #666; margin: 0;"><c:out value="${ligne.produit.description}" /></p>
                            <div style="color: #667eea; font-weight: bold; margin-top: 0.5rem;">
                                <c:out value="${ligne.prixUnitaire}" /> € / unité
                            </div>
                        </div>

                        <div style="flex: 1; text-align: center;">
                            <form action="${pageContext.request.contextPath}/panier" method="post" style="display: inline;">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="ligneId" value="${ligne.idLigne}">
                                <input type="number" name="quantite" value="${ligne.quantite}"
                                       min="1" max="${ligne.produit.stock + ligne.quantite}"
                                       style="width: 80px; padding: 0.5rem;">
                                <button type="submit" class="btn btn-warning btn-sm">🔄</button>
                            </form>
                        </div>

                        <div style="flex: 1; text-align: center; font-weight: bold;">
                            <c:out value="${ligne.sousTotal}" /> €
                        </div>

                        <div style="flex: 0.5; text-align: center;">
                            <a href="${pageContext.request.contextPath}/panier?action=remove&ligneId=${ligne.idLigne}"
                               class="btn btn-danger btn-sm"
                               onclick="return confirm('Retirer ce produit du panier ?')">
                                ❌
                            </a>
                        </div>
                    </div>
                </c:forEach>

                <div class="cart-total">
                    <div style="font-size: 1.5rem; font-weight: bold;">
                        Total : <c:out value="${panier.total}" /> €
                    </div>
                    <div style="margin-top: 0.5rem; color: #666;">
                        <c:out value="${panier.nombreArticles}" /> articles
                    </div>

                    <div style="margin-top: 2rem;">
                        <a href="${pageContext.request.contextPath}/commande?action=new"
                           class="btn btn-success btn-lg">
                            ✅ Passer la commande
                        </a>
                        <a href="${pageContext.request.contextPath}/produit"
                           class="btn btn-primary">
                            ➕ Continuer mes achats
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>
