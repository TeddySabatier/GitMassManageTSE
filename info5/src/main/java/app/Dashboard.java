package app;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Member;

import com.mashape.unirest.http.exceptions.UnirestException;

import api.FunctionsAPI;
import api.UserAPI;
import bdd.DatabaseApp;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;

/**
 * @author Florian YUN
 * @author Kim-Celine FRANOT
 */
@SuppressWarnings("serial")
public class Dashboard extends JPanel implements ActionListener, KeyListener, FocusListener {

	// frame components
	private JPanel mainPanel;
	private JPanel manageModules;
	private JTabbedPane tabbedPane;
	private JPanel tabModules;
	private JScrollPane modulesPane;
	private JPanel tabGroups;
	private JScrollPane groupsPane;
	private JTextField txtSearchGroupBy;
	private JPanel manageGroups;
	private JPanel tabGroups_1;
	private JScrollPane groupsPane_1;
	private JPanel panel_buttonsGroups1;
	private JButton btnCreateGroup;
	private JButton btnDeleteGroup;
	private JTextField txtSearchGroupBy_1;
	private JPanel tabUsers;
	private JScrollPane usersPane;
	private JTextField txtSearchUserBy;
	private JPanel panel_buttonsUsers;
	private JButton btnAddUser;
	private JButton btnRemoveUser;
	private JTextField txtCreateGroupBy;
	private JPanel panel_buttonsModules;
	private JButton btnCreateModule;
	private JButton btnDeleteModule;
	private JPanel panel_buttonsGroups;
	private JButton btnAddGroup;
	private JButton btnRemoveGroup;
	private JTextField txtCreateModuleBy;
	private JList<String> list_groups1;
	private JList<String> list_users;
	private JList<String> list_members;
	private JList<String> list_modules;
	private JList<String> list_groups;
	private JList<String> list_groupsofmodule;

	static GitLabApi gitlabApi;
	static ArrayList<String> listModules = new ArrayList<String>();
	static ArrayList<String> listGroups = new ArrayList<String>();
	static ArrayList<String> listUsers = new ArrayList<String>();
	private User userInstance = User.getInstance();

	private DatabaseApp DB;

	/**
	 * Constructor
	 * 
	 * @param gitLabApi GitLabApi that is used
	 */
	public Dashboard(GitLabApi gitLabApi) {
		gitlabApi = gitLabApi;
		try {
			// Use the database that was previously used
			DB = new DatabaseApp();
			// Get list of the owned groups
			List<Group> listRealGroups;
			listRealGroups = FunctionsAPI.getGroupsOwned(userInstance.getToken());
			listRealGroups.forEach((Group group) -> listGroups.add(group.getName()));
			listGroups = new ArrayList<>(new HashSet<>(listGroups));
			// Get list of users
			List<UserAPI> listAllUsers;
			listAllUsers = FunctionsAPI.getUsersList(userInstance.getToken());
			List<String> listUsersDuplicates = new ArrayList<String>();
			listAllUsers.forEach((UserAPI user) -> listUsersDuplicates.add(user.getName()));
			listUsers = new ArrayList<>(new HashSet<>(listUsersDuplicates));
			// Update the database if needed
			DB.updateGroups(gitLabApi);
			// Get list of modules
			listModules = DB.getModule();
		} catch (Exception e) {
			e.printStackTrace();
		}
		initComponents();
	}

	/**
	 * Initialise components
	 */
	private void initComponents() {
		setLayout(null);
		mainPanel = new JPanel();
		mainPanel.setBounds(0, 0, 924, 648);
		mainPanel.setBackground(new Color(153, 204, 255));
		add(mainPanel);

		JLabel jLabel5 = new JLabel();
		jLabel5.setBounds(24, 32, 430, 44);
		jLabel5.setToolTipText("");
		jLabel5.setText("Your Dashboard Page");
		jLabel5.setForeground(new Color(41, 41, 97));
		jLabel5.setFont(new Font("Segoe UI", Font.BOLD, 24));
		mainPanel.setLayout(null);
		mainPanel.add(jLabel5);

		String[] modules = listModules.toArray(new String[listModules.size()]);
		String[] groups = listGroups.toArray(new String[listGroups.size()]);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		javax.swing.GroupLayout gl_body = new javax.swing.GroupLayout(mainPanel);
		gl_body.setHorizontalGroup(gl_body.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_body.createSequentialGroup()
						.addGroup(gl_body.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_body.createSequentialGroup().addGap(21).addComponent(jLabel5,
										GroupLayout.PREFERRED_SIZE, 430, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_body.createSequentialGroup().addGap(101).addComponent(tabbedPane,
										GroupLayout.PREFERRED_SIZE, 694, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(129, Short.MAX_VALUE)));
		gl_body.setVerticalGroup(gl_body.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_body.createSequentialGroup().addGap(25)
						.addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE).addGap(18)
						.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 527, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(34, Short.MAX_VALUE)));

		mainPanel.setLayout(gl_body);

		// First panel
		manageModules = new JPanel();
		tabbedPane.addTab("Manage modules", null, manageModules, null);
		manageModules.setLayout(null);

		// Modules panel
		tabModules = new JPanel();
		tabModules.setLocation(10, 10);
		tabModules.setSize(322, 266);
		tabModules.setLayout(null);
		manageModules.add(tabModules);
		// Modules scroll pane
		modulesPane = new JScrollPane();
		modulesPane.setBounds(10, 3, 302, 167);
		tabModules.add(modulesPane);

		// Groups panel
		tabGroups = new JPanel();
		tabGroups.setSize(322, 266);
		tabGroups.setLocation(342, 10);
		tabGroups.setLayout(null);
		manageModules.add(tabGroups);
		// Groups scroll pane
		groupsPane = new JScrollPane();
		groupsPane.setBounds(10, 3, 302, 167);
		tabGroups.add(groupsPane);

		// Groups in the modules scroll pane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 286, 654, 204);
		manageModules.add(scrollPane);

		// ListModels for the JLists
		DefaultListModel<String> modelgroups = new DefaultListModel<>();
		listGroups.forEach((String group) -> modelgroups.addElement(group));
		DefaultListModel<String> modelgroupsofmodule = new DefaultListModel<>();

		// JLists
		list_modules = new JList<String>(modules);
		list_groups = new JList<String>(modelgroups);
		list_groupsofmodule = new JList<String>(modelgroupsofmodule);

		// Listener for when a module is selected
		list_modules.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// In case a group is selected show the users associated in membersPane and the
				// ones who are not in usersPane
				String selectedModule = (String) list_modules.getSelectedValue();
				if (selectedModule != null) {
					/// Update the panels
					// Get the groups of the module
					ArrayList<String> listMembers = DB.getListGroupsOfModules(selectedModule);// List of all the
																								// members of
					// the group
					DefaultListModel<String> membersModel = new DefaultListModel<>();
					listMembers.forEach((String user) -> membersModel.addElement(user));
					list_groupsofmodule.setModel(membersModel);

					// Get the groups who are not in the module
					ArrayList<String> listNotMembers = DB.getListNotGroupsOfModules(selectedModule);
					DefaultListModel<String> notMembersModel = new DefaultListModel<>();
					listNotMembers.forEach((String user) -> notMembersModel.addElement(user));
					list_groups.setModel(notMembersModel);

				}
			}
		});

		// List of modules
		modulesPane.setViewportView(list_modules);
		list_modules.setBounds(10, 5, 267, 128);
		tabModules.add(modulesPane);

		// List of groups
		groupsPane.setViewportView(list_groups);
		list_groups.setBounds(10, 5, 267, 128);
		tabGroups.add(groupsPane);

		// List of the groups in the module
		scrollPane.setViewportView(list_groupsofmodule);
		list_groupsofmodule.setBounds(0, 0, 1, 1);
		manageModules.add(scrollPane);

		// JTextField to search a group
		txtSearchGroupBy = new JTextField();
		txtSearchGroupBy.addKeyListener(this);
		txtSearchGroupBy.addFocusListener(this);
		txtSearchGroupBy.setText("Search Group by name...");
		txtSearchGroupBy.setColumns(10);
		txtSearchGroupBy.setBounds(10, 180, 302, 19);
		tabGroups.add(txtSearchGroupBy);

		// Panel of buttons on the groups side
		panel_buttonsGroups = new JPanel();
		panel_buttonsGroups.setBounds(10, 209, 302, 48);
		tabGroups.add(panel_buttonsGroups);
		panel_buttonsGroups.setLayout(new GridLayout(0, 2, 0, 0));

		// Button to add group
		btnAddGroup = new JButton("Add Group");
		btnAddGroup.addActionListener(this);
		panel_buttonsGroups.add(btnAddGroup);

		// Button to remove a group
		btnRemoveGroup = new JButton("Remove Group");
		btnRemoveGroup.addActionListener(this);
		panel_buttonsGroups.add(btnRemoveGroup);

		// JTextField to create a module
		txtCreateModuleBy = new JTextField();
		txtCreateModuleBy.setText("Create Module by name...");
		txtCreateModuleBy.setBounds(10, 180, 302, 19);
		tabModules.add(txtCreateModuleBy);
		txtCreateModuleBy.setColumns(10);
		txtCreateModuleBy.addFocusListener(this);

		// Panel of buttons on the modules side
		panel_buttonsModules = new JPanel();
		panel_buttonsModules.setBounds(10, 209, 302, 48);
		tabModules.add(panel_buttonsModules);
		panel_buttonsModules.setLayout(new GridLayout(0, 2, 0, 0));

		// Button to create a module
		btnCreateModule = new JButton("Create Module");
		btnCreateModule.addActionListener(this);
		panel_buttonsModules.add(btnCreateModule);

		// Button to delete a module
		btnDeleteModule = new JButton("Delete Module");
		btnDeleteModule.addActionListener(this);
		panel_buttonsModules.add(btnDeleteModule);

		// Second panel
		manageGroups = new JPanel();
		manageGroups.setLayout(null);
		tabbedPane.addTab("Manage Groups", null, manageGroups, null);

		// Groups Panel
		tabGroups_1 = new JPanel();
		tabGroups_1.setLayout(null);
		tabGroups_1.setBounds(10, 10, 322, 292);
		manageGroups.add(tabGroups_1);

		// Groups Scroll Pane
		groupsPane_1 = new JScrollPane();
		groupsPane_1.setBounds(10, 3, 302, 167);
		tabGroups_1.add(groupsPane_1);

		// Users Panel
		tabUsers = new JPanel();
		tabUsers.setLayout(null);
		tabUsers.setBounds(342, 10, 322, 292);
		manageGroups.add(tabUsers);

		// Users Scroll Pane
		usersPane = new JScrollPane();
		usersPane.setBounds(10, 3, 302, 167);
		tabUsers.add(usersPane);

		// Groups of module Pane
		JScrollPane membersPane = new JScrollPane();
		membersPane.setBounds(10, 312, 654, 178);
		manageGroups.add(membersPane);

		DefaultListModel<String> model = new DefaultListModel<>();
		listUsers.forEach((String user) -> model.addElement(user));
		DefaultListModel<String> membersModel = new DefaultListModel<>();

		list_groups1 = new JList<String>(groups);
		list_users = new JList<String>(model);
		list_members = new JList<String>(membersModel);

		list_groups1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				// In case a group is selected show the users associated in membersPane and the
				// ones who are not in usersPane
				String selectedGroup = (String) list_groups1.getSelectedValue();
				if (selectedGroup != null) {
					try {
						int selectedGroup_id = FunctionsAPI.getGroupId(gitlabApi, selectedGroup);// Id of the selected
																									// group
						// Get the members of the group
						List<Member> membersofSelectedGroup = gitlabApi.getGroupApi().getAllMembers(selectedGroup_id);
						ArrayList<String> listMembers = new ArrayList<String>();// List of all the members of the group
						membersofSelectedGroup.forEach((Member member) -> listMembers.add(member.getName()));
						DefaultListModel<String> membersModel = new DefaultListModel<>();
						listMembers.forEach((String user) -> membersModel.addElement(user));
						list_members.setModel(membersModel);

						// Get the users who are not in the group
						List<String> listNotMembers = new ArrayList<>(listUsers);// List of all the users not in the
																					// group
						listNotMembers.removeAll(listMembers);
						DefaultListModel<String> notMembersModel = new DefaultListModel<>();
						listNotMembers.forEach((String user) -> notMembersModel.addElement(user));
						list_users.setModel(notMembersModel);

					} catch (GitLabApiException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		groupsPane_1.setViewportView(list_groups1);
		list_groups1.setBounds(0, 0, 1, 1);
		tabGroups_1.add(groupsPane_1);

		usersPane.setViewportView(list_users);
		list_users.setBounds(0, 0, 1, 1);
		tabUsers.add(usersPane);

		membersPane.setViewportView(list_members);
		list_members.setBounds(0, 0, 1, 1);
		manageGroups.add(membersPane);

		panel_buttonsGroups1 = new JPanel();
		panel_buttonsGroups1.setBounds(10, 236, 302, 48);
		tabGroups_1.add(panel_buttonsGroups1);
		panel_buttonsGroups1.setLayout(new GridLayout(0, 2, 0, 0));

		btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.addActionListener(this);
		panel_buttonsGroups1.add(btnCreateGroup);

		btnDeleteGroup = new JButton("Delete Group");
		btnDeleteGroup.addActionListener(this);
		panel_buttonsGroups1.add(btnDeleteGroup);

		txtSearchGroupBy_1 = new JTextField();
		txtSearchGroupBy_1.addKeyListener(this);
		txtSearchGroupBy_1.addFocusListener(this);
		txtSearchGroupBy_1.setText("Search Group by name...");
		txtSearchGroupBy_1.setColumns(10);
		txtSearchGroupBy_1.setBounds(10, 178, 302, 19);
		tabGroups_1.add(txtSearchGroupBy_1);

		txtCreateGroupBy = new JTextField();
		txtCreateGroupBy.addFocusListener(this);
		txtCreateGroupBy.setText("Create Group by name...");
		txtCreateGroupBy.setBounds(10, 207, 302, 19);
		tabGroups_1.add(txtCreateGroupBy);
		txtCreateGroupBy.setColumns(10);

		txtSearchUserBy = new JTextField();
		txtSearchUserBy.addFocusListener(this);
		txtSearchUserBy.addKeyListener(this);
		txtSearchUserBy.setText("Search User by name...");
		txtSearchUserBy.setColumns(10);
		txtSearchUserBy.setBounds(10, 179, 302, 19);
		tabUsers.add(txtSearchUserBy);

		panel_buttonsUsers = new JPanel();
		panel_buttonsUsers.setBounds(10, 236, 302, 48);
		tabUsers.add(panel_buttonsUsers);
		panel_buttonsUsers.setLayout(new GridLayout(0, 2, 0, 0));

		btnAddUser = new JButton("Add User");
		btnAddUser.addActionListener(this);
		panel_buttonsUsers.add(btnAddUser);

		btnRemoveUser = new JButton("Remove User");
		btnRemoveUser.addActionListener(this);
		panel_buttonsUsers.add(btnRemoveUser);
	}

	/**
	 * Action when a button is clicked on
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAddGroup) {
			addGroupAction();
		} else if (e.getSource() == btnRemoveGroup) {
			removeGroupAction();
		} else if (e.getSource() == btnAddUser) {
			addUserAction();
		} else if (e.getSource() == btnRemoveUser) {
			removeUserAction();
		} else if (e.getSource() == btnCreateGroup) {
			createGroupAction();
		} else if (e.getSource() == btnCreateModule) {
			createModuleAction();
		} else if (e.getSource() == btnDeleteGroup) {
			deleteGroupAction();
		} else if (e.getSource() == btnDeleteModule) {
			deleteModuleAction();
		}
	}

	/**
	 * Method to add a Group, used by the Add Group button
	 */
	public void addGroupAction() {
		List<String> selectedGroups = list_groups.getSelectedValuesList();
		String selectedModule = (String) list_modules.getSelectedValue();
		if (selectedModule != null) {
			selectedGroups.forEach((String selectedGroup) -> {
				DB.addGroupToModule(selectedModule, selectedGroup);
			});
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			updatePanels();
		}
	}

	/**
	 * Method to remove a Group, used by the Remove Group button
	 */
	public void removeGroupAction() {
		List<String> selectedGroups = list_groupsofmodule.getSelectedValuesList();
		String selectedModule = (String) list_modules.getSelectedValue();
		if ((selectedModule != null) && (!selectedGroups.isEmpty())) {
			selectedGroups.forEach((String selectedGroup) -> {
				DB.removeGroup(selectedModule, selectedGroup);
			});
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			updatePanels();
		}
	}

	/**
	 * Method to add a User, used by the Add User button
	 */
	public void addUserAction() {
		String selectedGroup = (String) list_groups1.getSelectedValue();
		List<String> selectedUsers = list_users.getSelectedValuesList();
		if (selectedGroup != null) {
			try {
				// Add the member in Gitlab
				int selectedGroupId = FunctionsAPI.getGroupId(gitlabApi, selectedGroup);
				selectedUsers.forEach((String selectedUser) -> {
					try {
						int selectedUserId = FunctionsAPI.getUserId(selectedUser, userInstance.getToken());
						gitlabApi.getGroupApi().addMember(selectedGroupId, selectedUserId, 30);
					} catch (UnirestException | GitLabApiException e1) {
						e1.printStackTrace();
					}
				});
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				updatePanels();
			} catch (GitLabApiException e1) {
				e1.printStackTrace();
			}

		}
	}

	/**
	 * Method to remove a User, used by the Remove User button
	 */
	public void removeUserAction() {
		String selectedGroup = (String) list_groups1.getSelectedValue();
		List<String> selectedUsers = list_members.getSelectedValuesList();
		if ((selectedGroup != null) && (!selectedUsers.isEmpty())) {
			selectedUsers.forEach((String selectedUser) -> {
				try {
					int selectedGroupId = FunctionsAPI.getGroupId(gitlabApi, selectedGroup);
					int selectedUserId = FunctionsAPI.getUserId(selectedUser, userInstance.getToken());
					gitlabApi.getGroupApi().removeMember(selectedGroupId, selectedUserId);
				} catch (UnirestException | GitLabApiException e2) {
					e2.printStackTrace();
				}
			});
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			updatePanels();
		}
	}

	/**
	 * Method to Create a Group, used by the Create Group button
	 */
	public void createGroupAction() {
		String createdGroup = txtCreateGroupBy.getText();
		try {
			gitlabApi.getGroupApi().addGroup(createdGroup, createdGroup);
			listGroups.add(createdGroup);
			DB.createGroup(createdGroup, gitlabApi);
			DefaultListModel<String> modelGroups = new DefaultListModel<>();
			JOptionPane.showMessageDialog(mainPanel, "The group " + createdGroup + " has been created",
					"Group Creation", JOptionPane.INFORMATION_MESSAGE);
			listGroups.forEach((String user) -> modelGroups.addElement(user));
			list_groups.setModel(modelGroups);
			list_groups1.setModel(modelGroups);
			txtCreateGroupBy.setText("Create Group by name");
		} catch (GitLabApiException e1) {
			JOptionPane.showMessageDialog(mainPanel, "Group name already taken", "Group Creation Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Method to create a module, used by the Create Module button
	 */
	public void createModuleAction() {
		String createdModule = txtCreateModuleBy.getText();
		DB.createModule(createdModule);
		listModules.add(createdModule);
		DefaultListModel<String> modelGroups = new DefaultListModel<>();
		listModules.forEach((String user) -> modelGroups.addElement(user));
		list_modules.setModel(modelGroups);
	}

	/**
	 * Method to delete a Group, used by the Delete Group button
	 */
	public void deleteGroupAction() {
		List<String> selectedGroups = list_groups1.getSelectedValuesList();
		if (!selectedGroups.isEmpty()) {
			selectedGroups.forEach((String selectedGroup) -> {
				try {
					gitlabApi.getGroupApi().deleteGroup(selectedGroup);
				} catch (GitLabApiException e1) {
					e1.printStackTrace();
				}
				DB.deleteGroup(selectedGroup);
				listGroups.remove(selectedGroup);
			});
			DefaultListModel<String> modelGroups = new DefaultListModel<>();
			listGroups.forEach((String user) -> modelGroups.addElement(user));
			list_groups1.setModel(modelGroups);
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			updatePanels();

		}
	}

	/**
	 * Method to delete a module, used by the Delete Module button
	 */
	public void deleteModuleAction() {
		List<String> selectedModules = list_modules.getSelectedValuesList();
		selectedModules.forEach((String selectedModule) -> {
			DB.deleteModule(selectedModule);
			listModules.remove(selectedModule);
			DefaultListModel<String> modelGroups = new DefaultListModel<>();
			listModules.forEach((String user) -> modelGroups.addElement(user));
			list_modules.setModel(modelGroups);
		});
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		updatePanels();
	}

	/**
	 * Update the panels
	 */
	public void updatePanels() {
		// Update the database
		try {
			DB.updateGroups(gitlabApi);
		} catch (GitLabApiException | UnirestException e2) {
			e2.printStackTrace();
		}
		// First panel
		DefaultListModel<String> modelModule = new DefaultListModel<>();
		listModules.forEach((String module) -> modelModule.addElement(module));
		list_modules.setModel(modelModule);
		String selectedModule = (String) list_modules.getSelectedValue();
		// Case 1: No module is selected
		// Update the group panel only
		List<Group> listRealGroups = null;
		try {
			listRealGroups = FunctionsAPI.getGroupsOwned(userInstance.getToken());
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		listRealGroups.forEach((Group group) -> listGroups.add(group.getName()));
		listGroups = new ArrayList<>(new HashSet<>(listGroups));
		DefaultListModel<String> modelgroups = new DefaultListModel<>();
		listGroups.forEach((String group) -> modelgroups.addElement(group));
		list_groups.setModel(modelgroups);
		list_groupsofmodule.setModel(new DefaultListModel<>());

		// Case 2: A module is selected
		// Update the groups of the modules
		if (selectedModule != null) {
			/// Update the panels
			// Get the groups of the module
			ArrayList<String> listMembers = DB.getListGroupsOfModules(selectedModule);// List of all the
																						// members of
			// the group
			DefaultListModel<String> membersModel = new DefaultListModel<>();
			listMembers.forEach((String user) -> membersModel.addElement(user));
			list_groupsofmodule.setModel(membersModel);

			// Get the groups who are not in the module
			ArrayList<String> listNotMembers = DB.getListNotGroupsOfModules(selectedModule);
			DefaultListModel<String> notMembersModel = new DefaultListModel<>();
			listNotMembers.forEach((String user) -> notMembersModel.addElement(user));
			list_groups.setModel(notMembersModel);
		}

		// Second panel
		String selectedGroup = (String) list_groups1.getSelectedValue();
		// Case 1: No group is selected
		// Update the group panel only
		list_groups1.setModel(modelgroups);
		list_members.setModel(new DefaultListModel<>());

		// Case 1: No group is selected
		// Update the members panel
		if (selectedGroup != null) {
			try {
				int selectedGroupId = FunctionsAPI.getGroupId(gitlabApi, selectedGroup);
				// Update the members panels
				// Get the members of the group
				List<Member> membersofSelectedGroup = gitlabApi.getGroupApi().getAllMembers(selectedGroupId);
				ArrayList<String> listMembers = new ArrayList<String>();// List of all the members of the group
				membersofSelectedGroup.forEach((Member member) -> listMembers.add(member.getName()));
				DefaultListModel<String> membersModel = new DefaultListModel<>();
				listMembers.forEach((String user) -> membersModel.addElement(user));
				list_members.setModel(membersModel);

				// Get the users who are not in the group
				ArrayList<String> listNotMembers = new ArrayList<String>(listUsers);// List of all the users not in the
																					// group
				listNotMembers.removeAll(listMembers);
				DefaultListModel<String> notMembersModel = new DefaultListModel<>();
				listNotMembers.forEach((String user) -> notMembersModel.addElement(user));
				list_users.setModel(notMembersModel);

			} catch (GitLabApiException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * Changes the display of the lists when something is written in the Search
	 * fields
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (txtSearchGroupBy.hasFocus()) {
			String groupSearched = txtSearchGroupBy.getText();
			ArrayList<String> listCorrespondingGroups = new ArrayList<String>();// List of all the users who
																				// correspond to the search
			listGroups.forEach((String group) -> {
				if (group.contains(groupSearched)) {
					listCorrespondingGroups.add(group);
				}
			});
			DefaultListModel<String> groupsModel = new DefaultListModel<>();
			listCorrespondingGroups.forEach((String group) -> groupsModel.addElement(group));
			list_groups.setModel(groupsModel);
		} else if (txtSearchGroupBy_1.hasFocus()) {
			String groupSearched = txtSearchGroupBy_1.getText();
			ArrayList<String> listCorrespondingGroups = new ArrayList<String>();// List of all the users who
																				// correspond to the search
			listGroups.forEach((String group) -> {
				if (group.toLowerCase().contains(groupSearched.toLowerCase())) {
					listCorrespondingGroups.add(group);
				}
			});
			DefaultListModel<String> groupsModel = new DefaultListModel<>();
			listCorrespondingGroups.forEach((String group) -> groupsModel.addElement(group));
			list_groups1.setModel(groupsModel);
		} else if (txtSearchUserBy.hasFocus()) {
			String userSearched = txtSearchUserBy.getText();
			ArrayList<String> listCorrespondingUsers = new ArrayList<String>();// List of all the users who
																				// correspond to the search
			listUsers.forEach((String user) -> {
				if (user.toLowerCase().contains(userSearched.toLowerCase())) {
					listCorrespondingUsers.add(user);
				}
			});
			DefaultListModel<String> notMembersModel = new DefaultListModel<>();
			listCorrespondingUsers.forEach((String user) -> notMembersModel.addElement(user));
			list_users.setModel(notMembersModel);
		}
	}

	/**
	 * Removes the previous text of a JTextField when it gains focus
	 */
	@Override
	public void focusGained(FocusEvent e) {
		JTextField src = (JTextField) e.getSource();
		src.setText("");
	}

	/**
	 * Set the search text fields to their default values when focus is lost
	 */
	@Override
	public void focusLost(FocusEvent e) {
		if (e.getSource() == txtSearchGroupBy || e.getSource() == txtSearchGroupBy_1) {
			JTextField src = (JTextField) e.getSource();
			src.setText("Search Group by name...");
		} else if (e.getSource() == txtSearchUserBy) {
			txtSearchUserBy.setText("Search User by name...");
		}
	}
}
