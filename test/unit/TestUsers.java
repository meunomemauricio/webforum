package unit;
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

import model.users.InvalidUserError;
import model.users.RegistrationError;
import model.users.User;
import model.users.UserDAO;
import model.users.UserManager;

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

		User u = new User("mauricio", "mauricio@mail.com", "Mauricio Freitas", "s3n#A");
		_userMgmt.insert(u);

		verificaDatabase("only_user.xml");
	}

	@Test
	public void registerAlreadyExistingUser() {
		setupDatabase("only_user.xml");

		User u = new User("mauricio", "another@email.com", "Another Name", "s3n#A");

		try {
			_userMgmt.insert(u);
			fail("User should not have been registered.");
		} catch (RegistrationError e) {}

		verificaDatabase("only_user.xml");
	}

	@Test
	public void retrieveExistingUser() throws Exception {
		setupDatabase("only_user.xml");

		User u = _userMgmt.retrieve("mauricio");

		assertEquals("mauricio", u.getLogin());
		assertEquals("mauricio@mail.com", u.getEmail());
		assertEquals("Mauricio Freitas", u.getName());
		assertEquals("s3n#A", u.getPassword());
		assertEquals(0, u.getPoints());
	}

	@Test
	public void retrieveNonExistingUser() {
		setupDatabase("empty_db.xml");

		assertNull(_userMgmt.retrieve("nonexisting"));
	}

	@Test
	public void authenticateNonRegisteredUser() {
		setupDatabase("empty_db.xml");

		assertFalse(_userMgmt.authenticate("nonregistered", "password"));
	}

	@Test
	public void authenticateUserWithCorrectCredentials(){
		setupDatabase("only_user.xml");

		assertTrue(_userMgmt.authenticate("mauricio", "s3n#A"));
	}

	@Test
	public void authenticateUserWithWrongPassword() {
		setupDatabase("only_user.xml");

		assertFalse(_userMgmt.authenticate("mauricio", "incorreta"));
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
		} catch (InvalidUserError e) {}

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

		_userMgmt.insert(new User("joao", "joao@mail.com", "Joao", "joao123", 0));
		_userMgmt.insert(new User("jose", "jose@mail.com", "Jose", "jose123", 0));
		_userMgmt.insert(new User("maria", "maria@mail.com", "Maria", "maria123", 0));

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