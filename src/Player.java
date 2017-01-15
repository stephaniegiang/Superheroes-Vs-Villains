import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Keeps track of the player information including last move made, name, score,
 * player number, and date. 
 * @author Stephanie Giang, Sherilyn Hua, Zoe Zou
 * @version June 11 2015
 */
public class Player implements Comparable<Player>, Serializable
{
	// Initialize the variables
	private int score;
	private String name;
	public int lastCol;
	public int lastRow;
	public int lastSide;
	private int playerNo;
	Date date;
	private int fewestBoxes;
	String lowestBoxes;
	ArrayList<String> movesPossible;

	/**
	 * Construct a player object with the given score and the side of the player
	 * @param playerScore the score of the player
	 * @param player the side of the player
	 */
	public Player(int playerScore, int player)
	{
		playerNo = player;
		score = playerScore;
		lastRow = 0;
		lastCol = 0;
		lastSide = 0;
	}

	/**
	 * Construct a player object with the given score
	 * @param playerScore the score of the player
	 */
	public Player(int playerScore)
	{
		score = playerScore;
	}

	/**
	 * Construct a player object with the given player name, score and side of the player
	 * @param playerName the name of the player
	 * @param playerScore the score of the player
	 * @param playerNum the side of the player
	 */
	public Player(String playerName, int playerScore, int playerNum)
	{
		this.score = playerScore;
		if (playerName == null)
		{
			if (playerNum == 1)
				this.name = "Superhero";
			else
				this.name = "Villain";
		}
		else
			this.name = playerName;
		date = new Date();
	}

	/**
	 * Adds to the Player's score
	 * 
	 * @param playerScore the number to add to the score
	 */
	public void addScore(int playerScore)
	{
		this.score += playerScore;
	}

	/**
	 * Gets the score of the Player
	 * 
	 * @return the score
	 */
	public int getScore()
	{
		return this.score;
	}

	/**
	 * Resets the score of the Player
	 */
	public void resetScore()
	{
		this.score = 0;
	}

	/**
	 * Determines the last move of a player by giving its coordinates and side
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param edge the side that the move is on
	 */
	public void lastMove(int x, int y, int edge)
	{
		this.lastCol = y;
		this.lastRow = x;
		this.lastSide = edge;
	}

	/**
	 * Compares two Players' scores
	 * @param other the other Player to compare to
	 * @return positive, if other is greater than this; negative, if other is
	 *         less than this; zero, if they are equal
	 */
	public int compareTo(Player other)
	{
		return other.score - this.score;
	}

	/**
	 * Formats the output of a player object
	 * @return the formatted score line
	 */
	public String toString()
	{
		String newDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
		return String.format("%-25s%-17d%s", name, score, newDate);
	}

	// ////////////////////////////////////////////////////////////
	// METHODS BELLOW WILL ONLY BE USED FOR THE COMPUTER PLAYER //
	// ////////////////////////////////////////////////////////////

	/**
	 * Generate a random move the computer can make
	 * @param board the board the game is on
	 * @param possibleSides the possible sides the computer can choose
	 * @return the side the player chooses
	 */
	public int[] makeMove(Grid board, ArrayList<String> possibleSides)
	{
		int[] moves = new int[3];
		boolean boxFilled = false;
		int check = 0;
		// Check to see if there are three sides of a box that has been clicked
		// There will be two checks if there is a box filled
		do
		{
			check++;
			for (int row = 0; row < 8; row++)
			{
				for (int col = 0; col < 8; col++)
				{
					int sidesClicked = (board.getBoard())[row][col]
							.sidesClicked();
					// If there are 3 sides filled, fill in the box
					if (sidesClicked == 3)
					{
						// If there is a box filled make it true
						boxFilled = true;
						int sideToTake = (board.getBoard())[row][col]
								.sideToTake();
						int score = board.updateBoxComputer(row, col,
								sideToTake, this.playerNo);
						// If a box is filled, add it the the score
						if (score > 0)
							this.addScore(score);
					}
				}
			}
		}
		while (boxFilled && check < 3);

		// Make a copy of the original array
		movesPossible = new ArrayList<String>(possibleSides);

		// Check the entire array and remove sides that would result to a box to
		// have
		// 3 sides filled if clicked
		for (String nextSide : possibleSides)
		{
			int row = nextSide.charAt(0) - '0';
			int col = nextSide.charAt(2) - '0';
			int sideChosen = nextSide.charAt(4) - '0';
			int sidesClicked = (board.getBoard())[row][col].sidesClicked();
			if (sidesClicked < 2)
			{
				if (sideChosen == 1 && row > 0)
					// Check to see if box above has 2 sides clicked
					if ((board.getBoard())[row - 1][col].sidesClicked() >= 2)
						movesPossible.remove(row + "," + col + ",1");

				if (sideChosen == 3 && col > 0)
				{
					// Check to see if box to left has 2 sides clicked
					if ((board.getBoard())[row][col - 1].sidesClicked() >= 2)
						movesPossible.remove(row + "," + col + ",3");
				}
			}
			else
			{
				// If the box has more than 2 sides filled, remove all the
				// available sides
				for (int side = 1; side <= 4; side++)
				{
					int indexOf = movesPossible.indexOf(row + "," + col + ","
							+ side);
					if (indexOf >= 0)
						movesPossible.remove(indexOf);
				}
			}
		}

		String moveToMake;
		// If there are available sides in array use those to calculate next
		// move
		if (movesPossible.size() > 0)
		{
			int randomSide = (int) (Math.random() * movesPossible.size());
			moveToMake = movesPossible.get(randomSide);
		}
		else
		{
			// If not check to see which move would result to the fewest boxes
			// formed
			moveToMake = fewestBoxes(possibleSides, board);
		}
		moves[0] = moveToMake.charAt(0) - '0';
		moves[1] = moveToMake.charAt(2) - '0';
		moves[2] = moveToMake.charAt(4) - '0';

		// remove the side from the array
		possibleSides.remove(moveToMake);

		return moves;
	}

	/**
	 * To find the move the computer shall choose to make the player fill least boxes
	 * @param possibleMoves the possible moves the computer can make
	 * @param board the board the game is working on
	 * @return the move the computer shall choose to make the player fill least boxes
	 */
	public String fewestBoxes(ArrayList<String> possibleMoves, Grid board)
	{
		fewestBoxes = 1000;
		int boxesFilled = 0;

		// Make copy of the original possibleMoves
		ArrayList<String> movesPossible = new ArrayList<String>(possibleMoves);

		// Go through all the possibleMoves
		for (int nextMove = 0; nextMove < movesPossible.size(); nextMove++)
		{
			String theNextMove = movesPossible.get(nextMove);
			Grid copyBoard = new Grid(board.getBoard());
			
			//Get the row, column and side from the String
			int row = theNextMove.charAt(0) - '0';
			int col = theNextMove.charAt(2) - '0';
			int side = theNextMove.charAt(4) - '0';
			copyBoard.updateBoardComputer(row, col, side, this.playerNo);

			//Make two rounds of checking through the board
			for (int check = 0; check <= 2; check++)
			{
				for (int rows = 0; rows < 8; rows++)
				{
					for (int cols = 0; cols < 8; cols++)
					{
						int sidesClicked = (copyBoard.getBoard())[rows][cols]
								.sidesClicked();
						if (sidesClicked == 3)
						{
							int sideToClick = (copyBoard.getBoard())[rows][cols]
									.sideToTake();
							int filledBoxes = copyBoard.updateBoardComputer(
									rows, cols, sideToClick, this.playerNo);
							if (filledBoxes > 0)
								boxesFilled += filledBoxes;
						}
					}
				}
			}
			
			//Compare the current number of filled boxes with historical lowest boxes and update the record if needed
			if (boxesFilled < fewestBoxes)
			{
				fewestBoxes = boxesFilled;
				lowestBoxes = theNextMove;
			}
			boxesFilled = 0;
		}

		return lowestBoxes;
	}

}