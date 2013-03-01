package Rumbaugh;

public enum RobotData {
	INSTANCE;

	public static final int ARRAY_LENGTH = 133;
	public static final int ARRAY_HEIGHT = 90;
	public static final int RESOLUTION = 3;
	
	private static int[][] mapArray = new int[ARRAY_HEIGHT][ARRAY_LENGTH];
	
	public static void main(String[] args) {
		initMap();

	}
	
	// Initalise the map with zeroes
	public static void initMap() {
        for(int i=0;i<ARRAY_HEIGHT;i++)
        	for(int j=0;j<ARRAY_LENGTH;j++){
        		mapArray[i][j]=0;
        	}
	}
    
    public int[][] getMap() { return mapArray; }
    public void setMap(int[][] map) { mapArray = map; }
    
}
