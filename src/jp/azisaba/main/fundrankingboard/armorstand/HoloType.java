package jp.azisaba.main.fundrankingboard.armorstand;

public enum HoloType {

	ONLY_TEXT(1), CLICKABLE(2);

	private final int value;

	private HoloType(int value) {
		this.value = value;
	}

	public int getValue() {
        return value;
}
}
