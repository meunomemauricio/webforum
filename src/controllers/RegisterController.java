package controllers;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.users.RegistrationError;
import model.users.User;
import model.users.UserDAO;
import model.users.UserManager;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("RegisterView.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getParameter("login");
		if (login == null || login.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String password = request.getParameter("password");
		if (password == null || password.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		String name = request.getParameter("name");
		String email = request.getParameter("email");

		UserDAO users = new UserManager();
		try {
			users.register(new User(login, email, name), password);
		} catch (RegistrationError e) {
			request.setAttribute("msgError", e.getMessage());
			doGet(request, response);
			return;
		}

		response.sendRedirect("login");
	}

}
