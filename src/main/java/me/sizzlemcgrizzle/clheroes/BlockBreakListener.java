package me.sizzlemcgrizzle.clheroes;

import me.sizzlemcgrizzle.clheroes.command.util.BlockLocationUtil;
import me.sizzlemcgrizzle.clheroes.command.util.MaterialUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.io.IOException;

public class BlockBreakListener implements Listener {


	@EventHandler(ignoreCancelled = true)
	public void onBreakEvent(BlockBreakEvent event) throws IOException, InvalidConfigurationException {
		Material material = event.getBlock().getType();
		Location location = event.getBlock().getLocation();

		if (!MaterialUtil.isBanner(material) && !MaterialUtil.isSign(material) && !MaterialUtil.isHead(material))
			return;

		if (BlockLocationUtil.isBlockLocation(location)) {
			BlockLocationUtil.removeBlockLocation(location);
		}


	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onExplosion(EntityExplodeEvent event) throws IOException, InvalidConfigurationException {
		for (Block block : event.blockList()) {
			if (BlockLocationUtil.isBlockLocation(block.getLocation())) {
				event.setCancelled(true);
				return;
			}
		}

	}
}
