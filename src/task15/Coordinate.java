package task15;

public class Coordinate implements Cloneable {
	public int	x;
	public int	y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public Coordinate clone() {
		return new Coordinate(x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.x;
		result = prime * result + this.y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + "|" + y + ")";
	}

	public int manhattanDistanceTo(Coordinate target) {
		return Math.abs(x - target.x) + Math.abs(y - target.y);
	}

	public boolean isAdjacentOf(Coordinate key) {
		return this.manhattanDistanceTo(key) == 1;
	}
}
