package ComLog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import ComLog.States;
import ComLog.Util;
import ComLog.CenterPanel;
import ComLog.User;
import ComLog.Login;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.Timer;

public class ComLog extends JFrame {

	// MainWindow Dimensions
	private final int WINDOW_WIDTH = 600;
	private final int WINDOW_HEIGHT = 600;

	/*holds text editor, preferences, login interface*/
	private CenterPanel CENTER_PANEL;
	
	/*login object instance*/
	private Login LOGIN_PANEL;
	
	/*menu object instance*/
	private Menu menu;
	
	// hold identity of current user
	public User currentUser;
	
	/*hold current state of program*/
	private String state;

	private long sessionIdleTime = 0; // hold elapsed session time of user.
										// measure 'silent' time and lock
										// program after specified period



	public ComLog() {
		super("Secure Asynchronious Communication Log"); // build frame
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT); // set size
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // set window close
														// behavior
	}

	
	/*SESSION MONITORING FUNCTIONS*/
	public void testForSessionTimeout() {
		
		boolean authenticated = false; // marker to monitor login attempt
		int attempts = 0; // count number of times user tries to login

		

			if (!(currentUser == null)) { // only trigger action if there is a user logged in
				
				if (sessionIdleTime >= currentUser.getSessionLengthInSeconds()) { // check against user's set session time
				
				while (!(authenticated)) { 
					
					getCenterPanel().getMainCenterPanel().setVisible(false); // mask content
					
					JOptionPane.showMessageDialog(null, currentUser.getName() // alert user
							+ ", authentication is required.");
					
					String inputPass = Util.getUserInputPasswordFromPopup(null); // get password
					
					authenticated = ComSecurity.isEnteredPassMatchUserPass( // check against stored password
							currentUser.getName(), inputPass);
					
					if (authenticated) { // refresh session, show content
						getCenterPanel().getMainCenterPanel().setVisible(true);
						sessionIdleTime = 0;
						return;
					}
					if (attempts == 1) { // give user two tries to log back in,
											// then force logout
						JOptionPane.showMessageDialog(null,
								"Authentication Failed. Logging out...");
						getCenterPanel().getMainCenterPanel().setVisible(true);
						changeUIState(States.progState.INIT.tellState()); 
						return;
					}
					attempts++;
				}

			}
		}
	}

	public long getSessionIdleTime() {
		return sessionIdleTime;
	}

	public void incrementSessionIdleTime(long l) {
		sessionIdleTime += l;
	}

	public void resetSessionIdleTime() {
		sessionIdleTime = 0;
	}
	/*END SESSION MONITORING*/
	
	
	/* USER FUNCTIONS */
	public void setCurrentUser(User u) {
		currentUser = u;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void userLogout() {
		currentUser = null;
	}
	/*END USER FUNCIONS*/
	
	/*INTERFACE SETTERS/GETTERS*/
	public void setCenter(CenterPanel c) {
		CENTER_PANEL = c;
	}

	public CenterPanel getCenterPanel() {
		return CENTER_PANEL;
	}

	public void setLoginPanel(Login l) {
		LOGIN_PANEL = l;
	}

	public Login getLogin() {
		return LOGIN_PANEL;
	}
	/*END INTERFACE SETTER/GETTERS*/
	
	/* each panel instance referenced, all children call this on state change */
	public void changeUIState(String str) {

		// keep up with potential date change while system is running
		Util.refreshDate();
		
		/*update program state*/
		state = str;
		
		/*toggle component visibility */
		LOGIN_PANEL.updateComponentStates(str);
		CENTER_PANEL.updateComponentStates(str);
		menu.setComponentState(str);
	}
	
	public String getProgramState(){
		return state;
	}

	public static void main(String[] args) {

		/* check for existence of user log. */
		Util.buildUserLogIfNotExists();

		/* MAIN WINDOW */
		final ComLog win = new ComLog();

		/*watch mouse activity, this resets session timer for user*/
		win.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent arg0) {
				win.resetSessionIdleTime();

			}

			public void mouseMoved(MouseEvent arg0) {
				win.resetSessionIdleTime();

			}

		});

		/* initialize session timer */
		Timer t = new Timer(1000, new ActionListener() { // 1000 ms. delay between timer incrememtns
			public void actionPerformed(ActionEvent e) {
				win.sessionIdleTime += 1; // increment session idle time each
											// second.
				//System.out.println("elapsed: " + win.sessionIdleTime);
				win.testForSessionTimeout(); //check for idle time limit
			}
		});
		t.start(); // start timer

		/* Center main window to screen */
		win.setLocationRelativeTo(null);

		/* CENTER PANEL */
		CenterPanel cp = new CenterPanel(win); // build instance
		/*layout*/
		cp.getMainCenterPanel().setAlignmentX(JComponent.CENTER_ALIGNMENT);
		cp.getMainCenterPanel().setBorder(
				javax.swing.BorderFactory.createRaisedBevelBorder());
		/*set object reference to toggle program state*/
		win.setCenter(cp);

		/* SET MAIN WINDOW POINTER TO LOGIN PANEL*/
		win.setLoginPanel(cp.getLoginObjecForMainWindow());
	
		/*Build Menu*/
		win.menu = new Menu(win);

		/* Layout Manager */
		win.setLayout(new BorderLayout());
		
		/*add components*/
		win.add(cp.getMainCenterPanel(), BorderLayout.CENTER);
		win.add(win.menu.getMenu(), BorderLayout.NORTH);
		
		// initialize state of child panels
		win.changeUIState(States.progState.INIT.tellState());

		// show the window
		win.setVisible(true);

	}

}
