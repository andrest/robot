package com.Rumbaugh;
/*
 * This class provides a pattern correction method, used where the robot measurements
 * are not that accurate, especially corners.
 */
public class PatternCheck {
	
	
static void patternCorrect(int[][] a){
    	
    	for(int i=7;i<160;i++)
    		for(int j=7;j<160;j++)
    		{
    			if(a[i][j]==1 && a[i][j+2]==1)
    				a[i][j+1]=1;
    			if(a[i][j]==1 && a[i+2][j]==1)
    				a[i+1][j]=1;
    			
    			if(a[i][j]==1 && a[i+2][j-1]==1 && a[i][j+1]==1&& 
    					a[i+1][j]!=1&& a[i+2][j]!=1){
    				a[i][j-1]=1;
    				a[i+1][j-1]=1;
    			}
    			if(a[i][j]==1 && a[i+2][j-1]==1 && a[i-1][j]==1 && 
    			a[i][j-1]!=1 && a[i+1][j-1]!=1){
    				a[i+1][j]=1;
    				a[i+2][j]=1;
    			}
    			
    			
    			if(a[i][j]==1 && a[i+2][j+1]==1 && a[i][j-1]==1 &&
    					a[i+1][j]!=1 && a[i+2][j]!=1){
    				a[i][j+1]=1;
    				a[i+1][j+1]=1;
    			}
    			if(a[i][j]==1 && a[i+2][j+1]==1 && a[i+2][j+2]==1&&
    					a[i][j+1]!=1 && a[i+1][j+1]!=1){
    				a[i+1][j]=1;
    				a[i+2][j]=1;
    			}
    			
    			
    			if(a[i][j]== 1 && a[i-1][j+2]==1 && a[i+1][j]==1&&
    				a[i][j+1]!=1 && a[i][j+2]!=1){
    				a[i-1][j]=1;
    				a[i-1][j+1]=1;
    			}
    		
    			if(a[i][j]== 1 && a[i-1][j+2]==1 && a[i-2][j+2]==1 &&
    					a[i-1][j]!=1 && a[i-1][j+1]!=1){
    				a[i][j+1]=1;
    				a[i][j+2]=1;
    			}
    			if(a[i][j]==1 && a[i+1][j+2]==1 && a[i][j-1]==1&&
    					a[i+1][j]!=1 && a[i+1][j+1]!=1){
    				a[i][j+1]=1;
    				a[i][j+2]=1;
    			}
    			
    			
    			if(a[i][j]==1 && a[i+2][j+2]==1){
    				if((a[i-1][j]==1&& a[i-2][j]==1 && a[i-3][j]==1)||
    					(a[i+2][j+3]==1 && a[i+2][j+4]==1 && a[i+2][j+5] == 1 )){
    					a[i+1][j]=1;
    					a[i+2][j]=1;
    					a[i+2][j+1]=1;
    				}
    			}
    			if(a[i][j]==1 && a[i+2][j-2]==1)
    				if((a[i-1][j]== 1 && a[i-2][j]==1 && a[i-3][j]==1)||
    						(a[i+2][j-3]==1 && a[i+2][j-4]==1 && a[i+2][j-5]==1)){
    					a[i+1][j]=1;
    					a[i+2][j]=1;
    					a[i+2][j-1]=1;
    				}
    			
    		}
	}
}

