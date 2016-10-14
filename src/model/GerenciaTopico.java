package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GerenciaTopico implements TopicosDAO {

	private final String insereQuery = "INSERT INTO topico(titulo, conteudo, login) VALUES (?, ?, ?);";

	private final int PONTOS_POR_TOPICO = 10;

	private UsuarioDAO _gerUser = new GerenciaUsuario();

	public GerenciaTopico(UsuarioDAO dao) {
		_gerUser = dao;
	}

	@Override
	public void insere(Topico topico) throws TopicoInvalidoError {
		try {
			_gerUser.adicionarPontos(topico.getLogin(), PONTOS_POR_TOPICO);
		} catch (UsuarioInexistenteError e) {
			e.printStackTrace();
		}

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

}
