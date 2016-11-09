package functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FunctionalTests {

	protected static WebDriver _driver;
	protected static WebDriverWait _wait;

	protected static final String baseUrl = "http://localhost:8080/WebForum/";;

	protected static StringBuffer verificationErrors = new StringBuffer();

	protected static JdbcDatabaseTester _jdt;

	private final String db_loc = "jdbc:postgresql:" + System.getenv("WEBFORUM_DB_LOC");
	private final String db_user = System.getenv("WEBFORUM_DB_USER");
	private final String db_pw = System.getenv("WEBFORUM_DB_PW");


	@BeforeClass
	public static void setUpClass() throws Exception {
		_driver = new FirefoxDriver();
		_driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		_wait = new WebDriverWait(_driver, 5);

		_jdt = new JdbcDatabaseTester("org.postgresql.Driver", db_loc, db_user, db_pw);
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

	// Database Methods

	protected void setupDatabase(String file) {
		DataFileLoader loader = new FlatXmlDataFileLoader();
		IDataSet dataSet = loader.load(String.format("/datasets/%s", file));
		_jdt.setDataSet(dataSet);
		try {
			_jdt.onSetup();
		} catch (Exception e) {
			throw new RuntimeException("Could not load XML file: " + e);
		}
	}


	// Action Methods

	protected void goToPage(String page) {
		_driver.get(baseUrl + page);
	}

	protected void waitForTitle(String title) {
		_wait.until(ExpectedConditions.titleIs(title));
	}

	protected void clickItemByID(String id) {
		_driver.findElement(By.id(id)).click();
	}

	protected void clickItemByText(String text) {
		String xpath = String.format("//*[contains(text(), '%s')]", text);
		_driver.findElement(By.xpath(xpath)).click();
	}

	protected void expectInvalidInputField(String fieldName) {
	    WebElement elem = _driver.findElement(By.cssSelector("input:invalid"));
	    assertEquals(fieldName, elem.getAttribute("name"));
	}

	protected void expectInvalidTextArea(String fieldName) {
	    WebElement elem = _driver.findElement(By.cssSelector("textarea:invalid"));
	    assertEquals(fieldName, elem.getAttribute("name"));
	}

	protected void fillAndSubmitRegisterForm(String login, String password, String name, String email) {
		goToPage("register");
		waitForTitle("Register - Web Forum");

		_driver.findElement(By.name("login")).sendKeys(login);
		_driver.findElement(By.name("password")).sendKeys(password);
		_driver.findElement(By.name("name")).sendKeys(name);
		_driver.findElement(By.name("email")).sendKeys(email);
		_driver.findElement(By.cssSelector("button")).click();
	}

	protected void assertRegisterMessage(String message) {
		String regMessage = _driver.findElement(By.id("reg_msg")).getText();
		assertEquals(message, regMessage);
	}

	protected void fillAndSubmitLoginForm(String login, String password) {
		goToPage("login");
		waitForTitle("Login - Web Forum");

		_driver.findElement(By.name("login")).sendKeys(login);
		_driver.findElement(By.name("password")).sendKeys(password);
		_driver.findElement(By.cssSelector("button")).click();
	}

	protected void loginUser(String login, String password) {
		fillAndSubmitLoginForm(login, password);
		waitForTitle("Posts - Web Forum");
	}

	protected void waitUntilErrorMessage(String message) {
		_wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error")));

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals(message, error_msg);
	}

	protected void fillAndSubmitPostForm(String title, String content) {
		_driver.findElement(By.name("title")).clear();
		_driver.findElement(By.name("content")).clear();
		_driver.findElement(By.name("title")).sendKeys(title);
		_driver.findElement(By.name("content")).sendKeys(content);
		_driver.findElement(By.cssSelector("button")).click();
	}

	protected void openPostByTitle(String title) {
		goToPage("posts");
		waitForTitle("Posts - Web Forum");

		String xpath = String.format("//p[contains(text(), '%s')]/..", title);
		_driver.findElement(By.xpath(xpath)).click();

		waitForTitle(String.format("%s - Web Forum", title));
	}

	protected String getPostIdFromCurrentURL() throws Exception {
		String url = _driver.getCurrentUrl();
		Pattern p = Pattern.compile(".*?id=(\\d+)");
		Matcher m = p.matcher(url);

		if (m.find())
			return m.group(1);
		else
			throw new Exception("Could not extract id from URL: " + url);
	}


	protected void fillAndSubmitCommentForm(String comment) {
		_driver.findElement(By.name("comment")).clear();
	    _driver.findElement(By.name("comment")).sendKeys(comment);
	    _driver.findElement(By.cssSelector("button")).click();
	}

	protected void waitForCommentToBeDisplayed(String content, String login) {
		String xpath = String.format("//p[contains(text(), '%s')]/..", content);
	    _wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

	    WebElement cmtDiv = _driver.findElement(By.xpath(xpath));
	    WebElement user = cmtDiv.findElement(By.className("topic-cmt-user"));
	    assertEquals(login, user.getText());
	}

	protected void waitForNumberOfVotes(String votes) {
		_wait.until(ExpectedConditions.textToBe(By.id("votes"), votes));
	}

	protected void verifyRankingPosition(String login, String position, String points) {
		goToPage("ranking");
		waitForTitle("Ranking - Web Forum");

		String id = String.format("%sPos", login);
		WebElement posElement = _driver.findElement(By.id(id));
		assertEquals(position, posElement.getText());

		id = String.format("%sLogin", login);
		WebElement loginElement = _driver.findElement(By.id(id));
		assertEquals(login, loginElement.getText());

		id = String.format("%sPoints", login);
		WebElement pointsElement = _driver.findElement(By.id(id));
		assertEquals(points, pointsElement.getText());
	}

	// Post Methods

	protected int sendPost(String url, String urlParameters) throws Exception {
		URL obj = new URL(baseUrl + url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		return con.getResponseCode();
	}

	protected int sendPost(String url, String urlParameters, Set<Cookie> cookies) throws Exception {
		URL obj = new URL(baseUrl + url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");

		// Set Cookies
		for (Cookie c: cookies) {
			String cookie = String.format("%s=%s", c.getName(), c.getValue());
			con.setRequestProperty("Cookie", cookie);
		}

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		return con.getResponseCode();
	}
}