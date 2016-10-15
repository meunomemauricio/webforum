<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Login - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
	</head>
<body>
	<div class="login-page">
		<div class="form">
			<p class="form-title">Entre com suas credenciais:</p>
			<form class="login-form" action="login" method="post">
				<input type="text" placeholder="login" name="login"/>
				<input type="password" placeholder="senha" name="senha"/>
				<button>login</button>
				<% if (request.getAttribute("msgError") != null) { %>
					<p class="error">${msgError}</p>
				<% } %>
				<p class="message">Não se registrou? <a href="cadastro">Criar uma conta.</a></p>
			</form>
		</div>
	</div>
</body>
</html>