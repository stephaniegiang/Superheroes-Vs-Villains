import java.awt.Graphics;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Keeps track of the grid information including the possible sides and
 * boxes in the grid. Includes a constructor to create a default Grid object
 * and another to copy a current Grid object. Includes updateGrid that updates
 * Grid on current player and whether there is a computer, computerPlayer that
 * checks to see if there is a computer player, currentPlayer that returns
 * the character of the player, possibleMoves to return the ArrayList of
 * possibleMoves, getBoard that returns the array of Box objects, updateBoxOnePlayer
 * that updates the grid when a player has made a move in one player game, updateBox
 * that updates the grid when a player has made a move in 2 player game, updateBoxComputer
 * that updates the grid when the computer has made a move, updateBoardComputer that updates
 * the grid when the computer is choosing a move, calculateScore to calculate the score,
 * draw to draw the board, drawComputer to highlight the last move made by the computer. 
 * @author Stephanie Giang, Sherilyn Hua, & Zoe Zou
 * @version June 11, 2015
 */
public class Grid implements Serializable
{
	private final static int POINT_SPACING = 75;
	private final static int LEFT_SPACING = 17;
	private final static int TOP_SPACING = 44;
	
	private static final long serialVersionUID = 1L;
	private Box[][] grid;
	private ArrayList<String> possibleSides;
	private int currentPlayer;
	private int computer;

	/**
	 * Constructs a Grid object
	 */
	public Grid()
	{
		currentPlayer = 0;
		computer = 0; 
		grid = new Box[8][8];
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				grid[row][col] = new Box();
			}
		}
		
		// The ArrayList of possible moves that can be made
		possibleSides=new ArrayList<String>(64);
		for (int row=0;row<=6;row++)
		{
			for (int col=0;col<=7;col++)
			{
				possibleSides.add(row+","+col+",1");
				possibleSides.add(row+","+col+",3");
			}
			possibleSides.add(row+","+7+",4");
		}
		for (int col=0;col<=7;col++)
		{
			possibleSides.add(7+","+col+",1");
			possibleSides.add(7+","+col+",2");
			possibleSides.add(7+","+col+",3");
		}
		possibleSides.add(7+","+7+",4");
	}
	
	/**
	 * Constructs a Grid object with the given 
	 * @param board
	 */
	public Grid (Box [][] board){
		grid = new Box [8][8];
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				grid[row][col] = new Box (board[row][col]);
			}
		}
		
	}
	
	/**
	 * Updates the current player's turn and whether there is
	 * a computer or not. Used for save and load game.
	 * @param current the current player's turn
	 * @param isComputer whether there is a computer or not
	 */
	public void updateGrid (int current, int isComputer)
	{
		currentPlayer = current;
		computer = isComputer;
	}
	
	/**
	 * Returns the character of the computer player
	 * @return -1 if it is a villain
	 * 			1 if it is a superhero
	 * 			0 if it is none
	 */
	public int computerPlayer ()
	{
		return computer;
	}
	
	/**
	 * Returns the character of the player
	 * @return -1 if it is a villain
	 * 			1 if it is a superhero
	 */
	public int currentPlayer ()
	{
		return currentPlayer;
	}
	
	/**
	 * Returns the possible sides
	 * @return the ArrayList of strings that contain the possible moves
	 */
	public ArrayList<String> possibleMoves ()
	{
		return this.possibleSides;
	}
	
	/**
	 * Returns grid that contains Box objects
	 * @return the array of Boxes (objects)
	 */
	public Box[][] getBoard()
	{
		return this.grid;
	}
	

	/**
	 * Checks to see if the side of the box was already clicked. Also
	 * updates the Box that was clicked and checks to see if it affects
	 * other Box objects as well. Also updates the possibleSides by
	 * removing the sides clicked and the sides that were affected by the
	 * click. This is used in the one player game. 
	 * @param x the x coordinate of the click
	 * @param y the y coordinate of the click
	 * @param side the side that the player clicked
	 * @param currentPlayer the player that clicked the box
	 * @param possibleSides contains the available moves left
	 * @return -1 if the side was already clicked
	 * 			0 if a side was clicked and no boxes were filled
	 * 			>0 if a side was clicked and a box was filled
	 */
	public int updateBoxOnePlayer(int x, int y, int side, int currentPlayer)
	{
		// 1 - top, 2 - bottom, 3 - left, 4 - right
		int filledBoxes = 0;
		boolean lineClicked = false;
		int row = (y - 40) / POINT_SPACING;
		int col = (x - 10) / POINT_SPACING;
		if (row > 7)
			row--;
		else if (col > 7)
			col--;

		// Checks to see if that side has already been clicked
		if (grid[row][col].sideClicked(side) == false)
			return -1;
		// Checks to see if the box is filled
		if (grid[row][col].isBoxFilled(currentPlayer))
			filledBoxes++;

		// Remove the side that was clicked from available moves
		int indexOfOriginal = possibleSides.indexOf(row+","+col+","+side);
		if (indexOfOriginal != -1)
			possibleSides.remove(indexOfOriginal);
		// Check to see if the box next to the current box that has been clicked
		// If so, make the appropriate side true
		int indexOf = -1;
		if (side == 1)
		{
			if (row == 0)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row - 1][col].sideClicked(2))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row - 1][col].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf((row-1)+","+"col"+",2");
			}
		}
		else if (side == 2)
		{
			if (row == 7)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row + 1][col].sideClicked(1))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row + 1][col].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf((row+1)+","+"col"+",1");
			}
		}
		else if (side == 3)
		{
			if (col == 0)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row][col - 1].sideClicked(4))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row][col - 1].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf(row+","+"(col-1)"+",4");
			}
		}
		else
		{
			if (col == 7)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row][col + 1].sideClicked(3))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row][col + 1].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf(row+","+"(col-1)"+",3");
			}
		}
		
		// If indexOf is not -1 remove it from possibleSides
		if (indexOf != -1)
			possibleSides.remove(indexOf);
		// If boxes were filled
		if (filledBoxes > 0)
			return filledBoxes;
		// If a line was just clicked
		if (lineClicked)
			return 0;
		return -1;
	}
		
	/**
	 * Checks to see if the side of the box was already clicked. Also
	 * updates the Box that was clicked and checks to see if it affects
	 * other Box objects as well. This is used in the two player game. 
	 * @param x the x coordinate of the click
	 * @param y the y coordinate of the click
	 * @param side the side that the player clicked
	 * @param currentPlayer the player that clicked the box
	 * @return -1 if the side was already clicked
	 * 			0 if a side was clicked and no boxes were filled
	 * 			>0 if a side was clicked and a box was filled
	 */
		public int updateBox(int x, int y, int side, int currentPlayer)
		{
			int filledBoxes = 0;
			boolean lineClicked = false;
			// Find the row and columns of the box affected
			int row = (y - 40) / POINT_SPACING;
			int col = (x - 10) / POINT_SPACING;
			// Check for out of bounds
			if (row > 7)
				row--;
			else if (col > 7)
				col--;

			// Checks to see if that side has already been clicked
			if (grid[row][col].sideClicked(side) == false)
				return -1;
			// Checks to see if the box is filled
			if (grid[row][col].isBoxFilled(currentPlayer))
				filledBoxes++;

			// Check to see if the box next to the current box that has been clicked
			// If so, make the appropriate side true
			if (side == 1)
			{
				if (row == 0)
					lineClicked = true;
				else
				{
					// Make the affected side true
					if (grid[row - 1][col].sideClicked(2))
						lineClicked = true;
					// Check to see if the box has been filled
					if (grid[row - 1][col].isBoxFilled(currentPlayer))
						filledBoxes++;
				}
			}
			else if (side == 2)
			{
				if (row == 7)
					lineClicked = true;
				else
				{
					// Make the affected side true
					if (grid[row + 1][col].sideClicked(1))
						lineClicked = true;
					// Check to see if the box has been filled
					if (grid[row + 1][col].isBoxFilled(currentPlayer))
						filledBoxes++;
				}
			}
			else if (side == 3)
			{
				if (col == 0)
					lineClicked = true;
				else
				{
					// Make the affected side true
					if (grid[row][col - 1].sideClicked(4))
						lineClicked = true;
					// Check to see if the box has been filled
					if (grid[row][col - 1].isBoxFilled(currentPlayer))
						filledBoxes++;
				}
			}
			else
			{
				if (col == 7)
					lineClicked = true;
				else
				{
					// Make the affected side true
					if (grid[row][col + 1].sideClicked(3))
						lineClicked = true;
					// Check to see if the box has been filled
					if (grid[row][col + 1].isBoxFilled(currentPlayer))
						filledBoxes++;
				}
			}

		// If boxes were filled
		if (filledBoxes > 0)
			return filledBoxes;
		// If a line was just clicked
		if (lineClicked)
			return 0;
		return -1;

	}

	/**
	 * Checks to see if the side of the box was already clicked. Also
	 * updates the Box that was clicked and checks to see if it affects
	 * other Box objects as well. This is used by the computer player.
	 * Updates the possibleSides as well. 
	 * @param row the row of the Box
	 * @param col the col of the Box
	 * @param side the side of the Box
	 * @param currentPlayer the character the computer is playing
	 * @param possibleSides the possible moves left
	 * @return -1 if the side was already clicked
	 * 			0 if a side was clicked and no boxes were filled
	 * 			>0 if a side was clicked and a box was filled
	 */
	public int updateBoxComputer(int row, int col, int side, int currentPlayer)
	{
		int filledBoxes = 0;
		boolean lineClicked = false;
		int indexOf=0;
		// Check to see if side was clicked before, if so update the Box information
		if (!grid[row][col].sideClicked(side))
			return -1;
		// Check to see if current Box is filled 
		if (grid[row][col].isBoxFilled(currentPlayer))
			filledBoxes++;
		// Remove it from possible moves
		possibleSides.remove(row +"," + col +"," + side);
		
		// Check to see if the box next to the current box that has been clicked
		// If so, make the appropriate side true
		if (side == 1)
		{
			// Check for out of bounds
			if (row == 0)
			{
				lineClicked = true;
				indexOf=-1;
			}
			else
			{
				// Make the affected side true
				if (grid[row - 1][col].sideClicked(2))
					lineClicked = true;
				// Check to see if box is filled
				if (grid[row - 1][col].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf((row-1)+","+col+",2");
			}
		}
		else if (side == 2)
		{
			// Check for out of bounds
			if (row == 7)
			{
				lineClicked = true;
				indexOf=-1;
			}
			else
			{
				// Make the affected side true
				if (grid[row + 1][col].sideClicked(1))
					lineClicked = true;
				// Check to see if box is filled
				if (grid[row + 1][col].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf((row+1)+","+col+",1");
			}
		}
		else if (side == 3)
		{
			// Check for out of bounds
			if (col == 0)
			{
				lineClicked = true;
				indexOf=-1;
			}
			else
			{
				// Make the affected side true
				if (grid[row][col - 1].sideClicked(4))
					lineClicked = true;
				// Check to see if box is filled
				if (grid[row][col - 1].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf(row+","+(col-1)+",4");
			}
		}
		else
		{
			// Check for out of bounds
			if (col == 7)
			{
				lineClicked = true;
				indexOf=-1;
			}
			else
			{
				// Make affected side true
				if (grid[row][col + 1].sideClicked(3))
					lineClicked = true;
				// Check to see if box is filled
				if (grid[row][col + 1].isBoxFilled(currentPlayer))
					filledBoxes++;
				indexOf = possibleSides.indexOf(row+","+(col+1)+",3");
			}
		}
		// If indexOf is not -1 remove it from possibleSides
		if (indexOf != -1)
			possibleSides.remove(indexOf);
		if (filledBoxes > 0)
			return filledBoxes;
		if (lineClicked)
			return 0;
		return -1;

	}
	
	/**
	 * Checks to see if the side of the box was already clicked. Also
	 * updates the Box that was clicked and checks to see if it affects
	 * other Box objects as well. This is used to calculate the next
	 * move that the computer should make.
	 * @param row the row of the Box
	 * @param col the column of the Box
	 * @param side the side of the Box
	 * @param currentPlayer the character the computer is playing
	 * @param possibleSides the possible moves left
	 * @return -1 if the side was already clicked
	 * 			0 if a side was clicked and no boxes were filled
	 * 			>0 if a side was clicked and a box was filled
	 */
	public int updateBoardComputer(int row, int col, int side, int currentPlayer)
	{
		int filledBoxes = 0;
		boolean lineClicked = false;
		// Checks to see if that side has already been clicked
		if (grid[row][col].sideClicked(side) == false)
			return -1;
		// Checks to see if the box is filled
		if (grid[row][col].isBoxFilled(currentPlayer))
			filledBoxes++;

		// Check to see if the box next to the current box that has been clicked
		// If so, make the appropriate side true
		if (side == 1)
		{
			if (row == 0)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row - 1][col].sideClicked(2))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row - 1][col].isBoxFilled(currentPlayer))
					filledBoxes++;
			}
		}
		else if (side == 2)
		{
			if (row == 7)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row + 1][col].sideClicked(1))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row + 1][col].isBoxFilled(currentPlayer))
					filledBoxes++;
			}
		}
		else if (side == 3)
		{
			if (col == 0)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row][col - 1].sideClicked(4))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row][col - 1].isBoxFilled(currentPlayer))
					filledBoxes++;
			}
		}
		else
		{
			if (col == 7)
				lineClicked = true;
			else
			{
				// Make the affected side true
				if (grid[row][col + 1].sideClicked(3))
					lineClicked = true;
				// Check to see if the box has been filled
				if (grid[row][col + 1].isBoxFilled(currentPlayer))
					filledBoxes++;
			}
		}

	// If boxes were filled
	if (filledBoxes > 0)
		return filledBoxes;
	// If a line was just clicked
	if (lineClicked)
		return 0;
	return -1;

}

	/**
	 * Clears the grid
	 */
	public void clear()
	{
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				grid[row][col] = new Box();
			}
		}
	}
	
	/**
	 * Calculates the score for a specific player
	 * @param playerNum the player
	 * @return the score of the player
	 */
	public int calculateScore(int playerNum)
	{
		int score = 0;
		for (int row = 0; row < grid.length; row++)
		{
			for (int col = 0; col < grid[row].length; col++)
			{
				int playerFilled = grid[row][col].playerFilledBox();
				if (playerFilled == playerNum)
					score++;
			}
		}
		return score;
	}

	/**
	 * Reads from a file and creates Grid objects
	 * @param fileName the file to read from
	 * @return the file as Grid objects
	 */
	public static Grid readFromFile(String fileName)
	{
		try
		{
			ObjectInputStream fileIn = new ObjectInputStream(
					new FileInputStream(fileName));
			Grid board = (Grid) fileIn.readObject();
			fileIn.close();
			return board;
		}
		catch (Exception exp)
		{
			return new Grid();
		}
	}

	/**
	 * Writes Grid objects to a file
	 * @param fileName the file to write to
	 */
	public void writeToFile(String fileName)
	{
		try
		{
			ObjectOutputStream fileOut = new ObjectOutputStream(
					new FileOutputStream(fileName));
			fileOut.writeObject(this);
			fileOut.close();
		}
		catch (IOException exp)
		{
			System.out.println("Error writing to the file");
		}
	}

	/**
	 * Draws the grid. 
	 * @param g
	 */
	public void draw(Graphics g)
	{
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
			{
				grid[row][col].setPosition(new Point(POINT_SPACING * col
						+ LEFT_SPACING, POINT_SPACING * row + TOP_SPACING));
				grid[row][col].draw(g);
			}
	}
	
	/**
	 * Highlights the computer's last move on the board
	 * @param g
	 * @param computer the Player that is the computer
	 * @param newGame whether the game is a newGame or not
	 */
	public void drawComputer (Graphics g, Player computer, boolean newGame)
	{
		// Only highlight the player's last move if it is not a new game
		if (!newGame)
			grid[computer.lastRow][computer.lastCol].drawLine(g, computer.lastSide);
	}
}