package functional;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;

public class RankingFunctionalTests extends FunctionalTests {

	/**
	 * Try to GET the vote page after login
	 *
	 * The server should send a redirect to the login page
	 */
	@Test
	public void getVotePage() throws Exception {
		goToPage("vote");
		waitForTitle("Login - Web Forum");
	}

	@Test
	public void rankingPageWithoutLogin() throws Exception {
		goToPage("ranking");
		waitUntilErrorMessage("It's necessary to be logged in to load that page.");
	}

	@Test
	public void upVotePost() throws Exception {
		setupDatabase("post_with_5_votes.xml");
		fillAndSubmitLoginForm("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");
		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		waitForTitle("First Post - Web Forum");

		clickItemByID("rUpvote");

		waitForNumberOfVotes("6");
		verifyRankingPosition("mauricio", "1", "6");
	}

	@Test
	public void downVotePost() throws Exception {
		setupDatabase("post_with_5_votes.xml");
		fillAndSubmitLoginForm("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");
		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		waitForTitle("First Post - Web Forum");

		clickItemByID("rDownvote");

		waitForNumberOfVotes("4");
		verifyRankingPosition("mauricio", "1", "4");
	}

	@Test
	public void rankingOrder() throws Exception {
		setupDatabase("three_users.xml");

		fillAndSubmitLoginForm("maria", "maria1234");
		waitForTitle("Posts - Web Forum");

		verifyRankingPosition("joao", "1", "15");
		verifyRankingPosition("maria", "2", "10");
		verifyRankingPosition("jose", "3", "5");
	}

	@Test
	public void postWithoutLogin() throws Exception {
		String params = "post_id=0&vote=up";
		assertEquals(400, sendPost("vote", params));
	}

	@Test
	public void postInvalidVote() throws Exception {
		setupDatabase("post_with_5_votes.xml");
		fillAndSubmitLoginForm("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");
		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		waitForTitle("First Post - Web Forum");

		String params = String.format("post_id=%s&vote=invalid", getPostIdFromCurrentURL());
		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("vote", params, cookies));
	}

	@Test
	public void postInvalidPostId() throws Exception {
		setupDatabase("post_with_5_votes.xml");
		fillAndSubmitLoginForm("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");

		String params = "post_id=invalid&vote=up";
		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(400, sendPost("vote", params, cookies));
	}

	@Test
	public void postInexistingPostId() throws Exception {
		setupDatabase("post_with_5_votes.xml");
		fillAndSubmitLoginForm("mauricio", "p4$$w0rd");
		waitForTitle("Posts - Web Forum");

		String params = "post_id=0&vote=up";
		Set<Cookie> cookies = _driver.manage().getCookies();
		assertEquals(404, sendPost("vote", params, cookies));
	}
}