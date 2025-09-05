package game;

public class Tile {
	private int value;

	public Tile() {
		this.value = 0;
	}

	public Tile(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isEmpty() {
		return value == 0;
	}

	public void clear() {
		value = 0;
	}

	public boolean merge(Tile other) {
		if (this.value == other.getValue() && this.value != 0) {
			this.value *= 2;
			other.clear();
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}