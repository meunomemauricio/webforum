package unit;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.util.fileloader.DataFileLoader;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;
import org.junit.Before;
import org.junit.Test;

import model.Comentario;
import model.ComentariosDAO;
import model.GerenciaComentario;
import model.GerenciaTopico;
import model.Topico;
import model.TopicosDAO;

public class TestaComentarios {

	private JdbcDatabaseTester _jdt;
	private TopicosDAO _gerTopico = new GerenciaTopico();
	private ComentariosDAO _gerCmnt = new GerenciaComentario();

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");
	}

	@Test
	public void insereComentario() {
		setupDatabase("unico_topico.xml");
		List<Topico> topicos = _gerTopico.listarTopicos();
		Topico tpc = topicos.get(0);

		Comentario cmnt = new Comentario("Primeiro Comentario", tpc.getLogin(), tpc.getId());
		_gerCmnt.adicionaComentario(cmnt);

		verificaDatabase("unico_comentario.xml");
	}

	@Test
	public void recuperaComentario() {
		setupDatabase("unico_topico.xml");
		List<Topico> topicos = _gerTopico.listarTopicos();
		Topico tpc = topicos.get(0);

		Comentario cmnt = new Comentario("Primeiro Comentario", tpc.getLogin(), tpc.getId());
		_gerCmnt.adicionaComentario(cmnt);

		List<Comentario> comentarios = _gerCmnt.recuperarComentarios(tpc.getId());

		assertEquals(1, comentarios.size());
		assertEquals("Primeiro Comentario", comentarios.get(0).getComentario());
	}

	@Test
	public void recuperaMultiplosComentarios() {
		setupDatabase("multiplos_topicos.xml");
		List<Topico> topicos = _gerTopico.listarTopicos();
		Topico tpc1 = topicos.get(0);
		Topico tpc2 = topicos.get(1);

		Comentario cmnt = new Comentario("Primeiro Comentario", tpc1.getLogin(), tpc1.getId());
		_gerCmnt.adicionaComentario(cmnt);

		cmnt = new Comentario("Segundo Comentario", tpc1.getLogin(), tpc1.getId());
		_gerCmnt.adicionaComentario(cmnt);

		cmnt = new Comentario("Terceiro Comentario", tpc1.getLogin(), tpc1.getId());
		_gerCmnt.adicionaComentario(cmnt);

		cmnt = new Comentario("Quarto Comentario", tpc2.getLogin(), tpc2.getId());
		_gerCmnt.adicionaComentario(cmnt);

		List<Comentario> comentarios = _gerCmnt.recuperarComentarios(tpc1.getId());

		// Verificar que o quarto comentário não é recuperado pois é de outro tópico
		assertEquals(3, comentarios.size());
		// Verifica que os comentários estão ordenados com o mais recente por último
		assertEquals("Primeiro Comentario", comentarios.get(0).getComentario());
		assertEquals("Segundo Comentario", comentarios.get(1).getComentario());
		assertEquals("Terceiro Comentario", comentarios.get(2).getComentario());
	}

	private void setupDatabase(String file) {
		DataFileLoader loader = new FlatXmlDataFileLoader();
		IDataSet dataSet = loader.load(String.format("/datasets/%s", file));
		_jdt.setDataSet(dataSet);
		try {
			_jdt.onSetup();
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível carregar o arquivo XML: " + e);
		}
	}

	private void verificaDatabase(String file) {
		try {
			IDataSet databaseDataSet = _jdt.getConnection().createDataSet();
			ITable actualTable = databaseDataSet.getTable("usuario");
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(
					new File(String.format("test//datasets/%s", file)));
			ITable expectedTable = expectedDataSet.getTable("usuario");
			Assertion.assertEquals(expectedTable, actualTable);

			actualTable = databaseDataSet.getTable("topico");
			expectedTable = expectedDataSet.getTable("topico");
			// É necessário excluir a coluna id_topico porque ela sempre varia
			ITable filteredTable = DefaultColumnFilter.includedColumnsTable(
					actualTable, expectedTable.getTableMetaData().getColumns());
			Assertion.assertEquals(expectedTable, filteredTable);

			actualTable = databaseDataSet.getTable("comentario");
			expectedTable = expectedDataSet.getTable("comentario");
			filteredTable = DefaultColumnFilter.includedColumnsTable(
					actualTable, expectedTable.getTableMetaData().getColumns());
			Assertion.assertEquals(expectedTable, filteredTable);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível verificar Tabela:" + e);
		}
	}

}
