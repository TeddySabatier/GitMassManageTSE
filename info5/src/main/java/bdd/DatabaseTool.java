package bdd;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * 
 * @author Baptiste Masson
 *
 */
public class DatabaseTool {
	// classe au design pattern d'abstract factory permet de faciliter la gestion de l'ajout ou la recuperation de donnees

	
	/**
	 * obtain a data from the database
	 * @param rs : ResultSet, result of the query
	 * @param type : String the type of the data
	 * @param column : String the name of the column which contains the data
	 * @return String (the data)
	 * @throws SQLException
	 */
	public static String getObject(ResultSet rs, String type, String column) throws SQLException {
		// permet d'obtenir un objet (utile lors d'un select)
		if(type== "integer") { // si un objet est un entier
			return Integer.toString(rs.getInt(column)); // on recupere l'entier sous forme de chaine de caractere
		}
		else {
			if(type == "float") { // si un objet est un flottant
				return Float.toString(rs.getFloat(column)); // on recupere le flottant sous forme de chaine de caractere
			}
			else{ // on utilise majoritairement des donnees textes 
				return rs.getString(column);
			}
		}
	}
	
	
	/**
	 * place an object in the database
	 * @param pstmt : PreparedStatement
	 * @param column : String the name of the column which will contain the data
	 * @param data : String what will be placed in the database
	 * @param type : String the type of the data
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
	public static void setObject(PreparedStatement pstmt,int column, String data, String type) throws NumberFormatException, SQLException {
		// permet de definir un objet (utile lors d'un insert ou d'un update)
		if(type== "integer") { // si un objet doit etre un entier
			pstmt.setInt(column, Integer.parseInt(data)); // on enregistre le string sous forme d'entier 
		}
		else {
			if(type== "float") { // si un objet doit etre un flottant
				pstmt.setFloat(column, Float.parseFloat(data)); // on enregistre le string sous forme de flottant
			}
			else {
				pstmt.setString(column, data);
			}
		}
	}

}

