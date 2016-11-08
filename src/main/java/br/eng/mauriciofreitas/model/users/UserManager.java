package br.eng.mauriciofreitas.model.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements UserDAO {

	private final String insertQuery = "INSERT INTO users(login, email, name, pwhash, salt, points) VALUES (?, ?, ?, ?, ?, ?);";
	private final String retrieveQuery = "SELECT * FROM users WHERE login = ?;";
	private final String addPointQuery = "UPDATE users SET points = points + ? WHERE login = ?;";
	private final String rankingQuery = "SELECT * FROM users ORDER BY points DESC;";

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void register(User u, String password) throws RegistrationError {
		try {
			retrieve(u.getLogin());
			throw new RegistrationError("Login already registered");
		} catch (AuthenticationError e) {}

		u.genPasswordHash(password);

		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(insertQuery);
			stm.setString(1, u.getLogin());
			stm.setString(2, u.getEmail());
			stm.setString(3, u.getName());
			stm.setString(4, u.getPwHash());
			stm.setString(5, u.getSalt());
			stm.setInt(6, u.getPoints());
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public User retrieve(String login) throws AuthenticationError {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(retrieveQuery);
			stm.setString(1, login);
			ResultSet rs = stm.executeQuery();
			if (!rs.next()) {
				throw new AuthenticationError("Invalid user credentials");
			}
			return resultSet2Users(rs);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void authenticate(String login, String password) throws AuthenticationError {
		retrieve(login).checkPassword(password);
	}

	@Override
	public void addPoints(String login, int points) throws AuthenticationError {
		retrieve(login);

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
				ranking.add(resultSet2Users(rs));
			}
			return ranking;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	private User resultSet2Users(ResultSet rs) throws SQLException {
		String login = rs.getString("login");
		String email = rs.getString("email");
		String name = rs.getString("name");
		String pwhash = rs.getString("pwhash");
		String salt = rs.getString("salt");
		int points = rs.getInt("points");

		return new User(login, email, name, pwhash, salt, points);
	}

}