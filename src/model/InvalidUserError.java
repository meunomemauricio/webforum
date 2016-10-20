package model;

public class InvalidUserError extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidUserError(String msg) {
		super(msg);
	}

}