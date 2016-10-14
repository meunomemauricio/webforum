package controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.GerenciaUsuario;
import model.RegistroError;
import model.Usuario;
import model.UsuarioDAO;

@WebServlet("/cadastro")
public class CadastroController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("TelaCadastro.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		String nome = request.getParameter("nome");
		String email = request.getParameter("email");
		String senha = request.getParameter("senha");

		UsuarioDAO usuarios = new GerenciaUsuario();
		try {
			usuarios.inserir(new Usuario(login, email, nome, senha));
		} catch (RegistroError e) {
			request.setAttribute("msgError", e.getMessage());
			doGet(request, response);
			return;
		}

		response.sendRedirect("login");
	}

}
