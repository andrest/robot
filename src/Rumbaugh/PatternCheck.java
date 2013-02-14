package Rumbaugh;

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
    	
    	for(int i=3;i<WallFollower.h;i++)
    		for(int j=3;j<WallFollower.l;j++)
    		{
    			if(a[i][j]==1 && a[i][j+2]==1)
    				a[i][j+1]=1;
    			if(a[i][j]==1 && a[i+2][j]==1)
    				a[i+1][j]=1;
    			
     			
    		}
	}
public static void floodFill(int x, int y, int[][] array) {


	Queue<String> queue = new LinkedList<String>();
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
				if(array[x][w-1] == 0)
					w--;
				else
					b=false;
			}
			b= true;
				while(e<array.length-1 && b)
					if(array[x][e+1] == 0)
						e++;
					else 
						b= false;
				
			for(int j= w;j<=e;j++){
				array[x][j]= 8;
				if(x>0)
					if(array[x-1][j] == 0)
						queue.add(array[x-1][j] + " " + (x-1) +" "+ j);
				if(x<array.length - 1)
					if(array[x+1][j] == 0)
						queue.add(array[x+1][j] + " " + (x+1) + " " + j);
			}
		}

	}
	return;

}

public static boolean outerWallDone(int[][] array){

	boolean b= false;
	floodFill(0 ,0 , array);
	
	for(int i=0;i<array.length;i++)
		for(int j=0;j<array.length;j++)
			if(array[i][j]==0)
				b = true;
	return b;
}
}

