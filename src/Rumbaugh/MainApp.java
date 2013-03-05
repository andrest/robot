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
                                String fileName = args[i + 1];
                                System.out.println("The name of the file is: "+fileName);
                        }
                        else if(args[i].equals("-collect"))
                        {
                                double x1 = Double.parseDouble(args[i + 1]);
                                double y1 = Double.parseDouble(args[i + 2]);
                                double x2 = Double.parseDouble(args[i + 3]);
                                double y2 = Double.parseDouble(args[i + 4]);
                                System.out.println("X1: "+x1+"\nY1: "+y1+"\nX2: "+x2+"\nY2: "+y2);
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