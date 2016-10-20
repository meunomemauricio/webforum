package controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.InvalidUserError;
import model.Post;
import model.PostDAO;
import model.PostManager;
import model.UserDAO;
import model.UserManager;

@WebServlet("/insert")
public class InsertController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int PONTOS_POR_TOPICO = 10;

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
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "It's necessary to be logged in to load that page.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		String title = request.getParameter("title");
		String content = request.getParameter("content");

		PostDAO postMgmt = new PostManager();
		postMgmt.insert(new Post(title, content, (String) login));

		UserDAO userMgmt = new UserManager();
		try {
			userMgmt.addPoints((String) login, PONTOS_POR_TOPICO);
		} catch (InvalidUserError e) {
			response.sendRedirect("logout");
			return;
		}

		response.sendRedirect("posts");
	}

}
