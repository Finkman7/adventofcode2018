import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Task02 {
	private static final Pattern	threeMatch	= Pattern.compile(".*([a-z]).*\\1.*\\1.*");
	private static final Pattern	twoMatch	= Pattern.compile(".*([a-z]).*\\1.*");

	public static void main(String[] args) throws IOException {
		// aRegEx();
		a();
		b();
	}

	private static void b() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("inputs/input02.txt")).stream()
				.map(line -> line.toLowerCase()).collect(Collectors.toList());

		for (String line1 : lines) {
			for (String line2 : lines) {
				if (line1 != line2) {
					try {
						String c = getCommonCharacters(line1, line2);
						System.out.println(line1 + " and " + line2 + " have " + c + " in common");
					} catch (Exception e) {

					}
				}
			}
		}
	}

	private static void a() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("inputs/input02.txt")).stream()
				.map(line -> line.toLowerCase()).collect(Collectors.toList());
		long countTwo = 0;
		long countThree = 0;

		Map<String, Map<String, Long>> charCountsTotal = new HashMap<>();
		for (String line : lines) {
			Map<String, Long> charCounts = line.codePoints().mapToObj(i -> String.valueOf((char) i))
					.collect(Collectors.groupingBy(i -> i, Collectors.counting()));
			charCountsTotal.put(line, charCounts);

			if (charCounts.values().stream().filter(count -> count == 3).findAny().isPresent()) {
				countThree++;
			}

			if (charCounts.values().stream().filter(count -> count == 2).findAny().isPresent()) {
				countTwo++;
			}
		}

		System.out.println("Checksum: " + countTwo * countThree);
	}

	private static String getCommonCharacters(String line1, String line2) throws Exception {
		int differingPosition = -1;

		if (line1.length() == line2.length()) {
			for (int i = 0; i < line1.length(); i++) {
				if (line1.charAt(i) != line2.charAt(i)) {
					if (differingPosition == -1) {
						differingPosition = i;
					} else {
						throw new Exception();
					}
				}
			}
		}

		return line1.substring(0, differingPosition) + line1.substring(differingPosition + 1);
	}

	// Wrong
	private static void aRegEx() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("inputs/input02.txt")).stream()
				.map(line -> line.toLowerCase()).collect(Collectors.toList());
		long countTwo = 0;
		long countThree = 0;

		for (String line : lines) {
			Set<String> threeMatches = new HashSet<>();
			Matcher threeMatcher = threeMatch.matcher(line);
			while (threeMatcher.find()) {
				threeMatches.add(threeMatcher.group(1));
			}

			if (!threeMatches.isEmpty()) {
				countThree++;
			}

			Matcher twoMatcher = twoMatch.matcher(line);
			while (twoMatcher.find()) {
				if (!threeMatches.contains(twoMatcher.group(1))) {
					countTwo++;
					break;
				}
			}
		}

		System.out.println("Checksum: " + countTwo * countThree);
	}

}
