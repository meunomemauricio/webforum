<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Novo Tópico - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="insert-page">
		<div class="form">
			<p class="form-title">Digite abaixo o conteúdo do novo tópico:</p>
			<form class="insert-form" action="insere" method="post">
				<input type="text" placeholder="título" name="titulo"/>
				<textarea placeholder="digite o conteúdo do tópico aqui..."></textarea>
				<button>novo tópico</button>
				<% if (request.getAttribute("msgError") != null) { %>
					<p class="error">${msgError}</p>
				<% } %>
			</form>
		</div>
	</div>
</body>
</html>