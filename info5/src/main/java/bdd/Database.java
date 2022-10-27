package bdd;

import java.util.ArrayList;
import java.sql.*;

/**
 * 
 * @author Baptiste Masson
 *
 */

public class Database {
	/**
	 * Classe de gestion des bases de donnees
	 * 
	 */
	private ArrayList<Table> tables;
	private String url;


	/**
	 * create the object Database
	 * @param name : String the name of the file
	 * @param path : the path to access the file
	 */
	public Database(String name, String path) {
		super();
		this.tables = new ArrayList<Table>();
		this.url = "jdbc:sqlite:" + path + name + ".db";
	}

	/**
	 * create the database file 
	 */
	public void createDatabase() {
		// create the DB file

		try (Connection conn = this.connect()) {
			if (conn != null) { // test if the app can connect to the database
				DatabaseMetaData meta = conn.getMetaData();
				// message to show in the console that the database is created
				// System.out.println("The driver name is " + meta.getDriverName());
				// System.out.println("A new database has been created.");
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @return the database connector
	 */
	public Connection connect() {
		// create a connector to the database file
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(this.url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	/**
	 * @param table : Table atable to add in the database
	 */
	public void addTable(Table table) {
		// add a table to the database

		// Save of the table locally in the class
		tables.add(table);

		// SQL statement for creating a new table if it does not exist
		String sql = "CREATE TABLE IF NOT EXISTS " + table.getName() + "(\n" + table.getFormTable() + ");";
		try (Connection conn = this.connect(); Statement stmt = conn.createStatement()) {
			// create a new table
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @param table : Table in which the application wants to insert the data
	 * @param data : String[] the data to insert in the database
	 */
	public void insertData(Table table, String[] data) {
		// insert in the table of the Database
		String comp_sql = "(";
		String value = "VALUES(";
		ArrayList<String> columns = table.getColumns();
		for (int i = 0; i < data.length; i++) {
			if (i + 1 == data.length) { // case if that is the end of the data set or not
				comp_sql = comp_sql + columns.get(i + 1) + ") ";
				value = value + "?)";
			} else {
				comp_sql = comp_sql + columns.get(i + 1) + ", ";
				value = value + "?,";
			}

		}
		ArrayList<String> types = table.getTypes();
		// creation of the sql request
		String sql = "INSERT INTO " + table.getName() + comp_sql + value;

		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < data.length; i++) {
				// definition of the type of data
				DatabaseTool.setObject(pstmt, i + 1, data[i], types.get(i + 1));

			}
			// request execution
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @param table : Table in which the application will select the data
	 * @return a list of data which are string
	 */
	public ArrayList<String> selectAll(Table table) {
		// to get all the element of the table
		String sql = "SELECT * FROM " + table.getName(); // SQL request
		ArrayList<String> column = table.getColumns();
		ArrayList<String> types = table.getTypes();
		ArrayList<String> data = new ArrayList<String>();
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			while (rs.next()) {
				String res_query = "";
				for (int i = 0; i < column.size(); i++) {

					res_query = res_query + DatabaseTool.getObject(rs, types.get(i), column.get(i)) + "   ";

				}
				data.add(res_query); // the result is saved in data
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return data;
	}


	/**
	 * @param table : Table in which the application will select the data
	 * @param columnCond : String the name of the column on which the application will set a condition
	 * @param column : String the name of the column the application requests the data
	 * @param condition : string the specification of the data in columnCond
	 * @return
	 */
	public String selectWhere(Table table, String columnCond, String column, String condition) {
		/*
		 * permet de recuperer une donnee precise si la condition est unique
		 */

		// creation de la requete
		String sql = "SELECT " + column + " FROM " + table.getName() + " WHERE " + columnCond + " = " + "'" + condition
				+ "'" + ';';

		ArrayList<String> columns = table.getColumns();
		ArrayList<String> types = table.getTypes(); // permet de choisir le bon type lors de la recuperation

		String resultat = "";// permet de recuperer le resultat de la requete
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			String res_query = "";
			// loop through the result set
			while (rs.next()) {
				Integer index = table.getIndex(column);
				res_query = DatabaseTool.getObject(rs, types.get(index), columns.get(index));

			}
			resultat = resultat + res_query + "\n";
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return resultat;
	}

	/**
	 * @param table : Table in which the application will delete the data
	 * @param element : String Information about the row we want to delete
	 * @param column : String the name of the column of element
	 */
	public void delete(Table table, String element, String column) {
		String idstr = this.selectWhere(table, column, "id", element);
		Integer id = Integer.valueOf(idstr);

		String sql = "DELETE FROM " + table.getName() + " WHERE id = ?";

		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			// set the corresponding param
			pstmt.setInt(1, id);
			// execute the delete statement
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @param name : String the name of the table
	 * @return the table which has the corresponding name
	 */
	public Table getTable(String name) {
		Table resultat = new Table("error");
		int n = tables.size();
		for (int i = 0; i < n; i++) {
			if (tables.get(i).getName() == name) {
				resultat = tables.get(i);
			}

		}

		return resultat;

	}

	/**
	 * @param table : Table in which the application will select the data
	 * @param column : String the name of the column in which the application will requests the data
	 * @return the list of element in the column
	 */
	public ArrayList<String> selectColumnArray(Table table, String column) {
		//
		String sql = "SELECT " + column + " FROM " + table.getName();
		ArrayList<String> columns = table.getColumns();
		ArrayList<String> types = table.getTypes();
		Integer index = table.getIndex(column);
		ArrayList<String> res = new ArrayList<String>();
		try (Connection conn = this.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			// loop through the result set
			while (rs.next()) {

				res.add(DatabaseTool.getObject(rs, types.get(index), columns.get(index)));

			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return res;
	}

	/**
	 * @param table : Table in which the application will select the data
	 * @param column : String the name of the column in which the application will update the data
	 * @param data : String the element that will replace the old data in column
	 * @param id : the id of the element the application wants to update
	 */
	public void update(Table table, String column, String data, String id) {
		// update 1 data in a specific row

		Integer index = table.getIndex(column); // get the index of the column
		ArrayList<String> types = table.getTypes(); // get the type of the column
		String sql = "UPDATE " + table.getName() + " SET " + column + " = ? WHERE id = " + id; // creation of the
																								// request id is the
																								// element to update

		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

			DatabaseTool.setObject(pstmt, 1, data, types.get(index)); // 1 because the app change only 1 element
			// update
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
