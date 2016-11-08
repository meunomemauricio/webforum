package functional;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.Cookie;

public class InsertPostFunctionalTests extends FunctionalTests {

	@Test
	public void postListPageWithoutLogin() throws Exception {
		goToPage("posts");
		waitUntilErrorMessage("It's necessary to be logged in to load that page.");
	}

	@Test
	public void insertPostPageWithoutLogin() throws Exception {
		goToPage("insert");
		waitUntilErrorMessage("It's necessary to be logged in to load that page.");
	}

	@Test
	public void createNewPostEmptyTitle() throws Exception {
		setupDatabase("only_user.xml");
		loginUser("mauricio", "p4$$w0rd");

		clickItemByText("+ New Post");
		waitForTitle("New Post - Web Forum");
		fillAndSubmitPostForm("", "Any Content");

		expectInvalidInputField("title");
	}

	@Test
	public void createNewPostEmptyContent() throws Exception {
		setupDatabase("only_user.xml");

		loginUser("mauricio", "p4$$w0rd");

		clickItemByText("+ New Post");
		waitForTitle("New Post - Web Forum");
		fillAndSubmitPostForm("Title", "");

		expectInvalidTextArea("content");
	}

	@Test
	public void createNewPost() throws Exception {
		setupDatabase("only_user.xml");
		loginUser("mauricio", "p4$$w0rd");

		clickItemByText("+ New Post");
		waitForTitle("New Post - Web Forum");
		fillAndSubmitPostForm("First Post", "First Post Content");
		waitForTitle("Posts - Web Forum");

		openPostByTitle("First Post");
	}

	@Test
	public void createNewPostWithSpecialCharacters() throws Exception {
		setupDatabase("only_user.xml");
		loginUser("mauricio", "p4$$w0rd");
		clickItemByText("+ New Post");
		waitForTitle("New Post - Web Forum");

		fillAndSubmitPostForm("Fírßt Pøsŧ", "Fírst Pøst ©ont€nt");
		waitForTitle("Posts - Web Forum");

		openPostByTitle("Fírßt Pøsŧ");
	}

	@Test
	public void postInsertionWithoutTitle() throws Exception {
		setupDatabase("only_user.xml");

		loginUser("mauricio", "p4$$w0rd");

		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("insert", "content=content", cookies));
	}

	@Test
	public void postInsertionEmptyTitle() throws Exception {
		setupDatabase("only_user.xml");

		loginUser("mauricio", "p4$$w0rd");

		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("insert", "title&content=content", cookies));
	}

	@Test
	public void postInsertionWithoutContent() throws Exception {
		setupDatabase("only_user.xml");

		loginUser("mauricio", "p4$$w0rd");

		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("insert", "title=title", cookies));
	}

	@Test
	public void postInsertionEmptyContent() throws Exception {
		setupDatabase("only_user.xml");

		loginUser("mauricio", "p4$$w0rd");

		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("insert", "title=title&content", cookies));
	}
}
