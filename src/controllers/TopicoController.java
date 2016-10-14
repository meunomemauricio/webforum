package controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.GerenciaTopico;
import model.Topico;
import model.TopicoInexistente;
import model.TopicosDAO;

@WebServlet("/topico")
public class TopicoController extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		Topico topico;
		try {
			topico = gerTopicos.recuperar(topicoID);
		} catch (TopicoInexistente e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		request.setAttribute("topico", topico);
		request.getRequestDispatcher("TelaTopico.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String comentario = request.getParameter("comentario");
		String topicoID = request.getParameter("id");
		response.sendRedirect(String.format("topico?id=%s", topicoID));
	}


}
