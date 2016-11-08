package br.eng.mauriciofreitas.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.eng.mauriciofreitas.model.posts.InvalidPost;
import br.eng.mauriciofreitas.model.posts.Post;
import br.eng.mauriciofreitas.model.posts.PostDAO;
import br.eng.mauriciofreitas.model.posts.PostManager;
import br.eng.mauriciofreitas.model.users.AuthenticationError;
import br.eng.mauriciofreitas.model.users.UserDAO;
import br.eng.mauriciofreitas.model.users.UserManager;

@WebServlet("/vote")
public class VoteController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("login");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		if (notLoggedIn(request)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		int votes, postId;
		try {
			postId = Integer.parseInt(request.getParameter("post_id"));
			votes = getVotes(request);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		PostDAO postMgmt = new PostManager();
		Post post;
		try {
			postMgmt.addVotes(postId, votes);
			post = postMgmt.retrieve(postId);
		} catch (InvalidPost e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		UserDAO userMgmt = new UserManager();
		try {
			userMgmt.addPoints(post.getLogin(), votes);
		} catch (AuthenticationError e) {
			// Since the login comes from the post, if this exception happens,
			// something very wrong went in the model.
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		response.sendRedirect(String.format("post?id=%d", postId));
	}

	/**
	 * Check if the user is not logged in
	 *
	 * @param request  Current request
	 * @return  true if the session in the request is logged in
	 */
	private boolean notLoggedIn(HttpServletRequest request) {
		return request.getSession().getAttribute("login") == null;
	}

	/**
	 * Get the number of votes depending on whether the post was upvoted or downvoted
	 *
	 * @param request  Current request object
	 * @return  Number of votes to add to the post
	 * @throws Exception  if the "vote" parameter value is invalid
	 */
	private int getVotes(HttpServletRequest request) throws Exception {
		String vote = request.getParameter("vote");
		if("up".equals(vote))
			return 1;
		else if ("down".equals(vote))
			return -1;
		throw new Exception("Invalid value for vote. Should be 'up' or 'down'.");
	}
}