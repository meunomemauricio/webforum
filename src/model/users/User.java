package model.users;

public class User {

	private String _login;
	private String _email;
	private String _name;
	private String _password;
	private int _points;

	public User(String login, String email, String name, String password) {
		_login = login;
		_email = email;
		_name = name;
		_password = password;
		_points = 0;
	}

	public User(String login, String email, String name, String password, int points) {
		_login = login;
		_email = email;
		_name = name;
		_password = password;
		_points = points;
	}

	public String getPassword() {
		return _password;
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

	public int getPoints() {
		return _points;
	}

}