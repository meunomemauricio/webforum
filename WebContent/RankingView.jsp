<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Ranking - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="topic-content">
		<p class="topic-link"><a class="topic-link" href="posts">« Return</a></p>
	</div>
	<div class="ranking">
		<div class="ranking-row header">
			<div class="ranking-cell">#</div>
			<div class="ranking-cell">Login</div>
			<div class="ranking-cell">Points</div>
		</div>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<c:forEach var="pos" items="${ranking}" varStatus="loop">
			<div class="ranking-row">
				<div class="ranking-cell">${loop.count}</div>
				<div class="ranking-cell">${pos.getLogin()}</div>
				<div class="ranking-cell">${pos.getPoints()}</div>
			</div>
		</c:forEach>
	</div>
</body>
</html>