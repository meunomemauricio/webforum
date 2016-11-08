package br.eng.mauriciofreitas.controllers;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.eng.mauriciofreitas.model.comments.Comment;
import br.eng.mauriciofreitas.model.comments.CommentManager;
import br.eng.mauriciofreitas.model.comments.CommentsDAO;
import br.eng.mauriciofreitas.model.posts.InvalidPost;
import br.eng.mauriciofreitas.model.posts.PostDAO;
import br.eng.mauriciofreitas.model.posts.PostManager;

@WebServlet("/post")
public class PostController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "It's necessary to be logged in to load that page.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		int postId;
		try {
			postId = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);  // 404 page
			return;
		}

		PostDAO postMgmt = new PostManager();
		try {
			request.setAttribute("post", postMgmt.retrieve(postId));
		} catch (InvalidPost e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		CommentsDAO commentMgmt = new CommentManager();
		List<Comment> comments = commentMgmt.retrieveComments(postId);
		request.setAttribute("comments", comments);

		request.getRequestDispatcher("PostView.jsp").forward(request, response);
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

		int postId;
		try {
			postId = Integer.parseInt(request.getParameter("postId"));
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String comment = request.getParameter("comment");
		if (comment == null || comment.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		CommentsDAO cmntMgmt = new CommentManager();
		Comment cmnt = new Comment(comment, (String) login, postId);
		cmntMgmt.addComment(cmnt);

		response.sendRedirect(String.format("post?id=%d", postId));
	}


}
