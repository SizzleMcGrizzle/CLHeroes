package me.sizzlemcgrizzle.clheroes.command;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.settings.SimpleLocalization;

public class CLHeroesReloadCommand extends SimpleSubCommand {
	protected CLHeroesReloadCommand(final SimpleCommandGroup parent) {
		super(parent, "reload");
		setPermission("clheroes.reload");
	}

	@Override
	protected void onCommand() {
		try {
			SimplePlugin.getInstance().reload();
			tell(Settings.PREFIX + "&aPlugin has been successfully reloaded.");
		} catch (Throwable t) {
			t.printStackTrace();

			tell(SimpleLocalization.Commands.RELOAD_FAIL.replace("{error}", t.toString()));

		}
	}
}
