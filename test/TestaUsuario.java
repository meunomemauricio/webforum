import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Before;
import org.junit.Test;

import model.GerenciaUsuario;
import model.Usuario;
import model.UsuarioDAO;
import model.UsuarioInexistenteError;

public class TestaUsuario {

	JdbcDatabaseTester _jdt;
	UsuarioDAO _gerUser;

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");

		_gerUser = new GerenciaUsuario();
	}

	@Test
	public void registraNovoUsuario() {
		setupDatabase("vazio.xml");

		Usuario u = new Usuario("mauricio", "mauricio@mail.com", "Mauricio Freitas", "s3n#A", 0);
		_gerUser.inserir(u);

		verificaDatabase("unico_usuario.xml");
	}

	@Test
	public void recuperaUsuarioExistente() throws Exception {
		setupDatabase("unico_usuario.xml");

		Usuario u = _gerUser.recuperar("mauricio");

		assertEquals("mauricio", u.getLogin());
		assertEquals("mauricio@mail.com", u.getEmail());
		assertEquals("Mauricio Freitas", u.getNome());
		assertEquals("s3n#A", u.getSenha());
		assertEquals(0, u.getPontos());
	}

	@Test
	public void recuperarUsuarioNaoExistente() {
		setupDatabase("vazio.xml");

		assertNull(_gerUser.recuperar("inexistente"));
	}

	@Test
	public void autenticaUsuarioNaoCadastrado() {
		setupDatabase("vazio.xml");

		assertFalse(_gerUser.autenticar("naocadastrado", "senha"));
	}

	@Test
	public void autenticaUsuarioSenhaCorreta(){
		setupDatabase("unico_usuario.xml");

		assertTrue(_gerUser.autenticar("mauricio", "s3n#A"));
	}

	@Test
	public void autenticaUsuarioSenhaIncorreta(){
		setupDatabase("unico_usuario.xml");

		assertFalse(_gerUser.autenticar("mauricio", "incorreta"));
	}

	@Test
	public void adicionaPontosUsuarioExistente() throws Exception {
		setupDatabase("unico_usuario.xml");

		_gerUser.adicionarPontos("mauricio", 5);

		verificaDatabase("usuario_com_pontos.xml");
	}

	@Test
	public void adicionaPontosUsuarioInexistente() {
		setupDatabase("vazio.xml");

		try {
			_gerUser.adicionarPontos("inexistente", 5);
			fail("Não deveria ser possível adicionar pontos a usuário inexistente.");
		} catch (UsuarioInexistenteError e) {}

	}

	@Test
	public void rankingVazio() {
		setupDatabase("vazio.xml");

		List<Usuario> ranking = _gerUser.ranking();

		assertEquals(0, ranking.size());
	}

	@Test
	public void rankingUmUsuario() {
		setupDatabase("usuario_com_pontos.xml");

		List<Usuario> ranking = _gerUser.ranking();

		assertEquals(1, ranking.size());

		assertEquals("mauricio", ranking.get(0).getLogin());
		assertEquals(5, ranking.get(0).getPontos());
	}

	@Test
	public void rankingMultiplosUsuarios() {
		setupDatabase("tres_usuarios.xml");

		List<Usuario> ranking = _gerUser.ranking();

		assertEquals(3, ranking.size());

		assertEquals("joao", ranking.get(0).getLogin());
		assertEquals("maria", ranking.get(1).getLogin());
		assertEquals("jose", ranking.get(2).getLogin());

		assertEquals(15, ranking.get(0).getPontos());
		assertEquals(10, ranking.get(1).getPontos());
		assertEquals(5, ranking.get(2).getPontos());
	}

	@Test
	public void pequenoTesteFuncional() throws Exception {
		setupDatabase("vazio.xml");

		_gerUser.inserir(new Usuario("joao", "joao@mail.com", "Joao", "joao123", 0));
		_gerUser.inserir(new Usuario("jose", "jose@mail.com", "Jose", "jose123", 0));
		_gerUser.inserir(new Usuario("maria", "maria@mail.com", "Maria", "maria123", 0));

		_gerUser.adicionarPontos("joao", 15);
		_gerUser.adicionarPontos("jose", 5);
		_gerUser.adicionarPontos("maria", 10);

		verificaDatabase("tres_usuarios.xml");
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