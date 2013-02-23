package Rumbaugh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileArrayProvider {
    static String[][] strng = new String[15][15];
    static String[][] str;

    public static String[][] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line = null;
        for(int i=0;i<14;i++){
        		line= bufferedReader.readLine();
        		for (int j=0;j<14;j++)
        			strng[i][j]=line.split(" ")[j];
        }
        bufferedReader.close();
        return strng;
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
  
    	pth.Asearch("4 7 0", "9 7");
    	ArrayList<String> al = pth.reconstructPath();
    	for(int i=0;i<al.size();i++)
    		System.out.println(al.get(i));
    	
    	
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