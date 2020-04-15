package me.sizzlemcgrizzle.clheroes.runnables;

import de.craftlancer.clclans.Clan;
import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import me.sizzlemcgrizzle.clheroes.command.util.MaterialUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.BannerMeta;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ClanApplyRunnable extends HeroRunnable {
	private File file = new File(SimplePlugin.getInstance().getDataFolder() + File.separator + "locations.yml");
	private YamlConfiguration config = new YamlConfiguration();
	private List<Clan> topClans;

	public ClanApplyRunnable(List<Clan> topClans) {
		this.topClans = topClans;
	}

	@Override
	public void doFunction() {
		if (!file.exists()) {
			FileUtil.extract("locations.yml");
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

			signLocation = HeroRunnable.parseLocation(configSection.getString("sign_location"));
			bannerLocation = HeroRunnable.parseLocation(configSection.getString("banner_location"));

			setClanSign(clan, signLocation);
			setClanBanner(clan, bannerLocation);

			counter++;
		}

	}

	@Override
	public void run() {
		doFunction();
	}

	private static void setClanSign(Clan clan, Location signLocation) {
		if (signLocation == null || clan == null || !MaterialUtil.isSign(signLocation.getBlock().getType()) || !signLocation.getWorld().isChunkLoaded(signLocation.getBlockX() >> 4, signLocation.getBlockZ() >> 4)) {
			return;
		}
		org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signLocation.getBlock().getState();
		sign.setLine(1, clan.getColor() + " " + ChatColor.BOLD + "[" + clan.getTag() + "]");
		sign.setLine(2, clan.getColor() + clan.getName());
		sign.update();
		if (Settings.DEBUG_MESSAGES)
			Common.log("Setting clan sign, with clan " + clan.getName());
	}

	private static void setClanBanner(Clan clan, Location bannerLocation) {
		if (clan.getBanner() == null || bannerLocation == null || !MaterialUtil.isBanner(bannerLocation.getBlock().getType()) || !bannerLocation.getWorld().isChunkLoaded(bannerLocation.getBlockX() >> 4, bannerLocation.getBlockZ() >> 4)) {
			return;
		}

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
		if (Settings.DEBUG_MESSAGES)
			Common.log("Setting clan banner, with clan " + clan.getName());
	}
}
