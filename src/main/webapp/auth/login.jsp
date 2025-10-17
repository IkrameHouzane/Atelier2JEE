<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="title" value="Connexion" />
<%@ include file="../includes/header.jsp" %>

<div class="row">
    <div class="col-6" style="margin: 0 auto;">
        <div class="card">
            <div class="card-header">
                <h2>🔐 Connexion</h2>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="form-group">
                        <label class="form-label" for="email">Email :</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="motDePasse">Mot de passe :</label>
                        <input type="password" class="form-control" id="motDePasse" name="motDePasse" required>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">Se connecter</button>
                </form>

                
            </div>
        </div>
    </div>
</div>

<%@ include file="../includes/footer.jsp" %>
