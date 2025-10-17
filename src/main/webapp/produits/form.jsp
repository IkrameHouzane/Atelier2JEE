<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="${empty produit.idProduit ? 'Nouveau Produit' : 'Modifier Produit'}" />
<%@ include file="../includes/header.jsp" %>

<div class="row">
    <div class="col-6" style="margin: 0 auto;">
        <div class="card">
            <div class="card-header">
                <h2>${empty produit.idProduit ? '➕ Nouveau Produit' : '✏️ Modifier Produit'}</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/produit" method="post">
                    <input type="hidden" name="action" value="${empty produit.idProduit ? 'create' : 'update'}">
                    <c:if test="${not empty produit.idProduit}">
                        <input type="hidden" name="id" value="${produit.idProduit}">
                    </c:if>

                    <div class="form-group">
                        <label class="form-label" for="nom">Nom :</label>
                        <input type="text" class="form-control" id="nom" name="nom"
                               value="<c:out value='${produit.nom}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="description">Description :</label>
                        <textarea class="form-control" id="description" name="description" rows="3"><c:out value="${produit.description}" /></textarea>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="prix">Prix (€) :</label>
                        <input type="number" step="0.01" class="form-control" id="prix" name="prix"
                               value="<c:out value='${produit.prix}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="stock">Stock :</label>
                        <input type="number" class="form-control" id="stock" name="stock"
                               value="<c:out value='${produit.stock}' />" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="categorie_id">Catégorie :</label>
                        <select class="form-control" id="categorie_id" name="categorie_id" required>
                            <option value="">Sélectionnez une catégorie</option>
                            <c:forEach var="categorie" items="${categories}">
                                <option value="${categorie.idCategorie}"
                                    ${produit.categorie.idCategorie == categorie.idCategorie ? 'selected' : ''}>
                                    <c:out value="${categorie.nomCategorie}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div style="display: flex; gap: 1rem; margin-top: 2rem;">
                        <button type="submit" class="btn btn-primary">
                            ${empty produit.idProduit ? 'Créer' : 'Modifier'}
                        </button>
                        <a href="${pageContext.request.contextPath}/produit" class="btn btn-secondary">
                            ↩️ Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>
