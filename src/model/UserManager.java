package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements UserDAO {

	private final String insertQuery = "INSERT INTO usuario(login, email, nome, senha, pontos) VALUES (?, ?, ?, ?, ?);";
	private final String retrieveQuery = "SELECT * FROM usuario WHERE login = ?;";
	private final String addPointQuery = "UPDATE usuario SET pontos = pontos + ? WHERE login = ?;";
	private final String rankingQuery = "SELECT * FROM usuario ORDER BY pontos DESC;";

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insert(User u) throws RegistrationError {
		if (retrieve(u.getLogin()) != null)
			throw new RegistrationError("Login already registered");

		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(insertQuery);
			stm.setString(1, u.getLogin());
			stm.setString(2, u.getEmail());
			stm.setString(3, u.getName());
			stm.setString(4, u.getPassword());
			stm.setInt(5, u.getPoints());
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public User retrieve(String login) {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(retrieveQuery);
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
	public boolean authenticate(String login, String password) {
		User u = retrieve(login);
		if (u == null) {
			return false;
		}
		if (u.getPassword().equals(password)) {
			return true;
		}
		return false;
	}

	@Override
	public void addPoints(String login, int points) throws InvalidUserError {
		User u = retrieve(login);
		if (u == null) {
			throw new InvalidUserError("Unregistered User");
		}

		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {

			PreparedStatement stm = con.prepareStatement(addPointQuery);
			stm.setInt(1, points);
			stm.setString(2, login);
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

	}

	@Override
	public List<User> rankUsers() {
		List<User> ranking = new ArrayList<>();
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

	private User resultSet2Usuario(ResultSet rs) throws SQLException {
		String login = rs.getString("login");
		String email = rs.getString("email");
		String name = rs.getString("nome");
		String password = rs.getString("senha");
		int points = rs.getInt("pontos");

		return new User(login, email, name, password, points);
	}

}