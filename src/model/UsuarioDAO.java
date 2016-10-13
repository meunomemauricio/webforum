package model;

import java.util.List;

public interface UsuarioDAO {
	   public void inserir(Usuario u);
	   public Usuario recuperar(String login);
	   public boolean autenticar(String login, String senha);
	   public void adicionarPontos(String login, int pontos) throws UsuarioInexistenteError;
	   public List<Usuario> ranking();
}