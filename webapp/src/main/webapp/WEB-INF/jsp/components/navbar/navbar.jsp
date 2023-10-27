<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Alfa+Slab+One&display=swap" rel="stylesheet">
<div class="side-bar">
    <a class="brand" href="<c:url value="/"/>">
        <div class="brand-name">
            <div class="brand-1 not-selectable">Taste</div>
            <div class="brand-1 not-selectable">Tales</div>
        </div>
    </a>
    <a class="features-item" href="<c:url value="/upload-recipe"/>">
        <i class="material-icons icons">add</i>
        <span class="features-item-text hideable"><spring:message code="navbar.createNew"/></span>
    </a>
    <a class="features-item" href=<c:url value="/"/>>
        <i class="material-icons icons">search</i>
        <span class="features-item-text hideable"><spring:message code="navBar.discover"/></span>
    </a>
    <c:choose>
        <c:when test="${not currentUser.present}">
            <a class="features-item" href="<c:url value="/login"/>">
                <i class="material-icons icons">login</i>
                <span class="features-item-text hideable"><spring:message code="navbar.login"/></span>
            </a>
        </c:when>
        <c:otherwise>
            <a class="features-item" href="<c:url value="/saved"/>">
                <i class="material-icons icons">bookmark</i>
                <span class="features-item-text hideable"><spring:message code="navbar.saved"/></span>
            </a>
            <a class="features-item" href="<c:url value="/myRecipes"/>">
                <i class="material-icons icons">receipt</i>
                <span class="features-item-text hideable"><spring:message code="navbar.myRecipes"/></span>
            </a>
            <a class="features-item" href="<c:url value="/profile"/>">
                <i class="material-icons icons">person</i>
                <span class="features-item-text hideable"
                      id="username"><span class="name">${currentUser.get().name.substring(0,1).toUpperCase()}${currentUser.get().name.substring(1).toLowerCase()}</span></span>
                <c:if test="${currentUser.get().admin}">
                    <div class="admin">
                        <div><spring:message code="navbar.admin"/></div>
                    </div>
                </c:if>

            </a>
            <a class="features-item" href=<c:url value="/logout"/>>
                <i class="material-icons icons">logout</i>
                <span class="features-item-text hideable"><spring:message code="navbar.logout"/></span>
            </a>
        </c:otherwise>
    </c:choose>
</div>