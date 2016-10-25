package unit;
import static org.junit.Assert.assertEquals;
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

import model.users.AuthenticationError;
import model.users.RegistrationError;
import model.users.SaltGenerator;
import model.users.User;
import model.users.UserDAO;
import model.users.UserManager;
import unit.mocks.MockSaltGenerator;

public class TestUsers {

	JdbcDatabaseTester _jdt;
	UserDAO _userMgmt;

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");

		_userMgmt = new UserManager();
	}

	@Test
	public void registerNewUser() throws Exception {
		setupDatabase("empty_db.xml");

		SaltGenerator mockSalt = new MockSaltGenerator();
		User u = new User("mauricio", "mauricio@mail.com", "Mauricio Freitas", mockSalt);
		_userMgmt.register(u, "p4$$w0rd");

		verificaDatabase("only_user.xml");
	}

	@Test
	public void registerAlreadyExistingUser() {
		setupDatabase("only_user.xml");

		User u = new User("mauricio", "another@email.com", "Another Name");

		try {
			_userMgmt.register(u, "p4$$w0rd");
			fail("User should not have been registered.");
		} catch (RegistrationError e) {}

		verificaDatabase("only_user.xml");
	}

	@Test
	public void registerUserWithShortPassword() {
		setupDatabase("empty_db.xml");

		// Password lower limit should be 8 chars
		User u = new User("mauricio", "mauricio@mail.com", "Mauricio Freitas");

		try {
			_userMgmt.register(u, "1234567");
			fail("Short password should not be accepted.");
		} catch (RegistrationError e) {}

		verificaDatabase("empty_db.xml");
	}

	@Test
	public void retrieveExistingUser() throws Exception {
		setupDatabase("only_user.xml");

		User u = _userMgmt.retrieve("mauricio");

		assertEquals("mauricio", u.getLogin());
		assertEquals("mauricio@mail.com", u.getEmail());
		assertEquals("Mauricio Freitas", u.getName());
		assertEquals(0, u.getPoints());
	}

	@Test
	public void retrieveNonExistingUser() {
		setupDatabase("empty_db.xml");

		try {
			_userMgmt.retrieve("nonexisting");
			fail("Non existing user should not be able to be retrieved");
		} catch (AuthenticationError e) {
		}
	}

	@Test
	public void authenticateUserWithCorrectCredentials() throws Exception {
		setupDatabase("only_user.xml");

		_userMgmt.authenticate("mauricio", "p4$$w0rd");
	}

	@Test
	public void authenticateNonRegisteredUser() {
		setupDatabase("empty_db.xml");

		try {
			_userMgmt.authenticate("nonregistered", "password");
			fail("Authentication error expected");
		} catch (AuthenticationError e) {
		}
	}

	@Test
	public void authenticateUserWithWrongPassword() {
		setupDatabase("only_user.xml");

		try {
			_userMgmt.authenticate("mauricio", "incorreta");
			fail("Authentication error expected");
		} catch (AuthenticationError e) {
		}
	}

	@Test
	public void addPointsToExistingUSer() throws Exception {
		setupDatabase("only_user.xml");

		_userMgmt.addPoints("mauricio", 5);

		verificaDatabase("user_with_5_points.xml");
	}

	@Test
	public void incrementExistingPoints() throws Exception {
		setupDatabase("user_with_5_points.xml");

		_userMgmt.addPoints("mauricio", 5);

		verificaDatabase("user_with_10_points.xml");
	}

	@Test
	public void addPointsToNonRegisteredUser() {
		setupDatabase("empty_db.xml");

		try {
			_userMgmt.addPoints("inexistente", 5);
			fail("Should not be able to add points.");
		} catch (AuthenticationError e) {}

	}

	@Test
	public void emptyRanking() {
		setupDatabase("empty_db.xml");

		List<User> ranking = _userMgmt.rankUsers();

		assertEquals(0, ranking.size());
	}

	@Test
	public void oneUserRanking() {
		setupDatabase("user_with_5_points.xml");

		List<User> ranking = _userMgmt.rankUsers();

		assertEquals(1, ranking.size());

		assertEquals("mauricio", ranking.get(0).getLogin());
		assertEquals(5, ranking.get(0).getPoints());
	}

	@Test
	public void multipleUserRanking() {
		setupDatabase("three_users.xml");

		List<User> ranking = _userMgmt.rankUsers();

		assertEquals(3, ranking.size());

		assertEquals("joao", ranking.get(0).getLogin());
		assertEquals("maria", ranking.get(1).getLogin());
		assertEquals("jose", ranking.get(2).getLogin());

		assertEquals(15, ranking.get(0).getPoints());
		assertEquals(10, ranking.get(1).getPoints());
		assertEquals(5, ranking.get(2).getPoints());
	}

	@Test
	public void smallOverallTest() throws Exception {
		setupDatabase("empty_db.xml");

		SaltGenerator mockSalt = new MockSaltGenerator();

		_userMgmt.register(new User("joao", "joao@mail.com", "Joao", mockSalt), "joao1234");
		_userMgmt.register(new User("jose", "jose@mail.com", "Jose", mockSalt), "jose1234");
		_userMgmt.register(new User("maria", "maria@mail.com", "Maria", mockSalt), "maria1234");

		_userMgmt.addPoints("joao", 15);
		_userMgmt.addPoints("jose", 5);
		_userMgmt.addPoints("maria", 10);

		verificaDatabase("three_users.xml");
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
			ITable actualTable = databaseDataSet.getTable("users");
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(
					new File(String.format("test//datasets/%s", file)));
			ITable expectedTable = expectedDataSet.getTable("users");
			Assertion.assertEquals(expectedTable, actualTable);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível verificar Tabela:" + e);
		}
	}

}