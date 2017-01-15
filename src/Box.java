import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

/**
 * Keeps track of Box information including whether a certain side
 * was clicked, position, how many sides were clicked, and whether
 * the box has been filled or not. Includes a constructor that creates
 * a default Box and another constructor to copy another Box object.
 * Includes isBoxFilled to check whether the box has been filled and to
 * update it when it is, boxFilled to check whether the box has been filled,
 * sidesClicked to return the number of sides clicked, sideToTake to return
 * an available side, hasSideBeenTaken to check whether the side has already
 * been clicked, playerFilledBox to check which player filled in the box,
 * setPosition to set the position of the box, draw to draw the box, drawLine
 * to highlight the player's last move, sideClicked to update the box when
 * a side has been clicked.
 * @author Stephanie Giang, Sherry Hua, Zoe Zou
 * @version June 11, 2015
 */
public class Box implements Serializable
{
	private boolean left;
	private boolean right;
	private boolean top;
	private boolean bottom;
	private int playerFilled;
	Point position;
	private int sidesClicked;

	private final static int SPACING = 75;
	private final static int WIDTH = 10;

	/**
	 * Creates the box object
	 */
	public Box()
	{
		this.left = false;
		this.right = false;
		this.top = false;
		this.bottom = false;
		sidesClicked = 0;
		playerFilled = 0;
	}
	
	public Box (Box otherBox){
		this.left = otherBox.left;
		this.right = otherBox.right;
		this.top = otherBox.top;
		this.bottom = otherBox.bottom;
	}

	/**
	 * Checks to see if the Box has been filled, if it is update the Box
	 * @param player the player that has clicked the box
	 * @return true if the box has been filled false if the box has not been
	 *         filled
	 */
	public boolean isBoxFilled(int player)
	{
		if (left == true && right == true && top == true && bottom == true)
		{
			playerFilled = player;
			return true;
		}
		return false;
	}

	/**
	 * Checks to see if the Box has been filled already been filled
	 * @return true if the box has been filled
	 * 			false if the box has not been filled
	 */
	public boolean boxFilled ()
	{
		if (left == true && right == true && top == true && bottom == true)
			return true;
		return false;
	}
	
	/**
	 * Checks to see how many sides of the box has been clicked
	 * @return the number of sides that have been clicked in a
	 * 			Box
	 */
	public int sidesClicked()
	{
		return this.sidesClicked;
	}

	/**
	 * Checks to see which side is available to click.
	 * @return 1 if the top side is available
	 * 			2 if the bottom side is available
	 * 			3 if the left side is available
	 * 			4 if the right side is available
	 * 			0 if no sides are available
	 */
	public int sideToTake()
	{
		if (!this.top)
			return 1;
		if (!this.bottom)
			return 2;
		if (!this.left)
			return 3;
		if (!this.right)
			return 4;
		return 0;
	}

	/**
	 * Checks to see whether a certain side of the box has been
	 * taken/clicked
	 * @param side the side to check
	 * @return true if the side has been taken
	 * 			false if the side has not been taken
	 */
	public boolean hasSideBeenTaken(int side)
	{
		if (side == 1)
			return top;
		if (side == 2)
			return bottom;
		if (side == 3)
			return left;
		if (side == 4)
			return right;
		return false;
	}

	/**
	 * Checks to see which player has filled in the box
	 * @return -1 if the villain has filled in the box
	 * 			1 if the superhero has filled in the box
	 * 			0 if no one filled in the box
	 */
	public int playerFilledBox()
	{
		return this.playerFilled;
	}

	/**
	 * Sets the position of the Box
	 * @param point the position to set the Box
	 */
	public void setPosition(Point point)
	{
		this.position = point;
	}
	
	/**
	 * Draws the 4 dots for each corner of the box, also draws a line if the side has been
	 * clicked, and fills in the box if the box has been filled.
	 * @param g the graphics context to paint
	 */
	public void draw(Graphics g)
	{
		// Draws the lines for if clicked
		g.setColor(new Color(60, 60, 60));
		if (top)
			g.fillRect(position.x, position.y, SPACING + 10, WIDTH);
		if (bottom)
			g.fillRect(position.x, position.y + SPACING, SPACING + 10, WIDTH);
		if (left)
			g.fillRect(position.x, position.y, WIDTH, SPACING + 10);
		if (right)
			g.fillRect(position.x + SPACING, position.y, WIDTH, SPACING + 10);

		// Draw the dots
		g.setColor(Color.BLACK);
		g.fillOval(position.x, position.y, WIDTH, WIDTH);
		g.fillOval(position.x + SPACING, position.y, WIDTH, WIDTH);
		g.fillOval(position.x, position.y + SPACING, WIDTH, WIDTH);
		g.fillOval(position.x + SPACING, position.y + SPACING, WIDTH, WIDTH);

		// Check to see if the box has been filled
		if (playerFilled != 0)
		{
			if (playerFilled == 1)
				g.setColor(new Color(225, 0, 0));
			else
				g.setColor(new Color(72, 61, 139));
			g.fillRect(position.x + WIDTH, position.y + WIDTH, SPACING - 10,
					SPACING - 10);
		}
		g.setColor(Color.BLACK);

	}
	
	/**
	 * Highlights the last move that the computer has made
	 * @param g the graphics context to paint
	 * @param side the side that the computer has chosen
	 */
	public void drawLine (Graphics g, int side)
	{
		g.setColor(new Color(125, 130, 63));
		if (side ==1)
		{
			g.fillRect(position.x, position.y, SPACING + 10, WIDTH);
			g.setColor(Color.BLACK);
			g.fillOval(position.x, position.y, WIDTH, WIDTH);
			g.fillOval(position.x + SPACING, position.y, WIDTH, WIDTH);
		}
		else if (side ==2)
		{
			g.fillRect(position.x, position.y + SPACING, SPACING + 10, WIDTH);
			g.setColor(Color.BLACK);
			g.fillOval(position.x, position.y + SPACING, WIDTH, WIDTH);
			g.fillOval(position.x + SPACING, position.y + SPACING, WIDTH, WIDTH);
		}
		else if (side ==3)
		{
			g.fillRect(position.x, position.y, WIDTH, SPACING + 10);
			g.setColor(Color.BLACK);
			g.fillOval(position.x, position.y, WIDTH, WIDTH);
			g.fillOval(position.x, position.y + SPACING, WIDTH, WIDTH);
		}
		else
		{
			g.fillRect(position.x + SPACING, position.y, WIDTH, SPACING + 10);
			g.setColor(Color.BLACK);
			g.fillOval(position.x + SPACING, position.y, WIDTH, WIDTH);
			g.fillOval(position.x + SPACING, position.y + SPACING, WIDTH, WIDTH);
		}
	}

	/**
	 * Checks to see if the side is already clicked, if not makes the side true.
	 * @param side the side to check
	 * @return true if the side has not been clicked false if the side has
	 *         already been clicked
	 */
	public boolean sideClicked(int side)
	{
		// 1 - top, 2 - bottom, 3 - left, 4 - right
		if (side == 1)
		{
			// To check to see if the side was already clicked
			if (this.top)
				return false;
			else
			{
				this.top = true;
				sidesClicked++;
			}
		}
		else if (side == 2)
		{
			// To check to see if the side was already clicked
			if (this.bottom)
				return false;
			else
			{
				this.bottom = true;
				sidesClicked++;
			}
		}
		else if (side == 3)
		{
			// To check to see if the side was already clicked
			if (this.left)
				return false;
			else
			{
				this.left = true;
				sidesClicked++;
			}
		}
		else
		{
			// To check to see if the side was already clicked
			if (this.right)
				return false;
			else
			{
				this.right = true;
				sidesClicked++;
			}
		}
		return true;
	}
}
