<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>XXX Titulo do Tópico XXX - Web Forum</title>
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
	    <div class="topic-cmt">
			<p class="topic-cmt-user">Comentador 1</p>
			<p class="topic-cmt-text">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi quis est ut mauris molestie</p>
		</div>

	    <div class="topic-cmt">
			<p class="topic-cmt-user">Comentador 2</p>
			<p class="topic-cmt-text">Duis congue est nisl, id tempor arcu vehicula quis. Maecenas non te</p>
		</div>

	    <div class="topic-cmt">
			<p class="topic-cmt-user">Comentador 3</p>
			<p class="topic-cmt-text">Nulla facilisi. Proin sapien nisl, imperdiet ac tincidunt sed, dapibus at massa. Aliquam in turpis sed augue aliquam feugiat blandit eget justo. Mauris placerat felis rutrum odio fringilla, at pellentesque urna porta.</p>
		</div>

		<form class="cmt-form" action="topico" method="post">
			<input type="hidden" name="id" value="id">
			<textarea placeholder="Escreva seu comentario..." name="comentario"></textarea>
			<button>adicionar comentario</button>
		</form>
	</div>

</body>
</html>