package model;

import java.util.List;

public interface ComentariosDAO {

	void adicionaComentario(Comentario cmnt);

	List<Comentario> recuperarComentarios(int topicoId);

}
