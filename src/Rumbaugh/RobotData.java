package Rumbaugh;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javaclient3.Position2DInterface;

import javax.imageio.ImageIO;
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
        private static int[][] usedArray;
        
        private static Timer timer;
        private static Image mapImage;
        private JPanel panel;
		private Position2DInterface pos2d;
		private static BufferedImage bufferedImage;
        
        RobotData() { }
        
        // Initalise the map with zeroes
        public void initMap() {
                mapArray = new int[ARRAY_HEIGHT][ARRAY_LENGTH]; 
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
        
        public Point getLocation() {
        	while (!pos2d.isDataReady()) {};
            return new Point((int)Math.round(ARRAY_HEIGHT - RESOLUTION*(HEIGHT_OFFSET + pos2d.getY())),
            				 (int)Math.round(RESOLUTION*(LENGTH_OFFSET+pos2d.getX())));
        }
        
        private int[][] getUsedMap(int[][] array){
        	int x1 = 0; 
        	int	x2 = ARRAY_HEIGHT-1; 
        	int y1=0; 
        	int y2 = ARRAY_LENGTH-1;
        	boolean b = true;
        	while(b){
        			for(int j = 0;j<ARRAY_LENGTH/2.5;j++)
        				if(array[x1][j] != 0)
        					b = false;
        			if(b && x1 < ARRAY_HEIGHT-1)
        				x1 ++;
        			b=false;
        		
        	}
        	b=true;
        	while (b){
        		
        			for(int j = 0; j< ARRAY_LENGTH/2.5;j++)
        				if(array[x2][j]!= 0)
        					b= false;
        			if(b)
        				x2 -- ;
        			b=false;
        		
        	}
        	b=true;
        	while (b){
        			for(int i=0;i<ARRAY_HEIGHT/2.5;i++)
        				if(array[i][y1] != 0)
        					b= false;
        			if(b)
        				y1 ++ ;
        			b=false;
        		
        	}
        	b= true;
        	while(b){
        			for(int i=0;i<ARRAY_HEIGHT/2.5;i++)
        				if(array[i][y2] != 0)
        					b=false;
        			if(b)
        				y2--;
        			b= false;
        		
        	}
        	int[][] newArray = new int[x2 - x1 + 1][y2 -y1 + 1];
        	for(int i= 0; i<x2-x1+1; i++)
        		for(int j=0;j<y2-y1+1;j++)
        			newArray[i][j] = array[x1 + i][y1 + j];
        	return newArray;
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
        File file = new File(fileName);
        //to export to png, change 2 parameter to "png"
        ImageIO.write(image, "jpg", file);
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

        public Image getMapImage() { return mapImage; };
        public int[][] getMap() { return mapArray; }
        public BufferedImage getBufferedImage(){return bufferedImage;}
    public void setMap(int[][] map) { mapArray = map; }

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
}