package Rumbaugh;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileArrayProvider {
    public static final int ARRAY_LENGTH = 36;
    public static final int ARRAY_HEIGHT = 45;
    static ArrayList<String[]> strng = new ArrayList<String[]>();
    static String[][] mapArray;

    public static String[][] mapFromFile(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = "";
        while((line = bufferedReader.readLine()) != null) {
        		String[] lineArray = line.split(" ");
        		strng.add(lineArray);
        }
        bufferedReader.close();
        return strng.toArray(new String[0][0]);
    }
    public static void main(String[] args) throws IOException{
    	mapArray = mapFromFile("src/Rumbaugh/testfile.txt");
    	int h = mapArray.length;
    	int l = mapArray[0].length;
    	int[][] arr = new int[h][l];
    	for(int i=0;i<h;i++)
    		for(int j=0;j<l;j++)
    			arr[i][j] = Integer.parseInt(mapArray[i][j]);
/**    	floodFill(5,5);
    	for(int i=0;i<15;i++){
    		for (int j=0;j<15;j++)
    		System.out.print(str[i][j]+" ");
    		System.out.println();
    	}

*/
    	//PathPlanner planner = new PathPlanner(mapArray);
  
    	ArrayList<Point> path = planner.getPath(new Point(4,4), new Point(18,30));

    	for(int i=0;i<path.size();i++)
    		System.out.println(path.get(i).x + " " + path.get(i).y);
    	System.out.println("+++++++++++++++++++++++++++++++++++++");
    	ArrayList<Point> straight = planner.straightLines(path);
    	for(int i=0;i<straight.size();i++)
    		System.out.println(straight.get(i).x + " " + straight.get(i).y);
    	
      	
    	
    }
    
}