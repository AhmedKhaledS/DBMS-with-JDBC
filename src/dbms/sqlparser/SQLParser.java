package dbms.sqlparser;

import dbms.exception.SyntaxErrorException;
import dbms.sqlparser.sqlInterpreter.Where;
import dbms.sqlparser.sqlInterpreter.rules.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * validate and parse a sql query.
 */
public class SQLParser {

	/**
	 * singleton instance of {@link SQLParser}.
	 */
	private static SQLParser instance;

	private SQLParser() {
	}

	/**
	 * validate the query.
	 * 
	 * @param regex
	 *            the pattern to validate against.
	 * @param query
	 *            the sql statement.
	 * @return return the index of the error
	 */
	public Matcher validate(Pattern regex, String query) throws SyntaxErrorException {
		for (int i = 0; i <= query.length(); i++) {
			Matcher m = regex.matcher(query.substring(0, i));
			if (!m.matches() && !m.hitEnd()) {
				throw new SyntaxErrorException("Error: near \"" + query.substring(i - 1, i) + "\" : syntax error");
			}
		}
		if (regex.matcher(query).matches()) {
			return regex.matcher(query);
		} else {
			throw new SyntaxErrorException("Error: statement must end with \";\": syntax error");
		}
	}

	/**
	 * parse the query.
	 * 
	 * @param query
	 *            sql statement
	 * @return {@link Expression}.
	 * @throws SyntaxErrorException
	 */
	public Expression parse(String query) throws SyntaxErrorException {
		Pattern rulePattern = PatternsHolder.getRulePattern();
		Matcher ruleMatcher = validate(rulePattern, query);

		ruleMatcher.matches();
		switch (ruleMatcher.group(1).toLowerCase()) {
		case "select":
			Pattern selectPattern = PatternsHolder.getSelectPattern();
			return parseSelect(validate(selectPattern, query));
		case "drop":
			Pattern dropPattern = PatternsHolder.getDropPattern();
			return parseDrop(validate(dropPattern, query));
		case "insert":
			Pattern insertPattern = PatternsHolder.getInsertIntoPattern();
			return parseInsert(validate(insertPattern, query));
		case "delete":
			Pattern deletePattern = PatternsHolder.getDeletePattern();
			return parseDelete(validate(deletePattern, query));
		case "update":
			Pattern updatePattern = PatternsHolder.getUpdatePattern();
			return parseUpdate(validate(updatePattern, query));
		case "create":
			Pattern createPattern = PatternsHolder.getCreatePattern();
			return parseCreate(validate(createPattern, query));
		case "alter":
			Pattern alterPattern = PatternsHolder.getAlterPattern();
			return parseAlter(validate(alterPattern, query));
		case "use":
			Pattern usePattern = PatternsHolder.getUsePattern();
			return parseUse(validate(usePattern, query));
		default:
			return null;
		}
	}

	/**
	 * will returns the SQLParser singleton instance or create a new instance if
	 * not found.
	 * 
	 * @return the singleton instance.
	 */
	public static SQLParser getInstance() {
		if (instance == null) {
			instance = new SQLParser();
		}
		return instance;
	}

	private Expression parseAlter(Matcher matcher) {
        matcher.matches();
        String tableName = matcher.group(1);
        String columnName;
        Class dataType;

        if (matcher.group(7) != null) {
            columnName = matcher.group(8);
            if (matcher.group(9).toLowerCase().compareTo("int") == 0) {
                dataType = Integer.class;
            } else {
                dataType = String.class;
            }
            AlterAdd alterAdd = new AlterAdd(tableName, columnName, dataType);
            return alterAdd;
        } else {
            columnName = matcher.group(5);
            AlterDrop alterDrop = new AlterDrop(tableName, columnName);
            return alterDrop;
        }
    }

    /**
     * parse insert statement.
     * @param matcher matched pattern from query.
     * @return {@link Expression}.
     * @throws SyntaxErrorException
     */
    private Expression parseInsert(Matcher matcher) throws SyntaxErrorException {
        matcher.matches();
        String tableName = matcher.group(1);
        String[] columns = matcher.group(2).split(",");
        String[] values = matcher.group(4).split(",");
        if (columns.length != values.length) {
            throw new SyntaxErrorException("Error: Columns number does not match values number");
        }
        HashMap<String, Object> entryMap = new LinkedHashMap<>();
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i].trim();
            String value = values[i].trim();
            if (value.startsWith("'") || value.startsWith("\"")) {
                entryMap.put(column, value.replaceAll("('|\")", ""));
            } else {
                entryMap.put(column, Integer.parseInt(value));
            }
        }
        return new InsertIntoTable(tableName, entryMap);
    }

	/**
	 * parse select statement
	 * 
	 * @param matcher
	 *            matched pattern from query.
	 * @return {@link Expression}.
	 */
	private Expression parseSelect(Matcher matcher) {
		matcher.matches();
		String tableName = matcher.group(5);
		Select select = new Select(tableName);
		if (matcher.group(4) != null) {
			select.setColumns(null);
		} else {
			String[] columnsTemp = matcher.group(2).split(",");
			Collection<String> columns = new ArrayList<>();
			for (int i = 0; i < columnsTemp.length; i++) {
				columns.add(columnsTemp[i].trim());
			}
			select.setColumns(columns);
		}

		if (matcher.group(7) != null) { // if there is order by statement.
            select.setOrderBy(matcher.group(8));
            select.setAscending(matcher.group(9).equals("ASC"));
		}
		if (matcher.group(11) != null) { // if there is where condition
			select.setWhere(new Where(matcher.group(11)));
		}
		return select;
	}

	/**
	 * parse drop statement.
	 * 
	 * @param matcher
	 *            matched pattern from query.
	 * @return {@link Expression}.
	 */
	private Expression parseDrop(Matcher matcher) {
		matcher.matches();
		switch (matcher.group(1).toLowerCase()) {
		case "database":
			return new DropDatabase(matcher.group(2));
		case "table":
			return new DropTable(matcher.group(2));
		default:
			return null;
		}
	}

	/**
	 * parse delete statement.
	 * 
	 * @param matcher
	 *            matched pattern from query.
	 * @return {@link Expression}.
	 */
	private Expression parseDelete(Matcher matcher) {
		matcher.matches();
		Delete delete = new Delete(matcher.group(2));

		if (matcher.group(4) != null) {
			delete.setWhere(new Where(matcher.group(4)));
		}
		return delete;
	}

    private Expression parseUpdate(Matcher matcher) {
        matcher.matches();
        Map<String, Object> values = new HashMap<>();
        Map<String, String> columns = new HashMap<>();

        String[] setValues = matcher.group(2).split(",");
        for (int i = 0; i < setValues.length; i++) {
            String key = setValues[i].split("=")[0].trim();
            String value = setValues[i].split("=")[1].trim();
            if (value.startsWith("'") || value.startsWith("\"")) {
                values.put(key, value.replaceAll("('|\")", ""));
            } else {
                try {
                    values.put(key, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    columns.put(key, value);
                }
            }
        }
        Update update = new Update(matcher.group(1), values, columns);

        if (matcher.group(7) != null) {
            update.setWhere(new Where(matcher.group(7)));
        }
        return update;
    }
	/**
	 * parse create statement.
	 * 
	 * @param matcher
	 *            matched pattern from query.
	 * @return {@link Expression}.
	 */
	private Expression parseCreate(Matcher matcher) {
		matcher.matches();

		if (matcher.group(1).toLowerCase().startsWith("database")) {
			return new CreateDatabase(matcher.group(3));
		}

		String[] columnsDesc = matcher.group(6).split(",");
		Map<String, Class> columns = new LinkedHashMap<>();
		for (int i = 0; i < columnsDesc.length; i++) {
			String key = columnsDesc[i].trim().split("\\s+")[0];
			switch (columnsDesc[i].trim().split("\\s+")[1].toLowerCase()) {
			case "int":
				columns.put(key, Integer.class);
				break;
			case "varchar":
				columns.put(key, String.class);
				break;
			case "date":
				columns.put(key, java.sql.Date.class);
				break;
			}
		}

		return new CreateTable(matcher.group(5), columns);
	}

	/**
	 * parse the use statement.
	 * 
	 * @param matcher
	 *            matched pattern from query.
	 * @return {@link Expression}.
	 */
	private Expression parseUse(Matcher matcher) {
		matcher.matches();
		return new UseDatabase(matcher.group(1));
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println(((Select) new SQLParser()
					.parse("select * from tableName order by col1 DESC where (col1==fo);")).getWhere().getPostfix());
		} catch (SyntaxErrorException e) {
			System.out.println(e.toString());
		}
	}
}