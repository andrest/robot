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
        

        public GarbageCollector(GripperInterface gripper_0, Position2DInterface pos2d, RangerInterface ranger, Point designatedA, Point designatedB) {
                this.pathPlanner = pathPlanner;
                this.pos2d = pos2d;
                this.ranger = ranger;
                setDesignated(designatedA, designatedB);
        }

        public void startCollection() {
                garbages = RobotData.INSTANCE.getGarbage();
                for (Point garbage : garbages) {
                        pathPlanner = new PathPlanner(pos2d, ranger);
                        try {PathPlanner.testMap();} catch (IOException e) {};
                        fetchGarbage(garbage);
                        
                        System.out.println("Garbage fetched");
                        //System.out.println(RobotData.INSTANCE.getLocation());
                        //disposeGarbage(garbage);
                }
        }
        private void disposeGarbage(Point garbage) {
                pathPlanner.goToPoint(designated);
                // + open grippers
        }

        private void fetchGarbage(Point garbage) {
                pathPlanner.goToPenultimate(garbage);
                //dockToGarbage(garbage);
        }

        private void dockToGarbage(Point garbage) {
                // TODO Approach and align using fiducial sensor
                // and grip it when it becomes available
        }
        
        private void setDesignated(Point a, Point b) {
                designated = new Point((int)(a.x + b.x)/2, (int)(a.y + b.y)/2);
        }

}