package BatController;
/**
 * Setters and getters for all data sent form robot
 * 
 * @author Sean Murphy
 * Student number: 11829078
 *
 */
public class sensorDataGetSet {

	private static String nextMessageToRobot;

	// String to hold compass angle
	private static String compassAngle;

	// String to hold signal strength
	private static String signalStrength;

	// Strings to hold IR readings
	private static String centreIRSensor;
	private static String rightIRSensor;
	private static String leftIRSensor;

	// Strings to hold sonar readings
	private static String lFSonarInches;
	private static String centerSonarInches;
	private static String rightFSonarInches;
	private static String backSonarInches;
	
	private static int wheelRotation;
	
	private static int wheelForward;
	private static int wheelReverse;
	private static int wheelRotate;
	
	private static String leftSideSonarInches;
	private static String reftSideSonarInches;
	//private static int noMotion;
	/**
	 * 
	 * @param csi
	 */
	public static void setcenterSonarInches( String csi ) {

		centerSonarInches = csi;
	}
	
	/**
	 * 
	 * @param fli
	 */
	public static void setFLSonarInches(String fli) {

		lFSonarInches = fli;
	}

	/**
	 * 
	 * @param cir
	 */
	public static void setcentreIRSensor( String cir ) {

		centreIRSensor = cir;
	}

	/**
	 * 
	 * @param bsi
	 */
	public static void setbackSonarInches( String bsi ) {

		backSonarInches = bsi;
	}

	/**
	 * 
	 * @param cA
	 */
	public static void setcompassAngle( String cA ) {

		compassAngle = cA;
	}

	/**
	 * 
	 * @param rFS
	 */
	public static void setFRSonarInches( String rFS ) {

		rightFSonarInches = rFS;
	}

	/**
	 * 
	 * @param wR
	 */
	public static void setWheelRotation( String wR ) {

		wheelRotation = Integer.parseInt(wR);
	}
	
	/**
	 * 
	 * @param wF
	 */
	public static void setWheelForward( String wF ) {

		wheelForward = Integer.parseInt(wF);
	}
	
	/**
	 * 
	 * @param wRev
	 */
	public static void setWheelReverse( String wRev ) {

		wheelReverse = Integer.parseInt(wRev);
	}
	
	/**
	 * 
	 * @param wRota
	 */
	public static void setRotating( String wRota ) {

		wheelRotate = Integer.parseInt(wRota);
	}
	
	public static void setLeftSideSonar( String lSS ) {

		leftSideSonarInches = lSS;
	}
	
	public static void setReftSideSonar(String rSS) {
		
		reftSideSonarInches = rSS;
	}

	/**
	 * 
	 * @return compassAngle
	 */
	public static String getcompassAngle() {

		return compassAngle;
	}

	/**
	 * 
	 * @return centerSonarInches
	 */
	public static String getCentSonarInches() {

		return centerSonarInches;
	}

	/**
	 * 
	 * @return centreIRSensor
	 */
	public static String getcentreIRSensor() {

		return centreIRSensor;
	}

	/**
	 * 
	 * @return backSonarInches
	 */
	public static String getbackSonarInches() {

		return backSonarInches;
	}

	/**
	 * 
	 * @return lFSonarInches
	 */
	public static String getFLSonarInches() {

		return lFSonarInches;
	}
	
	/**
	 * 
	 * @return rightFSonarInches
	 */
	public static String getFRSonarInches() {

		return rightFSonarInches;
	}
	
	/**
	 * 
	 * @return wheelRotation
	 */
	public static int getWheelRotation() {

		return wheelRotation;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getWheelForward() {

		return wheelForward;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getWheelReverse() {

		return wheelReverse;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getWheelRotating() {

		return wheelRotate;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getLeftSideSonar() {

		return leftSideSonarInches;
	}

	/**
	 * 
	 * @return
	 */
	public static String getReftSideSonar() {

		return reftSideSonarInches;
	}
}
