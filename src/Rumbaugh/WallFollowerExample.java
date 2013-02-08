package com.Rumbaugh;

/*
 *  Player Java Client 3 Examples - WallFollowerExample.java
 *  Copyright (C) 2006 Radu Bogdan Rusu
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: WallFollowerExample.java,v 1.1 2005/11/27 19:10:01 rusu Exp $
 *
 */

import java.text.NumberFormat;

import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

public class WallFollowerExample {
    
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
    static double SPEED   = 0.2;
    static double DEF_YAW_SPEED = 0.15;
    
    // array to hold the SONAR sensor values
    static double[] sonarValues;
    static double frontSide, leftSide;
    
    static int l= 170;  // array size
    static int[][] a = new int[l][l];   // the actual array
    static int aa=3;				// resolution
    static int k=30;				
    static int alfa= 0;
    
    
    public static void main (String[] args) {
        PlayerClient        robot = null;
        Position2DInterface posi  = null;
        RangerInterface     rngi  = null;
//        Map m= new Map();
//       m.drawMap();
        for(int i=0;i<l;i++)
        	for(int j=0;j<l;j++)
        a[i][j]=0;
        try {
            // Connect to the Player server and request access to Position and Sonar
            robot  = new PlayerClient ("localhost", 6665);
            posi = robot.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
            rngi = robot.requestInterfaceRanger     (0, PlayerConstants.PLAYER_OPEN_MODE);
        } catch (PlayerException e) {
            System.err.println ("WallFollowerExample: > Error connecting to Player: ");
            System.err.println ("    [ " + e.toString() + " ]");
            System.exit (1);
        }
        
        robot.runThreaded (-1, -1);
        
        // Go ahead and find a wall and align to it on the robot's left side
        getWall (posi, rngi);
        
        while (true) {
            // get all SONAR values and perform the necessary adjustments
            getSonars (rngi);
            xSpeed   = SPEED;
            yawSpeed = 0;
          
            // if we're getting too close to the wall with the front side...
            if (frontSide < MAX_WALL_THRESHOLD) {
                // back up a little bit if we're bumping in front
                xSpeed   = -0.10f;
                yawSpeed = - DEF_YAW_SPEED * 10;
            } else
            	if(leftSide>2.5)
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
                        xSpeed   = SPEED / 2;
                        yawSpeed = DEF_YAW_SPEED;
                    }
            if(leftSide<1.2)
            	if(posi.getYaw()>1.45 && posi.getYaw()<1.7){
            		a[(int)Math.round(aa*(k-posi.getY()))][(int)Math.round(aa*(k+posi.getX()-leftSide))]=1;
            		if(frontSide<1.2)
            			a[(int)Math.round(aa*(k-posi.getY()-frontSide))][(int)(aa*(k+posi.getX()))]=1;
            	}
            	else if(posi.getYaw()<0.2 && posi.getYaw()>-0.2){
            		a[(int)Math.round(aa*(k-posi.getY()-leftSide))][(int)Math.round(aa*(k+posi.getX()))]=1;
            		if(frontSide<1.2)
            			a[(int)(aa*(k-posi.getY()))][(int)Math.round(aa*(k+posi.getX()+frontSide))]=1;
            		}
            	else if(posi.getYaw()<-1.4 && posi.getYaw()>-1.7){
            			a[(int)Math.round(aa*(k-posi.getY()))][(int)Math.round(aa*(k+posi.getX()+leftSide))]=1;
            			if(frontSide<1.2)
            				a[(int)Math.round(aa*(k-posi.getY()+frontSide))][(int)(aa*(k+posi.getX()))]=1;

            			}
            	else if(posi.getYaw()<-2.9 || posi.getYaw()>2.9){
            			a[(int)Math.round(aa*(k-posi.getY()+leftSide))][(int)Math.round(aa*(k+posi.getX()))]=1;
            			if(frontSide<1.2)
            				a[(int)(aa*(k-posi.getY()))][(int)(aa*(k+posi.getX()-frontSide))]=1;
            	
            		}
            	
            // Move the robot
            posi.setSpeed (xSpeed, yawSpeed);
            for(int i=0;i<l;i++){
            	for(int j=0;j<l;j++)
            		System.out.print(a[i][j]+" ");
            	System.out.println();
            }
            alfa++;
            if(alfa%50==0)
            	PatternCheck.patternCorrect(a);

            try { Thread.sleep (100); } catch (Exception e) { }
    
        }
    }
    
    static void getWall (Position2DInterface posi, RangerInterface rngi) {
        // get all SONAR values and perform the necessary adjustments
        getSonars (rngi);
        
        // if the robot is in open space, go ahead until it "sees" the wall
        while ((leftSide > MAX_WALL_THRESHOLD+0.5) && 
                (frontSide > MAX_WALL_THRESHOLD)) {
            posi.setSpeed (0.1, 0.3);
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