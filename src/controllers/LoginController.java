package controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.GerenciaUsuario;
import model.UsuarioDAO;

@WebServlet("/login")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("TelaLogin.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String senha = request.getParameter("senha");
		UsuarioDAO usuarios = new GerenciaUsuario();
		HttpSession session = request.getSession();
		if (usuarios.autenticar(login, senha)) {
			session.setAttribute("login", login);
			response.sendRedirect("topicos");
		}
		else {
			session.removeAttribute("login");
			request.setAttribute("msgError", "Não foi possível autenticar o usuário");
			doGet(request, response);
		}
	}

}
