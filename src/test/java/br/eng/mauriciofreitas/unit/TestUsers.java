package br.eng.mauriciofreitas.unit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import br.eng.mauriciofreitas.model.users.AuthenticationError;
import br.eng.mauriciofreitas.model.users.RegistrationError;
import br.eng.mauriciofreitas.model.users.SaltGenerator;
import br.eng.mauriciofreitas.model.users.User;
import br.eng.mauriciofreitas.model.users.UserDAO;
import br.eng.mauriciofreitas.model.users.UserManager;
import br.eng.mauriciofreitas.unit.mocks.MockSaltGenerator;

public class TestUsers {

	JdbcDatabaseTester _jdt;
	UserDAO _userMgmt;

	private final String db_loc = "jdbc:postgresql:" + System.getenv("WEBFORUM_DB_LOC");
	private final String db_user = System.getenv("WEBFORUM_DB_USER");
	private final String db_pw = System.getenv("WEBFORUM_DB_PW");

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester("org.postgresql.Driver", db_loc, db_user, db_pw);
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
	public void registerAlreadyExistingUser() throws Exception {
		setupDatabase("only_user.xml");

		User u = new User("mauricio", "another@email.com", "Another Name");

		try {
			_userMgmt.register(u, "p4$$w0rd");
			fail("User should not have been registered.");
		} catch (RegistrationError e) {}

		verificaDatabase("only_user.xml");
	}

	@Test
	public void registerUserWithShortPassword() throws Exception {
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
	public void retrieveNonExistingUser() throws Exception {
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
	public void authenticateNonRegisteredUser() throws Exception {
		setupDatabase("empty_db.xml");

		try {
			_userMgmt.authenticate("nonregistered", "password");
			fail("Authentication error expected");
		} catch (AuthenticationError e) {
		}
	}

	@Test
	public void authenticateUserWithWrongPassword() throws Exception {
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
	public void addPointsToNonRegisteredUser() throws Exception {
		setupDatabase("empty_db.xml");

		try {
			_userMgmt.addPoints("inexistente", 5);
			fail("Should not be able to add points.");
		} catch (AuthenticationError e) {}

	}

	@Test
	public void emptyRanking() throws Exception {
		setupDatabase("empty_db.xml");

		List<User> ranking = _userMgmt.rankUsers();

		assertEquals(0, ranking.size());
	}

	@Test
	public void oneUserRanking() throws Exception {
		setupDatabase("user_with_5_points.xml");

		List<User> ranking = _userMgmt.rankUsers();

		assertEquals(1, ranking.size());

		assertEquals("mauricio", ranking.get(0).getLogin());
		assertEquals(5, ranking.get(0).getPoints());
	}

	@Test
	public void multipleUserRanking() throws Exception {
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

	private void setupDatabase(String filename) throws Exception {
		InputStream input = this.getClass().getResourceAsStream("/datasets/" + filename);
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		_jdt.setDataSet(builder.build(input));
		_jdt.onSetup();
	}

	private void verificaDatabase(String filename) throws Exception {
		IDataSet databaseDataSet = _jdt.getConnection().createDataSet();
		ITable actualTable = databaseDataSet.getTable("users");

		InputStream input = this.getClass().getResourceAsStream("/datasets/" + filename);
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		IDataSet expectedDataSet = builder.build(input);
		ITable expectedTable = expectedDataSet.getTable("users");

		Assertion.assertEquals(expectedTable, actualTable);
	}
}