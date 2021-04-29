/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.eventhandler;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import io.bottone.mc.plugins.mrc.MRC;

public class VehicleEventHandler implements Listener {

	private final MRC plugin;

	public VehicleEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onRightClickEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.BOAT
				&& event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) { // FIXME Look into improving this (low priority)
		if (event.getVehicle().isDead() || !event.getVehicle().isValid())
			return;
		if (plugin.killedBoats.contains(event.getVehicle().getUniqueId())) {
			event.getVehicle().remove();
			return;
		}

		if (event.getExited() instanceof Player && ((Player) event.getExited()).getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
			event.getVehicle().addPassenger(event.getExited());
		}
	}

	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (event.getAttacker() instanceof HumanEntity
				&& ((HumanEntity) event.getAttacker()).getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		if (event.getAttacker() instanceof HumanEntity
				&& ((HumanEntity) event.getAttacker()).getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

}
