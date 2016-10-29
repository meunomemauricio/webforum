<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${post.getTitle()} - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="topic-content">
		<p class="topic-link"><a class="topic-link" href="posts">« Return</a></p>
		<p class="topic-title">${post.getTitle()}</p>
		<p class="topic-user">by: <span class="topic-user">${post.getLogin()}</span></p>
		<p class="topic-text">${post.getContent()}</p>
	</div>
	
	<div class="topic-comments">
		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
			<c:forEach var="cmnt" items="${comments}">
				<div class="topic-cmt">
					<p class="topic-cmt-text">${cmnt.getContent()}</p>
					<p class="topic-cmt-user">${cmnt.getLogin()}</p>
				</div>
			</c:forEach>

		<form class="cmt-form" action="post" method="post">
			<input type="hidden" name="postId" value="${post.getId()}">
			<textarea placeholder="write you comment..." name="comment" required></textarea>
			<button>submit</button>
		</form>
	</div>

</body>
</html>