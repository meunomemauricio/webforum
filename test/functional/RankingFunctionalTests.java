package functional;

import org.junit.Test;

public class RankingFunctionalTests extends FunctionalTests {

	@Test
	public void rankingPageWithoutLogin() throws Exception {
		goToPage("ranking");
		waitUntilErrorMessage("It's necessary to be logged in to load that page.");
	}

}
