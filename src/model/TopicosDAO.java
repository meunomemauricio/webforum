package model;

import java.util.List;

public interface TopicosDAO {

	public void insere(Topico topico);

	public Topico recuperar(int id) throws TopicoInexistente;

	public List<Topico> listarTopicos();

}
