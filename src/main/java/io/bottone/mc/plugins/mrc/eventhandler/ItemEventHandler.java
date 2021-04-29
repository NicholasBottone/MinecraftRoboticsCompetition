/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.eventhandler;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class ItemEventHandler implements Listener {

	private final MRC plugin;

	public ItemEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPickupArrow(PlayerPickupArrowEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPickupItem(EntityPickupItemEvent event) {
		if (event.getItem().getItemStack().getType() != Material.ARROW || !(event.getEntity() instanceof HumanEntity))
			return;

		Player player = (Player) event.getEntity();

		if (!plugin.arena.canPickupArrow(player)) {
			event.setCancelled(true);
		} else if (plugin.playerClasses.get(player) == PlayerClass.SNOWBALL) {
			event.setCancelled(true);
			plugin.arena.givePowerCells(player, event.getItem().getItemStack().getAmount(),
					Material.SNOWBALL);
			event.getItem().remove();
		}

	}

}
