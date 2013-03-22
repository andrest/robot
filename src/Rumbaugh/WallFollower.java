package Rumbaugh;


import java.awt.Point;
import java.util.Arrays;

import javaclient3.FiducialInterface;
import javaclient3.PlayerClient;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.fiducial.PlayerFiducialItem;

public class WallFollower{
    
    
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
    static double tempPositionX1 = 0;
    static double tempPositionY = 0;
    static double tempPositionY1 = 0;
    static double tempSonar = 0;
    static double tempYaw = 0;
    static double tempYaw1 = 0;
    static double startX;
    static double startY;
    static double startYaw;
    static long startTime;
    static boolean owDone = false;
    

    static int[][] map = RobotData.INSTANCE.getMap();
    static int[][] varMap = new int[RobotData.ARRAY_HEIGHT][RobotData.ARRAY_LENGTH];
    
    static PlayerClient robot;
    static Position2DInterface posi;
    static RangerInterface rngi;
    static FiducialInterface fid;
    static PathPlanner pathPlanner;;
	static Thread t1 = new Thread(new ThreadedClass(7));
	static boolean bolThread = true;
	static Thread t2 = new Thread(new ThreadedClass(1));
	static boolean wallBoolean = false;
	static boolean outerwall = true;
	
	
    public WallFollower(PlayerClient robot, Position2DInterface posi, RangerInterface rngi, FiducialInterface fid){
    	this.robot = robot;
    	this.posi = posi;
    	this.rngi = rngi;
    	this.fid = fid;

    }
    
    public static void map(){
    	getSonars(rngi);
    	t1.start();
//    	t2.start();

    	//    	t3.start();
    	WallFollow();

    	mapWhole();
    }
    
    
    public static void mapWhole(){
    	boolean b = false;
    	int i=0,j=0;
    	while(i<map.length && !b){
    		j=0;
    		while(j<map[0].length){
    		if(map[i][j] == 0)
				b=true;
    		j++;
    		}
    		i++;

    	}    			
    	System.out.println(b);

    	if(b){    	
    		goToUnexplored();
    		System.out.println("Still unexplored areas remaining");
    		mapWhole();
    	}
    	else{
    		bolThread= false;
           	PatternCheck.wallCorrect(map);
           	PatternCheck.patternCorrect(map);
    		System.out.print("Finished mapping");
    	}
    }
    
    public static void goToUnexplored(){
    	
        int x = RobotData.INSTANCE.getLocation().x;
        int y = RobotData.INSTANCE.getLocation().y;
    	Point coor = PatternCheck.getClosestUnexplored(x, y, map,0);
    	int tarX = coor.x;
    	int tarY = coor.y;
    	//path planning, going to unexplored
   // 	pathPlanner = new PathPlanner(posi, rngi, map);
    	map[tarX][tarY] = 3;
    		pathPlanner = new PathPlanner(posi, rngi);
          pathPlanner.goToPoint(new Point(tarX, tarY));
    	explore();
 
    }
    
    public static void explore(){
    	getSonars(rngi);
    	if(frontSide > 2 && leftSide > 2 && sonarValues[8]>2){
    		int a=0;
    		posi.setSpeed(0.5, 0);
            while (a<50){
            	a++;
            	try { Thread.sleep(50);} catch (Exception e) {}
            }
        posi.setSpeed(0,0.15);
        a  = 0;
        while (a<430){
        	a++;
        	try { Thread.sleep(100);} catch (Exception e) {}
        }
  		
  			WallFollow();
  		
  }
    }
   
    
    public static void WallFollow () {
        while(!posi.isDataReady());
        tempPositionX1 = posi.getX();
        tempPositionY1 = posi.getY();
        tempYaw1 = posi.getYaw();
        // Go ahead and find a wall and align to it on the robot's left side
    	getSonars(rngi);
        getWall (posi, rngi);
        wallBoolean = true;
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
            if (frontSide < MAX_WALL_THRESHOLD && sonarValues[8] > 1.2) {
                // back up a little bit if we're bumping in front
                xSpeed   = -0.10f;
                yawSpeed = - DEF_YAW_SPEED * 3;
            } else
            	if(sonarValues[3] > 1.7){
            		turnLeft(posi);
            	}
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
 //           while(!WallFollower.fid.isDataReady());
 //           PlayerFiducialItem[] data = WallFollower.fid.getData().getFiducials();
 //           if(data.length>0)
 //           WallFollower.mapGarbage();
         
            // Move the robot
            posi.setSpeed (xSpeed, yawSpeed);

            for(int i=0;i<RobotData.ARRAY_HEIGHT;i++)
            	for(int j=0;j<RobotData.ARRAY_LENGTH;j++)
            varMap[i][j]= map[i][j];
            if(iterator%30== 0){
            	if(iterator == 30){
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
            
            try { Thread.sleep (50); } catch (Exception e) { }
    
        }
        wallBoolean = false;
        int x = RobotData.INSTANCE.getLocation().x;
        int y = RobotData.INSTANCE.getLocation().y;
        if(map[x][y] != 3 && map[x][y] != 0){
        	Point p = PatternCheck.getClosestUnexplored(x, y, map, 3);
        	x= p.x;
        	y= p.y;
        }
        posi.setSpeed(0, 0);
        if(PatternCheck.outerWallDone(varMap,x,y)){
 //       	System.out.print("Outer Wall Done");
        	PatternCheck.floodFill(0, 0, map);
        	for(int i=0;i<varMap.length;i++){
        		for(int j=0;j<varMap[0].length;j++)
        			if(varMap[i][j] == 0)
        				PatternCheck.floodFill(i, j, map);
        		break;
        	}
        	outerwall = true;
        }
        
        else
        	System.out.print("Inner");
        if(outerwall){
        int[][] testmap= new int[RobotData.ARRAY_HEIGHT][RobotData.ARRAY_LENGTH];
    	for(int i= 0;i<RobotData.INSTANCE.getMap().length; i++) 
    		for(int j= 0;j<RobotData.INSTANCE.getMap()[i].length; j++){
        			testmap[i][j] = RobotData.INSTANCE.getMap()[i][j];}
    	for(int i= 2;i<RobotData.INSTANCE.getMap().length-2; i++) 
    		for(int j= 2;j<RobotData.INSTANCE.getMap()[i].length-2; j++){
        			if(testmap[i][j] == 0 && (testmap[i][j+1] == 3 || testmap[i][j-1] == 3))
    				if(PatternCheck.UnexploredFloodFill(i, j, testmap) < 20){
    					System.out.println(true);
    					PatternCheck.UnexploredFloodFill(i, j, RobotData.INSTANCE.getMap());
    				}
    		}
        }
        posi.setSpeed(0, 0);
    }
    
    static void getWall (Position2DInterface posi, RangerInterface rngi) {
        // get all SONAR values and perform the necessary adjustments
        getSonars (rngi);
        
        // if the robot is in open space, go ahead until it "sees" the wall
        while ((leftSide > MAX_WALL_THRESHOLD) && 
                (frontSide > MAX_WALL_THRESHOLD)) {
            posi.setSpeed (0.6, 0);
            try { Thread.sleep (100); } catch (Exception e) { }
            getSonars (rngi);
        }
        while(!posi.isDataReady());
        startX = posi.getX();
        startY = posi.getY();
        startTime = System.nanoTime();
        double previousLeftSide = sonarValues[4];
        
        // rotate until we get a smaller value in sonar 0 
        while (sonarValues[4] >1) {
            previousLeftSide = sonarValues[4];
            
            // rotate more if we're almost bumping in front
             if (Math.min (leftSide, frontSide) == frontSide){
            	if(sonarValues[8] >1)
                yawSpeed = -DEF_YAW_SPEED * 5;
            	else
            		turnR180();
            }
            else
                yawSpeed = -DEF_YAW_SPEED;
            
            // Move the robot
            posi.setSpeed (-0.05, yawSpeed);
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
        while((sonarValues = rngi.getData ().getRanges ()) == null);
        for (int i = 0; i < rngi.getData ().getRanges_count (); i++) {
            if (sonarValues[i] < SONAR_MIN_VALUE)
                sonarValues[i] = SONAR_MIN_VALUE;
            else
                if (sonarValues[i] > SONAR_MAX_VALUE)
                    sonarValues[i] = SONAR_MAX_VALUE;
        }
        
        leftSide = Math.min (sonarValues[3], sonarValues [4]);
        frontSide = Math.min (Math.min(sonarValues [0], sonarValues [1]), sonarValues[2]);
    }
    
    
    static void turnLeft(Position2DInterface posi){
    	int a=0;
    	posi.setSpeed(0.15, 0.2);
    	while(a<100){
    		a++;
    		if(a%10 == 0){
    			getSonars(rngi);
    			if(sonarValues[4]<1.5)
    			mapWalls();
    			mapGarbage();
    		}
    		try{Thread.sleep(65);}catch(Exception e) { }
    	}
    	
    }

    static void turnR180 (){
    	int a=0;
    	posi.setSpeed(0, 0.3);
    	while(a<180){
    		a++;
    		try{Thread.sleep(70);}catch(Exception e) { }
    	}
    }
    static boolean validNeighbours(int i, int j){
    	boolean b= true;
    	for(int k = i-5;k<=i+5;k++)
    		for(int p = j-5;p<=j+5;p++)
    			if(k!= i || p != j)
    				if(map[k][p] == 2 || map[k][p] == 5)
    					b= false;
    	return b;
    }
    
static void mapGarbage(){
    	while(!posi.isDataReady());
		tempPositionX = posi.getX();
		tempPositionY = posi.getY();
		tempYaw = posi.getYaw();
        while(!fid.isDataReady());
        PlayerFiducialItem[] data = fid.getData().getFiducials();
        for(int i=0; i<data.length; i++) {
    		double X = data[i].getPose().getPx() +0.1;
    		double Y = data[i].getPose().getPy();
    		if(Y<0)
    			Y = Y-0.1;
    		else if(Y>0)
    			Y = Y+0.1;
    		double distance =Math.sqrt((X * X)+(Y * Y));
    		double angle = tempYaw + Math.atan2(Y,X);
    		
    		double xDistance = distance * Math.cos(angle);
    		double yDistance = distance * Math.sin(angle);
    		
    		int ind1, ind2;
    		ind1= (int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET + (tempPositionY +yDistance)));
    		ind2= (int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX + xDistance)));

    		if (map[ind1][ind2] != 1 && validNeighbours(ind1, ind2) && Arrays.asList(5,6,7).contains(data[i].getId())){
    			map[ind1][ind2] = 2;
    		}
    			
    	}
    }

    
    static void mapExplored(int ind, Position2DInterface posi){
 
        while(!posi.isDataReady());
		tempPositionX1 = posi.getX();
		tempPositionY1 = posi.getY();
		while(!posi.isDataReady());
		tempYaw1 = posi.getYaw();
		getSonars(rngi);
			tempSonar = sonarValues[ind];
		double angle = 0;
		switch (ind) {
		case 5:
			angle = tempYaw1+ Math.PI/6;
			break;
		case 6:
			angle = tempYaw1 + Math.PI/12;
			break;
		case 7:
			angle = tempYaw1 - Math.PI/6;
			break;
		case 8:
			angle = tempYaw1 - Math.PI/12;
			break;
		default :
				angle = tempYaw1;
		}
		
		while(tempSonar>0.0){
			double var11, var22;
			if(Math.abs(angle)<=((Math.PI)/2)){
    		
			var11 = (Math.sin(((Math.PI)/2) - Math.abs(angle))) * tempSonar;
			var22 = (Math.cos(((Math.PI)/2) - Math.abs(angle))) * tempSonar;
			int indX1 = (int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY1 + var22)));
			int indY1 = (int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX1 + var11)));
			int indX2 = (int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY1 - var22)));
			int indY2 = (int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX1 + var11)));
			if(angle>0 && map[indX1][indY1] == 0){
						map[indX1][indY1] = 3;
				}
				else if(angle<0 && map[indX2][indY2] == 0){
					map[indX2][indY2] = 3;
				}

			}
    
			else if(Math.abs(angle)>((Math.PI)/2)){
    			var11 = (Math.sin((Math.PI) - Math.abs(angle))) * tempSonar;
    			var22 = (Math.cos((Math.PI) - Math.abs(angle))) * tempSonar;
    			
    			int indX1 = (int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY1 + var11)));
    			int indY1 = (int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX1 - var22)));
    			int indX2 = (int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY1 - var11)));
    			int indY2 = (int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX1 - var22)));
    				if(angle>0 && map[indX1][indY1] == 0){
    					map[indX1][indY1] = 3;
    				}
    				else if(angle<0 && map[indX2][indY2] == 0){
    					map[indX2][indY2] = 3;
    				}
    		
    		}
			tempSonar = tempSonar - 0.1;
		}
    }
    
    
    /**
     * blablabla
     */
    
    
    static void mapWalls(){
    	if(sonarValues[4]<1.5){
    	if(Math.abs(tempYaw)<=((Math.PI)/2)){
    		
    		var1 = (Math.sin(((Math.PI)/2) - Math.abs(tempYaw))) * tempLeftSide;
    		var2 = (Math.cos(((Math.PI)/2) - Math.abs(tempYaw))) * tempLeftSide;
    	
    		if(tempYaw>0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX - var2)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX - var2)))] = 1;
    		}
    		if(tempYaw<0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX + var2)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY + var1)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX + var2)))] = 1;
    		}
    	}
    
    	if(Math.abs(tempYaw)>((Math.PI)/2)){
    	
    		var1 = (Math.sin((Math.PI) - Math.abs(tempYaw))) * tempLeftSide;
    		var2 = (Math.cos((Math.PI) - Math.abs(tempYaw))) * tempLeftSide;
    	
    		if(tempYaw>0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX - var1)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX - var1)))] = 1;
    		}
    		if(tempYaw<0 && map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX + var1)))] != 2){
    			map[(int)Math.round(RobotData.ARRAY_HEIGHT - RobotData.RESOLUTION*(RobotData.HEIGHT_OFFSET+(tempPositionY - var2)))][(int)Math.round(RobotData.RESOLUTION*(RobotData.LENGTH_OFFSET+(tempPositionX + var1)))] = 1;
    		}
    	}
    	}
       	PatternCheck.patternCorrect(map);
    }
    	 
    static Point neighbour(Point p){
    	Point q = null;
    	for(int i= p.x-1;i<= p.x+1;i++)
    		for(int j= p.y-1; j<=p.y+1;j++)
    			if(map[i][j] == 3 || map[i][j] == 8 || map[i][j] == 0 || map[i][j] == 7)
    				q= new Point(i,j);
    	return q;
    }
    
}
class ThreadedClass implements Runnable{
	int alfa;
	public ThreadedClass(int i){
		alfa = i;
	}
	@Override
	public void run() {
		while(WallFollower.bolThread){
		WallFollower.mapExplored(alfa, WallFollower.posi);
		WallFollower.mapGarbage();
		try { Thread.sleep(25); } catch (Exception e) {}
		}		
	}
	
}
class MapFront implements Runnable{

	int alfa;
	public MapFront(int i){
		alfa = i;
	}
	@Override
	public void run() {
		while(WallFollower.bolThread){
		WallFollower.mapExplored(alfa, WallFollower.posi);
		try { Thread.sleep(50); } catch (Exception e) {}
		}		
	}
	
}

