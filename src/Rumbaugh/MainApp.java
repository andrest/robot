package Rumbaugh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Rumbaugh.Robot.Solo;
import Rumbaugh.View.RobotUserInterface;

/**
 * This is the main starting point of the application where the user will enter
 * commands on the terminal and they will be processed here
 * 
 */
public class MainApp {
        private static String robotMode;
        private static Solo soloRobot;
        public static void main(String [] args){
                
        if(args.length > 0){
                List<String> arguments = Arrays.asList(args);
                
                if(arguments.contains("-gui")) {
		                    RobotUserInterface gui = new RobotUserInterface();
		                    gui.setVisible(true);
		            
		            if(arguments.contains("-solo"))
		                    gui.getSoloRadioButton().setSelected(true);
		                    soloRobot = new Solo();
		            if(arguments.contains("-explore") && arguments.contains("-solo")) {
		                    gui.getExploreButton().setEnabled(false);
		                    gui.getCollectButton().setEnabled(false);
		                    soloRobot.startMapping();
		                    gui.getCollectButton().setEnabled(true);
		                    if(arguments.contains("-collect")) {
		                        gui.getCollectButton().setEnabled(false);
		                        int i = arguments.indexOf("-collect");
		                        double x1 = Double.parseDouble(arguments.get(i + 1));
		                        double y1 = Double.parseDouble(arguments.get(i + 2));
		                        double x2 = Double.parseDouble(arguments.get(i + 3));
		                        double y2 = Double.parseDouble(arguments.get(i + 4));
		                        soloRobot.collectGarbage(x1,y1,x2,y2);
		                    }
		            }
	            } else {
	                    if(arguments.contains("-solo"))
	                            soloRobot = new Solo();
	                    else if(arguments.contains("-multi"));
	                            //;
	                    if(arguments.contains("-explore") && arguments.contains("-solo")) {     
	                            soloRobot.startMapping();
	                            if(arguments.contains("-collect")) {
	                                int i = arguments.indexOf("-collect");
	                                double x1 = Double.parseDouble(arguments.get(i + 1));
	                                double y1 = Double.parseDouble(arguments.get(i + 2));
	                                double x2 = Double.parseDouble(arguments.get(i + 3));
	                                double y2 = Double.parseDouble(arguments.get(i + 4));
	                                soloRobot.collectGarbage(x1,y1,x2,y2);
	                            }
	                    }
	            }
        }
    }
}