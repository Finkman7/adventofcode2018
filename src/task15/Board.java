package task15;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Board extends TreeMap<Coordinate, Token> {
	public final int				WIDTH, HEIGHT;
	private Comparator<Coordinate>	byUnitHealth	= Comparator.comparingInt(c -> ((Unit) this.get(c)).hp);

	public Board(int width, int height) {
		super(CoordComparator.instance);
		this.WIDTH = width;
		this.HEIGHT = height;
	}

	public boolean isOccupied(Coordinate c) {
		return this.get(c) != null;
	}

	public List<java.util.Map.Entry<Coordinate, Token>> getUnitEntries() {
		return this.entrySet().stream().filter(c -> isOccupied(c.getKey()) && c.getValue() instanceof Unit)
				.collect(Collectors.toList());
	}

	public List<java.util.Map.Entry<Coordinate, Token>> getOpponentEntriesOf(Unit unit, Coordinate unitCoords) {
		return this.entrySet().stream()
				.filter(c -> isOccupied(c.getKey()) && c.getValue() instanceof Unit
						&& !((Unit) c.getValue()).type.equals(unit.type))
				.sorted(Comparator.comparingInt(e -> unitCoords.manhattanDistanceTo(e.getKey())))
				.collect(Collectors.toList());
	}

	public List<Coordinate> getAdjacentCoordinatesof(Coordinate c) {
		Coordinate up = c.clone();
		up.y--;
		Coordinate down = c.clone();
		down.y++;
		Coordinate left = c.clone();
		left.x--;
		Coordinate right = c.clone();
		right.x++;

		return List.of(up, left, right, down).stream().filter(c2 -> isValid(c2)).collect(Collectors.toList());
	}

	public List<Coordinate> getFreeAdjacentCoordinatesOf(Coordinate c) {
		return getAdjacentCoordinatesof(c).stream().filter(c2 -> !isOccupied(c2)).collect(Collectors.toList());
	}

	public Optional<SimpleEntry<Coordinate, Unit>> getNextAdjacentOpponentOf(Unit unit, Coordinate unitCoords) {
		return getAdjacentCoordinatesof(unitCoords).stream()
				.filter(c -> isOccupied(c) && this.get(c) instanceof Unit
						&& !((Unit) this.get(c)).type.equals(unit.type))
				.sorted(byUnitHealth.thenComparing(CoordComparator.instance))
				.map(c -> new AbstractMap.SimpleEntry<Coordinate, Unit>(c, (Unit) this.get(c))).findFirst();
	}

	private boolean isValid(Coordinate c2) {
		return c2.x >= 0 && c2.x < WIDTH && c2.y >= 0 && c2.y < HEIGHT;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		this.keySet().stream().forEach(c -> {
			if (isOccupied(c)) {
				sb.append(this.get(c).toShortString());
			} else {
				sb.append(".");
			}

			if (c.x == this.WIDTH - 1) {
				sb.append(" ");
				// sb.append(this.getUnitEntries().stream().filter(e -> e.getKey().y == c.y)
				// .map(e -> e.getValue().toString()).collect(Collectors.joining(", ")));
				sb.append("\n");
			}
		});

		return sb.toString();
	}

	public Path getShortestPath(Coordinate startCoords, Coordinate targetCoords) {
		Comparator<Path> comparator = byTotalPlusHeuristicDistance(targetCoords).thenComparing(byFirstStep());
		PriorityQueue<Path> potentialPaths = new PriorityQueue<>(comparator);
		Path initial = new Path();
		initial.add(startCoords);
		potentialPaths.add(initial);

		Path shortestPath = null;
		Map<Coordinate, Path> bestPartialPaths = new HashMap<>();
		while (!potentialPaths.isEmpty()) {
			Path p = potentialPaths.poll();

			if (p.getTail().equals(targetCoords)) {
				return p;
			} else {
				for (Coordinate adj_Coords : getFreeAdjacentCoordinatesOf(p.getTail())) {
					Path extension = p.clone();
					extension.add(adj_Coords);

					if (!bestPartialPaths.containsKey(adj_Coords)
							|| comparator.compare(bestPartialPaths.get(adj_Coords), extension) > 0) {
						potentialPaths.add(extension);
						bestPartialPaths.put(adj_Coords, extension);
					}
				}
			}
		}

		return shortestPath;
	}

	private Comparator<Path> byTotalPlusHeuristicDistance(Coordinate target) {
		return Comparator.comparingInt(p -> p.size() + p.getTail().manhattanDistanceTo(target));

	}

	private Comparator<Path> byFirstStep() {
		return (p1, p2) -> CoordComparator.instance.compare(p1.get(1), p2.get(1));
	}

	public int finalScore(int counter) {
		return counter * getUnitEntries().stream().mapToInt(e -> ((Unit) e.getValue()).hp).sum();
	}

	public long count(UnitType type) {
		return this.getUnitEntries().stream().filter(c -> ((Unit) c.getValue()).type.equals(type)).count();
	}
}
