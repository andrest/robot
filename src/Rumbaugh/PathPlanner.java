package Rumbaugh;

import java.util.ArrayList;

/**
 * Class that provides an A* searching algorithm, as well as
 * methods to return the walkable neighbours of a specific node
 * and calculate heuristics.
 * @author Ionut
 *
 */

public class PathPlanner {
	ArrayList<String> closedset = new ArrayList<String>();		//contains nodes that have been already visited
	ArrayList<String> parent = new ArrayList<String>();			// contains all nodes that have been visited, with their parents
	ArrayList<String> openset = new ArrayList<String>();		//contains potential nodes to explore in next step
	ArrayList<String> openset2 = new ArrayList<String>();		//same as above, except it doesn't hold the 3rd value (f*(n)), which
																//eases the 'contains' search

	ArrayList<String> neighbours = new ArrayList<String>();		//will hold walkable neighbours of each node, via getNeighbours method
	String[][] strArray ;										//the map
	String current;												//current node
	String target;												//target node

	
	public PathPlanner(String[][] mapStr){
		strArray = mapStr;
	}
	
	public void Asearch(String start, String goal){
		
		target = goal;
		openset.add(start);
		openset2.add(process(start));
		parent.add(process(start));
		while(!openset.isEmpty() && !closedset.contains(goal)){
			current = retMin(openset);
			openset.remove(current);
			openset2.remove(process(current));
			closedset.add(process(current));
			neighbours = getNeighbours(current, goal);
			for(int i = 0 ; i<neighbours.size();i++){
				String s = process(neighbours.get(i));
				if(!closedset.contains(s)){
					if(!openset2.contains(s)){
						openset.add(neighbours.get(i));
						openset2.add(s);
						parent.add(process(current)+ " " +  s);
					}
					else{
						int index= 0;
						for(int j = 0;j<openset.size();j++){
							if(openset.get(j).startsWith(s))
								index = j;
						}
					
						if(Integer.parseInt(openset.get(index).split(" ")[2]) > Integer.parseInt(neighbours.get(i).split(" ")[2])){
							openset.remove(index);
							openset.add(neighbours.get(i));
							parent.add(process(current)+ " " + s);
						}
					}
				}
					
			}
		}
		
	}

	
	/**
	 * This method reconstructs the path, starting at
	 * the target node, and going backwards, to corresponding
	 * parent.
	 * @return An array list with the path between the two nodes.
	 */
	
	public ArrayList<String> reconstructPath(){
		ArrayList<String> s = new ArrayList<String>();
		ArrayList<String> reversed = new ArrayList<String>();
		String s1= target;
		s.add(s1);
		for(int i= parent.size()-1;i>0;i--){
			if(parent.get(i).endsWith(s1)){
				s1= process(parent.get(i));
				s.add(s1);
			}
		}
		for(int i = s.size()-1;i>=0;i--){
			reversed.add(s.get(i));
		}
		
		return reversed;
	}
	
	
	/**
	 * This method computes the minimum f*(n) value
	 * (3rd int) in each element of the array list.
	 * @param array - The array list to be examined
	 * @return The element of the array having the 
	 * 			smallest f*(n) value.
	 */
	
	public String retMin(ArrayList<String> array){
		int min = getH(array.get(0), target);
		int j = 0;
		for(int i=0;i<array.size();i++)
			if(getH(array.get(i),target)< min){
				min = getH(array.get(i),target);
				j=i;
			}
		return array.get(j);
	}
	
	
	/**
	 * This method retrieves all walkable neighbours
	 * of a particular node, and computes the f*(n)
	 * value for each of them, via getH() method.
	 * @param str The node to get neighbours for
	 * @param tar  The target node, used to calculate f*(n) value
	 * @return	An array list with all walkable nodes
	 */
	
	public ArrayList<String> getNeighbours(String str, String tar){
		ArrayList<String> neighbors = new ArrayList<String>();
		int i = Integer.parseInt(str.split(" ")[0]);
		int j = Integer.parseInt(str.split(" ")[1]);
		int k = Integer.parseInt(str.split(" ")[2]);
		for(int alfa = i-1; alfa<= i+1; alfa++)
			for(int beta = j-1; beta<=j+1;beta++){
				if(!strArray[alfa][beta].equalsIgnoreCase("1") &&(alfa != i || beta != j)){
					if((alfa+beta)%2 != (i+j)%2){
						neighbors.add(alfa + " " + beta+ " " + (10+k + getH(alfa+" " + beta, tar)));
					}
					else{
						if((alfa == i-1) && (beta == j-1) && !strArray[i-1][j].equalsIgnoreCase("1") && !strArray[i][j-1].equalsIgnoreCase("1"))
							neighbors.add(alfa + " " + beta + " " + (14+k) + getH(alfa+" " + beta, tar));
						else if((alfa == i-1) && (beta == j+1) && !strArray[i-1][j].equalsIgnoreCase("1") && !strArray[i][j+1].equalsIgnoreCase("1"))
								neighbors.add(alfa+ " " + beta + " " + (14+k + getH(alfa+" " + beta, tar)));
							else if((alfa == i+1) && (beta == j-1) && !strArray[i][j-1].equalsIgnoreCase("1") && !strArray[i+1][j].equalsIgnoreCase("1"))
									neighbors.add(alfa + " " + beta + " " + (14+k + getH(alfa+" " + beta, tar)));
								else if((alfa == i+1) && (beta == j+1) && !strArray[i+1][j].equalsIgnoreCase("1") && !strArray[i][j+1].equalsIgnoreCase("1"))
										neighbors.add(alfa+ " " + beta + " " + (14+k + getH(alfa+" " + beta, tar)));
					}
				}
				
			}
		

		return neighbors;
	}
	

	/**
	 * Gets the heuristic for a particular node,
	 * calculating the direct distance (straight line)
	 * between the node and the target
	 * @param cur	Current node
	 * @param tar	Target node
	 * @return	Integer value, representing the heuristic for a node
	 */
	
	public int getH(String cur, String tar){
		int xCur = Integer.parseInt(cur.split(" ")[0]);
		int yCur = Integer.parseInt(cur.split(" ")[1]);
		int xTar = Integer.parseInt(tar.split(" ")[0]);
		int yTar = Integer.parseInt(tar.split(" ")[1]);
		
		return (int) (10 *Math.sqrt(((xCur - xTar)* (xCur - xTar)) + ((yCur - yTar)* (yCur - yTar))));
	}
	
	
	/**
	 * Processes a string, to retrieve only the
	 * first 2 integers (array indexes)
	 * @param string	String to be processed
	 * @return	A string containing the first 2 
	 * 			integers of the param.
	 */
	
	public String process(String string){
		return string.split(" ")[0] + " " + string.split(" ")[1];
	}
}
