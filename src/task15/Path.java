package task15;

import java.util.ArrayList;

public class Path extends ArrayList<Coordinate> {

	public Path(Path best) {
		super(best);
	}

	public Path() {

	}

	public Coordinate getTail() {
		return this.get(this.size() - 1);
	}
}
