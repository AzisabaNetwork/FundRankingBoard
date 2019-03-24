package jp.azisaba.main.fundrankingboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import jp.azisaba.main.homogui.utils.RankingFetcher;

public class DisplayListener implements Listener {

	private HashMap<Player, Hologram> results = new HashMap<>();

	@EventHandler
	public void onMoveClose(PlayerMoveEvent e) {

		Player p = e.getPlayer();

		Location from = e.getFrom();
		Location to = e.getTo();
		Location point = FundRankingBoard.getPluginConfig().displayLocation;

		if (point == null) {
			return;
		}

		if (point.getWorld() != from.getWorld() || point.getWorld() != to.getWorld()
				|| from.getWorld() != to.getWorld()) {
			return;
		}

		if (from.distance(point) > 32 && to.distance(point) <= 32) {
			display(p, point);
		} else if (from.distance(point) <= 32 && to.distance(point) > 32) {
			clear(p);
		}
	}

	@EventHandler
	public void onTeleportToClose(PlayerTeleportEvent e) {

		Player p = e.getPlayer();

		Location from = e.getFrom();
		Location to = e.getTo();
		Location point = FundRankingBoard.getPluginConfig().displayLocation;

		if (point == null) {
			return;
		}

		if (point.getWorld() != from.getWorld() || point.getWorld() != to.getWorld()
				|| from.getWorld() != to.getWorld()) {
			return;
		}

		if (from.distance(point) > 32 && to.distance(point) <= 32) {
			display(p, point);
		} else if (from.distance(point) <= 32 && to.distance(point) > 32) {
			clear(p);
		}
	}

	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();

		World from = e.getFrom();
		World to = p.getWorld();

		Location point = FundRankingBoard.getPluginConfig().displayLocation;

		if (point == null || from == to) {
			return;
		}

		if (point.getWorld() == from && from != to) {
			clear(p);
		} else if (point.getWorld() == to && p.getLocation().distance(point) <= 32) {
			display(p, point);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		Location join = p.getLocation();
		Location point = FundRankingBoard.getPluginConfig().displayLocation;

		if (point == null) {
			return;
		}

		if (point.getWorld() != join.getWorld()) {
			return;
		}

		if (point.distance(join) <= 32) {
			display(p, point);
		}
	}

	public void display(Player p, Location loc) {
		if (results.containsKey(p)) {
			results.get(p).removeAll();
		}

		Location point = loc.clone();

		Hologram holo = Hologram.create(ChatColor.RED + "集計中...", point.clone().add(0, 1, 0));
		holo.display(p);

		holo.setLine(0, ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "総資金ランキング");

		new Thread() {
			public void run() {
				List<Entry<String, BigDecimal>> dataList = RankingFetcher.getData();

				int count = 0;
				int currentRank = 0;
				BigDecimal before = BigDecimal.ZERO;
				boolean includeTarget = false;
				for (Entry<String, BigDecimal> data : dataList) {

					if (data.getValue().compareTo(before) != 0) {
						currentRank++;
					}
					before = data.getValue();
					count++;

					String prefix = "";
					if (data.getKey().equals(p.getName())) {
						prefix = ChatColor.BLUE + "YOU » ";
					}

					if (count > 5) {

						if (includeTarget) {
							break;
						}

						if (!data.getKey().equals(p.getName())) {
							continue;
						}

						holo.addLine(ChatColor.LIGHT_PURPLE + StringUtils.repeat("=", 30));
						holo.addLine(
								prefix + ChatColor.YELLOW + "" + currentRank + "位 " + ChatColor.AQUA + data.getKey()
										+ ChatColor.GREEN + ": " + ChatColor.GOLD + "" + format(data.getValue()));
						break;
					}
					holo.addLine(prefix + ChatColor.YELLOW + "" + currentRank + "位 " + ChatColor.AQUA + data.getKey()
							+ ChatColor.GREEN + ": " + ChatColor.GOLD + "" + format(data.getValue()));

					if (data.getKey().equals(p.getName())) {
						includeTarget = true;
					}
				}

				holo.update(p);
				results.put(p, holo);
			}
		}.start();
	}

	private void clear(Player p) {
		if (results.containsKey(p)) {
			results.get(p).removeAll();
			results.remove(p);
		}
	}

	public void clearAll() {
		for (Player p : results.keySet()) {
			results.get(p).removeAll();
		}
	}

	Map<Integer, String> digits = new HashMap<Integer, String>() {
		{
			put(0, "");
			put(1, "万");
			put(2, "億");
			put(3, "兆");
			put(4, "京");
			put(5, "垓");
		}
	};

	private String format(BigDecimal value) {
		String str = "";
		value = value.setScale(1, RoundingMode.HALF_DOWN);

		if (!value.toString().endsWith(".0")) {
			str = value.toString().substring(value.toString().length() - 2, value.toString().length());
			value = value.setScale(0, RoundingMode.HALF_DOWN);
		}

		String numStr = value.toString().replace(".0", "");

		int attempt = 0;
		while (numStr.length() > 4) {

			if (attempt > 5) {
				return value.toString();
			}

			String sec = numStr.substring(numStr.length() - 4, numStr.length());

			while (sec.startsWith("0")) {
				sec = sec.substring(1);
			}

			if (sec.length() > 0) {
				str = sec + digits.get(attempt) + str;
			}
			numStr = numStr.substring(0, numStr.length() - 4);

			attempt++;
		}

		if (numStr.length() != 0) {
			str = numStr + digits.get(attempt) + str;
		}

		return str;
	}
}
