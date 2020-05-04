package me.sizzlemcgrizzle.clheroes.command;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import me.sizzlemcgrizzle.clheroes.command.util.MaterialUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CLHeroesCleanLocationsCommand extends SimpleSubCommand {
	private File file = new File(SimplePlugin.getInstance().getDataFolder(), "locations.yml");
	
	protected CLHeroesCleanLocationsCommand(final SimpleCommandGroup parent) {
		super(parent, "cleanLocations");
		setPermission("clheroes.cleanlocations");
	}
	
	@Override
	protected void onCommand() {
		Runnable runnable = () -> {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			
			for (String dataType : config.getKeys(false)) {
				ConfigurationSection dataSection = config.getConfigurationSection(dataType);
				for (String number : dataSection.getKeys(false)) {
					ConfigurationSection numberSection = dataSection.getConfigurationSection(number);
					for (String locationKey : numberSection.getKeys(false)) {
						List<Location> list = (List<Location>) numberSection.getList(locationKey);
						list.removeIf(location -> !MaterialUtil.isHead(location.getBlock().getType())
								&& !MaterialUtil.isBanner(location.getBlock().getType())
								&& !MaterialUtil.isSign(location.getBlock().getType()));
						numberSection.set(locationKey, list);
					}
				}
			}
			
			try {
				config.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		tell(Settings.PREFIX + "&aCleaning up &2locations.yml &afile, please wait...");
		Common.runLaterAsync(1, runnable);
	}
}
