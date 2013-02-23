package Rumbaugh;

import Rumbaugh.Robot.Multi;
import Rumbaugh.Robot.Solo;
import Rumbaugh.View.RobotUserInterface;

/**
 * This is the main starting point of the application
 * where the user will enter commands on the terminal and 
 * they will be processed here
 * 
 */
public class MainApp{
	private static String robotMode;
        public static void main(String [] args){
            
                for(int i = 0; i < args.length; i++)
                {
                        if(args[i].equals("-explore"))
                        {
                                //this is where the explore code goes
                        	System.out.println("The explore area is chosen");
                        	if(robotMode == null)
                        	{
                        		System.out.println("Choose a robot mode, either -solo or -multi");
                        	}
                        	else if(robotMode.equals("Solo"))
                        	{
                        		Solo soloRobot = new Solo();
                        		soloRobot.startMapping();
                        	}
                        	else
                        	{
                        		Multi multiRoMulti = new Multi();
                        	}
                        }
                        else if(args[i].equals("-map"))
                        {
                                //this is where the map code will go
                        }
                        else if(args[i].equals("-collect"))
                        {
                                //This is where the collect code will go
                        }
                        else if(args[i].equals("-solo"))
                        {
                        	System.out.println("The solo mode is chosen");
                        	robotMode = "Solo";
                        }
                        else if(args[i].equals("-multi"))
                        {
                                //this is where the multi code will go
                        	robotMode = "Multi";
                        }
                        else if(args[i].equals("-gui"))
                        {
                                new RobotUserInterface().setVisible(true);
                        }
                }
        }
}