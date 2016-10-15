<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${topico.getTitulo()} - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="topic-content">
		<p class="topic-link"><a class="topic-link" href="topicos">« Voltar para a lista de tópicos</a></p>
		<p class="topic-title">${topico.getTitulo()}</p>
		<p class="topic-user">Postado por: <span class="topic-user">${topico.getLogin()}</span></p>
		<p class="topic-text">${topico.getConteudo()}</p>
	</div>
	
	<div class="topic-comments">
		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
			<c:forEach var="cmnt" items="${comentarios}">
				<div class="topic-cmt">
					<p class="topic-cmt-user">${cmnt.getLogin()}</p>
					<p class="topic-cmt-text">${cmnt.getComentario()}</p>
				</div>
			</c:forEach>

		<form class="cmt-form" action="topico" method="post">
			<input type="hidden" name="topicoId" value="${topico.getId()}">
			<textarea placeholder="Escreva seu comentario..." name="comentario"></textarea>
			<button>adicionar comentario</button>
		</form>
	</div>

</body>
</html>