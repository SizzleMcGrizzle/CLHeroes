package me.sizzlemcgrizzle.clheroes.command;

import org.mineacademy.fo.command.SimpleCommandGroup;

public class CLHeroesCommand extends SimpleCommandGroup {
	@Override
	protected void registerSubcommands() {
		registerSubcommand(new CLHeroesAddLocationCommand(this));
		registerSubcommand(new CLHeroesReloadCommand(this));
		registerSubcommand(new CLHeroesConfigCommand(this));
		registerSubcommand(new CLHeroesRefreshCommand(this));
		registerSubcommand(new CLHeroesCleanLocationsCommand(this));
	}
	
	@Override
	protected String getCredits() {
		return "";
	}
	
	@Override
	protected String getHeaderPrefix() {
		return "&d&l";
	}
}
