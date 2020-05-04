package me.sizzlemcgrizzle.clheroes.runnables;

import me.sizzlemcgrizzle.clheroes.command.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Skull;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;

import java.util.UUID;

public class ApplyHeadRunnable extends BukkitRunnable {
	Location location;
	UUID uuid;
	
	ApplyHeadRunnable(Location location, UUID uuid) {
		this.location = location;
		this.uuid = uuid;
	}
	
	@Override
	public void run() {
		Skull skull = (Skull) location.getBlock().getState();
		skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		skull.update();
		if (Settings.DEBUG_MESSAGES)
			Common.log("Setting head, with player " + Bukkit.getOfflinePlayer(uuid).getName());
	}
}
