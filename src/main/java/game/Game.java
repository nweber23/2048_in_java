package game;

import input.InputHandler;
import ui.ConsoleRenderer;
import ui.Menu;
import utils.ScoreManager;

public class Game {
	private Board board;
	private ConsoleRenderer renderer;
	private InputHandler inputHandler;
	private ScoreManager scoreManager;
	private boolean running;
	private boolean useColors;
	private boolean useAsciiArt;

	public Game(int size, boolean useColors, boolean useAsciiArt) {
		this.board = new Board(size);
		this.renderer = new ConsoleRenderer(size, useColors, useAsciiArt);
		this.inputHandler = new InputHandler();
		this.scoreManager = new ScoreManager();
		this.running = true;
		this.useColors = useColors;
		this.useAsciiArt = useAsciiArt;
	}

	public void run() {
		boolean won = false;

		while (running) {
			renderer.render(board);

			if (board.hasWon() && !won) {
				renderer.showWinMessage();
				won = true;
			}

			if (board.isGameOver()) {
				renderer.showGameOverMessage();
				scoreManager.saveScore(board.getScore());
				break;
			}

			InputHandler.Direction direction = inputHandler.getInput();

			if (direction == null) {
				continue;
			}

			boolean moved = false;

			switch (direction) {
				case UP:
					moved = board.moveUp();
					break;
				case DOWN:
					moved = board.moveDown();
					break;
				case LEFT:
					moved = board.moveLeft();
					break;
				case RIGHT:
					moved = board.moveRight();
					break;
				case QUIT:
					running = false;
					break;
				case RESIZE:
					renderer.handleResize();
					break;
			}

			if (moved) {
				board.addRandomTile();
			}
		}
	}

	public static void main(String[] args) {
		int size = utils.Constants.DEFAULT_SIZE;
		boolean useColors = true;
		boolean useAsciiArt = false;

		// Parse command line arguments
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-s") && i + 1 < args.length) {
				try {
					size = Integer.parseInt(args[i + 1]);
					if (size != 4 && size != 5) {
						System.out.println("Invalid size. Using default size 4.");
						size = utils.Constants.DEFAULT_SIZE;
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid size. Using default size 4.");
				}
			}
		}

		Menu menu = new Menu();
		Menu.MenuResult result = menu.show();

		if (result == Menu.MenuResult.QUIT) {
			System.exit(0);
		}

		if (result == Menu.MenuResult.OPTIONS) {
			useColors = !useColors;
			useAsciiArt = !useAsciiArt;
		}

		Game game = new Game(size, useColors, useAsciiArt);
		game.run();
	}
}
