import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Statistics class. Keeps track of information including saved high score;
 * contains various method that alter the information on high score including
 * checking for a high score and writing it to the file. Includes an constructor
 * that creates the ArrayList, lengthOfArray that returns the length of the
 * array, topPlayer that returns a certain Player, isTopScore to check whether
 * the score is a top score, readFromFile to read from a file, writeToFile to
 * write to a file, addScore to add the score to the ArrayList and toString
 * to return the information as a string. 
 * @author Stephanie Giang, Zoe Zou, Sherilyn Hua
 * @version June 11 2015
 */
public class Statistics implements Serializable
{
	// An ArrayList of high scores
	ArrayList<Player> topScore;

	/**
	 * Constructs a new ArrayList of high scores
	 */
	public Statistics()
	{
		topScore = new ArrayList<Player>(5);
	}

	/**
	 * Determines the size of topScore
	 * @return the size of topScore
	 */
	public int lengthOfArray()
	{
		return topScore.size();
	}

	/**
	 * Finds the top player within the topScore ArrayList
	 * @param index the position of the player in topScore
	 * @return the player at the given position
	 */
	public Player topPlayer(int index)
	{
		return topScore.get(index);
	}

	/**
	 * Checks to see whether the score is a top score
	 * @param playerScore the score to check
	 * @return a index value of where the score should be inserted into the top
	 *         score array a -1 if it is not a top score
	 */
	public int isTopScore(int playerScore)
	{
		if (topScore.size() == 0)
			return 0;
		Player newScore = new Player(playerScore);
		for (int count = 0; count < topScore.size(); count++)
		{
			int difference = topScore.get(count).compareTo(newScore);
			if (difference > 0)
				return count;
		}
		if (topScore.size() < 5)
			return topScore.size();
		return -1;
	}

	/**
	 * Reads from a file and creates Statistic objects
	 * @param fileName the file to read from
	 * @return the file as Statistic objects
	 */
	public static Statistics readFromFile(String fileName)
	{
		try
		{
			ObjectInputStream fileIn = new ObjectInputStream(
					new FileInputStream(fileName));
			Statistics stats = (Statistics) fileIn.readObject();
			fileIn.close();
			return stats;
		}
		catch (Exception exp)
		{
			return new Statistics();
		}
	}

	/**
	 * Writes Statistic objects to a file
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
	 * Adds a score to the top score array
	 * @param score the score to add to the array
	 * @param index the position to add the array
	 */
	public void addScore(Player score, int index)
	{
		if (topScore.size() == 5)
		{
			topScore.remove(4);
			topScore.add(index, score);
		}
		else
			topScore.add(index, score);

	}

	/**
	 * Returns the Statistic information as a string
	 */
	public String toString()
	{
		StringBuilder score = new StringBuilder();
		score.append("Name            Score             Date\n");
		for (int index = 0; index < topScore.size(); index++)
		{
			score.append(topScore.get(index));
		}
		return score.toString();
	}
}
