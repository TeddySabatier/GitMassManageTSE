package app;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.Project;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.mashape.unirest.http.exceptions.UnirestException;

import api.FunctionsAPI;
import bdd.DatabaseApp;

/**
 * @author Kim-Celine FRANOT
 * @author Thomas VAN WYNENDAELE
 *
 */
@SuppressWarnings("serial")
public class Statistics extends JPanel implements ItemListener {

	// components of the panel
	private JPanel panelFrame;
	private JComboBox<String> listProject;
	private JComboBox<String> listGroup;
	private JComboBox<String> listModule;
	private JLabel lblTitle;
	private JLabel lblProject;
	private JLabel lblGroup;
	private JLabel lblModule;
	private JList<String> memberslist;

	// static variables
	static GitLabApi gitlabApi;
	static Stream<Project> projectStream;

	// Stream the visible projects printing out the project name.
	static ArrayList<String> projects;
	static List<Project> listproject;
	static ArrayList<String> groups;
	static List<Group> listgroup;
	static ArrayList<String> modules;
	static int count_commits = 0;
	static int count_branches = 0;
	static int count_repositories = 0;
	static Date last_activity;
	static ArrayList<String> members = new ArrayList<String>();

	static int nb_group_members = 0;
	static int nb_group_projects = 0;
	static int nb_module_members = 0;
	static int nb_module_projects = 0;

	private static DatabaseApp DB;

	private JPanel globalpanel;
	private JPanel panelProject;
	private JPanel panelGroup;
	private JPanel panelModule;

	private DefaultMutableTreeNode Gitroot;
	private DefaultTreeModel treeModel;
	private JTree tree;

	private ChartPanel panel;
	private JFreeChart chart;
	private DefaultPieDataset dataset;

	private JPanel panelNbBranches;
	private JLabel lblBranches;
	private JLabel lblNbBranches;
	private JPanel panelMembers;
	private JLabel lblGroups;
	private JScrollPane scrollPane;
	private JPanel panelNbCommit;
	private JLabel lblRepositories;
	private JLabel lblNbCommit;
	private JPanel panelCommitDate;
	private JLabel lblLastCommit;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;

	private JPanel panelNbRepo;
	private JLabel lblRepo;
	private JLabel lblNbRepo;
	private JPanel panelNbMembers;
	private JLabel lblMembers;
	private JLabel lblNbMembers;
	private JPanel panelStorage;
	private JLabel lblStorage;
	private JLabel lblStorageSize;

	private JPanel panelNbGroup_;
	private JLabel lblGroup_;
	private JLabel lblNbGroup_;
	private JPanel panelNbRepo_;
	private JLabel lblRepo_;
	private JLabel lblNbRepo_;
	private JPanel panelNbWorkers_;
	private JLabel lblWorkers_;
	private JLabel lblNbWorkers_;

	// variables
	private String currentPanel;
	private User user = User.getInstance();

	/**
	 * Create the panel.
	 */
	public Statistics(GitLabApi gitLabApi) {
		setVisible(true);
		gitlabApi = gitLabApi;
		projects = new ArrayList<String>();
		groups = new ArrayList<String>();
		modules = new ArrayList<String>();
		try {
			listproject = gitlabApi.getProjectApi().getProjects(false, null, Constants.ProjectOrderBy.NAME,
					Constants.SortOrder.ASC, null, false, true, false, false, true);
			if (user.getToken() == null) {
				listgroup = FunctionsAPI.getGroupsOwned(user.getUsername(), user.getPassword());
			} else {
				listgroup = FunctionsAPI.getGroupsOwned(user.getToken());
			}
			listproject.forEach((Project p) -> projects.add(p.getName()));
			listgroup.forEach((Group p) -> groups.add(p.getName()));

			DB = new DatabaseApp();
			modules = DB.getModule();
		} catch (GitLabApiException e) {
			e.printStackTrace();
		} catch (UnirestException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		initComponents();
	}

	private void initComponents() {
		setLayout(null);
		panelFrame = new JPanel();
		panelFrame.setBounds(0, 0, 884, 634);
		panelFrame.setBackground(new Color(153, 204, 255));
		add(panelFrame);

		lblTitle = new JLabel();
		lblTitle.setBounds(24, 32, 430, 44);
		lblTitle.setToolTipText("");
		lblTitle.setText("Your GitLab Statistics");
		lblTitle.setForeground(new Color(41, 41, 97));
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

		lblProject = new JLabel();
		lblProject.setBounds(24, 88, 85, 44);
		lblProject.setToolTipText("");
		lblProject.setText("Project :");
		lblProject.setForeground(new Color(41, 41, 97));
		lblProject.setFont(new Font("Segoe UI", Font.BOLD, 18));

		lblModule = new JLabel();
		lblModule.setBounds(597, 88, 85, 44);
		lblModule.setToolTipText("");
		lblModule.setText("Module :");
		lblModule.setForeground(new Color(41, 41, 97));
		lblModule.setFont(new Font("Segoe UI", Font.BOLD, 18));

		lblGroup = new JLabel();
		lblGroup.setBounds(313, 88, 85, 44);
		lblGroup.setToolTipText("");
		lblGroup.setText("Group :");
		lblGroup.setForeground(new Color(41, 41, 97));
		lblGroup.setFont(new Font("Segoe UI", Font.BOLD, 18));
		panelFrame.add(lblGroup);
		panelFrame.setLayout(null);
		panelFrame.add(lblTitle);
		panelFrame.add(lblProject);

		listProject = new JComboBox<String>();
		listProject.setBounds(105, 104, 181, 21);
		String[] array1 = projects.toArray(new String[projects.size()]);
		DefaultComboBoxModel model = new DefaultComboBoxModel<>(array1);
		listProject.setModel(model);
		listProject.setSelectedIndex(-1);
		listProject.addItemListener(this);
		panelFrame.add(listProject);

		listGroup = new JComboBox<String>();
		listGroup.setBounds(384, 104, 181, 21);
		String[] array2 = groups.toArray(new String[groups.size()]);
		DefaultComboBoxModel model2 = new DefaultComboBoxModel<>(array1);
		listGroup.setModel(model);
		listGroup.setSelectedIndex(-1);
		listGroup.addItemListener(this);
		panelFrame.add(listGroup);

		listModule = new JComboBox<String>();
		listModule.setBounds(679, 104, 181, 21);
		String[] array3 = modules.toArray(new String[modules.size()]);
		DefaultComboBoxModel model3 = new DefaultComboBoxModel<>(array1);
		listModule.setModel(model3);
		listModule.setSelectedIndex(-1);
		listModule.addItemListener(this);

		// add extra elements if application account
		if (user.getApp()) {
			panelFrame.add(lblModule);
			panelFrame.add(listModule);
		}

		// definition du panel global regroupant les 3 panels de statistiques :
		// project, group, module
		globalpanel = new JPanel(new CardLayout());
		globalpanel.setBounds(24, 162, 860, 472);
		panelFrame.add(globalpanel);

		// project panel initialisation
		panelProject = new JPanel();
		panelProject.setBounds(0, 162, 884, 472);
		panelProject.setBackground(new Color(153, 204, 255));
		panelProject.setLayout(null);
		globalpanel.add(panelProject, "project");

		Gitroot = new DefaultMutableTreeNode("Gitlab");
		treeModel = new DefaultTreeModel(Gitroot);
		tree = new JTree(treeModel);
		tree.setBounds(581, 5, 200, 221);
		tree.setBackground(null);
		panelProject.add(tree);

		dataset = new DefaultPieDataset();
		dataset.setValue("Unknow", new Double(100));
		JFreeChart chart = ChartFactory.createPieChart("", // chart title
				dataset, false, false, true);

		panel = new ChartPanel(chart);
		panel.setBounds(544, 228, 274, 185);
		panelProject.add(panel);
		panel.setVisible(true);

		panelNbBranches = new JPanel();
		panelNbBranches.setBounds(453, 5, 1, 1);
		panelNbBranches.setLayout(null);
		panelProject.add(panelNbBranches);

		lblBranches = new JLabel();
		lblBranches.setToolTipText("");
		lblBranches.setText("Number of branches");
		lblBranches.setHorizontalAlignment(SwingConstants.CENTER);
		lblBranches.setForeground(new Color(41, 41, 97));
		lblBranches.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblBranches.setBounds(10, 10, 180, 25);
		panelNbBranches.add(lblBranches);

		lblNbBranches = new JLabel();
		lblNbBranches.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbBranches.setForeground(new Color(208, 45, 26));
		lblNbBranches.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNbBranches.setBounds(10, 45, 180, 49);
		panelNbBranches.add(lblNbBranches);

		panelMembers = new JPanel();
		panelMembers.setLayout(null);
		panelMembers.setName("");
		panelMembers.setBounds(18, 47, 202, 131);
		panelProject.add(panelMembers);

		lblGroups = new JLabel();
		lblGroups.setToolTipText("");
		lblGroups.setText("Members");
		lblGroups.setHorizontalAlignment(SwingConstants.CENTER);
		lblGroups.setForeground(new Color(41, 41, 97));
		lblGroups.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblGroups.setBounds(10, 10, 182, 25);
		panelMembers.add(lblGroups);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 0, 0);
		panelMembers.add(scrollPane);

		panelNbCommit = new JPanel();
		panelNbCommit.setLayout(null);
		panelNbCommit.setBounds(285, 47, 200, 131);
		panelProject.add(panelNbCommit);

		lblRepositories = new JLabel();
		lblRepositories.setToolTipText("");
		lblRepositories.setText("Commits Counts");
		lblRepositories.setHorizontalAlignment(SwingConstants.CENTER);
		lblRepositories.setForeground(new Color(41, 41, 97));
		lblRepositories.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblRepositories.setBounds(10, 10, 180, 25);
		panelNbCommit.add(lblRepositories);

		lblNbCommit = new JLabel();
		lblNbCommit.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbCommit.setForeground(new Color(208, 45, 26));
		lblNbCommit.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNbCommit.setBounds(10, 45, 180, 49);
		panelNbCommit.add(lblNbCommit);

		panelCommitDate = new JPanel();
		panelCommitDate.setLayout(null);
		panelCommitDate.setBounds(285, 228, 197, 131);
		panelProject.add(panelCommitDate);

		lblLastCommit = new JLabel();
		lblLastCommit.setToolTipText("");
		lblLastCommit.setText("Last Commit");
		lblLastCommit.setHorizontalAlignment(SwingConstants.CENTER);
		lblLastCommit.setForeground(new Color(41, 41, 97));
		lblLastCommit.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblLastCommit.setBounds(10, 10, 177, 25);
		panelCommitDate.add(lblLastCommit);

		lblNewLabel_2 = new JLabel();
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setForeground(new Color(208, 45, 26));
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel_2.setBounds(8, 43, 187, 46);
		panelCommitDate.add(lblNewLabel_2);

		lblNewLabel_3 = new JLabel();
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setForeground(new Color(208, 45, 26));
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 20));
		lblNewLabel_3.setBounds(10, 89, 177, 33);
		panelCommitDate.add(lblNewLabel_3);

		panelNbBranches = new JPanel();
		panelNbBranches.setLayout(null);
		panelNbBranches.setBounds(18, 228, 200, 131);
		panelProject.add(panelNbBranches);

		lblBranches = new JLabel();
		lblBranches.setToolTipText("");
		lblBranches.setText("Number of branches");
		lblBranches.setHorizontalAlignment(SwingConstants.CENTER);
		lblBranches.setForeground(new Color(41, 41, 97));
		lblBranches.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblBranches.setBounds(10, 10, 180, 25);
		panelNbBranches.add(lblBranches);

		lblNbBranches = new JLabel();
		lblNbBranches.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbBranches.setForeground(new Color(208, 45, 26));
		lblNbBranches.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNbBranches.setBounds(10, 45, 180, 49);
		panelNbBranches.add(lblNbBranches);

		// group panel initialisation
		panelGroup = new JPanel();
		panelGroup.setBounds(0, 162, 884, 472);
		panelGroup.setBackground(new Color(153, 204, 255));
		panelGroup.setLayout(null);
		globalpanel.add(panelGroup, "group");

		panelNbRepo = new JPanel();
		panelNbRepo.setLayout(null);
		panelNbRepo.setBounds(8, 30, 230, 131);
		panelGroup.add(panelNbRepo);

		lblRepo = new JLabel();
		lblRepo.setToolTipText("");
		lblRepo.setText("Number of repository");
		lblRepo.setHorizontalAlignment(SwingConstants.CENTER);
		lblRepo.setForeground(new Color(41, 41, 97));
		lblRepo.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblRepo.setBounds(10, 10, 212, 25);
		panelNbRepo.add(lblRepo);

		lblNbRepo = new JLabel();
		lblNbRepo.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbRepo.setForeground(new Color(208, 45, 26));
		lblNbRepo.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNbRepo.setBounds(10, 45, 180, 49);
		panelNbRepo.add(lblNbRepo);

		panelNbMembers = new JPanel();
		panelNbMembers.setLayout(null);
		panelNbMembers.setBounds(315, 30, 200, 131);
		panelGroup.add(panelNbMembers);

		lblMembers = new JLabel();
		lblMembers.setToolTipText("");
		lblMembers.setText("Number of workers");
		lblMembers.setHorizontalAlignment(SwingConstants.CENTER);
		lblMembers.setForeground(new Color(41, 41, 97));
		lblMembers.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblMembers.setBounds(10, 10, 180, 25);
		panelNbMembers.add(lblMembers);

		lblNbMembers = new JLabel();
		lblNbMembers.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbMembers.setForeground(new Color(208, 45, 26));
		lblNbMembers.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNbMembers.setBounds(10, 45, 180, 49);
		panelNbMembers.add(lblNbMembers);

		panelStorage = new JPanel();
		panelStorage.setLayout(null);
		panelStorage.setBounds(590, 30, 224, 131);
		panelGroup.add(panelStorage);

		lblStorage = new JLabel();
		lblStorage.setToolTipText("");
		lblStorage.setText("Last Activity");
		lblStorage.setHorizontalAlignment(SwingConstants.CENTER);
		lblStorage.setForeground(new Color(41, 41, 97));
		lblStorage.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblStorage.setBounds(10, 10, 212, 25);
		panelStorage.add(lblStorage);

		lblStorageSize = new JLabel();
		lblStorageSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblStorageSize.setForeground(new Color(208, 45, 26));
		lblStorageSize.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblStorageSize.setBounds(10, 45, 206, 49);
		panelStorage.add(lblStorageSize);

		// modules panel intialisation
		panelModule = new JPanel();
		panelModule.setBounds(0, 162, 884, 472);
		panelModule.setBackground(new Color(153, 204, 255));
		panelModule.setLayout(null);
		globalpanel.add(panelModule, "module");

		panelNbGroup_ = new JPanel();
		panelNbGroup_.setLayout(null);
		panelNbGroup_.setBounds(8, 30, 202, 131);
		panelModule.add(panelNbGroup_);

		lblGroup_ = new JLabel();
		lblGroup_.setToolTipText("");
		lblGroup_.setText("Number of group");
		lblGroup_.setHorizontalAlignment(SwingConstants.CENTER);
		lblGroup_.setForeground(new Color(41, 41, 97));
		lblGroup_.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblGroup_.setBounds(10, 10, 180, 25);
		panelNbGroup_.add(lblGroup_);

		lblNbGroup_ = new JLabel();
		lblNbGroup_.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbGroup_.setForeground(new Color(208, 45, 26));
		lblNbGroup_.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNbGroup_.setBounds(10, 45, 180, 49);
		panelNbGroup_.add(lblNbGroup_);

		panelNbRepo_ = new JPanel();
		panelNbRepo_.setLayout(null);
		panelNbRepo_.setBounds(288, 30, 222, 131);
		panelModule.add(panelNbRepo_);

		lblRepo_ = new JLabel();
		lblRepo_.setToolTipText("");
		lblRepo_.setText("Number of repository");
		lblRepo_.setHorizontalAlignment(SwingConstants.CENTER);
		lblRepo_.setForeground(new Color(41, 41, 97));
		lblRepo_.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblRepo_.setBounds(10, 10, 204, 25);
		panelNbRepo_.add(lblRepo_);

		lblNbRepo_ = new JLabel();
		lblNbRepo_.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbRepo_.setForeground(new Color(208, 45, 26));
		lblNbRepo_.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblNbRepo_.setBounds(10, 45, 180, 49);
		panelNbRepo_.add(lblNbRepo_);

		panelNbWorkers_ = new JPanel();
		panelNbWorkers_.setLayout(null);
		panelNbWorkers_.setBounds(590, 30, 222, 131);
		panelModule.add(panelNbWorkers_);

		lblWorkers_ = new JLabel();
		lblWorkers_.setToolTipText("");
		lblWorkers_.setText("Last Activity");
		lblWorkers_.setHorizontalAlignment(SwingConstants.CENTER);
		lblWorkers_.setForeground(new Color(41, 41, 97));
		lblWorkers_.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblWorkers_.setBounds(10, 10, 180, 25);
		panelNbWorkers_.add(lblWorkers_);

		lblNbWorkers_ = new JLabel();
		lblNbWorkers_.setHorizontalAlignment(SwingConstants.CENTER);
		lblNbWorkers_.setForeground(new Color(208, 45, 26));
		lblNbWorkers_.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNbWorkers_.setBounds(10, 45, 204, 49);
		panelNbWorkers_.add(lblNbWorkers_);

		// show project panel
		CardLayout cl = (CardLayout) (globalpanel.getLayout());
		cl.show(globalpanel, "project");
		currentPanel = "project";
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == listProject && listProject.getSelectedIndex() != -1) {
			// set other lists to none selected
			listGroup.setSelectedIndex(-1);
			listModule.setSelectedIndex(-1);

			listActionProject();

			// change panel to show
			CardLayout cl = (CardLayout) (globalpanel.getLayout());
			cl.show(globalpanel, "project");
			currentPanel = "project";
		}
		if (e.getSource() == listGroup && listGroup.getSelectedIndex() != -1) {
			// set other lists to none selected
			listProject.setSelectedIndex(-1);
			listModule.setSelectedIndex(-1);

			listActionGroup();

			// change panel to show
			CardLayout cl = (CardLayout) (globalpanel.getLayout());
			cl.show(globalpanel, "group");
			currentPanel = "group";

		}
		if (e.getSource() == listModule && listModule.getSelectedIndex() != -1) {
			// set other lists to none selected
			listProject.setSelectedIndex(-1);
			listGroup.setSelectedIndex(-1);

			listActionModule();

			// change panel to show
			CardLayout cl = (CardLayout) (globalpanel.getLayout());
			cl.show(globalpanel, "module");
			currentPanel = "module";
		}
	}

	/**
	 * Method to update project panel according to project selected
	 */
	public void listActionProject() {
		// get the selected project
		Project selectedproject = listproject.get(listProject.getSelectedIndex());

		// update tree
		Gitroot = new DefaultMutableTreeNode("Gitlab");
		DefaultMutableTreeNode temp = new DefaultMutableTreeNode(selectedproject.getName());
		try {
			members = new ArrayList<String>();
			gitlabApi.getProjectApi().getMembers(selectedproject.getId()).forEach(item -> members.add(item.getName()));
			gitlabApi.getRepositoryApi().getTree(selectedproject.getId(), "/", null)
					.forEach(name -> temp.add(new DefaultMutableTreeNode(name.getName())));

		} catch (GitLabApiException e1) {
			e1.printStackTrace();
		}
		Gitroot.add(temp);
		treeModel.setRoot(Gitroot);
		treeModel.reload();

		// update list of members
		memberslist = new JList<String>(members.toArray(new String[projects.size()]));
		memberslist.setBounds(10, 5, 150, 100);
		scrollPane.setBounds(10, 40, 180, 90);
		scrollPane.setViewportView(memberslist);

		// get last activity
		last_activity = selectedproject.getLastActivityAt();
		if (last_activity != null) {
			lblNewLabel_2.setText(String.valueOf(last_activity.toGMTString().split("GMT")[0]));
		} else {
			lblNewLabel_2.setText("");
		}

		// get number of commits
		count_commits = (int) selectedproject.getStatistics().getCommitCount();
		lblNbCommit.setText(String.valueOf(count_commits));

		// get number of branches
		try {
			List<Branch> listBranch = gitlabApi.getRepositoryApi().getBranches(selectedproject.getId());
			count_branches = listBranch.size();
		} catch (GitLabApiException e) {
			e.printStackTrace();
		}
		lblNbBranches.setText(String.valueOf(count_branches));

		// create pie chart with languages of project
		Map<String, Float> lang;
		try {
			lang = gitlabApi.getProjectApi().getProjectLanguages(selectedproject.getId());
			if (lang.size() > 0) {
				dataset = new DefaultPieDataset();
				lang.forEach((key, value) -> dataset.setValue(key, new Double(value)));
				chart = ChartFactory.createPieChart("", // chart title
						dataset, false, false, true);
				panel.setChart(chart);
				panel.setVisible(true);
			} else {
				panel.setVisible(false);
			}
		} catch (GitLabApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to update group panel according to group selected
	 */
	public void listActionGroup() {
		// get the selected group
		Group selectedgroup = listgroup.get(listGroup.getSelectedIndex());
		List<Project> list_projects;
		nb_group_projects = 0;
		nb_group_members = 0;
		last_activity = null;

		// get different values
		try {
			list_projects = gitlabApi.getGroupApi().getGroup(selectedgroup.getId()).getProjects();
			nb_group_members = gitlabApi.getGroupApi().getMembers(selectedgroup.getId()).size();
			nb_group_projects = list_projects.size();
			if (list_projects.size() > 0) {
				last_activity = list_projects.get(0).getLastActivityAt();
			}
			for (Project project : list_projects) {
				if (project.getLastActivityAt().compareTo(last_activity) > 0) {
					last_activity = project.getLastActivityAt();
				}
			}
		} catch (GitLabApiException e1) {
			e1.printStackTrace();
		}
		lblNbRepo.setText(String.valueOf(nb_group_projects));
		lblNbMembers.setText(String.valueOf(nb_group_members));
		if (last_activity != null) {
			lblStorageSize.setText(String.valueOf(last_activity.toGMTString().split("GMT")[0]));
		} else {
			lblStorageSize.setText("");
		}
	}

	/**
	 * Method to update module panel according to module selected
	 */
	public void listActionModule() {
		// get the selected module
		String selectedmodule = modules.get(listModule.getSelectedIndex());
		List<String> list_group_id = DB.getListGroupsIdOfModules(selectedmodule);
		nb_module_projects = 0;
		nb_module_members = 0;
		last_activity = null;
		for (String group_id : list_group_id) {
			try {
				List<Project> list_projects = gitlabApi.getGroupApi().getGroup(group_id).getProjects();
				if (list_projects.size() > 0) {
					last_activity = list_projects.get(0).getLastActivityAt();
				}
				for (Project project : list_projects) {
					if (project.getLastActivityAt().compareTo(last_activity) > 0) {
						last_activity = project.getLastActivityAt();
					}
				}
				;
				System.out.println(last_activity);
				nb_module_projects += list_projects.size();
			} catch (GitLabApiException e1) {
				e1.printStackTrace();
			}
		}
		;
		lblNbGroup_.setText(String.valueOf(list_group_id.size()));
		lblNbRepo_.setText(String.valueOf(nb_module_projects));
		if (last_activity != null) {
			lblNbWorkers_.setText(String.valueOf(last_activity.toGMTString().split("GMT")[0]));
			System.out.println(last_activity.toGMTString().split("GMT")[0]);
		} else {
			lblNbWorkers_.setText("");
		}
	}

	/**
	 * Method to update values of the tab when tab is selected
	 */
	public void update() {
		projects = new ArrayList<String>();
		groups = new ArrayList<String>();
		modules = DB.getModule();

		// get new list of projects, groups and projects
		try {
			listproject = gitlabApi.getProjectApi().getProjects(false, null, Constants.ProjectOrderBy.NAME,
					Constants.SortOrder.ASC, null, false, true, false, false, true);
			if (user.getToken() == null) {
				listgroup = FunctionsAPI.getGroupsOwned(user.getUsername(), user.getPassword());
			} else {
				listgroup = FunctionsAPI.getGroupsOwned(user.getToken());
			}
			listproject.forEach((Project p) -> projects.add(p.getName()));
			listgroup.forEach((Group p) -> groups.add(p.getName()));
		} catch (GitLabApiException e) {
			e.printStackTrace();
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		// repopulate the projects combobox
		listProject.removeAllItems();
		String[] array1 = projects.toArray(new String[projects.size()]);
		DefaultComboBoxModel model = new DefaultComboBoxModel<>(array1);
		listProject.setModel(model);
		listProject.setSelectedIndex(0);

		// repopulate the groups combobox
		listGroup.removeAllItems();
		String[] array2 = groups.toArray(new String[groups.size()]);
		DefaultComboBoxModel model2 = new DefaultComboBoxModel<>(array2);
		listGroup.setModel(model2);
		listGroup.setSelectedIndex(-1);

		// repopulate the modules combobox
		listModule.removeAllItems();
		String[] array3 = modules.toArray(new String[modules.size()]);
		DefaultComboBoxModel model3 = new DefaultComboBoxModel<>(array3);
		listModule.setModel(model3);
		listModule.setSelectedIndex(-1);

		// show project panel
		CardLayout cl = (CardLayout) (globalpanel.getLayout());
		cl.show(globalpanel, "project");
		currentPanel = "project";

		// set to first project in the list
		try {
			listProject.setSelectedIndex(0);
			listActionProject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
