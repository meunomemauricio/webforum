package controllers;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Comentario;
import model.ComentariosDAO;
import model.GerenciaComentario;
import model.GerenciaTopico;
import model.GerenciaUsuario;
import model.TopicoInexistente;
import model.TopicosDAO;
import model.UsuarioDAO;
import model.UsuarioInexistenteError;

@WebServlet("/topico")
public class TopicoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int PONTOS_POR_COMENTARIO = 3;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "É necessário estar logado para acessar aquela página.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		int topicoID;
		try {
			topicoID = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);  // 404 page
			return;
		}

		TopicosDAO gerTopicos = new GerenciaTopico();
		try {
			request.setAttribute("topico", gerTopicos.recuperar(topicoID));
		} catch (TopicoInexistente e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		ComentariosDAO gerCmnt = new GerenciaComentario();
		List<Comentario> comentarios = gerCmnt.recuperarComentarios(topicoID);
		request.setAttribute("comentarios", comentarios);

		request.getRequestDispatcher("TelaTopico.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "É necessário estar logado para acessar aquela página.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		String comentario = request.getParameter("comentario");

		int topicoID;
		try {
			topicoID = Integer.parseInt(request.getParameter("topicoId"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		ComentariosDAO gerCmnt = new GerenciaComentario();
		Comentario cmnt = new Comentario(comentario, (String) login, topicoID);
		gerCmnt.adicionaComentario(cmnt);

		UsuarioDAO gerUser = new GerenciaUsuario();
		try {
			gerUser.adicionarPontos((String) login, PONTOS_POR_COMENTARIO);
		} catch (UsuarioInexistenteError e) {
			response.sendRedirect("logout");
			return;
		}

		response.sendRedirect(String.format("topico?id=%d", topicoID));
	}


}
