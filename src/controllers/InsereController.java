package controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.GerenciaTopico;
import model.GerenciaUsuario;
import model.Topico;
import model.TopicosDAO;
import model.UsuarioDAO;
import model.UsuarioInexistenteError;

@WebServlet("/insere")
public class InsereController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int PONTOS_POR_TOPICO = 10;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "É necessário estar logado para acessar aquela página.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		request.getRequestDispatcher("TelaInsereTopico.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "É necessário estar logado para acessar aquela página.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		String titulo = request.getParameter("titulo");
		String conteudo = request.getParameter("conteudo");

		TopicosDAO gerTopicos = new GerenciaTopico();
		gerTopicos.insere(new Topico(titulo, conteudo, (String) login));

		UsuarioDAO gerUser = new GerenciaUsuario();
		try {
			gerUser.adicionarPontos((String) login, PONTOS_POR_TOPICO);
		} catch (UsuarioInexistenteError e) {
			response.sendRedirect("logout");
			return;
		}

		response.sendRedirect("topicos");
	}

}
