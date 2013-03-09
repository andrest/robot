package Rumbaugh;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileArrayProvider {
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
/**    	floodFill(5,5);
    	for(int i=0;i<15;i++){
    		for (int j=0;j<15;j++)
    		System.out.print(str[i][j]+" ");
    		System.out.println();
    	}
*/

    	PathPlanner planner = new PathPlanner(mapArray);
  
    	ArrayList<Point> path = planner.getPath(new Point(4,4), new Point(18,30));

    	for(int i=0;i<path.size();i++)
    		System.out.println(path.get(i).x + " " + path.get(i).y);
    	
    	
    }
    
    public static void floodFill(int x, int y) {

        if (!mapArray[x][y].equalsIgnoreCase("0")) return;

        mapArray[x][y]= "8";
        if(x>0)
        floodFill(x - 1, y);
        if(x<14)
        floodFill(x + 1, y);
        if(y>0)
        floodFill(x, y - 1);
        if(y<14)
        floodFill(x, y + 1);

        return;

    }
}