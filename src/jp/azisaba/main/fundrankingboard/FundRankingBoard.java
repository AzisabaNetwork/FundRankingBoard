package jp.azisaba.main.fundrankingboard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FundRankingBoard extends JavaPlugin {

	private static PluginConfig config;

	@Override
	public void onEnable() {

		FundRankingBoard.config = new PluginConfig(this);
		FundRankingBoard.config.loadConfig();

		Bukkit.getPluginManager().registerEvents(new DispalyListener(), this);

		Bukkit.getLogger().info(getName() + " enabled.");
	}

	@Override
	public void onDisable() {
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
