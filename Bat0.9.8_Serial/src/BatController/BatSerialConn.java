package BatController;

import gnu.io.*; // RXTX

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Serial connection class. Uses RXTXcomm.jar
 * 
 * @author Sean Murphy
 * @author John Miles-Grove
 * @author Oddi McNally
 * 
 */
public class BatSerialConn implements SerialPortEventListener {

	private String chosenPort;
	boolean foundPorts;
	private SerialPort serialPort;
	private BufferedReader input;
	/** The output stream to the port **/
	private OutputStream output;
	private PrintStream p;
	/** Milliseconds to block while waiting for port open **/
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. **/
	private int DATA_RATE = 9600;
	public ArrayList<String> availablePorts = new ArrayList<String>();

	CommPortIdentifier serialPortId;
	// static CommPortIdentifier sSerialPortId;

	Enumeration enumComm;

	// String for next message
	private String nextMessageToRobot;

	// String to hold compass angle
	private String compassAngle;

	// String to hold signal strength
	private String signalStrength;

	// Strings to hold IR readings
	private String centreIRSensor;
	private String rightIRSensor;
	private String leftIRSensor;

	// Strings to hold sonar readings
	private String leftFrontSonarInches;
	private String centerSonarInches;
	private String rightFrontSonarInches;
	private String backSonarInches;

	private String wheelRotation;
	private String wheelForward;
	private String wheelReverse;
	// private String wheelRotate;
	// Declared but not yet used.
	private String leftSideSonarInches;
	private String rightSideSonarInches;

	//Bearing additions	
	private String targetBearing;
	private String res;

	/**
	 * Creates the serial port connection.
	 */
	public void connectButton() {

		try {

			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) serialPortId.open(this.getClass()
					.getName(), TIME_OUT);
			System.out.print("my name is " + serialPort.getName());

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(
					serialPort.getInputStream()));
			output = serialPort.getOutputStream();
			p = new PrintStream(output);

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			// need to un swallow this
		}
	}

	/**
	 * Collects the ports from available ports.
	 * 
	 * @return
	 */
	public ArrayList<String> setCommPorts() {

		enumComm = CommPortIdentifier.getPortIdentifiers();
		while (enumComm.hasMoreElements()) {
			serialPortId = (CommPortIdentifier) enumComm.nextElement();
			if (serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				availablePorts.add(serialPortId.getName());
			}
		}
		return availablePorts;
	}

	/**
	 * Collects the string from the robot and
	 * sets the results.
	 * 
	 * @param message
	 */
	private void upDateUI( String message ) {

		// This splits the param around commas, storing each substring as
		// objects in an array

		String[] data = message.split(",");
		// Switch on first index of array
		switch ( data[0] ) {

		case "S":
			// Assign values from robot to strings for display
			sensorDataGetSet.setcenterSonarInches( centerSonarInches = data[1] );// front sonar
			sensorDataGetSet.setFRSonarInches( rightFrontSonarInches = data[2] );// right front sonar
			sensorDataGetSet.setFLSonarInches( leftFrontSonarInches = data[3] ); // left front sonar
			sensorDataGetSet.setbackSonarInches( backSonarInches = data[4] );    // back centre sonar
			sensorDataGetSet.setcompassAngle( compassAngle = data[5] );         // compass angle
			signalStrength = data[6];
			sensorDataGetSet.setcentreIRSensor( centreIRSensor = data[7] );
			rightIRSensor = data[8];
			leftIRSensor = data[9];
			sensorDataGetSet.setWheelRotation( wheelRotation = data[10] );      // wheel photo sensor
			sensorDataGetSet.setWheelForward( wheelForward = data[11] );        // motion flags from robot
			sensorDataGetSet.setWheelReverse( wheelReverse = data[12] );        // motion flags from robot
			sensorDataGetSet.setLeftSideSonar(leftSideSonarInches = data[13] ); // motion flags from robot
			sensorDataGetSet.setReftSideSonar(rightSideSonarInches = data[14] );// motion flags from robot
			

			centerSonarInches 	  = data[1]; // front sonar
			rightFrontSonarInches = data[2]; // right front sonar
			leftFrontSonarInches  = data[3]; // left front sonar
			backSonarInches 	  = data[4]; // back centre sonar
			compassAngle 	 	  = data[5]; // compass angle
			signalStrength 		  = data[6]; // signal strength
			centreIRSensor 		  = data[7]; // centre IR
			rightIRSensor 		  = data[8]; // right IR
			leftIRSensor		  = data[9]; // left IR
			wheelRotation		  = data[10]; // wheel photo sensor
			wheelForward		  = data[11]; // motion flags from robot
			wheelReverse 		  = data[12]; // motion flags from robot
			leftSideSonarInches   = data[13]; // left side sonar
			rightSideSonarInches  = data[14]; // right side sonar
		}

		GUI.txtCompassReading.setText(compassAngle);

		// Centre Sonar Panel
		GuiSensorReadings.txtCenterFrontSonarDistInch.setText(centerSonarInches);
		GuiSensorReadings.txtCenterFrontSonarDistCM.setText(stringInchToStringCm(centerSonarInches));

		// Left Front Sonar Panel
		GuiSensorReadings.txtLeftFrontSonarDistInch.setText(leftFrontSonarInches);
		GuiSensorReadings.txtLeftFrontSonarDistCM.setText(stringInchToStringCm(leftFrontSonarInches));

		// Right Front Sonar Panel
		GuiSensorReadings.txtRightFrontSonarDistInch.setText(rightFrontSonarInches);
		GuiSensorReadings.txtRightFrontSonarDistCM.setText(stringInchToStringCm(rightFrontSonarInches));

		// Left Side Sonar Panel
		GuiSensorReadings.txtLeftSideSonarDistInch.setText(leftSideSonarInches);
		GuiSensorReadings.txtLeftSideSonarDistCM.setText(stringInchToStringCm(leftSideSonarInches));

		// Right Side Sonar Panel
		GuiSensorReadings.txtRightSideSonarDistInch.setText(rightSideSonarInches);
		GuiSensorReadings.txtRightSideSonarDistCM.setText(stringInchToStringCm(rightSideSonarInches));

		// Rear Sonar Panel
		GuiSensorReadings.txtRearSonarDistInch.setText(backSonarInches);
		GuiSensorReadings.txtRearSonarDistCM.setText(stringInchToStringCm(backSonarInches));

		// IR Sensor Panel
		GuiSensorReadings.txtFrontLeftIR.setText(stringInchToStringCm(leftIRSensor));
		GuiSensorReadings.txtFrontCenterIR.setText(stringInchToStringCm(centreIRSensor));
		GuiSensorReadings.txtFrontRightIR.setText(stringInchToStringCm(rightIRSensor));

		//Bearing panel
		GuiSensorReadings.txtCompass.setText(compassAngle);
		GuiSensorReadings.txtTarget.setText(targetBearing);
		GuiSensorReadings.txtRes.setText(res);

	}

	/**
	 * 
	 * @param inch
	 * @return
	 */
	public String stringInchToStringCm(String inch)
	{
		return String.valueOf((Double.parseDouble(inch)*2.54));
	}

	ArrayList<String> getCommPorts() {

		if (!foundPorts)
			setCommPorts();
		foundPorts = true;
		return availablePorts;
	}

	/**
	 * 
	 * @param thePort
	 * @throws NoSuchPortException
	 */
	public void setChosenPort(String thePort) throws NoSuchPortException {

		chosenPort = thePort;
		serialPortId = CommPortIdentifier.getPortIdentifier(chosenPort);
	}

	/**
	 * 
	 */
	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {

		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

			try {
				String line = null;
				if (input.ready()) {

					line = input.readLine();
					System.out.println(line);

					upDateUI(line);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void send(String command) {

		p.print(command);
	}
}