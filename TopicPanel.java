package ComLog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class TopicPanel {

	private ComLog parent; // reference current user, toggle program state
	private String state; // toggle visible panels

	private JPanel browsePanel = new JPanel(); // hold interface for perusing existing topics
	
	private JList<String> topicList = new JList<String>();
	private JTextArea topicText = new JTextArea();
	
	public TopicPanel(ComLog mainWin){
		
		parent  = mainWin; // assign ComLog object reference
		
		/*build browse topic panel*/
		buildBrowsePanel();
	} 
	
	public String getUserName(){
		return parent.getCurrentUser().getName();
	}
	
	public User getUser(){
		return parent.getCurrentUser();
	}
	public ComLog getParent(){
		return parent;
	}
	
	public void refreshTopicList(){
		String userRoot = Util.SYSTEM_ROOT + "\\" +  getUserName() ;
		DirectoryManager topicDM = new DirectoryManager(Util.TOPIC_DIR, userRoot);
		String[] userTopics = topicDM.getDMOStringArray();
		int topicListCount = userTopics.length +1;
		String[] topics = new String[topicListCount];
		topics[0] = "New Topic";
		
		for(int i = 1; i  < topicListCount; i++){
			/*truncate file extension from file name*/
			topics[i] = userTopics[i-1].replace(".txt", "");
		}
		StringListModel list = new StringListModel(topics);
		topicList.setModel(list);
	}
		
	public void buildBrowsePanel(){

		/*pane holds list of topics*/
		JPanel listPane = new JPanel();
		JLabel listLabel = new JLabel("Topic Selection: ");
		listLabel.setAlignmentX(SwingConstants.CENTER);

		
		/* Make Scroll-able */
		JScrollPane scrollPane = new JScrollPane(topicList);
		scrollPane.setPreferredSize(new Dimension(300, 150));
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		listPane.add(listLabel);
		topicList.setVisibleRowCount(10);
		//listPane.add(scrollPane);
		
		JPanel centerPadding = new JPanel();
		centerPadding.add(new JLabel(""));
		JPanel buttonPanel = new JPanel();
		JButton goButton = new JButton("Go");
		goButton.setActionCommand("load");
		goButton.addActionListener(new LoadTopicListener());
		buttonPanel.add(goButton);
		
		
		/*Pack components into middle panel*/
		browsePanel.setLayout(new GridLayout(3,1));
		browsePanel.add(listPane);
		browsePanel.add(scrollPane);
		browsePanel.add(buttonPanel);
	}
	
	public JPanel getBrowseTopicPanel(){
		return browsePanel;
	}

	public void setComponentState(String str){
		updateComponentState(str); // set state to member field
		
		switch (state) {
	
		case "BROWSE_TOPIC":{
			/*refresh the user's topic list*/
			refreshTopicList();
			
			/*display ui for selecting topic*/
			browsePanel.setVisible(true);
			break;
		}
		default:{
			
			/*hide this panel*/
			browsePanel.setVisible(false); // hide list of topics panel
			break;
		}
		}
	}
	
	public void updateComponentState(String str){
		state = str;
	}
	
	public void setUserTopicList(String[] topics){
		topicList.setModel(new StringListModel(topics));
	}
	
	/*Function to create new topic for user*/
	public void createNewTopic(){
	/* Prompt user for new topic name */
	String topicName = "";
	topicName = (String) JOptionPane.showInputDialog(null,
			"Enter New Topic Name", "Create Topic",
			JOptionPane.QUESTION_MESSAGE);

	/* if user did not cancel */
	if (!(topicName == null)) {

		/* topic name should be longer than three characters */
		if (topicName.length() > 3) {
			
			boolean acceptableFileName = Util.isGoodFileName(topicName);
			System.out.println("result: "+acceptableFileName);
			if(!(acceptableFileName)){
				String acceptable = "A-Z 0-9 Underscore( _ ) Hyphen(-) Period(.)";
				JOptionPane.showMessageDialog(null, "Invalid character in topic name.  \nAcceptable characters are:\n"+acceptable, "File Name Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			/*build file path 
			  for the topic*/
			String userFilePath = getUserName()+"\\"+Util.TOPIC_DIR+"\\"+topicName+".txt";
			
			/*check that file does not exist*/
			File file = new File(userFilePath);
			if(file.exists()){
				JOptionPane.showMessageDialog(null, "Topic '"+topicName+"' Exists.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			/*set path*/
			getUser().setFilePath(userFilePath);
			/*set file display name for entry panel*/
			parent.getCenterPanel().getEntryObject().setCurrentFileName(topicName);
			
			/*clear the user's entry field text to avoid potential text duplication if tey had been viewing an entry before they made a new topic*/
			getUser().setUserTextString("");
			
			/*save the file*/
			/*fire action on 'save button' in entry panel object*/
			//getParent().getCenterPanel().getEntryObject().getSaveButton().doClick();

			/* update program state */
			parent.changeUIState(
					States.progState.EDIT_ENTRY.tellState());
		} else {
			JOptionPane
					.showMessageDialog(
							null,
							"Topic name must be longer than three characters",
							"Topic Creation Error",
							JOptionPane.ERROR_MESSAGE);
		}
	} 
}
	
	/* list box model for building with string array */
	private class StringListModel extends DefaultComboBoxModel {
		public StringListModel(String[] items) {
			super(items);
		}
	}

	private class LoadTopicListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e){
			
			String command = e.getActionCommand();
			
			if(command.equals("load")){
				/*get chosen topic name*/
				String selectedTopic = topicList.getSelectedValue(); // get selected topic to load
				
				if(selectedTopic.equals("New Topic")){ // if user wants a new topic
					createNewTopic();
				}
				else {
				/*ELSE LOAD SELKECTED TOPIC!*/
				/*build file path String*/
				String userFilePath = getUserName()+"\\"+Util.TOPIC_DIR+"\\"+selectedTopic+".txt";
				/*set user file path*/
				getUser().setFilePath(userFilePath);
				/*set file display name for entry panel*/
				parent.getCenterPanel().getEntryObject().setCurrentFileName(selectedTopic);
				/*set file contents for display to user*/
				getUser().setUserEntryStringForSelectedFile(userFilePath);
				
				/*update program state*/
				parent.changeUIState(
						States.progState.EDIT_ENTRY.tellState());
				}
			}
		}
	}

}
