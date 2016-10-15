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
import org.junit.AfterClass;
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

		_wait = new WebDriverWait(_driver, 15);

		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");
	}

	@After
	public void tearDown() throws Exception {
		// Assegura que usuário não fica logado para o próximo teste
		vaiParaPagina("logout");
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
	public void registraNovoUsuario() throws Exception {
		setupDatabase("vazio.xml");

		vaiParaPagina("cadastro");
		preencheFormularioCadastro("mauricio", "s3n#A", "Mauricio Freitas", "mauricio@mail.com");
		aguardaPorTitulo("Login - Web Forum");

		verificaDatabase("unico_usuario.xml");
	}

	@Test
	public void registraUsuarioExistente() throws Exception {
		setupDatabase("unico_usuario.xml");

		vaiParaPagina("cadastro");
		preencheFormularioCadastro("mauricio", "s3n#A", "Mauricio Freitas", "mauricio@mail.com");
		aguardaPorMsgErro();

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("Este login já está cadastrado.", error_msg);

		verificaDatabase("unico_usuario.xml");
	}

	@Test
	public void loginBemSucedido() throws Exception {
		setupDatabase("unico_usuario.xml");

		fazLogin("mauricio", "s3n#A");

		aguardaPorTitulo("Tópicos - Web Forum");
	}

	@Test
	public void loginUsuarioNaoRegistrado() throws Exception {
		setupDatabase("unico_usuario.xml");

		fazLogin("inexistente", "senhaqualquer");
		aguardaPorMsgErro();

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("Não foi possível autenticar o usuário", error_msg);
	}

	@Test
	public void loginSenhaErrada() throws Exception {
		setupDatabase("unico_usuario.xml");

		fazLogin("mauricio", "senhaerrada");
		aguardaPorMsgErro();

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("Não foi possível autenticar o usuário", error_msg);
	}

	@Test
	public void acessaTopicosSemLogar() throws Exception {
		vaiParaPagina("topicos");
		aguardaPorMsgErro();

		String error_msg = _driver.findElement(By.cssSelector("p.error")).getText();
	    assertEquals("É necessário estar logado para acessar aquela página.", error_msg);
	}

	@Test
	public void criaNovoTopico() throws Exception {
		setupDatabase("unico_usuario.xml");

		fazLogin("mauricio", "s3n#A");
		aguardaPorTitulo("Tópicos - Web Forum");

		_driver.findElement(By.linkText("+ Novo Tópico")).click();
		aguardaPorTitulo("Novo Tópico - Web Forum");

		preencheFormularioTopico("Primeiro Topico", "Conteudo Primeiro Topico");
		aguardaPorTitulo("Tópicos - Web Forum");

		String titulo = _driver.findElement(By.cssSelector("p.list-item-title")).getText();
		assertEquals("Primeiro Topico", titulo);
	}

	@Test
	public void criaNovoComentario() throws Exception {
		setupDatabase("unico_topico.xml");

		fazLogin("mauricio", "s3n#A");
		aguardaPorTitulo("Tópicos - Web Forum");

		_driver.findElement(By.cssSelector("p.list-item-title")).click();
		aguardaPorTitulo("Primeiro Topico - Web Forum");

	    preencheComentario("Primeiro Comentario");
	    _wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.topic-cmt")));

	    String comentario = _driver.findElement(By.cssSelector("p.topic-cmt-text")).getText();
		assertEquals("Primeiro Comentario", comentario);
	}

	private void vaiParaPagina(String pagina) {
		_driver.get(baseUrl + pagina);
	}

	private void fazLogin(String login, String senha) {
		vaiParaPagina("login");
		_driver.findElement(By.name("login")).sendKeys(login);
		_driver.findElement(By.name("senha")).sendKeys(senha);
		_driver.findElement(By.cssSelector("button")).click();
	}

	private void aguardaPorTitulo(String titulo) {
		_wait.until(ExpectedConditions.titleIs(titulo));
	}

	private void aguardaPorMsgErro() {
		_wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p.error")));
	}

	private void preencheFormularioCadastro(String login, String senha, String nome, String email) {
		_driver.findElement(By.name("login")).sendKeys(login);
		_driver.findElement(By.name("senha")).sendKeys(senha);
		_driver.findElement(By.name("nome")).sendKeys(nome);
		_driver.findElement(By.name("email")).sendKeys(email);
		_driver.findElement(By.cssSelector("button")).click();
	}

	private void preencheFormularioTopico(String titulo, String conteudo) {
		_driver.findElement(By.name("titulo")).clear();
		_driver.findElement(By.name("conteudo")).clear();
		_driver.findElement(By.name("titulo")).sendKeys(titulo);
		_driver.findElement(By.name("conteudo")).sendKeys(conteudo);
		_driver.findElement(By.cssSelector("button")).click();
	}


	private void preencheComentario(String comentario) {
		_driver.findElement(By.name("comentario")).clear();
	    _driver.findElement(By.name("comentario")).sendKeys(comentario);
	    _driver.findElement(By.cssSelector("button")).click();
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
