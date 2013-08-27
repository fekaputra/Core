package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import static cz.cuni.xrg.intlib.rdf.enums.FileExtractType.*;
import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;

import static cz.cuni.xrg.intlib.rdf.enums.WriteGraphType.*;

import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.GraphNotEmptyException;
import cz.cuni.xrg.intlib.rdf.exceptions.InvalidQueryException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openrdf.model.*;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.*;
import org.openrdf.query.impl.DatasetImpl;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.*;
import org.slf4j.Logger;

/**
 * Abstract class provides common parent methods for RDFDataUnit implementation.
 *
 * @author Jiri Tomes
 */
public abstract class BaseRDFRepo implements RDFDataUnit, Closeable {

	/**
	 * How many triples is possible to add to SPARQL endpoind at once.
	 */
	protected static final int STATEMENTS_COUNT = 10;

	/**
	 * Represent successfully connection using HTTP.
	 */
	protected static final int HTTP_OK_RESPONSE = 200;

	/**
	 * Represent http error code needed authorisation for connection using HTTP.
	 */
	protected static final int HTTP_UNAUTORIZED_RESPONSE = 401;

	/**
	 * Default name for graph using for store RDF data.
	 */
	protected static final String DEFAULT_GRAPH_NAME = "http://default";

	/**
	 * Default select query using for extraction without query in parameter.
	 */
	protected static final String DEFAUTL_SELECT_QUERY = "select ?x ?y ?z where {?x ?y ?z}";

	/**
	 * Logging information about execution of method using openRDF.
	 */
	protected Logger logger;

	/**
	 * RDF data storage component.
	 */
	protected Repository repository;

	/**
	 * Graph resource for saving RDF triples.
	 */
	protected URI graph;

	/**
	 * DataUnit's name.
	 */
	protected String dataUnitName;

	/**
	 * Default used encoding.
	 */
	protected final String encode = "UTF-8";

	/**
	 * If the repository is used only for reading data or not.
	 */
	protected boolean isReadOnly;

	/**
	 * Add one tripple RDF (statement) to the repository (default empty
	 * namespace).
	 *
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 */
	@Override
	public void addTriple(String subjectName,
			String predicateName, String objectName) {

		Statement statement = createNewStatement(subjectName, predicateName,
				objectName);
		addStatement(statement, graph);
	}

	/**
	 * Add one tripple RDF (statement) to the repository.
	 *
	 * @param namespace     String name of defined namespace
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 */
	@Override
	public void addTriple(String namespace, String subjectName,
			String predicateName, String objectName) {

		Statement statement = createNewStatement(namespace, subjectName,
				predicateName, objectName);
		addStatement(statement, graph);
	}

	/**
	 * Add all RDF data from string to repository.
	 *
	 * @param rdfString string constains RDF data.
	 * @param format    RDF format of given string - used to select parser.
	 *
	 * @throws RDFException when adding RDF data failt.
	 */
	@Override
	public void addRDFString(String rdfString, RDFFormat format) throws RDFException {
		addRDFStringToRepository(rdfString, format, graph);
	}

	@Override
	public void extractFromFile(File file) throws RDFException {
		extractFromFile(file, RDFFormat.RDFXML, "", false);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format) throws RDFException {
		extractFromFile(file, format, "", false);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format,
			boolean useStatisticalHandler) throws RDFException {

		extractFromFile(file, format, "", useStatisticalHandler);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format, String baseURI)
			throws RDFException {

		extractFromFile(file, format, baseURI, false);
	}

	@Override
	public void extractFromFile(File file, RDFFormat format, String baseURI,
			boolean useStatisticalHandler) throws RDFException {

		if (file == null) {
			throw new RDFException("Given file for extraction is null");
		}
		extractFromFile(format, FileExtractType.PATH_TO_FILE, file
				.getAbsolutePath(), "",
				baseURI, false, useStatisticalHandler);
	}

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param path String path to file
	 *
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void extractFromLocalTurtleFile(String path) throws RDFException {
		extractFromFile(RDFFormat.TURTLE, FileExtractType.PATH_TO_FILE, path, "",
				"", false, false);

	}

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void extractFromFile(FileExtractType extractType,
			String path, String suffix,
			String baseURI, boolean useSuffix, boolean useStatisticHandler)
			throws RDFException {
		extractFromFile(RDFFormat.RDFXML, extractType, path, suffix, baseURI,
				useSuffix, useStatisticHandler);
	}

	/**
	 * Extract RDF triples from RDF file to repository.
	 *
	 * @param format              Specifies {@link RDFFormatRDF} (e.g., RDFXML,
	 *                            Turtle, ..)
	 * @param extractType         One of defined enum type for extraction data
	 *                            from file.
	 * @param path                String path to file/directory
	 * @param suffix              String suffix of fileName (example: ".ttl",
	 *                            ".xml", etc)
	 * @param baseURI             String name of defined used URI
	 * @param useSuffix           boolean value, if extract files only with
	 *                            defined suffix or not.
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @throws RDFException when extraction fail.
	 */
	@Override
	public void extractFromFile(RDFFormat format, FileExtractType extractType,
			String path, String suffix,
			String baseURI, boolean useSuffix, boolean useStatisticHandler)
			throws RDFException {

		if (path == null) {
			final String message = "Mandatory target path in extractor is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else if (path.isEmpty()) {

			final String message = "Mandatory target path in extractor have to be not empty.";

			logger.debug(message);
			throw new RDFException(message);

		}

		File dirFile = new File(path);

		switch (extractType) {
			case HTTP_URL:
				extractDataFileFromHTTPSource(path, baseURI, useStatisticHandler);
				break;
			case PATH_TO_DIRECTORY:
				if (dirFile.isDirectory()) {
					File[] files = getFilesBySuffix(dirFile, suffix, useSuffix);
					addFilesInDirectoryToRepository(format, files, baseURI,
							useStatisticHandler,
							graph);
				} else {
					throw new RDFException(
							"Path to directory \"" + path + "\" doesnt exist");
				}
				break;
			case PATH_TO_FILE:
			case UPLOAD_FILE:
				if (dirFile.isFile()) {
					addFileToRepository(format, dirFile, baseURI,
							useStatisticHandler,
							graph);
				} else {
					throw new RDFException(
							"Path to file \"" + path + "\"doesnt exist");
				}
				break;
		}

	}

	@Override
	public void loadToFile(File file, RDFFormatType formatType) throws RDFException {

		if (file == null) {

			final String message = "Given file for loading is null.";

			logger.debug(message);
			throw new RDFException(message);


		} else if (file.getName().isEmpty()) {

			final String message = "File name is empty.";


			logger.debug(message);
			throw new RDFException(message);
		}

		if (!file.exists()) {
			createNewFile(file);

		}

		writeDataIntoFile(file, formatType);

	}

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param directoryPath Path to directory, where file with RDF data will be
	 *                      saved.
	 * @param fileName      Name of file for saving RDF data.
	 * @param formatType    Type of RDF format for saving data (example: TURTLE,
	 *                      RDF/XML,etc.)
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFException                 when loading data fault.
	 */
	@Override
	public void loadToFile(String directoryPath,
			String fileName,
			RDFFormatType formatType) throws CannotOverwriteFileException, RDFException {

		loadToFile(directoryPath, fileName, formatType, false,
				false);
	}

	/**
	 * Load all triples in repository to defined file in defined RDF format.
	 *
	 * @param directoryPath    Path to directory, where file with RDF data will
	 *                         be saved.
	 * @param fileName         Name of file for saving RDF data.
	 * @param formatType       Type of RDF format for saving data (example:
	 *                         TURTLE, RDF/XML,etc.)
	 * @param canFileOverWrite boolean value, if existing file can be
	 *                         overwritten.
	 * @param isNameUnique     boolean value, if every pipeline execution has
	 *                         his unique name.
	 * @throws CannotOverwriteFileException when file is protected for
	 *                                      overwritting.
	 * @throws RDFException                 when loading data fault.
	 */
	@Override
	public void loadToFile(String directoryPath,
			String fileName, RDFFormatType formatType,
			boolean canFileOverWrite, boolean isNameUnique) throws CannotOverwriteFileException, RDFException {

		if (directoryPath == null || fileName == null) {

			final String message;

			if (directoryPath == null) {
				message = "Mandatory directory path in File_loader is null.";
			} else {
				message = "Mandatory file name in File_loader is null.";
			}

			logger.debug(message);
			throw new RDFException(message);


		} else if (directoryPath.isEmpty() || fileName.isEmpty()) {

			final String message;

			if (directoryPath.isEmpty()) {
				message = "Mandatory directory path in File_loader is empty.";
			} else {
				message = "Mandatory file name in File_loader is empty.";
			}

			logger.debug(message);
			throw new RDFException(message);
		}

		final String slash = File.separator;

		if (!directoryPath.endsWith(slash)) {
			directoryPath += slash;
		}

		File directory = new File(directoryPath);

		if (!directory.exists()) {
			directory.mkdirs();
		}

		File dataFile = new File(directoryPath + fileName);

		if (!dataFile.exists()) {
			createNewFile(dataFile);

		} else {
			if (isNameUnique) {

				String uniqueFileName = UniqueNameGenerator
						.getNextName(fileName);

				dataFile = new File(directoryPath + uniqueFileName);
				createNewFile(dataFile);

			} else if (canFileOverWrite) {
				createNewFile(dataFile);
			} else {
				logger.debug("File existed and cannot be overwritten");
				throw new CannotOverwriteFileException();
			}

		}

		writeDataIntoFile(dataFile, formatType);

	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * without endpoint authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @throws RDFException when loading data fault.
	 */
	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			WriteGraphType graphType) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphURI);

		loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "", graphType);
	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the one URI graph
	 * with endpoint authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI name of graph where RDF data are loading.
	 * @param name            String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @throws RDFException when loading data fault.
	 */
	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL, String defaultGraphURI,
			String name, String password, WriteGraphType graphType) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphURI);

		loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, name, password,
				graphType);
	}

	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, WriteGraphType graphType) throws RDFException {

		loadtoSPARQLEndpoint(endpointURL, endpointGraphsURI, "", "",
				graphType);
	}

	/**
	 * Load RDF data from repository to SPARQL endpointURL to the collection of
	 * URI graphs with endpoint authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphURI List with names of graph where RDF data are
	 *                        loading.
	 * @param userName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @param graphType       One of way, how to solve loading RDF data to graph
	 *                        when is it is not empty (MERGE, OVERRIDE, FAIL).
	 * @throws RDFException when loading data fault.
	 */
	@Override
	public void loadtoSPARQLEndpoint(URL endpointURL,
			List<String> endpointGraphsURI, String userName,
			String password, WriteGraphType graphType) throws RDFException {

		if (endpointURL == null) {
			final String message = "Mandatory URL path in extractor from SPARQL is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else {

			final String endpointName = endpointURL.toString().toLowerCase();

			String message = null;

			if (!endpointName.startsWith("http://")) {
				message = "Endpoint url name have to started with prefix \"http://\".";
			} else if (endpointName.contains(" ")) {
				message = "Endpoint url constains write spaces";
			}
			if (message != null) {
				logger.debug(message);
				throw new RDFException(message);
			}

		}

		if (endpointGraphsURI == null) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else if (endpointGraphsURI.isEmpty()) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is empty.";

			logger.debug(message);
			throw new RDFException(message);
		}

		final int graphSize = endpointGraphsURI.size();

		authenticate(userName, password);

		RepositoryConnection connection = null;

		try {

			connection = repository.getConnection();

			for (int i = 0; i < graphSize; i++) {

				final String endpointGraph = endpointGraphsURI.get(i);

				try {
					switch (graphType) {
						case MERGE:
							break;
						case OVERRIDE: {
							clearEndpointGraph(endpointURL, endpointGraph);
						}
						break;
						case FAIL: {

							long SPARQLGraphSize = getSPARQLEnpointGraphSize(
									endpointURL, endpointGraph);

							boolean sourceNotEmpty = SPARQLGraphSize > 0;

							if (sourceNotEmpty) {
								throw new GraphNotEmptyException(
										"Graph <" + endpointGraph + "> is not empty (has "
										+ SPARQLGraphSize
										+ " triples) - Loading to SPARQL endpoint FAIL.");
							}

						}

						break;

					}
				} catch (GraphNotEmptyException ex) {
					logger.debug(ex.getMessage());


					throw new RDFException(ex.getMessage(), ex);
				}

				List<String> dataParts = getInsertPartsTriplesQuery(
						STATEMENTS_COUNT);
				final int partsCount = dataParts.size();

				for (int j = 0; j < partsCount; j++) {

					final String query = dataParts.get(j);

					String myquery = getEncodedQuery(query);
					InputStreamReader inputStreamReader = getEndpointStreamReader(
							endpointURL, endpointGraph, myquery,
							RDFFormat.N3);

					try {
						inputStreamReader.close();
					} catch (IOException e) {
						throw new RDFException(e.getMessage(), e);
					}

					final String processing = String.valueOf(j + 1) + "/" + String
							.valueOf(partsCount);

					logger.debug(
							"Data " + processing + " part loaded successful");

				}
			}

		} catch (RepositoryException ex) {
			throw new RDFException("Repository connection failed. " + ex
					.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository. "
							+ ex.getMessage(), ex);
				}
			}

		}
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	@Override
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI) throws RDFException {

		extractFromSPARQLEndpoint(endpointURL, defaultGraphURI,
				DEFAUTL_SELECT_QUERY);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph without authentication.
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are loading.
	 * @param query           String SPARQL query.
	 * @throws RDFException when extraction data fault.
	 */
	@Override
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query) throws RDFException {

		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphUri);

		extractFromSPARQLEndpoint(endpointURL, endpointGraphsURI, query, "", "",
				RDFFormat.N3, false);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are stored.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 *
	 * @throws RDFException when extraction data from SPARQL endpoint fail.
	 */
	@Override
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String hostName, String password) throws RDFException {

		extractFromSPARQLEndpoint(endpointURL, defaultGraphURI,
				DEFAUTL_SELECT_QUERY, hostName, password);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are loading.
	 * @param query           String SPARQL query.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 *
	 * @throws RDFException when extraction data fault.
	 */
	@Override
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphURI, String query, String hostName,
			String password) throws RDFException {

		extractFromSPARQLEndpoint(endpointURL, defaultGraphURI, query, hostName,
				password, RDFFormat.N3);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * URI graph using authentication (name,password).
	 *
	 * @param endpointURL     Remote URL connection to SPARQL endpoint contains
	 *                        RDF data.
	 * @param defaultGraphUri name of graph where RDF data are loading.
	 * @param query           String SPARQL query.
	 * @param hostName        String name needed for authentication.
	 * @param password        String password needed for authentication.
	 * @param format          Type of RDF format for saving data (example:
	 *                        TURTLE, RDF/XML,etc.)
	 * @throws RDFException when extraction data fault.
	 */
	@Override
	public void extractFromSPARQLEndpoint(URL endpointURL,
			String defaultGraphUri, String query, String hostName,
			String password, RDFFormat format) throws RDFException {
		List<String> endpointGraphsURI = new ArrayList<>();
		endpointGraphsURI.add(defaultGraphUri);

		extractFromSPARQLEndpoint(endpointURL, endpointGraphsURI, query,
				hostName, password, format, false);
	}

	/**
	 * Extract RDF data from SPARQL endpoint to repository using only data from
	 * collection of URI graphs using authentication (name,password).
	 *
	 * @param endpointURL         Remote URL connection to SPARQL endpoint
	 *                            contains RDF data.
	 * @param defaultGraphsUri    List with names of graph where RDF data are
	 *                            loading.
	 * @param query               String SPARQL query.
	 * @param hostName            String name needed for authentication.
	 * @param password            String password needed for authentication.
	 * @param format              Type of RDF format for saving data (example:
	 *                            TURTLE, RDF/XML,etc.)
	 * @param useStatisticHandler boolean value if detailed log and statistic
	 *                            are awailable or not.
	 * @throws RDFException when extraction data fault.
	 */
	@Override
	public void extractFromSPARQLEndpoint(
			URL endpointURL,
			List<String> endpointGraphsURI,
			String query,
			String hostName,
			String password,
			RDFFormat format,
			boolean useStatisticHandler) throws RDFException {

		if (endpointURL == null) {
			final String message = "Mandatory URL path in extractor from SPARQL is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else if (!endpointURL.toString().toLowerCase().startsWith("http")) {

			final String message = "Mandatory URL path in extractor from SPARQL "
					+ "have to started with http.";

			logger.debug(message);
			throw new RDFException(message);

		}

		if (endpointGraphsURI == null) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is null.";

			logger.debug(message);
			throw new RDFException(message);

		} else if (endpointGraphsURI.isEmpty()) {
			final String message = "Mandatory graph´s name(s) in extractor from SPARQL is empty.";

			logger.debug(message);
			throw new RDFException(message);
		}

		if (query == null) {
			final String message = "Mandatory construct query is null.";
			logger.debug(message);
			throw new RDFException(message);
		} else if (query.isEmpty()) {
			final String message = "Construct query is empty";

			logger.debug(message);
			throw new RDFException(message);
		}

		final int graphSize = endpointGraphsURI.size();

		RepositoryConnection connection = null;

		try {
			connection = repository.getConnection();
			authenticate(hostName, password);

			for (int i = 0; i < graphSize; i++) {

				final String endpointGraph = endpointGraphsURI.get(i);

				InputStreamReader inputStreamReader = getEndpointStreamReader(
						endpointURL, endpointGraph, query, format);

				if (!useStatisticHandler) {
					if (graph != null) {

						connection.add(inputStreamReader, endpointGraph,
								format,
								graph);
					} else {
						connection.add(inputStreamReader, endpointGraph,
								format);
					}
				} else {
					StatisticalHandler handler = new StatisticalHandler();

					RDFParser parser = Rio.createParser(format);
					parser.setRDFHandler(handler);

					try {
						parser.parse(inputStreamReader, endpointGraph);

						if (graph != null) {
							connection.add(handler.getStatements(), graph);
						} else {
							connection.add(handler.getStatements());
						}
					} catch (IOException | RepositoryException ex) {
						logger.error(ex.getMessage(), ex);
					} catch (RDFHandlerException | RDFParseException ex) {
						logger.error(ex.getMessage(), ex);
						throw new RDFException(ex.getMessage(), ex);
					}


				}
			}
		} catch (IOException e) {

			final String message = "Http connection can can not open stream. ";
			logger.debug(message);

			throw new RDFException(message + e.getMessage(), e);

		} catch (RDFParseException e) {
			logger.debug(e.getMessage());

			throw new RDFException(e.getMessage(), e);

		} catch (RepositoryException e) {

			final String message = "Repository connection failt: " + e
					.getMessage();

			logger.debug(message);

			throw new RDFException(message, e);

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while extracting from SPQRQL endpoint.",
							ex);
				}
			}
		}

	}

	/**
	 * Transform RDF in repository by SPARQL updateQuery.
	 *
	 * @param updateQuery String value of update SPARQL query.
	 * @throws RDFException when transformation fault.
	 */
	@Override
	public void transformUsingSPARQL(String updateQuery) throws RDFException {

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();


			String newUpdateQuery = AddGraphToUpdateQuery(updateQuery);
			Update myupdate = connection.prepareUpdate(QueryLanguage.SPARQL,
					newUpdateQuery);


			logger.debug(
					"This SPARQL query for transform is valid and prepared for execution:");
			logger.debug(newUpdateQuery);

			myupdate.execute();
			connection.commit();

			logger.debug("SPARQL query for transform was executed succesfully");

		} catch (MalformedQueryException e) {

			logger.debug(e.getMessage());
			throw new RDFException(e.getMessage(), e);

		} catch (UpdateExecutionException ex) {

			final String message = "SPARQL query was not executed !!!";
			logger.debug(message);
			logger.debug(ex.getMessage());

			throw new RDFException(message + ex.getMessage(), ex);


		} catch (RepositoryException ex) {
			throw new RDFException(
					"Connection to repository is not available. "
					+ ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while executing SPARQL transform. "
							+ ex.getMessage(), ex);
				}
			}
		}

	}

	/**
	 * Return count of triples stored in repository.
	 *
	 * @return size of triples in repository.
	 */
	@Override
	public long getTripleCount() {
		long size = 0;

		RepositoryConnection connection = null;

		try {
			connection = repository.getConnection();

			if (graph != null) {
				size = connection.size(graph);
			} else {
				size = connection.size();
			}

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while counting triples. "
							+ ex.getMessage(), ex);
				}
			}
		}


		return size;
	}

	/**
	 * Return if RDF triple is in repository.
	 *
	 * @param namespace     String name of defined namespace
	 * @param subjectName   String name of subject
	 * @param predicateName String name of predicate
	 * @param objectName    String name of object
	 * @return true if such statement is in repository, false otherwise.
	 */
	@Override
	public boolean isTripleInRepository(String namespace, String subjectName,
			String predicateName, String objectName) {
		boolean hasTriple = false;



		RepositoryConnection connection = null;
		Statement statement = createNewStatement(namespace, subjectName,
				predicateName, objectName);

		try {
			connection = repository.getConnection();

			if (graph != null) {
				hasTriple = connection.hasStatement(statement, true, graph);
			} else {
				hasTriple = connection.hasStatement(statement, true);
			}

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while looking for triple. "
							+ ex.getMessage(), ex);
				}
			}
		}

		return hasTriple;
	}

	/**
	 * Removes all RDF data from repository.
	 */
	@Override
	public void cleanAllData() {

		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();

			if (graph != null) {
				connection.clear(graph);
			} else {
				connection.clear();
			}

			connection.commit();

		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while cleaning up. "
							+ ex.getMessage(), ex);
				}
			}
		}

	}

	/**
	 * Return all triples(statements) in reposiotory as list.
	 *
	 * @return List<code>&lt;Statement&gt;</code> list of all triples in
	 *         repository/
	 */
	@Override
	public List<Statement> getRepositoryStatements() {
		List<Statement> statemens = new ArrayList<>();

		if (repository != null) {
			RepositoryConnection connection = null;

			try {
				connection = repository.getConnection();

				if (graph != null) {
					statemens = connection.getStatements(null, null, null, true,
							graph).asList();
				} else {
					statemens = connection.getStatements(null, null, null, true)
							.asList();
				}

			} catch (RepositoryException ex) {
				logger.debug(ex.getMessage(), ex);
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (RepositoryException ex) {
						logger.warn(
								"Failed to close connection to RDF repository. "
								+ ex.getMessage(), ex);
					}
				}
			}

		}

		return statemens;
	}

	/**
	 * Make construct query over repository data and return file where RDF data
	 * as result are saved.
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @param formatType     Choosed type of format RDF data in result.
	 * @param filePath       String path to file where result with RDF data is
	 *                       stored.
	 * @return File with RDF data in defined format as result of construct
	 *         query.
	 * @throws InvalidQueryException when query is not valid or creating file
	 *                               fail.
	 */
	@Override
	public File makeConstructQueryOverRepository(String constructQuery,
			RDFFormatType formatType, String filePath) throws InvalidQueryException {

		RepositoryConnection connection = null;

		try {
			connection = repository.getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + constructQuery + " is valid.");

			try {

				File file = new File(filePath);
				createNewFile(file);

				FileOutputStream os = new FileOutputStream(file);

				MyRDFHandler myGoal = new MyRDFHandler(os, formatType);
				RDFHandler goal = myGoal.getRDFHandler();

				graphQuery.evaluate(goal);

				logger.debug(
						"Query " + constructQuery + " has not null result.");

				return file;

			} catch (QueryEvaluationException ex) {
				throw new InvalidQueryException(
						"This query is probably not valid. " + ex.getMessage(),
						ex);
			} catch (IOException ex) {
				logger.error("Stream were not closed. " + ex.getMessage(), ex);
			}

		} catch (MalformedQueryException ex) {
			throw new InvalidQueryException("This query is probably not valid. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} catch (RDFHandlerException ex) {
			logger.error("RDF handler failt. " + ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while querying. "
							+ ex.getMessage(), ex);
				}
			}
		}

		throw new InvalidQueryException(
				"Creating File with RDF data fault.");
	}

	/**
	 * Make construct query over repository data and return interface Graph as
	 * result contains iterator for statements (triples).
	 *
	 * @param constructQuery String representation of SPARQL query.
	 * @return Interface Graph as result of construct SPARQL query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public Graph makeConstructQueryOverRepository(
			String constructQuery) throws InvalidQueryException {

		RepositoryConnection connection = null;

		try {
			connection = repository.getConnection();

			GraphQuery graphQuery = connection.prepareGraphQuery(
					QueryLanguage.SPARQL,
					constructQuery);

			graphQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + constructQuery + " is valid.");

			try {

				GraphQueryResult result = graphQuery.evaluate();
				logger.debug(
						"Query " + constructQuery + " has not null result.");
				return result.asGraph();

			} catch (QueryEvaluationException ex) {
				throw new InvalidQueryException(
						"This query is probably not valid. " + ex.getMessage(),
						ex);
			}

		} catch (MalformedQueryException ex) {
			throw new InvalidQueryException("This query is probably not valid. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while querying. "
							+ ex.getMessage(), ex);
				}
			}
		}

		throw new InvalidQueryException(
				"Getting GraphQueryResult using SPARQL construct query failed.");
	}

	/**
	 * Make select query over repository data and return file as SPARQL XML
	 * result.
	 *
	 * @param selectQuery String representation of SPARQL query
	 * @param filePath    String path to file for saving result of query in
	 *                    SPARQL XML syntax.
	 * @return File contains result of given SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public File makeSelectQueryOverRepository(String selectQuery,
			String filePath)
			throws InvalidQueryException {

		RepositoryConnection connection = null;

		try {
			connection = repository.getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, selectQuery);

			tupleQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + selectQuery + " is valid.");

			File file = new File(filePath);
			createNewFile(file);

			FileOutputStream os = new FileOutputStream(file);
			TupleQueryResultWriter tupleHandler = new SPARQLResultsXMLWriter(os);

			tupleQuery.evaluate(tupleHandler);
			return file;

		} catch (QueryEvaluationException | MalformedQueryException ex) {
			throw new InvalidQueryException(
					"This query is probably not valid. " + ex.getMessage(),
					ex);
		} catch (TupleQueryResultHandlerException ex) {
			logger.error("Writing result to file fail. " + ex.getMessage(),
					ex);

		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		} catch (IOException ex) {
			logger.error("Stream were not closed. " + ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while querying."
							+ ex.getMessage(), ex);
				}
			}
		}

		throw new InvalidQueryException(
				"Creating File with RDF data fault.");

	}

	/**
	 * Make select query over repository data and return tables as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return <code>Map&lt;String,List&lt;String&gt;&gt;</code> as table, where
	 *         map key is column name and <code>List&lt;String&gt;</code> are
	 *         string values in this column. When query is invalid, return * *
	 *         empty <code>Map</code>.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public Map<String, List<String>> makeSelectQueryOverRepository(
			String selectQuery)
			throws InvalidQueryException {

		Map<String, List<String>> map = new HashMap<>();

		List<BindingSet> listBindings = new ArrayList<>();
		MyTupleQueryResult result = null;
		try {
			result = makeSelectQueryOverRepositoryAsResult(selectQuery);

			List<String> names = result.getBindingNames();

			for (String name : names) {
				map.put(name, new LinkedList<String>());
			}

			listBindings = result.asList();
		} catch (QueryEvaluationException ex) {
			throw new InvalidQueryException(
					"This query is probably not valid. " + ex
					.getMessage(),
					ex);
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (QueryEvaluationException ex) {
					logger.warn("Failed to close RDF tuple result. "
							+ ex.getMessage(), ex);
				}
			}
		}

		for (BindingSet bindingNextSet : listBindings) {
			for (Binding next : bindingNextSet) {

				String name = next.getName();
				String value = next.getValue().stringValue();

				if (map.containsKey(name)) {
					map.get(name).add(value);
				}

			}
		}

		return map;
	}

	/**
	 * Make select query over repository data and return MyTupleQueryResult
	 * class as result.
	 *
	 * @param selectQuery String representation of SPARQL select query.
	 * @return MyTupleQueryResult representation of SPARQL select query.
	 * @throws InvalidQueryException when query is not valid.
	 */
	@Override
	public MyTupleQueryResult makeSelectQueryOverRepositoryAsResult(
			String selectQuery)
			throws InvalidQueryException {

		try {
			RepositoryConnection connection = repository.getConnection();

			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, selectQuery);

			tupleQuery.setDataset(getDataSetForGraph());

			logger.debug("Query " + selectQuery + " is valid.");

			try {
				TupleQueryResult tupleResult = tupleQuery.evaluate();
				logger.debug(
						"Query " + selectQuery + " has not null result.");

				MyTupleQueryResult result = new MyTupleQueryResult(connection,
						tupleResult);

				return result;

			} catch (QueryEvaluationException ex) {
				throw new InvalidQueryException(
						"This query is probably not valid. " + ex
						.getMessage(),
						ex);
			}

		} catch (MalformedQueryException ex) {
			throw new InvalidQueryException(
					"This query is probably not valid. "
					+ ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			logger.error("Connection to RDF repository failed. "
					+ ex.getMessage(), ex);
		}
		throw new InvalidQueryException(
				"Getting TupleQueryResult using SPARQL select query failed.");
	}

	@Override
	public void merge(DataUnit unit) throws IllegalArgumentException {

		if (unit != null) {
			if (unit instanceof RDFDataUnit) {
				RDFDataUnit rdfRepository = (RDFDataUnit) unit;
				mergeRepositoryData(rdfRepository);

			} else {
				throw new IllegalArgumentException(
						"DataUnit is not instance of RDFDataRepository.");
			}
		}
	}

	private Dataset getDataSetForGraph() {
		DatasetImpl dataSet = new DatasetImpl();
		dataSet.addDefaultGraph(graph);

		return dataSet;
	}

	private String AddGraphToUpdateQuery(String updateQuery) {

		if (repository instanceof SailRepository) {
			return updateQuery;
		}

		String regex = "(insert|delete)\\s\\{";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(updateQuery.toLowerCase());

		boolean hasResult = matcher.find();

		if (hasResult) {

			int index = matcher.start();

			String first = updateQuery.substring(0, index);
			String second = updateQuery.substring(index, updateQuery.length());

			String graphName = " WITH <" + graph.stringValue() + "> ";

			String newQuery = first + graphName + second;
			return newQuery;


		} else {

			regex = "(insert|delete)\\sdata\\s\\{";
			pattern = Pattern.compile(regex);
			matcher = pattern.matcher(updateQuery.toLowerCase());

			hasResult = matcher.find();

			if (hasResult) {

				int start = matcher.start();
				int end = matcher.end();

				String first = updateQuery.substring(0, start);
				String second = updateQuery.substring(end, updateQuery.length());

				String myString = updateQuery.substring(start, end);
				String graphName = myString.replace("{",
						"in graph <" + graph.stringValue() + "> {");

				String newQuery = first + graphName + second;

				return newQuery;

			}
		}
		return updateQuery;


	}

	private String getEncodedQuery(String query) throws RDFException {
		try {
			String myquery = URLEncoder.encode(query, encode);
			return myquery;
		} catch (UnsupportedEncodingException ex) {
			String message = "Encoding " + encode + " is not supported.";
			logger.debug(message);
			throw new RDFException(message + ex.getMessage(), ex);
		}
	}

	long getSPARQLEnpointGraphSize(URL endpointURL, String endpointGraph) throws RDFException {
		String countQuery = "select count(*) as ?count where {?x ?y ?z}";

		InputStreamReader inputStreamReader = getEndpointStreamReader(
				endpointURL, endpointGraph,
				countQuery, RDFFormat.RDFXML);

		long count = -1;

		try (Scanner scanner = new Scanner(inputStreamReader)) {

			String regexp = ">[0-9]+<";
			Pattern pattern = Pattern.compile(regexp);
			boolean find = false;

			while (scanner.hasNext() & !find) {
				String line = scanner.next();
				Matcher matcher = pattern.matcher(line);

				if (matcher.find()) {
					String number = line.substring(matcher.start() + 1, matcher
							.end() - 1);
					count = Long.parseLong(number);
					find = true;

				}

			}
		}

		return count;

	}

	private void clearEndpointGraph(URL endpointURL, String endpointGraph)
			throws RDFException {

		String deleteQuery = "delete {?x ?y ?z} where {?x ?y ?z}";
		InputStreamReader inputStreamReader = getEndpointStreamReader(
				endpointURL,
				endpointGraph, deleteQuery, RDFFormat.RDFXML);

	}

	private InputStreamReader getEndpointStreamReader(URL endpointURL,
			String endpointGraphURI, String query,
			RDFFormat format) throws RDFException {

		final String endpointGraph = endpointGraphURI.replace(
				" ", "+");
		final String myquery = query.replace(" ", "+");

		final String encoder = getEncoder(format);

		URL call = null;
		try {
			call = new URL(
					endpointURL.toString() + "?default-graph-uri=" + endpointGraph + "&query=" + myquery + "&format=" + encoder);
		} catch (MalformedURLException e) {
			final String message = "Malfolmed URL exception by construct extract URL. ";
			logger.debug(message);
			throw new RDFException(message + e.getMessage(), e);
		}

		HttpURLConnection httpConnection = null;
		try {
			httpConnection = (HttpURLConnection) call.openConnection();

			int httpResponseCode = httpConnection.getResponseCode();

			if (httpResponseCode != HTTP_OK_RESPONSE) {

				StringBuilder message = new StringBuilder(
						httpConnection.getHeaderField(0));


				if (httpResponseCode == HTTP_UNAUTORIZED_RESPONSE) {
					message.append(
							". Your USERNAME and PASSWORD for connection is wrong.");

				} else {
					message.append(
							". You probably dont have enought PERMISSION for this action.");
				}

				throw new RDFException(message.toString());
			}

		} catch (IOException e) {
			logger.debug("Endpoint URL stream can not open");
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			throw new RDFException(e.getMessage(), e);
		}

		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					httpConnection.getInputStream(), Charset.forName(
					encode));

			return inputStreamReader;

		} catch (IOException e) {

			final String message = "Http connection can can not open stream. ";
			logger.debug(message);

			throw new RDFException(message + e.getMessage(), e);

		}
	}

	private String getEncoder(RDFFormat format) throws RDFException {
		String encoder = null;

		try {
			encoder = URLEncoder.encode(format.getDefaultMIMEType(), encode);

		} catch (UnsupportedEncodingException e) {
			String message = "Encode " + encode + " is not supported. ";
			logger.debug(message);
			throw new RDFException(message + e.getMessage(), e);
		}
		return encoder;
	}

	private void authenticate(String hostName, String password) {

		boolean usePassword = !(hostName.isEmpty() && password.isEmpty());

		if (usePassword) {

			final String myName = hostName;
			final String myPassword = password;

			Authenticator autentisator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(myName, myPassword
							.toCharArray());
				}
			};

			Authenticator.setDefault(autentisator);

		}
	}

	private void writeDataIntoFile(File dataFile, RDFFormatType formatType)
			throws RDFException {

		RepositoryConnection connection = null;

		try (OutputStreamWriter os = new OutputStreamWriter(
				new FileOutputStream(
				dataFile.getAbsoluteFile()), Charset
				.forName(encode))) {

			if (formatType == RDFFormatType.AUTO) {
				String fileName = dataFile.getName();
				RDFFormat newFormat = RDFFormat.forFileName(fileName,
						RDFFormat.RDFXML);
				formatType = RDFFormatType.getTypeByRDFFormat(newFormat);
			}
			MyRDFHandler myHandler = new MyRDFHandler(os, formatType);

			RDFHandler handler = myHandler.getRDFHandler();

			connection = repository.getConnection();

			if (graph != null) {
				connection.export(handler, graph);
			} else {
				connection.export(handler);
			}

			connection.commit();

		} catch (IOException ex) {
			throw new RDFException("Problems with file stream:" + ex
					.getMessage(), ex);
		} catch (RDFHandlerException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			throw new RDFException(
					"Repository connection failed while trying to load into XML file."
					+ ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn(
							"Failed to close connection to RDF repository while trying to load into XML file."
							+ ex.getMessage(), ex);
				}
			}
		}
	}

	private void extractDataFileFromHTTPSource(String path, String baseURI,
			boolean useStatisticHandler) throws RDFException {
		URL urlPath;
		try {
			urlPath = new URL(path);
		} catch (MalformedURLException ex) {
			throw new RDFException(ex.getMessage(), ex);
		}

		try (InputStreamReader inputStreamReader = new InputStreamReader(
				urlPath.openStream(), Charset.forName(encode))) {

			RDFFormat format = RDFFormat.forFileName(path, RDFFormat.RDFXML);
			RepositoryConnection connection = repository.getConnection();

			if (!useStatisticHandler) {

				addInputStreamToRepository(connection, inputStreamReader,
						baseURI, format, graph);
			} else {
				StatisticalHandler handler = parseFileUsingStatisticalHandler(
						format, inputStreamReader, baseURI);

				if (graph != null) {
					connection.add(handler.getStatements(), graph);
				} else {
					connection.add(handler.getStatements());
				}

				inputStreamReader.close();

			}


		} catch (IOException | RepositoryException | RDFParseException ex) {
			throw new RDFException(ex.getMessage(), ex);
		}
	}

	private void addFilesInDirectoryToRepository(RDFFormat format, File[] files,
			String baseURI,
			boolean useStatisticHandler, Resource... graphs) throws RDFException {

		if (files == null) {
			return; // nothing to add
		}

		for (int i = 0; i < files.length; i++) {
			File nextFile = files[i];
			addFileToRepository(format, nextFile, baseURI, useStatisticHandler,
					graphs);
		}
	}

	private File[] getFilesBySuffix(File dirFile, String suffix,
			boolean useAceptedSuffix) {

		if (useAceptedSuffix) {
			final String aceptedSuffix = suffix.toUpperCase();

			FilenameFilter acceptedFileFilter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.toUpperCase().endsWith(aceptedSuffix)) {
						return true;
					} else {
						return false;
					}
				}
			};

			return dirFile.listFiles(acceptedFileFilter);

		} else {
			return dirFile.listFiles();
		}

	}

	private void addInputStreamToRepository(RepositoryConnection connection,
			InputStreamReader inputStreamReader, String baseURI,
			RDFFormat format,
			Resource... graphs) throws IOException, RDFParseException, RepositoryException {

		if (graphs != null) {
			connection.add(inputStreamReader, baseURI, format, graphs);
		} else {
			connection.add(inputStreamReader, baseURI, format);
		}

	}

	private void addFileToRepository(RDFFormat rdfFormat, File dataFile,
			String baseURI,
			boolean useStatisticHandler, Resource... graphs) throws RDFException {

		RDFFormat fileFormat = RDFFormat.forFileName(
				dataFile.getAbsolutePath(),
				rdfFormat);

		RepositoryConnection connection = null;


		try (InputStreamReader is = new InputStreamReader(new FileInputStream(
				dataFile), Charset.forName(encode))) {

			connection = repository.getConnection();

			if (!useStatisticHandler) {

				addInputStreamToRepository(connection, is, baseURI, fileFormat,
						graphs);

			} else {

				StatisticalHandler handler = parseFileUsingStatisticalHandler(
						fileFormat, is, baseURI);

				if (graphs != null) {
					connection.add(handler.getStatements(), graphs);
				} else {
					connection.add(handler.getStatements());
				}

			}

			connection.commit();

		} catch (IOException | RDFParseException ex) {
			logger.debug(ex.getMessage(), ex);
		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage(), ex);
			throw new RDFException(
					"Error by adding file to repository " + ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					logger.warn("Failed to close connection to RDF repository.",
							ex);
				}
			}
		}
	}

	private StatisticalHandler parseFileUsingStatisticalHandler(
			RDFFormat fileFormat,
			InputStreamReader is, String baseURI) throws RDFException {

		StatisticalHandler handler = new StatisticalHandler();

		RDFParser parser = Rio.createParser(fileFormat);
		parser.setRDFHandler(handler);

		parser.setStopAtFirstError(false);
		parser.setParseErrorListener(new ParseErrorListener() {
			@Override
			public void warning(String msg, int lineNo, int colNo) {
				logger.warn(msg + "line:" + lineNo + "column:" + colNo);
			}

			@Override
			public void error(String msg, int lineNo, int colNo) {
				logger.error(msg + "line:" + lineNo + "column:" + colNo);
			}

			@Override
			public void fatalError(String msg, int lineNo, int colNo) {
				logger.error(msg + "line:" + lineNo + "column:" + colNo);
			}
		});
		try {
			parser.parse(is, baseURI);
		} catch (IOException | RDFParseException | RDFHandlerException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} finally {
			return handler;
		}
	}

	protected List<String> getInsertPartsTriplesQuery(int sizeSplit) {

		final String insertStart = "INSERT {";
		final String insertStop = "} ";

		List<String> parts = new ArrayList<>();

		StringBuilder builder = new StringBuilder();

		List<Statement> statements = getRepositoryStatements();

		if (!statements.isEmpty()) {
			builder.append(insertStart);

			int count = 0;

			for (Statement nextStatement : statements) {

				String subject = nextStatement.getSubject().stringValue();
				String predicate = nextStatement.getPredicate().stringValue();
				String object = nextStatement.getObject().stringValue();

				object = object.replaceAll("<br\\s*/>", "")
						.replaceAll("\\s+", "_")
						.replaceAll("<", "â€ą")
						.replaceAll(">", "â€ş");

				String appendLine = "<" + subject + "> <" + predicate + "> <" + object + "> . ";
				builder.append(appendLine.replaceAll("\\s+", " ").replaceAll(
						"\"", "'"));

				count++;
				if (count == sizeSplit) {
					builder.append(insertStop);
					parts.add(builder.toString());

					builder = new StringBuilder();
					builder.append(insertStart);
					count = 0;
				}
			}

			if (count > 0) {
				builder.append(insertStop);
				parts.add(builder.toString());
			}
		}

		return parts;
	}

	protected void addRDFStringToRepository(String rdfString, RDFFormat format,
			Resource... graphs) throws RDFException {
		RepositoryConnection connection = null;

		try {

			connection = repository.getConnection();
			StringReader reader = new StringReader(rdfString);

			if (graphs != null) {

				connection.add(reader, "", format, graphs);
			} else {
				connection.add(reader, "", format);
			}

			connection.commit();

		} catch (RepositoryException e) {
			logger.debug(e.getMessage());

		} catch (IOException | RDFParseException ex) {
			throw new RDFException(ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	protected void addStatement(Statement statement, Resource... graphs) {

		RepositoryConnection connection = null;

		try {

			connection = repository.getConnection();
			if (graphs != null) {

				connection.add(statement, graphs);
			} else {
				connection.add(statement);
			}

			connection.commit();

		} catch (RepositoryException e) {
			logger.debug(e.getMessage());


		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					throw new RuntimeException(ex);
				}
			}
		}
	}

	protected void createNewFile(File file) {

		if (file == null) {
			return;
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.debug(e.getMessage());
		}

	}

	protected Statement createNewStatement(String subjectName,
			String predicateName, String objectName) {
		ValueFactory valueFaktory = repository.getValueFactory();

		URI subject = valueFaktory.createURI(subjectName);
		URI predicate = valueFaktory.createURI(predicateName);
		Literal object = valueFaktory.createLiteral(objectName);

		Statement statement = new StatementImpl(subject, predicate, object);

		return statement;
	}

	protected Statement createNewStatement(String namespace, String subjectName,
			String predicateName, String objectName) {

		ValueFactory valueFaktory = repository.getValueFactory();

		URI subject = valueFaktory.createURI(namespace, subjectName);
		URI predicate = valueFaktory.createURI(namespace, predicateName);
		Literal object = valueFaktory.createLiteral(objectName);

		Statement statement = new StatementImpl(subject, predicate, object);

		return statement;
	}

	protected URI createNewGraph(String graphURI) {
		if (graphURI.toLowerCase().startsWith("http://")) {

			URI newGraph = new URIImpl(graphURI);
			return newGraph;

		} else {

			String newGraphUri = "http://" + graphURI;
			return createNewGraph(newGraphUri);

		}
	}

	/**
	 * Definitely destroy repository - use after all working in repository.
	 * Another repository using cause exception. For other using you have to
	 * create new instance.
	 */
	@Override
	public void shutDown() {

		Thread destroyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					repository.shutDown();
					logger.debug("Repository destroyed SUCCESSFULL");
				} catch (RepositoryException ex) {
					logger.debug(
							"Repository was not destroyed - potencial problems with locks .");
					logger.debug(ex.getMessage());
				}
			}
		});

		destroyThread.setDaemon(true);
		destroyThread.start();
	}

	public List<RDFTriple> getRDFTriplesInRepository() {

		List<RDFTriple> triples = new ArrayList<>();
		List<Statement> statements = getRepositoryStatements();

		int count = 0;

		for (Statement next : statements) {
			String subject = next.getSubject().stringValue();
			String predicate = next.getPredicate().stringValue();
			String object = next.getObject().stringValue();

			count++;

			RDFTriple triple = new RDFTriple(count, subject, predicate, object);
			triples.add(triple);
		}

		return triples;
	}

	@Override
	public void close() throws IOException {
		shutDown();
	}

	@Override
	public String getName() {
		return dataUnitName;
	}

	/**
	 * Return openRDF repository needed for almost every operation using RDF.
	 *
	 * @return openRDF repository.
	 */
	@Override
	public Repository getDataRepository() {
		return repository;
	}

	/**
	 * Return URI representation of graph where RDF data are stored.
	 *
	 * @return graph with stored data as URI.
	 */
	@Override
	public URI getDataGraph() {
		return graph;
	}

	/**
	 * Set data graph storage for given data in RDF format.
	 *
	 * @param newDataGraph new graph represented as URI.
	 */
	@Override
	public void setDataGraph(URI newDataGraph) {
		graph = newDataGraph;
		if (!isGraphDefault()) {
			logger.info("Set new data graph - " + graph.stringValue());
		}
	}

	private boolean isGraphDefault() {
		if (graph != null) {
			return graph.stringValue().equals(DEFAULT_GRAPH_NAME);
		} else {
			return false;
		}
	}

	@Override
	public boolean isReadOnly() {
		return isReadOnly;
	}

	@Override
	public void madeReadOnly() {
		setReadOnly(true);
	}

	protected void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
}
