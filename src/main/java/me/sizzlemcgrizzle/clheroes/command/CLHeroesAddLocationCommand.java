package me.sizzlemcgrizzle.clheroes.command;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import me.sizzlemcgrizzle.clheroes.command.util.MaterialUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CLHeroesAddLocationCommand extends SimpleSubCommand {
	protected CLHeroesAddLocationCommand(final SimpleCommandGroup parent) {
		super(parent, "addlocation");
		setPermission("clheroes.addlocation");
		setMinArguments(3);
	}

	@Override
	protected List<String> tabComplete() {
		if (args.length == 1)
			return completeLastWord(Arrays.asList("sign", "head", "banner"));
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("head"))
				return completeLastWord("baltop");
			else if (args[0].equalsIgnoreCase("banner"))
				return completeLastWord("clan");
			else
				return completeLastWord(Arrays.asList("clan", "baltop"));
		}
		if (args.length == 3)
			return Arrays.asList("1", "2", "3");
		return new ArrayList<>();
	}

	@Override
	protected void onCommand() {
		File file = new File(SimplePlugin.getInstance().getDataFolder() + File.separator + "locations.yml");
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection configSection;

		try {
			if (!file.exists())
				file.createNewFile();
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		Player player = getPlayer();
		Location blockLocation = player.getTargetBlock(null, 5).getLocation();
		Material material = blockLocation.getBlock().getType();

		String location = blockLocation.getX() + "&====&" + blockLocation.getY() + "&====&" + blockLocation.getZ() + "&====&" + blockLocation.getWorld().getName();

		if (args[0].equalsIgnoreCase("Sign")) {
			if (!MaterialUtil.isSign(material)) {
				tell(Settings.PREFIX + "&cYou are not looking at a sign!");
				return;
			}
			configSection = config.getConfigurationSection(args[1].toLowerCase()).getConfigurationSection(args[2]);
			configSection.set("sign_location", location);
			tell(Settings.PREFIX + "&aLocation has been set.");
		} else if (args[0].equalsIgnoreCase("Head")) {
			if (!MaterialUtil.isHead(material)) {
				tell(Settings.PREFIX + "&cYou are not looking at a head!");
				return;
			}
			configSection = config.getConfigurationSection(args[1].toLowerCase()).getConfigurationSection(args[2]);
			configSection.set("head_location", location);
			tell(Settings.PREFIX + "&aLocation has been set.");
		} else if (args[0].equalsIgnoreCase("Banner")) {
			if (!MaterialUtil.isBanner(material)) {
				tell(Settings.PREFIX + "&cYou are not looking at a banner!");
				return;
			}
			configSection = config.getConfigurationSection(args[1].toLowerCase()).getConfigurationSection(args[2]);
			configSection.set("banner_location", location);
			tell(Settings.PREFIX + "&aLocation has been set.");
		} else
			tell(Settings.PREFIX + "&cYou must enter &e'banner'&c, &e'sign'&c, or &e'head'&c in the first argument!");
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
