<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Register - Web Forum</title>
	<link rel="stylesheet" type="text/css" href="style/forum.css">
</head>
<body>
	<div class="login-page">
		<div class="form">
			<p class="form-title">Crie uma nova conta:</p>
			<form class="cadastro-form" action="register" method="post">
				<input type="text" name="login" placeholder="login"/>
				<input type="password" name="password" placeholder="password"/>
				<input type="text" name="name" placeholder="name (optional)"/>
				<input type="text" name="email" placeholder="email (optional)"/>
				<button>register</button>
				<% if (request.getAttribute("msgError") != null) { %>
					<p class="error">${msgError}</p>
				<% } %>
				<p class="message">Already registered? <a href="login">Login here</a></p>
			</form>
		</div>
	</div>
</body>
</html>