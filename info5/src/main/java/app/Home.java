package app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.gitlab4j.api.Constants;
import org.gitlab4j.api.Constants.ProjectOrderBy;
import org.gitlab4j.api.Constants.SortOrder;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.GroupProjectsFilter;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Visibility;

import com.mashape.unirest.http.exceptions.UnirestException;

import api.FunctionsAPI;
import api.ImportCSV;
import bdd.DatabaseApp;

/**
 * @author Kim-Celine FRANOT
 * @author Thomas VAN WYNENDAELE
 * @author Teddy SABATIER
 *
 */
@SuppressWarnings("serial")
public class Home extends JPanel implements ActionListener, FocusListener, KeyListener, MouseListener {

	// components of the panel
	private JPanel mainPanel;
	private JPanel ProjectsPanel;
	private JPanel panelManageProjects;
	private JPanel panelArchivedProjects;
	private JScrollPane scrollPaneArboGit;
	private JScrollPane scrollPaneSearchProjects;
	private JLabel lblImportCSV;
	private JLabel lblGitlabArborescence;
	private JLabel lblManageProjects;
	private JTextField textFieldProject;
	private JTextField txtGroup;
	private JTextField JSearchProject;
	private JTextField textFieldModule;
	private JButton btnImport;
	private JButton btnArchive;
	private JButton btnDelete;
	private JButton btnAdd;
	private JFileChooser jfc;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode gitRoot;
	private DefaultMutableTreeNode gitProjects;
	private DefaultMutableTreeNode gitGroups;
	private DefaultMutableTreeNode gitModules;
	private JList<String> listAlreadyArchived;
	private static JList<String> listProjects;
	private static JList<String> listModules;
	private DefaultListModel<String> modelAlreadyArchived;
	private DefaultListModel<String> modelToArchive;

	// variables
	static GitLabApi gitlabApi;
	static List<Project> projectlist;
	static List<Project> archivedlist;
	static List<Group> grouplist;
	static ArrayList<String> projects;
	static ArrayList<String> archived;
	static ArrayList<String> listGroups = new ArrayList<String>();
	static ArrayList<String> moduleListString = new ArrayList<String>();
	private static DatabaseApp DB;
	private static User userInstance = User.getInstance();

	/**
	 * Create the panel.
	 */
	public Home(GitLabApi gitLabApi) {
		setVisible(true);
		gitlabApi = gitLabApi;
		projects = new ArrayList<String>();
		archived = new ArrayList<String>();

		// get the list of projects unarchived and archived, and the groups
		try {
			projectlist = gitlabApi.getProjectApi().getProjects(false, null, Constants.ProjectOrderBy.NAME,
					Constants.SortOrder.ASC, null, false, true, false, false, true);
			projectlist.forEach((Project p) -> projects.add(p.getName()));
			archivedlist = gitlabApi.getProjectApi().getProjects(true, null, Constants.ProjectOrderBy.NAME,
					Constants.SortOrder.ASC, null, false, true, false, false, true);
			archivedlist.forEach((Project p) -> archived.add(p.getName()));
			if (userInstance.getToken() == null) {
				grouplist = FunctionsAPI.getGroupsOwned(userInstance.getUsername(), userInstance.getPassword());
			} else {
				grouplist = FunctionsAPI.getGroupsOwned(userInstance.getToken());
			}
		} catch (GitLabApiException e) {
			e.printStackTrace();
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		try {
			DB = new DatabaseApp();
		} catch (Exception e) {
			e.printStackTrace();
		}

		initComponents();
	}

	private void initComponents() {
		setLayout(null);

		mainPanel = new JPanel();
		mainPanel.setBackground(new Color(153, 204, 255));
		mainPanel.setBounds(0, 0, 884, 634);
		add(mainPanel);

		// CSV Import
		lblImportCSV = new JLabel();
		lblImportCSV.setBounds(10, 10, 130, 44);
		lblImportCSV.setToolTipText("");
		lblImportCSV.setText("CSV Import");
		lblImportCSV.setForeground(new Color(41, 41, 97));
		lblImportCSV.setFont(new Font("Segoe UI", Font.BOLD, 22));

		jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setBounds(100, 100, 450, 300);
		jfc.setVisible(false);

		btnImport = new JButton("Import");
		btnImport.setBounds(150, 20, 91, 25);
		btnImport.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnImport.setForeground(new java.awt.Color(41, 41, 97));
		btnImport.addActionListener(this);

		// GitLab Tree initialisation
		lblGitlabArborescence = new JLabel();
		lblGitlabArborescence.setBounds(10, 65, 244, 33);
		lblGitlabArborescence.setToolTipText("");
		lblGitlabArborescence.setText("Gitlab Tree");
		lblGitlabArborescence.setForeground(new Color(41, 41, 97));
		lblGitlabArborescence.setFont(new Font("Segoe UI", Font.BOLD, 22));

		treeModel = new DefaultTreeModel(gitRoot);
		tree = new JTree(treeModel);
		tree.setBackground(null);
		scrollPaneArboGit = new JScrollPane();
		scrollPaneArboGit.setBounds(10, 103, 233, 400);
		scrollPaneArboGit.setViewportView(tree);
		generateTree();

		// Project Management
		ProjectsPanel = new JPanel();
		ProjectsPanel.setBounds(270, 103, 586, 400);
		ProjectsPanel.setLayout(null);

		scrollPaneSearchProjects = new JScrollPane();
		scrollPaneSearchProjects.setBounds(318, 11, 258, 378);
		ProjectsPanel.add(scrollPaneSearchProjects);

		JSearchProject = new JTextField();
		JSearchProject.setText("Search projects");
		JSearchProject.setColumns(10);
		scrollPaneSearchProjects.setColumnHeaderView(JSearchProject);

		listProjects = new JList<String>();
		scrollPaneSearchProjects.setViewportView(listProjects);

		panelManageProjects = new JPanel();
		panelManageProjects.setBorder(new LineBorder(new Color(0, 0, 0)));
		panelManageProjects.setBounds(10, 66, 279, 263);
		panelManageProjects.setLayout(null);
		ProjectsPanel.add(panelManageProjects);

		btnDelete = new JButton("Delete projects");
		btnDelete.setBounds(10, 96, 258, 35);
		btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnDelete.addActionListener(this);
		panelManageProjects.add(btnDelete);

		textFieldProject = new JTextField();
		textFieldProject.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textFieldProject.setMargin(new Insets(0, 5, 0, 0));
		textFieldProject.setBounds(10, 142, 258, 30);
		textFieldProject.setText("Name of the project");
		textFieldProject.setColumns(10);
		panelManageProjects.add(textFieldProject);

		txtGroup = new JTextField();
		txtGroup.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtGroup.setMargin(new Insets(0, 5, 0, 0));
		txtGroup.setBounds(10, 176, 258, 30);
		txtGroup.setToolTipText("If the group is empty, project will be added at the root");
		txtGroup.setText("Group of the project");
		txtGroup.setColumns(10);
		panelManageProjects.add(txtGroup);

		btnAdd = new JButton("Add");
		btnAdd.setBounds(10, 217, 258, 35);
		btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnAdd.addActionListener(this);
		panelManageProjects.add(btnAdd);

		lblManageProjects = new JLabel();
		lblManageProjects.setHorizontalAlignment(SwingConstants.CENTER);
		lblManageProjects.setText("Manage projects");
		lblManageProjects.setForeground(Color.BLACK);
		lblManageProjects.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblManageProjects.setBounds(10, 0, 258, 40);
		panelManageProjects.add(lblManageProjects);

		String[] projectNames = projects.toArray(new String[projects.size()]);
		modelToArchive = new DefaultListModel<String>();
		for (int i = 0, n = projectNames.length; i < n; i++) {
			modelToArchive.addElement(projectNames[i]);
		}

		if (userInstance.getApp()) {
			// Archive projects initialisation
			panelArchivedProjects = new JPanel();
			panelArchivedProjects.setBounds(10, 456, 233, 156);
			panelArchivedProjects.setLayout(null);

			JLabel lblProjetsArchive = new JLabel("Archived Projects");
			lblProjetsArchive.setBounds(10, 424, 210, 32);
			lblProjetsArchive.setFont(new Font("Segoe UI", Font.BOLD, 22));
			lblProjetsArchive.setForeground(new Color(41, 41, 97));

			String[] archivedNames = archived.toArray(new String[archived.size()]);
			modelAlreadyArchived = new DefaultListModel<String>();
			for (int i = 0, n = archivedNames.length; i < n; i++) {
				modelAlreadyArchived.addElement(archivedNames[i]);
			}
			listAlreadyArchived = new JList<String>(modelAlreadyArchived);
			listAlreadyArchived.setBounds(10, 10, 213, 135);
			panelArchivedProjects.add(listAlreadyArchived);

			mainPanel.add(panelArchivedProjects);
			mainPanel.add(lblProjetsArchive);

			// Modules Scroll Pane
			JScrollPane scrollPaneSearchModules = new JScrollPane();
			scrollPaneSearchModules.setBounds(10, 262, 270, 263);
			ProjectsPanel.add(scrollPaneSearchModules);

			textFieldModule = new JTextField();
			textFieldModule.setText("Search module");
			textFieldModule.setColumns(10);
			textFieldModule.addFocusListener(this);
			textFieldModule.addKeyListener(this);
			scrollPaneSearchModules.setColumnHeaderView(textFieldModule);

			listModules = new JList<String>();
			listModules.addMouseListener(this);
			scrollPaneSearchModules.setViewportView(listModules);

			// Archive button in Project Management Panel
			btnArchive = new JButton("Archive");
			btnArchive.setBounds(309, 60, 236, 30);
			panelManageProjects.add(btnArchive);
			btnArchive.setFont(new Font("Segoe UI", Font.BOLD, 18));
			btnArchive.addActionListener(this);

			// Change Bounds of elements
			ProjectsPanel.setBounds(270, 65, 586, 547);
			panelManageProjects.setBounds(10, 26, 566, 205);
			scrollPaneSearchProjects.setBounds(307, 262, 270, 263);
			scrollPaneArboGit.setBounds(10, 103, 233, 310);
			btnDelete.setBounds(309, 101, 236, 30);
			textFieldProject.setBounds(21, 62, 258, 30);
			txtGroup.setBounds(21, 103, 258, 30);
			btnAdd.setBounds(21, 146, 258, 30);
			lblManageProjects.setBounds(10, 0, 546, 40);
		}

		mainPanel.setLayout(null);
		mainPanel.add(lblImportCSV);
		mainPanel.add(btnImport);
		mainPanel.add(lblGitlabArborescence);
		mainPanel.add(scrollPaneArboGit);
		mainPanel.add(ProjectsPanel);

		// Select the whole text of the field
		textFieldProject.addFocusListener(this);
		txtGroup.addFocusListener(this);
		JSearchProject.addFocusListener(this);
		// Go on the next field or button on enter pressed
		textFieldProject.addKeyListener(this);
		txtGroup.addKeyListener(this);
		// Search for projects
		JSearchProject.addKeyListener(this);

		refreshProjects();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnArchive) {
			archiveAction();
			refreshAction();
			btnArchive.getParent().requestFocus();

		} else if (e.getSource() == btnImport) {
			importAction();
			btnImport.getParent().requestFocus();

		} else if (e.getSource() == btnAdd) {
			addAction();
			btnAdd.getParent().requestFocus();

		} else if (e.getSource() == btnDelete) {
			deleteAction();
			btnDelete.getParent().requestFocus();
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		JTextField src = (JTextField) e.getSource();
		src.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
	}

	/**
	 * Method to trigger button import
	 */
	public void importAction() {
		jfc.setVisible(true);
		int returnValue = jfc.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			// ArrayList that contains all the informations
			ArrayList<String[]> status = new ArrayList<String[]>();
			// (PathOfCSV,GitLabAPI,host(code.telecom.tse by default),token for api if
			// connection by token)
			User userInstance = User.getInstance();
			String token = userInstance.getToken();
			String usernameUser = userInstance.getUsername();
			String passwordUser = userInstance.getPassword();
			JOptionPane infoImport = new JOptionPane();
			infoImport.showMessageDialog(null, "Import is going to launch don't close the app");
			status = ImportCSV.ImportProjectsMembers(selectedFile.getAbsolutePath(), gitlabApi, token, usernameUser,
					passwordUser, null);
			ImportCSVInfo importWindow = new ImportCSVInfo(status, this);
			refreshAction();
		}
	}

	/**
	 * Method to archive the projects on Gitlab
	 * 
	 */
	public void archiveAction() {
		// get index of each project selected in the list
		int[] selectedIx = listProjects.getSelectedIndices();

		// for each project, we call the function to archive it
		for (int i = 0; i < selectedIx.length; i++) {
			Object pr = listProjects.getModel().getElementAt(selectedIx[i]);
			try {
				Project current_project = null;
				for (Project project : gitlabApi.getProjectApi().getOwnedProjects()) {
					if (pr.toString().equals(project.getName().toString())) {
						current_project = project;
					}
				}
				int idProject = current_project.getId();
				Project archived = gitlabApi.getProjectApi().archiveProject(idProject);
			} catch (GitLabApiException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to add the project on Gitlab
	 * 
	 */
	public void addAction() {
		boolean finished = false;
		try {
			if (txtGroup.getText().equals("")) {// If no group provided
				Project project = new Project();
				project.setName(textFieldProject.getText());
				project.setInitializeWithReadme(true);
				gitlabApi.getProjectApi().createProject(project);

			} else {
				Project project = new Project();
				project.setName(textFieldProject.getText());
				project.setInitializeWithReadme(true);
				gitlabApi.getProjectApi()
						.createProject(FunctionsAPI.getGroupIdWithCreation(gitlabApi, txtGroup.getText()), project);
			}
			finished = true;// Everything went well
		} catch (GitLabApiException | UnirestException | IOException e1) {
			e1.printStackTrace();
		}

		if (finished) {
			JOptionPane.showMessageDialog(null, "Project added");
			refreshAction();
			textFieldProject.requestFocus();

		} else {
			JOptionPane.showMessageDialog(null, "Error while adding project");
			textFieldProject.requestFocus();

		}
	}

	/**
	 * Method to delete the project(s) on Gitlab
	 * 
	 */
	public void deleteAction() {
		JOptionPane infoDelete = new JOptionPane();
		infoDelete.showMessageDialog(null, "Delete is going to launch don't close the app");
		boolean finished = false;
		for (Object objectSelected : listProjects.getSelectedValues()) {
			boolean stepDone = false;

			int idProject = 0;
			try {
				idProject = FunctionsAPI.getProjectIdFromName(gitlabApi, objectSelected.toString());
				stepDone = true;
			} catch (GitLabApiException e1) {
				e1.printStackTrace();
			}
			try {
				if (stepDone) {// Only if the project selected exist and we got its id
					gitlabApi.getProjectApi().deleteProject(idProject);
					finished = true;
				}
			} catch (GitLabApiException e1) {
				e1.printStackTrace();
			}
		}
		if (finished) {
			JOptionPane.showMessageDialog(null, "Deletion done");
			refreshAction();
		} else {
			JOptionPane.showMessageDialog(null, "Erreur while deleting");
			refreshAction();
		}
	}

	/**
	 * Method to refresh frame elements jtree and jlists
	 */
	public void refreshAction() {

		generateTree();

		if (userInstance.getApp()) {
			refreshArchiveList();
		}

		refreshProjects();
	}

	/**
	 * Method to generate the gitlab tree
	 */
	public void generateTree() {
		if (tree != null) {
			tree.clearSelection();
		}

		//
		try {
			projectlist = gitlabApi.getProjectApi().getProjects(false, null, Constants.ProjectOrderBy.NAME,
					Constants.SortOrder.ASC, null, false, true, false, false, true);
			archivedlist = gitlabApi.getProjectApi().getProjects(true, null, Constants.ProjectOrderBy.NAME,
					Constants.SortOrder.ASC, null, false, true, false, false, true);
			if (userInstance.getToken() == null) {
				grouplist = FunctionsAPI.getGroupsOwned(userInstance.getUsername(), userInstance.getPassword());
			} else {
				grouplist = FunctionsAPI.getGroupsOwned(userInstance.getToken());
			}
			moduleListString = DB.getModule();
		} catch (GitLabApiException e1) {
			e1.printStackTrace();
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		gitRoot = new DefaultMutableTreeNode("Gitlab");
		gitProjects = new DefaultMutableTreeNode("Projects");
		gitGroups = new DefaultMutableTreeNode("Groups");
		gitRoot.add(gitProjects);
		gitRoot.add(gitGroups);

		// add projects to tree
		for (ListIterator<Project> iter = projectlist.listIterator(); iter.hasNext();) {
			Project element = iter.next();
			DefaultMutableTreeNode temp = new DefaultMutableTreeNode(element.getName());
			try {
				gitlabApi.getRepositoryApi().getTree(element.getId(), "/", null)
						.forEach(name -> temp.add(new DefaultMutableTreeNode(name.getName())));
			} catch (Exception e) {
				System.out.println(e);

			}
			gitProjects.add(temp);
		}

		// add groups to tree
		for (ListIterator<Group> iter = grouplist.listIterator(); iter.hasNext();) {
			Group element = iter.next();
			DefaultMutableTreeNode temp = new DefaultMutableTreeNode(element.getName());
			try {
				List<Project> p = gitlabApi.getGroupApi().getGroup(element.getId()).getProjects();
				p.forEach((Project pro) -> {
					DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(pro.getName());
					temp.add(projectNode);
					try {
						gitlabApi.getRepositoryApi().getTree(pro.getId(), "/", null)
								.forEach(name -> projectNode.add(new DefaultMutableTreeNode(name.getName())));
					} catch (GitLabApiException e1) {
						e1.printStackTrace();
					}
				});
			} catch (Exception e) {
				System.out.println(e);
			}
			gitGroups.add(temp);
		}

		// add modules to tree if user is of type app
		if (userInstance.getApp()) {
			gitModules = new DefaultMutableTreeNode("Modules");
			gitRoot.add(gitModules);
			for (ListIterator<String> iter = moduleListString.listIterator(); iter.hasNext();) {
				String module = iter.next();
				DefaultMutableTreeNode temp = new DefaultMutableTreeNode(module);
				try {
					List<String> groupList = DB.getListGroupsIdOfModules(module);
					groupList.forEach((String group) -> {
						try {
							Group g = gitlabApi.getGroupApi().getGroup(Integer.valueOf(group));
							DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(g.getName());
							temp.add(groupNode);
							List<Project> projectList = g.getProjects();
							projectList.forEach((Project p) -> {
								DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(p.getName());
								groupNode.add(projectNode);
								try {
									gitlabApi.getRepositoryApi().getTree(p.getId(), "/", null).forEach(
											name -> projectNode.add(new DefaultMutableTreeNode(name.getName())));
								} catch (GitLabApiException e) {
									e.printStackTrace();
								}
							});
						} catch (GitLabApiException e) {
							e.printStackTrace();
						}
					});
				} catch (Exception e) {
					System.out.println(e);
				}
				gitModules.add(temp);
			}
		}

		treeModel.setRoot(gitRoot);
		treeModel.reload();
	}

	/**
	 * Method to update the archived projects list
	 */
	public void refreshArchiveList() {
		archived.clear();
		for (Project p : archivedlist) {
			archived.add(p.getName());
		}
		String[] archivedNames = archived.toArray(new String[archived.size()]);
		modelAlreadyArchived.clear();
		for (int i = 0, n = archivedNames.length; i < n; i++) {
			modelAlreadyArchived.addElement(archivedNames[i]);
		}
		listAlreadyArchived.setModel(modelAlreadyArchived);
		listAlreadyArchived.updateUI();
	}

	/**
	 * Method to update the projects and the modules in the list
	 */
	public static void refreshProjects() {
		projects.clear();
		for (Project p : projectlist) {
			projects.add(p.getName());
		}

		DefaultListModel<String> projectsModel = new DefaultListModel<>();
		projects.forEach((String project) -> projectsModel.addElement(project));
		listProjects.setModel(projectsModel);

		// Liste des projets
		if (userInstance.getApp()) {
			moduleListString = DB.getModule();
			DefaultListModel<String> modulesModel = new DefaultListModel<>();
			moduleListString.forEach((String module) -> modulesModel.addElement(module));
			listModules.setModel(modulesModel);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER && textFieldProject.hasFocus()) {
			txtGroup.requestFocus();
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER && txtGroup.hasFocus()) {
			btnAdd.doClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (JSearchProject.hasFocus()) {
			String projectSearched = JSearchProject.getText();
			ArrayList<String> listCorrespondingProjects = new ArrayList<String>();// List of all the users who
																					// correspond to the search
			projects.forEach((String project) -> {
				if (project.toLowerCase().contains(projectSearched.toLowerCase())) {
					listCorrespondingProjects.add(project);
				}
			});
			DefaultListModel<String> projectsModel = new DefaultListModel<>();
			listCorrespondingProjects.forEach((String project) -> projectsModel.addElement(project));

			listProjects.setModel(projectsModel);// Set the new datas
		} else if (textFieldModule != null && textFieldModule.hasFocus()) {
			String moduleSearched = textFieldModule.getText();
			ArrayList<String> listCorrespondingModules = new ArrayList<String>();// List of all the users who
																					// correspond to the search
			moduleListString.forEach((String module) -> {
				if (module.toLowerCase().contains(moduleSearched.toLowerCase())) {
					listCorrespondingModules.add(module);
				}
			});
			DefaultListModel<String> modulesModel = new DefaultListModel<>();
			listCorrespondingModules.forEach((String project) -> modulesModel.addElement(project));

			listModules.setModel(modulesModel);// Set the new datas
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (listModules.hasFocus()) {
			if (listModules.getSelectedValuesList().isEmpty()) {
				refreshProjects();
			} else {
				ArrayList<String> modulesSelected = (ArrayList<String>) listModules.getSelectedValuesList();
				projects.clear();
				for (String module : modulesSelected) {// Each module
					ArrayList<String> IdOfGroups = DB.getListGroupsIdOfModules(module);
					for (String groupId : IdOfGroups) {// Each group
						List<Project> projectList = null;
						try {
							GroupProjectsFilter filter = new GroupProjectsFilter();
							filter.withArchived(false);
							filter.withVisibility(Visibility.PRIVATE);
							filter.withOrderBy(ProjectOrderBy.NAME);
							filter.withSortOder(SortOrder.ASC);
							projectList = gitlabApi.getGroupApi().getProjects(groupId, filter);
						} catch (GitLabApiException me) {
							me.printStackTrace();
						}
						for (Project project : projectList) {
							if (project != null) {
								projects.add(project.getName());
							}
						}
						DefaultListModel<String> projectsModel = new DefaultListModel<>();
						projects.forEach((String project) -> projectsModel.addElement(project));
						listProjects.setModel(projectsModel);
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
