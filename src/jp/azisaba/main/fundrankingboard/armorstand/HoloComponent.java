package jp.azisaba.main.fundrankingboard.armorstand;

import java.util.ArrayList;
import java.util.List;

public class HoloComponent {

	private List<HoloComponent> compList = new ArrayList<HoloComponent>();

	private HoloType type = HoloType.ONLY_TEXT;

	private String text;

	public HoloComponent(String str) {
		this.text = str;
	}

	public HoloComponent(String str, HoloType type) {
		this.text = str;
		this.type = type;
	}

	public HoloType getHoloType() {
		return type;
	}

	public void setHoloType(HoloType type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void append(HoloComponent component) {

		List<HoloComponent> compList2 = new ArrayList<HoloComponent>(component.getAllHoloComponent());
		component.clearAllHoloComponents();

		this.compList.add(component);

		for (HoloComponent comp : compList2) {
			this.compList.add(comp);
		}
	}

	protected List<HoloComponent> getAllHoloComponent() {
		return compList;
	}

	protected void clearAllHoloComponents() {
		this.compList.clear();
	}
}
