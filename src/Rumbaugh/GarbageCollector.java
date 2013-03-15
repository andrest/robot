package Rumbaugh;

import java.awt.Point;
import java.util.ArrayList;

import javaclient3.Position2DInterface;

public class GarbageCollector {
	private PathPlanner pathPlanner;
	private ArrayList<Point> garbages;
	private Point designated;
	

	public GarbageCollector(PathPlanner pathPlanner, Point designatedA, Point designatedB) {
		this.pathPlanner = pathPlanner;
		setDesignated(designatedA, designatedB);
	}
	
	public void startCollection() {
		garbages = RobotData.INSTANCE.getGarbage();
		for (Point garbage : garbages) {
			fetchGarbage(garbage);
			disposeGarbage(garbage);
		}
	}
	private void disposeGarbage(Point garbage) {
		pathPlanner.goToPoint(designated);
		// + open grippers
	}

	private void fetchGarbage(Point garbage) {
		pathPlanner.goToPenultimate(garbage);
		dockToGarbage(garbage);
	}

	private void dockToGarbage(Point garbage) {
		// TODO Approach and align using fiducial sensor
		// and grip it when it becomes available
	}
	
	private void setDesignated(Point a, Point b) {
		// designated = centre of a and b;
	}

}
