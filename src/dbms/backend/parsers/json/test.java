package dbms.backend.parsers.json;

import java.util.LinkedHashMap;
import java.util.Map;

import dbms.backend.BackendController;
import dbms.datatypes.DBInteger;
import dbms.datatypes.DBString;
import dbms.exception.DatabaseAlreadyCreatedException;
import dbms.exception.DatabaseNotFoundException;
import dbms.exception.IncorrectDataEntryException;
import dbms.exception.TableAlreadyCreatedException;
import dbms.exception.TableNotFoundException;

public class test {
	private final static BackendController JSONParserConc = BackendController.getInstance();
	public static void main(String[] args) {
		try {
			JSONParserConc.createDatabase("mine");
		} catch (DatabaseAlreadyCreatedException e) {
			e.printStackTrace();
		}
		Map<String, Class> passMap = new LinkedHashMap<String, Class>();
		passMap.put("column_1", DBInteger.class);
		passMap.put("column_2", DBString.class);
		try {
			JSONParserConc.createTable("table11", passMap);
		} catch (DatabaseNotFoundException | TableAlreadyCreatedException | IncorrectDataEntryException e) {
			e.printStackTrace();
		}
		Map<String, Object> entriesMap = new LinkedHashMap<String, Object>();
		entriesMap.put("column_1", 550);
		entriesMap.put("column_2", "KHalED");
		try {
			JSONParserConc.insertIntoTable("table11", entriesMap);
		} catch (DatabaseNotFoundException | TableNotFoundException | IncorrectDataEntryException e) {
			e.printStackTrace();
		}
	}
}
