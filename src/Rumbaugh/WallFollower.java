package Rumbaugh;


import javaclient3.FiducialInterface;
import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.fiducial.PlayerFiducialItem;

public class WallFollower implements Runnable{
    
    
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
    static double tempSonar = 0;
    static double tempYaw = 0;
    static Position2DInterface pos;
    static double startX;
    static double startY;
    static double startYaw;
    static long startTime;
    static final int HEIGHT_OFFSET=15;
    static final int LENGTH_OFFSET=22;

    static int[][] map = RobotData.INSTANCE.getMap();
    static int[][] varMap = new int[RobotData.ARRAY_HEIGHT][RobotData.ARRAY_LENGTH];
    
    public static void map (PlayerClient robot, Position2DInterface posi, RangerInterface rngi, FiducialInterface fid) {

    	Thread t1 = new Thread(new WallFollower());
    	
        pos = posi;
        // Go ahead and find a wall and align to it on the robot's left side
    	getSonars(rngi);
   // 	t1.start();
       	
        getWall (posi, rngi);
        for(int i=0;i<RobotData.ARRAY_HEIGHT;i++)
        	for(int j=0;j<RobotData.ARRAY_LENGTH;j++)
        		varMap[i][j]= map[i][j];
        boolean bol = true;
        int iterator = 0;
        int counter = 0;
        int prevcounter = 0;
        while (bol) {
        	iterator++;
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
            	if(leftSide > 2.5 && frontSide > 0.5)
            		turnLeft(posi);
            	else
                // if we're getting too close to the wall with the left side...
                if (leftSide < MIN_WALL_THRESHOLD && frontSide<0.3) {
                    // move slower at corners
                	xSpeed = 0.1;
                	yawSpeed = - DEF_YAW_SPEED;
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
            
            //start of code that maps walls
            while(!posi.isDataReady());
    		tempPositionX = posi.getX();
    		tempPositionY = posi.getY();
    		tempYaw = posi.getYaw();
            if(sonarValues[4]<1.5){
            	tempLeftSide = sonarValues [4];
            	mapWalls();
            }
            System.out.println(System.nanoTime());
            while(!fid.isDataReady());
            PlayerFiducialItem[] data = fid.getData().getFiducials();
            mapGarbage(posi, fid);
         
            // Move the robot
            posi.setSpeed (xSpeed, yawSpeed);

            for(int i=0;i<RobotData.ARRAY_HEIGHT;i++)
            	for(int j=0;j<RobotData.ARRAY_LENGTH;j++)
            varMap[i][j]= map[i][j];
            if(iterator%40== 0){
            	if(iterator == 40){
                	for(int i= 0;i<RobotData.ARRAY_HEIGHT; i++)
                		for(int j= 0;j<RobotData.ARRAY_LENGTH; j++){
                			if(map[i][j] == 1)
                				counter++;
                		}
                	prevcounter = counter;
            	}
            	else{
            		counter = 0;
                	for(int i= 0;i<RobotData.ARRAY_HEIGHT; i++)
                		for(int j= 0;j<RobotData.ARRAY_LENGTH; j++){
                			if(map[i][j] == 1)
                				counter++;
                		}
                	if(counter<= prevcounter)
                		bol = false;
                	prevcounter = counter;
            	}
            }
            
            try { Thread.sleep (100); } catch (Exception e) { }
    
        }
        
        PatternCheck.floodFill(0, 0, map);
       	PatternCheck.wallCorrect(map);
       	PatternCheck.patternCorrect(map);
        posi.setSpeed(0, 0);

        System.out.println("Finished mapping outer wall");
    }
    
    static void getWall (Position2DInterface posi, RangerInterface rngi) {
        // get all SONAR values and perform the necessary adjustments
        getSonars (rngi);
        
        // if the robot is in open space, go ahead until it "sees" the wall
        while ((leftSide > MAX_WALL_THRESHOLD) && 
                (frontSide > MAX_WALL_THRESHOLD)) {
            posi.setSpeed (1, 0);
            try { Thread.sleep (100); } catch (Exception e) { }
            getSonars (rngi);
        }
        while(!posi.isDataReady());
        startX = posi.getX();
        startY = posi.getY();
        startTime = System.nanoTime();
        double previousLeftSide = sonarValues[4];
        
        // rotate until we get a smaller value in sonar 0 
        while (sonarValues[4] <= previousLeftSide) {
            previousLeftSide = sonarValues[4];
            
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
    		mapWalls();
    		try{Thread.sleep(50);}catch(Exception e) { }
    	}
    	posi.setSpeed(0, 0);
    	
    }
    static boolean validNeighbours(int i, int j){
    	boolean b= true;
    	for(int k = i-1;k<=i+1;k++)
    		for(int p = j-1;p<=j+1;p++)
    			if(k!= i || p != j)
    				if(map[k][p] == 2)
    					b= false;
    	return b;
    }
    
    static void mapGarbage(Position2DInterface posi, FiducialInterface fid){
    	while(!posi.isDataReady());
		tempPositionX = posi.getX();
		tempPositionY = posi.getY();
		tempYaw = posi.getYaw();
        while(!fid.isDataReady());
        PlayerFiducialItem[] data = fid.getData().getFiducials();
        for(int i=0; i<data.length; i++) {

    		double X = data[i].getPose().getPx();
    		double Y = data[i].getPose().getPy();
    		
    		double distance =Math.sqrt((X * X)+(Y * Y));
    		double angle = tempYaw + Math.atan(Y/X);
    		
    		double xDistance = distance * Math.cos(angle);
    		double yDistance = distance * Math.sin(angle);
    		
    		int ind1, ind2;
    		ind1= (int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET + (tempPositionY +yDistance)));
    		ind2= (int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + xDistance)));

    		if (map[ind1][ind2] != 1 && validNeighbours(ind1, ind2)){
    			map[ind1][ind2] = 2;
    		}
    			
    	}
    }
    
    static void mapExplored(int ind, Position2DInterface posi){
 
        while(!posi.isDataReady());
		tempPositionX = posi.getX();
		tempPositionY = posi.getY();
		while(!posi.isDataReady());
		tempYaw = posi.getYaw();
			tempSonar = sonarValues[ind];
		double angle = 0;
		switch (ind) {
		case 5:
			angle = tempYaw+ Math.PI/6;
			break;
		case 6:
			angle = tempYaw + Math.PI/12;
			break;
		case 7:
			angle = tempYaw - Math.PI/6;
			break;
		case 8:
			angle = tempYaw - Math.PI/12;
			break;
		}
		tempSonar = Math.min(tempSonar, 2.5);
		while(tempSonar>0.0){
			if(Math.abs(angle)<=((Math.PI)/2)){
    		
				var1 = (Math.sin(((Math.PI)/2) - Math.abs(angle))) * tempSonar;
				var2 = (Math.cos(((Math.PI)/2) - Math.abs(angle))) * tempSonar;
    	
				if(angle>0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var1)))] == 0){
					map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var1)))] = 3;
				}
				else if(angle<0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var1)))] == 0){
					map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var1)))] = 3;
				}

			}
    
			else if(Math.abs(angle)>((Math.PI)/2)){
    			var1 = (Math.sin((Math.PI) - Math.abs(angle))) * tempSonar;
    			var2 = (Math.cos((Math.PI) - Math.abs(angle))) * tempSonar;
    	
    				if(angle>0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var2)))] == 0){
    					map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var2)))] = 3;
    				}
    				else if(angle<0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var2)))] == 0){
    					map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var2)))] = 3;
    				}
    		
    		}
			tempSonar = tempSonar - 0.3;
		}
    }
    
    
    /**
     * blablabla
     */
    
    
    static void mapWalls(){
    	
    	if(Math.abs(tempYaw)<=((Math.PI)/2)){
    		
    		var1 = (Math.sin(((Math.PI)/2) - Math.abs(tempYaw))) * tempLeftSide;
    		var2 = (Math.cos(((Math.PI)/2) - Math.abs(tempYaw))) * tempLeftSide;
    	
    		if(tempYaw>0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var2)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var2)))] = 1;
    		}
    		if(tempYaw<0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var2)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var2)))] = 1;
    		}
    	}
    
    	if(Math.abs(tempYaw)>((Math.PI)/2)){
    	
    		var1 = (Math.sin((Math.PI) - Math.abs(tempYaw))) * tempLeftSide;
    		var2 = (Math.cos((Math.PI) - Math.abs(tempYaw))) * tempLeftSide;
    	
    		if(tempYaw>0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var1)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX - var1)))] = 1;
    		}
    		if(tempYaw<0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var1)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(LENGTH_OFFSET+(tempPositionX + var1)))] = 1;
    		}
    	}
       	PatternCheck.patternCorrect(map);
    }
    
    	public void run(){
    		while(true){
    		mapExplored(7, pos);
    		try { Thread.sleep(2000); } catch (Exception e) {}
    		}
    	}
    	 
    
}