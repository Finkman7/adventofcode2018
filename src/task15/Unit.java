package task15;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class Unit extends Token {
	public int				ATK	= 3;
	public final UnitType	type;
	public int				hp	= 200;

	public Unit(UnitType type, int ATK) {
		this.type = type;
		this.ATK = ATK;
	}

	@Override
	public String toString() {
		return type.toString() + "(" + String.format("%3d", hp) + ")";
	}

	public Coordinate move(Board board, Coordinate c) throws Exception {
		List<Entry<Coordinate, Token>> targetOpponents = board.getOpponentEntriesOf(this, c);
		if (targetOpponents.isEmpty()) {
			throw new Exception();
		}
		Set<Coordinate> targetCoordinates = new TreeSet<>(CoordComparator.instance);
		for (Entry<Coordinate, Token> o : targetOpponents) {
			targetCoordinates.addAll(board.getFreeAdjacentCoordinatesOf(o.getKey()));
		}

		Map<Coordinate, Path> shortestPaths = new HashMap<>();
		for (Coordinate c_Target : targetCoordinates) {
			Path p = board.getShortestPath(c, c_Target);
			if (p != null) {
				shortestPaths.put(c_Target, p);
			}
		}

		if (!shortestPaths.isEmpty()) {
			Coordinate moveTarget = shortestPaths.entrySet().stream().sorted((e1, e2) -> {
				int result = Integer.compare(e1.getValue().size(), e2.getValue().size());

				if (result == 0) {
					result = CoordComparator.instance.compare(e1.getKey(), e2.getKey());
				}

				return result;
			}).findFirst().get().getValue().get(1);

			board.put(c, null);
			board.put(moveTarget, this);
			return moveTarget;
		}

		return c;
	}

	public boolean isDead() {
		return hp <= 0;
	}

	@Override
	public String toShortString() {
		return this.type.toString();
	}
}
