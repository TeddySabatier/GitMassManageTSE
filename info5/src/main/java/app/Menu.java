package app;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.gitlab4j.api.GitLabApi;

/**
 * @author Kim-Celine FRANOT
 * @author Thomas VAN WYNENDAELE
 *
 */
@SuppressWarnings("serial")
public class Menu extends JFrame implements MouseListener, ActionListener {

	// components of the frame
	private JPanel globalpanel;
	private Home home;
	private Statistics statistics;
	private JPanel menuPanel;
	private Dashboard dashboard;
	private Settings settings;
	private JPanel ButtonHome;
	private JPanel ButtonDashboard;
	private JPanel ButtonStatistics;
	private JPanel ButtonSettings;
	private JPanel IndicatorHome;
	private JPanel IndicatorDashboard;
	private JPanel IndicatorStatistics;
	private JPanel IndicatorSettings;
	private JLabel lblHome;
	private JLabel lblDashboard;
	private JLabel lblStatistics;
	private JLabel lblSettings;
	private JLabel lblLogo;
	private JLabel lblUserIcon;
	private JLabel lblUserName;
	private JButton btnLogout;
	private JButton btnHelp;

	// variables
	private String currentPanel;
	private User user = User.getInstance();
	static GitLabApi gitlabApi;

	/**
	 * Create the application.
	 */
	public Menu() {
		setTitle("GIT TSE");
		setResizable(false);
		setIconImage(new ImageIcon("./src/main/java/assets/icons/gitlab.png").getImage());
		gitlabApi = user.getGitLabApi();
		initialize();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		// set common elements of the frame regardless of the type of account
		setBounds(100, 100, 1140, 663);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		menuPanel = new JPanel();
		menuPanel.setBackground(new Color(41, 41, 97));
		menuPanel.setBounds(0, 0, 250, 634);
		menuPanel.setLayout(null);

		lblUserIcon = new JLabel("");
		lblUserIcon.setIcon(new ImageIcon(Menu.class.getResource("/assets/icons/user.png")));
		lblUserIcon.setBounds(10, 11, 32, 32);

		lblUserName = new JLabel(user.getDisplayName());
		lblUserName.setForeground(new Color(255, 255, 255));
		lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblUserName.setBounds(53, 16, 187, 26);

		lblLogo = new JLabel();
		lblLogo.setBounds(24, 86, 170, 141);
		lblLogo.setIcon(new ImageIcon(Menu.class.getResource("/assets/logo.png")));

		btnLogout = new JButton("Logout", new ImageIcon(Menu.class.getResource("/assets/icons/logout.png")));
		btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
		btnLogout.setBounds(10, 575, 132, 40);
		btnLogout.setForeground(new Color(255, 255, 255));
		btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnLogout.setFocusPainted(false);
		btnLogout.setBorderPainted(false);
		btnLogout.setContentAreaFilled(false);
		btnLogout.setOpaque(false);
		btnLogout.addActionListener(this);
		btnLogout.addMouseListener(this);

		btnHelp = new JButton("");
		btnHelp.setIcon(new ImageIcon(Menu.class.getResource("/assets/icons/help.png")));
		btnHelp.setBounds(196, 582, 44, 33);
		btnHelp.setFocusPainted(false);
		btnHelp.setBorderPainted(false);
		btnHelp.setContentAreaFilled(false);
		btnHelp.setOpaque(false);
		btnHelp.addActionListener(this);
		menuPanel.add(btnHelp);

		menuPanel.add(lblUserIcon);
		menuPanel.add(lblUserName);
		menuPanel.add(lblLogo);
		menuPanel.add(btnLogout);

		getContentPane().add(menuPanel);

		// panel that will display tabs content
		globalpanel = new JPanel(new CardLayout());
		globalpanel.setBounds(252, 0, 884, 634);
		getContentPane().add(globalpanel);

		// add elements if account is not of type user => admin
		if (user.getIsUser() == false) {

			// add tab Settings
			settings = new Settings();
			settings.setBounds(252, 0, 884, 634);

			globalpanel.add(settings, "settings");

			// add button Settings in menuPanel
			ButtonSettings = new JPanel();
			ButtonSettings.setBounds(10, 285, 240, 56);
			ButtonSettings.setBorder(null);
			ButtonSettings.setBackground(new Color(208, 45, 26));
			ButtonSettings.setLayout(null);

			IndicatorSettings = new JPanel();
			IndicatorSettings.setBounds(0, 0, 10, 56);
			IndicatorSettings.setForeground(Color.BLACK);
			IndicatorSettings.setBorder(null);
			IndicatorSettings.setBackground(Color.BLACK);
			IndicatorSettings.setLayout(null);

			lblSettings = new JLabel();
			lblSettings.setHorizontalAlignment(SwingConstants.LEFT);
			lblSettings.setBounds(10, 0, 230, 56);
			lblSettings.setText("    Settings");
			lblSettings.setForeground(Color.BLACK);
			lblSettings.setFont(new Font("Segoe UI", Font.BOLD, 14));

			ButtonSettings.add(lblSettings);
			ButtonSettings.add(IndicatorSettings);
			menuPanel.add(ButtonSettings);

			// show settings tab
			CardLayout cl = (CardLayout) (globalpanel.getLayout());
			cl.show(globalpanel, "settings");

		}
		// else if account is of type user
		else {
			// we add common elements

			// add tab Home
			home = new Home(gitlabApi);
			home.setBounds(252, 0, 884, 634);
			globalpanel.add(home, "home");

			// add button Home in menuPanel
			ButtonHome = new JPanel();
			ButtonHome.setBounds(10, 285, 240, 56);
			ButtonHome.setBorder(null);
			ButtonHome.addMouseListener(this);
			ButtonHome.setBackground(new Color(208, 45, 26));
			ButtonHome.setLayout(null);

			IndicatorHome = new JPanel();
			IndicatorHome.setBounds(0, 0, 10, 56);
			IndicatorHome.setForeground(Color.BLACK);
			IndicatorHome.setBorder(null);
			IndicatorHome.setBackground(Color.BLACK);
			IndicatorHome.setLayout(null);

			lblHome = new JLabel();
			lblHome.setHorizontalAlignment(SwingConstants.LEFT);
			lblHome.setBounds(10, 0, 230, 56);
			lblHome.addMouseListener(this);
			lblHome.setText("    Home");
			lblHome.setForeground(Color.BLACK);
			lblHome.setFont(new Font("Segoe UI", Font.BOLD, 14));

			ButtonHome.add(lblHome);
			ButtonHome.add(IndicatorHome);
			menuPanel.add(ButtonHome);

			// add tab Statistics
			statistics = new Statistics(gitlabApi);
			statistics.setBounds(252, 0, 884, 634);
			globalpanel.add(statistics, "statistics");

			// add button Statistics in menuPanel
			ButtonStatistics = new JPanel();
			ButtonStatistics.setBounds(10, 352, 240, 56);
			ButtonStatistics.setBorder(null);
			ButtonStatistics.addMouseListener(this);
			ButtonStatistics.setBackground(new Color(208, 45, 26));
			ButtonStatistics.setLayout(null);

			IndicatorStatistics = new JPanel();
			IndicatorStatistics.setBounds(0, 0, 10, 56);
			IndicatorStatistics.setForeground(Color.WHITE);
			IndicatorStatistics.setBorder(null);
			IndicatorStatistics.setBackground(Color.WHITE);
			IndicatorStatistics.setLayout(null);

			lblStatistics = new JLabel();
			lblStatistics.setBounds(10, 0, 230, 56);
			lblStatistics.setText("    Statistics");
			lblStatistics.setForeground(Color.WHITE);
			lblStatistics.setFont(new Font("Segoe UI", Font.BOLD, 14));
			lblStatistics.addMouseListener(this);

			ButtonStatistics.add(IndicatorStatistics);
			ButtonStatistics.add(lblStatistics);
			menuPanel.add(ButtonStatistics);

			// add extra elements if application account
			if (user.getApp()) {
				// add tab Dashboard
				dashboard = new Dashboard(gitlabApi);
				dashboard.setBounds(252, 0, 884, 634);
				globalpanel.add(dashboard, "dashboard");

				// add button Dashboard in menuPanel
				ButtonDashboard = new JPanel();
				ButtonDashboard.setBounds(10, 352, 240, 56);
				ButtonDashboard.setBorder(null);
				ButtonDashboard.addMouseListener(this);
				ButtonDashboard.setBackground(new Color(208, 45, 26));
				ButtonDashboard.setLayout(null);

				IndicatorDashboard = new JPanel();
				IndicatorDashboard.setBounds(0, 0, 10, 56);
				IndicatorDashboard.setForeground(Color.WHITE);
				IndicatorDashboard.setBorder(null);
				IndicatorDashboard.setBackground(Color.WHITE);
				IndicatorDashboard.setLayout(null);

				lblDashboard = new JLabel();
				lblDashboard.setBounds(10, 0, 230, 56);
				lblDashboard.setText("    Dashboard");
				lblDashboard.setForeground(Color.WHITE);
				lblDashboard.setFont(new Font("Segoe UI", Font.BOLD, 14));
				lblDashboard.addMouseListener(this);

				ButtonDashboard.add(IndicatorDashboard);
				ButtonDashboard.add(lblDashboard);
				menuPanel.add(ButtonDashboard);

				// add tab Settings
				settings = new Settings();
				settings.setBounds(252, 0, 884, 634);
				globalpanel.add(settings, "settings");

				// add button Settings in menuPanel
				ButtonSettings = new JPanel();
				ButtonSettings.setBounds(10, 487, 240, 56);
				ButtonSettings.setBorder(null);
				ButtonSettings.addMouseListener(this);
				ButtonSettings.setBackground(new Color(208, 45, 26));
				ButtonSettings.setLayout(null);

				IndicatorSettings = new JPanel();
				IndicatorSettings.setBounds(0, 0, 10, 56);
				IndicatorSettings.setForeground(Color.WHITE);
				IndicatorSettings.setBorder(null);
				IndicatorSettings.setBackground(Color.WHITE);
				IndicatorSettings.setLayout(null);

				lblSettings = new JLabel();
				lblSettings.setBounds(10, 0, 230, 56);
				lblSettings.setText("    Settings");
				lblSettings.setForeground(Color.WHITE);
				lblSettings.setFont(new Font("Segoe UI", Font.BOLD, 14));
				lblSettings.addMouseListener(this);

				ButtonSettings.add(IndicatorSettings);
				ButtonSettings.add(lblSettings);
				menuPanel.add(ButtonSettings);

				// change location of the statistics button
				ButtonStatistics.setBounds(10, 420, 240, 56);
			}

			// show Home tab
			CardLayout cl = (CardLayout) (globalpanel.getLayout());
			cl.show(globalpanel, "home");
			currentPanel = "home";
		}

	}

	/**
	 * Method to change background and foreground color of element to black
	 * 
	 * @param panel
	 * @param label
	 */
	private void onHover(JPanel panel, JLabel label) {
		panel.setBackground(new Color(0, 0, 0));
		panel.setForeground(new Color(0, 0, 0));
		label.setForeground(new Color(0, 0, 0));
	}

	/**
	 * Method to change background and foreground color of element to white
	 * 
	 * @param panel
	 * @param label
	 */
	private void onleaveHover(JPanel panel, JLabel label) {
		panel.setBackground(new Color(255, 255, 255));
		label.setForeground(new Color(255, 255, 255));
	}

	/**
	 * Method to change displayed panel when user clicks on the tab
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// if account is of type user
		if (user.getIsUser()) {
			// if application account
			if (user.getApp()) {
				// different tabs testing
				if (e.getSource() == ButtonHome || e.getSource() == lblHome) {
					home.refreshAction();
					CardLayout cl = (CardLayout) (globalpanel.getLayout());
					cl.show(globalpanel, "home"); // show corresponding panel
					currentPanel = "home"; // update value
				} else if (e.getSource() == ButtonDashboard || e.getSource() == lblDashboard) {
					dashboard.updatePanels();
					CardLayout cl = (CardLayout) (globalpanel.getLayout());
					cl.show(globalpanel, "dashboard");
					currentPanel = "dashboard";
				} else if (e.getSource() == ButtonStatistics || e.getSource() == lblStatistics) {
					statistics.update();
					CardLayout cl = (CardLayout) (globalpanel.getLayout());
					cl.show(globalpanel, "statistics");
					currentPanel = "statistics";
				} else if (e.getSource() == ButtonSettings || e.getSource() == lblSettings) {
					CardLayout cl = (CardLayout) (globalpanel.getLayout());
					cl.show(globalpanel, "settings");
					currentPanel = "settings";
				}
			}
			// else Gitlab account
			else {
				if (e.getSource() == ButtonHome || e.getSource() == lblHome) {
					home.refreshAction();
					CardLayout cl = (CardLayout) (globalpanel.getLayout());
					cl.show(globalpanel, "home");
					currentPanel = "home";
				} else if (e.getSource() == ButtonStatistics || e.getSource() == lblStatistics) {
					statistics.update();
					CardLayout cl = (CardLayout) (globalpanel.getLayout());
					cl.show(globalpanel, "statistics");
					currentPanel = "statistics";
				}
			}
		}

	}

	/**
	 * Method to execute actions when mouse hovers above a tab
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// if account is of type User
		if (user.getIsUser()) {
			// if application account
			if (user.getApp()) {
				// if mouse above home tab
				if (e.getSource() == ButtonHome || e.getSource() == lblHome) {
					// set home tab elements to black
					onHover(IndicatorHome, lblHome);
					// set other tab elements to white
					onleaveHover(IndicatorStatistics, lblStatistics);
					onleaveHover(IndicatorDashboard, lblDashboard);
					onleaveHover(IndicatorSettings, lblSettings);
				} else if (e.getSource() == ButtonDashboard || e.getSource() == lblDashboard) {
					onHover(IndicatorDashboard, lblDashboard);
					onleaveHover(IndicatorHome, lblHome);
					onleaveHover(IndicatorStatistics, lblStatistics);
					onleaveHover(IndicatorSettings, lblSettings);
				} else if (e.getSource() == ButtonStatistics || e.getSource() == lblStatistics) {
					onHover(IndicatorStatistics, lblStatistics);
					onleaveHover(IndicatorDashboard, lblDashboard);
					onleaveHover(IndicatorHome, lblHome);
					onleaveHover(IndicatorSettings, lblSettings);
				} else if (e.getSource() == ButtonSettings || e.getSource() == lblSettings) {
					onHover(IndicatorSettings, lblSettings);
					onleaveHover(IndicatorHome, lblHome);
					onleaveHover(IndicatorDashboard, lblDashboard);
					onleaveHover(IndicatorStatistics, lblStatistics);
				}
			}
			// else Gitlab account
			else {
				if (e.getSource() == ButtonHome || e.getSource() == lblHome) {
					onHover(IndicatorHome, lblHome);
					onleaveHover(IndicatorStatistics, lblStatistics);
				} else if (e.getSource() == ButtonStatistics || e.getSource() == lblStatistics) {
					onHover(IndicatorStatistics, lblStatistics);
					onleaveHover(IndicatorHome, lblHome);
				}
			}
		}

		// if mouse is above the logout button
		if (e.getSource() == btnLogout) {
			btnLogout.setForeground(Color.BLACK); // label becomes black
		}

	}

	/**
	 * Method to execute actions when mouse leaves a tab
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// if account is of type user
		if (user.getIsUser()) {
			// if application account
			if (user.getApp()) {
				if (currentPanel == "home") {
					// set current pannel's tab elements to black
					onHover(IndicatorHome, lblHome);
					// other tab elements to white
					onleaveHover(IndicatorStatistics, lblStatistics);
					onleaveHover(IndicatorDashboard, lblDashboard);
					onleaveHover(IndicatorSettings, lblSettings);
				} else if (currentPanel == "dashboard") {
					// set current pannel's tab elements to black
					onHover(IndicatorDashboard, lblDashboard);
					// other tab elements to white
					onleaveHover(IndicatorHome, lblHome);
					onleaveHover(IndicatorStatistics, lblStatistics);
					onleaveHover(IndicatorSettings, lblSettings);
				} else if (currentPanel == "statistics") {
					// set current pannel's tab elements to black
					onHover(IndicatorStatistics, lblStatistics);
					// other tab elements to white
					onleaveHover(IndicatorDashboard, lblDashboard);
					onleaveHover(IndicatorHome, lblHome);
					onleaveHover(IndicatorSettings, lblSettings);
				} else if (currentPanel == "settings") {
					// set current pannel's tab elements to black
					onHover(IndicatorSettings, lblSettings);
					// other tab elements to white
					onleaveHover(IndicatorHome, lblHome);
					onleaveHover(IndicatorDashboard, lblDashboard);
					onleaveHover(IndicatorStatistics, lblStatistics);
				}
			}
			// else GitLab account
			else {
				if (currentPanel == "home") {
					// set current pannel's tab elements to black
					onHover(IndicatorHome, lblHome);
					// set other tab elements to white
					onleaveHover(IndicatorStatistics, lblStatistics);
				} else if (currentPanel == "statistics") {
					// set current pannel's tab elements to black
					onHover(IndicatorStatistics, lblStatistics);
					// set other tab elements to white
					onleaveHover(IndicatorHome, lblHome);
				}
			}
		}

		// if the mouse exits the Logout button zone
		if (e.getSource() == btnLogout) {
			btnLogout.setForeground(Color.WHITE); // label is back to white
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * Method to execute action when button pressed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// when button logout is pressed
		if (e.getSource() == btnLogout) {
			user.setEmpty(); // empty member values
			new OpeningLayout(); // call a new Opening Layout
			this.dispose(); // dispose actual JFrame
		} else if (e.getSource() == btnHelp) {
			if (Desktop.isDesktopSupported()) {
				try {
					File myFile = new File("./Documentation_utilisateur_groupe5.pdf");
					Desktop.getDesktop().open(myFile);
				} catch (IOException ex) {
					// no application registered for PDFs
					JOptionPane.showMessageDialog(globalpanel, "No registered application found to open PDF",
							"Opening PDF Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

	}
}
