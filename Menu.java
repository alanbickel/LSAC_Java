package ComLog;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class Menu {

	private String state; // hold state of north panel(toggle component view)
	private JPanel controlFrame = new JPanel(); // hold link objects
	private ComLog mainWin; // hold reference to main window object
	
	/*menu options to be watched/toggled*/
	private JMenuItem delete;
	private JMenuItem save;

	public Menu(ComLog parent) {

		mainWin = parent; // main window 

		JMenuBar menubar = new JMenuBar();

		/* BUILD FILE MENU */
		JMenu fileMenu = new JMenu("File ");
		
		JMenuItem logout = new JMenuItem("Logout");
		logout.addActionListener(new FileListener());
		logout.setActionCommand("logout");

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new FileListener());

		fileMenu.add(logout);
		fileMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		fileMenu.add(exit);
		
		/*add file menu to main menu*/
		menubar.add(fileMenu);
		menubar.add(new JSeparator(JSeparator.VERTICAL));

		/* BUILD USER ACTION MENU */
		JMenu actionsMenu = new JMenu("User Actions ");
		
		actionsMenu.addMenuListener(new ActionMenuEnabler());
		
		
		JMenuItem preferences = new JMenuItem("Preferences");
		preferences.addActionListener(new UserActionListener());
		preferences.setActionCommand("preferences");
		
		save = new JMenuItem("Save Current Entry");
		save.addActionListener(new UserActionListener());
		save.setActionCommand("save");
		
		delete = new JMenuItem("Delete Current Entry");
		delete.addActionListener(new UserActionListener());
		delete.setActionCommand("delete");
		
		/*search sub-menu*/
		JMenu search = new JMenu("Search Entries");
		
		JMenuItem dates = new JMenuItem("Dated Entries");
		dates.setActionCommand("date");
		dates.addActionListener(new SearchListener());
		JMenuItem topic = new JMenuItem("Topic Entries");
		topic.setActionCommand("topic");
		topic.addActionListener(new SearchListener());
		
		search.add(dates);
		search.add(topic);

		
		/*add items to action menu*/
		actionsMenu.add(preferences);
		actionsMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		actionsMenu.add(search);
		actionsMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		actionsMenu.add(save);
		actionsMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		actionsMenu.add(delete);

		menubar.add(actionsMenu);
		menubar.add(new JSeparator(JSeparator.VERTICAL));

		/* links menu */
		JMenu linkMenu = new JMenu("Journal Entries");
		
		JMenuItem newEntry = new JMenuItem("Today's Entry");
		newEntry.addActionListener(new QuickLinkListener());
		newEntry.setActionCommand("new entry");
		
		JMenuItem BrowseEntries = new JMenuItem("Browse Dated Entries");
		BrowseEntries.addActionListener(new QuickLinkListener());
		BrowseEntries.setActionCommand("browse dates");
		
		
		JMenuItem yesterday = new JMenuItem("Yesterday's Entry");
		yesterday.addActionListener(new QuickLinkListener());
		yesterday.setActionCommand("yesterday");

		linkMenu.add(newEntry);
		linkMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		linkMenu.add(yesterday);
		linkMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		linkMenu.add(BrowseEntries);

		menubar.add(linkMenu);
		menubar.add(new JSeparator(JSeparator.VERTICAL));

		/* topics menu */
		JMenu topicMenu = new JMenu("Topic Entries");
		
		JMenuItem newTopic = new JMenuItem("New Topic");
		newTopic.addActionListener(new SubjectListener());
		newTopic.setActionCommand("new topic");
		
		JMenuItem brosweTopics = new JMenuItem("Browse Topics");
		brosweTopics.addActionListener(new SubjectListener());
		brosweTopics.setActionCommand("browse topic");

		topicMenu.add(newTopic);
		topicMenu.add(new JSeparator(JSeparator.HORIZONTAL));
		topicMenu.add(brosweTopics);

		menubar.add(topicMenu);

		/*parent component that holds menubar*/
		controlFrame.setLayout(new FlowLayout(FlowLayout.LEFT)); // left justify
		controlFrame.add(menubar);

	}

	public JPanel getMenu() {
		return controlFrame;
	}

	public ComLog getParent() {
		return mainWin;
	}

	public void setState(String str) {
		state = str;
	}

	public void setComponentState(String str) {
		setState(str);
		switch (state) {
		case "INIT":
		case "NEW_USER": {
			controlFrame.setVisible(false);
			break;
		}
		default: {
			controlFrame.setVisible(true);
			break;
		}

		}
	}

	/* file menu */
	private class FileListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			if (command.equals("logout")) {
				getParent().userLogout();
				getParent().changeUIState(States.progState.INIT.tellState());
			} else { // user chooses to exit
				System.exit(0);
			}

		}
	}

	/*watches 'user action' menu choices*/
	private class UserActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			String command = e.getActionCommand();

			switch (command) {
			case "preferences": {
				getParent().changeUIState(States.progState.CONFIG.tellState());
				break;
			}
			case "save":{
				/*fire action on 'save button' in entry panel object*/
				getParent().getCenterPanel().getEntryObject().getSaveButton().doClick();
				break;
			}
			case "delete":{
				
				FileRemover fr = new FileRemover(mainWin.getCurrentUser().getFilePath());
				
				String shortFileName = mainWin.getCenterPanel().getEntryObject().getShortFileName();
				fr.removFileInterface(shortFileName);
				mainWin.changeUIState(States.progState.NEW_ENTRY.tellState());
				break;
			}
			}
		}
	}

	private class QuickLinkListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();
			
			switch(command){
			
			case "new entry":{
				mainWin.changeUIState(States.progState.NEW_ENTRY.tellState());
				break;
			}
			case "yesterday":{
				mainWin.changeUIState(States.progState.YESTERDAY.tellState());
				break;
			}
			case "browse dates":{
				mainWin.changeUIState(States.progState.BROWSE_ENTRY.tellState());
				break;
			}
			
			}
		}
	}

	private class SubjectListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();
			
			switch(command){
			
			case "new topic":{
				mainWin.getCenterPanel().getTopicPanel().createNewTopic();
				break;
			}

			case "browse topic":{
				mainWin.changeUIState(States.progState.BROWSE_TOPIC.tellState());
				break;
			}
			}
		}
	}
	
	/*this class regulates which menu options are selectable based on particular criteria of each option*/
	private class ActionMenuEnabler implements MenuListener{
		public void actionPerformed(ActionEvent e){
			/*search for current file being viewed. if it exists, enable deleting*/
			
		}

		@Override
		public void menuCanceled(MenuEvent arg0) {
			
		}

		@Override
		public void menuDeselected(MenuEvent arg0) {
			
		}

		@Override
		public void menuSelected(MenuEvent arg0) {
			
			/*toggle delete option accessibility*/
			
			
			/*toggle save option accessibility*/
			String progState = mainWin.getProgramState(); // only enable 'save' option for modes that possess text editor
				if((progState.equals("NEW_ENTRY"))||
					(progState.equals("EDIT_ENTRY"))||
					(progState.equals("EDIT_TOPIC"))||
					(progState.equals("YESTERDAY"))){
					save.setEnabled(true);
					/*toggle delete availablility*/
					String fileName = getParent().getCurrentUser().getFilePath();
					if(fileName != null){
						File file = new File(fileName);
						if(file.exists()){
							delete.setEnabled(true);
						} else {
							delete.setEnabled(false);
						}
					} else {
						delete.setEnabled(false);
					}
				} else {
					save.setEnabled(false);
					delete.setEnabled(false);
				}
		
			
			
		}
	}

	private class SearchListener implements ActionListener{
		public void actionPerformed(ActionEvent e ){
			String command = e.getActionCommand();
			
			switch(command){
			/*search dated entries*/
			case "date":{
				SearchModule mod = new SearchModule(mainWin, Util.ENTRY_DIR ); // build search module
				
				
				
				/*pass module instance to the search result interface*/
				getParent().getCenterPanel().getSearchPanel().setSearchModule(mod);
				
				/*change program state*/
				mainWin.changeUIState(States.progState.SEARCH_RESULT.tellState());
				
				break;
			}
			case "topic":{
				//new SearchModule(mainWin, Util.TOPIC_DIR );
SearchModule mod = new SearchModule(mainWin, Util.TOPIC_DIR ); // build search module
				
				/*pass module instance to the search result interface*/
				getParent().getCenterPanel().getSearchPanel().setSearchModule(mod);
				
				/*change program state*/
				mainWin.changeUIState(States.progState.SEARCH_RESULT.tellState());
				break;
			}
			}
		}
	}
}
