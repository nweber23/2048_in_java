package ui;

import java.io.IOException;

import utils.ScoreManager;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi;
import input.TerminalUtils;

public class Menu {
	private final ScoreManager scoreManager;

	public Menu() {
		this.scoreManager = new ScoreManager();
		// Ensure AnsiConsole is installed before any menu output to avoid NPE
		AnsiConsole.systemInstall();

		// Enable raw terminal mode so arrow escape sequences are delivered immediately.
		// TerminalUtils is a no-op on non-Unix platforms.
		try {
			TerminalUtils.enableRawMode();
		} catch (Throwable t) {
			// ignore failures; menu will fall back to normal input
		}

		// Restore terminal when JVM exits
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				TerminalUtils.disableRawMode();
			} catch (Throwable t) {
				// ignore
			}
		}));
	}

	public enum MenuResult {
		START,
		OPTIONS,
		QUIT
	}

	public MenuResult show() {
		int selectedOption = 0;
		String[] options = {"Start Game", "Options", "High Scores", "Quit"};

		while (true) {
			// Clear screen and render title + options
			AnsiConsole.out.print(Ansi.ansi().eraseScreen().cursor(1, 1));
			AnsiConsole.out.println(Ansi.ansi().bold().fg(Ansi.Color.CYAN).a("2048").reset());
			AnsiConsole.out.println();

			for (int i = 0; i < options.length; i++) {
				if (i == selectedOption) {
					AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("> " + options[i]).reset());
				} else {
					AnsiConsole.out.println("  " + options[i]);
				}
			}

			// Show high scores when that option is selected
			if (selectedOption == 2) {
				AnsiConsole.out.println();
				AnsiConsole.out.println("High Scores:");
				int[] scores = scoreManager.getHighScores();
				for (int i = 0; i < scores.length; i++) {
					AnsiConsole.out.println((i + 1) + ". " + scores[i]);
				}
			}

			AnsiConsole.out.flush();

			// Read input and handle escape sequences for arrows
			try {
				int key = System.in.read();
				if (key == -1) {
					return MenuResult.QUIT;
				}

				if (key == 27) { // ESC - possible escape sequence
					// try to consume '[' and the final code
					int next = System.in.read();
					if (next == 91) { // '['
						int arrow = System.in.read();
						switch (arrow) {
							case 65: // Up
								selectedOption = (selectedOption - 1 + options.length) % options.length;
								break;
							case 66: // Down
								selectedOption = (selectedOption + 1) % options.length;
								break;
							default:
								break;
						}
					} else if (next == -1) {
						return MenuResult.QUIT;
					}
				} else {
					switch (key) {
						case 10: // LF (Enter)
						case 13: // CR (Enter)
							if (selectedOption == 0) return MenuResult.START;
							if (selectedOption == 1) return MenuResult.OPTIONS;
							if (selectedOption == 3) return MenuResult.QUIT;
							break;
						case 119: // 'w'
						case 87:  // 'W'
							selectedOption = (selectedOption - 1 + options.length) % options.length;
							break;
						case 115: // 's'
						case 83:  // 'S'
							selectedOption = (selectedOption + 1) % options.length;
							break;
						case 113: // 'q'
						case 81:  // 'Q'
							return MenuResult.QUIT;
						default:
							break;
					}
				}
			} catch (IOException e) {
				// ignore and continue looping
			}

			// small delay to avoid busy loop
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return MenuResult.QUIT;
			}
		}
	}
}