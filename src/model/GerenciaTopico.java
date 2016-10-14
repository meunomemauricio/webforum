package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GerenciaTopico implements TopicosDAO {

	private final String insereQuery = "INSERT INTO topico(titulo, conteudo, login) VALUES (?, ?, ?);";
	private final String listaQuery = "SELECT * FROM public.topico ORDER BY id_topico DESC;";
	private final String recuperaQuery = "SELECT * FROM public.topico WHERE id_topico=?;";

	@Override
	public void insere(Topico topico) {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(insereQuery);
			stm.setString(1, topico.getTitulo());
			stm.setString(2, topico.getConteudo());
			stm.setString(3, topico.getLogin());
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Topico> listarTopicos() {
		List<Topico> topicos = new ArrayList<>();
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(listaQuery);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				String titulo = rs.getString("titulo");
				String conteudo = rs.getString("conteudo");
				String login = rs.getString("login");
				int id = rs.getInt("id_topico");
				topicos.add(new Topico(id, titulo, conteudo, login));
			}
			return topicos;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Topico recuperar(int id) throws TopicoInexistente {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(recuperaQuery);
			stm.setInt(1, id);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				String titulo = rs.getString("titulo");
				String conteudo = rs.getString("conteudo");
				String login = rs.getString("login");
				return new Topico(id, titulo, conteudo, login);
			}
			throw new TopicoInexistente("Não foi possível encontrar este tópico.");
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

}
