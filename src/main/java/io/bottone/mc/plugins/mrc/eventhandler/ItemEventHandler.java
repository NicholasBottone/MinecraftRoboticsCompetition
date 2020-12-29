/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.eventhandler;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;

public class ItemEventHandler implements Listener {

	private MRC plugin;

	public ItemEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPickupArrow(PlayerPickupArrowEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPickupItem(EntityPickupItemEvent event) {
		if (event.getItem().getItemStack().getType() != Material.ARROW || !(event.getEntity() instanceof HumanEntity))
			return;

		int arrows = 0;
		for (ItemStack item : ((HumanEntity) event.getEntity()).getInventory().getContents()) {
			if (item != null && (item.getType() == Material.ARROW || item.getType() == Material.SNOWBALL))
				arrows += item.getAmount();
		}

		int maxArrows = 5;
		switch (plugin.playerClasses.get(event.getEntity())) {
		case BOW:
		case CROSSBOW:
			maxArrows = 5;
			break;
		case SNOWBALL:
			maxArrows = 3;
			break;
		case INSTACLIMB:
			maxArrows = 4;
			break;
		}

		if (arrows >= maxArrows) {
			event.setCancelled(true);
		} else if (plugin.playerClasses.get(event.getEntity()) == PlayerClass.SNOWBALL) {
			event.setCancelled(true);
			plugin.arena.givePowerCells((HumanEntity) event.getEntity(), event.getItem().getItemStack().getAmount(),
					Material.SNOWBALL);
			event.getItem().remove();
		}

	}

}
