package Rumbaugh;

import Rumbaugh.Robot.*;
import Rumbaugh.View.RobotUserInterface;

/**
 * This is the main starting point of the application
 * where the user will enter commands on the terminal and 
 * they will be processed here
 *
 */
public class MainApp{
	public static void main(String [] args){
		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equals("-explore"))
			{
				//this is where the explore code goes
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
				Solo solo = new Solo();
				//Multi multi = new Multi();
				solo.makeRobotMove();
				//multi.makeRobotMove();
			}
			else if(args[i].equals("-multi"))
			{
				//this is where the multi code will go
			}
			else if(args[i].equals("-gui"))
			{
				new RobotUserInterface().setVisible(true);
			}
		}
	}
}
