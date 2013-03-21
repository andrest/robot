package Rumbaugh.Robot;


import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Rumbaugh.GarbageCollector;
import Rumbaugh.PathPlanner;
import Rumbaugh.RobotData;
import Rumbaugh.WallFollower;

import javaclient3.FiducialInterface;
import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

/**
 * 
 * This is the class where the program
 * will initialise one robot, to be used in the application 
 *
 */
public class Solo {
        private static final String SERVER_NAME = "localhost";
        private static final int PORT_NUMBER = 6665;
        private static final short PLAYER_OPEN_MODE = PlayerConstants.PLAYER_OPEN_MODE;
        protected PlayerClient robot;
        //These are the elements of the first robot that is placed in index 0
        private Position2DInterface pos2d_0;
        private RangerInterface sonar_0;
        private GripperInterface gripper_0;
        private FiducialInterface fiducial_0;
        
        private static PathPlanner pathPlanner;
        static ArrayList<String[]> strng = new ArrayList<String[]>();
        /**
         * In this constructor for the class
         * will make a connection to the player/stage
         * client and initialise the elements of the robot
         */
        public Solo(){
        	
        		
                crashRobots(); 
                 
                robot = null;
                pos2d_0 = null;
                sonar_0 = null;
                gripper_0 = null;
                fiducial_0 = null;
                try{
                        robot = new PlayerClient(SERVER_NAME, PORT_NUMBER);
                        pos2d_0 = robot.requestInterfacePosition2D(0, PLAYER_OPEN_MODE);
                        sonar_0 = robot.requestInterfaceRanger(0, PLAYER_OPEN_MODE);
                        gripper_0 = robot.requestInterfaceGripper(0, PLAYER_OPEN_MODE);
                        fiducial_0 = robot.requestInterfaceFiducial(0, PLAYER_OPEN_MODE);
                }
                catch(PlayerException exception){
                        System.out.println(exception);
                        System.exit(1);
                }
                robot.runThreaded(-1, -1); 
                //pathPlanner = new PathPlanner(pos2d_0, sonar_0);
        }
        
        public void crashRobots() {
        //int i;
        for (int i = 1; i <= 2; i++)	{
	        try{
	        		final PlayerClient robot = new PlayerClient(SERVER_NAME, PORT_NUMBER);
	        		final Position2DInterface  pos2d = robot.requestInterfacePosition2D(i, PLAYER_OPEN_MODE);
	    	        robot.runThreaded(-1, -1);
	    	        pos2d.setSpeed(-1, 0);

	    	        Thread thread = new Thread(){
	    	        	public void run() {
	    	        		while(true){
	    	        			try { Thread.sleep(500);} catch (Exception e) {}
		    	    	        while(!pos2d.isDataReady());
		    	    	        //System.out.println(pos2d.getData().getStall());
		    	    	        if(pos2d.getData().getStall() == 1) {
		    	    	        	pos2d.setSpeed(0, 0);
		    	    	        	return;
		    	    	        }
		    	    	        
	    	        		}
	    	            }
	    	        };
	    	        thread.run();
	        } catch(PlayerException exception){
                System.out.println(exception);
                System.exit(1);
	        }
	    	
	        }
        }
        public void startMapping(){
                RobotData.INSTANCE.initMap();
                RobotData.INSTANCE.setPos2d(pos2d_0);
                
                /*try {
					PathPlanner.testMap();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
                //GarbageCollector garbageCollector = new GarbageCollector(gripper_0, pos2d_0, sonar_0, new Point(0,0), new Point(0,0));          
                //garbageCollector.startCollection();
                WallFollower wf = new WallFollower(robot, pos2d_0, sonar_0, fiducial_0);
                WallFollower.map();
        }

                public void collectGarbage(double x1, double y1, double x2, double y2) {
                        GarbageCollector garbageCollector = new GarbageCollector(gripper_0, pos2d_0, sonar_0, new Point((int)x1,(int)y1), new Point((int)x2,(int)y2));          
                        garbageCollector.startCollection();
                        System.out.println("X1: "+x1+"\nY1: "+y1+"\nX2: "+x2+"\nY2: "+y2);
                }

         
}