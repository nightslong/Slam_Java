package BatController;

import java.awt.Shape;

/**
 * Core SLAM class.
 * Populating the 2D arrays is here.
 * The arrays are the map.
 * 
 * @author Sean Murphy 
 * @author John Miles-Groves
 * 
 * 
 */
public class LocalisationFilter {

	private double cFS;  // front sonar is used for drawing data to canvas
	private double cRS;  // rear sonar is used for drawing data to canvas
	private double cFIR; // front inferred is used for drawing data to canvas
	private double fRS;  // front right sonar
	private double fLS;  // front left sonar
	private double lSS;  
	private double rSS;

	public  int cA = 0;
	private int[][] mapXY;// main map array
	private int[][] obsXY;// observed array
	
	private int mapSize;

	private int robotFrontSonar;
	private int robotRcenter;

	private int sqSize;// set this to map square size ( mapGui class x or y / window size (800) )
	private double scaling;
	
	private BatSerialConn serialClass;

	private double sensorTemp;
	private int IndexX;
	private int IndexY;

	private double offsetLeft = -45;// offsets for attaching front right sonar to robot shape
	private double offsetRight = 45;// offsets for attaching front left sonar to robot shape

	private double offsetLeftSonar = 270;// left side sonar offset as above
	private double offsetRightSonar = 90;// right side sonar offset as above

	private double theta; // compass reading
	private double hyp; // The hypotonuse - the line from BOT to scanned object
	private double adj; // The adjacent - Will hold the distance along X axis that we need to increment
	private double opp; // The adjacent - line going along Y axis, holding the distance we need to increment

	/**
	 * 
	 * @param instance
	 * @param sSize
	 * @param arrSize
	 * @param scale
	 */
	public LocalisationFilter(BatSerialConn instance,int sSize,int arrSize,double scale) {

		mapSize = arrSize;
		sqSize = sSize;
		scaling = scale;
		serialClass = instance;
		mapXY = new int[mapSize][mapSize];
		obsXY = new int[mapSize][mapSize];
	}

	/**
	 * 
	 * @param robot
	 */
	public void update(Shape robot) {

	 // Shape temp = robot;
		cFS  =  ( Double.parseDouble( sensorDataGetSet.getCentSonarInches() ) *scaling );
		fLS  =  ( Double.parseDouble( sensorDataGetSet.getFLSonarInches()   ) *scaling );
		fRS  =  ( Double.parseDouble( sensorDataGetSet.getFRSonarInches()   ) *scaling );
		lSS  =  ( Double.parseDouble( sensorDataGetSet.getLeftSideSonar()   ) *scaling );
		rSS  =  ( Double.parseDouble( sensorDataGetSet.getReftSideSonar()   ) *scaling );
		cRS  =  ( Double.parseDouble( sensorDataGetSet.getbackSonarInches() ) *scaling );
	    // cFIR =  Double.parseDouble( sensorDataGetSet.getcentreIRSensor() );// unused at the moment
	    cA   =  Integer.parseInt( sensorDataGetSet.getcompassAngle() );
	    // cA = rotate( cA ); // this is used to simulate rotation

	    // this loop adds to padded data to either side of the sensor reading to produce conical shape
		for (int i = -6; i < 6 ; i++) {
			
			calcIndexMapF( robot,cFS,i);
			calcIndexMapLss( robot,lSS,i);
			calcIndexMapRss( robot,rSS,i);
			calcIndexMapRear( robot,cRS,i);
		//	calcIndexMapFL(robot,fLS,i);
		//	calcIndexMapFR(robot,fRS,i);
		}
	
		calcIndexMapFL(robot,fLS,0);
		calcIndexMapFR(robot,fRS,0);
	}

	/**
	 * 
	 * @param temp
	 * @param sensor
	 * @param a
	 */
	public void calcIndexMapF( Shape temp,double sensor,int a) {

		sensorTemp = sensor;

		theta = (cA+a)*( Math.PI/180 );//The rotation (in degs, for now) - using 45 as an example
		hyp = sensorTemp + (temp.getBounds2D().getHeight()/2 /sqSize );
		opp = hyp * ( Math.cos( theta ) );
		adj = hyp * ( Math.sin( theta ) );

		IndexX = (int) ((( temp.getBounds2D().getCenterX() ) /sqSize ) + adj );
		IndexY = (int) ((( temp.getBounds2D().getCenterY() ) /sqSize ) - opp );

		if (IndexX < mapSize && IndexY < mapSize && IndexX > 0 && IndexY > 0){

			obsXY [IndexX] [IndexY] = obsXY [IndexX] [IndexY] = +1;

			if( hyp == cFS + (temp.getBounds2D().getHeight()/2 /sqSize ) && cFS <= (18*scaling) ){
				
				mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = +1;
			}
			else mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = -1;
		}

		for ( double i = 0; i < sensorTemp;  ) {

			sensorTemp = sensorTemp - 1;
			calcIndexMapF(temp,sensorTemp,a);
		}
	}

	/**
	 * 
	 * @param temp
	 * @param sensor
	 * @param a
	 */
	public void calcIndexMapFL( Shape temp,double sensor,int a ) {

		sensorTemp = sensor;

		theta = (cA + offsetLeft + a)*( Math.PI/180 );//The rotation (in degs, for now) - using 45 as an example
		hyp = sensorTemp + (temp.getBounds2D().getHeight()/2 /sqSize );

		opp = hyp * ( Math.cos( theta ) );
		adj = hyp * ( Math.sin( theta ) );

		IndexX = (int) ((( temp.getBounds2D().getCenterX() ) /sqSize ) + adj );
		IndexY = (int) ((( temp.getBounds2D().getCenterY() ) /sqSize ) - opp );

		if (IndexX < mapSize && IndexY < mapSize && IndexX > 0 && IndexY > 0){

			obsXY [IndexX] [IndexY] = obsXY [IndexX] [IndexY] = +1;

			if( hyp == fLS + ((temp.getBounds2D().getHeight()/2) /sqSize ) && fLS <= (25*scaling) ){
				mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = +1;
			}
			else mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = -1;
		}

		for ( double i = 6; i < sensorTemp;  ) {

			sensorTemp = sensorTemp - 1;
			calcIndexMapFL(temp,sensorTemp,a);
		}
	}

	/**
	 * 
	 * @param temp
	 * @param sensor
	 * @param a
	 */
	public void calcIndexMapFR( Shape temp,double sensor,int a ) {

		sensorTemp = sensor;

		theta = (cA + offsetRight + a)*( Math.PI/180 );//The rotation (in degs, for now) - using 45 as an example
		hyp = sensorTemp + (temp.getBounds2D().getHeight()/2 /sqSize );

		opp = hyp * ( Math.cos( theta ) );
		adj = hyp * ( Math.sin( theta ) );

		IndexX = (int) ((( temp.getBounds2D().getCenterX() ) /sqSize ) + adj );
		IndexY = (int) ((( temp.getBounds2D().getCenterY() ) /sqSize ) - opp );

		if (IndexX < mapSize && IndexY < mapSize && IndexX > 0 && IndexY > 0){

			obsXY [IndexX] [IndexY] = obsXY [IndexX] [IndexY] = +1;

			if( hyp == fRS + (temp.getBounds2D().getHeight()/2 /sqSize ) && fRS <= (25*scaling) ){
				mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = +1;
			}
			else mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = -1;
		}

		for ( double i = 6; i < sensorTemp;  ) {

			sensorTemp = sensorTemp - 1;
			calcIndexMapFR(temp,sensorTemp,a);
		}
	}

	/**
	 * 
	 * @param temp
	 * @param sensor
	 * @param a
	 */
	public void calcIndexMapRear( Shape temp,double sensor,int a) {

		sensorTemp = sensor;

		theta = (cA + a)*( Math.PI/180 );//The rotation (in degs, for now) - using 45 as an example
		hyp = sensorTemp + (temp.getBounds2D().getHeight()/2 /sqSize );
		opp = hyp * ( Math.cos( theta ) );
		adj = hyp * ( Math.sin( theta ) );

		IndexX = (int) ((( temp.getBounds2D().getCenterX() ) /sqSize ) - adj );
		IndexY = (int) ((( temp.getBounds2D().getCenterY() ) /sqSize ) + opp );

		if (IndexX < mapSize && IndexY < mapSize && IndexX > 0 && IndexY > 0){

			obsXY [IndexX] [IndexY] = obsXY [IndexX] [IndexY] = +1;

			if( hyp == cRS + (temp.getBounds2D().getHeight()/2 /sqSize ) && cRS <= (18*scaling) ){
				mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = +1;
			}
			else mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = -1;
		}

		for ( double i = 0; i < sensorTemp;  ) {

			sensorTemp = sensorTemp - 1;
			calcIndexMapRear(temp,sensorTemp,a);
		}
	}

	/**
	 * 
	 * @param temp
	 * @param sensor
	 * @param a
	 */
	public void calcIndexMapLss( Shape temp,double sensor,int a ) {

		sensorTemp = sensor;

		theta = (cA + offsetLeftSonar + a)*( Math.PI/180 );//The rotation (in degs, for now) - using 45 as an example
		hyp = sensorTemp + (temp.getBounds2D().getHeight()/2 /sqSize );

		opp = hyp * ( Math.cos( theta ) );
		adj = hyp * ( Math.sin( theta ) );

		IndexX = (int) ((( temp.getBounds2D().getCenterX() ) /sqSize ) + adj );
		IndexY = (int) ((( temp.getBounds2D().getCenterY() ) /sqSize ) - opp );

		if (IndexX < mapSize && IndexY < mapSize && IndexX > 0 && IndexY > 0){

			obsXY [IndexX] [IndexY] = obsXY [IndexX] [IndexY] = +1;

			if( hyp == lSS + (temp.getBounds2D().getHeight()/2 /sqSize ) && lSS <= (18*scaling) ){
				mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = +1;
			}
			else mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = -1;
		}

		for ( double i = 0; i < sensorTemp;  ) {

			sensorTemp = sensorTemp - 1;
			calcIndexMapLss(temp,sensorTemp,a);
		}
	}

	/**
	 * 
	 * @param temp
	 * @param sensor
	 * @param a
	 */
	public void calcIndexMapRss( Shape temp,double sensor,int a ) {

		sensorTemp = sensor;

		theta = (cA + offsetRightSonar + a)*( Math.PI/180 );//The rotation (in degs, for now) - using 45 as an example
		hyp = sensorTemp + ( ( temp.getBounds2D().getHeight()/2 ) /sqSize );

		opp = hyp * ( Math.cos( theta ) );
		adj = hyp * ( Math.sin( theta ) );

		IndexX = (int) ((( temp.getBounds2D().getCenterX() ) /sqSize ) + adj );
		IndexY = (int) ((( temp.getBounds2D().getCenterY() ) /sqSize ) - opp );

		if (IndexX < mapSize && IndexY < mapSize && IndexX > 0 && IndexY > 0){

			obsXY [IndexX] [IndexY] = obsXY [IndexX] [IndexY] = +1;

			if( hyp == rSS + ( temp.getBounds2D().getHeight()/2 /sqSize ) && rSS <= (18*scaling) ){
				mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = +1;
			}
			else mapXY [IndexX] [IndexY] = mapXY [IndexX] [IndexY] = -1;
		}

		for ( double i = 0; i < sensorTemp;  ) {

			sensorTemp = sensorTemp - 1;
			calcIndexMapRss(temp,sensorTemp, a);
		}
	}

	/**
	 * Used for testing without robot
	 * Arduino required though with test program
	 * @param A
	 * @return compass
	 * 
	 */
	public int rotate(int A) {

		//Thread.sleep(100);
		if ( cA == 359 ) {
			cA = 1;
		}
		cA = cA + 1;
		return cA;
	}

	/**
	 * returns map array
	 * @return mapXY
	 */
	public int[][] getMapXY() {

		return mapXY;
	}

	/**
	 * returns observed array
	 * @return obsXY
	 */
	public int[][] getObsXY() {

		return obsXY;
	}

	/**
	 * The following methods are applied to
	 * allow future development to be able to send
	 * commands to the robot from this class
	 * after using the map as a reference for its 
	 * own location.
	 *
	 * 
	 * Send move forward command to robot
	 */
	public void moveForward() {

		serialClass.send("^");
	}

	/**
	 * Send move reverse to robot
	 * currently unused
	 */
	public void moveBackwards() {

		serialClass.send("V");
	}

	/**
	 * Send command rotate left
	 * to robot currently unused.
	 */
	public void rotateLeft() {

		serialClass.send("<");
	}

	/**
	 * Send move rotate right
	 * to robot currently unused.
	 */
	public void rotateRight() {

		serialClass.send(">");
	}

	/**
	 * Send the stop command to the
	 * robot currently unused.
	 */
	public void stop() {

		serialClass.send("S");
	}
}