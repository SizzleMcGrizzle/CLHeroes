package me.sizzlemcgrizzle.clheroes;

import org.bukkit.event.Listener;

public class BlockBreakListener implements Listener {


	/*@EventHandler(ignoreCancelled = true)
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

	}*/
}
