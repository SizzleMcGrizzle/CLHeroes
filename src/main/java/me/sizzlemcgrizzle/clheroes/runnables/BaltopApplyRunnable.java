package me.sizzlemcgrizzle.clheroes.runnables;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import me.sizzlemcgrizzle.clheroes.command.util.MaterialUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class BaltopApplyRunnable extends HeroRunnable {
	private File file = new File(SimplePlugin.getInstance().getDataFolder(), "locations.yml");
	private YamlConfiguration config = new YamlConfiguration();
	private LinkedHashMap<String, Double> top3;

	public BaltopApplyRunnable(LinkedHashMap<String, Double> top3) {
		this.top3 = top3;
	}

	@Override
	public void doFunction() throws IOException, InvalidConfigurationException {
		if (!file.exists())
			FileUtil.extract("locations.yml");
		config.load(file);

		int counter = 1;
		ConfigurationSection configSection;
		Location signLocation;
		Location headLocation;
		for (Map.Entry<String, Double> entry : top3.entrySet()) {

			UUID uuid = UUID.fromString(entry.getKey());
			configSection = config.getConfigurationSection("baltop").getConfigurationSection(String.valueOf(counter));

			signLocation = HeroRunnable.parseLocation(configSection.getString("sign_location"));
			headLocation = HeroRunnable.parseLocation(configSection.getString("head_location"));
			setBaltopSign(uuid, signLocation, entry.getValue());

			if (headLocation != null && MaterialUtil.isHead(headLocation.getBlock().getType()))
				new ApplyBaltopHeadRunnable(headLocation, uuid).runTaskLater(SimplePlugin.getInstance(), counter * 80);

			counter++;
		}
		new ClanCalculateRunnable().runTaskAsynchronously(SimplePlugin.getInstance());
	}

	@Override
	public void run() {
		try {
			doFunction();
		} catch (IOException | InvalidConfigurationException e) {
			Common.log("Cannot load locations.yml!");
			e.printStackTrace();
		}
	}

	private static void setBaltopSign(UUID uuid, Location signLocation, Double balance) {
		if (signLocation == null || !MaterialUtil.isSign(signLocation.getBlock().getType()) || !signLocation.getWorld().isChunkLoaded(signLocation.getBlockX() >> 4, signLocation.getBlockZ() >> 4)) {
			return;
		}
		double i = balance;
		int e = (int) i;
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
		sign.setLine(1, ChatColor.WHITE + Bukkit.getOfflinePlayer(uuid).getName());
		sign.setLine(2, ChatColor.GOLD + "$" + e);
		sign.update();
		if (Settings.DEBUG_MESSAGES)
			Common.log("Setting baltop sign, with player " + Bukkit.getOfflinePlayer(uuid).getName());
	}

	private static class ApplyBaltopHeadRunnable extends BukkitRunnable {
		Location location;
		UUID uuid;

		ApplyBaltopHeadRunnable(Location location, UUID uuid) {
			this.location = location;
			this.uuid = uuid;
		}

		@Override
		public void run() {
			Skull skull = (Skull) location.getBlock().getState();
			skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
			skull.update();
			if (Settings.DEBUG_MESSAGES)
				Common.log("Setting baltop head, with player " + Bukkit.getOfflinePlayer(uuid).getName());
		}
	}
}
