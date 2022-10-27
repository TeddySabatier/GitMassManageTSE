package api;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
//Import for API
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author Teddy SABATIER
 *
 */
public class ImportCSV {

	/**
	 * @param path
	 * @param gitLabApi
	 * @param token
	 * @param usernameUser
	 * @param passwordUser
	 * @param host
	 * @return ArrayList<String[]> status
	 */
	public static ArrayList<String[]> ImportProjectsMembers(String path, GitLabApi gitLabApi, String token,
			String usernameUser, String passwordUser, String host) {
		if (host == null) {
			host = "https://code.telecomste.fr";
		}
		// Header of the CSV File
		String[] Headers = { "username", "group", "project" };
		ArrayList<String[]> status = new ArrayList<String[]>();
		try {
			status = givenCSVFile_whenRead_thenContentsAsExpected(path, Headers, gitLabApi, token, usernameUser,
					passwordUser, host);
			// Add import done at the end should be displayed as a popup
			String[] stat = new String[1];
			stat[0] = "Import terminé pas d'erreur majeure";
			status.add(stat);
		} catch (IOException e) {
			// Add the error message at the end should be displayed as a popup
			String[] stat = new String[1];
			stat[0] = e.getMessage();
			status.add(stat);
		}
		return status;
	}

	/**
	 * Import the groups/projects and members from a CSV file
	 * 
	 * @param path
	 * @param Headers
	 * @param gitLabApi
	 * @param token
	 * @param usernameUser
	 * @param passwordUser
	 * @param host
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String[]> givenCSVFile_whenRead_thenContentsAsExpected(String path, String[] Headers,
			GitLabApi gitLabApi, String token, String usernameUser, String passwordUser, String host)
			throws IOException {

		Reader in = new FileReader(path);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';').parse(in);
		int iteration = 0;
		// Variables used for the API
		String user_id = "";
		String project_id = "";
		String project_name = "";
		int permission = 0;
		HttpResponse<JsonNode> response = null;
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		String requestAnswers = "";
		JsonElement je = null;
		// List that will contain all the informations
		ArrayList<String[]> status = new ArrayList<String[]>();

		Project projects = null;
		for (CSVRecord record : records) {
			int recordSize = record.size();
			// Check if the line has something in it (A group with only a space will be
			// valid)
			if (record.get(0).toString() != "" || record.size() > 1) {
				String username = record.get(0).toString();
				String group = record.get(1).toString();
				String project = record.get(2).toString();
				if (iteration == 0) {// Check the titles
					String usernameH = Headers[0].trim();
					String groupH = Headers[1].trim();
					String projectH = Headers[2].trim();
					if (!username.trim().equals(usernameH) || !group.trim().equals(groupH)
							|| !project.trim().equals(projectH)) {
						throw new IOException("Les titres ne sont pas valides.");
					}
				} else {
					String[] stat = new String[4];
					String[] stat1 = new String[4];
					String[] stat2 = new String[4];
					if (group != "" && project != "" && !project.equals(project_name) && username.trim() != "") {// If
																													// there
						// is a new
						// project
						// name
						project_id = "";// Put to void to avoid adding wrong members to a previous project
						project_name = project;
						stat[0] = "Création du projet";// Add the name of the project in the list
						stat[1] = project_name;

						// With the GitLabAPI, the user Username and password

						try {
							Project project_ = new Project();
							project_.setName(project_name);
							project_.setInitializeWithReadme(true);
							gitLabApi.getProjectApi().createProject(getGroupId(gitLabApi, group), project_);
							stat[2] = "Fini";
							stat[3] = "OK";
						} catch (GitLabApiException e) {
							// TODO Auto-generated catch block
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							stat[2] = errors.toString();
							stat[3] = "NOK";
						} catch (UnirestException e) {
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							stat[2] = "|-------UnirestException-------|" + errors.toString();
							stat[3] = "NOK";
						}

						status.add(stat);

						// Add member to the project
						String state = "";
						String ok_nok = "";
						stat1[0] = "Ajout membre au projet " + project_name;// Add the name of the project in the list
						stat1[1] = username;
						try {
							addMemberProject(username.trim(), host, token, usernameUser, passwordUser, gitLabApi,
									project_name);
							state = "Fini";
							ok_nok = "OK";
						} catch (IOException e) {
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							state = errors.toString();
							ok_nok = "NOK";
						} catch (GitLabApiException e) {
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							state = state + "|-------GitLabApiExepection-------|" + errors.toString();
							ok_nok = "NOK";
						} catch (UnirestException e) {
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							state = state + "|-------UnirestException-------|" + errors.toString();
							ok_nok = "NOK";
						}

						stat1[2] = state;
						stat1[3] = ok_nok;
						status.add(stat1);

					} else {
						if (username.trim() != "") {
							// Add members to the project
							String state = "";
							String ok_nok = "";
							stat[0] = "Ajout membre au projet " + project_name;// Add the name of the project in the
																				// list
							stat[1] = username;
							try {
								addMemberProject(username.trim(), host, token, usernameUser, passwordUser, gitLabApi,
										project_name);
								state = "Fini";
								ok_nok = "OK";
							} catch (IOException e) {
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								state = errors.toString();
								ok_nok = "NOK";
							} catch (GitLabApiException e) {
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								state = state + "|-------GitLabApiExepection-------|" + errors.toString();
								ok_nok = "NOK";
							} catch (UnirestException e) {
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								state = state + "|-------GitLabApiExepection-------|" + errors.toString();
								ok_nok = "NOK";
							}

							stat[2] = state;
							stat[3] = ok_nok;
							status.add(stat);

						} else {
							throw new IOException("Membre d'un groupe manquant");
						}
					}
				}
				iteration = iteration + 1;
			} else {
				throw new IOException("Ligne vide");
			}
			System.out.println(requestAnswers);
		}
		return status;
	}

	/**
	 * Check if the given email is associated to a GitLab account Return true if yes
	 * False if no
	 * 
	 * @param username
	 * @param host
	 * @param token
	 * @param usernameUser
	 * @param passwordUser
	 * @return
	 * @throws IOException
	 * @throws UnirestException
	 */
	public static Boolean CheckUsernameAssociated(String username, String host, String token, String usernameUser,
			String passwordUser) throws IOException, UnirestException {
		Boolean usernameAssociated = false;
		HttpResponse<JsonNode> response = null;
		if (token != null) {// With the token
			response = Unirest.get(host + "/api/v4/users" + "?username=" + username).header("PRIVATE-TOKEN", token)
					.asJson();
		} else {// With the username and password
			response = Unirest.get(host + "/api/v4/users" + "?username=" + username)
					.basicAuth(usernameUser, passwordUser).asJson();
		}
		if (response.getBody().toString().equals("[]")) {
			throw new IOException("Le nom " + username + " n'est associé é aucun compte GitLab");
		} else {
			usernameAssociated = true;
		}
		return usernameAssociated;
	}

	/**
	 * Get the id of the user associated to a given name
	 * 
	 * @param username
	 * @param host
	 * @param token
	 * @param usernameUser
	 * @param passwordUser
	 * @return
	 * @throws UnirestException
	 * @throws IOException
	 */
	public static int getMemberId(String username, String host, String token, String usernameUser, String passwordUser)
			throws UnirestException, IOException {
		int id = 0;
		HttpResponse<JsonNode> response = null;

		if (CheckUsernameAssociated(username, host, token, usernameUser, passwordUser)) {
			if (token != null) {// With the token
				response = Unirest.get(host + "/api/v4/users" + "?username=" + username).header("PRIVATE-TOKEN", token)
						.asJson();
			} else {// With the username and password
				response = Unirest.get(host + "/api/v4/users" + "?username=" + username)
						.basicAuth(usernameUser, passwordUser).asJson();
			}
			// Get the id in the response
			if (!response.getBody().toString().equals("[]")) {
				id = Integer.valueOf(response.getBody().getArray().getJSONObject(0).get("id").toString());
			}
		}
		return id;
	}

	/**
	 * Get the id of a project from the name of it
	 * 
	 * @param host
	 * @param token
	 * @param usernameUser
	 * @param passwordUser
	 * @param gitLabApi
	 * @param project_name
	 * @return //Project id
	 * @throws GitLabApiException
	 * @throws IOException
	 * @throws UnirestException
	 */
	public static int getProjectIdFromName(String host, String token, String usernameUser, String passwordUser,
			GitLabApi gitLabApi, String project_name) throws GitLabApiException, IOException, UnirestException {
		List<Project> matching_projects = gitLabApi.getProjectApi().getProjects(project_name);// Get all the matching
		// project the the name
		// provided
		String username = null;
		HttpResponse<JsonNode> response = null;
		if (token != null) {// With the token
			response = Unirest.get(host + "/api/v4/user").header("PRIVATE-TOKEN", token).asJson();
		} else {
			username = usernameUser;
		}
		if (!response.getBody().toString().equals("[]")) {
			username = response.getBody().getArray().getJSONObject(0).get("username").toString();
		}
		Project current_project = null;
		for (Project project : matching_projects) {
			if (project_name.equals(project.getName().toString())
					&& project.getCreatorId().equals(getMemberId(username, host, token, usernameUser, passwordUser))) {
				current_project = project;// Get the project matching exactly with the name
			}
		}
		if (current_project != null) {
			return current_project.getId();// Get project id
		} else {
			throw new IOException("Le projet n'as pas été trouvé");
		}
	}

	// Add a member to a project
	/**
	 * Add a member to a project
	 * 
	 * @param username
	 * @param host
	 * @param token
	 * @param usernameUser
	 * @param passwordUser
	 * @param gitLabApi
	 * @param project_name
	 * @throws IOException
	 * @throws GitLabApiException
	 * @throws UnirestException
	 */
	public static void addMemberProject(String username, String host, String token, String usernameUser,
			String passwordUser, GitLabApi gitLabApi, String project_name)
			throws IOException, GitLabApiException, UnirestException {
		int member_id = getMemberId(username, host, token, usernameUser, passwordUser);// Get member id
		int project_id = getProjectIdFromName(host, token, usernameUser, passwordUser, gitLabApi, project_name);
		gitLabApi.getProjectApi().addMember(project_id, member_id, AccessLevel.DEVELOPER);
	}

	/**
	 * Check if the group already exist return true if already exist
	 * 
	 * @param gitLabApi
	 * @param groupName
	 * @return
	 * @throws GitLabApiException
	 */
	public static boolean CheckGroupExist(GitLabApi gitLabApi, String groupName) throws GitLabApiException {
		List<Group> groupMatching = null;
		groupMatching = gitLabApi.getGroupApi().getGroups(groupName);
		if (groupMatching.size() >= 1) {// More than one group has that name
			for (Group group : groupMatching) {
				if (group.getName().equals(groupName)) {// Get the exact group
					return true;
				}
			}
			return false;// No group matched
		} else {// No groups has that name
			return false;
		}
	}

	/**
	 * Get the id of a group name
	 * 
	 * @param gitLabApi
	 * @param groupName
	 * @return
	 * @throws GitLabApiException
	 * @throws UnirestException
	 * @throws IOException
	 */
	public static int getGroupId(GitLabApi gitLabApi, String groupName)
			throws GitLabApiException, UnirestException, IOException {
		if (!CheckGroupExist(gitLabApi, groupName)) {// Group does not exist, creation of the group
			gitLabApi.getGroupApi().addGroup(groupName, groupName);// Add the group
		}
		List<Group> groupMatching = null;
		int groupId = 0;
		groupMatching = gitLabApi.getGroupApi().getGroups(groupName);
		for (Group group : groupMatching) {
			if (group.getName().equals(groupName)) {// Get the exact group
				groupId = group.getId();
			}
		}
		return groupId;
	}

	/**
	 * Add a member to a group
	 * 
	 * @param username
	 * @param host
	 * @param token
	 * @param usernameUser
	 * @param passwordUser
	 * @param gitLabApi
	 * @param groupName
	 * @throws GitLabApiException
	 * @throws UnirestException
	 * @throws IOException
	 */
	public static void addMemberToGroup(String username, String host, String token, String usernameUser,
			String passwordUser, GitLabApi gitLabApi, String groupName)
			throws GitLabApiException, UnirestException, IOException {
		if (!CheckGroupExist(gitLabApi, groupName)) {// Group does not exist, creation of the group
			gitLabApi.getGroupApi().addGroup(groupName, groupName);// Add the group
		}
		List<Group> groupMatching = null;
		int groupId = 0;
		groupMatching = gitLabApi.getGroupApi().getGroups(groupName);
		for (Group group : groupMatching) {
			if (group.getName().equals(groupName)) {// Get the exact group
				groupId = group.getId();
			}
		}

		int member_id = getMemberId(username, host, token, usernameUser, passwordUser);// Get member id
		gitLabApi.getGroupApi().addMember(gitLabApi.getGroupApi().getGroup(groupId), // Tranfer the project to the group
				member_id, AccessLevel.MAINTAINER);
	}
}