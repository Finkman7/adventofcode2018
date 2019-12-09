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
				sb.append(this.get(c));
			} else {
				sb.append("......");
			}

			if (c.x == this.WIDTH - 1) {
				sb.append("\n");
			}
		});

		return sb.toString();
	}

	public Path getShortestPath(Coordinate c, Coordinate c_Target) {
		Comparator<Path> comparator = byTotalPlusHeuristicDistance(c_Target);
		PriorityQueue<Path> potentialPaths = new PriorityQueue<>(comparator);
		Path initial = new Path();
		initial.add(c);
		potentialPaths.add(initial);

		Path shortestPath = null;
		Map<Coordinate, Path> bestPaths = new HashMap<>();
		while (!potentialPaths.isEmpty()) {
			Path best = potentialPaths.poll();

			if (shortestPath != null) {
				if (comparator.compare(shortestPath, best) < 0) {
					break;
				} else if (CoordComparator.instance.compare(shortestPath.get(1), best.get(1)) > 0) {
					shortestPath = best;
				}
			} else {
				if (best.getTail().equals(c_Target)) {
					shortestPath = best;
				} else {
					for (Coordinate c2 : getFreeAdjacentCoordinatesOf(best.getTail())) {
						Path p = new Path(best);
						p.add(c2);

						if (!bestPaths.containsKey(c2) || bestPaths.get(c2).size() > p.size()
								|| CoordComparator.instance.compare(bestPaths.get(c2).get(1), p.get(1)) > 0) {
							potentialPaths.add(p);
							bestPaths.put(c2, p);
						}
					}
				}
			}
		}

		return shortestPath;
	}

	private Comparator<Path> byTotalPlusHeuristicDistance(Coordinate target) {
		return Comparator.comparingInt(p -> p.size() + p.getTail().manhattanDistanceTo(target));
	}

	public int finalScore(int counter) {
		return counter * getUnitEntries().stream().mapToInt(e -> ((Unit) e.getValue()).hp).sum();
	}
}
