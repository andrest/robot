package Rumbaugh;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import javaclient3.GripperInterface;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;

public class GarbageCollector {
    private PathPlanner pathPlanner;
    private ArrayList<Point> garbages;
    private Point designated;
    private Position2DInterface pos2d;
    private RangerInterface ranger;
	private GripperInterface gripper;
    

        public GarbageCollector(GripperInterface gripper, Position2DInterface pos2d, RangerInterface ranger, Point designatedA, Point designatedB) { 
        	try {PathPlanner.testMap();} catch (IOException e) {};
            this.pos2d = pos2d;
            this.ranger = ranger;
            this.gripper = gripper;
            setDesignated(designatedA, designatedB);
            
            ArrayList<Point> garbages = new ArrayList<Point>();
            int[][] map = RobotData.INSTANCE.getMap();
            for(int i = 0; i < map.length; i++) {
            	for(int j = 0; j < map.length; j++) {
            		if(map[i][j] == 2) {
            			garbages.add(new Point(i, j));
            		}
            	}
            }
            RobotData.INSTANCE.setGarbage(garbages);
        }

        public void startCollection() {
                garbages = RobotData.INSTANCE.getGarbage();
                for (Point garbage : garbages) {
                        pathPlanner = new PathPlanner(pos2d, ranger);
                        try {PathPlanner.testMap();} catch (IOException e) {};
                        while(!gripper.isDataReady()) {};
                        System.out.println(gripper.getData().getBeams());
                        gripper.open();
                        fetchGarbage(garbage);
                        
                        System.out.println("Garbage fetched");
                        //System.out.println(RobotData.INSTANCE.getLocation());
                        disposeGarbage(garbage);
                }
        }
        private void disposeGarbage(Point garbage) {
        		pathPlanner = new PathPlanner(pos2d, ranger);
                pathPlanner.goToPoint(designated);
                gripper.setGripper(1);
        }

        private void fetchGarbage(Point garbage) {
                pathPlanner.goToPenultimate(garbage);
                while(!gripper.isDataReady()) {};
                if (gripper.getData().getBeams() != 0) {
                	gripper.close();
                	//gripper.close();
                	//gripper.store();
                	//gripper.open();
                }
               
        }

        private void dockToGarbage(Point garbage) {
                // TODO Approach and align using fiducial sensor
                // and grip it when it becomes available
        }
        
        private void setDesignated(Point a, Point b) {
                designated = new Point((int)(a.x + b.x)/2, (int)(a.y + b.y)/2);
        }

}