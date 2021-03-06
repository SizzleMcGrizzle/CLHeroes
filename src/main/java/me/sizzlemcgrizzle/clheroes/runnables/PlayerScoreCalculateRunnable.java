package me.sizzlemcgrizzle.clheroes.runnables;

import de.craftlancer.clstuff.CLStuff;
import de.craftlancer.core.util.Tuple;
import me.sizzlemcgrizzle.clheroes.CLHeroesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerScoreCalculateRunnable extends BukkitRunnable {
	private CLStuff clStuff = (CLStuff) Bukkit.getPluginManager().getPlugin("CLStuff");
	
	@Override
	public void run() {
		
		List<Tuple<UUID, Integer>> entries = clStuff.getRankings().updateScores().entrySet().stream()
				.map(a -> new Tuple<>(a.getKey(), a.getValue().getScore()))
				.sorted(Comparator.comparingInt(Tuple<UUID, Integer>::getValue).reversed()).limit(3).collect(Collectors.toList());
		
		
		new PlayerScoreApplyRunnable(entries).runTask(CLHeroesPlugin.getInstance());
	}
	
}
