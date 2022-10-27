package bdd;

import java.util.ArrayList;

/**
 * 
 * @author Baptiste Masson
 *
 */
public class Table {
	//
	private static final Exception NoDefineColumn = new Exception();
	private String name; // name of the table
	private ArrayList<String> columns; // list the name of all columns
	private ArrayList<String> types; // list the element type of each column
	private ArrayList<String> indicator_null; // allow to know if the element is a primary key or can be null or not
	private ArrayList<String> foreignkey; // name of the key in the other table if it's a use in it else "None"

	/**
	 * create a table object
	 * @param name : String the name of the Table
	 */
	public Table(String name) {
		/*
		 * Create the basis of the Table of the database
		 */
		super();
		this.name = name;
		// creation of all the component of Table
		columns = new ArrayList<String>();
		types = new ArrayList<String>();
		indicator_null = new ArrayList<String>();
		foreignkey = new ArrayList<String>();

		/*
		 * creating the primary key column
		 */
		columns.add("id");
		types.add("integer");
		indicator_null.add("PRIMARY KEY");
		foreignkey.add(name + "_id");
	}

	/**
	 * @param confColumn : String[] the configuration of the colonne (1<length<5)
	 * @throws Exception
	 */
	public void addColumn(String[] confColumn) throws Exception {
		/*
		 * allow to add a column with a indicator that is not NULL
		 */
		int n = confColumn.length;
		if (n < 2 || n > 4) {
			// the app have too many or less information to create a column
			throw NoDefineColumn;
		}
		// elements that help to create a column if the app had 2 or 3 elements
		String indicator;
		String key;
		if (n == 2) {
			indicator = "NULL"; // the column might be empty
			key = "NONE"; // the column is not a foreign key
		} else if (n == 3) {
			indicator = confColumn[2];
			key = "NONE"; // the column is not a foreign key
		} else {
			indicator = confColumn[2];
			key = confColumn[3];
		}
		// the column is added to the Table
		columns.add(confColumn[0]);
		types.add(confColumn[1]);
		indicator_null.add(indicator);
		foreignkey.add(key);
	}

	/**
	 * @return the name of the table
	 */
	public String getName() {
		// give the name of the Table
		return name;
	}

	/**
	 * @return the columns of the table
	 */
	public ArrayList<String> getColumns() {
		// get the columns of the table
		return columns;
	}

	/**
	 * @return the type of each columns
	 */
	public ArrayList<String> getTypes() {
		// give the type of each columns
		return types;
	}

	/**
	 * @return indicator about the data
	 */
	public ArrayList<String> getIndicatorNull() {
		// give if the element can be null
		return indicator_null;
	}

	/**
	 * create a string to allow the creation of the table in the database
	 * @return String which contains the definition of the table
	 */
	public String getFormTable() {

		String form_table = ""; // string that will contain the final string

		String backline = ",\n"; // separator between every columns
		for (int i = 0; i < columns.size(); i++) {
			if ((i + 1) == columns.size()) {
				backline = "\n"; // at the last column the "," is not needed
			}

			if (indicator_null.get(i) != "NULL") {
				// if the element is not Null that must indicated to the Database
				form_table = form_table + columns.get(i) + " " + types.get(i) + " " + indicator_null.get(i) + backline;
			} else {
				form_table = form_table + columns.get(i) + " " + types.get(i) + backline;
			}
		}

		return form_table;
	}

	/**
	 * @param column : String the name of the column
	 * @return int the index of the column in the table
	 */
	public Integer getIndex(String column) {
		// give the index of the column if the name of this is the same as the string
		// column
		Integer res = columns.indexOf(column);
		return res;
	}

	/**
	 * create a list of columns for a query
	 * @param manytables : Boolean indicate if the query use many tables
	 * @return String : list of columns name
	 */
	public String getColumnsName(Boolean manytables) {
		// give the name of all the columns in the table
		Integer length = columns.size();
		String columnsName = ""; // final string
		String separator = ", "; // to separate all the columns
		String table = ""; // if the table must be precised
		if (manytables) {
			table = name + "."; // if the app request to use more than on table
		}
		for (int i = 0; i < length; i++) {
			if ((i + 1) == length) {
				separator = "";
			}
			columnsName = columnsName + table + columns.get(i) + separator;
		}

		return columnsName;
	}

	/**
	 * @param index : int the index of the column
	 * @param nametable : name of the other tabe
	 * @return String to complete the "on" in a query with join on
	 */
	public String getForeignKey(Integer index, String nametable) {
		// give the string to complete a join on request which the index of the chosen
		// column is index
		return name + "." + columns.get(index) + " = " + nametable + "." + foreignkey.get(index);
	}

}