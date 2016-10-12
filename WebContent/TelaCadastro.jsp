<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Cadastro - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="login-page">
		<div class="form">
			<p class="form-title">Crie uma nova conta:</p>
			<form class="cadastro-form" action="cadastro" method="post">
				<input type="text" name="login" placeholder="login"/>
				<input type="password" name="senha" placeholder="senha"/>
				<input type="text" name="nome" placeholder="nome"/>
				<input type="text" name="email" placeholder="email"/>
				<button>cadastrar</button>
				<% if (request.getAttribute("msgError") != null) { %>
					<p class="error">${msgError}</p>
				<% } %>
				<p class="message">Já está registrado? <a href="login">Faça Login</a></p>
			</form>
		</div>
	</div>
</body>
</html>