package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GerenciaComentario implements ComentariosDAO {

	private final String insereQuery = "INSERT INTO comentario(comentario, login, id_topico) VALUES (?, ?, ?);";
	private final String recuperaQuery = "SELECT * FROM comentario WHERE id_topico=? ORDER BY id_comentario ASC;";

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void adicionaComentario(Comentario cmnt) {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(insereQuery);
			stm.setString(1, cmnt.getComentario());
			stm.setString(2, cmnt.getLogin());
			stm.setInt(3, cmnt.getTopicoId());
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Comentario> recuperarComentarios(int topicoId) {
		List<Comentario> comentarios = new ArrayList<>();
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(recuperaQuery);
			stm.setInt(1, topicoId);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				String comentario = rs.getString("comentario");
				String login = rs.getString("login");
				int id = rs.getInt("id_topico");
				comentarios.add(new Comentario(id, comentario, login, topicoId));
			}
			return comentarios;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

}
