package model.posts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostManager implements PostDAO {

	private final String insertQuery = "INSERT INTO posts(title, content, login, votes) VALUES (?, ?, ?, ?);";
	private final String listQuery = "SELECT * FROM posts ORDER BY post_id DESC;";
	private final String recuperaQuery = "SELECT * FROM posts WHERE post_id=?;";

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insert(Post post) {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(insertQuery);
			stm.setString(1, post.getTitle());
			stm.setString(2, post.getContent());
			stm.setString(3, post.getLogin());
			stm.setInt(4, post.getVotes());
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Post> listPosts() {
		List<Post> posts = new ArrayList<>();
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(listQuery);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("post_id");
				String title = rs.getString("title");
				String content = rs.getString("content");
				String login = rs.getString("login");
				int votes = rs.getInt("votes");
				posts.add(new Post(id, title, content, login, votes));
			}
			return posts;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Post retrieve(int id) throws InvalidPost {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(recuperaQuery);
			stm.setInt(1, id);
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				String title = rs.getString("title");
				String content = rs.getString("content");
				String login = rs.getString("login");
				int votes = rs.getInt("votes");
				return new Post(id, title, content, login, votes);
			}
			throw new InvalidPost("Could not find this post.");
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

}
