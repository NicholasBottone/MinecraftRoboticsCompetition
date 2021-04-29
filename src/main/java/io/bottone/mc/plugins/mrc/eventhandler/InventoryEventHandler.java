/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.eventhandler;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class InventoryEventHandler implements Listener {

	private final MRC plugin;

	public InventoryEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);

		if (plugin.gameState != GameState.INGAME)
			return;

		if (!plugin.arena.canPickupArrow(player))
			return;

		if (event.getInventory().contains(Material.RED_WOOL) && plugin.redPlayers.contains(player)
				&& plugin.redBay > 0) {
			plugin.redBay--;
			if (plugin.playerClasses.get(player) == PlayerClass.SNOWBALL)
				plugin.arena.givePowerCells(player, 1, Material.SNOWBALL);
			else
				plugin.arena.givePowerCells(player, 1, Material.ARROW);
		}

		if (event.getInventory().contains(Material.BLUE_WOOL) && plugin.bluePlayers.contains(player)
				&& plugin.blueBay > 0) {
			plugin.blueBay--;
			if (plugin.playerClasses.get(player) == PlayerClass.SNOWBALL)
				plugin.arena.givePowerCells(player, 1, Material.SNOWBALL);
			else
				plugin.arena.givePowerCells(player, 1, Material.ARROW);
		}

		plugin.redBayLine.setText(plugin.redBay + " Power Cells");
		plugin.blueBayLine.setText(plugin.blueBay + " Power Cells");
	}

	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onSwapHands(PlayerSwapHandItemsEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

}
