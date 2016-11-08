package br.eng.mauriciofreitas.unit;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import br.eng.mauriciofreitas.model.comments.Comment;
import br.eng.mauriciofreitas.model.comments.CommentManager;
import br.eng.mauriciofreitas.model.comments.CommentsDAO;
import br.eng.mauriciofreitas.model.posts.Post;
import br.eng.mauriciofreitas.model.posts.PostDAO;
import br.eng.mauriciofreitas.model.posts.PostManager;

public class TestComments {

	private JdbcDatabaseTester _jdt;
	private PostDAO _postMgmt = new PostManager();
	private CommentsDAO _cmntMgmt = new CommentManager();

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");
	}

	@Test
	public void insertComment() throws Exception {
		setupDatabase("only_post.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);

		Comment cmnt = new Comment("First Comment", post.getLogin(), post.getId());
		_cmntMgmt.addComment(cmnt);

		assertDatabase("only_comment.xml");
	}

	@Test
	public void retrieveComment() throws Exception {
		setupDatabase("only_post.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);

		Comment cmnt = new Comment("First Comment", post.getLogin(), post.getId());
		_cmntMgmt.addComment(cmnt);

		List<Comment> comments = _cmntMgmt.retrieveComments(post.getId());

		assertEquals(1, comments.size());
		assertEquals("First Comment", comments.get(0).getContent());
	}

	@Test
	public void retrieveMultipleComments() throws Exception {
		setupDatabase("multiple_posts.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post1 = posts.get(0);
		Post post2 = posts.get(1);

		Comment cmnt = new Comment("First Comment", post1.getLogin(), post1.getId());
		_cmntMgmt.addComment(cmnt);

		cmnt = new Comment("Second Comment", post1.getLogin(), post1.getId());
		_cmntMgmt.addComment(cmnt);

		cmnt = new Comment("Third Comment", post1.getLogin(), post1.getId());
		_cmntMgmt.addComment(cmnt);

		cmnt = new Comment("Fourth Comment", post2.getLogin(), post2.getId());
		_cmntMgmt.addComment(cmnt);

		List<Comment> comments = _cmntMgmt.retrieveComments(post1.getId());

		// Assert 3 comments are retrieved, since the other are from another post
		assertEquals(3, comments.size());
		// Assert comments are sorted with the most recent last
		assertEquals("First Comment", comments.get(0).getContent());
		assertEquals("Second Comment", comments.get(1).getContent());
		assertEquals("Third Comment", comments.get(2).getContent());
	}

	private void setupDatabase(String filename) throws Exception {
		InputStream input = this.getClass().getResourceAsStream("/datasets/" + filename);
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		_jdt.setDataSet(builder.build(input));
		_jdt.onSetup();
	}

	private void assertDatabase(String filename) throws Exception {
		IDataSet databaseDataSet = _jdt.getConnection().createDataSet();
		ITable actualTable = databaseDataSet.getTable("users");

		InputStream input = this.getClass().getResourceAsStream("/datasets/" + filename);
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		IDataSet expectedDataSet = builder.build(input);

		ITable expectedTable = expectedDataSet.getTable("users");
		Assertion.assertEquals(expectedTable, actualTable);

		actualTable = databaseDataSet.getTable("posts");
		expectedTable = expectedDataSet.getTable("posts");
		// Its necessary to exclude the post_id column since it's always changing
		ITable filteredTable = DefaultColumnFilter.includedColumnsTable(
				actualTable, expectedTable.getTableMetaData().getColumns());
		Assertion.assertEquals(expectedTable, filteredTable);

		actualTable = databaseDataSet.getTable("comments");
		expectedTable = expectedDataSet.getTable("comments");
		filteredTable = DefaultColumnFilter.includedColumnsTable(
				actualTable, expectedTable.getTableMetaData().getColumns());
		Assertion.assertEquals(expectedTable, filteredTable);
	}

}
