package functional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RegisterFunctionalTests extends FunctionalTests {

	@Test
	public void registerNewUser() throws Exception {
		setupDatabase("empty_db.xml");

		goToPage("register");
		fillRegisterForm("mauricio", "p4$$w0rd", "Mauricio Freitas", "mauricio@mail.com");
		waitForTitle("Login - Web Forum");
	}

	@Test
	public void registerExistingUser() throws Exception {
		setupDatabase("only_user.xml");

		goToPage("register");
		fillRegisterForm("mauricio", "p4$$w0rd", "Mauricio Freitas", "mauricio@mail.com");
		waitUntilErrorMessage("Login already registered");
	}

	@Test
	public void registerUserWithShortPassword() throws Exception {
		setupDatabase("empty_db.xml");

		goToPage("register");
		fillRegisterForm("mauricio", "123457", "Mauricio Freitas", "mauricio@mail.com");
		waitUntilErrorMessage("Password too short");
	}

	@Test
	public void registerEmptyLogin() throws Exception {
		goToPage("register");
		fillRegisterForm("", "", "", "");

		expectInvalidInputField("login");
	}

	@Test
	public void registerEmptyPassword() throws Exception {
		goToPage("register");
		fillRegisterForm("mauricio", "", "", "");

		expectInvalidInputField("password");
	}

	@Test
	public void postRegistrationWithoutLogin() throws Exception {
		assertEquals(400, sendPost("register", ""));
	}

	@Test
	public void postRegistrationEmptyLogin() throws Exception {
		assertEquals(400, sendPost("register", "login"));
	}

	@Test
	public void postRegistrationWithoutPassword() throws Exception {
		assertEquals(400, sendPost("register", "login=login"));
	}

	@Test
	public void postRegistrationEmptyPassword() throws Exception {
		assertEquals(400, sendPost("register", "login=login&password"));
	}
}
