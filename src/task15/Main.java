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

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("inputs/input15.txt"));

		Board board = new Board(lines.get(0).length(), lines.size());
		initBoard(board, lines);
		play(board);
	}

	private static void play(Board board) {
		int counter = 0;
		while (true) {
			for (Entry<Coordinate, Token> e : board.getUnitEntries()) {
				if (e.getValue() == null) {
					continue; // got killed
				}

				Unit unit = (Unit) e.getValue();
				Coordinate c = e.getKey();

				Optional<SimpleEntry<Coordinate, Unit>> opponent = board.getNextAdjacentOpponentOf(unit, c);

				if (!opponent.isPresent()) {
					try {
						c = unit.move(board, e.getKey());
						opponent = board.getNextAdjacentOpponentOf(unit, c);
					} catch (Exception e1) {
						System.out.println(board);
						System.out.println(counter + ": " + board.finalScore(counter));
						return;
					}
				}

				if (opponent.isPresent()) {
					Unit opp = opponent.get().getValue();
					opp.hp -= Unit.ATK;
					if (opp.isDead()) {
						board.put(opponent.get().getKey(), null);
					}
				}
			}

			counter++;
			System.out.println(counter + ":\n" + board);
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
					tok = new Unit(UnitType.GOBLIN);
				} else if (type == 'E') {
					tok = new Unit(UnitType.ELF);
				}

				board.put(c, tok);
			}
		}
	}

}
