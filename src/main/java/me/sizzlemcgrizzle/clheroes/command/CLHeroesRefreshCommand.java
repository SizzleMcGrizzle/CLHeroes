package me.sizzlemcgrizzle.clheroes.command;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import me.sizzlemcgrizzle.clheroes.runnables.BaltopCalculateRunnable;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.plugin.SimplePlugin;

public class CLHeroesRefreshCommand extends SimpleSubCommand {
	protected CLHeroesRefreshCommand(final SimpleCommandGroup parent) {
		super(parent, "refreshdisplays");
		setPermission("clheroes.refreshdisplays");
	}

	@Override
	protected void onCommand() {
		new BaltopCalculateRunnable().runTaskAsynchronously(SimplePlugin.getInstance());
		tell(Settings.PREFIX + "&aRefreshing displays... please wait.");
	}
}
