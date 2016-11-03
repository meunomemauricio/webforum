package functional;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.Cookie;

public class CommentsFunctionalTests extends FunctionalTests {

	@Test
	public void createNewComment() throws Exception {
		setupDatabase("only_post.xml");
		loginUser("mauricio", "p4$$w0rd");
		openPostByTitle("First Post");

	    fillAndSubmitCommentForm("First Comment");

	    waitForCommentToBeDisplayed("First Comment", "mauricio");
	}

	@Test
	public void createNewCommentWithSpecialCharacters() throws Exception {
		setupDatabase("only_post.xml");
		loginUser("mauricio", "p4$$w0rd");
		openPostByTitle("First Post");

	    fillAndSubmitCommentForm("Fírßt Cømm€nŧ");

	    waitForCommentToBeDisplayed("Fírßt Cømm€nŧ", "mauricio");
	}

	@Test
	public void submitEmptyComment() throws Exception {
		setupDatabase("only_post.xml");
		loginUser("mauricio", "p4$$w0rd");
		openPostByTitle("First Post");

	    fillAndSubmitCommentForm("");

		expectInvalidTextArea("comment");
	}

	@Test
	public void postCommentWithoutContent() throws Exception {
		setupDatabase("only_post.xml");
		loginUser("mauricio", "p4$$w0rd");
		openPostByTitle("First Post");

		String params = String.format("postId=%s", getPostIdFromCurrentURL());
		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("post", params, cookies));
	}

	@Test
	public void postCommentWithEmptyContent() throws Exception {
		setupDatabase("only_post.xml");
		loginUser("mauricio", "p4$$w0rd");
		openPostByTitle("First Post");

		String params = String.format("postId=%s&comment=", getPostIdFromCurrentURL());
		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("post", params, cookies));
	}
}
