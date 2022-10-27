package api;

/**
 * @author Florian YUN
 */

public class UserAPI {
	private int id;
	private String name;

	public UserAPI(int user_id, String name_) {
		id = user_id;
		name = name_;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
