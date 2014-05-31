package BatController;

import gnu.io.NoSuchPortException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

/**
 * 
 * Creates a GUI which allows the user to connect and control the BAT.
 * @author Sean Murphy
 * @author John Miles-Groves
 * @author Odysseus McNally
 * @author Thomas Robins
 * @param <Picture>
 *
 */
public class GUI {

	public String portChoice;
	boolean serial;
	boolean wifi;
	//Serial connectivity class
	final BatSerialConn serialClass;

	//Compass
	JPanel pnlCompass;
	JLabel compassImg;

	//Robot Serial Connection
	JPanel pnlRobConSerial;
	JLabel lblRobConSerialPort;
	JComboBox<String> cmbPortList;
	JButton btnSerialConnect;

	//Robot Control Panel
	JPanel pnlRobotControl;
	JButton btnRobControlAuto;
	JButton btnRobControlFwdStep;
	JButton btnRobControlManual;
	JButton btnRobControlRight;
	JButton btnRobControlReverseStep;
	JButton btnRobControlLeft;
	JButton btnRobControlStop;
	JButton btnDesktopRealTimeMapLow;

	GuiSensorReadings sensorWindow;

	final JFrame windowFrame;
	final JButton parkButton;
	private JButton btnLaptopRealTimeMap;
	private JButton btnSensorReadingVIew;
	private JTextField txtPortNum;
	static JTextField txtCompassReading;

	/**
	 * Constructor.
	 */
	public GUI() {

		serialClass = new BatSerialConn();

		serial = false;
		wifi = false;

		windowFrame = new JFrame("BAT-III Controller");
		parkButton = new JButton("Park");

		windowFrame.setSize(350, 700); // Main window container size (in pixels) change this to resize whole GUI
		windowFrame.getContentPane().setLayout(null);

		windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowFrame.setVisible(true);

		sensorWindow = new GuiSensorReadings();

		setupCompass();		
		setupRobotConnectionPanel();				
		setupRobotControls();
		setupRealTimeMapButton();	

		setupActionListeners();

		windowFrame.setResizable(false);
	}

	public void setupActionListeners() {
		// Real Time Map Button Action Listener
		btnDesktopRealTimeMapLow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				new mapGUI(serialClass,4,200,47,55,10.16,100,800,"800x800 Real Time map ratio: 8x8"
						+ " pixels to 1 array index mapping (80,000 indexes)");
			}
		});

		// Stop Button Override Action Listener
		btnRobControlStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				serialClass.send("S");
			}
		});

		// Robot Control Manual Buttons
		// Forward Button Action Listener
		btnRobControlFwdStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				serialClass.send("^"); // one of these needs to change 
			}
		});

		// Left Button Action Listener
		btnRobControlLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				serialClass.send("<");
			}
		});

		// Reverse Button Action Listener
		btnRobControlReverseStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				serialClass.send("V");
			}
		});

		// Right Button Action Listener
		btnRobControlRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				serialClass.send(">");
			}
		});

		// Robot Control Buttons
		// Manual Control Button Action Listener
		btnRobControlManual.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				serialClass.send("M");
			}
		});

		// Robot Autopilot Button Action Listener
		btnRobControlAuto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				serialClass.send("A");
			}
		});

		// Robot Control Serial Connect Button Action Listener
		btnSerialConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				serialClass.connectButton();
			}
		});

		// Sensor reading view action listener
		btnSensorReadingVIew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sensorWindow.showSensorWindow();
			}
		});


		// Combo box event listener
		cmbPortList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				portChoice = (String) cmbPortList.getSelectedItem();//Get string from combo box selection
				try {		

					serialClass.setChosenPort(portChoice);//Set it to chosen port
				}
				catch (NoSuchPortException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setupRealTimeMapButton() {

		JPanel pnlRobWiFiConn = new JPanel();
		pnlRobWiFiConn.setBorder(new TitledBorder(null, "Robot WiFi Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlRobWiFiConn.setBounds(10, 11, 324, 63);
		windowFrame.getContentPane().add(pnlRobWiFiConn);
		pnlRobWiFiConn.setLayout(null);

		JButton btnWiFiConn = new JButton("Connect");
		btnWiFiConn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {


			}
		});
		btnWiFiConn.setBounds(195, 23, 97, 29);
		pnlRobWiFiConn.add(btnWiFiConn);

		txtPortNum = new JTextField();
		txtPortNum.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNum.setText("2042");
		txtPortNum.setBounds(129, 23, 60, 29);
		pnlRobWiFiConn.add(txtPortNum);
		txtPortNum.setColumns(10);

		JLabel lblPortNum = new JLabel("Port No");
		lblPortNum.setHorizontalAlignment(SwingConstants.CENTER);
		lblPortNum.setHorizontalTextPosition(SwingConstants.CENTER);
		lblPortNum.setBounds(59, 23, 60, 29);
		pnlRobWiFiConn.add(lblPortNum);

		JPanel panViewSelect = new JPanel();
		panViewSelect.setBorder(new TitledBorder(null, "Robot View Selections", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panViewSelect.setBounds(10, 329, 324, 236);
		windowFrame.getContentPane().add(panViewSelect);
		panViewSelect.setLayout(null);

		// Sensor reading view button
		btnSensorReadingVIew = new JButton("Sensor Reading View");
		btnSensorReadingVIew.setBounds(26, 202, 275, 23);
		panViewSelect.add(btnSensorReadingVIew);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Desktop View 800x800", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(26, 24, 275, 86);
		panViewSelect.add(panel);
		panel.setLayout(null);

		JButton btnDesktopRealTimeMapHigh = new JButton("High res map");
		btnDesktopRealTimeMapHigh.setBounds(10, 21, 116, 23);
		panel.add(btnDesktopRealTimeMapHigh);

		JButton btnDesktopRealTimeMapMedium = new JButton("Med res map");
		btnDesktopRealTimeMapMedium.setBounds(149, 21, 116, 23);
		panel.add(btnDesktopRealTimeMapMedium);
		// Real Time Map Button
		btnDesktopRealTimeMapLow = new JButton("Low res map");
		btnDesktopRealTimeMapLow.setBounds(74, 55, 130, 23);
		panel.add(btnDesktopRealTimeMapLow);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Laptop View 600x600", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(26, 128, 275, 63);
		panViewSelect.add(panel_1);
		panel_1.setLayout(null);

		// Laptop real time map - NOT IMPLEMENTED YET
		btnLaptopRealTimeMap = new JButton("Laptop Map");
		btnLaptopRealTimeMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new mapGUI(serialClass,2,400,23,27,5.08,200,600,"600x600 Real Time map ratio: 4x4"
						+ " pixel to 1 array index mapping (90,000 indexes)");
			}
		});
		btnLaptopRealTimeMap.setBounds(75, 29, 130, 23);
		panel_1.add(btnLaptopRealTimeMap);
		btnDesktopRealTimeMapMedium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new mapGUI(serialClass,2,400,47,55,5.08,200,800,"800x800 Real Time map ratio: 4x4"
						+ " pixel to 1 array index mapping (160,000 indexes)");
			}
		});
		btnDesktopRealTimeMapHigh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				new mapGUI(serialClass,1,800,23,27,2.54,400,800,"800x800 Real Time map ratio: 1 pixel"
						+ " to 1 array index mapping (640,000 indexes)");
			}
		});
	}

	private void setupRobotControls() {
		// Robot Control panel
		pnlRobotControl = new JPanel();
		pnlRobotControl.setBorder(new TitledBorder(null, "Robot Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlRobotControl.setBounds(10, 181, 324, 137);
		windowFrame.getContentPane().add(pnlRobotControl);
		pnlRobotControl.setLayout(null);

		// Robot Control Autopilot Button
		btnRobControlAuto = new JButton("Auto");
		btnRobControlAuto.setBounds(27, 22, 76, 23);
		pnlRobotControl.add(btnRobControlAuto);

		// Robot Control Forward Step Button
		btnRobControlFwdStep = new JButton("^");
		btnRobControlFwdStep.setBounds(123, 21, 76, 24);
		pnlRobotControl.add(btnRobControlFwdStep);

		// Robot Control Manual Pilot Button
		btnRobControlManual = new JButton("Manual");
		btnRobControlManual.setBounds(219, 21, 76, 24);
		pnlRobotControl.add(btnRobControlManual);

		// Robot Control Right Button
		btnRobControlRight = new JButton(">");
		btnRobControlRight.setBounds(219, 56, 76, 24);
		pnlRobotControl.add(btnRobControlRight);

		// Robot Control Reverse Step Button
		btnRobControlReverseStep = new JButton("V");
		btnRobControlReverseStep.setBounds(123, 56, 76, 24);
		pnlRobotControl.add(btnRobControlReverseStep);

		// Robot Control Left Button
		btnRobControlLeft = new JButton("<");
		btnRobControlLeft.setBounds(27, 56, 76, 24);
		pnlRobotControl.add(btnRobControlLeft);

		// Robot Control Stop Override Button		
		btnRobControlStop = new JButton("Stop");
		btnRobControlStop.setBounds(37, 91, 240, 24);
		pnlRobotControl.add(btnRobControlStop);
	}

	private void setupRobotConnectionPanel() {		
		// Robot Connect Serial panel
		pnlRobConSerial = new JPanel();
		pnlRobConSerial.setBounds(10, 85, 324, 85);
		windowFrame.getContentPane().add(pnlRobConSerial);
		pnlRobConSerial.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Robot Serial Connection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlRobConSerial.setLayout(null);

		// Robot Connection Serial Port list label
		lblRobConSerialPort = new JLabel("Serial Port");
		lblRobConSerialPort.setHorizontalAlignment(SwingConstants.CENTER);
		lblRobConSerialPort.setBounds(10, 40, 76, 29);
		pnlRobConSerial.add(lblRobConSerialPort);		

		// Robot Connection Serial Port List combo box		
		cmbPortList = new JComboBox<String>();
		cmbPortList.setBounds(96, 40, 92, 29);
		pnlRobConSerial.add(cmbPortList);		

		// obot Connection Serial connect button
		btnSerialConnect = new JButton("Connect");
		btnSerialConnect.setBounds(196, 40, 97, 29);
		pnlRobConSerial.add(btnSerialConnect);

		// Populate combo box
		for(int i=0; i < serialClass.getCommPorts().size()  ; i++) {
			cmbPortList.addItem(serialClass.getCommPorts().get(i));
		}		
	}


	private void setupCompass() {
		// Compass
		// Compass panel
		pnlCompass = new JPanel();
		pnlCompass.setBorder(new TitledBorder(null, "Compass", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlCompass.setBounds(53, 576, 235, 56);
		windowFrame.getContentPane().add(pnlCompass);
		
		txtCompassReading = new JTextField();
		txtCompassReading.setEditable(false);
		pnlCompass.add(txtCompassReading);
		txtCompassReading.setColumns(10);

		// windowFrame.setComponentZOrder( compass, 0 );
		// compass.setLayout(null);
		// JLayeredPane compassImgCon = new JLayeredPane();
		// compassImgCon.setBounds(313, 273, -289, -251);
		// compass.add(compassImgCon);
		// compass.add(compassImgCon, 0);// should move layered pane to the front on JPanel its in

		//compassImg = new JLabel("");
		//compassImg.setBounds(0, -195, -239, 195);
		// currently doesn't work when exporting to jar
		//compassImg.setIcon(new ImageIcon(GUI.class.getResource("images/compassLayerResize.png")));
		//pnlCompass.add(compassImg, 0);
		// end compass
	}
}