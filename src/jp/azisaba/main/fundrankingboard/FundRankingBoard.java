package jp.azisaba.main.fundrankingboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FundRankingBoard extends JavaPlugin {

	private static PluginConfig config;

	private DisplayListener listener = null;

	@Override
	public void onEnable() {

		FundRankingBoard.config = new PluginConfig(this);
		FundRankingBoard.config.loadConfig();

		listener = new DisplayListener();
		Bukkit.getPluginManager().registerEvents(listener, this);

		if (Bukkit.getOnlinePlayers().size() > 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (config.displayLocation.getWorld() != p.getWorld()) {
					continue;
				}

				if (config.displayLocation.distance(p.getLocation()) <= 32) {
					listener.display(p, config.displayLocation);
				}
			}
		}

		Bukkit.getLogger().info(getName() + " enabled.");
	}

	@Override
	public void onDisable() {

		if (listener != null) {
			listener.clearAll();
		}

		Bukkit.getLogger().info(getName() + " disabled.");
	}

	public void reloadPluginConfig() {

		this.reloadConfig();

		FundRankingBoard.config = new PluginConfig(this);
		FundRankingBoard.config.loadConfig();
	}

	public static PluginConfig getPluginConfig() {
		return config;
	}
}
