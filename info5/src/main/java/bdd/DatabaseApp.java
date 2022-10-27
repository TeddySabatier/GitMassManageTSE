package bdd;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Group;

import com.mashape.unirest.http.exceptions.UnirestException;

import api.FunctionsAPI;
import app.User;

/**
 * 
 * @author Baptiste Masson
 * @author Florian YUN
 *
 */
public class DatabaseApp {
	private static final Exception UserAlreadyExists = new Exception();
	Database local; // DB local for application user
	Password pass = new Password();

	/**
	 * create the application's database locally
	 * 
	 * @throws Exception
	 */
	public DatabaseApp() throws Exception {
		// creation of the local DB
		local = new Database("app", "./src/main/java/assets/");
		// test if the DB exists
		File test = new File("./src/main/java/assets/app.db");
		Boolean testVal = !test.exists();
		if (testVal) { // if it does not exists then the application creates it
			local.createDatabase();
		}

		// creation of the user table
		Table user = new Table("user");
		String[] confColumnlog = { "login", "text", "NOT NULL" };
		String[] confColumnpasswd = { "password", "text", "NOT NULL" };
		String[] confColumntoken = { "token", "text" };
		user.addColumn(confColumnlog);
		user.addColumn(confColumnpasswd);
		user.addColumn(confColumntoken);
		local.addTable(user);

		if (testVal) { // if the table was just created
			// creation of an admin user to allow the creation of other admin user
			String[] data = { "admin", "admin" };
			String password = pass.encrypt(data[0], data[1]);
			data[1] = password;
			local.insertData(user, data);
		}

		// creation of group table
		Table groupTable = new Table("groupe");
		String[] confGroupId = { "group_id", "integer", "NOT NULL", "group_id" };
		String[] confGroupName = { "group_name", "text", "NOT NULL", "group_name" };
		groupTable.addColumn(confGroupId);
		groupTable.addColumn(confGroupName);
		local.addTable(groupTable);

		// creation of module table
		Table module = new Table("module");
		String[] moduleName = { "module_name", "text", "NOT NULL", "module_name" };
		String[] moduleUser = { "user", "text", "NOT NULL", "user" };
		module.addColumn(moduleName);
		module.addColumn(moduleUser);
		local.addTable(module);

		// creation of the link table which binds groupe and module
		Table linkModuleGroup = new Table("link");
		String[] moduleId = { "module_id", "integer", "NOT NULL", "module_name" };
		linkModuleGroup.addColumn(moduleId);
		linkModuleGroup.addColumn(confGroupId);
		local.addTable(linkModuleGroup);

	}

	/**
	 * add an administrator in the database
	 * 
	 * @param login    : String the user name
	 * @param password : String the password of the user
	 * @param token    : String the gitlab token of the user
	 * @throws Exception
	 */
	public void addUser(String login, String password, String token) throws Exception {
		Table user = local.getTable("user"); // the app get the table
		String res = local.selectWhere(user, "login", "id", login); // check if the user exists
		if (res.isBlank()) { // if the user does not exists
			String[] data = { login, password, token }; // the app take the informations
			String passwd = pass.encrypt(data[0], data[1]); // get a crypted password
			data[1] = passwd; // change it in data
			local.insertData(user, data); // insert the data in the DB
		} else {
			throw UserAlreadyExists;
		}

	}

	/**
	 * verify if the user write the right password
	 * 
	 * @param login  : String the user name
	 * @param passwd : String the password of the user
	 * @return true if passwd matches with the password in the database, else false
	 */
	public Boolean verificationLogin(String login, String passwd) {
		// verification if a user uses the right password in the DB
		Table user = local.getTable("user");
		Boolean res;
		try {
			res = pass.verification(local, user, login, passwd); // verify if the user connect with the correct password
		} catch (Exception e) {
			res = false;
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * allow to get the user token
	 * 
	 * @param login : String the user name
	 * @return string the token
	 */
	public String getToken(String login) {
		// allow to get the token of the user
		Table user = local.getTable("user"); // get the user table
		String token = local.selectWhere(user, "login", "token", login); // select the token link to the login
		token = token.substring(0, token.length() - 1); // remove the \n at the end of the token
		return token;
	}

	/**
	 * @return the list of modules possessed by the user
	 */
	public ArrayList<String> getModule() {
		// allow to get the list of modules created by the connected user
		User currentUser = User.getInstance(); // get the information about the connected user
		String sql = "SELECT module_name FROM module WHERE user='" + currentUser.getDisplayName() + "'";
		return getList(sql, "module_name");
	}

	/**
	 * @param login       : String the user name
	 * @param newPassword : String the new password of the user
	 */
	public void updatePassword(String login, String newPassword) {
		// allow an user to change his own password
		Table user = local.getTable("user");
		String id = local.selectWhere(user, "login", "id", login);
		String password = pass.encrypt(login, newPassword);
		local.update(user, "password", password, id);
	}

	/**
	 * @param login : String the user name
	 * @param token : String the new token of the user
	 */
	public void updateToken(String login, String token) {
		// allow a user to change his own token
		Table user = local.getTable("user");
		String id = local.selectWhere(user, "login", "id", login);
		local.update(user, "token", token, id);
	}

	/**
	 * Method to get all groups that are in the specified module
	 * 
	 * @param module_name Name of the module
	 * @return List of group names
	 */
	public ArrayList<String> getListGroupsOfModules(String module_name) {
		String sql = "SELECT group_name FROM groupe WHERE group_id IN (SELECT group_id FROM link WHERE module_id IN (SELECT id FROM module WHERE module_name ='"
				+ module_name + "' AND user='" + User.getInstance().getDisplayName() + "'))";
		return getList(sql, "group_name");
	}

	/**
	 * Method to get all ids of groups in the specified module
	 * 
	 * @param module_name Name of the module
	 * @return List of group ids
	 */
	public ArrayList<String> getListGroupsIdOfModules(String module_name) {
		String sql = "SELECT group_id FROM link WHERE module_id IN (SELECT id FROM module WHERE module_name='"
				+ module_name + "' AND user='" + User.getInstance().getDisplayName() + "')";
		return getList(sql, "group_id");
	}

	/**
	 * Method to get all groups that are not in the specified module
	 * 
	 * @param module_name Name of the module
	 * @return List of group names
	 */
	public ArrayList<String> getListNotGroupsOfModules(String module_name) {
		String sql = "SELECT group_name FROM groupe WHERE group_id NOT IN (SELECT group_id FROM link WHERE module_id IN (SELECT id FROM module WHERE module_name ='"
				+ module_name + "' AND user='" + User.getInstance().getDisplayName() + "'))";
		return getList(sql, "group_name");
	}

	/**
	 * Method to get a list of a certain column given a column and a sql query
	 * 
	 * @param sql  SQL query
	 * @param data name of the column
	 * @return List of the specified data
	 */
	public ArrayList<String> getList(String sql, String data) {
		try (Connection conn = local.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			ArrayList<String> listToGet = new ArrayList<String>();
			while (rs.next()) {
				listToGet.add(rs.getString(data));
			}
			return listToGet;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return new ArrayList<String>();
		}
	}

	/**
	 * Verifies that each group of the table exists in Gitlab and deletes it
	 * otherwise Update the group table with the new groups
	 * 
	 * @param gitlabApi GitLabApi that is used
	 * @throws GitLabApiException
	 * @throws UnirestException
	 */
	public void updateGroups(GitLabApi gitlabApi) throws GitLabApiException, UnirestException {
		// Delete the groups that do not exist in Gitlab
		List<Group> listofGroups;
		if (User.getInstance().getToken() == null) {
			listofGroups = FunctionsAPI.getGroupsOwned(User.getInstance().getUsername(),
					User.getInstance().getPassword());
		} else {
			listofGroups = FunctionsAPI.getGroupsOwned(User.getInstance().getToken());
		}
		List<String> listofGroupsId = new ArrayList<String>();
		listofGroups.forEach((Group group) -> listofGroupsId.add(Integer.toString(group.getId())));
		Table groupTable = local.getTable("groupe");
		List<String> GroupsIdsInDB = local.selectColumnArray(groupTable, "group_id");
		GroupsIdsInDB.forEach((String id) -> {
			if (!listofGroupsId.contains(id)) {
				String group_name = local.selectWhere(groupTable, "group_id", "group_name",
						id.substring(0, id.length() - 1));
				deleteGroup(group_name.substring(0, group_name.length() - 1));
			}
		});
		// Updates the table with new groups
		listofGroups.forEach((Group group) -> {
			if (!checkGroupExists(group.getName())) {
				String[] dataGroup = { group.getId().toString(), group.getName() };
				local.insertData(groupTable, dataGroup);
			}
		});

	}

	/**
	 * Checks if a group exists in the Database
	 * 
	 * @param name is the name of the group
	 * @return true if the group exists, else false
	 */
	public Boolean checkGroupExists(String name) {
		Table group = local.getTable("groupe");
		String res = local.selectWhere(group, "group_name", "id", name);
		if (res.isBlank()) {
			return false;
		}
		return true;
	}

	/**
	 * @param module_name Name of the module to check
	 * @return Result query of the sql query
	 */
	public ArrayList<String> selectCheckModule(String module_name) {
		String sql = "SELECT module_name FROM module where module_name = '" + module_name + "' AND user='"
				+ User.getInstance().getDisplayName() + "'";
		ArrayList<String> res_query = new ArrayList<String>();
		try (Connection conn = local.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			while (rs.next()) {
				String row = "";
				row = rs.getString(1);
				res_query.add(row);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return res_query;
	}

	/**
	 * @param module_name Name of the module to check
	 * @return true if the module is already in the Database
	 */
	private Boolean checkModuleExists(String module_name) {
		ArrayList<String> res = selectCheckModule(module_name);
		if ((!res.isEmpty()) && (module_name.equals(res.get(0).toString()))) {
			return true;
		}
		return false;
	}

	/**
	 * Creates a Module
	 * @param module_name Name of the module
	 * @return true if the Module is successfully created
	 */
	public boolean createModule(String module_name) {
		Table module = local.getTable("module");
		if (!checkModuleExists(module_name)) {
			String[] dataGroup = { module_name, User.getInstance().getDisplayName() };
			local.insertData(module, dataGroup);
			return true;
		}
		return false;
	}

	/**
	 * @param module_id Id of the module
	 * @param group_id Id of the group
	 * @return Result query of the sql query
	 */
	public ArrayList<String> selectCheckGroupAdded(String module_id, String group_id) {
		String sql = "SELECT module_id, group_id FROM link where group_id = '" + group_id + "' AND module_id = '"
				+ module_id + "'";
		ArrayList<String> res_query = new ArrayList<String>();
		try (Connection conn = local.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			// loop through the result set
			while (rs.next()) {
				String row = "";
				for (int i = 0; i < 2; i++) {
					row = rs.getString(i + 1);
					res_query.add(row);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return res_query;
	}

	/**
	 * @param module_id Id of the module
	 * @param group_id Id of the group
	 * @return true if the group was already in the module, false otherwise
	 */
	private Boolean checkGroupAdded(String module_id, String group_id) {
		// Verifie si le groupe est deja dans le module
		ArrayList<String> res = selectCheckGroupAdded(module_id, group_id);
		if ((!res.isEmpty()) && (module_id.equals(res.get(1).toString())) && (group_id.equals(res.get(2).toString()))) {
			return true;
		}
		return false;
	}

	/**
	 * Adds a group to a module
	 * 
	 * @param module_name Name of the Module
	 * @param group_name Name of the group to add
	 */
	public void addGroupToModule(String module_name, String group_name) {
		Table linkModuleGroup = local.getTable("link");
		Table module = local.getTable("module");
		Table groupTable = local.getTable("groupe");
		String module_id = local.selectWhere(module, "module_name", "id", module_name);
		module_id = module_id.substring(0, module_id.length() - 1);
		String group_id = local.selectWhere(groupTable, "group_name", "group_id", group_name);
		group_id = group_id.substring(0, group_id.length() - 1);
		if ((checkModuleExists(module_name)) && (!checkGroupAdded(module_id, group_id))) {
			String[] dataGroup = { module_id, group_id };
			local.insertData(linkModuleGroup, dataGroup);
		}
	}

	/**
	 * Deletes a module from the Database
	 * 
	 * @param module_name Name of the Module to delete
	 */
	public void deleteModule(String module_name) {
		// Delete all lines in link where the module is specified
		String sql1 = "DELETE FROM link WHERE module_id=(SELECT id FROM module WHERE module_name='" + module_name
				+ "' AND user='" + User.getInstance().getDisplayName() + "')";
		// Deletes module from the module Table
		String sql2 = "DELETE FROM module WHERE module_name='" + module_name + "' AND user='"
				+ User.getInstance().getDisplayName() + "'";
		try {
			Connection conn = local.connect();
			Statement stmt = conn.createStatement();
			stmt.executeQuery(sql1);
		} catch (SQLException e) {
		}
		try {
			Connection conn = local.connect();
			Statement stmt = conn.createStatement();
			stmt.executeQuery(sql2);
		} catch (SQLException e) {
		}
	}

	/**
	 * Deletes a group from the Database
	 * 
	 * @param group_name name of the group to delete
	 */
	public void deleteGroup(String group_name) {
		// Delete all links where the group is specified
		String sql1 = "DELETE FROM link WHERE group_id=(SELECT group_id FROM groupe WHERE group_name='" + group_name
				+ "' AND module_id IN (SELECT id FROM module WHERE user='" + User.getInstance().getDisplayName() + "'))";
		// Delete the group from the groupe Table
		String sql2 = "DELETE FROM groupe WHERE group_name='" + group_name + "'";
		try {
			Connection conn = local.connect();
			Statement stmt = conn.createStatement();
			stmt.executeQuery(sql1);
		} catch (SQLException e) {
			// The query does not return a ResultSet
		}
		try {
			Connection conn = local.connect();
			Statement stmt = conn.createStatement();
			stmt.executeQuery(sql2);
		} catch (SQLException e) {
			// The query does not return a ResultSet
		}
	}

	/**
	 * Removes a group from the Database
	 * 
	 * @param module_name
	 * @param group_name  Name of the group to remove
	 */
	public void removeGroup(String module_name, String group_name) {
		// Deletes all lines in link where the group is specified
		String sql = "DELETE FROM link WHERE module_id=(SELECT id FROM module WHERE module_name='" + module_name
				+ "' AND user='" + User.getInstance().getDisplayName()
				+ "') AND group_id=(SELECT group_id FROM groupe WHERE group_name='" + group_name + "')";
		try {
			Connection conn = local.connect();
			Statement stmt = conn.createStatement();
			stmt.executeQuery(sql);
		} catch (SQLException e) {
			// The query does not return a ResultSet
		}
	}

	/**
	 * Inserts a group data in the groupe Table when it is created if it doesn't
	 * already exist
	 * 
	 * @param createdGroup name of the created group
	 * @param gitlabapi GitLabApi that is used
	 * @throws GitLabApiException
	 */
	public void createGroup(String createdGroup, GitLabApi gitlabapi) throws GitLabApiException {
		Table groupTable = local.getTable("groupe");
		if (!checkGroupExists(createdGroup)) {
			String[] dataGroup = { Integer.toString(FunctionsAPI.getGroupId(gitlabapi, createdGroup)), createdGroup };
			local.insertData(groupTable, dataGroup);
		}
	}
}
