package Rumbaugh.View;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * This is the class for the user
 * interface that will be shown to the user
 * to control the robot
 * @author Hussain
 *
 */
@SuppressWarnings("serial")
public class RobotUserInterface extends JFrame {
	private JLabel mapLabel;
	
	private JButton exploreButton;
	private JButton mapButton;
	private JButton collectButton;
	private JLabel x1Label;
	private final JTextField x1TextField = new JTextField(5);
	private JLabel y1Label;
	private final JTextField y1TextField = new JTextField(5);
	private JLabel x2Label;
	private final JTextField x2TextField = new JTextField(5);
	private JLabel y2Label;
	private final JTextField y2TextField = new JTextField(5);
	
	private JRadioButton soloRadioButton;
	private JRadioButton multiRadioButton;
	
	private ButtonGroup buttonGroup;
	
	private JPanel panelSouth;
	
	public RobotUserInterface(){
		super("Robot User Interface");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		initWidgets();
		setLayout();
		addWidgets();
		pack();
	}
	
	/**
	 * This is a method that will initialise the widgets
	 * that will be added to the GUI
	 */
	private void initWidgets(){
		mapLabel = new JLabel("This is where the map will go");
		
		exploreButton = new JButton("Explore");
		mapButton = new JButton("Map");
		collectButton = new JButton("Collect");
		
		x1Label = new JLabel("X1:");
		y1Label = new JLabel("Y1:");
		x2Label = new JLabel("X2:");
		y2Label = new JLabel("Y2:");
		
		soloRadioButton = new JRadioButton("Solo");
		multiRadioButton = new JRadioButton("Multi");
		
		buttonGroup = new ButtonGroup();
		
		buttonGroup.add(soloRadioButton);
		buttonGroup.add(multiRadioButton);
		
		panelSouth = new JPanel();
		
		actionListeners();
	}
	
	/**
	 * This is a method to set the layout of the
	 * parent panel and also the panel that will be added
	 * to the south
	 */
	private void setLayout(){
		this.setLayout(new BorderLayout());
		panelSouth.setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	/**
	 * This is a method to add
	 * all the widgets to the screen
	 */
	private void addWidgets(){
		this.add(panelSouth, BorderLayout.SOUTH);
		
		this.add(mapLabel, BorderLayout.CENTER);
		
		panelSouth.add(exploreButton);
		panelSouth.add(mapButton);
		panelSouth.add(collectButton);
		
		panelSouth.add(x1Label);
		panelSouth.add(x1TextField);
		
		panelSouth.add(y1Label);
		panelSouth.add(y1TextField);
		
		panelSouth.add(x2Label);
		panelSouth.add(x2TextField);
		
		panelSouth.add(y2Label);
		panelSouth.add(y2TextField);
		
		panelSouth.add(soloRadioButton);
		panelSouth.add(multiRadioButton);
	}
	
	/**
	 * This is a method that will
	 * hold all the action listeners for the
	 * buttons that are placed in the GUI
	 */
	private void actionListeners(){
		exploreButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = "The operation button was pressed with mode of operation: ";
				if(soloRadioButton.isSelected() == false && multiRadioButton.isSelected() == false)
				{
					JOptionPane.showMessageDialog(null, "Choose a mode of Operation", "Robot Operation Mode", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					System.out.println(message+findModeSelection());
				}
			}
		});
		
		mapButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String fileName = (String)JOptionPane.showInputDialog(null, "Enter the file name for the Map", "File Name", JOptionPane.INFORMATION_MESSAGE);
				if((fileName != null) && (fileName.length() > 0))
				{
					System.out.println("The name of the file will be: "+fileName);
				}
			}
		});
		
		collectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(x1TextField.getText().isEmpty() || y1TextField.getText().isEmpty() || x2TextField.getText().isEmpty() || y2TextField.getText().isEmpty() || (soloRadioButton.isSelected() == false && multiRadioButton.isSelected() == false))
				{
					JOptionPane.showMessageDialog(null, "Enter the coordinates for the collection area and also choose the operation mode", "Collection Coordinates", JOptionPane.WARNING_MESSAGE);
				}
				else if(validateCoordinates(x1TextField.getText()) == false || validateCoordinates(y1TextField.getText()) == false || validateCoordinates(x2TextField.getText()) == false || validateCoordinates(y2TextField.getText()) == false)
				{
					JOptionPane.showMessageDialog(null, "Only numbers can be entered in the coordinates field", "Coordinates Wrong Format", JOptionPane.WARNING_MESSAGE);
				}
				else
				{
					System.out.println("The collect button was pressed with coordinates \n X1: "+x1TextField.getText()+"\n Y1: "+y1TextField.getText()+"\n X2: "+x2TextField.getText()+"\n Y2: "+y2TextField.getText()+"\n Operation Mode: "+findModeSelection());
				}
			}
		});
	}
	
	/**
	 * This is a method that validates that
	 * only number are entered in the coordinates area of 
	 * application
	 * @param validate This is the string to validate 
	 * @return return true if the string contains only numbers
	 * and false otherwise
	 */
	private boolean validateCoordinates(String validate){
		String pattern = "^[0-9]*$";//This is a regular expression that represents only numbers
		Pattern regexPattern = Pattern.compile(pattern);
		Matcher matcher = regexPattern.matcher(validate);
		return matcher.find();
	}
	
	/**
	 * This is method to return the operation
	 * mode that the user has chosen on the user interface
	 * @return The mode of operation that the robot
	 * will follow, this will either be Solo or
	 * Multi
	 */
	private String findModeSelection(){
		Enumeration<AbstractButton> radioButtons = buttonGroup.getElements();
		while(radioButtons.hasMoreElements())
		{
			AbstractButton modeButton = radioButtons.nextElement();
			if(modeButton.isSelected())
			{
				return modeButton.getText();
			}
		}
		return null;
	}

}
