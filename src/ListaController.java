

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
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
