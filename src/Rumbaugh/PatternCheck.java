package Rumbaugh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class provides a pattern correction method, used where the robot measurements
 * are not that accurate.
 * It also has a flood fill method, used to determine when the robot has finished 
 * mapping the outer wall.
 */
public class PatternCheck {
	
	
static void patternCorrect(int[][] a){
    	for(int i=3;i<RobotData.ARRAY_HEIGHT-3;i++)
    		for(int j=3;j<RobotData.ARRAY_LENGTH-3;j++)
    		{
    			if(a[i][j]==1 && a[i][j+2]==1 && (a[i][j+1] == 0 || a[i][j+1] == 3))
    				a[i][j+1]=1;
    			if(a[i][j]==1 && a[i+2][j]==1 && (a[i+1][j] == 0 || a[i+1][j] == 3))
    				a[i+1][j]=1;
    			if(a[i][j] == 0)
    				for(int k=i-1;k<= i+1;k++)
    					for(int p = j-1;p<= j+1;p++)
    						if(a[k][p] == 1)
    							a[i][j] = 3; 
    			if(a[i][j] == 0){
    				if(a[i][j-1] == 3 && a[i][j+1] == 3  && a[i-1][j] == 3 && a[i+1][j] == 3)
    					a[i][j] = 3;
    			}
    		}
	}
static void wallCorrect(int[][] a){
	for(int i=3;i<RobotData.ARRAY_HEIGHT-3;i++)
		for(int j=3;j<RobotData.ARRAY_LENGTH-3;j++)
		{
			if((a[i][j] == 0 || a[i][j] == 3 || a[i][j] == 2)&& a[i][j+1] == 1 && 
					a[i][j+2] == 1 && a[i][j+3] == 1 && 
					(a[i][j+4] == 0 || a[i][j+4] == 3 || a[i][j+4] == 2)){
				int k = i;
				while (a[k][j+2] == 1)
					k++;
				for(int p = i;p< k ; p++){
					if(a[p][j] == 0 || a[p][j] == 3 || a[p][j] == 2)
						if(a[p][j+1] != 2)
						a[p][j+1] = 3;
					if(a[p][j+4] == 0 || a[p][j+4] == 3 || a[p][j+4]== 2)
						if(a[p][j+3] != 2)
						a[p][j+3] = 3;
				}
						
			}
			else if((a[i][j] == 0 || a[i][j] == 3 || a[i][j] == 2) && 
					a[i+1][j] == 1 && a[i+2][j] == 1 && a[i+3][j] == 1 &&
					(a[i+4][j] == 0 || a[i+4][j] == 3 || a[i+4][j] == 2)){
				int k=j;
				while (a[i+2][k] == 1)
					k++;
				for(int p = j;p<k;p++){
					if(a[i][p] == 0 || a[i][p] == 3 || a[i][p] == 2)
						if(a[i+1][p] !=2)
						a[i+1][p] = 3;
					if(a[i+4][p] == 0 || a[i+4][p] == 3 || a[i+4][p] == 2)
						if(a[i+3][p] !=2)
						a[i+3][p] = 3;
				}
			}
		}
}
public static void floodFill(int x, int y, int[][] array) {


	Queue<String> queue = new LinkedList<String>();
	int counter = 0;
	queue.add(array[x][y]+ " "+ x + " "+y);
	String s;
	int w,e;
	while(!queue.isEmpty()){
		s = queue.remove();
		if(s.startsWith("0") || s.startsWith("3")){
			x=Integer.parseInt(s.split(" ")[1]);
			y=Integer.parseInt(s.split(" ")[2]);
			w=y;
			e=y;
			boolean b= true;
			while(w>0 && b){
				if(array[x][w-1] == 0 || array[x][w-1] == 3)
					w--;
				else
					b=false;
			}
			b= true;
				while(e<RobotData.ARRAY_LENGTH-1 && b)
					if(array[x][e+1] == 0 || array[x][e+1] == 3)
						e++;
					else 
						b= false;
				
			for(int j= w;j<=e;j++){
				array[x][j]= 8;
				if(x>0)
					if(array[x-1][j] == 0 || array[x-1][j] == 3)
						queue.add(array[x-1][j] + " " + (x-1) +" "+ j);
				if(x<RobotData.ARRAY_HEIGHT - 1)
					if(array[x+1][j] == 0 || array[x+1][j] == 3)
						queue.add(array[x+1][j] + " " + (x+1) + " " + j);
			}
		}

	}
	return;

}

public static int UnexploredFloodFill(int x, int y, int[][] array) {


	Queue<String> queue = new LinkedList<String>();
	ArrayList<String> changed = new ArrayList<String>();
	int counter = 0;
	queue.add(array[x][y]+ " "+ x + " "+y);
	String s;
	int w,e;
	while(!queue.isEmpty()){
		s = queue.remove();
		if(s.startsWith("0")){
			
			x=Integer.parseInt(s.split(" ")[1]);
			y=Integer.parseInt(s.split(" ")[2]);
			w=y;
			e=y;
			boolean b= true;
			while(w>0 && b){
				if(array[x][w-1] == 0){
					w--;
				}
				else
					b=false;
			}
		
			b= true;
				while(e<RobotData.ARRAY_LENGTH-1 && b)
					if(array[x][e+1] == 0){
						e++;
					}
					else 
						b= false;
			
			for(int j= w;j<=e;j++){
				array[x][j]= 8;
			if(!changed.contains(x + " " + j))
				changed.add(x + " " + j);
				if(x>0)
					if(array[x-1][j] == 0){
						queue.add(array[x-1][j] + " " + (x-1) +" "+ j);
						
					}
				if(x<RobotData.ARRAY_HEIGHT - 1)
					if(array[x+1][j] == 0){
						queue.add(array[x+1][j] + " " + (x+1) + " " + j);
			
					}
			}
		}

	}
	return changed.size();

}



public static boolean outerWallDone(int[][] array, int x, int y){

	boolean b= false;
	floodFill(x ,y , array);
	
	for(int i=0;i<RobotData.ARRAY_HEIGHT;i++)
		for(int j=0;j<RobotData.ARRAY_LENGTH;j++)
			if(array[i][j]== 0 || array[i][j] == 3)
				b = true;
	return b;
}

public static String getClosestUnexplored(int x, int y, int[][] map){
	double distance;
	double prev = 1000;
	String coordinates = null;
	for(int i=0;i<map.length;i++)
		for(int j=0;j<map[0].length;j++)
			if(map[i][j] == 0 && (i!= x || j!= y)){
				distance = Math.sqrt(((i - x)* (i - x)) + ((j - y)* (j - y)));
				if(distance < prev){
					prev = distance;
					coordinates = i + " " +  j;
				}
			}
	return coordinates;
}

}

