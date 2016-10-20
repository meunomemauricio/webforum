package unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

import model.InvalidPost;
import model.Post;
import model.PostDAO;
import model.PostManager;

public class TestPosts {

	private JdbcDatabaseTester _jdt;
	private PostDAO _postMgmt = new PostManager();

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");
	}

	@Test
	public void insertNewPost() throws Exception {
		setupDatabase("only_user.xml");

		Post post = new Post("First Post", "First Post Content", "mauricio");
		_postMgmt.insert(post);

		assertDatabase("only_post.xml");
	}

	@Test
	public void retrievePostList() throws Exception {
		setupDatabase("only_post.xml");

		List<Post> posts = _postMgmt.listPosts();

		assertEquals(1, posts.size());
		assertEquals(posts.get(0).getTitle(), "First Post");
	}

	@Test
	public void retrieveListWithMultiplePosts() throws Exception {
		setupDatabase("multiple_posts.xml");

		List<Post> posts = _postMgmt.listPosts();

		assertEquals(5, posts.size());
		// Assert list is sorted with the most recent post first
		assertEquals(posts.get(0).getTitle(), "Fifth Post");
		assertEquals(posts.get(2).getTitle(), "Third Post");
		assertEquals(posts.get(4).getTitle(), "First Post");
	}

	@Test
	public void retrievePostWithInvalidID() throws Exception {
		setupDatabase("only_post.xml");

		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);
		int id = post.getId();

		Post retrievedPost = _postMgmt.retrieve(id);

		assertEquals(post, retrievedPost);
	}

	@Test
	public void retreiveInexistingPost() throws Exception {
		setupDatabase("only_user.xml");

		try {
			_postMgmt.retrieve(0);
			fail("No post should be recovered.");
		} catch (InvalidPost e) {};
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
			// It's necessary to exclude the id_topico column from verification
			ITable filteredTable = DefaultColumnFilter.includedColumnsTable(
					actualTable, expectedTable.getTableMetaData().getColumns());
			Assertion.assertEquals(expectedTable, filteredTable);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível verificar Tabela:" + e);
		}
	}


}