<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Novo Tópico - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="insert-form">
		<form action="insere" method="post">
			<p class="form-title">Digite abaixo o conteúdo do novo tópico:</p>
			<input type="text" placeholder="título" name="titulo"/>
			<textarea placeholder="conteúdo"></textarea>
			<button>novo tópico</button>
			<% if (request.getAttribute("msgError") != null) { %>
				<p class="error">${msgError}</p>
			<% } %>
		</form>
		<p class="insert-link"><a class="insert-link" href="topicos">« Voltar para a lista de tópicos</a></p>
	</div>
</body>
</html>