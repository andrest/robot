package Rumbaugh.View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import Rumbaugh.RobotData;
import Rumbaugh.Robot.Solo;

/**
 * This is the class for the user
 * interface that will be shown to the user
 * to control the robot
 * @author Hussain
 *
 */
@SuppressWarnings("serial")
public class RobotUserInterface extends JFrame {        
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
        private JPanel panelCentre;
        
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
                setExploreButton(new JButton("Explore"));
                mapButton = new JButton("Save Map");
                setCollectButton(new JButton("Collect"));
                
                x1Label = new JLabel("X1:");
                y1Label = new JLabel("Y1:");
                x2Label = new JLabel("X2:");
                y2Label = new JLabel("Y2:");
                
                setSoloRadioButton(new JRadioButton("Solo"));
                multiRadioButton = new JRadioButton("Multi");
                
                buttonGroup = new ButtonGroup();
                
                buttonGroup.add(getSoloRadioButton());
                buttonGroup.add(multiRadioButton);
                
                panelSouth = new JPanel();

                panelCentre = RobotData.INSTANCE.getImagePanel();
                panelCentre.setBackground(Color.BLACK);

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
                
                this.add(panelCentre, BorderLayout.CENTER);
                
                panelSouth.add(getExploreButton());
                panelSouth.add(mapButton);
                panelSouth.add(getCollectButton());
                
                panelSouth.add(x1Label);
                panelSouth.add(x1TextField);
                
                panelSouth.add(y1Label);
                panelSouth.add(y1TextField);
                
                panelSouth.add(x2Label);
                panelSouth.add(x2TextField);
                
                panelSouth.add(y2Label);
                panelSouth.add(y2TextField);
                
                panelSouth.add(getSoloRadioButton());
                panelSouth.add(multiRadioButton);
        }
        
        /**
         * This is a method that will
         * hold all the action listeners for the
         * buttons that are placed in the GUI
         */
        private void actionListeners(){
                getExploreButton().addActionListener(new ActionListener() {
                        
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if(getSoloRadioButton().isSelected() == false && multiRadioButton.isSelected() == false)
                                {
                                        JOptionPane.showMessageDialog(null, "Choose a mode of Operation", "Robot Operation Mode", JOptionPane.WARNING_MESSAGE);
                                }
                                else
                                {
                                        Thread exploreThread = new Thread(new Runnable() {
                                                
                                                @Override
                                                public void run() {
                                                        getExploreButton().setEnabled(false);
                                                        mapButton.setEnabled(false);
                                                        getCollectButton().setEnabled(false);
                                                        Solo soloRobot = new Solo();
                                                        soloRobot.startMapping();
                                                        mapButton.setEnabled(true);
                                                        getCollectButton().setEnabled(true);
                                                        
                                                }
                                        });
                                        exploreThread.start();
                                }
                        }
                });
                
                mapButton.addActionListener(new ActionListener() {
                        
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                DateFormat dateFormat = new SimpleDateFormat("HH.mm.ss");
                                Date date = new Date();
                                BufferedImage image = RobotData.INSTANCE.getScaledBufferedImage();
                                try {
                                        RobotData.exportImageToFile("map-"+dateFormat.format(date), image);
                                } catch (IOException e1) {      
                                        JOptionPane.showMessageDialog(null, e1.getMessage());
                                }
                        }
                });
                
                getCollectButton().addActionListener(new ActionListener() {
                        
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if(x1TextField.getText().isEmpty() || y1TextField.getText().isEmpty() || x2TextField.getText().isEmpty() || y2TextField.getText().isEmpty() || (getSoloRadioButton().isSelected() == false && multiRadioButton.isSelected() == false))
                                {
                                        JOptionPane.showMessageDialog(null, "Enter the coordinates for the collection area and also choose the operation mode", "Collection Coordinates", JOptionPane.WARNING_MESSAGE);
                                }
                                else if(validateCoordinates(x1TextField.getText()) == false || validateCoordinates(y1TextField.getText()) == false || validateCoordinates(x2TextField.getText()) == false || validateCoordinates(y2TextField.getText()) == false)
                                {
                                        JOptionPane.showMessageDialog(null, "Only numbers can be entered in the coordinates field", "Coordinates Wrong Format", JOptionPane.WARNING_MESSAGE);
                                }
                                else
                                {
                                        getExploreButton().setEnabled(false);
                                        getCollectButton().setEnabled(false);
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

        /**
         * @return the soloRadioButton
         */
        public JRadioButton getSoloRadioButton() {
                return soloRadioButton;
        }

        /**
         * @param soloRadioButton the soloRadioButton to set
         */
        public void setSoloRadioButton(JRadioButton soloRadioButton) {
                this.soloRadioButton = soloRadioButton;
        }

        /**
         * @return the exploreButton
         */
        public JButton getExploreButton() {
                return exploreButton;
        }

        /**
         * @param exploreButton the exploreButton to set
         */
        public void setExploreButton(JButton exploreButton) {
                this.exploreButton = exploreButton;
        }

        /**
         * @return the collectButton
         */
        public JButton getCollectButton() {
                return collectButton;
        }

        /**
         * @param collectButton the collectButton to set
         */
        public void setCollectButton(JButton collectButton) {
                this.collectButton = collectButton;
        }
}