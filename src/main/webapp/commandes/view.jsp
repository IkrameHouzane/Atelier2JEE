<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="title" value="Détails de la Commande" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
        <h2>📦 Commande #<c:out value="${commande.idCommande}" /></h2>
        <span class="status-badge status-${commande.statut.toLowerCase()}">
            <c:out value="${commande.statut}" />
        </span>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h4>👤 Informations client</h4>
                    </div>
                    <div class="card-body">
                        <p><strong>Nom :</strong>
                            <c:out value="${commande.internaute.prenom}" /> <c:out value="${commande.internaute.nom}" />
                        </p>
                        <p><strong>Email :</strong> <c:out value="${commande.internaute.email}" /></p>
                        <p><strong>Adresse :</strong><br>
                            <c:out value="${commande.internaute.adresseLivraison}" />
                        </p>
                    </div>
                </div>
            </div>

            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h4>📅 Informations commande</h4>
                    </div>
                    <div class="card-body">
                        <p><strong>Date :</strong>
                            <fmt:formatDate value="${commande.dateCommande}" pattern="dd/MM/yyyy à HH:mm" />
                        </p>
                        <p><strong>Statut :</strong>
                            <span class="status-badge status-${commande.statut.toLowerCase()}">
                               <c:out value="${commande.statut}" />
                           </span>
                        </p>
                        <p><strong>Total :</strong>
                            <span style="font-size: 1.5rem; font-weight: bold; color: #667eea;">
                               <c:out value="${commande.total}" /> €
                           </span>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card" style="margin-top: 2rem;">
            <div class="card-header">
                <h4>🛍️ Détails des articles</h4>
            </div>
            <div class="card-body">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Produit</th>
                        <th>Prix unitaire</th>
                        <th>Quantité</th>
                        <th>Sous-total</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="ligne" items="${commande.ligneCommandes}">
                        <tr>
                            <td>
                                <strong><c:out value="${ligne.produit.nom}" /></strong>
                                <br>
                                <small style="color: #666;"><c:out value="${ligne.produit.description}" /></small>
                            </td>
                            <td><c:out value="${ligne.prixUnitaire}" /> €</td>
                            <td><c:out value="${ligne.quantite}" /></td>
                            <td style="font-weight: bold;">
                                <c:out value="${ligne.sousTotal}" /> €
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                    <tr style="background-color: #f8f9fa;">
                        <td colspan="3" style="text-align: right; font-weight: bold;">Total :</td>
                        <td style="font-weight: bold; font-size: 1.2rem;">
                            <c:out value="${commande.total}" /> €
                        </td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>

        <!-- Actions selon le statut -->
        <div class="card" style="margin-top: 2rem;">
            <div class="card-header">
                <h4>⚡ Actions</h4>
            </div>
            <div class="card-body">
                <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
                    <c:if test="${commande.statut == 'EN_ATTENTE'}">
                        <a href="${pageContext.request.contextPath}/commande?action=confirm&id=${commande.idCommande}"
                           class="btn btn-success"
                           onclick="return confirm('Confirmer cette commande ? Les stocks seront déduits.')">
                            ✅ Confirmer la commande
                        </a>
                        <a href="${pageContext.request.contextPath}/commande?action=cancel&id=${commande.idCommande}"
                           class="btn btn-danger"
                           onclick="return confirm('Annuler cette commande ?')">
                            ❌ Annuler la commande
                        </a>
                    </c:if>

                    <c:if test="${sessionScope.internauteEmail == 'admin@email.com' && commande.statut != 'ANNULEE' && commande.statut != 'LIVREE'}">
                        <div style="display: flex; gap: 0.5rem; align-items: center;">
                            <form action="${pageContext.request.contextPath}/commande" method="post" style="display: inline;">
                                <input type="hidden" name="action" value="updateStatus">
                                <input type="hidden" name="id" value="${commande.idCommande}">
                                <select name="statut" class="form-control" style="width: auto; display: inline;">
                                    <option value="EN_ATTENTE" ${commande.statut == 'EN_ATTENTE' ? 'selected' : ''}>En attente</option>
                                    <option value="CONFIRMEE" ${commande.statut == 'CONFIRMEE' ? 'selected' : ''}>Confirmée</option>
                                    <option value="EXPEDIEE" ${commande.statut == 'EXPEDIEE' ? 'selected' : ''}>Expédiée</option>
                                    <option value="LIVREE" ${commande.statut == 'LIVREE' ? 'selected' : ''}>Livrée</option>
                                </select>
                                <button type="submit" class="btn btn-warning">🔄 Mettre à jour</button>
                            </form>
                        </div>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/commande?action=history" class="btn btn-secondary">
                        ↩️ Retour à l'historique
                    </a>

                    <c:if test="${sessionScope.internauteEmail == 'admin@email.com'}">
                        <a href="${pageContext.request.contextPath}/commande" class="btn btn-primary">
                            📦 Toutes les commandes
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>
