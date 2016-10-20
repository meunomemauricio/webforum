package model;

public class InvalidPost extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPost(String message) {
		super(message);
	}

}
