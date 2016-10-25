package controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.users.AuthenticationError;
import model.users.UserDAO;
import model.users.UserManager;

@WebServlet("/login")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("LoginView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
