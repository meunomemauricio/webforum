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

import model.posts.InvalidPost;
import model.posts.Post;
import model.posts.PostDAO;
import model.posts.PostManager;

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

	@Test
	public void addVotesToExistingPost() throws Exception {
		setupDatabase("only_post.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);
		int id = post.getId();

		_postMgmt.addVotes(id, 5);

        assertDatabase("post_with_5_votes.xml");
	}

	@Test
	public void incrementExistingPoints() throws Exception {
		setupDatabase("post_with_5_votes.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);
		int id = post.getId();

		_postMgmt.addVotes(id, 5);

		assertDatabase("post_with_10_votes.xml");
	}

	@Test
	public void negativeVotes() throws Exception {
		setupDatabase("post_with_5_votes.xml");
		List<Post> posts = _postMgmt.listPosts();
		Post post = posts.get(0);
		int id = post.getId();

		_postMgmt.addVotes(id, -10);

		assertDatabase("post_with_negative_votes.xml");
	}

	@Test
	public void addVotesToInvalidPost() {
		setupDatabase("empty_db.xml");

		try {
			_postMgmt.addVotes(0, 5);
			fail("Should not be able to add votes.");
		} catch (InvalidPost e) {}
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
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(
					new File(String.format("test//datasets/%s", file)));

			ITable actualTable = databaseDataSet.getTable("posts");
			ITable expectedTable = expectedDataSet.getTable("posts");
			// It's necessary to exclude the post_id column from verification
			ITable filteredTable = DefaultColumnFilter.includedColumnsTable(
					actualTable, expectedTable.getTableMetaData().getColumns());
			Assertion.assertEquals(expectedTable, filteredTable);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível verificar Tabela:" + e);
		}
	}
}