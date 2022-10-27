package api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author Florian YUN
 * @author Kim-Celine FRANOT
 */
public class FunctionsAPI {
	/**
	 * Method to get the id of a group given its name
	 * 
	 * @param gitlabApi GitLabApi that is used
	 * @param groupname Name of the group
	 * @return id of the group
	 * @throws GitLabApiException
	 */
	public static int getGroupId(GitLabApi gitlabApi, String groupname) throws GitLabApiException {
		List<Group> matching_groups;
		// Get all groups that match the group name
		matching_groups = gitlabApi.getGroupApi().getGroups(groupname);
		Group current_module = null;
		for (Group group : matching_groups) {
			if (groupname.equals(group.getName().toString())) {
				current_module = group;// Get the group matching exactly with the name
			}
		}
		int selectedGroup_id = current_module.getId();
		return selectedGroup_id;
	}

	/**
	 * Method to get a user id given its name
	 * 
	 * @param user     Name of the user whose id needs to be found
	 * @param username Username of the user of the app
	 * @param password Password of the user of the app
	 * @return id of the user
	 * @throws UnirestException
	 */
	public static int getUserId(String user, String username, String password) throws UnirestException {
		List<UserAPI> listOfUsers;
		listOfUsers = getUsersList(username, password);
		UserAPI current_user = null;
		for (UserAPI testedUser : listOfUsers) {
			if (user.equals(testedUser.getName())) {
				current_user = testedUser;// Get the user matching exactly with the name
			}
		}
		return current_user.getId();
	}

	/**
	 * @param user  Name of the user whose id needs to be found
	 * @param token Token of the user of the app
	 * @return id of the user
	 * @throws UnirestException
	 */
	public static int getUserId(String user, String token) throws UnirestException {
		List<UserAPI> listOfUsers;
		listOfUsers = getUsersList(token);
		UserAPI current_user = null;
		for (UserAPI testedUser : listOfUsers) {
			if (user.equals(testedUser.getName())) {
				current_user = testedUser;// Get the user matching exactly with the name
			}
		}
		return current_user.getId();
	}

	/**
	 * @param username Username of the user
	 * @param password Password of the user
	 * @return List<UserAPI> containing all users info (id and name)
	 * @throws UnirestException
	 */
	public static List<UserAPI> getUsersList(String username, String password) throws UnirestException {
		String host = "https://code.telecomste.fr";
		HttpResponse<JsonNode> response = Unirest.post(host + "/oauth/token").field("grant_type", "password")
				.field("username", username).field("password", password).asJson();
		String token = response.getBody().getObject().getString("access_token");
		response = Unirest.get(host + "/api/v4/users?page=1&per_page=100").header("Authorization", "Bearer " + token)
				.asJson();
		Headers headers = response.getHeaders();
		List<String> listHeaders = headers.get("X-Total-Pages");
		int nbPages = Integer.valueOf(listHeaders.get(0));
		JSONArray jsonarray = response.getBody().getArray();
		List<UserAPI> listUsersList = new ArrayList<UserAPI>();
		jsonarray.forEach(json -> {
			UserAPI user = new UserAPI(((JSONObject) json).getInt("id"), ((JSONObject) json).getString("name"));
			listUsersList.add(user);
		});
		if (nbPages > 1) {
			for (int i = 2; i <= nbPages; i++) {
				response = Unirest.get(host + "/api/v4/users?page=" + i + "&per_page=100")
						.header("Authorization", "Bearer " + token).asJson();
				jsonarray = response.getBody().getArray();
				jsonarray.forEach(json -> {
					UserAPI user = new UserAPI(((JSONObject) json).getInt("id"), ((JSONObject) json).getString("name"));
					listUsersList.add(user);
				});
			}
		}
		return listUsersList;
	}

	/**
	 * @param token Token of the user
	 * @return List<UserAPI> containing all users info (id and name)
	 * @throws UnirestException
	 */
	public static List<UserAPI> getUsersList(String token) throws UnirestException {
		String host = "https://code.telecomste.fr";
		HttpResponse<JsonNode> response = Unirest.get(host + "/api/v4/users?page=1&per_page=100")
				.header("PRIVATE-TOKEN", token).asJson();
		Headers headers = response.getHeaders();
		List<String> listHeaders = headers.get("X-Total-Pages");
		int nbPages = Integer.valueOf(listHeaders.get(0));
		JSONArray jsonarray = response.getBody().getArray();
		List<UserAPI> listUsersList = new ArrayList<UserAPI>();
		jsonarray.forEach(json -> {
			UserAPI user = new UserAPI(((JSONObject) json).getInt("id"), ((JSONObject) json).getString("name"));
			listUsersList.add(user);
		});
		if (nbPages > 1) {
			for (int i = 2; i <= nbPages; i++) {
				response = Unirest.get(host + "/api/v4/users?page=" + i + "&per_page=100")
						.header("Authorization", "Bearer " + token).asJson();
				jsonarray = response.getBody().getArray();
				jsonarray.forEach(json -> {
					UserAPI user = new UserAPI(((JSONObject) json).getInt("id"), ((JSONObject) json).getString("name"));
					listUsersList.add(user);
				});
			}
		}
		return listUsersList;
	}

	/**
	 * Method to get all the groups owned by the user by using its token
	 * 
	 * @param token of the user
	 * @return List<Group>, list of groups owned by the user
	 * @throws UnirestException
	 */
	public static List<Group> getGroupsOwned(String token) throws UnirestException {
		String host = "https://code.telecomste.fr";
		HttpResponse<JsonNode> response = Unirest.get(host + "/api/v4/groups?owned=true&page=1&per_page=100")
				.header("PRIVATE-TOKEN", token).asJson();
		Headers headers = response.getHeaders();
		List<String> listHeaders = headers.get("X-Total-Pages");
		int nbPages = Integer.valueOf(listHeaders.get(0));
		JSONArray jsonarray = response.getBody().getArray();
		List<Group> listGroupsOwned = new ArrayList<Group>();
		jsonarray.forEach(json -> {
			Group group = new Group();
			group.setId(((JSONObject) json).getInt("id"));
			group.setName(((JSONObject) json).getString("name"));
			listGroupsOwned.add(group);
		});
		if (nbPages > 1) {
			for (int i = 2; i <= nbPages; i++) {
				response = Unirest.get(host + "/api/v4/groups?owned=true&page=" + i + "&per_page=100")
						.header("Authorization", "Bearer " + token).asJson();
				jsonarray = response.getBody().getArray();
				jsonarray.forEach(json -> {
					Group group = new Group();
					group.setId(((JSONObject) json).getInt("id"));
					group.setName(((JSONObject) json).getString("name"));
					listGroupsOwned.add(group);
				});
			}
		}
		return listGroupsOwned;
	}

	/**
	 * Method to get all the groups owned by the Gitlab user by using its username
	 * and password
	 * 
	 * @param username of the gitlab user
	 * @param password of the gitlab user
	 * @return List<Group>, list of groups owned by the gitlab user
	 * @throws UnirestException
	 */
	public static List<Group> getGroupsOwned(String username, String password) throws UnirestException {
		String host = "https://code.telecomste.fr";
		HttpResponse<JsonNode> response = Unirest.post(host + "/oauth/token").field("grant_type", "password")
				.field("username", username).field("password", password).asJson();
		String token = response.getBody().getObject().getString("access_token");
		response = Unirest.get(host + "/api/v4/groups?owned=true&page=1&per_page=100")
				.header("Authorization", "Bearer " + token).asJson();
		Headers headers = response.getHeaders();
		List<String> listHeaders = headers.get("X-Total-Pages");
		int nbPages = Integer.valueOf(listHeaders.get(0));
		JSONArray jsonarray = response.getBody().getArray();
		List<Group> listGroupsOwned = new ArrayList<Group>();
		jsonarray.forEach(json -> {
			Group group = new Group();
			group.setId(((JSONObject) json).getInt("id"));
			group.setName(((JSONObject) json).getString("name"));
			listGroupsOwned.add(group);
		});
		if (nbPages > 1) {
			for (int i = 2; i <= nbPages; i++) {
				response = Unirest.get(host + "/api/v4/groups?owned=true&page=" + i + "&per_page=100")
						.header("Authorization", "Bearer " + token).asJson();
				jsonarray = response.getBody().getArray();
				jsonarray.forEach(json -> {
					Group group = new Group();
					group.setId(((JSONObject) json).getInt("id"));
					group.setName(((JSONObject) json).getString("name"));
					listGroupsOwned.add(group);
				});
			}
		}
		return listGroupsOwned;
	}

	/**
	 * Method to get the id of an owned project
	 * 
	 * @param gitlabApi    GitLabApi that is used
	 * @param project_name Name of the project
	 * @return id of the project
	 * @throws GitLabApiException
	 */
	public static int getProjectIdFromName(GitLabApi gitlabApi, String project_name) throws GitLabApiException {
		Project current_project = null;
		for (Project project : gitlabApi.getProjectApi().getOwnedProjects()) {
			if (project_name.equals(project.getName().toString())) {
				current_project = project;
			}
		}
		return current_project.getId();// Get project id
	}

	/**
	 * Checks if the group already exists
	 * 
	 * @param gitlabApi GitLabApi that is used
	 * @param groupName Name of the group name to check
	 * @return true if already exist in Gitlab
	 * @throws GitLabApiException
	 */
	public static boolean CheckGroupExist(GitLabApi gitlabApi, String groupName) throws GitLabApiException {
		List<Group> groupMatching = null;
		groupMatching = gitlabApi.getGroupApi().getGroups(groupName);
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
	 * Returns the id of a group, given its name and creates it if it does not exist
	 * yet
	 * 
	 * @param gitlabApi GitLabApi that is used
	 * @param groupName name of the group to create or to find
	 * @return groupId id of the group named groupName
	 * @throws GitLabApiException
	 * @throws UnirestException
	 * @throws IOException
	 */
	public static int getGroupIdWithCreation(GitLabApi gitlabApi, String groupName)
			throws GitLabApiException, UnirestException, IOException {
		if (!CheckGroupExist(gitlabApi, groupName)) {// Group does not exist, creation of the group
			gitlabApi.getGroupApi().addGroup(groupName, groupName);// Add the group
		}
		List<Group> groupMatching = null;
		int groupId = 0;
		groupMatching = gitlabApi.getGroupApi().getGroups(groupName);
		for (Group group : groupMatching) {
			if (group.getName().equals(groupName)) {// Get the exact group
				groupId = group.getId();
			}
		}
		return groupId;
	}
}
