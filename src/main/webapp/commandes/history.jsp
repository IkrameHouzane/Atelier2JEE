<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="title" value="Mes Commandes" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header">
        <h2>📦 Historique de mes Commandes</h2>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty commandes}">
                <div style="text-align: center; padding: 3rem;">
                    <div style="font-size: 4rem; margin-bottom: 1rem;">📦</div>
                    <h3>Vous n'avez pas encore passé de commande</h3>
                    <p>Découvrez nos produits et faites votre première commande !</p>
                    <a href="${pageContext.request.contextPath}/produit" class="btn btn-primary">
                        🏷️ Voir les produits
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="commande" items="${commandes}">
                    <div class="card" style="margin-bottom: 1.5rem;">
                        <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
                            <div>
                                <h4>Commande #<c:out value="${commande.idCommande}" /></h4>
                                <small>
                                    <fmt:formatDate value="${commande.dateCommande}" pattern="dd/MM/yyyy à HH:mm" />
                                </small>
                            </div>
                            <div>
                                <span class="status-badge status-${commande.statut.toLowerCase()}">
                                    <c:out value="${commande.statut}" />
                                </span>
                                <span style="font-weight: bold; margin-left: 1rem;">
                                    <c:out value="${commande.total}" /> €
                                </span>
                            </div>
                        </div>
                        <div class="card-body">
                            <div style="display: grid; grid-template-columns: 2fr 1fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                                <div><strong>Produit</strong></div>
                                <div style="text-align: center;"><strong>Quantité</strong></div>
                                <div style="text-align: right;"><strong>Prix</strong></div>

                                <c:forEach var="ligne" items="${commande.ligneCommandes}">
                                    <div>
                                        <c:out value="${ligne.produit.nom}" />
                                        <br>
                                        <small style="color: #666;"><c:out value="${ligne.prixUnitaire}" /> €/unité</small>
                                    </div>
                                    <div style="text-align: center;">
                                        <c:out value="${ligne.quantite}" />
                                    </div>
                                    <div style="text-align: right; font-weight: bold;">
                                        <c:out value="${ligne.sousTotal}" /> €
                                    </div>
                                </c:forEach>
                            </div>

                            <div style="text-align: right; border-top: 1px solid #eee; padding-top: 1rem;">
                                <a href="${pageContext.request.contextPath}/commande?action=view&id=${commande.idCommande}"
                                   class="btn btn-primary btn-sm">
                                    👁️ Détails
                                </a>
                                <c:if test="${commande.statut == 'EN_ATTENTE'}">
                                    <a href="${pageContext.request.contextPath}/commande?action=cancel&id=${commande.idCommande}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Annuler cette commande ?')">
                                        ❌ Annuler
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../includes/header.jsp" %>
