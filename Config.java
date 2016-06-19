package ComLog;

import ComLog.States;

import java.awt.GridLayout;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class Config {

	private User user; // holds current user
	private ComLog mainWin; // hold program object instance
	private JPasswordField pass = new JPasswordField(20);
	private JPasswordField newPass = new JPasswordField(20);
	private JPasswordField confPass = new JPasswordField(20);

	public Config(User setUser, ComLog parent) {
		user = setUser;
		mainWin = parent;
	}

	public void changePasswordPanel() {
		JPanel pChange = new JPanel();

		JLabel passLabel = new JLabel("Current Password");
		JLabel newLabel = new JLabel("New Password");
		JLabel congLabel = new JLabel("Confirm New Password");
		pChange.setLayout(new GridLayout(4, 2));
		pChange.add(passLabel);
		pChange.add(pass);
		pChange.add(newLabel);
		pChange.add(newPass);
		pChange.add(congLabel);
		pChange.add(confPass);

		boolean isMatch = false; // have yet to authenticate user
		boolean newPasswordsMatch = false;
		int counter = 0; // count login attempts

		/*
		 * loop if incorrect password. log user out after two failed password
		 * attempts
		 */
		while (!(isMatch)) {
			/* log user out for too many failed attempts */
			if (counter > 1) {
				mainWin.userLogout();
				/* set program to initialization */
				JOptionPane.showMessageDialog(null, "Incorrect Password Entered. Logging out...");
				mainWin.changeUIState(States.progState.INIT.tellState());
				return;
			}

			int result = JOptionPane.showConfirmDialog(null, pChange,
					"Change Password", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {

				/* get string representations of password inputs */
				String inputPass = new String(pass.getPassword()); // get user
																	// current
																	// password
				String newPassword = new String(newPass.getPassword());
				String confirmPassword = new String(confPass.getPassword());

				/* test user original password (authentication) */
				isMatch = ComSecurity.isEnteredPassMatchUserPass(
						user.getName(), inputPass); // check that user knows the
													// stored password
				if (isMatch) {
					newPasswordsMatch = newPassword.equals(confirmPassword); // test
																				// proposed
																				// passwords

					/* alert user that new passwords do not match */
					if (!(newPasswordsMatch)) {
						/*
						 * do not increment counter, just fall through another
						 * loop
						 */
						JOptionPane.showMessageDialog(null,
								"New Passwords Do Not Match.", "Input Error",
								JOptionPane.ERROR_MESSAGE);
						continue;
						
						/*make sure new password is not blank*/
					}  else if((newPassword == null) || (newPassword.equals(""))){
						JOptionPane.showMessageDialog(null, "Password cannot be blank", "Password Error", JOptionPane.ERROR_MESSAGE);
					}
					/*
					 * Password authenticated, new passwords match, change
					 * user password!
					 */
					else {
						/* Encrypt user password */
						byte[] newEncodedPassword = ComSecurity.encryptString(
								newPassword, user.getKey());
						/* build file path */
						String path = Util.SYSTEM_ROOT + "\\" + user.getName()
								+ "\\" + Util.INIT;
						System.out.println("path: " + path);
						Util.writeByteArrayToFile(newEncodedPassword, path,
								"Unable to change password. Please try again");

						JOptionPane.showMessageDialog(null, "Password Changed");
						return;
					}

				} else {
					counter++; // increment count of login attempts
				}

			} else { // user cancels. break loop
				return;
			}

		}

	}
}
