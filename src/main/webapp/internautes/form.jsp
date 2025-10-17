<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="${empty internaute.idInternaute ? 'Nouvel Internaute' : 'Modifier Internaute'}" />
<%@ include file="../includes/header.jsp" %>

<div class="row">
    <div class="col-6" style="margin: 0 auto;">
        <div class="card">
            <div class="card-header">
                <h2>${empty internaute.idInternaute ? '👤 Nouvel Internaute' : '✏️ Modifier Internaute'}</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/internaute" method="post">
                    <input type="hidden" name="action" value="${empty internaute.idInternaute ? 'create' : 'update'}">
                    <c:if test="${not empty internaute.idInternaute}">
                        <input type="hidden" name="id" value="${internaute.idInternaute}">
                    </c:if>

                    <div class="form-group">
                        <label class="form-label" for="nom">Nom :</label>
                        <input type="text" class="form-control" id="nom" name="nom"
                               value="<c:out value='${internaute.nom}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="prenom">Prénom :</label>
                        <input type="text" class="form-control" id="prenom" name="prenom"
                               value="<c:out value='${internaute.prenom}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="email">Email :</label>
                        <input type="email" class="form-control" id="email" name="email"
                               value="<c:out value='${internaute.email}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="motDePasse">Mot de passe :</label>
                        <input type="password" class="form-control" id="motDePasse" name="motDePasse"
                               value="<c:out value='${internaute.motDePasse}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="adresseLivraison">Adresse de livraison :</label>
                        <textarea class="form-control" id="adresseLivraison" name="adresseLivraison"
                                  rows="3" required><c:out value="${internaute.adresseLivraison}" /></textarea>
                    </div>

                    <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                        <button type="submit" class="btn btn-primary">
                            ${empty internaute.idInternaute ? 'Créer' : 'Modifier'}
                        </button>
                        <a href="${pageContext.request.contextPath}/internaute" class="btn btn-secondary">
                            ↩️ Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>
