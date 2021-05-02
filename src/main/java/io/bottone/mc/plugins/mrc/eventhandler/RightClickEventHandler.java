/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.eventhandler;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class RightClickEventHandler implements Listener {

	private final MRC plugin;

	public RightClickEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {

		Player player = event.getPlayer();

		if (event.getMaterial() == Material.SNOWBALL && !player.isInsideVehicle()) {
			event.setCancelled(true);
			return;
		}

		if (event.getMaterial() == Material.IRON_DOOR) {
			// Send back to hub bungee server
			plugin.sendToBungeeServer(player, "Hub");
			return;
		}

		if (event.getMaterial() == Material.ACACIA_DOOR) {
			if (plugin.gameState != GameState.INGAME)
				return;
			// Dismount from boat to begin climb
			beginClimbing(player);
			return;
		}

		if (event.getClickedBlock() != null) {

			if (event.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
				handleSignClick(event, player);
				return;
			}

			if (event.getClickedBlock().getType() == Material.BELL) {
				checkForClimb(event, player);
			}

		}
	}

	private void beginClimbing(Player player) {
		Entity vehicle = player.getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}
		player.getInventory().remove(Material.ACACIA_DOOR);
		player.getInventory().remove(Material.BOW);
		player.getInventory().remove(Material.CROSSBOW);
		switch (plugin.playerClasses.get(player)) {
			case INSTACLIMB:
				// Instant hang, award points for hang
				awardPointsForHang(player);
				break;
			case TRIDENT:
				// Enchant all tridents with riptide
				for (ItemStack item : player.getInventory().getContents()) {
					//noinspection ConstantConditions
					if (item != null && (item.getType() == Material.ARROW || item.getType() == Material.SNOWBALL)
							|| item.getType() == Material.TRIDENT)
						item.addEnchantment(Enchantment.RIPTIDE, 1);
				}
				break;
		}
	}

	private void handleSignClick(PlayerInteractEvent event, Player player) {
		if (Objects.equals(event.getClickedBlock(), plugin.redLeftSign.getBlock())) {
			player.performCommand("pos redleft");
		} else if (Objects.equals(event.getClickedBlock(), plugin.redCenterSign.getBlock())) {
			player.performCommand("pos redcenter");
		} else if (Objects.equals(event.getClickedBlock(), plugin.redRightSign.getBlock())) {
			player.performCommand("pos redright");
		} else if (Objects.equals(event.getClickedBlock(), plugin.blueLeftSign.getBlock())) {
			player.performCommand("pos blueleft");
		} else if (Objects.equals(event.getClickedBlock(), plugin.blueCenterSign.getBlock())) {
			player.performCommand("pos bluecenter");
		} else if (Objects.equals(event.getClickedBlock(), plugin.blueRightSign.getBlock())) {
			player.performCommand("pos blueright");
		}
	}

	private void checkForClimb(PlayerInteractEvent event, Player player) {
		if (plugin.gameState != GameState.INGAME)
			return;

		if (plugin.redPlayers.contains(player)) {
			// if they are on the red alliance
			Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
			loc.setY(loc.getY() + 1);
			if (loc.getBlock().getType() != Material.RED_CONCRETE)
				return;
		} else if (plugin.bluePlayers.contains(player)) {
			// if they are on the blue alliance
			Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
			loc.setY(loc.getY() + 1);
			if (loc.getBlock().getType() != Material.BLUE_CONCRETE)
				return;
		} else {
			return;
		}

		// Fully hung, award points for hang
		awardPointsForHang(player);
	}

	private void awardPointsForHang(Player player) {
		if (!plugin.hungPlayers.contains(player)) {
			plugin.playerData.get(player).addPoints(20);
			plugin.hungPlayers.add(player);
			if (plugin.redPlayers.contains(player)) {
				plugin.redScore += 20;
				plugin.redEndgame += 20;
			} else if (plugin.bluePlayers.contains(player)) {
				plugin.blueScore += 20;
				plugin.blueEndgame += 20;
			}
			player.sendMessage(MRC.PREFIX + "You have hung.");
		}
	}

}
