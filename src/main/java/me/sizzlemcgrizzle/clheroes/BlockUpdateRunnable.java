package me.sizzlemcgrizzle.clheroes;

import com.earth2me.essentials.IEssentials;
import de.craftlancer.clclans.CLClans;
import de.craftlancer.clclans.Clan;
import me.sizzlemcgrizzle.clheroes.command.util.MaterialUtil;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BlockUpdateRunnable extends BukkitRunnable {

	@Override
	public void run() {
		new CalculateBalanceRunnable().runTaskAsynchronously(CLHeroesPlugin.getInstance());
	}


	private static class CalculateClanScore extends BukkitRunnable {

		@Override
		public void run() {
			CLClans clans = (CLClans) Bukkit.getPluginManager().getPlugin("CLClans");
			List<Clan> topClans = new ArrayList<>();
			clans.getClans().stream().sorted(Comparator.comparingDouble(Clan::calculateClanScore).reversed()).limit(3).forEachOrdered(topClans::add);
			new ApplyClanRunnable(topClans).runTask(SimplePlugin.getInstance());
		}
	}


	private static class CalculateBalanceRunnable extends BukkitRunnable {
		private File folder = new File(Bukkit.getPluginManager().getPlugin("Essentials").getDataFolder() + File.separator + "/userdata");
		private YamlConfiguration reader = new YamlConfiguration();
		private IEssentials ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
		private LinkedHashMap<String, Double> top3 = new LinkedHashMap<>();

		@Override
		public void run() {
			if (!folder.exists()) {
				try {
					folder.createNewFile();
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Map<String, Double> balances = new HashMap<>();
			String first = null, second = null, third = null;

			if (ess.getSettings().isEcoDisabled()) {
				if (ess.getSettings().isDebug()) {
					ess.getLogger().info("Internal economy functions disabled, aborting baltop.");
				}
			} else {
				if (folder.listFiles() == null) {
					return;
				}
				for (File file : folder.listFiles()) {
					try {
						reader.load(file);
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
					}
					if (reader.getString("money") == null || reader.getString("lastAccountName") == null)
						continue;
					balances.put(file.getName().replace(".yml", ""), Double.parseDouble(reader.getString("money")));
				}
				for (Map.Entry<String, Double> entry : balances.entrySet()) {
					Double balance = entry.getValue();
					if (first == null || balance > balances.get(first)) {
						third = second;
						second = first;
						first = entry.getKey();
						continue;
					}
					if (second == null || balance > balances.get(second)) {
						third = second;
						second = entry.getKey();
						continue;
					}
					if (third == null || balance > balances.get(third)) {
						third = entry.getKey();
					}
				}
				top3.put(first, balances.get(first));
				top3.put(second, balances.get(second));
				top3.put(third, balances.get(third));
				new ApplyBaltopRunnable(top3).runTask(SimplePlugin.getInstance());
			}
		}
	}

	private static class ApplyBaltopRunnable extends BukkitRunnable {
		private LinkedHashMap<String, Double> map;

		public ApplyBaltopRunnable(LinkedHashMap<String, Double> map) {
			this.map = map;
		}

		@Override
		public void run() {
			File file = new File(SimplePlugin.getInstance().getDataFolder() + File.separator + "locations.yml");
			YamlConfiguration config = new YamlConfiguration();

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				config.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}

			int counter = 1;
			ConfigurationSection configSection;
			Location signLocation;
			Location headLocation;
			for (Map.Entry<String, Double> entry : map.entrySet()) {

				UUID uuid = UUID.fromString(entry.getKey());
				configSection = config.getConfigurationSection("baltop").getConfigurationSection(String.valueOf(counter));

				signLocation = BlockUpdateRunnable.parseLocation(configSection.getString("sign_location"));
				headLocation = BlockUpdateRunnable.parseLocation(configSection.getString("head_location"));
				if (signLocation == null || headLocation == null) {
					counter++;
					continue;
				}
				if (!MaterialUtil.isSign(signLocation.getBlock().getType())) {
					counter++;
					continue;
				}
				double i = entry.getValue();
				int e = (int) i;
				org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
				sign.setLine(1, ChatColor.WHITE + Bukkit.getOfflinePlayer(uuid).getName());
				sign.setLine(2, ChatColor.GOLD + "$" + e);
				sign.update();

				if (!MaterialUtil.isHead(headLocation.getBlock().getType())) {
					counter++;
					continue;
				}
				Skull skull = (Skull) headLocation.getBlock().getState();
				skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
				skull.update();

				counter++;
			}
			new CalculateClanScore().runTaskAsynchronously(CLHeroesPlugin.getInstance());
		}
	}

	private static class ApplyClanRunnable extends BukkitRunnable {
		private File file = new File(SimplePlugin.getInstance().getDataFolder() + File.separator + "locations.yml");
		private YamlConfiguration config = new YamlConfiguration();
		List<Clan> topClans;

		public ApplyClanRunnable(List<Clan> topClans) {
			this.topClans = topClans;
		}

		@Override
		public void run() {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				config.load(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}

			ConfigurationSection configSection;
			Location signLocation;
			Location bannerLocation;
			int counter = 1;
			for (Clan clan : topClans) {

				configSection = config.getConfigurationSection("clan").getConfigurationSection(String.valueOf(counter));

				signLocation = BlockUpdateRunnable.parseLocation(configSection.getString("sign_location"));
				bannerLocation = BlockUpdateRunnable.parseLocation(configSection.getString("banner_location"));
				if (signLocation == null) {
					counter++;
					continue;
				}
				if (!MaterialUtil.isSign(signLocation.getBlock().getType())) {
					counter++;
					continue;
				}
				org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
				sign.setLine(1, clan.getColor() + " " + ChatColor.BOLD + "[" + clan.getTag() + "]");
				sign.setLine(2, clan.getColor() + clan.getName());
				sign.update();

				if (bannerLocation == null) {
					counter++;
					continue;
				}
				if (!MaterialUtil.isBanner(bannerLocation.getBlock().getType())) {
					counter++;
					continue;
				}

				if (clan.getBanner() == null) {
					counter++;
					continue;
				}

				//To apply color and patter


				//If the bannerLocation is a wall banner, set the type to the wall banner variant of the clan banner
				if (bannerLocation.getBlock().getType().toString().contains("_WALL_BANNER")) {
					Directional directional = (Directional) bannerLocation.getBlock().getBlockData();
					BlockFace face = directional.getFacing();

					bannerLocation.getBlock().setType(Material.getMaterial(clan.getBanner().getType().toString().replace("_BANNER", "_WALL_BANNER")));
					//To apply direction

					//Set face to what it was before.
					Directional newFace = (Directional) bannerLocation.getBlock().getBlockData();
					newFace.setFacing(face);
					bannerLocation.getBlock().setBlockData(newFace);
				} else {
					Rotatable rotatable = (Rotatable) bannerLocation.getBlock().getBlockData();
					BlockFace face = rotatable.getRotation();

					bannerLocation.getBlock().setType(clan.getBanner().getType());

					Rotatable newRotatable = (Rotatable) bannerLocation.getBlock().getBlockData();
					newRotatable.setRotation(face);
					bannerLocation.getBlock().setBlockData(newRotatable);
				}

				//If the banner meta of the clan banner is null, don't set the patterns.

				//Set directional to previous face

				BannerMeta clanBanner = (BannerMeta) clan.getBanner().getItemMeta();
				Banner banner = (Banner) bannerLocation.getBlock().getState();

				if (clanBanner != null) {
					banner.setPatterns(clanBanner.getPatterns());
					banner.update();
				}

				counter++;
			}


		}
	}

	private static Location parseLocation(String string) {
		double x, y, z;
		World world;

		String[] array = string.split("&====&");
		if (array.length < 4)
			return null;
		x = Double.parseDouble(array[0]);
		y = Double.parseDouble(array[1]);
		z = Double.parseDouble(array[2]);
		world = Bukkit.getWorld(array[3]);

		return new Location(world, x, y, z);
	}
}
