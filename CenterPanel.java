package ComLog;

import ComLog.Login;
import java.awt.Dimension;
import javax.swing.JPanel;


/**
 * this object holds all children components*/
public class CenterPanel extends ComLog {

	/* fields common to all panels */
	private String state; // hold state of center panel
	
	private ComLog mainWin; /* hold main window instance address */
	private Login loginPanel = new Login(); // hold login components
	private EntryPanel entryObj;// hold 'edit entry' functionlity
	private TopicPanel topicObj; // topic construct
	private PreferencePanel prefObj; // user preferences
	private SearchResultPanel searchObj;
	
	private JPanel entryPanel; // new entry components
	private JPanel browseEntriesPanel; // selection of dated entries
	private JPanel browseTopicPanel; // selection of topics
	private JPanel preferencePanel; // user preferences
	private JPanel resultPanel; 	// search result UI
	
	

	/* add sub-components to this panel, pass to main */
	private JPanel centerForMain = new JPanel();

	public CenterPanel(ComLog mainwin) {

		setWin(mainwin); // main window object reference

		loginPanel.setParent(mainWin); // main window object reference for login component
		
		entryObj  = new EntryPanel(mainWin); // entry panel object reference, tie to main window object(pass state change)
		entryPanel = entryObj.getEntryPanel(); 
		entryPanel.setPreferredSize(new Dimension(410, 500));
		browseEntriesPanel = entryObj.getBrowseEntryPanel(); // get panel to browse dated entries
		
		topicObj = new TopicPanel(mainWin); // topic panel object reference, tie to main window object (pass state change)
		browseTopicPanel = topicObj.getBrowseTopicPanel();
		
		prefObj = new PreferencePanel(mainWin); // user preference interface
		preferencePanel = prefObj.getPreferencePanel();// add preferences panel
		
		searchObj = new SearchResultPanel(mainWin); // search result object
		resultPanel = searchObj.getResultPanel(); // panel to hold search results
		
		/*Add sub components to main window object*/
 		centerForMain.add(loginPanel.getComponentFrame());
		centerForMain.add(entryPanel);
		centerForMain.add(browseEntriesPanel);
		centerForMain.add(browseTopicPanel);
		centerForMain.add(preferencePanel);
		centerForMain.add(resultPanel);

	}

	/* SETTERS, GETTERS, AND OTHER FUNCTIONALITY */
	
	public SearchResultPanel getSearchPanel(){
		return searchObj;
	}

	public Login getLoginObjecForMainWindow() {
		return loginPanel;
	}

	/* set main window instance reference */
	public void setWin(ComLog w) {
		mainWin = w;
	}

	/* get referenceof main window object */

	public ComLog getMainWindow() {
		return mainWin;
	}

	/* get Center Panel panel to add to main window */
	public JPanel getMainCenterPanel() {
		return centerForMain;
	}
	
	public EntryPanel getEntryObject(){
		return entryObj;
	}
	
	public TopicPanel getTopicPanel(){
		return topicObj;
	}

	public void setComponentState(String s) {
		state = s;
	}

	public String getComponentState() {
		return state;
	}

	
	public void updateComponentStates(String str) {
		// set object stare
		setComponentState(str);
		
		// inform the login panel
		loginPanel.setComponentState(str);
		
		topicObj.setComponentState(str);
		
		// inform the dated entry panel
		entryObj.setComponentState(str);
		
		// toggle visibility of user preferences
		prefObj.setComponentState(str);
		
		// search result interface toggle
		searchObj.setComponentState(str);
		
		

	}

}
