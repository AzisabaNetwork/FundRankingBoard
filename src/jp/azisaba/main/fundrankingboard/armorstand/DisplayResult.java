package jp.azisaba.main.fundrankingboard.armorstand;

import java.util.List;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;

public class DisplayResult {

	private Player p;
	private List<Integer> displayedIDs;

	public DisplayResult(Player p, List<Integer> displayedIDs) {
		this.p = p;
		this.displayedIDs = displayedIDs;
	}

	public void delete() {

		for (int id : displayedIDs) {
			PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(id);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(destroy);
		}
	}
}
