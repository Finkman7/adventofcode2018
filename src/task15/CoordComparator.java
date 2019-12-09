package task15;

import java.util.Comparator;

public class CoordComparator implements Comparator<Coordinate> {
	public static final CoordComparator	instance	= new CoordComparator();
	private Comparator<Coordinate>		byX			= Comparator.comparingInt(c -> c.x);
	private Comparator<Coordinate>		byY			= Comparator.comparingInt(c -> c.y);

	@Override
	public int compare(Coordinate o1, Coordinate o2) {
		return byY.thenComparing(byX).compare(o1, o2);
	}

	private CoordComparator() {

	}
}