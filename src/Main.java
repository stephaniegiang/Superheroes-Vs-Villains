import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

/**
 * Main program of the Superheroes vs Villains game. Sets the game screen. 
 * @author Stephanie Giang, Sherilyn Hua, Zoe Zou
 * @version June 11, 2015
 */
public class Main extends JFrame implements ActionListener
{
	private SuperVillainsBoard gameBoard;

	// private Board gameBoard;

	public Main()
	{
		super("Superheroes VS Villains");// Sets the title to our game name
		setResizable(false);// Makes the window not resizable

		setIconImage(Toolkit.getDefaultToolkit().getImage("images/icon.jpg"));// Sets the icon
																// image
		gameBoard = new SuperVillainsBoard();// Constructs a new game board

		// Sets the location of the ContentPane
		setLayout(new BorderLayout());
		add(gameBoard, BorderLayout.CENTER);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - SuperVillainsBoard.WIDTH) / 2,
				(screen.height - SuperVillainsBoard.HEIGHT) / 2 - 52);
	}

	public void actionPerformed(ActionEvent event)
	{

	}

	/**
	 * The main method that starts the game up
	 */
	public static void main(String[] args)
	{
		Main game = new Main();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.pack();
		game.setVisible(true);// Sets the JFrame to visible

	}
}