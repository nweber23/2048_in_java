package utils;

import java.io.*;
import java.util.Arrays;

public class ScoreManager {
	private static final String SCORES_FILE = "scores.txt";
	private int[] highScores;

	public ScoreManager() {
		this.highScores = new int[5]; // Top 5 scores
		loadScores();
	}

	private void loadScores() {
		try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
			for (int i = 0; i < highScores.length; i++) {
				String line = reader.readLine();
				if (line != null) {
					highScores[i] = Integer.parseInt(line.trim());
				} else {
					highScores[i] = 0;
				}
			}
		} catch (IOException | NumberFormatException e) {
			// Initialize with zeros if file doesn't exist or is corrupted
			Arrays.fill(highScores, 0);
		}
	}

	public void saveScore(int score) {
		if (score > highScores[highScores.length - 1]) {
			// Add the new score and sort in descending order
			highScores[highScores.length - 1] = score;
			Arrays.sort(highScores);

			// Reverse to get descending order
			for (int i = 0; i < highScores.length / 2; i++) {
				int temp = highScores[i];
				highScores[i] = highScores[highScores.length - 1 - i];
				highScores[highScores.length - 1 - i] = temp;
			}

			// Save to file
			try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE))) {
				for (int highScore : highScores) {
					writer.println(highScore);
				}
			} catch (IOException e) {
				System.err.println("Failed to save scores: " + e.getMessage());
			}
		}
	}

	public int[] getHighScores() {
		return highScores;
	}
}
