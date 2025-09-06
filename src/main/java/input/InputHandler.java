package input;

import java.io.IOException;

public class InputHandler {
	public enum Direction {
		UP, DOWN, LEFT, RIGHT, QUIT, RESIZE
	}

	public Direction getInput() {
		try {
			int key = System.in.read();

			switch (key) {
				case 27: // ESC
					int next = System.in.read();
					if (next == 91) {
						int arrow = System.in.read();
						switch (arrow) {
							case 65: return Direction.UP;
							case 66: return Direction.DOWN;
							case 67: return Direction.RIGHT;
							case 68: return Direction.LEFT;
						}
					} else if (next == -1) {
						return Direction.QUIT;
					}
					break;
				case 113: // Q
				case 81:  // q
					return Direction.QUIT;
				case 119: // W
				case 87:  // w
					return Direction.UP;
				case 115: // S
				case 83:  // s
					return Direction.DOWN;
				case 97:  // A
				case 65:  // a
					return Direction.LEFT;
				case 100: // D
				case 68:  // d
					return Direction.RIGHT;
				case 12:  // Ctrl+L (clear screen)
					return Direction.RESIZE;
			}
		} catch (IOException e) {
			// Ignore
		}

		return null;
	}
}
