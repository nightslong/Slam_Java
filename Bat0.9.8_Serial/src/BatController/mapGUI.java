package BatController;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Main mapGUI class
 * 
 * @author Sean Murphy 
 * Student number: 11829078
 * @author John Miles-Groves
 * 
 */
public class mapGUI {

	private JFrame mapFrame;

	LocalisationFilter filter;
	BatSerialConn serialClass;

	/**
	 * 
	 * 
	 * @param instance
	 * @param squareSize
	 * @param arraySize
	 * @param robotW
	 * @param robotH
	 * @param scale
	 * @param roboPos
	 * @param res
	 * @param windowTitle
	 */
	public mapGUI(BatSerialConn instance,int squareSize,int arraySize, int robotW,int robotH,double scale,int roboPos,int res,String windowTitle) {

		mapFrame = new JFrame( windowTitle );
		mapFrame.getContentPane().add( new Map(squareSize,arraySize,robotW,robotH,scale,roboPos) );
		mapFrame.setVisible( true );  // displays frame
		mapFrame.setSize( res+5, 6+res );
		mapFrame.getContentPane().setSize( res, res );
		mapFrame.setResizable( false );
		mapFrame.setLocationRelativeTo( null );
		mapFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );// allows the map to close separate from main GUI
		serialClass = instance;
	}

	/**
	 * inner class
	 * @author Sean Murphy
	 * Student number: 11829078
	 *
	 */
	private class Map extends Canvas {

		private static final long serialVersionUID = 1L;

		private int rowX;// col
		private int colY;// row
		private Graphics doubleGfxHolder; // holder for double buffer
		private Image OffScreen;// image object for double buffer
		private boolean st = false;
		private Robot robot;
		private Rectangle2D robotShape;

		private int wheelSegments = 0;

		private int wheelForward = 0;
		private int wheelReverse = 0;
		private int wheelRotate = 0;

		private int[][] mapXY = new int[rowX][colY];
		private int[][] obsXY = new int[rowX][colY];

		int sqSize;
		int roboStartpos;

		private int forward; // forward motion
		private int reverse; // reverse motion

		Shape temp1 = null;
		/**
		 * Creates the actual map.
		 * 
		 * @param sSize
		 * @param arrSize
		 * @param roboW
		 * @param roboH
		 * @param scale
		 * @param roboPos
		 */
		public Map(int sSize,int arrSize,int roboW,int roboH,double scale,int roboPos) {	

			filter = new LocalisationFilter(serialClass,sSize,arrSize,scale);
			robot = new Robot(0, 0, roboW, roboH);
			robotShape = null;
			roboStartpos = roboPos;
			rowX = arrSize;
			colY = arrSize;
			sqSize = sSize;
			forward =  sqSize;
			reverse = -sqSize;
		}

		/**
		 * Called by repaint()
		 */
		public void update( Graphics g ) {
			paint(g);
		}

		/**
		 * Paint called from update
		 */
		@Override
		public void paint ( Graphics g ) {
			Dimension d = getSize();
			checkOffScreenImage();// check if screen size has changed
			doubleGfxHolder =  OffScreen.getGraphics();
			doubleGfxHolder.setColor( getBackground() );
			doubleGfxHolder.fillRect( 0, 0, d.width, d.height );
			drawUpdateRTdata (  OffScreen.getGraphics() );// get g context we create in drawupdateRTdata
			g.drawImage( OffScreen, 0, 0, null );// first pass
		}

		/**
		 * Check if screen size has changed
		 * in preparation for double buffer
		 */
		private void checkOffScreenImage() {

			Dimension d = getSize();
			if ( OffScreen == null || OffScreen.getWidth( null ) != d.width
					|| OffScreen.getHeight( null ) != d.height ) {

				OffScreen = createImage( d.width,d.height );
			}
		}

		/**
		 * Draw the actual picture from buffer
		 * currently 50ms delay
		 * @param g
		 */
		public void drawUpdateRTdata ( Graphics g ) {

			final long startTime = System.currentTimeMillis();

			double width = getSize().width;
			double height = getSize().height;
			double htOfX = height / rowX;
			double wdOfX = width / colY;


			if ( st == false ) {

				int robotStartElavationInRows = roboStartpos; // Change this to move starting position of robot (up or down on map)
				int robotStartX = (int) ( ( ( colY*wdOfX )/2) - ( wdOfX * 2 ) ); // robots X start position on map (passed to g.fillRect)
				int robotStartY = (int) ( ( rowX*htOfX ) - ( htOfX * robotStartElavationInRows ) ); // robots Y start position on map (passed to g.fillRect)
				robot.setX( robotStartX - ( robot.getWidth()/2 ) );
				robot.setY( robotStartY - ( robot.getHeight()/2 ) );
				System.out.println( robotStartX );
				System.out.println( robotStartY );
				st = true;
				robotShape = new Rectangle2D.Double( robot.getX(), robot.getY() , robot.getWidth(), robot.getHeight() );
			}

			int compass = filter.cA;
			double radians = compass*( Math.PI/180 );// Converts compass bearing to radians
			Graphics2D g2d = ( Graphics2D ) g;
			Rectangle2D actRobot = robotShape.getBounds2D();
			AffineTransform at = new AffineTransform();



			// update the SLAM filter then draw
			//if ( temp1 != null ) {

			//	filter.update( temp1 );
			//}else {

			filter.update( actRobot );
			//}

			mapXY = filter.getMapXY();
			obsXY = filter.getObsXY();
			// main paint operation, this is the mostly costly operation within the entire program BIG O(n^2)
			for ( int x = 0; x < mapXY.length ; x++ ) {

				for ( int y = 0 ; y < mapXY.length; y++ ) {

					if ( obsXY[x][y] == 0 ) { // never observed
						g.setColor(Color.GRAY);// graphics context colour
						g.fillRect( x * sqSize, y * sqSize , sqSize, sqSize );
					}

					if ( mapXY[x][y] > 0 && obsXY[x][y] > 0 ) { // probably occupied
						g.setColor(Color.RED);// graphics context colour
						g.fillRect( x * sqSize, y * sqSize , sqSize, sqSize );
					}

					if ( mapXY[x][y] < 0 && obsXY[x][y] > 0 ) { // probably unoccupied
						g.setColor(Color.WHITE);// graphics context colour
						g.fillRect( x * sqSize, y * sqSize , sqSize, sqSize );
					}
				}
			}

			g.setColor(Color.BLUE);

			Shape temp;
			at.rotate( radians, actRobot.getCenterX(), actRobot.getCenterY() );
			g2d.draw( temp = at.createTransformedShape( actRobot ) );
			g2d.setTransform(at);
			g2d.draw( robotShape );
			// need to test these ints work everywhere and don't break 
			g.setColor(Color.ORANGE);
			g2d.fillRect((int)robotShape.getX(),(int) robotShape.getY(),(int) robotShape.getWidth(), (int) ((int)robotShape.getHeight()-(robotShape.getHeight()-5)));
			//	temp1 = temp;
			if ( sensorDataGetSet.getWheelForward() == 1 ) {

				straightMotion(radians,robotShape,forward);
			}
			if ( sensorDataGetSet.getWheelReverse() == 1 ) {

				straightMotion(radians,robotShape,reverse);
			}

			// can be used to measure time to complete map repaint
			final long endTime = System.currentTimeMillis();
			//System.out.println("Total execution time: " + ( endTime - startTime ) );
			repaint();
		}

		/**
		 * Allows the robot to move on the map
		 * called within the paint method above.
		 * 
		 * @param compass
		 * @param actRobo
		 * @param motion
		 */
		public void straightMotion(double compass,Rectangle2D actRobo, int motion) {

			// System.out.println("Wheel segments are " + sensorDataGetSet.getWheelRotation() );

			if( sensorDataGetSet.getWheelRotation() != 0 ) {

				wheelSegments = sensorDataGetSet.getWheelRotation() + wheelSegments;
			}

			if( wheelSegments >= 5 ) {

				double theta = compass;
				double hyp = motion;

				double opp = hyp * ( Math.cos( theta ) );
				double adj = hyp * ( Math.sin( theta ) );

				double x = actRobo.getX() + adj;
				double y = actRobo.getY() - opp;

				if (x > 0 && x < rowX*sqSize && y > 0 && y < colY*sqSize) {

					actRobo.setRect(x, y, actRobo.getWidth(), actRobo.getHeight());
				}
				wheelSegments = wheelSegments - 5;

				sensorDataGetSet.setWheelRotation("0");
			}
		}
	}
}