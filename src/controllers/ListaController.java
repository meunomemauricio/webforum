package controllers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/topicos")
public class ListaController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object login = request.getSession().getAttribute("login");
		if (login == null) {
			request.setAttribute("msgError", "É necessário estar logado para acessar aquela página.");
			request.getRequestDispatcher("login").forward(request, response);
			return;
		}

		List<String> topicos = new ArrayList<>();
		topicos.add("Topico1");
		topicos.add("Topico2");
		topicos.add("Topico3");
		topicos.add("Topico4");
		topicos.add("Topico4");
		request.setAttribute("listTopicos", topicos);
		request.getRequestDispatcher("TelaTopicos.jsp").forward(request, response);
	}

}
