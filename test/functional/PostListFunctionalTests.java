package functional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

public class PostListFunctionalTests extends FunctionalTests {

	@Test
	public void postsPageWithoutLogin() throws Exception {
		goToPage("posts");
		waitUntilErrorMessage("It's necessary to be logged in to load that page.");
	}

	@Test
	public void createNewPost() throws Exception {
		setupDatabase("only_user.xml");

		doLogin("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");

		_driver.findElement(By.linkText("+ New Post")).click();
		waitForTitle("New Post - Web Forum");

		fillPostForm("First Post", "First Post Content");
		waitForTitle("Posts - Web Forum");

		String title = _driver.findElement(By.cssSelector("p.list-item-title")).getText();
		assertEquals("First Post", title);
	}

}
