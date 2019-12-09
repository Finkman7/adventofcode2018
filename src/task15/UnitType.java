package task15;

public enum UnitType {
	ELF, GOBLIN;

	@Override
	public String toString() {
		if (this == ELF) {
			return "E";
		} else if (this == GOBLIN) {
			return "G";
		} else {
			return "!";
		}
	}
}
