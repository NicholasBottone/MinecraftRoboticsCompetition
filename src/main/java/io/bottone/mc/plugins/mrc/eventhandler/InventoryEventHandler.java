package io.bottone.mc.plugins.mrc.eventhandler;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.enums.PlayerClass;

public class InventoryEventHandler implements Listener {

	private MRC plugin;

	public InventoryEventHandler(MRC plugin) {

		this.plugin = plugin;

	}

	@EventHandler
	public void onInventoryEvent(InventoryClickEvent event) {
		if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			return;

		event.setCancelled(true);

		int arrows = 0;
		for (ItemStack item : event.getPlayer().getInventory().getContents()) {
			if (item != null && (item.getType() == Material.ARROW || item.getType() == Material.SNOWBALL))
				arrows += item.getAmount();
		}

		int maxArrows = 5;
		switch (plugin.playerClasses.get(event.getPlayer())) {
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
			return;
		}

		if (event.getInventory().contains(Material.RED_WOOL) && plugin.redPlayers.contains(event.getPlayer())
				&& plugin.redBay > 0) {
			plugin.redBay--;
			if (plugin.playerClasses.get(event.getPlayer()) == PlayerClass.SNOWBALL)
				plugin.arena.givePowerCells(event.getPlayer(), 1, Material.SNOWBALL);
			else
				plugin.arena.givePowerCells(event.getPlayer(), 1, Material.ARROW);
		}

		if (event.getInventory().contains(Material.BLUE_WOOL) && plugin.bluePlayers.contains(event.getPlayer())
				&& plugin.blueBay > 0) {
			plugin.blueBay--;
			if (plugin.playerClasses.get(event.getPlayer()) == PlayerClass.SNOWBALL)
				plugin.arena.givePowerCells(event.getPlayer(), 1, Material.SNOWBALL);
			else
				plugin.arena.givePowerCells(event.getPlayer(), 1, Material.ARROW);
		}

		plugin.redBayLine.setText(plugin.redBay + " Power Cells");
		plugin.blueBayLine.setText(plugin.blueBay + " Power Cells");
	}

	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

}
