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
		AnsiConsole.systemInstall();

		try {
			TerminalUtils.enableRawMode();
		} catch (Throwable t) {
		}

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				TerminalUtils.disableRawMode();
			} catch (Throwable t) {
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
			AnsiConsole.out.print(Ansi.ansi().eraseScreen().cursor(1, 1));
			AnsiConsole.out.println("\r" + Ansi.ansi().bold().fg(Ansi.Color.CYAN).a("2048").reset());
			AnsiConsole.out.println("\r");

			for (int i = 0; i < options.length; i++) {
				if (i == selectedOption) {
					AnsiConsole.out.println("\r" + Ansi.ansi().fg(Ansi.Color.GREEN).a("> " + options[i]).reset());
				} else {
					AnsiConsole.out.println("\r" + "  " + options[i]);
				}
			}

			if (selectedOption == 2) {
				AnsiConsole.out.println("\r");
				AnsiConsole.out.println("\r" + "High Scores:");
				int[] scores = scoreManager.getHighScores();
				for (int i = 0; i < scores.length; i++) {
					AnsiConsole.out.println("\r" + (i + 1) + ". " + scores[i]);
				}
			}

			AnsiConsole.out.flush();

			try {
				int key = System.in.read();
				if (key == -1) {
					return MenuResult.QUIT;
				}

				if (key == 27) {
					int next = System.in.read();
					if (next == 91) {
						int arrow = System.in.read();
						switch (arrow) {
							case 65:
								selectedOption = (selectedOption - 1 + options.length) % options.length;
								break;
							case 66:
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
						case 10:
						case 13:
							if (selectedOption == 0) return MenuResult.START;
							if (selectedOption == 1) return MenuResult.OPTIONS;
							if (selectedOption == 3) return MenuResult.QUIT;
							break;
						case 119:
						case 87:
							selectedOption = (selectedOption - 1 + options.length) % options.length;
							break;
						case 115:
						case 83:
							selectedOption = (selectedOption + 1) % options.length;
							break;
						case 113:
						case 81:
							return MenuResult.QUIT;
						default:
							break;
					}
				}
			} catch (IOException e) {
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return MenuResult.QUIT;
			}
		}
	}
}
