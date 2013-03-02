package Rumbaugh;

import java.text.NumberFormat;

import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;

public class WallFollower {
    
    static NumberFormat fmt = NumberFormat.getInstance ();
    
    // define minimum/maximum allowed values for the SONAR sensors
    static double SONAR_MIN_VALUE = 0.2;
    static double SONAR_MAX_VALUE = 5.0;
    
    // define the wall threshold
    static double MIN_WALL_THRESHOLD  = 0.4;
    static double MAX_WALL_THRESHOLD  = 0.7;
    static final double PD_P_CONSTANT = -0.75;
    static final double PD_D_CONSTANT = -1.5;
    
    // define the default translational and rotational speeds
    static double xSpeed, yawSpeed;
    static double SPEED   = 0.4;
    static double DEF_YAW_SPEED = 0.15;
    
    // array to hold the SONAR sensor values
    static double[] sonarValues;
    static double frontSide, leftSide;
    
    static double var1 = 0; 
    static double var2 = 0;
    static double tempLeftSide = 0;
    static double tempPositionX = 0;
    static double tempPositionY = 0;
    static double tempYaw = 0;
    
    static final int HEIGHT_OFFSET=15;
    static final int LENGTH_OFFSET=22;

    static int[][] map = RobotData.INSTANCE.getMap();
    static int[][] varMap = new int[RobotData.ARRAY_HEIGHT][RobotData.ARRAY_LENGTH];
    
    public static void run (PlayerClient robot, Position2DInterface posi, RangerInterface rngi) {

        
        // Go ahead and find a wall and align to it on the robot's left side
        getWall (posi, rngi);
        for(int i=0;i<RobotData.ARRAY_HEIGHT;i++)
        	for(int j=0;j<RobotData.ARRAY_LENGTH;j++)
        varMap[i][j]= map[i][j];
        while (!PatternCheck.outerWallDone(varMap)) {
            // get all SONAR values and perform the necessary adjustments
            getSonars (rngi);
            xSpeed   = SPEED;
            yawSpeed = 0;
          
            // if we're getting too close to the wall with the front side...
            if (frontSide < MAX_WALL_THRESHOLD) {
                // back up a little bit if we're bumping in front
                xSpeed   = -0.10f;
                yawSpeed = - DEF_YAW_SPEED * 3;
            } else
            	if(leftSide > 2.5)
            		turnLeft(posi);
            	else
                // if we're getting too close to the wall with the left side...
                if (leftSide < MIN_WALL_THRESHOLD && frontSide<0.3) {
                    // move slower at corners

                }
                else if(leftSide<MIN_WALL_THRESHOLD){
                    xSpeed   = 0;
                    yawSpeed = - DEF_YAW_SPEED/1.1 ;
                }
                else
                    // if we're getting too far away from the wall with the left side...
                    if (leftSide > MAX_WALL_THRESHOLD-0.1&&leftSide<3) {
                        // move slower at corners
                        xSpeed   = SPEED / 4;
                        yawSpeed = DEF_YAW_SPEED;
                    }
            if(sonarValues[4]<1.2){
            	tempLeftSide = sonarValues [4];
        		tempPositionX = posi.getX();
        		tempPositionY = posi.getY();
        		tempYaw = posi.getYaw();
            	
            	if(Math.abs(tempYaw)<((Math.PI)/2)){
            		
            		var1 = (Math.sin(((Math.PI)/2) - Math.abs(tempYaw))) * tempLeftSide;
            		var2 = (Math.cos(((Math.PI)/2) - Math.abs(tempYaw))) * tempLeftSide;
            	
            		if(tempYaw>0){
            			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var2)))] = 1;
            		}
            		if(tempYaw<0){
            			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var2)))] = 1;
            		}
            	}
            
            	if(Math.abs(tempYaw)>((Math.PI)/2)){
            	
            		var1 = (Math.sin((Math.PI) - Math.abs(tempYaw))) * tempLeftSide;
            		var2 = (Math.cos((Math.PI) - Math.abs(tempYaw))) * tempLeftSide;
            	
            		if(tempYaw>0){
            			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var1)))] = 1;
            		}
            		if(tempYaw<0){
            			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var1)))] = 1;
            		}
            	}
            }
            	
            // Move the robot
            posi.setSpeed (xSpeed, yawSpeed);
           	PatternCheck.patternCorrect(map);
            for(int i=0;i<RobotData.ARRAY_HEIGHT;i++)
            	for(int j=0;j<RobotData.ARRAY_LENGTH;j++)
            varMap[i][j]= map[i][j];
        	
            try { Thread.sleep (100); } catch (Exception e) { }
    
        }
        PatternCheck.floodFill(0, 0, map);
       	PatternCheck.wallCorrect(map);
        posi.setSpeed(0, 0);

        System.out.println("Finished mapping outer wall");
    }
    
    static void getWall (Position2DInterface posi, RangerInterface rngi) {
        // get all SONAR values and perform the necessary adjustments
        getSonars (rngi);
        
        // if the robot is in open space, go ahead until it "sees" the wall
        while ((leftSide > MAX_WALL_THRESHOLD+0.5) && 
                (frontSide > MAX_WALL_THRESHOLD)) {
            posi.setSpeed (0.5, 0);
            try { Thread.sleep (100); } catch (Exception e) { }
            getSonars (rngi);
        }
        
        double previousLeftSide = sonarValues[0];
        
        // rotate until we get a smaller value in sonar 0 
        while (sonarValues[0] <= previousLeftSide) {
            previousLeftSide = sonarValues[0];
            
            // rotate more if we're almost bumping in front
            if (Math.min (leftSide, frontSide) == frontSide)
                yawSpeed = -DEF_YAW_SPEED * 3;
            else
                yawSpeed = -DEF_YAW_SPEED;
            
            // Move the robot
            posi.setSpeed (0, yawSpeed);
            try { Thread.sleep (100); } catch (Exception e) { }
            
            getSonars (rngi);
        }
        posi.setSpeed (0, 0);
    }
   static double getError() {
    	double distance = leftSide;
    	if (distance<MIN_WALL_THRESHOLD) {
    	return(distance-MIN_WALL_THRESHOLD);
    	} else if (distance>MAX_WALL_THRESHOLD) {
    	return(distance-MAX_WALL_THRESHOLD);
    	} else return(0.0);
    	}
    
    static void getSonars (RangerInterface rngi) {
        while (!rngi.isDataReady ());
        sonarValues = rngi.getData ().getRanges ();
        
        for (int i = 0; i < rngi.getData ().getRanges_count (); i++)
            if (sonarValues[i] < SONAR_MIN_VALUE)
                sonarValues[i] = SONAR_MIN_VALUE;
            else
                if (sonarValues[i] > SONAR_MAX_VALUE)
                    sonarValues[i] = SONAR_MAX_VALUE;
    
        leftSide = Math.min (sonarValues[3], sonarValues [4]);
        frontSide = Math.min (Math.min(sonarValues [0], sonarValues [1]), sonarValues[2]);
    }
    
    
    static void turnLeft(Position2DInterface posi){
    	int a=0;
    	posi.setSpeed(0.15, 0.3);
    	while(a<100){
    		a++;
    		try{Thread.sleep(50);}catch(Exception e) { }
    	}
    	posi.setSpeed(0, 0);
    	
    }
    
    
}