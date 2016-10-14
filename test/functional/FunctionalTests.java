package functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FunctionalTests {

	private WebDriver _driver;
	private WebDriverWait _wait;

	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();

	private JdbcDatabaseTester _jdt;

	@Before
		public void setUp() throws Exception {
		_driver = new FirefoxDriver();
		baseUrl = "http://localhost:8080/";
		_driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		_wait = new WebDriverWait(_driver, 15);

		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");
	}

	@After
	public void tearDown() throws Exception {
		// Assegura que usuário não fica logado para o próximo teste
		_driver.get(baseUrl + "WebForum/logout");

		_driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	@Test
	public void registraNovoUsuario() throws Exception {
		setupDatabase("vazio.xml");

		_driver.get(baseUrl + "WebForum/cadastro");
		_driver.findElement(By.name("login")).sendKeys("mauricio");
		_driver.findElement(By.name("senha")).sendKeys("s3n#A");
		_driver.findElement(By.name("nome")).sendKeys("Mauricio Freitas");
		_driver.findElement(By.name("email")).sendKeys("mauricio@mail.com");
		_driver.findElement(By.cssSelector("button")).click();
		_wait.until(ExpectedConditions.titleIs("Login - Web Forum"));

		verificaDatabase("unico_usuario.xml");
	}

	@Test
	public void registraUsuarioExistente() throws Exception {
		setupDatabase("unico_usuario.xml");

		_driver.get(baseUrl + "WebForum/cadastro");
		_driver.findElement(By.name("login")).sendKeys("mauricio");
		_driver.findElement(By.cssSelector("button")).click();
		_wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error")));

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("Este login já está cadastrado.", error_msg);

		verificaDatabase("unico_usuario.xml");
	}

	@Test
	public void loginBemSucedido() throws Exception {
		setupDatabase("unico_usuario.xml");

		_driver.get(baseUrl + "WebForum/login");
		_driver.findElement(By.name("login")).sendKeys("mauricio");
		_driver.findElement(By.name("senha")).sendKeys("s3n#A");
		_driver.findElement(By.cssSelector("button")).click();

		_wait.until(ExpectedConditions.titleIs("Tópicos - Web Forum"));
	}

	@Test
	public void loginUsuarioNaoRegistrado() throws Exception {
		setupDatabase("unico_usuario.xml");

		_driver.get(baseUrl + "WebForum/login");
		_driver.findElement(By.name("login")).sendKeys("inexistente");
		_driver.findElement(By.name("senha")).sendKeys("senhaqualquer");
		_driver.findElement(By.cssSelector("button")).click();
		_wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error")));

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("Não foi possível autenticar o usuário", error_msg);
	}

	@Test
	public void loginSenhaErrada() throws Exception {
		setupDatabase("unico_usuario.xml");

		_driver.get(baseUrl + "WebForum/login");
		_driver.findElement(By.name("login")).sendKeys("mauricio");
		_driver.findElement(By.name("senha")).sendKeys("senhaerrada");
		_driver.findElement(By.cssSelector("button")).click();
		_wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error")));

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("Não foi possível autenticar o usuário", error_msg);
	}

	@Test
	public void acessaTopicosSemLogar() throws Exception {
		_driver.get(baseUrl + "WebForum/topicos");
		_wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error")));

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("É necessário estar logado para acessar aquela página.", error_msg);
	}

	private void setupDatabase(String file) {
		DataFileLoader loader = new FlatXmlDataFileLoader();
		IDataSet dataSet = loader.load(String.format("/datasets/%s", file));
		_jdt.setDataSet(dataSet);
		try {
			_jdt.onSetup();
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível carregar o arquivo XML: " + e);
		}
	}

	private void verificaDatabase(String file) {
		try {
			IDataSet databaseDataSet = _jdt.getConnection().createDataSet();
			ITable actualTable = databaseDataSet.getTable("usuario");
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(
					new File(String.format("test//datasets/%s", file)));
			ITable expectedTable = expectedDataSet.getTable("usuario");
			Assertion.assertEquals(expectedTable, actualTable);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível verificar Tabela:" + e);
		}
	}

}
