package ui;

import utils.ScoreManager;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class Menu {
	private ScoreManager scoreManager;

	public enum MenuResult {
		START, OPTIONS, QUIT
	}

	public Menu() {
		this.scoreManager = new ScoreManager();
	}

	public MenuResult show() {
		int selectedOption = 0;
		String[] options = {"Start Game", "Options", "High Scores", "Quit"};

		while (true) {
			AnsiConsole.out.print(ansi().eraseScreen().cursor(1, 1));

			// Display title
			AnsiConsole.out.println(ansi().bold().fg(CYAN).a("2048").reset());
			AnsiConsole.out.println();

			// Display menu options
			for (int i = 0; i < options.length; i++) {
				if (i == selectedOption) {
					AnsiConsole.out.println(ansi().fg(GREEN).a("> " + options[i]).reset());
				} else {
					AnsiConsole.out.println("  " + options[i]);
				}
			}

			// Display high scores if selected
			if (selectedOption == 2) {
				AnsiConsole.out.println();
				AnsiConsole.out.println("High Scores:");
				int[] scores = scoreManager.getHighScores();
				for (int i = 0; i < scores.length; i++) {
					AnsiConsole.out.println((i + 1) + ". " + scores[i]);
				}
			}

			// Get input
			try {
				int key = System.in.read();

				switch (key) {
					case 27: // ESC
						return MenuResult.QUIT;
					case 10: // Enter
					case 13: // Return
						if (selectedOption == 0) return MenuResult.START;
						if (selectedOption == 1) return MenuResult.OPTIONS;
						if (selectedOption == 3) return MenuResult.QUIT;
						break;
					case 65: // Up arrow
					case 119: // W
						selectedOption = (selectedOption - 1 + options.length) % options.length;
						break;
					case 66: // Down arrow
					case 115: // S
						selectedOption = (selectedOption + 1) % options.length;
						break;
				}
			} catch (Exception e) {
				// Ignore
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
