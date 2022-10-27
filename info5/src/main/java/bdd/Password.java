package bdd;

import java.net.UnknownHostException;
import java.util.ArrayList;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 
 * @author Baptiste Masson
 *
 */
public class Password {
	private static java.lang.Exception Userdoesnotexist;
	// Class to create an encrypted password
	java.net.InetAddress localMachine; // nom de l'ordinateur
	private BCryptPasswordEncoder encoder; // clef de cryptage

	/**
	 * create the object to encode a password 
	 */
	public Password() {
		super();
		this.encoder = new BCryptPasswordEncoder(16); // valeur a mettre dans un fichier conf.txt pour plus de securite
		/*
		 * cette partie recupere l'id de la machine pour complexifier le mot de passe
		 */
		this.localMachine = null;
		try {
			localMachine = java.net.InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			this.localMachine = null;
		}
	}

	/**
	 * encode the password
	 * @param login: String the user name
	 * @param password: String the password of the user
	 * @return the encrypted password
	 */
	public String encrypt(String login, String password) {
		/*
		 * methode de chiffrement du mot de passe
		 */

		/*
		 * partie de chiffrement
		 */
		String chain;
		if (this.localMachine != null) { // cas si on ne peut pas avoir le nom de la machine hote
			chain = login + password + localMachine.getHostName(); // on cree la chaine a crypter
		} else {
			chain = login + password; // on cree la chaine a crypter
		}
		String crypt = encoder.encode(chain); // cryptage de la chaine

		return crypt;// on recupere le mot de passe (la chaine) chiffre
	}

	/**
	 * @param DB : Database where the password must be verified
	 * @param table : Table that contains the password
	 * @param login : String the user name
	 * @param password : String the password of the user
	 * @return true if the password, false else
	 * @throws Exception
	 */
	public Boolean verification(Database DB, Table table, String login, String password) throws Exception {
		ArrayList<String> columns = table.getColumns(); // on recupere les colonnes de la table
		String DBpassword = DB.selectWhere(table, columns.get(1), columns.get(2), login); // on verifie si un
																							// utilisateur existe
		DBpassword = DBpassword.replace("\n", ""); // normalement le login est unique
		if (DBpassword.isBlank()) { // si un utilisateur n'existe pas on renvoie une exception
			throw Userdoesnotexist;
		}
		String passwd = ""; // chaine du mot de passe
		if (this.localMachine != null) { // on cree la chaine de la meme maniere que dans la partie cryptage
			passwd = login + password + localMachine.getHostName();
		} else {
			passwd = login + password;
		}
		// on verifie si le mot de passe correspond
		Boolean res = encoder.matches(passwd, DBpassword);
		return res;
	};

}
