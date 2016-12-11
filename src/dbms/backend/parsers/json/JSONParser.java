package dbms.backend.parsers.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dbms.backend.BackendParser;
import dbms.backend.BackendParserFactory;
import dbms.datatypes.DBDate;
import dbms.datatypes.DBFloat;
import dbms.datatypes.DBInteger;
import dbms.datatypes.DBString;
import dbms.exception.DatabaseNotFoundException;
import dbms.exception.TableAlreadyCreatedException;
import dbms.exception.TableNotFoundException;
import dbms.util.Table;

import java.io.*;
import java.util.ResourceBundle;

public class JSONParser extends BackendParser {
	public static final String KEY = "json";
	private static JSONParser instance = null;
	private static final String WORKSPACE_DIR = System.getProperty("user.home")
			+ File.separator + "databases";
	private static final ResourceBundle CONSTANTS = ResourceBundle.getBundle(
			"dbms.backend.parsers.json.Constants");
	private GsonBuilder builder;
	Gson gson;

	static {
		BackendParserFactory.getFactory().register(KEY, getInstance());
	}

	private JSONParser() {
		builder = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
			@Override
			public boolean shouldSkipField(FieldAttributes fieldAttributes) {
				return false;
			}

			@Override
			public boolean shouldSkipClass(Class<?> aClass) {
				return false;
			}
		});
		builder.serializeNulls();
		builder.disableHtmlEscaping();
		builder.registerTypeAdapterFactory(new ClassTypeAdapterFactory());
		builder.registerTypeAdapter(DBString.class, new ClassTypeAdapter());
		builder.registerTypeAdapter(DBInteger.class, new ClassTypeAdapter());
		builder.setPrettyPrinting();
		gson = builder.create();
	}

	public static JSONParser getInstance() {
		if (instance == null) {
			instance = new JSONParser();
		}
		return instance;
	}

	@Override
	public BackendParser getParser() {
		return instance;
	}

	@Override
	public void loadTable(Table table) throws TableNotFoundException, DatabaseNotFoundException {
		builder.registerTypeAdapterFactory(new ClassTypeAdapterFactory());
		builder.registerTypeAdapter(DBString.class, new ClassTypeAdapter());
		builder.registerTypeAdapter(DBInteger.class, new ClassTypeAdapter());
		builder.registerTypeAdapter(DBFloat.class, new ClassTypeAdapter());
		builder.registerTypeAdapter(DBDate.class, new ClassTypeAdapter());
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(table.getName() + CONSTANTS.getString("extension.json")));
		} catch (FileNotFoundException e) {
			throw new TableNotFoundException();
		}
		table = gson.fromJson(bufferedReader, Table.class);
	}

	@Override
	public void writeToFile(Table table) throws TableNotFoundException, DatabaseNotFoundException {
		File tableFile = openTable(table.getDatabase().getName(), table.getName());
		try {
			write(table, tableFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static void writeToFile1(Table table , File tableFile) throws
//		TableNotFoundException, DatabaseNotFoundException {
//		try {
//			write(table, tableFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void createTable(Table table) throws DatabaseNotFoundException, TableAlreadyCreatedException {
		File tableFile = new File(openDB(table.getDatabase().getName()),
				table.getName() + CONSTANTS.getString("extension.json"));
		if (tableFile.exists()) {
			throw new TableAlreadyCreatedException();
		}
		try {
			write(table, tableFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void write(Table table, File tableFile) throws IOException {
		
		FileWriter writer = new FileWriter(tableFile);
		writer.write(gson.toJson(table));
		writer.close();
	}


	@Override
	public void dropTable(Table table) throws DatabaseNotFoundException {
		File tableFile = new File(openDB(table.getDatabase().getName()), table.getName()
				+ CONSTANTS.getString("extension.json"));
		if (tableFile.exists()) {
			tableFile.delete();
		}
	}

	private static File openTable(String dbName, String tableName) throws TableNotFoundException, DatabaseNotFoundException {
		File tableFile = new File(openDB(dbName), tableName + CONSTANTS.getString("extension.json"));
		if (!tableFile.exists()) {
			throw new TableNotFoundException();
		}
		return tableFile;
	}

	private static File openDB(String dbName) throws DatabaseNotFoundException {
		File database = new File(WORKSPACE_DIR + File.separator + dbName);
		if (!database.exists()) {
			throw new DatabaseNotFoundException();
		}
		return database;
	}

//	public static void main(String[] argv) {
//		Database database = new Database("Yakout");
//		Table tb = database.createTable("anas14");
//		tb.addColumn(new Column("hamada", DBString.class));
//		tb.addColumn(new Column("Naggar", DBString.class));
//		tb.addColumn(new Column("tolba", DBInteger.class));
//		
//		try {
//			createDatabase(database);
//		} catch (DatabaseAlreadyCreatedException e1) {
//			e1.printStackTrace();
//		}
//		
//		try {
//			createTable1(tb);
//		} catch (DatabaseNotFoundException | TableAlreadyCreatedException e) {
//			e.printStackTrace();
//		}
//		tb.clear();
//		try {
//			loadTable1(tb);
//		} catch (TableNotFoundException | DatabaseNotFoundException e) {
//			e.printStackTrace();
//		}
//		System.out.println(tb);
//}
}