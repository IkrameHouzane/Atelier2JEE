<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="${empty categorie.idCategorie ? 'Nouvelle Catégorie' : 'Modifier Catégorie'}" />
<%@ include file="../includes/header.jsp" %>

<div class="row">
    <div class="col-6" style="margin: 0 auto;">
        <div class="card">
            <div class="card-header">
                <h2>
                    <c:choose>
                        <c:when test="${empty categorie.idCategorie}">
                            📂 Nouvelle Catégorie
                        </c:when>
                        <c:otherwise>
                            ✏️ Modifier la Catégorie
                        </c:otherwise>
                    </c:choose>
                </h2>
                <c:if test="${not empty categorie.idCategorie}">
                    <small>ID : #<c:out value="${categorie.idCategorie}" /></small>
                </c:if>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/categorie" method="post">
                    <input type="hidden" name="action" value="${empty categorie.idCategorie ? 'create' : 'update'}">

                    <c:if test="${not empty categorie.idCategorie}">
                        <input type="hidden" name="id" value="${categorie.idCategorie}">
                    </c:if>

                    <!-- Nom de la catégorie -->
                    <div class="form-group">
                        <label class="form-label" for="nomCategorie">
                            📝 Nom de la catégorie *
                        </label>
                        <input type="text" class="form-control" id="nomCategorie" name="nomCategorie"
                               value="<c:out value='${categorie.nomCategorie}' />"
                               placeholder="Ex: Électronique, Vêtements, Maison..."
                               required maxlength="100">
                        <small class="form-text" style="color: #666;">
                            Le nom doit être unique et descriptif.
                        </small>
                    </div>

                    <!-- Description -->
                    <div class="form-group">
                        <label class="form-label" for="description">
                            📄 Description
                        </label>
                        <textarea class="form-control" id="description" name="description"
                                  rows="5" placeholder="Décrivez cette catégorie..."><c:out value="${categorie.description}" /></textarea>
                        <small class="form-text" style="color: #666;">
                            Optionnel - Décrivez le type de produits dans cette catégorie.
                        </small>
                    </div>

                    <!-- Informations sur les produits associés -->
                    <c:if test="${not empty categorie.idCategorie && not empty categorie.produits}">
                        <div class="alert alert-warning">
                            <h5>⚠️ Attention</h5>
                            <p>
                                Cette catégorie contient <strong>${categorie.produits.size()} produit(s)</strong>.
                                La modification affectera tous les produits associés.
                            </p>
                            <ul style="margin: 0.5rem 0 0 1rem;">
                                <c:forEach var="produit" items="${categorie.produits}" end="3">
                                    <li><c:out value="${produit.nom}" /></li>
                                </c:forEach>
                                <c:if test="${categorie.produits.size() > 3}">
                                    <li>... et ${categorie.produits.size() - 3} autres</li>
                                </c:if>
                            </ul>
                        </div>
                    </c:if>

                    <!-- Statistiques (en mode édition) -->
                    <c:if test="${not empty categorie.idCategorie}">
                        <div class="card" style="background: #f8f9fa; margin-bottom: 1.5rem;">
                            <div class="card-body">
                                <h5>📊 Statistiques de la catégorie</h5>
                                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-top: 1rem;">
                                    <div style="text-align: center;">
                                        <div style="font-size: 2rem; font-weight: bold; color: #667eea;">
                                                ${categorie.produits.size()}
                                        </div>
                                        <div style="font-size: 0.9rem; color: #666;">Produits</div>
                                    </div>
                                    <div style="text-align: center;">
                                        <div style="font-size: 2rem; font-weight: bold; color: #28a745;">
                                            <c:set var="totalStock" value="0" />
                                            <c:forEach var="produit" items="${categorie.produits}">
                                                <c:set var="totalStock" value="${totalStock + produit.stock}" />
                                            </c:forEach>
                                                ${totalStock}
                                        </div>
                                        <div style="font-size: 0.9rem; color: #666;">Total stock</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Boutons d'action -->
                    <div style="display: flex; gap: 1rem; margin-top: 2rem; flex-wrap: wrap;">
                        <button type="submit" class="btn btn-primary">
                            <c:choose>
                                <c:when test="${empty categorie.idCategorie}">
                                    ✅ Créer la catégorie
                                </c:when>
                                <c:otherwise>
                                    💾 Enregistrer les modifications
                                </c:otherwise>
                            </c:choose>
                        </button>

                        <a href="${pageContext.request.contextPath}/categorie" class="btn btn-secondary">
                            ↩️ Annuler
                        </a>

                        <!-- Bouton suppression (uniquement en mode édition) -->
                        <c:if test="${not empty categorie.idCategorie}">
                            <a href="${pageContext.request.contextPath}/categorie?action=delete&id=${categorie.idCategorie}"
                               class="btn btn-danger"
                               onclick="return confirm('Êtes-vous sûr de vouloir supprimer la catégorie \"${categorie.nomCategorie}\" ? ${categorie.produits.size() > 0 ? 'Cette catégorie contient ' += categorie.produits.size() += ' produit(s) qui seront également affectés.' : ''}')">
                            🗑️ Supprimer
                            </a>
                        </c:if>
                    </div>
                </form>
            </div>
        </div>

        <!-- Aide -->
        <div class="card" style="margin-top: 1.5rem;">
            <div class="card-header">
                <h5>💡 Conseils</h5>
            </div>
            <div class="card-body">
                <ul style="margin: 0; padding-left: 1.5rem;">
                    <li>Utilisez des noms de catégories clairs et descriptifs</li>
                    <li>Une bonne description aide les clients à trouver vos produits</li>
                    <li>Organisez vos produits en catégories logiques</li>
                    <li>Vous ne pouvez pas supprimer une catégorie qui contient des produits</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<style>
    .form-text {
        display: block;
        margin-top: 0.25rem;
        font-size: 0.875rem;
    }

    .card {
        transition: box-shadow 0.3s ease;
    }

    .card:hover {
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    }

    .btn {
        transition: all 0.3s ease;
    }

    .btn:hover {
        transform: translateY(-1px);
    }
</style>

<script>
    // Validation côté client
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('form');
        const nomInput = document.getElementById('nomCategorie');

        form.addEventListener('submit', function(e) {
            const nomValue = nomInput.value.trim();

            if (nomValue.length === 0) {
                e.preventDefault();
                alert('Veuillez saisir un nom pour la catégorie.');
                nomInput.focus();
                return;
            }

            if (nomValue.length > 100) {
                e.preventDefault();
                alert('Le nom de la catégorie ne peut pas dépasser 100 caractères.');
                nomInput.focus();
                return;
            }
        });

        // Aide en temps réel pour le comptage de caractères
        const descriptionInput = document.getElementById('description');
        if (descriptionInput) {
            const charCount = document.createElement('small');
            charCount.style.color = '#666';
            charCount.style.marginTop = '0.25rem';
            charCount.style.display = 'block';
            descriptionInput.parentNode.appendChild(charCount);

            function updateCharCount() {
                const length = descriptionInput.value.length;
                charCount.textContent = `${length} caractères` + (length > 500 ? ' (limite recommandée)' : '');

                if (length > 500) {
                    charCount.style.color = '#dc3545';
                } else {
                    charCount.style.color = '#666';
                }
            }

            descriptionInput.addEventListener('input', updateCharCount);
            updateCharCount(); // Initial count
        }
    });
</script>

<%@ include file="../includes/footer.jsp" %>
