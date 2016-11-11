package br.eng.mauriciofreitas.integration;

import org.junit.Test;

public class ITLogin extends FunctionalTests {

	@Test
	public void sucessfulLogin() throws Exception {
		setupDatabase("only_user.xml");

		fillAndSubmitLoginForm("mauricio", "p4$$w0rd");

		waitForTitle("Posts - Web Forum");
	}

	@Test
	public void loginUserWithSpecialCharacters() throws Exception {
		setupDatabase("empty_db.xml");
		fillAndSubmitRegisterForm("maurício", "p4ßwórd_", "", "");
		waitForTitle("Login - Web Forum");
		assertRegisterMessage("New account created successfully");

		fillAndSubmitLoginForm("maurício", "p4ßwórd_");

		waitForTitle("Posts - Web Forum");
	}

	@Test
	public void unregisteredUserLogin() throws Exception {
		setupDatabase("only_user.xml");

		fillAndSubmitLoginForm("inexistente", "anypw");

		waitUntilErrorMessage("Invalid user credentials");
	}

	@Test
	public void wrongPasswordLogin() throws Exception {
		setupDatabase("only_user.xml");

		fillAndSubmitLoginForm("mauricio", "wrongpw");

		waitUntilErrorMessage("Invalid user credentials");
	}
}