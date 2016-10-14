package model;

public class Topico {

	private int _id;
	private String _titulo;
	private String _conteudo;
	private String _login;

	public Topico(String _titulo, String _conteudo, String _login) {
		this._titulo = _titulo;
		this._conteudo = _conteudo;
		this._login = _login;
		_id = 0;
	}

	public Topico(int _id, String _titulo, String _conteudo, String _login) {
		this._id = _id;
		this._titulo = _titulo;
		this._conteudo = _conteudo;
		this._login = _login;
	}

	public int getId() {
		return _id;
	}

	public String getTitulo() {
		return _titulo;
	}

	public String getConteudo() {
		return _conteudo;
	}

	public String getLogin() {
		return _login;
	}

}
