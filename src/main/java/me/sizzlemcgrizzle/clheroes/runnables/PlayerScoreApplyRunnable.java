package me.sizzlemcgrizzle.clheroes.runnables;

import de.craftlancer.core.util.Tuple;
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
import java.util.List;
import java.util.UUID;

public class PlayerScoreApplyRunnable extends BukkitRunnable {
	
	private List<Tuple<UUID, Integer>> top3;
	
	private File file = new File(SimplePlugin.getInstance().getDataFolder(), "locations.yml");
	private YamlConfiguration config = new YamlConfiguration();
	
	PlayerScoreApplyRunnable(List<Tuple<UUID, Integer>> top3) {
		this.top3 = top3;
	}
	
	@Override
	public void run() {
		try {
			
			if (!file.exists())
				FileUtil.extract("locations.yml");
			config.load(file);
			
			int counter = 1;
			for (Tuple<UUID, Integer> entry : top3) {
				
				UUID uuid = entry.getKey();
				ConfigurationSection configSection = config.getConfigurationSection("playerscore").getConfigurationSection(String.valueOf(counter));
				
				List<Location> signLocationList = (List<Location>) configSection.getList("sign_location");
				List<Location> headLocationList = (List<Location>) configSection.getList("head_location");
				if (signLocationList != null)
					for (Location signLocation : signLocationList)
						setSign(signLocation, entry.getValue(), entry.getKey());
				
				if (headLocationList != null)
					for (int i = 0; i < headLocationList.size(); i++)
						if (MaterialUtil.isHead(headLocationList.get(i).getBlock().getType()))
							new ApplyHeadRunnable(headLocationList.get(i), uuid).runTaskLater(SimplePlugin.getInstance(), counter * 80 * (i + 1));
				
				counter++;
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			
		}
	}
	
	private void setSign(Location signLocation, int score, UUID playerUUID) {
		if (signLocation == null || !MaterialUtil.isSign(signLocation.getBlock().getType()) || !signLocation.getWorld().isChunkLoaded(signLocation.getBlockX() >> 4, signLocation.getBlockZ() >> 4))
			return;
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
		sign.setLine(1, ChatColor.WHITE + Bukkit.getOfflinePlayer(playerUUID).getName());
		sign.setLine(2, ChatColor.GOLD + "" + score);
		sign.update();
		if (Settings.DEBUG_MESSAGES)
			Common.log("Setting baltop sign, with player " + Bukkit.getOfflinePlayer(playerUUID).getName());
	}
}


