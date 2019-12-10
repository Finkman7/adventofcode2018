package task15;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class Main {
	static int elfATK = 3;

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("inputs/input15.txt"));

		while (true) {
			elfATK++;
			Board board = new Board(lines.get(0).length(), lines.size());
			initBoard(board, lines);
			play(board);
		}

	}

	private static void play(Board board) {
		long initialElfCount = board.count(UnitType.ELF);
		int counter = 0;
		while (true) {
			System.out.println(counter + ":\n" + board);
			for (Entry<Coordinate, Token> e : board.getUnitEntries()) {
				if (e.getValue() == null) {
					continue; // unit got killed this turn
				}

				Unit unit = (Unit) e.getValue();
				Coordinate unitCoords = e.getKey();

				Optional<SimpleEntry<Coordinate, Unit>> opponent = board.getNextAdjacentOpponentOf(unit, unitCoords);

				if (!opponent.isPresent()) {
					try {
						unitCoords = unit.move(board, unitCoords);
						opponent = board.getNextAdjacentOpponentOf(unit, unitCoords);
					} catch (Exception e1) {
						System.out.println("Combat Ended:\n" + board);
						System.out.println(board.finalScore(counter));
						if (board.count(UnitType.ELF) == initialElfCount) {
							System.exit(0);
						}
						return;
					}
				}

				if (opponent.isPresent()) {
					Unit opp = opponent.get().getValue();
					opp.hp -= unit.ATK;
					if (opp.isDead()) {
						board.put(opponent.get().getKey(), null);
					}
				}
			}

			counter++;
		}
	}

	private static void initBoard(Map<Coordinate, Token> board, List<String> lines) {
		for (int j = 0; j < lines.size(); j++) {
			String line = lines.get(j);
			for (int i = 0; i < line.length(); i++) {
				Coordinate c = new Coordinate(i, j);
				char type = line.charAt(i);
				Token tok = null;

				if (type == '#') {
					tok = new Wall();
				} else if (type == 'G') {
					tok = new Unit(UnitType.GOBLIN, 3);
				} else if (type == 'E') {
					tok = new Unit(UnitType.ELF, elfATK);
				}

				board.put(c, tok);
			}
		}
	}

}
