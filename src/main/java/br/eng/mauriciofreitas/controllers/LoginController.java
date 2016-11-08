package br.eng.mauriciofreitas.controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.eng.mauriciofreitas.model.users.AuthenticationError;
import br.eng.mauriciofreitas.model.users.UserDAO;
import br.eng.mauriciofreitas.model.users.UserManager;

@WebServlet("/login")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		displayMsgIfFirstLogin(request);
		request.getRequestDispatcher("LoginView.jsp").forward(request, response);
	}

	/**
	 * Display message on the first login after account creation.
	 *
	 * @param request Servlet request object
	 */
	private void displayMsgIfFirstLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("regSuccess") != null) {
			request.setAttribute("regMessage", "New account created successfully");
			session.removeAttribute("regSuccess");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String login = request.getParameter("login");
		String password = request.getParameter("password");
		UserDAO users = new UserManager();
		HttpSession session = request.getSession();
		try {
			users.authenticate(login, password);
			session.setAttribute("login", login);
			response.sendRedirect("posts");
		} catch (AuthenticationError e) {
			session.removeAttribute("login");
			request.setAttribute("msgError", e.getMessage());
			doGet(request, response);
		}
	}

}
