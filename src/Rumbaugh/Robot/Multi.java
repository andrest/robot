package Rumbaugh.Robot;

import javaclient3.FiducialInterface;
import javaclient3.GripperInterface;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

/**
 * 
 * This is the class that will initialise
 * all the robots on the map.
 *
 */
public class Multi extends Solo {
	private static final short PLAYER_CONSTANT = PlayerConstants.PLAYER_OPEN_MODE;
	//Elements for the robot that is in index 1
	private Position2DInterface pos2d_1;
	private RangerInterface sonar_1;
	private GripperInterface gripper_1;
	private FiducialInterface fiducial_1;
	//Elements for the robot that is in index 2 this is the last and final robot
	private Position2DInterface pos2d_2;
	private RangerInterface sonar_2;
	private GripperInterface gripper_2;
	private FiducialInterface fiducial_2;
	
	public Multi(){
		super();//Call the super class
		pos2d_1 = robot.requestInterfacePosition2D(1, PLAYER_CONSTANT);
		sonar_1 = robot.requestInterfaceRanger(1, PLAYER_CONSTANT);
		gripper_1 = robot.requestInterfaceGripper(1, PLAYER_CONSTANT);
		fiducial_1 = robot.requestInterfaceFiducial(1, PLAYER_CONSTANT);
		
		pos2d_2 = robot.requestInterfacePosition2D(2, PLAYER_CONSTANT);
		sonar_2 = robot.requestInterfaceRanger(2, PLAYER_CONSTANT);
		gripper_1 = robot.requestInterfaceGripper(2, PLAYER_CONSTANT);
		fiducial_2 = robot.requestInterfaceFiducial(2, PLAYER_CONSTANT);
	}
	
	public void makeRobotMove(){
		pos2d_1.setSpeed(1, 0);
		pos2d_2.setSpeed(1, 0);
	}

}
