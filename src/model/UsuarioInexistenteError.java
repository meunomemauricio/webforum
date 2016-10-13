package model;

public class UsuarioInexistenteError extends Exception {

	private static final long serialVersionUID = 1L;

	public UsuarioInexistenteError(String msg) {
		super(msg);
	}

}