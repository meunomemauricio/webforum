package unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

import model.GerenciaTopico;
import model.Topico;
import model.TopicoInexistente;
import model.TopicosDAO;

public class TestaTopicos {

	private JdbcDatabaseTester _jdt;
	private TopicosDAO _gerTopico = new GerenciaTopico();

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");
	}

	@Test
	public void insereNovoTopico() throws Exception {
		setupDatabase("unico_usuario.xml");

		Topico topico = new Topico("Primeiro Topico", "Conteudo Primeiro Topico", "mauricio");
		_gerTopico.insere(topico);

		verificaDatabase("unico_topico.xml");
	}

	@Test
	public void recuperaListaComUmTopico() throws Exception {
		setupDatabase("unico_topico.xml");

		List<Topico> topicos = _gerTopico.listarTopicos();

		assertEquals(1, topicos.size());
		assertEquals(topicos.get(0).getTitulo(), "Primeiro Topico");
	}

	@Test
	public void recuperaListaMultiplosTopicos() throws Exception {
		setupDatabase("multiplos_topicos.xml");

		List<Topico> topicos = _gerTopico.listarTopicos();

		assertEquals(5, topicos.size());
		// Garante que a lista é ordenada sendo o topico mais recente primeiro
		assertEquals(topicos.get(0).getTitulo(), "Quinto Topico");
		assertEquals(topicos.get(2).getTitulo(), "Terceiro Topico");
		assertEquals(topicos.get(4).getTitulo(), "Primeiro Topico");
	}

	@Test
	public void recuperaTopicoPorIDValida() throws Exception {
		setupDatabase("unico_topico.xml");

		List<Topico> topicos = _gerTopico.listarTopicos();
		Topico topicoLista = topicos.get(0);
		int id = topicoLista.getId();

		Topico topicoRecuperado = _gerTopico.recuperar(id);

		assertEquals(topicoLista, topicoRecuperado);
	}

	@Test
	public void recuperarTopicoInexistente() throws Exception {
		setupDatabase("unico_usuario.xml");

		try {
			_gerTopico.recuperar(0);
			fail("Nenhum tópico deveria ter sido recuperado");
		} catch (TopicoInexistente e) {};
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
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível verificar Tabela:" + e);
		}
	}


}
