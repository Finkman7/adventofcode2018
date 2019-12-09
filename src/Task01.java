import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Task01 {

	public static void main(String[] args) throws IOException {
		a();
		b();
	}

	private static void b() throws NumberFormatException, IOException {
		long frequency = 0;
		Set<Long> frequencies = new HashSet<>();
		List<String> lines = Files.readAllLines(Paths.get("inputs/input01.txt"));

		while (true) {
			for (String line : lines) {
				frequency += Long.parseLong(line);
				if (frequencies.contains(frequency)) {
					System.out.println("Reached " + frequency + " twice!");
					return;
				} else {
					frequencies.add(frequency);
				}
			}
		}
	}

	private static void a() throws NumberFormatException, IOException {
		long frequency = 0;

		for (String line : Files.readAllLines(Paths.get("inputs/input01.txt"))) {
			frequency += Long.parseLong(line);
		}

		System.out.println(frequency);
	}

}
