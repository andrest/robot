package Rumbaugh.Robot;


import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import javaclient3.FiducialInterface;
import javaclient3.GripperInterface;
import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;
import javaclient3.PlayerException;
import Rumbaugh.GarbageCollector;
import Rumbaugh.PathPlanner;
import Rumbaugh.RobotData;
import Rumbaugh.WallFollower;
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
                pathPlanner = new PathPlanner(pos2d_0, sonar_0);
        }
        
        public void crashRobots() {
        	//Thread worker = new Thread();
		    for (int i = 1; i <= 2; i++)	{
		        try{
		        		final PlayerClient robot = new PlayerClient(SERVER_NAME, PORT_NUMBER);
		        		final Position2DInterface  pos2d = robot.requestInterfacePosition2D(i, PLAYER_OPEN_MODE);
		    	        robot.runThreaded(-1, -1);
		    	        pos2d.setSpeed(1, 0);
		
		    	        Runnable crashRobot = new Runnable(){	
		    	        	public void run() {
		    	        		long timeStarted = System.currentTimeMillis();
		    	        		while(true){
		    	        			try { Thread.sleep(500);} catch (Exception e) {}
			    	    	        while(!pos2d.isDataReady());
			    	    	        if(pos2d.getData().getStall() == 1 || timeStarted + 15000 < System.currentTimeMillis()) {
			    	    	        	pos2d.setSpeed(0, 0);
			    	    	        	robot.close();
			    	    	        	return;
			    	    	        }
			    	    	        
		    	        		}
		    	            }
		    	        };
		    	        Thread worker = new Thread(crashRobot);
		    	        worker.start();
		        } catch(PlayerException exception){
		            System.out.println(exception);
		            System.exit(1);
		        }
	        }
        }
        public void startMapping(){
                RobotData.INSTANCE.initMap();
                RobotData.INSTANCE.setPos2d(pos2d_0);
                
                //try {PathPlanner.testMap();} catch (IOException e) {};
                //GarbageCollector garbageCollector = new GarbageCollector(gripper_0, pos2d_0, sonar_0, new Point(160,220), new Point(160,220));          
                //garbageCollector.startCollection();
                WallFollower wf = new WallFollower(robot, pos2d_0, sonar_0, fiducial_0);
                WallFollower.map();
        }

        public void collectGarbage(double x1, double y1, double x2, double y2) {
                GarbageCollector garbageCollector = new GarbageCollector(gripper_0, pos2d_0, sonar_0, 
                		new Point(RobotData.convertY(y1),RobotData.convertX(x1)), new Point(RobotData.convertY(y2),RobotData.convertX(x2)));  
                //System.out.println(new Point(RobotData.convertY(y1),RobotData.convertX(x1)) + "  \n" + new Point(RobotData.convertY(y2),RobotData.convertX(x2)));
                garbageCollector.startCollection();
        }

         
}