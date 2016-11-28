package dbms.test;

import java.io.FileNotFoundException;

import dbms.exception.DataTypeNotSupportedException;
import dbms.exception.DatabaseAlreadyCreatedException;
import dbms.exception.DatabaseNotFoundException;
import dbms.exception.IncorrectDataEntryException;
import dbms.exception.SyntaxErrorException;
import dbms.exception.TableAlreadyCreatedException;
import dbms.exception.TableNotFoundException;
import dbms.sqlparser.SQLParser;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		try {
			SQLParser.getInstance().parse("CREATE DATABASE testDB;").execute();
			SQLParser.getInstance().parse("USE DATABASE testDB;").execute();
			SQLParser.getInstance().parse("CREATE TABLE table1 (ID int, Name varchar, Gender varchar);").execute();
			SQLParser.getInstance().parse("USE DATABASE testDB;").execute();
			SQLParser.getInstance().parse("INSERT INTO table1 (ID, Name, Gender) VALUES (15, 'hamada14', 'Male');").execute();
			SQLParser.getInstance().parse("INSERT INTO table1 (ID, Name, Gender) VALUES (16, 'what_ever', 'Female');").execute();
			SQLParser.getInstance().parse("INSERT INTO table1 (ID, Name, Gender) VALUES (17, 'awalid', 'Male');").execute();
			SQLParser.getInstance().parse("INSERT INTO table1 (ID, Name, Gender) VALUES (18, 'tolbas', 'Female');").execute();
			SQLParser.getInstance().parse("INSERT INTO table1 (ID, Name, Gender) VALUES (19, 'khaled', 'Male');").execute();
			//SQLParser.getInstance().parse("SELECT * FROM table1;").execute();
//			System.out.print("\n\n");
//			// SQLParser.getInstance().parse("DELETE FROM table1;").execute();
			SQLParser.getInstance().parse("SELECT * FROM table1 where ID > 18;").execute();
//			XMLParser.getInstance().dropDataBase("testDB");
		} catch (DatabaseNotFoundException | TableNotFoundException | SyntaxErrorException | DataTypeNotSupportedException | TableAlreadyCreatedException | DatabaseAlreadyCreatedException | IncorrectDataEntryException e) {
			e.printStackTrace();
		}

	}
}