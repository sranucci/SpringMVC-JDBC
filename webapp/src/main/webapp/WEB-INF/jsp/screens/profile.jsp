<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Profile</title>
    <jsp:include page="/resources/externalResources.jsp"/>
    <link rel="stylesheet" href="<c:url value="/css/main.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/navbar.css" />">
    <link rel="stylesheet" href="<c:url value="/css/mainPagesStructure.css" />">
    <link rel="stylesheet" href="<c:url value="/css/profile.css" />">
    <link rel="icon" type="image/x-icon" href="<c:url value="/images/favicon.ico" />">
</head>
<body class="font-family">
<div class="main-structure">
    <div class="nav-bar-container">
        <jsp:include page="/WEB-INF/jsp/components/navbar/navbar.jsp"/>
    </div>
    <div class="main-content-container main-profile">
        <div class="main-content">
            <div class="form-container">
                <div class="profile-container">
                    <div class="header">
                        <c:if test="${currentUser.get().admin}">
                            <div class="adminProfile">
                                <i class="material-icons icons adminIcon">supervisor_account</i> <spring:message
                                    code="profile.admin"/>
                            </div>
                        </c:if>
                        <div class="edit-profile">
                            <a class="edit_button" href="<c:url value="/editProfile"/>">
                                <i class="material-icons icons">create</i>
                            </a>
                        </div>
                    </div>
                    <div class="profile-info">
                        <div class="photo-container">
                            <img class="user-photo"
                                 id="userPhoto"
                                 src="<c:url value="/userImage/${user.id}"/>" alt="profile photo not found">
                        </div>
                        <div class="info-container-values">
                            <div><spring:message code="profile.name"/></div>
                            <div class="info-input"><c:out value="${user.name}"/></div>
                        </div>
                        <div class="info-container-values">
                            <div><spring:message code="profile.lastName"/></div>
                            <div class="info-input"><c:out value="${user.lastname}"/></div>
                        </div>
                    </div>
                    <div class="profile-info">
                        <div class="info-container margin-bottom">
                            <div><spring:message code="profile.email"/></div>
                            <div class="info-input email-profile"><c:out value="${user.email}"/></div>
                        </div>
                    </div>
                    <hr class="line margin-bottom"/>
                    <div class="interactions-container">
                        <div class="interactions-title margin-bottom"><spring:message
                                code="profile.interactions"/></div>
                        <div class="interactions margin-bottom">
                            <div><spring:message code="profile.likes"/>
                                <c:out value="${likes}"/>
                            </div>
                            <div><spring:message code="profile.comments"/>
                                <c:out value="${comments}"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
