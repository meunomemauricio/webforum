package unit;

import java.io.File;

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
import model.GerenciaUsuario;
import model.Topico;
import model.TopicosDAO;

public class TestaTopicos {

	private JdbcDatabaseTester _jdt;
	private TopicosDAO _gerTopico;

	@Before
	public void setUp() throws Exception {
		_jdt = new JdbcDatabaseTester(
				"org.postgresql.Driver",
				"jdbc:postgresql://localhost/coursera",
				"postgres", "admin");

		_gerTopico = new GerenciaTopico(new GerenciaUsuario());
	}

	@Test
	public void insereNovoTopico() throws Exception {
		setupDatabase("unico_usuario.xml");

		Topico topico = new Topico("Primeiro Topico", "Conteudo Primeiro Topico", "mauricio");
		_gerTopico.insere(topico);

		verificaDatabase("unico_topico.xml");
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
