package ui;

import game.Board;
import org.fusesource.jansi.AnsiConsole;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class ConsoleRenderer {
	private int boardSize;
	private boolean useColors;
	private boolean useAsciiArt;
	private Map<Integer, String[]> asciiArtDigits;

	public ConsoleRenderer(int boardSize, boolean useColors, boolean useAsciiArt) {
		this.boardSize = boardSize;
		this.useColors = useColors;
		this.useAsciiArt = useAsciiArt;
		this.asciiArtDigits = new HashMap<>();

		if (useAsciiArt) {
			loadAsciiArtDigits();
		}

		AnsiConsole.systemInstall();
	}

	private void loadAsciiArtDigits() {
		int[] values = {0, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048};

		for (int value : values) {
			try {
				InputStream is = getClass().getResourceAsStream("/digits/" + value + ".txt");
				if (is != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String[] lines = new String[5];
					for (int i = 0; i < 5; i++) {
						lines[i] = reader.readLine();
					}
					asciiArtDigits.put(value, lines);
					reader.close();
				}
			} catch (IOException e) {
				System.err.println("Failed to load ASCII art for value: " + value);
			}
		}
	}

	public void render(Board board) {
		AnsiConsole.out.print(ansi().eraseScreen().cursor(1, 1));

		// Display score
		AnsiConsole.out.println(ansi().bold().a("Score: " + board.getScore()).reset());
		AnsiConsole.out.println();

		// Display board
		for (int i = 0; i < boardSize; i++) {
			if (useAsciiArt) {
				renderAsciiArtRow(board, i);
			} else {
				renderNormalRow(board, i);
			}
			AnsiConsole.out.println();
		}

		// Display controls
		AnsiConsole.out.println();
		AnsiConsole.out.println("Controls: Arrow keys to move, ESC or Q to quit");
	}

	private void renderNormalRow(Board board, int row) {
		for (int j = 0; j < boardSize; j++) {
			int value = board.getGrid()[row][j].getValue();
			String cellText = value == 0 ? "    " : String.format("%4d", value);

			if (useColors) {
				Color color = getColorForValue(value);
				AnsiConsole.out.print(ansi().bg(color).fg(BLACK).bold().a(cellText).reset());
			} else {
				AnsiConsole.out.print(cellText);
			}

			if (j < boardSize - 1) {
				AnsiConsole.out.print(" ");
			}
		}
	}

	private void renderAsciiArtRow(Board board, int row) {
		// ASCII art is 5 lines high for each row
		for (int line = 0; line < 5; line++) {
			for (int j = 0; j < boardSize; j++) {
				int value = board.getGrid()[row][j].getValue();
				String[] art = asciiArtDigits.getOrDefault(value, asciiArtDigits.get(0));

				if (useColors) {
					Color color = getColorForValue(value);
					AnsiConsole.out.print(ansi().bg(color).fg(BLACK).a(art[line]).reset());
				} else {
					AnsiConsole.out.print(art[line]);
				}

				if (j < boardSize - 1) {
					AnsiConsole.out.print(" ");
				}
			}
			AnsiConsole.out.println();
		}
	}

	private Color getColorForValue(int value) {
		switch (value) {
			case 0: return WHITE;
			case 2: return YELLOW;
			case 4: return CYAN;
			case 8: return GREEN;
			case 16: return MAGENTA;
			case 32: return BLUE;
			case 64: return RED;
			case 128: return YELLOW;
			case 256: return CYAN;
			case 512: return GREEN;
			case 1024: return MAGENTA;
			case 2048: return RED;
			default: return WHITE;
		}
	}

	public void showWinMessage() {
		AnsiConsole.out.println(ansi().eraseScreen().cursor(1, 1));
		AnsiConsole.out.println(ansi().bold().fg(GREEN).a("Congratulations! You reached 2048!").reset());
		AnsiConsole.out.println("Press any key to continue playing...");
	}

	public void showGameOverMessage() {
		AnsiConsole.out.println(ansi().eraseScreen().cursor(1, 1));
		AnsiConsole.out.println(ansi().bold().fg(RED).a("Game Over!").reset());
		AnsiConsole.out.println("No more moves available.");
	}

	public void handleResize() {
		// Redraw the board when terminal is resized
		AnsiConsole.out.print(ansi().eraseScreen());
	}
}
