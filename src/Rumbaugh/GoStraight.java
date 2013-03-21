package Rumbaugh;


import javaclient3.PlayerClient;
import javaclient3.PlayerException;
import javaclient3.Position2DInterface;
import javaclient3.RangerInterface;
import javaclient3.structures.PlayerConstants;

public class GoStraight {
	
	PlayerClient robot=null;
	Position2DInterface pos2D=null;
	RangerInterface sonar=null;
    protected final static int FINALPREC = 2500;
    double x=0,y=0;
    double tYaw=0;
    double left,front,Sp,ySp;
	

	
    public void turnTo() {

        while(!pos2D.isDataReady());
        tYaw= getAngle(x,y,pos2D.getX(),pos2D.getY());
        while (!pos2D.isDataReady());
        double delta =  Math.abs(roundedYaw(pos2D.getYaw()-tYaw));
        while (!inRange(delta,0)) {
            while (!pos2D.isDataReady());
                delta = Math.abs(roundedYaw(pos2D.getYaw()-tYaw));
                pos2D.setSpeed(0,delta/FINALPREC);
                try {Thread.sleep(10);} catch (InterruptedException e) {}
        }
        pos2D.setSpeed(0,0);
            
    }
    public void goTo(){
    	boolean bol=false;
    	pos2D.setSpeed(0.3, 0);
    	while(!bol){
    		while(!sonar.isDataReady());
    		double[] sonarValues = sonar.getData().getRanges();
    		if(sonarValues[0]<0.5 ||sonarValues[1]<0.5){
    			pos2D.setSpeed(0, 0);
    			avoidObs();
    		}
    		
       	while(!pos2D.isDataReady());
       	if(inRange(pos2D.getX(), x)&&inRange(pos2D.getY(),y)){
       		pos2D.setSpeed(0, 0);
       		bol=true;
       		System.exit(1);
       	}
       	try {Thread.sleep(10);} catch (InterruptedException e) {}
    }
    }
    
    public static long roundedYaw(double yaw) {
       return(Math.round(yaw*FINALPREC));
    }
	public static double getAngle(double a,double b,double c, double d){
		if((a>=c&&b>=d)||(a>=c&&b<=d))
			return Math.asin((d-b)/
	        		Math.sqrt(Math.pow((a-c),2)+
	        				Math.pow((b-d), 2)));
			else if(a<c&&b<=d)
				
				return Math.toRadians(90.0)+Math.acos((b-d)/
		        		Math.sqrt(Math.pow((a-c),2)+
		        				Math.pow((b-d), 2)));
			else return -Math.toRadians(90.0)+Math.asin((a-c)/
	        		Math.sqrt(Math.pow((a-c),2)+
	        				Math.pow((b-d), 2)));
	}
	
	public static boolean inRange(double a, double b){
		if((a<b+0.05)&&(a>b-0.05))
			return true;
		else return false;
	}
	public void avoidObs(){
		int counter=0;
		boolean b= false;
		while (!b) {
			counter++;
            getRangers ();
			if(counter%150==0){
				turnTo();
				getRangers();
				if(front>1.5){
					goTo();
					b=true;
				}
					
			}
            Sp   = 0.2;
            ySp = 0;
          
            if (front < 0.4) {
                Sp   = -0.10f;
                ySp = - 0.6;
            } else
                if (left < 0.3) {
                    Sp   = 0.1;
                    ySp = - 0.15 ;
                }
                else
                    if (left > 0.5 && left<3) {
                        Sp   = 0.1;
                        ySp = 0.15;
                    }
            pos2D.setSpeed (Sp, ySp);           
            try { Thread.sleep (100); } catch (Exception e) { }
        }		
	}

    public void getRangers () {
        while (!sonar.isDataReady ());
        double[] sonarValues = sonar.getData ().getRanges ();
        
        for (int i = 0; i < sonar.getData ().getRanges_count (); i++)
            if (sonarValues[i] < 0.2)
                sonarValues[i] = 0.2;
            else
                if (sonarValues[i] > 10)
                    sonarValues[i] = 10;
    
        left = Math.min (sonarValues[2], sonarValues [3]);
        front = Math.min (sonarValues [0], sonarValues [1]);
    }

}