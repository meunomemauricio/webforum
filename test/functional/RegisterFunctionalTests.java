package functional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

public class RegisterFunctionalTests extends FunctionalTests {

	@Test
	public void registerNewUser() throws Exception {
		setupDatabase("empty_db.xml");

		goToPage("register");
		fillAndSubmitRegisterForm("mauricio", "p4$$w0rd", "Mauricio Freitas", "mauricio@mail.com");
		waitForTitle("Login - Web Forum");

		assertRegisterMessage("New account created successfully");
	}

	@Test
	public void registerExistingUser() throws Exception {
		setupDatabase("only_user.xml");

		goToPage("register");
		fillAndSubmitRegisterForm("mauricio", "p4$$w0rd", "Mauricio Freitas", "mauricio@mail.com");
		waitUntilErrorMessage("Login already registered");
	}

	@Test
	public void registerUserWithShortPassword() throws Exception {
		setupDatabase("empty_db.xml");

		goToPage("register");
		fillAndSubmitRegisterForm("mauricio", "123457", "Mauricio Freitas", "mauricio@mail.com");
		waitUntilErrorMessage("Password too short");
	}

	@Test
	public void registerEmptyLogin() throws Exception {
		goToPage("register");
		fillAndSubmitRegisterForm("", "", "", "");

		expectInvalidInputField("login");
	}

	@Test
	public void registerEmptyPassword() throws Exception {
		goToPage("register");
		fillAndSubmitRegisterForm("mauricio", "", "", "");

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

	private void assertRegisterMessage(String message) {
		String regMessage = _driver.findElement(By.id("reg_msg")).getText();
		assertEquals(message, regMessage);
	}
}


