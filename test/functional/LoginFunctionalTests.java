package functional;

import org.junit.Test;

public class LoginFunctionalTests extends FunctionalTests {

	@Test
	public void sucessfulLogin() throws Exception {
		setupDatabase("only_user.xml");

		doLogin("mauricio", "p4$$w0rd");

		waitForTitle("Posts - Web Forum");
	}

	@Test
	public void unregisteredUserLogin() throws Exception {
		setupDatabase("only_user.xml");

		doLogin("inexistente", "anypw");
		waitUntilErrorMessage("Invalid user credentials");
	}

	@Test
	public void wrongPasswordLogin() throws Exception {
		setupDatabase("only_user.xml");

		doLogin("mauricio", "wrongpw");
		waitUntilErrorMessage("Invalid user credentials");
	}

}
