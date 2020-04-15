package me.sizzlemcgrizzle.clheroes.runnables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public abstract class HeroRunnable extends BukkitRunnable {

	public abstract void doFunction() throws IOException, InvalidConfigurationException;

	protected static Location parseLocation(String string) {
		double x, y, z;
		World world;

		if (string == null || !string.contains("&====&"))
			return null;
		String[] array = string.split("&====&");
		if (array.length < 4)
			return null;
		x = Double.parseDouble(array[0]);
		y = Double.parseDouble(array[1]);
		z = Double.parseDouble(array[2]);
		world = Bukkit.getWorld(array[3]);

		return new Location(world, x, y, z);
	}

}
