package ComLog;

import ComLog.States;
import ComLog.User;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.border.CompoundBorder;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;

public class Login extends ComLog {

	private JPanel loginPane = new JPanel(), // main container, holds 'select
												// user' and 'new user'
												// interface
			selectUserPane = new JPanel(), // interface to choose user
			newUserFrame = new JPanel(); // register new user interface

	private JPasswordField newUserPassword; // get password of new user
	private JPasswordField confirmPassword; // make sure the new password is
											// correct
	private JTextField newUserName; // get new user name
	private JComboBox<String> userComboBox; // hold list of available users

	private String state; // hold state of panel (toggle display)
	private ComLog mainWin; // reference to parent object
	private String[] users = Util.readFileToArray(Util.USER_LOG); // list user
																	// names

	/* Construct */
	public Login() {
		/* build user select interface */
		buildSelectUserPanel();
		/* build new user interface */
		buildNewUserPanel();

		/* add components to main container */
		loginPane.add(selectUserPane);
		loginPane.add(newUserFrame);

	}

	/* set main window object reference */
	public void setParent(ComLog w) {
		mainWin = w;
	}

	/* center panel gets component to add */
	public JPanel getComponentFrame() {
		return loginPane;
	}

	private void buildSelectUserPanel() {

		// build combo box
		userComboBox = new JComboBox<String>(users); // put users in combo box

		// limit number of users shown (limit size of popup box on screen)
		userComboBox.setMaximumRowCount(6);

		// set command to find in action listener
		userComboBox.setActionCommand("User List Select");

		// title
		JPanel titleBox = new JPanel();
		JLabel title = new JLabel(
				"LSAC: Secure Asynchronious Communication Log");
		title.setHorizontalAlignment(JLabel.CENTER);
		titleBox.setLayout(new GridLayout(0, 1));

		titleBox.add(title);
		titleBox.setPreferredSize(new Dimension(305, 75));
		CompoundBorder titleBorder = new CompoundBorder(
				BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createRaisedBevelBorder());
		titleBox.setBorder(titleBorder);

		// add to scroll pane
		JScrollPane userScrollPane = new JScrollPane(userComboBox);

		// build information label
		JLabel userLabel = new JLabel("User:");
		userLabel.setHorizontalAlignment(JLabel.CENTER);
		// build component panel
		JPanel userPanel = new JPanel();
		/* add to container */
		userPanel.add(userLabel);
		userPanel.add(userScrollPane);
		userPanel.setPreferredSize(new Dimension(305, 75));
		userPanel.setAlignmentY(JLabel.CENTER);

		/* layout of 'select user' interface */
		selectUserPane.setLayout(new GridLayout(0, 1));
		selectUserPane.add(new JLabel(""));
		selectUserPane.add(new JLabel(""));
		selectUserPane.add(titleBox);
		selectUserPane.add(userPanel);

		// build login button
		JButton goButton = new JButton("Go");
		goButton.setHorizontalAlignment(JLabel.CENTER);
		goButton.setActionCommand("Login");

		// listener to determine action
		goButton.addActionListener(new loginListener());
		// panel to hold login button (preserve button sizing)
		JPanel holdPanel = new JPanel();
		holdPanel.add(new JLabel(""));
		holdPanel.add(goButton);

		selectUserPane.add(holdPanel);
		selectUserPane.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		selectUserPane.setAlignmentY(JComponent.CENTER_ALIGNMENT);

	}

	private void buildNewUserPanel() {

		JLabel newUserLabel = new JLabel("Register New User",
				SwingConstants.CENTER);

		// user name
		JLabel userNameLabel = new JLabel("User Name: ");
		newUserName = new JTextField();

		// user pass
		JLabel userPassLabel = new JLabel("Enter Password");
		newUserPassword = new JPasswordField();
		// confirm password
		JLabel confirmPass = new JLabel("Confirm Password");
		confirmPassword = new JPasswordField();

		// hold input components for layout
		JPanel newUserInputPanel = new JPanel();
		newUserInputPanel.setLayout(new GridLayout(3, 2));
		JLabel title = new JLabel("", SwingConstants.CENTER);
		title.setPreferredSize(new Dimension(50, 50));
		newUserInputPanel.add(userNameLabel);
		newUserInputPanel.add(newUserName);
		newUserInputPanel.add(userPassLabel);
		newUserInputPanel.add(newUserPassword);
		newUserInputPanel.add(confirmPass);
		newUserInputPanel.add(confirmPassword);

		JPanel newUserButtonPanel = new JPanel();
		// add a user
		JButton newUserAddButton = new JButton("Register");
		newUserAddButton.setActionCommand("Add New User Button");
		newUserAddButton.addActionListener(new UserButtonPanelListener());

		// cancel user create
		JButton cancelNewUser = new JButton("Cancel");
		cancelNewUser.setActionCommand("Cancel New User Button");
		cancelNewUser.addActionListener(new UserButtonPanelListener());

		newUserButtonPanel.add(newUserAddButton);
		newUserButtonPanel.add(cancelNewUser);

		newUserFrame.setLayout(new GridLayout(4, 1));
		newUserFrame.add(title);
		newUserFrame.add(newUserLabel);
		newUserFrame.add(newUserInputPanel);
		newUserFrame.add(newUserButtonPanel);
		newUserFrame.setVisible(false);

	}

	// existing user login
	private class loginListener implements ActionListener {

		public void actionPerformed(ActionEvent a) {

			// get value of combo box selection
			String selection = (String) userComboBox.getSelectedItem();
			// validate selection
			if ((selection.equals("Select User")) || (selection.equals(null))) {

				JOptionPane.showMessageDialog(userComboBox,
						"Select username to log in");

			} else { // user either selects a name, or 'new user'

				if (selection.equals("New User")) {
					/* NEW USER CREATE OPTION */
					mainWin.changeUIState(States.progState.NEW_USER.tellState());

				} else {
					/* USER LOGIN ATTEMPT */

					// iterate login attempts. if three failed logins, exit
					// program
					int loginTryCount = 0;
					boolean isValidPassword = false;

					// get the username from combo box
					String attemptedUserName = selection;

					while (!isValidPassword) {
						String inputPass = Util
								.getUserInputPasswordFromPopup(userComboBox);

						// if user cancels
						if (inputPass.equals("cancel")) {
							break;
						}

						// test passwords
						isValidPassword = ComSecurity
								.isEnteredPassMatchUserPass(attemptedUserName,
										inputPass);

						if (isValidPassword) {
							// log user in, create User object and set main
							// program 'current user'
							User thisUser = new User(selection);

							// pass to main program
							mainWin.setCurrentUser(thisUser);
							// destroy user object to prevent key/user data from
							// hanging around
							thisUser = null;

							// update program state
							mainWin.changeUIState(States.progState.NEW_ENTRY
									.tellState());

						}

						// too many failed login attempts. close program
						if (loginTryCount == 2) {
							JOptionPane
									.showMessageDialog(
											userComboBox,
											"Excessive failed login attempts. Exiting...",
											"Too many login attempts",
											JOptionPane.ERROR_MESSAGE);
							System.exit(0);
						}
						loginTryCount++;
					} // end login validation loop
				}
			}
		}

	}

	// listen for user add/login/cancel buttons
	private class UserButtonPanelListener implements ActionListener {

		public void actionPerformed(ActionEvent a) {
			String command = a.getActionCommand(); // get name of calling action
			// System.out.println("listener");
			// if 'register new user' is clicked
			if (command.equals("Add New User Button")) {

				// get user name
				String userName = newUserName.getText();

				// get password field values

				String pass1 = new String(newUserPassword.getPassword());
				String pass2 = new String(confirmPassword.getPassword());

				// if entered passwords don't match
				if (!(pass1.equals(pass2))) {

					JOptionPane.showMessageDialog(newUserPassword,
							"Passwords Do Not Match", "Password Mismatch",
							JOptionPane.ERROR_MESSAGE);

				}

				else if (pass1.length() < 6) {

					JOptionPane.showMessageDialog(newUserPassword,
							"Password must be at least 6 characters",
							"Password Length", JOptionPane.ERROR_MESSAGE);
				} else { // passwords are good, let's make the user!

					// first, check that user entered a reasonable name
					if (userName.length() >= 3) {

						boolean acceptableFileName = Util
								.isGoodFileName(userName);

						if (!(acceptableFileName)) {
							String acceptable = "A-Z 0-9 Underscore( _ ) Hyphen(-) Period(.)";
							JOptionPane.showMessageDialog(null,
									"Invalid character in User name.  \nAcceptable characters are:\n"
											+ acceptable, "File Name Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						/* check user name does not exist */
						for (String existUser : users) {
							if (existUser != null) {
								/*
								 * convert input to lowercase, compare to each
								 * existing mane converted to lowercase
								 */
								if (userName.toLowerCase(getLocale()).equals(
										existUser.toLowerCase(getLocale()))) {
									JOptionPane
											.showMessageDialog(
													null,
													"User '"
															+ userName
															+ "' Exists. Please choose another name ",
													"Error",
													JOptionPane.ERROR_MESSAGE);
									return;
								}
							}
						}
						// create the user directory
						Util.createNewUserDirectory(userName, 0, newUserFrame);

						// get unique user key
						byte[] userKey = ComSecurity.getNewRandomKey();

						// log user config data
						boolean userCreated = Util.writeNewUserConfigData(
								userKey, pass1, userName);

						if (!userCreated) {
							// something wrong with user system. abort attempt
							JOptionPane
									.showMessageDialog(
											newUserPassword,
											"Error creating user. Please restart and try again.",
											"File Error",
											JOptionPane.ERROR_MESSAGE);
							FileRemover fr = null;
							// remove empty directories
							String entryDirectory = userName + "\\"
									+ Util.ENTRY_DIR;
							String topicDirectory = userName + "\\"
									+ Util.TOPIC_DIR;
							String configFile = userName + "\\"
									+ Util.CONFIG_FILE;
							String initFile = userName + "\\" + Util.INIT;
							String preferenceFile = userName + "\\"
									+ Util.PREFERENCES;
							fr = new FileRemover(entryDirectory);
							fr.delete();
							fr = new FileRemover(topicDirectory);
							fr.delete();
							fr = new FileRemover(configFile);
							fr.delete();
							fr = new FileRemover(initFile);
							fr.delete();
							fr = new FileRemover(preferenceFile);
							fr.delete();

							// reset program to initialization phase
							mainWin.changeUIState(States.progState.INIT
									.tellState());
						}

						Util.updateUserLog(userName);
						// update main window's user list
						mainWin.getLogin().updateUserComboBox();

						JOptionPane.showMessageDialog(newUserPassword, "User '"
								+ userName + "' has been created", "User Name",
								JOptionPane.INFORMATION_MESSAGE);
						mainWin.changeUIState(States.progState.INIT.tellState());

					} else {

						JOptionPane.showMessageDialog(newUserPassword,
								"User name must be at least 3 characters",
								"User Name", JOptionPane.ERROR_MESSAGE);

					}

				}

			}
			// user selects 'cancel'
			else {

				// revert to initialization state
				mainWin.changeUIState(States.progState.INIT.tellState());
			}
		}
	}

	public void setComponentState(String s) {
		state = s;
	}

	public void updateComponentStates(String s) {
		setComponentState(s);

		if (state.equals("INIT")) {
			selectUserPane.setVisible(true);
			newUserFrame.setVisible(false);
		} else if (state.equals("NEW_USER")) {
			selectUserPane.setVisible(false);
			newUserFrame.setVisible(true);
		} else {
			selectUserPane.setVisible(false);
			newUserFrame.setVisible(false);
		}
	}

	public void updateUserComboBox() {
		String[] newUserList = Util.readFileToArray(Util.USER_LOG);
		userComboBox.removeAllItems();
		for (String s : newUserList) {
			userComboBox.addItem(s);
		}
	}
}
