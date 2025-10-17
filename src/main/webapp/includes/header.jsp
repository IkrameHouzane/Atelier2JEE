<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-Commerce - <c:out value="${param.title}" default="Accueil" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="header">
    <div class="container">
        <nav class="navbar">
            <a href="${pageContext.request.contextPath}/" class="logo">🛍️ E-Commerce</a>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/">Accueil</a></li>
                <li><a href="${pageContext.request.contextPath}/produit">Produits</a></li>

                <c:choose>
                    <c:when test="${not empty sessionScope.internauteId}">
                        <li><a href="${pageContext.request.contextPath}/panier">Panier</a></li>
                        <li><a href="${pageContext.request.contextPath}/commande?action=history">Mes Commandes</a></li>
                        <li>
                            <span>Bonjour, <c:out value="${sessionScope.internauteNom}" /></span>
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-warning">Déconnexion</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/login" class="btn btn-sm btn-primary">Connexion</a></li>
                    </c:otherwise>
                </c:choose>

                <!-- Admin links -->
                <c:if test="${sessionScope.internauteEmail == 'admin@email.com'}">
                    <li><a href="${pageContext.request.contextPath}/categorie">Catégories</a></li>
                    <li><a href="${pageContext.request.contextPath}/internaute">Utilisateurs</a></li>
                    <li><a href="${pageContext.request.contextPath}/commande">Commandes</a></li>
                </c:if>
            </ul>
        </nav>
    </div>
</header>

<main class="main-content">
<div class="container">
<!-- Messages d'alerte -->
<c:if test="${not empty param.success}">
    <div class="alert alert-success">
        <c:choose>
            <c:when test="${param.success == 'create'}">✅ Création réussie !</c:when>
            <c:when test="${param.success == 'update'}">✅ Modification réussie !</c:when>
            <c:when test="${param.success == 'delete'}">✅ Suppression réussie !</c:when>
            <c:when test="${param.success == 'added'}">✅ Produit ajouté au panier !</c:when>
            <c:when test="${param.success == 'created'}">✅ Commande créée avec succès !</c:when>
            <c:when test="${param.success == 'confirmed'}">✅ Commande confirmée !</c:when>
            <c:otherwise>✅ Opération réussie !</c:otherwise>
        </c:choose>
    </div>
</c:if>

<c:if test="${not empty param.error}">
    <div class="alert alert-danger">
        <c:choose>
            <c:when test="${param.error == 'create'}">❌ Erreur lors de la création</c:when>
            <c:when test="${param.error == 'update'}">❌ Erreur lors de la modification</c:when>
            <c:when test="${param.error == 'delete'}">❌ Erreur lors de la suppression</c:when>
            <c:when test="${param.error == 'auth'}">❌ Email ou mot de passe incorrect</c:when>
            <c:when test="${param.error == 'empty'}">❌ Le panier est vide</c:when>
            <c:when test="${param.error == 'stock'}">❌ Stock insuffisant</c:when>
            <c:when test="${param.error == 'hasProducts'}">❌ Impossible de supprimer : catégorie utilisée par des produits</c:when>
            <c:otherwise>❌ Une erreur est survenue</c:otherwise>
        </c:choose>
    </div>
</c:if>