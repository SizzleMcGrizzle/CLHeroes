package me.sizzlemcgrizzle.clheroes.command;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CLHeroesConfigCommand extends SimpleSubCommand {
	protected CLHeroesConfigCommand(final SimpleCommandGroup parent) {
		super(parent, "config");
		setMinArguments(1);
		setPermission("clheroes.config");
	}

	@Override
	protected List<String> tabComplete() {
		if (args.length == 1)
			return completeLastWord(Arrays.asList("setprefix", "setdelay"));
		return new ArrayList<>();
	}

	@Override
	protected void onCommand() {
		File file = new File(SimplePlugin.getInstance().getDataFolder() + File.separator + "settings.yml");
		YamlConfiguration config = new YamlConfiguration();

		if (!file.exists()) {
			FileUtil.extract("settings.yml");
		}

		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		if (args[0].equalsIgnoreCase("setprefix")) {
			if (args.length < 2) {
				tell(Settings.PREFIX + "&cYou didn't input a prefix!");
				return;
			}
			config.set("Prefix", args[1] + " ");
			tell(Settings.PREFIX + "&aPrefix has been updated. Do &2/heroes reload&a to apply changes.");
		} else if (args[0].equalsIgnoreCase("setdelay")) {
			if (args.length < 2) {
				tell(Settings.PREFIX + "&cYou didn't input a delay!");
				return;
			}
			config.set("Delay", args[1]);
			tell(Settings.PREFIX + "&aDelay has been updated. Do &2/heroes reload&a to apply changes.");
		} else
			tell(Settings.PREFIX + "&cYou must enter &e'setprefix'&c or &e'setdelay'&c!");
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
