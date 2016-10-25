package model.users;

public class User {

	private static final int MIN_PW_LENGTH = 8;

	private String _login;
	private String _email;
	private String _name;
	private String _pwhash = null;
	private String _salt = null;
	private int _points = 0;

	private SaltGenerator _saltGen;

	public User(String login, String email, String name) {
		_login = login;
		_email = email;
		_name = name;
		_saltGen = new SecureSaltGenerator();
	}

	public User(String login, String email, String name, SaltGenerator saltGen) {
		_login = login;
		_email = email;
		_name = name;
		_saltGen = saltGen;
	}

	public User(String login, String email, String name, String pwhash, String salt, int points) {
		_login = login;
		_email = email;
		_name = name;
		_pwhash = pwhash;
		_salt = salt;
		_points = points;
	}

	public String getLogin() {
		return _login;
	}

	public String getName() {
		return _name;
	}

	public String getEmail() {
		return _email;
	}

	public String getPwHash() {
		return _pwhash;
	}

	public String getSalt() {
		return _salt;
	}

	public int getPoints() {
		return _points;
	}

	private void validatePassword(String password) throws RegistrationError {
		if(password.length() < MIN_PW_LENGTH)
			throw new RegistrationError("Password too short");
	}

	protected void genPasswordHash(String password) throws RegistrationError {
		validatePassword(password);
		_salt = _saltGen.generate();
		_pwhash = PwHashGenerator.generateHash(password, _salt);
	}

	protected void checkPassword(String password) throws AuthenticationError {
		if (!_pwhash.equals(PwHashGenerator.generateHash(password, _salt)))
			throw new AuthenticationError("Invalid user credentials");
	}

}