package br.eng.mauriciofreitas.controllers;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.eng.mauriciofreitas.model.posts.Post;
import br.eng.mauriciofreitas.model.posts.PostDAO;
import br.eng.mauriciofreitas.model.posts.PostManager;

@WebServlet("/posts")
public class PostListController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "It's necessary to be logged in to load that page.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		PostDAO postMgmt = new PostManager();
		List<Post> posts = postMgmt.listPosts();
		request.setAttribute("postList", posts);
		request.getRequestDispatcher("PostListView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


}
