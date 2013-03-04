package Rumbaugh.Robot;


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
	
	/**
	 * In this constructor for the class
	 * will make a connection to the player/stage
	 * client and initialise the elements of the robot
	 */
	public Solo(){
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
	}
	
	public void startMapping(){
		RobotData.INSTANCE.initMap();
		WallFollower.run(robot, pos2d_0, sonar_0);
	}



	 
}
