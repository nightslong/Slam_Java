package BatController;
/**
 * Robot object
 * 
 * @author S Murphy
 *
 */
public class Robot {

	private int topX 		 = 0; // Top left corner X
	private int topY 		 = 0; // Top left corner Y
	private int width		 = 0; // Width of robot
	private int height		 = 0; // Height of robot
	private int centerFrontX = 0;
	private int centerBackX  = 0;
	private int centerBackY  = 0;
	private int sqSize       = 1; // set this to map square size ( mapGui class x or y / window size (840) )

	/**
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public Robot(int x,int y,int w,int h) {

		topX = x; topY = y;
		width = w; height = h;
	}

	public int getX()      { return topX; }
	public int getY()      { return topY; }
	public int getWidth()  { return width; }
	public int getHeight() { return height; }
	
	public int getCenterFront() { return centerFrontX; }

	public int getCenterBackX() { return centerBackX; }
	public int getCenterBackY() { return centerBackY; }
	
	/**
	 * 
	 * @param x
	 */
	public void setX( int x ) {
		
		topX = x;
		centerFrontX = topX + ( width/2 ) + sqSize;
	}

	/**
	 * 
	 * @param y
	 */
	public void setY( int y ) {

		topY = y;
		setCenterBack();
	}
	/**
	 * 
	 */
	public void setCenterBack() {
		
		centerBackY = topY + height;
		centerBackX = topX + ( width/2 ) + sqSize;
	}
}