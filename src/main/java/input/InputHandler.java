// ...existing code...
package input;

import java.io.IOException;

public class InputHandler {
	public enum Direction {
		UP, DOWN, LEFT, RIGHT, QUIT, RESIZE
	}

	public InputHandler() {
		// enable raw mode so arrow keys and single-byte inputs are delivered immediately
		TerminalUtils.enableRawMode();

		// ensure terminal is restored on JVM exit
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			TerminalUtils.disableRawMode();
		}));
	}

	/**
	 * Drain any immediately-available bytes from System.in to avoid stray
	 * characters being echoed when escape sequences are only partially read.
	 */
	private void drainAvailable() {
		try {
			while (System.in.available() > 0) {
				System.in.read();
			}
		} catch (IOException ignored) {
		}
	}

	public Direction getInput() {
		try {
			int key = System.in.read();

			if (key == -1) return Direction.QUIT;

			switch (key) {
				case 27: // ESC
					// try to read the rest of the escape sequence
					int next = System.in.read();
					if (next == -1) return Direction.QUIT;

					if (next == 91) { // '['
						int arrow = System.in.read();
						if (arrow == -1) return null;
						switch (arrow) {
							case 65: return Direction.UP;
							case 66: return Direction.DOWN;
							case 67: return Direction.RIGHT;
							case 68: return Direction.LEFT;
							default:
								// unknown sequence - drain any remaining bytes
								drainAvailable();
								return null;
						}
					} else {
						// not an arrow sequence; drain and ignore
						drainAvailable();
						return null;
					}
				case 113: // 'q' / 'Q'
				case 81:
					return Direction.QUIT;
				case 119: // 'w' / 'W'
				case 87:
					return Direction.UP;
				case 115: // 's' / 'S'
				case 83:
					return Direction.DOWN;
				case 97:  // 'a' / 'A'
				case 65:
					return Direction.LEFT;
				case 100: // 'd' / 'D'
				case 68:
					return Direction.RIGHT;
				case 12:  // Ctrl+L (clear screen)
					return Direction.RESIZE;
				default:
					// ignore other bytes
					return null;
			}
		} catch (IOException e) {
			// Ignore and continue
		}

		return null;
	}
}
