package me.sizzlemcgrizzle.clheroes.runnables;

import de.craftlancer.clclans.CLClans;
import de.craftlancer.clclans.Clan;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.plugin.SimplePlugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClanCalculateRunnable extends BukkitRunnable {
	private CLClans clans = (CLClans) Bukkit.getPluginManager().getPlugin("CLClans");
	private List<Clan> topClans = new ArrayList<>();
	
	@Override
	public void run() {
		clans.getClans().stream().sorted(Comparator.comparingDouble(Clan::calculateClanScore).reversed()).limit(3).forEachOrdered(topClans::add);
		new ClanApplyRunnable(topClans).runTask(SimplePlugin.getInstance());
	}
}
