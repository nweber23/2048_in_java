package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
	private Tile[][] grid;
	private int size;
	private int score;
	private Random random;

	public Board(int size) {
		this.size = size;
		this.grid = new Tile[size][size];
		this.random = new Random();
		this.score = 0;

		initializeBoard();
	}

	private void initializeBoard() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				grid[i][j] = new Tile();
			}
		}

		// Add two initial tiles
		addRandomTile();
		addRandomTile();
	}

	public void addRandomTile() {
		List<int[]> emptyCells = new ArrayList<>();

		// Find all empty cells
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j].isEmpty()) {
					emptyCells.add(new int[]{i, j});
				}
			}
		}

		if (!emptyCells.isEmpty()) {
			int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
			int value = (random.nextInt(100) < utils.Constants.SPAWN_2_PROBABILITY) ? 2 : 4;
			grid[cell[0]][cell[1]].setValue(value);
		}
	}

	public boolean moveLeft() {
		boolean moved = false;

		for (int i = 0; i < size; i++) {
			// Compact tiles to the left
			for (int j = 1; j < size; j++) {
				if (!grid[i][j].isEmpty()) {
					int k = j;
					while (k > 0 && grid[i][k-1].isEmpty()) {
						grid[i][k-1].setValue(grid[i][k].getValue());
						grid[i][k].clear();
						k--;
						moved = true;
					}
				}
			}

			// Merge tiles
			for (int j = 0; j < size - 1; j++) {
				if (!grid[i][j].isEmpty() && grid[i][j].getValue() == grid[i][j+1].getValue()) {
					grid[i][j].setValue(grid[i][j].getValue() * 2);
					score += grid[i][j].getValue();
					grid[i][j+1].clear();
					moved = true;

					// Compact again after merge
					for (int k = j+1; k < size - 1; k++) {
						grid[i][k].setValue(grid[i][k+1].getValue());
						grid[i][k+1].clear();
					}
				}
			}
		}

		return moved;
	}

	public boolean moveRight() {
		rotateBoard();
		rotateBoard();
		boolean moved = moveLeft();
		rotateBoard();
		rotateBoard();
		return moved;
	}

	public boolean moveUp() {
		rotateBoard();
		rotateBoard();
		rotateBoard();
		boolean moved = moveLeft();
		rotateBoard();
		return moved;
	}

	public boolean moveDown() {
		rotateBoard();
		boolean moved = moveLeft();
		rotateBoard();
		rotateBoard();
		rotateBoard();
		return moved;
	}

	private void rotateBoard() {
		Tile[][] rotated = new Tile[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				rotated[i][j] = grid[size - j - 1][i];
			}
		}

		grid = rotated;
	}

	public boolean isGameOver() {
		// Check if there are any empty cells
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j].isEmpty()) {
					return false;
				}
			}
		}

		// Check if there are any possible merges
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int value = grid[i][j].getValue();

				// Check right neighbor
				if (j < size - 1 && grid[i][j+1].getValue() == value) {
					return false;
				}

				// Check bottom neighbor
				if (i < size - 1 && grid[i+1][j].getValue() == value) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean hasWon() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j].getValue() == utils.Constants.WIN_VALUE) {
					return true;
				}
			}
		}
		return false;
	}

	public Tile[][] getGrid() {
		return grid;
	}

	public int getSize() {
		return size;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}