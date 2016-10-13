package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GerenciaUsuario implements UsuarioDAO {

	private final String insereQuery = "INSERT INTO usuario(login, email, nome, senha, pontos) VALUES (?, ?, ?, ?, ?);";
	private final String recuperaQuery = "SELECT * FROM usuario WHERE login = ?;";
	private final String adicionaQuery = "UPDATE usuario SET pontos = pontos + ? WHERE login = ?;";
	private final String rankingQuery = "SELECT * FROM usuario ORDER BY pontos DESC;";

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void inserir(Usuario u) {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(insereQuery);
			stm.setString(1, u.getLogin());
			stm.setString(2, u.getEmail());
			stm.setString(3, u.getNome());
			stm.setString(4, u.getSenha());
			stm.setInt(5, u.getPontos());
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Usuario recuperar(String login) {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(recuperaQuery);
			stm.setString(1, login);
			ResultSet rs = stm.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return resultSet2Usuario(rs);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean autenticar(String login, String senha) {
		Usuario u = recuperar(login);
		if (u == null) {
			return false;
		}
		if (u.getSenha().equals(senha)) {
			return true;
		}
		return false;
	}

	@Override
	public void adicionarPontos(String login, int pontos) throws UsuarioInexistenteError {
		Usuario u = recuperar(login);
		if (u == null) {
			throw new UsuarioInexistenteError("Usuario não existe");
		}
		int novaPontuacao = u.getPontos() + pontos;

		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {

			PreparedStatement stm = con.prepareStatement(adicionaQuery);
			stm.setInt(1, novaPontuacao);
			stm.setString(2, login);
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

	}

	@Override
	public List<Usuario> ranking() {
		List<Usuario> ranking = new ArrayList<>();
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(rankingQuery);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				ranking.add(resultSet2Usuario(rs));
			}
			return ranking;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	private Usuario resultSet2Usuario(ResultSet rs) throws SQLException {
		String login = rs.getString("login");
		String email = rs.getString("email");
		String nome = rs.getString("nome");
		String senha = rs.getString("senha");
		int pontos = rs.getInt("pontos");

		return new Usuario(login, email, nome, senha, pontos);
	}

}