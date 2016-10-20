package model.comments;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentManager implements CommentsDAO {

	private final String insertQuery = "INSERT INTO comments(content, login, post_id) VALUES (?, ?, ?);";
	private final String recoverQuery = "SELECT * FROM comments WHERE post_id=? ORDER BY comment_id ASC;";

	static {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addComment(Comment cmnt) {
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(insertQuery);
			stm.setString(1, cmnt.getContent());
			stm.setString(2, cmnt.getLogin());
			stm.setInt(3, cmnt.getPostId());
			stm.executeUpdate();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<Comment> retrieveComments(int postId) {
		List<Comment> comments = new ArrayList<>();
		try(Connection con = DriverManager.getConnection(
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin")) {
			PreparedStatement stm = con.prepareStatement(recoverQuery);
			stm.setInt(1, postId);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				String content = rs.getString("content");
				String login = rs.getString("login");
				int id = rs.getInt("post_id");
				comments.add(new Comment(id, content, login, postId));
			}
			return comments;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

}
