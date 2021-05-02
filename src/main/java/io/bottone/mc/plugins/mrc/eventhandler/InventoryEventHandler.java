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
import org.bukkit.Sound;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Objects;

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

		if (event.getInventory().getHolder() instanceof Dispenser ||
				event.getInventory().getHolder() instanceof Dropper) {
			takeCellFromLoadingBay(event, player);
			return;
		}

		if (event.getInventory().getHolder() instanceof ShulkerBox) {
			dumpInLowGoal(event, player);
		}

	}

	private void dumpInLowGoal(InventoryOpenEvent event, Player player) {

		int powerCells = plugin.arena.getPowerCellCount(player);

		if (event.getInventory().contains(Material.RED_WOOL) && powerCells > 0) {
			// Score power cells in the low goal for the red alliance
			for (int i = 0; i < powerCells; i++) {
				if (plugin.redPlayers.contains(player)) {
					plugin.playerData.get(player).addLower(plugin.countdown > 135);
				}

				plugin.redScore += (plugin.countdown > 135) ? 2 : 1;
				plugin.world.playSound(Objects.requireNonNull(event.getInventory().getLocation()),
						Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 100, 1);
				plugin.redPC++;

				if (plugin.blueBay < 15) {
					plugin.blueBay++;
				} else {
					plugin.arena.spawnRandomPC();
				}
			}
		}

		if (event.getInventory().contains(Material.BLUE_WOOL) && powerCells > 0) {
			// Score power cells in the low goal for the blue alliance
			for (int i = 0; i < powerCells; i++) {
				if (plugin.bluePlayers.contains(player)) {
					plugin.playerData.get(player).addLower(plugin.countdown > 135);
				}

				plugin.blueScore += (plugin.countdown > 135) ? 2 : 1;
				plugin.world.playSound(Objects.requireNonNull(event.getInventory().getLocation()),
						Sound.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, 100, 1);
				plugin.bluePC++;

				if (plugin.redBay < 15) {
					plugin.redBay++;
				} else {
					plugin.arena.spawnRandomPC();
				}
			}
		}

		// Remove power cells from inventory
		plugin.arena.clearPowerCellsFromPlayer(player);

	}

	private void takeCellFromLoadingBay(InventoryOpenEvent event, Player player) {

		if (!plugin.arena.canPickupPowerCell(player))
			return;

		if (event.getInventory().contains(Material.RED_WOOL) && plugin.redPlayers.contains(player)
				&& plugin.redBay > 0) {
			plugin.redBay--;
			if (plugin.playerClasses.get(player) == PlayerClass.SNOWBALL)
				plugin.arena.givePowerCells(player, 1, Material.SNOWBALL);
			else
				plugin.arena.givePowerCells(player, 1, Material.ARROW);

			plugin.redBayLine.setText(plugin.redBay + " Power Cells");
		}

		if (event.getInventory().contains(Material.BLUE_WOOL) && plugin.bluePlayers.contains(player)
				&& plugin.blueBay > 0) {
			plugin.blueBay--;
			if (plugin.playerClasses.get(player) == PlayerClass.SNOWBALL) {
				plugin.arena.givePowerCells(player, 1, Material.SNOWBALL);
			}
			else {
				plugin.arena.givePowerCells(player, 1, Material.ARROW);
			}

			plugin.blueBayLine.setText(plugin.blueBay + " Power Cells");
		}

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
