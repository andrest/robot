package Rumbaugh;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileArrayProvider {
    static ArrayList<String[]> strng = new ArrayList<String[]>();
    static String[][] str;

    public static String[][] readLines(String filename) throws IOException {
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
    	str = readLines("src/Rumbaugh/testfile.txt");
/**    	floodFill(5,5);
    	for(int i=0;i<15;i++){
    		for (int j=0;j<15;j++)
    		System.out.print(str[i][j]+" ");
    		System.out.println();
    	}
*/

    	PathPlanner pth = new PathPlanner(str);
  
    	pth.Asearch("4 4 0", "18 30");
    	ArrayList<Point> al = pth.reconstructPath();
    	for(int i=0;i<al.size();i++)
    		System.out.println(al.get(i).x + " " + al.get(i).y);
    	
    	
    }
    
    public static void floodFill(int x, int y) {

        if (!str[x][y].equalsIgnoreCase("0")) return;

        str[x][y]= "8";
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