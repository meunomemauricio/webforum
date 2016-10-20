package unit;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Before;
import org.junit.Test;

import model.comments.Comment;
import model.comments.CommentManager;
import model.comments.CommentsDAO;
import model.posts.Post;
import model.posts.PostDAO;
import model.posts.PostManager;

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
	public void insertComment() {
		setupDatabase("only_post.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);

		Comment cmnt = new Comment("First Comment", post.getLogin(), post.getId());
		_cmntMgmt.addComment(cmnt);

		assertDatabase("only_comment.xml");
	}

	@Test
	public void retrieveComment() {
		setupDatabase("only_post.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);

		Comment cmnt = new Comment("Primeiro Comentario", post.getLogin(), post.getId());
		_cmntMgmt.addComment(cmnt);

		List<Comment> comments = _cmntMgmt.retrieveComments(post.getId());

		assertEquals(1, comments.size());
		assertEquals("Primeiro Comentario", comments.get(0).getContent());
	}

	@Test
	public void retrieveMultipleComments() {
		setupDatabase("multiple_posts.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post1 = posts.get(0);
		Post post2 = posts.get(1);

		Comment cmnt = new Comment("Primeiro Comentario", post1.getLogin(), post1.getId());
		_cmntMgmt.addComment(cmnt);

		cmnt = new Comment("Segundo Comentario", post1.getLogin(), post1.getId());
		_cmntMgmt.addComment(cmnt);

		cmnt = new Comment("Terceiro Comentario", post1.getLogin(), post1.getId());
		_cmntMgmt.addComment(cmnt);

		cmnt = new Comment("Quarto Comentario", post2.getLogin(), post2.getId());
		_cmntMgmt.addComment(cmnt);

		List<Comment> comments = _cmntMgmt.retrieveComments(post1.getId());

		// Assert 3 comments are retrieved, since the other are from another post
		assertEquals(3, comments.size());
		// Assert comments are sorted with the most recent last
		assertEquals("Primeiro Comentario", comments.get(0).getContent());
		assertEquals("Segundo Comentario", comments.get(1).getContent());
		assertEquals("Terceiro Comentario", comments.get(2).getContent());
	}

	private void setupDatabase(String file) {
		DataFileLoader loader = new FlatXmlDataFileLoader();
		IDataSet dataSet = loader.load(String.format("/datasets/%s", file));
		_jdt.setDataSet(dataSet);
		try {
			_jdt.onSetup();
		} catch (Exception e) {
			throw new RuntimeException("Could not load XML file: " + e);
		}
	}

	private void assertDatabase(String file) {
		try {
			IDataSet databaseDataSet = _jdt.getConnection().createDataSet();
			ITable actualTable = databaseDataSet.getTable("usuario");
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(
					new File(String.format("test//datasets/%s", file)));
			ITable expectedTable = expectedDataSet.getTable("usuario");
			Assertion.assertEquals(expectedTable, actualTable);

			actualTable = databaseDataSet.getTable("topico");
			expectedTable = expectedDataSet.getTable("topico");
			// Its necessary to exclude the id_topico column since it's always changing
			ITable filteredTable = DefaultColumnFilter.includedColumnsTable(
					actualTable, expectedTable.getTableMetaData().getColumns());
			Assertion.assertEquals(expectedTable, filteredTable);

			actualTable = databaseDataSet.getTable("comentario");
			expectedTable = expectedDataSet.getTable("comentario");
			filteredTable = DefaultColumnFilter.includedColumnsTable(
					actualTable, expectedTable.getTableMetaData().getColumns());
			Assertion.assertEquals(expectedTable, filteredTable);
		} catch (Exception e) {
			throw new RuntimeException("Could not assert database:" + e);
		}
	}

}
