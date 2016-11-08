<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
		<form action="vote" method="post"> 
			<input type="hidden" name="post_id" value="${post.getId()}">
			<input id="rUpvote" type="radio" name="vote"
				value="up" onclick="this.form.submit();">
			<label for="rUpvote">Upvote</label>
			<input id="rDownvote" type="radio" name="vote"
				value="down" onclick="this.form.submit();">
			<label for="rDownvote">Downvote</label>
		</form>
		<p id="votes">${post.getVotes()}</p>
		<p class="topic-title">${post.getTitle()}</p>
		<p class="topic-user">by: <span class="topic-user">${post.getLogin()}</span></p>
		<p class="topic-text">${post.getContent()}</p>
	</div>
	<div class="topic-comments">
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