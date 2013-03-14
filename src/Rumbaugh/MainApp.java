package Rumbaugh;

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
        		
        	boolean test = true;
           	int i = 0;
            	
           	while(i<args.length && test){
            		
           		if(args[i].equals("-gui"))
           			new RobotUserInterface().setVisible(true);
            		
           		else if(args[i].equals("-solo"))
           			soloRobot = new Solo();
           		
           		
            	else if(args[i].equals("-multi"))
            		test = false;  
            	i++;
            }
            	
            if(test){
            		
            	for(int j = 0; j<args.length; j++){
            			
            		if(args[j].equals("-explore"))
            			//this is where the explore code goes
                           soloRobot.startMapping();
            			
            		else if(args[j].equals("-collect")){
            			 double x1 = Double.parseDouble(args[i + 1]);
                         double y1 = Double.parseDouble(args[i + 2]);
                         double x2 = Double.parseDouble(args[i + 3]);
                         double y2 = Double.parseDouble(args[i + 4]);
                         System.out.println("X1: "+x1+"\nY1: "+y1+"\nX2: "+x2+"\nY2: "+y2);
            		}
            	}
            }
        }
    }
}