<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>
</head>
<body>
	<h1>Hello world!</h1>
	<button>
		<a
			href="https://accounts.google.com/o/oauth2/auth?client_id=877951810439-pgeplp24bg2t9eej5ffdc7qlde2hjjnc.apps.googleusercontent.com&redirect_uri=http://localhost:8080/backend/login/oauth2/code/google&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile">
			google login </a>
	</button>
	<c:if test="${not empty userInfoList }">
		<c:forEach items="${userInfoList }" var="info">
			<p>${info }</p>
			<p>==============================</p>
		</c:forEach>
	</c:if>
</body>
</html>
