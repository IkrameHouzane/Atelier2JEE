<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="title" value="Liste des Commandes" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header">
        <h2>📦 Liste des Commandes</h2>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty commandes}">
                <div style="text-align: center; padding: 2rem;">
                    <p>Aucune commande passée.</p>
                </div>
            </c:when>
            <c:otherwise>
                <table class="table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Client</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Statut</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="commande" items="${commandes}">
                        <tr>
                            <td>#<c:out value="${commande.idCommande}" /></td>
                            <td>
                                <c:out value="${commande.internaute.prenom}" />
                                <c:out value="${commande.internaute.nom}" />
                                <br>
                                <small style="color: #666;"><c:out value="${commande.internaute.email}" /></small>
                            </td>
                            <td>
                                <fmt:formatDate value="${commande.dateCommande}" pattern="dd/MM/yyyy HH:mm" />
                            </td>
                            <td style="font-weight: bold;">
                                <c:out value="${commande.total}" /> €
                            </td>
                            <td>
                                    <span class="status-badge status-${commande.statut.toLowerCase()}">
                                        <c:out value="${commande.statut}" />
                                    </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/commande?action=view&id=${commande.idCommande}"
                                   class="btn btn-primary btn-sm">👁️</a>
                                <c:if test="${commande.statut == 'EN_ATTENTE'}">
                                    <a href="${pageContext.request.contextPath}/commande?action=confirm&id=${commande.idCommande}"
                                       class="btn btn-success btn-sm"
                                       onclick="return confirm('Confirmer cette commande ?')">✅</a>
                                    <a href="${pageContext.request.contextPath}/commande?action=cancel&id=${commande.idCommande}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Annuler cette commande ?')">❌</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<style>
    .status-badge {
        padding: 0.25rem 0.75rem;
        border-radius: 20px;
        font-size: 0.875rem;
        font-weight: 600;
    }

    .status-en_attente {
        background: #fff3cd;
        color: #856404;
        border: 1px solid #ffeaa7;
    }

    .status-confirmee {
        background: #d4edda;
        color: #155724;
        border: 1px solid #c3e6cb;
    }

    .status-expediee {
        background: #cce7ff;
        color: #004085;
        border: 1px solid #b3d7ff;
    }

    .status-livree {
        background: #d1ecf1;
        color: #0c5460;
        border: 1px solid #bee5eb;
    }

    .status-annulee {
        background: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
    }
</style>

<%@ include file="../includes/footer.jsp" %>
