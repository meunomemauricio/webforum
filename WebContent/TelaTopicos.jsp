<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Tópicos - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<c:forEach var="topico" items="${listTopicos}">
		<a href="topico?id=1" class="list-topic-item">
			<p class="list-item-title">${topico}</p>
		    <p class="list-item-user">Por: <span class="list-item-user">Autor</span></p>
		</a>
	</c:forEach>
</body>
</html>