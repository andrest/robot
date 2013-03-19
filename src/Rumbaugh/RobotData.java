package Rumbaugh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javaclient3.Position2DInterface;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public enum RobotData {
        INSTANCE;

        public static final int ARRAY_LENGTH = 180;
        public static final int ARRAY_HEIGHT = 120;
        public static final int RESOLUTION = 4;
        public static final int SCALE = 800;
        static final int HEIGHT_OFFSET=15;
        static final int LENGTH_OFFSET=22;
        
        private static int[][] mapArray;
        private static ArrayList<Point> garbage;
        
        private static Timer timer;
        private static Image mapImage;
        private JPanel panel;
		private static Position2DInterface pos2d;
		private static BufferedImage bufferedImage;
		private static String state;
        
        RobotData() { }
        
        /**
         * Initialise the map with zeroes
         */
        public void initMap() {
                mapArray = new int[ARRAY_HEIGHT][ARRAY_LENGTH]; 
                garbage = new ArrayList<Point>();
                timer = new Timer();            
                resetArray(mapArray);
                startUpdatingImage();
        }
        
        private void startUpdatingImage() {
        // Update the mapImage every 750ms

        timer.schedule( new TimerTask() {
            public void run() {
                if (panel != null) {
                        mapImage = toImage(mapArray).getScaledInstance(SCALE, -1, Image.SCALE_SMOOTH);
                        panel.repaint();
                        panel.revalidate();
                }
                }
        }, 0, 750);
        }
        
        /**
         * @return Point representing the robot's co-ordinate location
         * on the real map
         */
        public Point getLocation() {
        	while (!pos2d.isDataReady()) {};
            return new Point((int)convertY(pos2d.getY()),
            				 (int)convertX(pos2d.getX()));
        }

        static public double convertY(double a) {
        	return Math.round((ARRAY_HEIGHT - RESOLUTION*(HEIGHT_OFFSET + a))*100)/100;
        }
        // Return e.g. 4.56
        static public double convertX(double a) {
        	return Math.round(RESOLUTION*(LENGTH_OFFSET+a)*100)/100;
        }
        
        private int[][] trimMap(int[][] map) {
        	int startRow = 0, startColumn = 0;
        	int endRow = 0, endColumn = 0;

		    // Bottom horizontal
        	outer:
		    for(int i=map.length-1; i >= 0; i--) {
	            for(int j=map[0].length-1; j >= 0; j--) {
	            	if (map[i][j] != 1){
	            		endRow = i+1;
	            		break outer;
	            	}
	            }
		    }
		    // Right Side vertical
        	outer:
        	for(int j=map[0].length-1; j >= 0; j--) {
        		for(int i=map.length-1; i >= 0; i--) {
	            	if (map[i][j] == 1){
	            		endColumn = j+1;
	            		break outer;
	            	}
	            }
		    }
		    
		    // Top horizontal
		    outer:
		    for(int i=0; i < map.length; i++) {
	            for(int j=0; j < map[0].length; j++) {
	            	if (map[i][j] == 1){
	            		startRow = i;
	            		break outer;
	            	}
	            }
		    }
		    // Left side horizontal
		    outer:
		    for(int j=0; j < map[0].length; j++) {
		    	for(int i=0; i < map.length; i++) {
	            	if (map[i][j] == 1){
	            		startColumn = j;
	            		break outer;
	            	}
		        }
		    }

		    // Create the new trimmed array
		    int[][] trimmedMap = new int[endRow-startRow][endColumn-startColumn];
		    for(int i=0; i < endRow-startRow; i++) {
		    	for(int j= 0; j < endColumn-startColumn; j++){    		
		    		trimmedMap[i][j] = map[startRow+i][startColumn+j];
		    	}
		    }
		    return trimmedMap;
        }

        public void stopUpdatingImage() {
                timer.cancel();
                timer.purge();
        }
        
        private void resetArray(int[][] array) {
        for(int i=0;i<ARRAY_HEIGHT;i++)
                for(int j=0;j<ARRAY_LENGTH;j++){
                        array[i][j]=0;
                }
       
        }
    

    /**
     * Export an image to a JPG file
     * 
     * @param fileName The filename to export to
     * @param image The image to write to file
     * @throws IOException If problems occur during writing of file
     */
    public static void exportImageToFile(String fileName, RenderedImage image)throws IOException{
        File saveFile = new File(fileName +".jpg");
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(saveFile);
        int rval = chooser.showSaveDialog(null);
        if (rval == JFileChooser.APPROVE_OPTION) {
            saveFile = chooser.getSelectedFile();
            try {
            	ImageIO.write(image, "jpg", saveFile);
            } catch (IOException ex) {
            }
        }
    }
    
    /**
     * Convert a two dimensional array of ints to a BufferedImage.
     * Each int represents a pixel of a certain colour.
     * 
     * @param map The two dimensional int array representing the pixels
     * @return A BufferedImage with all the pixels drawn
     */
    private static BufferedImage toImage(int[][] map){
        int height = map.length;
        int width = map[0].length;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int y=0; y< height; y++){
            for(int x=0; x< width; x++){
                
                int color;
                switch (map[y][x]) {
                        case 0:
                                color = Color.WHITE.getRGB();
                                break;
                        case 1:
                                color = Color.BLACK.getRGB();
                                break;
                        case 2:
                                color = Color.BLUE.getRGB();
                                break;
                        case 3:
                                color = Color.GRAY.getRGB();
                                break;
                        case 4:
                        		color = Color.YELLOW.getRGB();
                        		break;
                        default:
                                color = Color.DARK_GRAY.getRGB();
                                break;
                }
                bufferedImage.setRGB(x,y,color);
            }
        }
        return bufferedImage;  
    }

    /**
     * Returns a scaled version of the map
     * 
     * @return BufferedImage scaled as it appears in the GUI
     */
    public BufferedImage getScaledBufferedImage(){
    	Image bigMap = toImage(trimMap(mapArray)).getScaledInstance(SCALE, -1, Image.SCALE_SMOOTH);
    	BufferedImage bufferedBigMap = new BufferedImage(bigMap.getWidth(null),
    	                                                 bigMap.getHeight(null),
    	                                                 BufferedImage.TYPE_INT_RGB);
    	bufferedBigMap.getGraphics().drawImage(bigMap, 0, 0, null);
    	return bufferedBigMap;
    }
    public Image getMapImage() { return mapImage; };
    public int[][] getMap() { return mapArray; }
    public void setMap(int[][] map) { mapArray = map; }
    public void setState(String state) { this.state = state; }
    public String getState() { return state; }
    /**
     * Returns the JPanel with the map image
	 *
     * @return JPanel where the map image will be drawn
     */
    public JPanel getImagePanel() {
    	JPanel panel = new JPanel() {
    		private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
            	super.paintComponent(g);
                if (mapImage == null) return;
                g.drawImage(mapImage, 0, 0, mapImage.getWidth(null), mapImage.getHeight(null), null);
            }
            @Override  
            public Dimension getPreferredSize(){
            	if (mapImage == null) return new Dimension(SCALE ,SCALE*ARRAY_HEIGHT/ARRAY_LENGTH);
                return new Dimension(mapImage.getWidth(null), mapImage.getHeight(null));  
            }  
        };
        this.panel = panel;
        return panel;
    }

	public void setPos2d(Position2DInterface pos2d) {
		this.pos2d = pos2d;	
	}

	public ArrayList<Point> getGarbage() {
		return garbage;
	}

	public void setGarbage(ArrayList<Point> garb) {
		garbage = garb;
		
	}
}