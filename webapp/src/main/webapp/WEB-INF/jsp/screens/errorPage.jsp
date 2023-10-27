<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Error ${errorCode}</title>
    <jsp:include page="/resources/externalResources.jsp"/>
    <link rel="stylesheet" href="<c:url value="/css/errorPage.css"/>">
    <link rel="icon" type="image/x-icon" href="<c:url value="/images/favicon.ico" />">
</head>
<body>
<c:set var="varTitle"><c:out value="${title}" /></c:set>
<h1><spring:message code="${varTitle}" /></h1>

<c:set var="varMessage"><c:out value="${message}" /></c:set>
<p><spring:message code="${varMessage}" /></p>
<p><spring:message code="errorPage.text1"/> <a href="<c:url value="/"/>"><spring:message code="errorPage.text2"/></a>.</p>
</body>
</html>
