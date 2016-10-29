package functional;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CommentsFunctionalTests extends FunctionalTests {

	@Test
	public void createNewComment() throws Exception {
		setupDatabase("only_post.xml");

		doLogin("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");

		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		waitForTitle("First Post - Web Forum");

	    fillAndSubmitCommentForm("First Comment");
	    _wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.topic-cmt")));

	    String comment = _driver.findElement(By.cssSelector("p.topic-cmt-text")).getText();
		assertEquals("First Comment", comment);
	}

	@Test
	public void submitEmptyComment() throws Exception {
		setupDatabase("only_post.xml");

		doLogin("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");

		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		waitForTitle("First Post - Web Forum");

	    fillAndSubmitCommentForm("");

	    _driver.findElement(By.cssSelector("textarea:invalid"));
	}

	@Test
	public void postCommentWithoutContent() throws Exception {
		setupDatabase("only_post.xml");

		doLogin("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");

		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		waitForTitle("First Post - Web Forum");

		String url = baseUrl + "post";
		String params = String.format("postId=%s", getPostIdFromCurrentURL());
		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost(url, params, cookies));
	}

	@Test
	public void postCommentWithEmptyContent() throws Exception {
		setupDatabase("only_post.xml");

		doLogin("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");

		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		waitForTitle("First Post - Web Forum");

		String url = baseUrl + "post";
		String params = String.format("postId=%s&comment=", getPostIdFromCurrentURL());
		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost(url, params, cookies));
	}


}
