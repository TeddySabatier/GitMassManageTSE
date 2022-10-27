package app;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;

import bdd.DatabaseApp;

/**
 * @author Kim-Celine FRANOT
 *
 */
public class OpeningLayout extends JFrame
		implements ItemListener, ActionListener, FocusListener, DocumentListener, KeyListener {

	// components of the frame
	private JCheckBox chckbxShowPassword;
	private JPanel loginPanel;
	private JPanel panel;
	private JLabel lblUsernameLogin;
	private JLabel lblPasswordLogin;
	private JLabel lblTitleLogin;
	private JLabel lblConnexionOption;
	private JTextField textFieldUsernameLogin;
	private JPasswordField passwordFieldLogin;
	private JRadioButton rdbtnAppConnexion;
	private JRadioButton rdbtnGitConnexion;
	private ButtonGroup bgConnexionOption;
	private JButton btnLogin;

	// private variables
	private User userInstance = User.getInstance();
	private DatabaseApp bdd;

	/**
	 * Launch the application.
	 * 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OpeningLayout frame = new OpeningLayout();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public OpeningLayout() {
		setTitle("GIT TSE");
		setResizable(false);
		setIconImage(new ImageIcon("./src/main/java/assets/icons/gitlab.png").getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 743, 555);

		initializeLoginPanel();

		try {
			bdd = new DatabaseApp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setVisible(true);
		setLocationRelativeTo(null);
	}

	public void initializeLoginPanel() {
		loginPanel = new JPanel();
		loginPanel.setLocation(0, 0);
		loginPanel.setSize(739, 530);
		loginPanel.setLayout(null);
		setContentPane(loginPanel);

		panel = new JPanel();
		panel.setBackground(new Color(0, 0, 51));
		panel.setBounds(0, 0, 206, 530);
		panel.setLayout(null);
		loginPanel.add(panel);

		lblUsernameLogin = new JLabel("USERNAME");
		lblUsernameLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblUsernameLogin.setBounds(260, 116, 103, 28);
		loginPanel.add(lblUsernameLogin);

		textFieldUsernameLogin = new JTextField();
		textFieldUsernameLogin.setMargin(new Insets(0, 5, 0, 0));
		textFieldUsernameLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		textFieldUsernameLogin.setBounds(260, 154, 303, 35);
		textFieldUsernameLogin.setColumns(10);
		textFieldUsernameLogin.addFocusListener(this);
		textFieldUsernameLogin.getDocument().addDocumentListener(this);
		loginPanel.add(textFieldUsernameLogin);

		lblPasswordLogin = new JLabel("PASSWORD");
		lblPasswordLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblPasswordLogin.setBounds(260, 209, 113, 28);
		loginPanel.add(lblPasswordLogin);

		passwordFieldLogin = new JPasswordField();
		passwordFieldLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		passwordFieldLogin.setMargin(new Insets(0, 5, 0, 0));
		passwordFieldLogin.setBounds(260, 247, 303, 35);
		passwordFieldLogin.addFocusListener(this);
		passwordFieldLogin.getDocument().addDocumentListener(this);
		passwordFieldLogin.addKeyListener(this);
		loginPanel.add(passwordFieldLogin);

		btnLogin = new JButton("Login");
		btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnLogin.setBackground(new Color(255, 153, 51));
		btnLogin.setBounds(260, 377, 303, 35);
		btnLogin.setBorder(BorderFactory.createEmptyBorder());
		btnLogin.addActionListener(this);
		btnLogin.setEnabled(false);
		loginPanel.add(btnLogin);

		lblTitleLogin = new JLabel("Login");
		lblTitleLogin.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblTitleLogin.setBounds(260, 60, 206, 35);
		loginPanel.add(lblTitleLogin);

		chckbxShowPassword = new JCheckBox("Show");
		chckbxShowPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		chckbxShowPassword.setBounds(569, 256, 64, 21);
		chckbxShowPassword.addItemListener(this);
		loginPanel.add(chckbxShowPassword);

		rdbtnAppConnexion = new JRadioButton("Application");
		rdbtnAppConnexion.setSelected(true);
		rdbtnAppConnexion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		rdbtnAppConnexion.setBounds(260, 328, 131, 21);
		loginPanel.add(rdbtnAppConnexion);

		rdbtnGitConnexion = new JRadioButton("GitLab");
		rdbtnGitConnexion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		rdbtnGitConnexion.setBounds(443, 328, 103, 21);
		loginPanel.add(rdbtnGitConnexion);

		bgConnexionOption = new ButtonGroup();
		bgConnexionOption.add(rdbtnAppConnexion);
		bgConnexionOption.add(rdbtnGitConnexion);

		lblConnexionOption = new JLabel("Connection via: ");
		lblConnexionOption.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblConnexionOption.setBounds(260, 309, 155, 13);
		loginPanel.add(lblConnexionOption);
	}

	/**
	 * Method to login into application according to details entered
	 */
	public void loginAction() {
		GitLabApi gitLabApi;
		// get values from fields
		String username = textFieldUsernameLogin.getText();
		String password = String.valueOf(passwordFieldLogin.getPassword());
		// Connexion through application
		if (rdbtnAppConnexion.isSelected()) {
			// check if user exists in database
			boolean userOk = bdd.verificationLogin(username, password);
			if (userOk) {

				if (textFieldUsernameLogin.getText().equals("admin")) {
					// set members
					userInstance.setDisplayName(username);
					userInstance.setIsUser(false); // account if of type admin => false
				} else {
					// Get the token of the user
					String token = bdd.getToken(username);
					gitLabApi = new GitLabApi("https://code.telecomste.fr", token);

					// set members
					userInstance.setDisplayName(username);
					userInstance.setToken(token);
					userInstance.setGitLabApi(gitLabApi);
					userInstance.setApp(true); // connection through application => true
					userInstance.setIsUser(true); // account is of type user => true
				}

				// open application
				this.setVisible(false);
				Menu main = new Menu();

			} else {
				// show error message if account not recognized/found in database
				JOptionPane.showMessageDialog(loginPanel, "Credentials not recognized", "Authentication Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			// Connexion through GitLab
			try {
				gitLabApi = GitLabApi.oauth2Login("https://code.telecomste.fr", username, password);
				// set members
				userInstance.setDisplayName(username);
				userInstance.setUsername(username);
				userInstance.setPassword(password);
				userInstance.setGitLabApi(gitLabApi);
				userInstance.setApp(false); // connection is through git => false
				userInstance.setIsUser(true); // account is of type user => true

				// open application
				this.setVisible(false);
				Menu main = new Menu();

			} catch (GitLabApiException e) {
				// show error message if gitlab authentication failed
				System.out.println("Username or password incorrect");
				JOptionPane.showMessageDialog(loginPanel, "Gitlab credentials not recognized", "Authentication Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Method to change password visualisation on check/uncheck of checkbox
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			passwordFieldLogin.setEchoChar((char) 0);
		} else {
			passwordFieldLogin.setEchoChar('\u2022');
		}
	}

	/**
	 * Method to execute action on button click
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnLogin) {
			loginAction();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
	}

	/**
	 * Method to select field content on focus
	 */
	@Override
	public void focusGained(FocusEvent e) {
		JTextField src = (JTextField) e.getSource();
		src.selectAll();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		enableButton();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		enableButton();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		enableButton();
	}

	/**
	 * Method to enable login button once all fields are filled
	 */
	public void enableButton() {
		if (!textFieldUsernameLogin.getText().trim().isEmpty()
				&& !String.valueOf(passwordFieldLogin.getPassword()).trim().isEmpty()) {
			btnLogin.setEnabled(true);
		} else {
			btnLogin.setEnabled(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!textFieldUsernameLogin.getText().trim().isEmpty()
				&& !String.valueOf(passwordFieldLogin.getPassword()).trim().isEmpty()
				&& e.getKeyCode() == KeyEvent.VK_ENTER) {
			btnLogin.doClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
