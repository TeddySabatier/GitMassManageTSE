package app;

import org.gitlab4j.api.GitLabApi;

/**
 * Class to store information about the connected user on the application
 * 
 * @author Kim-Celine FRANOT
 */
public class User {
	private String displayName; // to store the name to be displayed in the menu
	private String username; // to store username when gitlab connection
	private String password; // to store password when gitlab connection
	private String token; // to store token when application connection
	private GitLabApi gitLabApi; // to store gitlabapi instance upon login
	private Boolean app; // store true when application connection, false when gitlab connection
	private Boolean isUser; // store true when real user, false when admin
	private static User instance = null;

	public User() {
		username = null;
		password = null;
		token = null;
		app = null;
		gitLabApi = null;
		isUser = null;
		displayName = null;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public GitLabApi getGitLabApi() {
		return gitLabApi;
	}

	public void setGitLabApi(GitLabApi gitLabApi) {
		this.gitLabApi = gitLabApi;
	}

	public Boolean getApp() {
		return app;
	}

	public void setApp(Boolean app) {
		this.app = app;
	}

	public Boolean getIsUser() {
		return isUser;
	}

	public void setIsUser(Boolean user) {
		this.isUser = user;
	}

	/**
	 * Method to set all memebers to null (used when user disconnects)
	 */
	public void setEmpty() {
		this.username = null;
		this.password = null;
		this.token = null;
		this.gitLabApi = null;
		this.app = null;
		this.isUser = null;
		this.displayName = null;
	}

	/**
	 * Method to get User instance (creates one if not existant)
	 * 
	 * @return User
	 */
	public static User getInstance() {
		if (instance == null) {
			instance = new User();
		}
		return instance;
	}

}