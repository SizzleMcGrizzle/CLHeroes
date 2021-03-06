package me.sizzlemcgrizzle.clheroes.runnables;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import me.sizzlemcgrizzle.clheroes.command.util.MaterialUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BaltopApplyRunnable extends BukkitRunnable {
	private File file = new File(SimplePlugin.getInstance().getDataFolder(), "locations.yml");
	private YamlConfiguration config = new YamlConfiguration();
	private LinkedHashMap<String, Double> top3;
	
	public BaltopApplyRunnable(LinkedHashMap<String, Double> top3) {
		this.top3 = top3;
	}
	
	@Override
	public void run() {
		try {
			
			if (!file.exists())
				FileUtil.extract("locations.yml");
			config.load(file);
			
			int counter = 1;
			for (Map.Entry<String, Double> entry : top3.entrySet()) {
				if (entry == null || entry.getKey() == null || entry.getValue() == null) {
					counter++;
					continue;
				}
				UUID uuid = UUID.fromString(entry.getKey());
				ConfigurationSection configSection = config.getConfigurationSection("baltop").getConfigurationSection(String.valueOf(counter));
				
				List<Location> signLocationList = (List<Location>) configSection.getList("sign_location");
				List<Location> headLocationList = (List<Location>) configSection.getList("head_location");
				if (signLocationList != null)
					for (Location signLocation : signLocationList)
						setBaltopSign(uuid, signLocation, entry.getValue());
				
				if (headLocationList != null)
					for (int i = 0; i < headLocationList.size(); i++)
						if (MaterialUtil.isHead(headLocationList.get(i).getBlock().getType()))
							new ApplyHeadRunnable(headLocationList.get(i), uuid).runTaskLater(SimplePlugin.getInstance(), counter * 80 * (i + 1));
				
				counter++;
			}
			new ClanCalculateRunnable().runTaskAsynchronously(SimplePlugin.getInstance());
		} catch (IOException | InvalidConfigurationException e) {
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
}
