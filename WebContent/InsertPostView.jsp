<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>New Post - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="insert-form">
		<form action="insert" method="post">
			<p class="form-title">Write the content of the new post:</p>
			<input type="text" placeholder="title" name="title"/>
			<textarea placeholder="post content" name="content"></textarea>
			<button>submit</button>
			<% if (request.getAttribute("msgError") != null) { %>
				<p class="error">${msgError}</p>
			<% } %>
		</form>
		<p class="insert-link"><a class="insert-link" href="posts">« Return</a></p>
	</div>
</body>
</html>