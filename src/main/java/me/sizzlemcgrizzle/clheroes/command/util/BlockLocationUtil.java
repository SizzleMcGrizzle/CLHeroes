package me.sizzlemcgrizzle.clheroes.command.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockLocationUtil {

	public static boolean isBlockLocation(Location location) throws IOException, InvalidConfigurationException {
		File file = new File(SimplePlugin.getInstance().getDataFolder() + File.separator + "locations.yml");
		YamlConfiguration config = new YamlConfiguration();
		List<Location> locationList = new ArrayList<>();

		config.load(file);

		for (String masterKey : config.getKeys(false))
			for (String numberKey : config.getConfigurationSection(masterKey).getKeys(false))
				for (String key : config.getConfigurationSection(masterKey).getConfigurationSection(numberKey).getKeys(false)) {
					String[] parsedLocation = config.getConfigurationSection(masterKey).getConfigurationSection(numberKey).getString(key).split("&====&");
					if (parsedLocation.length < 4)
						continue;
					locationList.add(new Location(Bukkit.getWorld(parsedLocation[3]),
							Double.parseDouble(parsedLocation[0]),
							Double.parseDouble(parsedLocation[1]),
							Double.parseDouble(parsedLocation[2])));
				}

		for (Location loc : locationList) {
			if (location.equals(loc))
				return true;
		}

		return false;
	}

	public static void removeBlockLocation(Location location) throws IOException, InvalidConfigurationException {
		File file = new File(SimplePlugin.getInstance().getDataFolder() + File.separator + "locations.yml");
		YamlConfiguration config = new YamlConfiguration();

		if (!file.exists())
			FileUtil.extract("locations.yml");

		config.load(file);

		for (String masterKey : config.getKeys(false))
			for (String numberKey : config.getConfigurationSection(masterKey).getKeys(false))
				for (String key : config.getConfigurationSection(masterKey).getConfigurationSection(numberKey).getKeys(false)) {
					String[] parsedLocation = config.getConfigurationSection(masterKey).getConfigurationSection(numberKey).getString(key).split("&====&");
					if (parsedLocation.length < 4)
						continue;
					if (location.equals(new Location(Bukkit.getWorld(parsedLocation[3]),
							Double.parseDouble(parsedLocation[0]),
							Double.parseDouble(parsedLocation[1]),
							Double.parseDouble(parsedLocation[2]))))
						config.getConfigurationSection(masterKey).getConfigurationSection(numberKey).set(key, "");
				}

		config.save(file);
	}
}
