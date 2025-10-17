<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="Liste des Internautes" />
<%@ include file="../includes/header.jsp" %>

<div class="card">
    <div class="card-header" style="display: flex; justify-content: space-between; align-items: center;">
        <h2>👥 Liste des Internautes</h2>
        <a href="${pageContext.request.contextPath}/internaute?action=new" class="btn btn-primary">
            ➕ Ajouter un internaute
        </a>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty internautes}">
                <div style="text-align: center; padding: 2rem;">
                    <p>Aucun internaute inscrit.</p>
                </div>
            </c:when>
            <c:otherwise>
                <table class="table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Email</th>
                        <th>Adresse</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="internaute" items="${internautes}">
                        <tr>
                            <td><c:out value="${internaute.idInternaute}" /></td>
                            <td><c:out value="${internaute.nom}" /></td>
                            <td><c:out value="${internaute.prenom}" /></td>
                            <td><c:out value="${internaute.email}" /></td>
                            <td><c:out value="${internaute.adresseLivraison}" /></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/internaute?action=edit&id=${internaute.idInternaute}"
                                   class="btn btn-warning btn-sm">✏️</a>
                                <a href="${pageContext.request.contextPath}/internaute?action=delete&id=${internaute.idInternaute}"
                                   class="btn btn-danger btn-sm"
                                   onclick="return confirm('Êtes-vous sûr de vouloir supprimer cet internaute ?')">🗑️</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>
