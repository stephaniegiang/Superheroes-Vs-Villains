import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.*;

/**
 * The SuperVillainBoard class. Keeps track of a Super Villain game board
 * information including the board's size and various other variables and
 * constants; contains various methods that alter the board's information.
 * Includes an constructor that sets up the game and board. Includes a
 * loadGame method to load a save game, saveGame to save a current game,
 * newGame to start a new game, getNoOfPlayers to ask the user for the 
 * number of players that will be played, getSide to ask the user for the
 * side that have chosen in a one player game, checkIsOnEdge to see if the
 * player has clicked an edge of a box, gameOver to load the game over page,
 * paintComponent to draw all the images for the game, computerMove to call
 * the AI, makeMove to update the board when a player has made a move, and
 * playMusic to continue to play the background music. 
 * @author Stephanie Giang, Zoe Zou, and Sherilyn Hua
 * @version June 11, 2015
 */
public class SuperVillainsBoard extends JPanel implements MouseListener
{
	private static final long serialVersionUID = 1L;

	// Constants that can only be accessed within the class
	private final int POINT_SPACING = 75;
	private final int NO_OF_ROWS = 8;
	private final int NO_OF_COLUMNS = 8;

	// Static constants that can only be accessed within the class
	private final static int LEFT_SPACING = 17;
	private final static int TOP_SPACING = 44;

	// Constants that can be accessed from both within the class and
	// elsewhere
	public final int SUPERHERO = 1;
	public final int VILLAINS = -1;
	public final Dimension BOARD_SIZE = new Dimension(NO_OF_COLUMNS
			* POINT_SPACING + 400, NO_OF_ROWS * POINT_SPACING + 100);

	// Variables that can only be accessed within the class
	private boolean mouseOverLoadGame;
	private boolean mouseOverNewGame;
	private boolean mouseOverHelpMain;
	private boolean mouseOverRestart;
	private boolean mouseOverHelp;
	private boolean mouseOverMainMenu;
	private boolean mouseOverSaveGame;
	private boolean mouseOverQuit;
	private boolean mouseOverSetting;
	private boolean mouseOverBackMain;
	private boolean mouseOverBackGame;
	private boolean gameOver;
	private boolean showSetting;
	private boolean mouseOverMusic;
	private boolean musicOff;
	private boolean mouseOverQuitSetting;
	private boolean mouseOverNext;
	private boolean mouseOverBack;
	private boolean showHighScores;
	private boolean viewHighScores;
	private boolean inGame;
	private int pageNo;
	private int numFilled;
	private int currentPlayer;
	private int noOfPlayers;
	private int side;
	private int computer;

	// Public game variables
	boolean loadGame;
	boolean firstMove;
	boolean computerPlayer;
	boolean isNewGame;
	boolean showMainMenu;
	boolean restart;

	// The ArrayList of possible sides
	ArrayList<String> possibleSides;

	// The high score information
	Statistics myStats;

	// The game board
	Grid board;

	// The two players
	Player superhero;
	Player villain;

	// Background music
	private AudioClip backgroundMusic;

	/**
	 * Constructs a new SuperVillainsBoard object by initializing certain
	 * instance variables and setting the game board layout
	 */
	public SuperVillainsBoard()
	{
		// Initializing the instance variables
		isNewGame = false;
		mouseOverLoadGame = false;
		mouseOverNewGame = false;
		mouseOverHelpMain = false;
		mouseOverQuit = false;
		mouseOverSetting = false;
		showSetting = false;
		showHighScores = false;
		mouseOverMusic = false;
		musicOff = false;
		mouseOverQuitSetting = false;
		mouseOverNext = false;
		inGame = false;
		board = new Grid();
		showMainMenu = true;
		loadGame = false;
		superhero = new Player(0, 1);
		villain = new Player(0, -1);

		computer = 0;
		firstMove = false;

		// Sets up the board area
		setPreferredSize(BOARD_SIZE);
		setBackground(new Color(219, 207, 202));
		// Add mouse listeners to the game board
		this.addMouseListener(this);
		// Add mouse listeners to the drawing panel
		this.addMouseMotionListener(new MouseMotionHandler());
		setFocusable(true);
		requestFocusInWindow();
		myStats = Statistics.readFromFile("playerData.dat");
		// Add music
		backgroundMusic = Applet.newAudioClip(getCompleteURL("bgm.wav"));
		// Sets up the background music
		playMusic();
	}

	/**
	 * Loads the game that has previously been saved
	 */
	public void loadGame()
	{
		loadGame = true;

		// Reads the saved game from a file
		board = Grid.readFromFile("gameBoard.dat");

		// Calculates the score of each side and adds them back onto the game
		superhero.addScore(board.calculateScore(1));
		villain.addScore(board.calculateScore(-1));
		numFilled = superhero.getScore() + villain.getScore();
		currentPlayer = board.currentPlayer();
		computer = board.computerPlayer();
		if (computer != 0)
		{
			noOfPlayers = 1;
			if (computer == 1)
				side = -1;
			else
				side = 1;
		}
		else
			noOfPlayers = 2;
		newGame();
		loadGame = false;
	}

	/**
	 * Starts a new game or loads a previous game according to the player's
	 * choice
	 */
	public void newGame()
	{
		inGame = true;
		isNewGame = true;

		// If the player chooses to start a new game
		if (loadGame == false)
		{
			// Initializes a new board
			board = new Grid();
			numFilled = 0;
			restart = false;
			mouseOverRestart = false;
			mouseOverHelp = false;
			mouseOverMainMenu = false;
			mouseOverSaveGame = false;
			gameOver = false;
			pageNo = 2;
			currentPlayer = 1;

			// Resets the score
			superhero.resetScore();
			villain.resetScore();

			// Gets the number of players by prompting the player
			noOfPlayers = getNoOfPlayers();

			// After the player chooses the number of players
			if (!showMainMenu)
			{
				if (noOfPlayers == 1)
				{
					// Determines the side(s) of the player(s)
					side = getSide();
					if (side == SUPERHERO)
					{
						computer = -1;
					}
					else
					{
						computer = 1;
						currentPlayer = -1;
					}
				}
			}
			repaint();
		}
		// If the player chooses to load a previously saved game
		else
		{
			mouseOverRestart = false;
			mouseOverHelp = false;
			mouseOverMainMenu = false;
			mouseOverSaveGame = false;
			restart = false;
			pageNo = 2;

			// Displays reminder of the type of game accordingly
			if (noOfPlayers == 1)
				JOptionPane.showMessageDialog(this,
						"This is a saved ONE player game");
			else
				JOptionPane.showMessageDialog(this,
						"This is a saved TWO player game");
			repaint();
		}
	}

	/**
	 * Gets the number of players that the user wants in the game
	 * @return the number of players
	 */
	public int getNoOfPlayers()
	{
		int players = 1;
		// Create a panel with radio buttons
		JPanel panel = new JPanel();
		Border lowerEtched = BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED);

		panel.setBorder(BorderFactory.createTitledBorder(lowerEtched,
				"Choose Number of Player(s)"));
		panel.setLayout(new GridLayout(1, 2));

		// Create a group of radio buttons to add to the Panel
		ButtonGroup playerGroup = new ButtonGroup();
		JRadioButton[] buttonList = new JRadioButton[2];

		// Create and add each radio button to the panel
		buttonList[0] = new JRadioButton("1 Player", true);
		buttonList[1] = new JRadioButton("2 Players");
		playerGroup.add(buttonList[0]);
		playerGroup.add(buttonList[1]);
		panel.add(buttonList[0]);
		panel.add(buttonList[1]);

		// Show a dialog with the panel attached
		int choice = JOptionPane.showConfirmDialog(this, panel,
				"Player Options", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.DEFAULT_OPTION);

		// Update grade if OK is selected
		if (choice == JOptionPane.OK_OPTION)
		{
			for (int index = 0; index < buttonList.length; index++)
				if (buttonList[index].isSelected())
					players = index + 1;
			showMainMenu = false;
		}
		// Otherwise go back to main menu
		else
		{
			inGame = false;
			showMainMenu = true;
			pageNo = 0;
			repaint();
		}
		return players;
	}

	/**
	 * Gets the side (Superhero or Villain) that the player wants to play if one
	 * player option is selected.
	 * @return 1 if Superhero is selected -1 if Villain is selected
	 */
	public int getSide()
	{
		int playerSide = 1;
		// Create a panel with radio buttons
		JPanel panel = new JPanel();
		Border lowerEtched = BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED);

		panel.setBorder(BorderFactory.createTitledBorder(lowerEtched,
				"Choose which side do you want to be"));
		panel.setLayout(new GridLayout(2, 1));

		// Create a group of radio buttons to add to the Panel
		ButtonGroup sideGroup = new ButtonGroup();
		JRadioButton[] buttonList = new JRadioButton[2];

		// Create and add each radio button to the panel
		buttonList[0] = new JRadioButton("Superheroes", true);
		buttonList[1] = new JRadioButton("Villains");
		sideGroup.add(buttonList[0]);
		sideGroup.add(buttonList[1]);
		panel.add(buttonList[0]);
		panel.add(buttonList[1]);

		// Show a dialog with the panel attached
		int choice = JOptionPane.showConfirmDialog(this, panel, "Side Options",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION);

		// Update grade if OK is selected
		if (choice == JOptionPane.OK_OPTION)
		{
			if (buttonList[0].isSelected())
				playerSide = 1;
			else
				playerSide = -1;
		}
		// Otherwise returns to main menu
		else
		{
			inGame = false;
			showMainMenu = true;
			pageNo = 0;
			repaint();
		}
		return playerSide;
	}

	/**
	 * Checks to see if the user has clicked on side of a box
	 * @param x the x value of the mouse
	 * @param y the y value of the mouse
	 * @return 1 if the top side of the box was clicked 2 if the bottom side of
	 *         the box was clicked 3 if the left side of the box was clicked 4
	 *         if the right side of the box was clicked -1 if no box was clicked
	 * 
	 */
	public int checkIsOnEdge(int x, int y)
	{
		if (x >= 10 && y >= 40 && x <= 635 && y <= 660)
		{
			if (x < 610 && y < 640)
			{
				if ((x - 10) % POINT_SPACING >= 0
						&& (x - 10) % POINT_SPACING <= 20
						&& (y - TOP_SPACING) % POINT_SPACING >= 10
						&& (y - TOP_SPACING) % POINT_SPACING <= 75)
					return 3;
				if ((x - LEFT_SPACING) % POINT_SPACING >= 10
						&& (x - LEFT_SPACING) % POINT_SPACING <= 75
						&& (y - 40) % POINT_SPACING >= 0
						&& (y - 40) % POINT_SPACING <= 20)
					return 1;
			}
			else if ((x >= 610 || y >= 640))
			{
				if ((x - 10) % POINT_SPACING >= 0
						&& (x - 10) % POINT_SPACING <= 20
						&& (y - TOP_SPACING) % POINT_SPACING >= 10
						&& (y - TOP_SPACING) % POINT_SPACING <= 75)
					return 4;
				if ((x - LEFT_SPACING) % POINT_SPACING >= 10
						&& (x - LEFT_SPACING) % POINT_SPACING <= 75
						&& (y - 40) % POINT_SPACING >= 0
						&& (y - 40) % POINT_SPACING <= 20)
					return 2;
			}
		}
		return -1;
	}

	/**
	 * Saves the current game that is being played to a file
	 */
	public void saveGame()
	{
		if (noOfPlayers == 2)
			board.updateGrid(currentPlayer, 0);
		else
		{
			if (computer == SUPERHERO)
				board.updateGrid(currentPlayer, SUPERHERO);
			else
				board.updateGrid(currentPlayer, VILLAINS);
		}

		// Writes the board to a file
		board.writeToFile("gameBoard.dat");
	}

	/**
	 * Game over after the game is finished and one side wins over the other
	 */
	public void gameOver()
	{
		int score = 0;
		inGame = false;
		gameOver = true;
		// If the superhero wins the game
		if (superhero.getScore() > villain.getScore())
		{
			if (noOfPlayers == 1)
			{
				// If the player's side is superhero
				if (side == SUPERHERO)
				{
					JOptionPane.showMessageDialog(this,
							"You won! Superheroes have saved the world!");
					score = superhero.getScore();
				}
				// If the player's side is villain
				else
				{
					JOptionPane.showMessageDialog(this,
							"You lose! Superheroes saved the world!");
					score = -100;
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this,
						"Superheroes have saved the world!");
				score = superhero.getScore();
			}
		}
		// If the villain wins the game
		else if (superhero.getScore() < villain.getScore())
		{
			if (noOfPlayers == 1)
			{
				// If the player's side is villain
				if (side == VILLAINS)
				{
					JOptionPane.showMessageDialog(this,
							"You won! Villains have conquered the world!");
					score = villain.getScore();
				}
				// If the player's side is superhero
				else
				{
					JOptionPane
							.showMessageDialog(this,
									"You have failed to save the world. Villains have conquered the world!");
					score = -100;
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this,
						"Villains have conqured the world!");
				score = villain.getScore();
			}
		}
		// If there's a tie
		else
		{
			JOptionPane
					.showMessageDialog(
							this,
							"It was a tough battle. There was no real winner yet. Play again to see who will win.");
			score = -100;
		}
		if (score != -100)
		{
			int topScore = myStats.isTopScore(score);
			// If a high score is achieved
			if (topScore > -1)
			{
				String name = JOptionPane
						.showInputDialog("Please enter your name: ");
				myStats.addScore(new Player(name, score, side), topScore);
				myStats.writeToFile("playerData.dat");
			}
		}
		//Prompts the player for a new game
		if (JOptionPane.showConfirmDialog(SuperVillainsBoard.this,
				"Do you want to Play Again?", "Game Over",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			newGame();
		else
		{
			gameOver = false;
			showMainMenu = true;
			pageNo = 0;
			repaint();
		}
	}

	/**
	 * Draws the background, buttons, text and score of pages. Also draws the
	 * grid for the game.
	 * @param g the graphics context to paint
	 */
	public void paintComponent(Graphics g)
	{
		// Calls the original method
		super.paintComponent(g);
		// If is at main board
		if (showMainMenu)
		{
			// Sets up background image
			g.drawImage(new ImageIcon("images/main.jpg").getImage(), 0, 0, this);
			// Sets up the setting button
			if (mouseOverSetting)
				g.drawImage(new ImageIcon("images/setting2.png").getImage(),
						920, 20,
						this);
			// Sets up the quit button
			else if (mouseOverQuit)
				g.drawImage(new ImageIcon("images/quit2.png").getImage(), 860,
						20,
						this);
			// Sets up Load Game button
			else if (mouseOverLoadGame)// If mouse over the button
				g.drawImage(new ImageIcon("images/loadgame2.jpg").getImage(),
						385,
						320, this);
			// Sets up New Game button
			else if (mouseOverNewGame)// if mouse over the button
				g.drawImage(new ImageIcon("images/newgame2.jpg").getImage(),
						385, 415,
						this);
			// Sets up ranking button
			else if (mouseOverHelpMain)// If mouse over the button
				g.drawImage(
						new ImageIcon("images/instructions2.jpg").getImage(),
						385,
						510, this);
		}
		else if (pageNo == 2)
		{
			// Sets up background image
			g.drawImage(new ImageIcon("images/gamebg.jpg").getImage(), 0, 0,
					this);
			// Sets up the setting button
			if (mouseOverSetting)
				g.drawImage(new ImageIcon("images/setting2.png").getImage(),
						920, 20,
						this);
			// Sets up the quit button
			else if (mouseOverQuit)
				g.drawImage(new ImageIcon("images/quit2.png").getImage(), 860,
						20,
						this);
			// Sets up Load Game button
			else if (mouseOverRestart)// If mouse over the button
				g.drawImage(new ImageIcon("images/restart2.jpg").getImage(),
						660, 300,
						this);
			else if (mouseOverHelp)// If mouse over the button
				g.drawImage(
						new ImageIcon("images/instructions2.jpg").getImage(),
						660,
						395, this);
			else if (mouseOverMainMenu)// If mouse over the button
				g.drawImage(new ImageIcon("images/mainmenu2.jpg").getImage(),
						660,
						490, this);
			else if (mouseOverSaveGame)// If mouse over the button
				g.drawImage(new ImageIcon("images/savegame2.jpg").getImage(),
						660,
						585, this);
			board.draw(g);
			// Highlight the line that the computer has drawn
			if (noOfPlayers == 1 && firstMove == true)
			{
				if (computer == SUPERHERO)
					board.drawComputer(g, superhero, isNewGame);
				else
					board.drawComputer(g, villain, isNewGame);
			}

			// Draw the arrows next to the current player
			if (currentPlayer == 1)
				g.drawImage(new ImageIcon("images/arrow.png").getImage(), 910,
						95,
						this);
			else
				g.drawImage(new ImageIcon("images/arrow.png").getImage(), 910,
						195,
						this);

			// Draw the scores of each player
			g.setFont(new Font("font", Font.BOLD, 50));
			g.drawString(Integer.toString(superhero.getScore()), 680, 170);
			g.drawString(Integer.toString(villain.getScore()), 680, 270);
		}
		// the first page of the instructions
		if (pageNo == 3)
		{
			if (inGame)
			{
				// Draw background image and button
				g.drawImage(new ImageIcon("images/instructions1game.png")
						.getImage(), 0, 0, this);
				if (mouseOverBackGame)
					g.drawImage(
							new ImageIcon("images/backtogame2.png").getImage(),
							10, 623, this);
			}
			else
			{
				// Draw background image and button
				g.drawImage(
						new ImageIcon("images/instructions1.png").getImage(),
						0, 0, this);
				if (mouseOverBackMain)
					g.drawImage(
							new ImageIcon("images/backtomain2.png").getImage(),
							10, 623, this);
			}
			if (mouseOverNext)
				g.drawImage(new ImageIcon("images/next2.png").getImage(), 760,
						623, this);
		}
		// Draws the other pages of the instructions that are in between the
		// beginning and the end
		else if (pageNo >= 4 && pageNo <= 6)
		{
			// Draw the background
			if (pageNo == 4)
				g.drawImage(
						new ImageIcon("images/instructions2.png").getImage(),
						0, 0, this);
			else if (pageNo == 5)
				g.drawImage(
						new ImageIcon("images/instructions3.png").getImage(),
						0, 0, this);
			else
				g.drawImage(
						new ImageIcon("images/instructions4.png").getImage(),
						0, 0, this);
			// Draw the buttons
			if (mouseOverNext)
				g.drawImage(new ImageIcon("images/next2.png").getImage(), 760,
						623, this);
			else if (mouseOverBack)
				g.drawImage(new ImageIcon("images/back2.png").getImage(), 10,
						623, this);
		}
		// Draws last page of the instructions
		else if (pageNo == 7)
		{
			if (inGame)
			{
				// Draw background image and button
				g.drawImage(new ImageIcon("images/instructions5game.png")
						.getImage(), 0, 0, this);
				if (mouseOverBackGame)
					g.drawImage(
							new ImageIcon("images/backtogame2.png").getImage(),
							760, 623, this);
			}
			else
			// Draw background image and button
			{
				g.drawImage(
						new ImageIcon("images/instructions5.png").getImage(),
						0, 0, this);
				if (mouseOverBackMain)
					g.drawImage(
							new ImageIcon("images/backtomain2.png").getImage(),
							760, 623, this);
			}
			if (mouseOverBack)
				g.drawImage(new ImageIcon("images/back2.png").getImage(), 10,
						623, this);
		}
		if (showSetting)
		{
			// Draw background
			g.drawImage(new ImageIcon("images/settingsbg.png").getImage(), 100,
					100,
					this);
			// Draw the music setting
			if (mouseOverMusic)
				g.drawImage(new ImageIcon("images/musicON.png").getImage(),
						322, 178,
						this);
			else if (musicOff)
				g.drawImage(new ImageIcon("images/musicOFF.png").getImage(),
						322, 178,
						this);
			// Draw other buttons
			if (mouseOverQuitSetting)
				g.drawImage(new ImageIcon("images/exit2.png").getImage(), 819,
						128, this);
			else if (viewHighScores)
				g.drawImage(
						new ImageIcon("images/viewhighscore.png").getImage(),
						138,
						294, this);
		}
		else if (showHighScores)
		{
			// Draw background
			g.drawImage(new ImageIcon("images/highscoresbg.png").getImage(),
					100, 100,
					this);
			// Draw the high scores
			g.setFont(new Font("font", Font.BOLD, 30));
			g.drawString("Name                  Score                   Date",
					180, 250);
			for (int score = 0; score < myStats.lengthOfArray(); score++)
			{
				g.drawString((myStats.topPlayer(score)).toString(), 180,
						300 + score * 30);
			}
			// Draw the
			if (mouseOverQuitSetting)
				g.drawImage(new ImageIcon("images/exit2.png").getImage(), 819,
						128, this);
		}

	}

	public void mouseClicked(MouseEvent event)
	{
	}

	public void mouseEntered(MouseEvent event)
	{
	}

	public void mouseExited(MouseEvent event)
	{
	}

	/**
	 * Handles a mousePress when pressing on a button or clicking sides during
	 * the game
	 * @param event the event information
	 */
	public void mousePressed(MouseEvent event)
	{
		// If the game is over, disable any mouse presses
		if (gameOver)
		{
			if (JOptionPane.showConfirmDialog(SuperVillainsBoard.this,
					"Do you want to Play Again?", "Game Over",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				newGame();
			else
			{
				showMainMenu = true;
				pageNo = 0;
				repaint();
			}
			return;
		}
		// get the coordinate of the mouse click
		int x, y;
		x = event.getX();
		y = event.getY();
		// If settings was clicked
		if (showSetting)
		{
			// To turn the music on or off
			if (x >= 332 && x <= 521 && y >= 178 && y <= 272)
			{
				if (musicOff)
				{
					musicOff = false;
					playMusic();
				}
				else
				{
					musicOff = true;
					backgroundMusic.stop();
				}
			}
			// To exit the settings page
			else if (x >= 819 && x <= 869 && y >= 128 && y <= 178)
				showSetting = false;
			// To access the high scores page
			else if (x >= 138 && x <= 494 && y >= 294 && y <= 344)
			{
				showHighScores = true;
				showSetting = false;
			}
			repaint();
			return;
		}
		// If high scores was clicked
		if (showHighScores)
		{
			// To exit high scores
			if (x >= 819 && x <= 869 && y >= 128 && y <= 178)
			{
				showSetting = true;
				showHighScores = false;
			}
		}
		if (showMainMenu || pageNo == 2)
		{
			if (y >= 20 && y <= 60)
			{
				// If the power off button was clicked
				if (x >= 860 && x <= 900)
				{
					if (JOptionPane.showConfirmDialog(SuperVillainsBoard.this,
							"Do you want to Save the Game?", "Quit the Game",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
						saveGame();
					System.exit(0);
				}
				// If the settings button was clicked
				else if (x >= 920 && x <= 960)
					showSetting = true;
			}
			if (showMainMenu)
			{
				if (x >= 385 && x <= 615)
				{
					// If load game was clicked
					if (y >= 320 && y <= 385)
					{
						showMainMenu = false;
						loadGame();
					}
					// If new games was clicked
					else if (y >= 415 && y <= 480)
					{
						showMainMenu = false;
						newGame();
					}
					// If instructions was clicked
					else if (y >= 510 && y <= 575)
					{
						showMainMenu = false;
						pageNo = 3;
					}
				}
			}
			// If in game
			else
			{
				if (x >= 660 && x <= 890)
				{
					if (y >= 300 && y <= 365)
					{
						// If restart was clicked
						if (JOptionPane.showConfirmDialog(
								SuperVillainsBoard.this,
								"Are you sure you want to restart?", "Warning",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
						{
							restart = true;
							repaint();
							newGame();
						}
					}
					// If instructions was clicked
					else if (y >= 395 && y <= 460)
						pageNo = 3;
					// If main menu was clicked
					else if (y >= 490 && y <= 555)
						showMainMenu = true;
					// If save game was clicked
					else if (y >= 585 && y <= 650)
					{
						if (JOptionPane.showConfirmDialog(
								SuperVillainsBoard.this,
								"Do you want to Save the Game?",
								"Quit the Game",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							saveGame();
					}
				}
				// If the player has made a move during their turn
				if (currentPlayer == side || noOfPlayers == 2)
				{
					makeMove(x, y);
				}
			}
		}
		// First page of instructions
		else if (pageNo == 3)
		{
			// If back was clicked
			if (inGame)
			{
				if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
					pageNo = 2;
				showMainMenu = false;
			}
			else
			{
				if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
				{
					showMainMenu = true;
					pageNo = 0;
				}
			}
			// If next was clicked
			if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
				pageNo++;
		}
		// Rest of the instructions page
		else if (pageNo >= 4 && pageNo <= 6)
		{
			// Goes back 1 page if back is clicked
			if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
				pageNo--;
			// Goes forward 1 page if next is clicked
			else if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
				pageNo++;
		}
		// The last page of instructions
		else if (pageNo == 7)
		{
			// If next was clicked
			if (inGame)
			{
				if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
				{
					pageNo = 2;
				}
			}
			else
			{
				if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
				{
					showMainMenu = true;
					pageNo = 0;
				}
			}
			// If back was clicked
			if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
				pageNo--;
		}
		else if (showMainMenu == true && x >= 800 && x <= 950 && y >= 520
				&& y <= 670)
		{
			pageNo = 0;
			newGame();
		}
		// if grid is full, game over
		repaint();
	}

	/**
	 * Makes the computer player make a move, updates the board, checks to see whether game is over
	 * or not. Also adds score to the appropriate player when a box is filled.
	 */
	public void computerMove()
	{
		isNewGame = false;
		firstMove = true;
		int[] moves = new int[3];
		int sideChosen = 0;
		// ArrayList<String> sidesCopy = new ArrayList<String> (possibleSides);
		// Make move according to what the computer is
		if (computer == SUPERHERO)
			moves = superhero.makeMove(board, board.possibleMoves());
		else
			moves = villain.makeMove(board, board.possibleMoves());
		// Update the board with the move that the computer has chosen to make
		sideChosen = board.updateBoxComputer(moves[0], moves[1], moves[2],
				currentPlayer);
		// If a box was formed
		if (sideChosen >= 0)
		{
			// Update last moved
			if (computer == SUPERHERO)
				superhero.lastMove(moves[0], moves[1], moves[2]);
			else
				villain.lastMove(moves[0], moves[1], moves[2]);
			// If box was clicked
			if (sideChosen >= 1)
			{
				if (currentPlayer == SUPERHERO)
					superhero.addScore(sideChosen);
				else
					villain.addScore(sideChosen);
				// Add up the score
				computerMove(); // Player gets to go again
			}
			// Otherwise other player's turn
			else
				currentPlayer *= -1;
		}
		numFilled = superhero.getScore() + villain.getScore();
		// Check to see if game is over
		if (numFilled == 64)
		{
			repaint();
			gameOver();
		}
	}

	/**
	 * Updates the board of the move that the player has decide to make. Checks to 
	 * see whether game is over or not. Also adds score to the appropriate player
	 * when a box is filled.
	 * @param x the x coordinate of the click
	 * @param y the y coordinate of the click
	 */
	public void makeMove(int x, int y)
	{
		int edge = checkIsOnEdge(x, y);
		int sideClicked = 0;
		if (edge >= 1)
		{
			// Check to see if move is valid and update the board if it is
			if (noOfPlayers == 2)
				sideClicked = board.updateBox(x, y, edge, currentPlayer);
			else
				sideClicked = board.updateBoxOnePlayer(x, y, edge,
						currentPlayer);
			// If a box was formed
			if (sideClicked >= 1)
			{
				// Add to score
				if (currentPlayer == VILLAINS)
					villain.addScore(sideClicked);
				if (currentPlayer == SUPERHERO)
					superhero.addScore(sideClicked);
			}
			// Check to see if game is over
			numFilled = superhero.getScore() + villain.getScore();
			if (numFilled == 64)
			{
				repaint();
				gameOver();
			}
			// Otherwise other player's turn
			if (sideClicked == 0)
			{
				currentPlayer *= -1;
				// If one player, it is computer's turn
				if (noOfPlayers == 1)
					computerMove();
			}
		}
	}

	public void mouseReleased(MouseEvent event)
	{
	}


	private class MouseMotionHandler extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent event)
		{
			// get the coordinate of the mouse click
			int x, y;
			x = event.getX();
			y = event.getY();
			if (showSetting)
			{
				mouseOverMusic = false;
				mouseOverQuitSetting = false;
				viewHighScores = false;
				if (x >= 330 && x <= 373 && y >= 140 && y <= 190)
					mouseOverMusic = true;
				else if (x >= 820 && x <= 870 && y >= 120 && y <= 170)
					mouseOverQuitSetting = true;
				else if (x >= 138 && x <= 494 && y >= 294 && y <= 344)
					viewHighScores = true;
				repaint();
				return;
			}
			if (showHighScores)
			{
				mouseOverQuitSetting = false;
				if (x >= 820 && x <= 870 && y >= 120 && y <= 170)
					mouseOverQuitSetting = true;
				repaint();
				return;
			}
			if (showMainMenu || pageNo == 2)
			{
				mouseOverQuit = false;
				mouseOverSetting = false;
				if (y >= 20 && y <= 60)
				{
					if (x >= 860 && x <= 900)
						mouseOverQuit = true;
					else if (x >= 920 && x <= 960)
						mouseOverSetting = true;
				}
				if (showMainMenu)
				{
					pageNo = 0;
					if (x >= 385 && x <= 615)
					{
						mouseOverLoadGame = false;
						mouseOverNewGame = false;
						mouseOverHelpMain = false;
						if (y >= 320 && y <= 385)
							mouseOverLoadGame = true;
						else if (y >= 415 && y <= 480)
							mouseOverNewGame = true;
						else if (y >= 510 && y <= 575)
							mouseOverHelpMain = true;
					}
				}
				else
				{
					if (x >= 660 && x <= 890)
					{
						mouseOverRestart = false;
						mouseOverHelp = false;
						mouseOverMainMenu = false;
						mouseOverSaveGame = false;
						if (y >= 300 && y <= 365)
							mouseOverRestart = true;
						else if (y >= 395 && y <= 460)
							mouseOverHelp = true;
						else if (y >= 490 && y <= 555)
							mouseOverMainMenu = true;
						else if (y >= 585 && y <= 650)
							mouseOverSaveGame = true;
					}
				}
			}
			else if (pageNo == 3)
			{
				mouseOverBackGame = false;
				mouseOverBackMain = false;
				mouseOverNext = false;
				if (inGame)
				{
					if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
						mouseOverBackGame = true;
				}
				else
				{
					if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
						mouseOverBackMain = true;
				}
				if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
					mouseOverNext = true;
			}
			else if (pageNo >= 4 && pageNo <= 6)
			{
				mouseOverBack = false;
				mouseOverNext = false;
				if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
					mouseOverBack = true;
				else if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
					mouseOverNext = true;
			}
			else if (pageNo == 7)
			{
				mouseOverBackGame = false;
				mouseOverBackMain = false;
				mouseOverBack = false;
				if (inGame)
				{
					if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
						mouseOverBackGame = true;
				}
				else
				{
					if (x >= 760 && x <= 990 && y >= 623 && y <= 688)
						mouseOverBackMain = true;
				}
				if (x >= 10 && x <= 240 && y >= 623 && y <= 688)
					mouseOverBack = true;
			}
			repaint();
		}
	}

	/**
	 * Plays the background music and loops it
	 */
	public void playMusic()
	{
		backgroundMusic.play();
		backgroundMusic.loop();
	}

	/**
	 * get the URL for the file
	 */
	public URL getCompleteURL(String fileName)
	{
		// finding the URL
		try
		{
			return new URL("file:" + System.getProperty("user.dir") + "/"
					+ fileName);
		}
		// catch exceptions
		catch (MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		return null;
	}
}
