package br.eng.mauriciofreitas.model.users;

public class RegistrationError extends Exception {
	private static final long serialVersionUID = 1L;

	public RegistrationError(String msg) {
		super(msg);
	}

}
