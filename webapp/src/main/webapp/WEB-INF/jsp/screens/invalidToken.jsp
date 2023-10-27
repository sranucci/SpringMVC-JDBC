<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Invalid Token</title>
    <jsp:include page="/resources/externalResources.jsp"/>
    <link rel="stylesheet" href="<c:url value="/css/mainPagesStructure.css" />">
    <link rel="stylesheet" href="<c:url value="/css/navbar.css" />">
    <link rel="stylesheet" href="<c:url value="/css/failure.css" />">
    <link rel="icon" type="image/x-icon" href="<c:url value="/images/favicon.ico" />">
</head>
<body>
<div class="main-structure">
    <div class="nav-bar-container">
        <jsp:include page="/WEB-INF/jsp/components/navbar/navbar.jsp"/>
    </div>
    <div class="container">
        <div class="card-panel">
            <!-- Use the card-panel class to create a card, and add green lighten-3 class for the success color -->
            <span class="white-text"> <!-- Use the white-text class to set the text color to white -->
                <spring:message code="invalidToken.text"/>
            </span>
        </div>
        <a href="<c:url value="/" />" class="link"><spring:message code="invalidToken.return"/></a>
    </div>
</div>
</body>
</html>
