package me.sizzlemcgrizzle.clheroes.command.settings;

import org.mineacademy.fo.settings.SimpleSettings;

public class Settings extends SimpleSettings {
	@Override
	protected int getConfigVersion() {
		return 0;
	}

	@Override
	protected String[] getHeader() {
		String[] header = new String[6];
		header[0] = " ------------------------------------------------------------------------------------------- #";
		header[1] = "                                                                                             #";
		header[2] = " Welcome to the settings file for CLHeroes.                                                  #";
		header[3] = " For documentation, ask Sizzle!                                                              #";
		header[4] = "                                                                                             #";
		header[5] = " ------------------------------------------------------------------------------------------- #";
		return header;
	}

	public static String PREFIX;
	public static Integer DELAY;
	public static Boolean DEBUG_MESSAGES;

	private static void init() {
		pathPrefix(null);
		PREFIX = getString("Prefix");
		DELAY = Integer.valueOf(getString("Delay"));
		DEBUG_MESSAGES = getBoolean("Debug_Messages");
	}


}
