<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Posts - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="header">
		<p class="header-link">
			<span class="header-link-l"><a class="header-link" href="insert">+ New Post</a></span>
			<span class="header-link-c"><a href="ranking" class="header-link">🏅 Ranking</a></span>
			<span class="header-link-r"><a class="header-link" href="logout">✗ Logout</a></span>
		</p>
	</div>
	<c:forEach var="post" items="${postList}">
		<a href="post?id=${post.getId()}" class="list-topic-item">
			<p class="list-item-votes">${post.getVotes()}</p>
			<p class="list-item-title">${post.getTitle()}</p>
		    <p class="list-item-user">by: <span class="list-item-user">${post.getLogin()}</span></p>
		</a>
	</c:forEach>
</body>
</html>