package functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FunctionalTests {

	private static WebDriver _driver;
	private static WebDriverWait _wait;

	private static final String baseUrl = "http://localhost:8080/WebForum/";;

	private static StringBuffer verificationErrors = new StringBuffer();

	private static JdbcDatabaseTester _jdt;


	@BeforeClass
	public static void setUpClass() throws Exception {
		_driver = new FirefoxDriver();
		_driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		_wait = new WebDriverWait(_driver, 5);

		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");

	}

	@Before
	public void setUp() throws Exception {
		// Make sure user is not logged from the test before
		goToPage("logout");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

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

	private void goToPage(String page) {
		_driver.get(baseUrl + page);
	}

	private void doLogin(String login, String password) {
		goToPage("login");
		_driver.findElement(By.name("login")).sendKeys(login);
		_driver.findElement(By.name("password")).sendKeys(password);
		_driver.findElement(By.cssSelector("button")).click();
	}

	private void waitForTitle(String title) {
		_wait.until(ExpectedConditions.titleIs(title));
	}

	private void waitUntilErrorMessage(String message) {
		_wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error")));

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals(message, error_msg);
	}

	private void fillRegisterForm(String login, String password, String name, String email) {
		_driver.findElement(By.name("login")).sendKeys(login);
		_driver.findElement(By.name("password")).sendKeys(password);
		_driver.findElement(By.name("name")).sendKeys(name);
		_driver.findElement(By.name("email")).sendKeys(email);
		_driver.findElement(By.cssSelector("button")).click();
	}

	private void fillPostForm(String title, String content) {
		_driver.findElement(By.name("title")).clear();
		_driver.findElement(By.name("content")).clear();
		_driver.findElement(By.name("title")).sendKeys(title);
		_driver.findElement(By.name("content")).sendKeys(content);
		_driver.findElement(By.cssSelector("button")).click();
	}


	private void fillAndSubmitCommentForm(String comment) {
		_driver.findElement(By.name("comment")).clear();
	    _driver.findElement(By.name("comment")).sendKeys(comment);
	    _driver.findElement(By.cssSelector("button")).click();
	}

	private void setupDatabase(String file) {
		DataFileLoader loader = new FlatXmlDataFileLoader();
		IDataSet dataSet = loader.load(String.format("/datasets/%s", file));
		_jdt.setDataSet(dataSet);
		try {
			_jdt.onSetup();
		} catch (Exception e) {
			throw new RuntimeException("Could not load XML file: " + e);
		}
	}
}
