package app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;

import bdd.DatabaseApp;

/**
 * @author Kim-Celine FRANOT
 *
 */
public class Settings extends JPanel implements ActionListener, ItemListener, FocusListener, DocumentListener {

	// components of the panel
	private JPanel mainPanel;
	private JPanel panelCreate;
	private JPanel panelPassword;
	private JPanel panelToken;

	private JLabel lblCreateAccount;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JLabel lblToken;
	private JTextField textFieldUsername;
	private JPasswordField passwordField;
	private JTextField textFieldToken;
	private JCheckBox chckbxShowPwd;
	private JButton btnCreate;

	private JLabel lblChangeToken;
	private JLabel lblUsernameToken;
	private JLabel lblPasswordToken;
	private JLabel lblNewToken;
	private JTextField textFieldUsernameToken;
	private JPasswordField passwordFieldToken;
	private JTextField textFieldNewToken;
	private JButton btnChangeToken;

	private JLabel lblChangePassword;
	private JLabel lblCurrent;
	private JLabel lblNew;
	private JLabel lblConfirm;
	private JPasswordField passwordFieldChangeCurrent;
	private JPasswordField passwordFieldChangeNew;
	private JPasswordField passwordFieldChangeConfirm;
	private JButton btnChangePassword;

	private User user = User.getInstance();
	private DatabaseApp bdd;

	/**
	 * Create the panel.
	 */
	public Settings() {
		setVisible(true);
		initComponents();

		try {
			bdd = new DatabaseApp();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void initComponents() {
		setLayout(null);

		mainPanel = new JPanel();
		mainPanel.setBackground(new Color(153, 204, 255));
		mainPanel.setBounds(0, 0, 884, 634);
		add(mainPanel);
		mainPanel.setLayout(null);

		// Create Account

		panelCreate = new JPanel();
		panelCreate.setBackground(new Color(153, 204, 255));
		panelCreate.setBounds(10, 10, 421, 613);
		panelCreate.setBorder(new MatteBorder(0, 0, 0, 2, (Color) Color.GRAY));
		mainPanel.add(panelCreate);
		panelCreate.setLayout(null);

		lblCreateAccount = new JLabel("Create a new admin account");
		lblCreateAccount.setHorizontalAlignment(SwingConstants.LEFT);
		lblCreateAccount.setForeground(new Color(41, 41, 97));
		lblCreateAccount.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblCreateAccount.setBounds(10, 182, 352, 32);
		panelCreate.add(lblCreateAccount);

		lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblUsername.setBounds(10, 227, 85, 19);
		panelCreate.add(lblUsername);

		lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblPassword.setBounds(10, 260, 85, 19);
		panelCreate.add(lblPassword);

		lblToken = new JLabel("Token");
		lblToken.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblToken.setBounds(10, 295, 85, 19);
		panelCreate.add(lblToken);

		textFieldUsername = new JTextField();
		textFieldUsername.setMargin(new Insets(0, 5, 0, 0));
		textFieldUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textFieldUsername.setColumns(10);
		textFieldUsername.addFocusListener(this);
		textFieldUsername.setBounds(107, 224, 220, 24);
		textFieldUsername.setMargin(new Insets(0, 5, 0, 0));
		textFieldUsername.getDocument().addDocumentListener(this);
		panelCreate.add(textFieldUsername);

		passwordField = new JPasswordField();
		passwordField.setMargin(new Insets(0, 5, 0, 0));
		passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		passwordField.setBounds(107, 258, 220, 24);
		passwordField.setMargin(new Insets(0, 5, 0, 0));
		passwordField.addFocusListener(this);
		passwordField.getDocument().addDocumentListener(this);
		panelCreate.add(passwordField);

		textFieldToken = new JTextField();
		textFieldToken.setText("");
		textFieldToken.setMargin(new Insets(0, 5, 0, 0));
		textFieldToken.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textFieldToken.setColumns(10);
		textFieldToken.setBounds(107, 292, 220, 24);
		textFieldToken.setMargin(new Insets(0, 5, 0, 0));
		textFieldToken.addFocusListener(this);
		textFieldToken.getDocument().addDocumentListener(this);
		panelCreate.add(textFieldToken);

		chckbxShowPwd = new JCheckBox("Show");
		chckbxShowPwd.setOpaque(false);
		chckbxShowPwd.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		chckbxShowPwd.setBounds(342, 260, 68, 21);
		chckbxShowPwd.addItemListener(this);
		panelCreate.add(chckbxShowPwd);

		btnCreate = new JButton("Create");
		btnCreate.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnCreate.setBackground(new Color(255, 153, 51));
		btnCreate.setBounds(107, 340, 220, 32);
		btnCreate.addActionListener(this);
		btnCreate.setEnabled(false);
		panelCreate.add(btnCreate);

		// Change Password

		panelPassword = new JPanel();
		panelPassword.setBorder(new MatteBorder(0, 0, 2, 0, (Color) Color.GRAY));
		panelPassword.setBackground(new Color(153, 204, 255));
		panelPassword.setBounds(431, 10, 432, 282);
		mainPanel.add(panelPassword);
		panelPassword.setLayout(null);

		lblChangePassword = new JLabel("Change password");
		lblChangePassword.setHorizontalAlignment(SwingConstants.LEFT);
		lblChangePassword.setForeground(new Color(41, 41, 97));
		lblChangePassword.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblChangePassword.setBounds(44, 28, 352, 32);
		panelPassword.add(lblChangePassword);

		lblCurrent = new JLabel("Current");
		lblCurrent.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblCurrent.setBounds(44, 78, 85, 19);
		panelPassword.add(lblCurrent);

		lblNew = new JLabel("New");
		lblNew.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblNew.setBounds(44, 111, 85, 19);
		panelPassword.add(lblNew);

		lblConfirm = new JLabel(" Confirm new");
		lblConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblConfirm.setBounds(40, 145, 103, 19);
		panelPassword.add(lblConfirm);

		passwordFieldChangeCurrent = new JPasswordField();
		passwordFieldChangeCurrent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		passwordFieldChangeCurrent.setBounds(146, 75, 220, 24);
		passwordFieldChangeCurrent.setMargin(new Insets(0, 5, 0, 0));
		passwordFieldChangeCurrent.getDocument().addDocumentListener(this);
		passwordFieldChangeCurrent.addFocusListener(this);
		panelPassword.add(passwordFieldChangeCurrent);

		passwordFieldChangeNew = new JPasswordField();
		passwordFieldChangeNew.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		passwordFieldChangeNew.setBounds(146, 108, 220, 24);
		passwordFieldChangeNew.setMargin(new Insets(0, 5, 0, 0));
		passwordFieldChangeNew.getDocument().addDocumentListener(this);
		passwordFieldChangeNew.addFocusListener(this);
		panelPassword.add(passwordFieldChangeNew);

		passwordFieldChangeConfirm = new JPasswordField();
		passwordFieldChangeConfirm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		passwordFieldChangeConfirm.setBounds(146, 143, 220, 24);
		passwordFieldChangeConfirm.setMargin(new Insets(0, 5, 0, 0));
		passwordFieldChangeConfirm.getDocument().addDocumentListener(this);
		passwordFieldChangeConfirm.addFocusListener(this);
		panelPassword.add(passwordFieldChangeConfirm);

		btnChangePassword = new JButton("Change Password");
		btnChangePassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnChangePassword.setBackground(new Color(255, 153, 51));
		btnChangePassword.setBounds(146, 194, 220, 32);
		btnChangePassword.addActionListener(this);
		btnChangePassword.setEnabled(false);
		panelPassword.add(btnChangePassword);

		// Change Token

		panelToken = new JPanel();
		panelToken.setBackground(new Color(153, 204, 255));
		panelToken.setBounds(431, 295, 443, 328);
		mainPanel.add(panelToken);
		panelToken.setLayout(null);

		lblChangeToken = new JLabel("Change token");
		lblChangeToken.setHorizontalAlignment(SwingConstants.LEFT);
		lblChangeToken.setForeground(new Color(41, 41, 97));
		lblChangeToken.setFont(new Font("Segoe UI", Font.BOLD, 24));
		lblChangeToken.setBounds(40, 52, 352, 32);
		panelToken.add(lblChangeToken);

		lblUsernameToken = new JLabel("Username");
		lblUsernameToken.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblUsernameToken.setBounds(41, 113, 85, 19);
		panelToken.add(lblUsernameToken);

		lblPasswordToken = new JLabel("Password");
		lblPasswordToken.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblPasswordToken.setBounds(41, 148, 85, 19);
		panelToken.add(lblPasswordToken);

		lblNewToken = new JLabel("New token");
		lblNewToken.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblNewToken.setBounds(41, 184, 85, 19);
		panelToken.add(lblNewToken);

		textFieldUsernameToken = new JTextField();
		textFieldUsernameToken.setMargin(new Insets(0, 5, 0, 0));
		textFieldUsernameToken.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textFieldUsernameToken.setColumns(10);
		textFieldUsernameToken.setBounds(142, 108, 220, 24);
		textFieldUsernameToken.setMargin(new Insets(0, 5, 0, 0));
		textFieldUsernameToken.getDocument().addDocumentListener(this);
		textFieldUsernameToken.addFocusListener(this);
		panelToken.add(textFieldUsernameToken);

		passwordFieldToken = new JPasswordField();
		passwordFieldToken.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		passwordFieldToken.setBounds(142, 143, 220, 24);
		passwordFieldToken.setMargin(new Insets(0, 5, 0, 0));
		passwordFieldToken.getDocument().addDocumentListener(this);
		passwordFieldToken.addFocusListener(this);
		panelToken.add(passwordFieldToken);

		textFieldNewToken = new JTextField();
		textFieldNewToken.setText("");
		textFieldNewToken.setMargin(new Insets(0, 5, 0, 0));
		textFieldNewToken.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		textFieldNewToken.setColumns(10);
		textFieldNewToken.setBounds(142, 179, 220, 24);
		textFieldNewToken.setMargin(new Insets(0, 5, 0, 0));
		textFieldNewToken.getDocument().addDocumentListener(this);
		textFieldNewToken.addFocusListener(this);
		panelToken.add(textFieldNewToken);

		btnChangeToken = new JButton("Change Token");
		btnChangeToken.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		btnChangeToken.setBackground(new Color(255, 153, 51));
		btnChangeToken.setBounds(142, 224, 220, 32);
		btnChangeToken.addActionListener(this);
		btnChangeToken.setEnabled(false);
		panelToken.add(btnChangeToken);
	}

	/**
	 * Method to perform action according to button clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCreate) {
			createAction();
		} else if (e.getSource() == btnChangePassword) {
			changePasswordAction();
		} else if (e.getSource() == btnChangeToken) {
			changeTokenAction();
		}
	}

	/**
	 * Method to change password visualisation when checkbox is ticked/unticked
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			passwordField.setEchoChar((char) 0);
		} else {
			passwordField.setEchoChar('\u2022');
		}

	}

	/**
	 * Method to select content of field when focus gained
	 */
	@Override
	public void focusGained(FocusEvent e) {
		JTextField src = (JTextField) e.getSource();
		src.selectAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
	}

	/**
	 * Method to create new admin user
	 * 
	 * @throws Exception
	 */
	public void createAction() {
		// get values from fields
		String username = textFieldUsername.getText().trim();
		String password = String.valueOf(passwordField.getPassword()).trim();
		String token = textFieldToken.getText().trim();

		// check if token is valid
		GitLabApi git = new GitLabApi("https://code.telecomste.fr", token);
		try {
			List<Project> p = git.getProjectApi().getOwnedProjects();
			// add user in database
			bdd.addUser(username, password, token);
			// show information message of success
			JOptionPane.showMessageDialog(mainPanel, "The account for the username " + username + " has been created",
					"Create New User", JOptionPane.INFORMATION_MESSAGE);
			// empty fields
			textFieldUsername.setText("");
			passwordField.setText("");
			textFieldToken.setText("");
		} catch (GitLabApiException e) {
			// show error message that token is not valid
			JOptionPane.showMessageDialog(mainPanel, "Gitlab token not recognized. Please provide a valid token",
					"Check Token Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(mainPanel, "Username already exists. Please choose another one",
					"Create User Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Method to change password of current connected user
	 */
	public void changePasswordAction() {
		// get values from fields
		String currentPW = String.valueOf(passwordFieldChangeCurrent.getPassword()).trim();
		String newPW = String.valueOf(passwordFieldChangeNew.getPassword()).trim();
		String confirmPW = String.valueOf(passwordFieldChangeConfirm.getPassword()).trim();

		// TODO check if current password equals the one in database
		boolean userOk = bdd.verificationLogin(user.getDisplayName(), currentPW);
		if (userOk) {
			// if passwords are equal
			if (newPW.equals(confirmPW)) {
				// and if password is different from current one
				if (!newPW.equals(currentPW)) {
					// update databaFse
					bdd.updatePassword(user.getDisplayName(), newPW);

					// show information window of success
					JOptionPane.showMessageDialog(mainPanel, "Password has been updated", "Change Password",
							JOptionPane.INFORMATION_MESSAGE);
					// empty fields
					passwordFieldChangeCurrent.setText("");
					passwordFieldChangeNew.setText("");
					passwordFieldChangeConfirm.setText("");
				} else {
					// show error message that new password must be different from previous one
					JOptionPane.showMessageDialog(mainPanel, "New password must me different from current one",
							"Change Password Error", JOptionPane.ERROR_MESSAGE);

					// empty fields
					passwordFieldChangeNew.setText("");
					passwordFieldChangeConfirm.setText("");
				}

			} else {
				// show error message that passwords are not equal
				JOptionPane.showMessageDialog(mainPanel, "New password does not match", "Change Password Error",
						JOptionPane.ERROR_MESSAGE);
				// empty fields
				passwordFieldChangeNew.setText("");
				passwordFieldChangeConfirm.setText("");
			}
		} else {
			// show error message that current password is wrong
			JOptionPane.showMessageDialog(mainPanel,
					"Current password does not match for user " + user.getDisplayName(), "Change Password Error",
					JOptionPane.ERROR_MESSAGE);
			// empty fields
			passwordFieldChangeCurrent.setText("");
		}

	}

	/**
	 * Method to change token of a user
	 */
	public void changeTokenAction() {
		// get values from fields
		String username = textFieldUsernameToken.getText().trim();
		String password = String.valueOf(passwordFieldToken.getPassword()).trim();
		String token = textFieldNewToken.getText().trim();

		boolean userOk = bdd.verificationLogin(username, password);
		if (userOk) {
			// check if token is valid
			GitLabApi git = new GitLabApi("https://code.telecomste.fr", token);
			try {
				List<Project> p = git.getProjectApi().getOwnedProjects();

				// update database
				bdd.updateToken(username, token);
				// show information dialog of success
				JOptionPane.showMessageDialog(mainPanel, "The token for the username " + username + " has been updated",
						"Change Token", JOptionPane.INFORMATION_MESSAGE);

				// empty fields
				textFieldUsernameToken.setText("");
				passwordFieldToken.setText("");
				textFieldNewToken.setText("");
			} catch (GitLabApiException e1) {
				// if token is invalid show error window
				JOptionPane.showMessageDialog(mainPanel, "Gitlab token not recognized. Please provide a valid token",
						"Change Token Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			// show error message that account was not recognized
			JOptionPane.showMessageDialog(mainPanel, "Credentials do not match any account. Please try again",
					"Authentication Error", JOptionPane.ERROR_MESSAGE);
			// empty fields
			textFieldUsernameToken.setText("");
			passwordFieldToken.setText("");
		}

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
	 * Method to enable button once all fields linked to the button are filled
	 */
	public void enableButton() {
		if (!textFieldUsername.getText().trim().isEmpty() && !textFieldToken.getText().trim().isEmpty()
				&& !String.valueOf(passwordField.getPassword()).trim().isEmpty()) {
			btnCreate.setEnabled(true);
		} else {
			btnCreate.setEnabled(false);
		}
		if (!String.valueOf(passwordFieldChangeCurrent.getPassword()).trim().isEmpty()
				&& !String.valueOf(passwordFieldChangeNew.getPassword()).trim().isEmpty()
				&& !String.valueOf(passwordFieldChangeConfirm.getPassword()).trim().isEmpty()) {
			btnChangePassword.setEnabled(true);
		} else {
			btnChangePassword.setEnabled(false);
		}

		if (!textFieldUsernameToken.getText().trim().isEmpty() && !textFieldNewToken.getText().trim().isEmpty()
				&& !String.valueOf(passwordFieldToken.getPassword()).trim().isEmpty()) {
			btnChangeToken.setEnabled(true);
		} else {
			btnChangeToken.setEnabled(false);
		}

	}
}
