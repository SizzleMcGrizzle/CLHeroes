package me.sizzlemcgrizzle.clheroes;

import me.sizzlemcgrizzle.clheroes.command.CLHeroesCommand;
import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import me.sizzlemcgrizzle.clheroes.runnables.BaltopCalculateRunnable;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.YamlStaticConfig;

import java.util.Arrays;
import java.util.List;

public class CLHeroesPlugin extends SimplePlugin {
	@Override
	protected void onPluginStart() {
		registerCommands("heroes", new CLHeroesCommand());

		registerEvents(new BlockBreakListener());

		new BaltopCalculateRunnable().runTaskTimerAsynchronously(this, 0, Settings.DELAY);
	}

	@Override
	protected void onPluginReload() {
		new BaltopCalculateRunnable().runTaskTimerAsynchronously(this, 0, Settings.DELAY);
	}

	@Override
	public List<Class<? extends YamlStaticConfig>> getSettings() {
		return Arrays.asList(Settings.class);
	}
}
