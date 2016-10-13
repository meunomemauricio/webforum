<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Tópicos - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="header">
		<p class="header-link">
			<span class="header-link-l"><a class="header-link" href="insere">+ Novo Tópico</a></span>
			<span class="header-link-c"><a href="ranking" class="header-link">🏅 Ranking</a></span>
			<span class="header-link-r"><a class="header-link" href="logout">✗ Logout</a></span>
		</p>
	</div>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
	<c:forEach var="topico" items="${listTopicos}">
		<a href="topico?id=1" class="list-topic-item">
			<p class="list-item-title">${topico}</p>
		    <p class="list-item-user">Por: <span class="list-item-user">Autor</span></p>
		</a>
	</c:forEach>
</body>
</html>