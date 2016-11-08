package br.eng.mauriciofreitas.controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.eng.mauriciofreitas.model.posts.Post;
import br.eng.mauriciofreitas.model.posts.PostDAO;
import br.eng.mauriciofreitas.model.posts.PostManager;

@WebServlet("/insert")
public class InsertController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "It's necessary to be logged in to load that page.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		request.getRequestDispatcher("InsertPostView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "It's necessary to be logged in to load that page.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		String title = request.getParameter("title");
		if (title == null || title.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String content = request.getParameter("content");
		if (content == null || content.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		PostDAO postMgmt = new PostManager();
		postMgmt.insert(new Post(title, content, (String) login));

		response.sendRedirect("posts");
	}

}
