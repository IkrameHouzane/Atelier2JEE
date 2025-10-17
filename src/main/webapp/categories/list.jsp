<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="${empty categorie.idCategorie ? 'Nouvelle Catégorie' : 'Modifier Catégorie'}" />
<%@ include file="../includes/header.jsp" %>

<div class="row">
    <div class="col-6" style="margin: 0 auto;">
        <div class="card">
            <div class="card-header">
                <h2>${empty categorie.idCategorie ? '📂 Nouvelle Catégorie' : '✏️ Modifier Catégorie'}</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/categorie" method="post">
                    <input type="hidden" name="action" value="${empty categorie.idCategorie ? 'create' : 'update'}">
                    <c:if test="${not empty categorie.idCategorie}">
                        <input type="hidden" name="id" value="${categorie.idCategorie}">
                    </c:if>

                    <div class="form-group">
                        <label class="form-label" for="nomCategorie">Nom de la catégorie :</label>
                        <input type="text" class="form-control" id="nomCategorie" name="nomCategorie"
                               value="<c:out value='${categorie.nomCategorie}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="description">Description :</label>
                        <textarea class="form-control" id="description" name="description"
                                  rows="4"><c:out value="${categorie.description}" /></textarea>
                    </div>

                    <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                        <button type="submit" class="btn btn-primary">
                            ${empty categorie.idCategorie ? 'Créer' : 'Modifier'}
                        </button>
                        <a href="${pageContext.request.contextPath}/categorie" class="btn btn-secondary">
                            ↩️ Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>