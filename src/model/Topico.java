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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_conteudo == null) ? 0 : _conteudo.hashCode());
		result = prime * result + ((_login == null) ? 0 : _login.hashCode());
		result = prime * result + ((_titulo == null) ? 0 : _titulo.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Topico [_id=" + _id + ", _titulo=" + _titulo + ", _conteudo=" + _conteudo + ", _login=" + _login + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Topico other = (Topico) obj;
		if (_conteudo == null) {
			if (other._conteudo != null)
				return false;
		} else if (!_conteudo.equals(other._conteudo))
			return false;
		if (_login == null) {
			if (other._login != null)
				return false;
		} else if (!_login.equals(other._login))
			return false;
		if (_titulo == null) {
			if (other._titulo != null)
				return false;
		} else if (!_titulo.equals(other._titulo))
			return false;
		return true;
	}

}
