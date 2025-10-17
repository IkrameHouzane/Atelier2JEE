
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="Confirmation de Commande" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header">
        <h2>✅ Confirmation de Commande</h2>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h4>👤 Informations de livraison</h4>
                    </div>
                    <div class="card-body">
                        <p><strong>Client :</strong>
                            <c:out value="${internaute.prenom}" /> <c:out value="${internaute.nom}" />
                        </p>
                        <p><strong>Email :</strong> <c:out value="${internaute.email}" /></p>
                        <p><strong>Adresse de livraison :</strong><br>
                            <c:out value="${internaute.adresseLivraison}" />
                        </p>
                    </div>
                </div>
            </div>

            <div class="col-6">
                <div class="card">
                    <div class="card-header">
                        <h4>📦 Récapitulatif de la commande</h4>
                    </div>
                    <div class="card-body">
                        <c:forEach var="ligne" items="${panier.lignePaniers}">
                            <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                                <div>
                                    <c:out value="${ligne.produit.nom}" /> × <c:out value="${ligne.quantite}" />
                                </div>
                                <div style="font-weight: bold;">
                                    <c:out value="${ligne.sousTotal}" /> €
                                </div>
                            </div>
                        </c:forEach>

                        <hr>
                        <div style="display: flex; justify-content: space-between; font-size: 1.2rem; font-weight: bold;">
                            <div>Total :</div>
                            <div><c:out value="${panier.total}" /> €</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="card" style="margin-top: 2rem;">
            <div class="card-header">
                <h4>📝 Finaliser la commande</h4>
            </div>
            <div class="card-body">
                <p>Vérifiez que toutes les informations sont correctes avant de confirmer votre commande.</p>

                <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                    <form action="${pageContext.request.contextPath}/commande" method="post" style="display: inline;">
                        <input type="hidden" name="action" value="create">
                        <button type="submit" class="btn btn-success btn-lg">
                            ✅ Confirmer la commande
                        </button>
                    </form>

                    <a href="${pageContext.request.contextPath}/panier" class="btn btn-secondary">
                        ↩️ Retour au panier
                    </a>

                    <a href="${pageContext.request.contextPath}/produit" class="btn btn-primary">
                        ➕ Continuer les achats
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>