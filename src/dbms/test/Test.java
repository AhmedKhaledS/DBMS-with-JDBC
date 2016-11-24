package dbms.test;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import dbms.exception.DatabaseAlreadyCreatedException;
import dbms.exception.DatabaseNotFoundException;
import dbms.exception.SyntaxErrorException;
import dbms.exception.TableAlreadyCreatedException;
import dbms.exception.TableNotFoundException;
import dbms.util.Result;
import dbms.util.ResultSet;
import dbms.xml.XMLParser;

public class Test {
	public static void main(String[] args) {
		try {
			XMLParser.getInstance().createDatabase("testDB");
		} catch (DatabaseAlreadyCreatedException e) {
			e.printStackTrace();
		}
		Map<String, Class> columns = new HashMap<>();
		columns.put("ID", Integer.class);
		columns.put("Name", String.class);
		columns.put("Organization", String.class);
		try {
			XMLParser.getInstance().createTable("testDB", "table1", columns);
		} catch (DatabaseNotFoundException | TableAlreadyCreatedException | TransformerException | SyntaxErrorException e) {
			e.printStackTrace();
		}
		Map<String, Object> rows = new HashMap<>();
		rows.put("ID", 125);
		rows.put("Name", "hamada14");
		rows.put("Organization", "AlexU");
		try {
			XMLParser.getInstance().insertIntoTable("testDB", "table1", rows);
		} catch (DatabaseNotFoundException | TableNotFoundException | SyntaxErrorException e) {
			e.printStackTrace();
		}
		rows.put("ID", 1532);
		rows.put("Name", "tryyy");
		rows.put("Organization", "zzzzzzz");
		try {
			XMLParser.getInstance().insertIntoTable("testDB", "table1", rows);
		} catch (DatabaseNotFoundException | TableNotFoundException | SyntaxErrorException e) {
			e.printStackTrace();
		}
		ResultSet x = null;
		try {
			x = XMLParser.getInstance().select("testDB", "table1", null);
		} catch (DatabaseNotFoundException | TableNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Result res : x) {
			System.out.println(res.getInt("ID"));
			System.out.println(res.getString("Name"));
			System.out.println(res.getString("Organization"));
		}
	}
}