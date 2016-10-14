package model;

public class Usuario {

	private String _login;
	private String _email;
	private String _nome;
	private String _senha;
	private int _pontos;

	public Usuario(String login, String email, String nome, String senha) {
		_login = login;
		_email = email;
		_nome = nome;
		_senha = senha;
		_pontos = 0;
	}

	public Usuario(String login, String email, String nome, String senha, int pontos) {
		_login = login;
		_email = email;
		_nome = nome;
		_senha = senha;
		_pontos = pontos;
	}

	public String getSenha() {
		return _senha;
	}

	public String getLogin() {
		return _login;
	}

	public String getNome() {
		return _nome;
	}

	public String getEmail() {
		return _email;
	}

	public int getPontos() {
		return _pontos;
	}

	public void setPontos(int pontos) {
		this._pontos = pontos;
	}

}