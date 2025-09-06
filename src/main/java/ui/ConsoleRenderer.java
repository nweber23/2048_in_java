package ui;

import game.Board;
import org.fusesource.jansi.AnsiConsole;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class ConsoleRenderer {
	private int boardSize;
	private boolean useColors;
	private boolean useAsciiArt;
	private Map<Integer, String[]> asciiArtDigits;
	private int asciiArtHeight = 0;
	private int asciiArtWidth = 0;

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

		Map<Integer, List<String>> raw = new HashMap<>();
		int maxHeight = 0;
		int maxWidth = 0;
		asciiArtWidth = 0;
		asciiArtHeight = 0;

		for (int value : values) {
			try (InputStream is = getClass().getResourceAsStream("/digits/" + value + ".txt")) {
				if (is != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					List<String> lines = new ArrayList<>();
					String line;
					while ((line = reader.readLine()) != null) {
						lines.add(line);
						if (line.length() > maxWidth) maxWidth = line.length();
					}
					if (lines.size() > maxHeight) maxHeight = lines.size();
					raw.put(value, lines);
				}
			} catch (IOException e) {
				System.err.println("Failed to load ASCII art for value: " + value);
			}
		}

		if (raw.isEmpty()) {
			asciiArtHeight = 0;
			return;
		}

		for (int value : values) {
			List<String> lines = raw.getOrDefault(value, raw.get(0));
			if (lines == null) {
				String[] blank = new String[maxHeight];
				for (int i = 0; i < maxHeight; i++) blank[i] = " ".repeat(maxWidth);
				asciiArtDigits.put(value, blank);
				continue;
			}

			String[] normalized = new String[maxHeight];
			for (int i = 0; i < maxHeight; i++) {
				String l = i < lines.size() ? lines.get(i) : "";
				if (l.length() < maxWidth) {
					l = l + " ".repeat(maxWidth - l.length());
				}
				normalized[i] = l;
			}
			asciiArtDigits.put(value, normalized);
		}

		asciiArtHeight = maxHeight;
		asciiArtWidth = maxWidth;
	}
	private void renderAsciiArtRow(Board board, int row) {
		if (asciiArtHeight <= 0) {
			renderNormalRow(board, row);
			return;
		}

		for (int line = 0; line < asciiArtHeight; line++) {
			for (int j = 0; j < boardSize; j++) {
				int value = board.getGrid()[row][j].getValue();
				String[] art = asciiArtDigits.getOrDefault(value, asciiArtDigits.get(0));
				String artLine = art[line];

				if (useColors) {
					Color color = getColorForValue(value);
					String cell = artLine;
					if (cell.length() < asciiArtWidth) {
						cell = cell + " ".repeat(asciiArtWidth - cell.length());
					}
					AnsiConsole.out.print(ansi().bg(color).fg(BLACK).a(cell + " ").reset());
				} else {
					AnsiConsole.out.print(artLine);
					if (j < boardSize - 1) AnsiConsole.out.print(" ");
				}
			}
			AnsiConsole.out.println();
		}
	}
	public void render(Board board) {
		AnsiConsole.out.print(ansi().eraseScreen().cursor(1, 1));
		AnsiConsole.out.println(ansi().bold().a("2048").reset());
		AnsiConsole.out.println("Score: " + board.getScore());
		AnsiConsole.out.println();

		for (int r = 0; r < board.getSize(); r++) {
			if (useAsciiArt && asciiArtHeight > 0) {
				renderAsciiArtRow(board, r);
			} else {
				renderNormalRow(board, r);
			}
		}

		AnsiConsole.out.println();
		AnsiConsole.out.flush();
	}

	private void renderNormalRow(Board board, int row) {
		int cellWidth = 6;
		for (int j = 0; j < board.getSize(); j++) {
			int value = board.getGrid()[row][j].getValue();
			String text = value == 0 ? "." : String.valueOf(value);
			String padded = String.format("%" + cellWidth + "s", text);

			if (useColors) {
				Color bg = getColorForValue(value);
				AnsiConsole.out.print(ansi().bg(bg).fg(BLACK).a(" " + padded + " ").reset());
			} else {
				AnsiConsole.out.print("[" + padded + "]");
			}

			if (j < board.getSize() - 1) {
				AnsiConsole.out.print(" ");
			}
		}
		AnsiConsole.out.println();
	}

	private Color getColorForValue(int value) {
		switch (value) {
			case 2:    return WHITE;
			case 4:    return CYAN;
			case 8:    return GREEN;
			case 16:   return YELLOW;
			case 32:   return BLUE;
			case 64:   return MAGENTA;
			case 128:  return RED;
			case 256:  return BLACK;
			case 512:  return BLUE;
			case 1024: return MAGENTA;
			case 2048: return YELLOW;
			default:   return BLACK;
		}
	}

	public void showWinMessage() {
		AnsiConsole.out.println();
		AnsiConsole.out.println(ansi().fg(GREEN).bold().a("You reached " + utils.Constants.WIN_VALUE + "!").reset());
		AnsiConsole.out.flush();
	}

	public void showGameOverMessage() {
		AnsiConsole.out.println();
		AnsiConsole.out.println(ansi().fg(RED).bold().a("Game Over").reset());
		AnsiConsole.out.flush();
	}

	public void handleResize(Board board) {
		AnsiConsole.out.print(ansi().eraseScreen().cursor(1, 1));
		AnsiConsole.out.flush();
		render(board);
	}
}
