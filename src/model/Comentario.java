package model;

public class Comentario {

	private int _id;
	private String _comentario;
	private String _login;
	private int _topicoId;

	public Comentario(String comentario, String login, int topicoId) {
		this._id = 0;
		this._comentario = comentario;
		this._login = login;
		this._topicoId = topicoId;
	}

	public Comentario(int id, String comentario, String login, int topicoId) {
		this._id = id;
		this._comentario = comentario;
		this._login = login;
		this._topicoId = topicoId;
	}

	public int getId() {
		return _id;
	}

	public String getComentario() {
		return _comentario;
	}

	public String getLogin() {
		return _login;
	}

	public int getTopicoId() {
		return _topicoId;
	}

}
