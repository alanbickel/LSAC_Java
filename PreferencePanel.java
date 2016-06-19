package ComLog;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class PreferencePanel {

	private User user = null;
	private ComLog mainWin;

	private String state; // toggle visible panels

	private JPanel preferencePanel = new JPanel();

	private int sessLen = 5;
	

	private JRadioButton sessionOption1 = new JRadioButton("5");
	
	private JRadioButton sessionOption2 = new JRadioButton("10");
	private JRadioButton sessionOption3 = new JRadioButton("30");
	private ButtonGroup timeoutGroup = new ButtonGroup();

	public void setSelectedSessionOption() {
		switch (sessLen) {
		case 5: {
			sessionOption1.setSelected(true);
			break;
		}
		case 10: {
			sessionOption2.setSelected(true);
			break;
		}
		case 30: {
			sessionOption3.setSelected(true);
			break;
		}
		}
	}

	public PreferencePanel(ComLog c) {
		mainWin = c;

		buildPreferencePanel();
	}

	public void setUser(User u) {
		user = u;
	}

	public JPanel getPreferencePanel() {
		return preferencePanel;
	}

	public void updateComponentState(String str) {
		state = str;
	}

	public void setComponentState(String str) {

		updateComponentState(str); // set state to member field
		switch (state) {

		case "CONFIG": {
			/*set user*/
			user = mainWin.getCurrentUser();
			setUserSessionPreference();
			setSelectedSessionOption();
			preferencePanel.setVisible(true);
			break;
		}
		default: {
			preferencePanel.setVisible(false);
			break;
		}
		}
	}

	public void buildPreferencePanel() {

		
		sessionOption1.setActionCommand("5");
		sessionOption2.setActionCommand("10");
		sessionOption3.setActionCommand("30");
		
		timeoutGroup.add(sessionOption1);
		timeoutGroup.add(sessionOption2);
		timeoutGroup.add(sessionOption3);
		/* layout for main panel */
		preferencePanel.setLayout(new GridLayout(0, 1));
		/* holds session manipulation interface */
		JPanel sessionPanel = new JPanel();
		/* session layout */
		sessionPanel.setLayout(new GridLayout(0, 1));
		JPanel centeringPanel = new JPanel();
		centeringPanel.add(new JLabel("Session Inactivity Timeout (Minutes)"));
		sessionPanel.add(centeringPanel);
		sessionPanel.setAlignmentX(SwingConstants.CENTER);

		// options for session length
		JPanel radio1Holder = new JPanel();
		radio1Holder.add(sessionOption1);
		radio1Holder.add(sessionOption2);
		radio1Holder.add(sessionOption3);

		sessionPanel.add(radio1Holder);

		// button to update selection
		JPanel sessionButtonPanel = new JPanel();
		JButton sessionLengthUpdateButton = new JButton("Update");
		sessionLengthUpdateButton.setActionCommand("update timeout");
		sessionLengthUpdateButton.addActionListener(new PreferenceListener());
		sessionButtonPanel.add(sessionLengthUpdateButton);

		sessionPanel.add(sessionButtonPanel);
		sessionPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		sessionPanel.setPreferredSize(new Dimension(345, 120));
		preferencePanel.add(sessionPanel);
		
		/*change password*/
		JPanel passwordPanel = new JPanel();
		passwordPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		passwordPanel.setPreferredSize(new Dimension(350, 40));
		JButton passChange = new JButton("Change Password");
		passChange.setAlignmentY(SwingConstants.CENTER);
		passwordPanel.setAlignmentY(SwingConstants.CENTER);
		passChange.setActionCommand("change pass");
		passChange.addActionListener(new PreferenceListener());
		passwordPanel.add(passChange);
		JPanel bufferPanel = new JPanel();
		bufferPanel.add(passwordPanel);
		preferencePanel.add(new JLabel(""));
		preferencePanel.add(bufferPanel);
	
		
		
		
		
		
		
		
		
	}

	public void setUserSessionPreference() {

		if (user != null) { // if a user exists
			/* get the file that holds session preference */
			String userFilePath = user.getName() + "\\" + Util.PREFERENCES;
			/* search their preference file for a session length */
			String prefLength = Util.getUserPreferenceItem(userFilePath,
					"Sess:");
				// set field variable
				sessLen = Integer.parseInt(prefLength);
			
		}
	}

	private class PreferenceListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();
			
			switch(command){
			case "update timeout":{
				
				// get selected value
				//JRadioButton source = (JRadioButton)e.getSource();
				String value = timeoutGroup.getSelection().getActionCommand();
				String replacementLine = "Sess:"+value;
				String userFilePath = mainWin.getCurrentUser().getName() + "\\" + Util.PREFERENCES;
				Util.updatePreferenceFile(userFilePath, "Sess:", replacementLine);
				JOptionPane.showMessageDialog(null, "Session length updated.");
				break;
			}
			case "change pass":{
				Config changeUserPass = new Config(mainWin.getCurrentUser(), mainWin);
				changeUserPass.changePasswordPanel();
				break;
			}
			}
		}
	}
}
