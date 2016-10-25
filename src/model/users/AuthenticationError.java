package model.users;

public class AuthenticationError extends Exception {

	private static final long serialVersionUID = 1L;

	public AuthenticationError(String msg) {
		super(msg);
	}


}
