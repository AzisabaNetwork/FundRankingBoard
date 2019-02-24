package jp.azisaba.main.fundrankingboard.armorstand;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R2.WorldServer;

public class HoloAPI {

	private static final double lineSpace = 0.1;
	private static final double lineHeight = 0.25;

	public static DisplayResult displayHolographic(Player p, Location loc, HoloComponent component) {

		if (p == null) {
			throw new IllegalArgumentException("player mustn't be null.");
		}
		if (loc == null) {
			throw new IllegalArgumentException("location mustn't be null.");
		}
		if (loc.getWorld() == null) {
			throw new IllegalArgumentException("world of location mustn't be null.");
		}
		if (component == null) {
			throw new IllegalArgumentException("component mustn't be null.");
		}

		return displayHolo(p, loc, component);
	}

	private static DisplayResult displayHolo(Player p, Location loc, HoloComponent comp) {

		int lines = getLines(comp);
		double y = lines * lineHeight + (lines - 1) * lineSpace;
		loc.add(0, y, 0);

		List<Integer> idList = new ArrayList<Integer>();
		int id;

		id = createArmorStand(p, loc, comp.getText(), comp);
		loc.subtract(0, lineHeight + lineSpace, 0);

		idList.add(id);

		for (HoloComponent childComp : comp.getAllHoloComponent()) {
			id = createArmorStand(p, loc, childComp.getText(), childComp);
			idList.add(id);

			loc.subtract(0, lineHeight + lineSpace, 0);
		}

		return new DisplayResult(p, idList);
	}

	private static int getLines(HoloComponent comp) {
		int lines = comp.getText().split("\n").length;

		for (HoloComponent childComp : comp.getAllHoloComponent()) {
			lines += childComp.getText().split("\n").length;
		}

		return lines;
	}

	private static int createArmorStand(Player p, Location loc, String str, HoloComponent holoComp) {
		WorldServer s = ((CraftWorld) loc.getWorld()).getHandle();
		EntityArmorStand stand = new EntityArmorStand(s);

		NBTTagCompound comp = new NBTTagCompound();
		comp.setBoolean("Marker", true);
		comp.setByte("HoloType", (byte) holoComp.getHoloType().getValue());
		comp.setByte("HoloCompID", (byte) holoComp.getHoloType().getValue());

		stand.a(comp);

		stand.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);

		stand.setCustomName(new ChatComponentText(str));
		stand.setCustomNameVisible(true);
		stand.setInvisible(true);

		PacketPlayOutSpawnEntityLiving spawnP = new PacketPlayOutSpawnEntityLiving((EntityLiving) stand);

		((CraftPlayer) p).getHandle().playerConnection.sendPacket(spawnP);

		return stand.getId();
	}
}
