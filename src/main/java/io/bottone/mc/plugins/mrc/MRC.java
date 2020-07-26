package io.bottone.mc.plugins.mrc;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class MRC extends JavaPlugin implements Listener {

	public static enum GameState {
		LOBBY, COUNTDOWN, INGAME, FINISHED
	}

	public static final String PREFIX = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MRC"
			+ ChatColor.DARK_PURPLE + "] " + ChatColor.AQUA;

	private Logger l;

	private Location stadium;
	private Location red1;
	private Location red2;
	private Location red3;
	private Location blue1;
	private Location blue2;
	private Location blue3;

	private List<Player> players = new ArrayList<>();
	private List<Player> spectators = new ArrayList<>();

	private List<Player> redPlayers = new ArrayList<>();
	private List<Player> bluePlayers = new ArrayList<>();

	private GameState gameState = GameState.LOBBY;
	private int countdown = 30;
	private boolean joinable = true;

	private int redScore = 0;
	private int redPC = 0;
	private int redEndgame = 0;

	private int blueScore = 0;
	private int bluePC = 0;
	private int blueEndgame = 0;

//	private Economy econ;

	private MRC plugin;

	@Override
	public void onEnable() {
		l = getLogger();
		plugin = this;

		/*
		 * // Check for Vault
		 *
		 * if (getServer().getPluginManager().getPlugin("Vault") == null) {
		 * l.log(Level.SEVERE, "Unable to hook with Vault, disabling MRC!");
		 * getServer().getPluginManager().disablePlugin(this); return; }
		 *
		 * // Hook Vault economy
		 *
		 * RegisteredServiceProvider<Economy> rspe =
		 * getServer().getServicesManager().getRegistration(Economy.class); econ =
		 * rspe.getProvider(); if (econ == null) { l.log(Level.SEVERE,
		 * "Unable to hook with Vault economy, disabling MRC!");
		 * getServer().getPluginManager().disablePlugin(this); return; } else
		 * l.log(Level.INFO, "MRC successfully hooked to Vault economy " +
		 * econ.getName());
		 */

		// GAME TICK (every second)
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {

				switch (gameState) {

				case LOBBY:
					// Lobby normally means no players.
					joinable = true;

					if (players.size() >= 1) {
						// We have player(s) ... start the countdown!
						gameState = GameState.COUNTDOWN;
						countdown = 30;

						redScore = 0;
						redPC = 0;
						redEndgame = 0;
						blueScore = 0;
						bluePC = 0;
						blueEndgame = 0;

						getServer().broadcastMessage(PREFIX + ChatColor.BOLD + "Match starting in 30 seconds!");
					}
					break;

				case COUNTDOWN:
					// Pre-game state, starting soon.

					if (players.size() < 1) {
						// We don't have enough players ... abort the countdown!
						gameState = GameState.LOBBY;
						countdown = 30;
						joinable = true;

						getServer().broadcastMessage(PREFIX + "Match countdown aborted due to lack of players.");
						break;
					}
					if (countdown <= 0 && joinable) {
						// Game no longer joinable, assigning teams and moving to the arena.
						joinable = false;
						countdown = 10;

						boolean red = true;
						int position = 1;
						for (Player player : players) {
							// Give players their bows
							ItemStack bow = new ItemStack(Material.BOW, 1);
							ItemMeta bowMeta = bow.getItemMeta();
							bowMeta.setUnbreakable(true);
							bow.setItemMeta(bowMeta);
							player.getInventory().addItem(bow);

							// Teleport players to their positions
							switch (position) {
							case 1:
								if (red) {
									player.teleport(red1);
									redPlayers.add(player);
								} else {
									player.teleport(blue1);
									bluePlayers.add(player);
								}
								break;
							case 2:
								if (red) {
									player.teleport(red2);
									redPlayers.add(player);
								} else {
									player.teleport(blue2);
									bluePlayers.add(player);
								}
								break;
							case 3:
								if (red) {
									player.teleport(red3);
									redPlayers.add(player);
								} else {
									player.teleport(blue3);
									bluePlayers.add(player);
								}
								break;
							}

							red = !red;
							if (red)
								position++;
						}

					}
					if (countdown > 0 && !joinable) {
						// Final countdown.
						// Show title
						showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + countdown, "");
					}
					if (countdown <= 0 && !joinable) {
						// Match starts.
						// Show title
						showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "GO",
								ChatColor.LIGHT_PURPLE + "Good luck!");

						// Time to start the match!
						gameState = GameState.INGAME;
						countdown = 150;
						getServer().broadcastMessage(PREFIX + "Let the match begin!");

						int position = 1;
						for (Player player : redPlayers) {
							// Give players their 3 starting arrows
							player.getInventory().addItem(new ItemStack(Material.ARROW, 3));

							// Give players their colored armor
							ItemStack armor = new ItemStack(Material.LEATHER_BOOTS);
							LeatherArmorMeta lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.RED);
							armor.setItemMeta(lameta);
							player.getInventory().setBoots(armor);

							armor = new ItemStack(Material.LEATHER_LEGGINGS);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.RED);
							armor.setItemMeta(lameta);
							player.getInventory().setLeggings(armor);

							armor = new ItemStack(Material.LEATHER_HELMET);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.RED);
							armor.setItemMeta(lameta);
							player.getInventory().setHelmet(armor);

							armor = new ItemStack(Material.LEATHER_CHESTPLATE);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.RED);
							armor.setItemMeta(lameta);
							player.getInventory().setChestplate(armor);

							// Teleport players to their positions
							switch (position) {
							case 1:
								player.teleport(red1);
								redPlayers.add(player);
								break;
							case 2:
								player.teleport(red2);
								redPlayers.add(player);
								break;
							case 3:
								player.teleport(red3);
								redPlayers.add(player);
								break;
							}
							position++;
						}

						position = 1;
						for (Player player : bluePlayers) {
							// Give players their 3 starting arrows
							player.getInventory().addItem(new ItemStack(Material.ARROW, 3));

							// Give players their colored armor
							ItemStack armor = new ItemStack(Material.LEATHER_BOOTS);
							LeatherArmorMeta lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.BLUE);
							armor.setItemMeta(lameta);
							player.getInventory().setBoots(armor);

							armor = new ItemStack(Material.LEATHER_LEGGINGS);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.BLUE);
							armor.setItemMeta(lameta);
							player.getInventory().setLeggings(armor);

							armor = new ItemStack(Material.LEATHER_HELMET);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.BLUE);
							armor.setItemMeta(lameta);
							player.getInventory().setHelmet(armor);

							armor = new ItemStack(Material.LEATHER_CHESTPLATE);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(Color.BLUE);
							armor.setItemMeta(lameta);
							player.getInventory().setChestplate(armor);

							// Teleport players to their positions
							switch (position) {
							case 1:
								player.teleport(blue1);
								redPlayers.add(player);
								break;
							case 2:
								player.teleport(blue2);
								redPlayers.add(player);
								break;
							case 3:
								player.teleport(blue3);
								redPlayers.add(player);
								break;
							}
							position++;
						}

						break;
					}

					countdown--;

					break;

				case INGAME:
					joinable = false;

					if (countdown <= 0) {
						// Match is over.
						gameState = GameState.FINISHED;
						countdown = 10;

						for (

						Player player : players) {
							player.getInventory().clear();
						}

						if (redScore > blueScore) {
							getServer().broadcastMessage(
									PREFIX + ChatColor.RED.toString() + ChatColor.BOLD + "RED ALLIANCE WINS!");
							showTitle(ChatColor.RED.toString() + ChatColor.BOLD + "RED ALLIANCE WINS!", "");
						} else if (blueScore > redScore) {
							getServer().broadcastMessage(
									PREFIX + ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE ALLIANCE WINS!");
							showTitle(ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE ALLIANCE WINS!", "");
						} else {
							getServer().broadcastMessage(
									PREFIX + ChatColor.WHITE.toString() + ChatColor.BOLD + "IT'S A TIE!");
							showTitle(ChatColor.WHITE.toString() + ChatColor.BOLD + "IT'S A TIE!", "");
						}

						getServer().broadcastMessage(PREFIX + "Final Score: " + ChatColor.RED + ChatColor.BOLD
								+ redScore + ChatColor.AQUA + " to " + ChatColor.BLUE + ChatColor.BOLD + blueScore);

					}

					// TODO: actual game tick goes here
					countdown--;
					break;

				case FINISHED:
					joinable = false;

					if (countdown <= 0) {

						for (Player player : players) {
							player.chat("/hub");
						}

						for (Player player : spectators) {
							player.chat("/hub");
						}

						gameState = GameState.LOBBY;
						joinable = true;
					}

					countdown--;
					break;

				}

				// Update scoreboards for all players and spectators
				updateScoreboards();

			}
		}, 100, 20);

		// waits for worlds to load before setting locations
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				stadium = new Location(getServer().getWorld("MRC"), -0.5, 35.5, -1.5, 0, 0); // TODO get coords
				red1 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				red2 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				red3 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				blue1 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				blue2 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);
				blue3 = new Location(getServer().getWorld("MRC"), 6, 14, -6, 90, 90);

				getServer().getPluginManager().registerEvents(plugin, plugin);
				l.log(Level.INFO, "Locations loaded and MRC activated.");
			}

		}, 200);

	}

	private void showInstantTitle(String title, String subtitle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, 0, 20, 0);
		}
	}

	private void showTitle(String title, String subtitle) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, 20, 100, 20);
		}
	}

	private void updateScoreboards() {

		MRCScoreboard sb = new MRCScoreboard(PREFIX + gameState.toString());

		switch (gameState) {

		case LOBBY:
			sb.put(2, "Waiting to start the game!");
			sb.put(1, players.size() + " players");
			sb.put(0, "");
			sb.put(-1, ChatColor.GREEN + "mc.bottone.io");
			break;

		case COUNTDOWN:
			if (joinable) {
				sb.put(2, "Match initiating in " + countdown);
			} else {
				sb.put(2, ChatColor.BOLD + "Here we go in " + countdown);
			}
			sb.put(1, players.size() + " players");
			sb.put(0, "");
			sb.put(-1, ChatColor.GREEN + "mc.bottone.io");
			break;

		case INGAME:
			sb.put(9, ChatColor.BOLD + "Timer: " + countdown);
			sb.put(8, "");
			sb.put(7, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(6, ChatColor.RED.toString() + "Score: " + ChatColor.BOLD + redScore);
			sb.put(5, ChatColor.RED.toString() + "Power Cells: " + redPC);
			sb.put(4, "");
			sb.put(3, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(2, ChatColor.BLUE.toString() + "Score: " + ChatColor.BOLD + blueScore);
			sb.put(1, ChatColor.BLUE.toString() + "Power Cells: " + bluePC);
			break;

		case FINISHED:
			if (redScore > blueScore) {
				sb.put(11, ChatColor.RED.toString() + ChatColor.BOLD + "RED WINS!");
			} else if (blueScore > redScore) {
				sb.put(11, ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE WINS!");
			} else {
				sb.put(11, ChatColor.BOLD + "TIE!");
			}
			sb.put(10, "");
			sb.put(9, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(8, ChatColor.RED.toString() + "Score: " + ChatColor.BOLD + redScore);
			sb.put(7, ChatColor.RED.toString() + "Power Cells: " + redPC);
			sb.put(6, ChatColor.RED.toString() + "Endgame: " + redEndgame);
			sb.put(5, "");
			sb.put(4, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(3, ChatColor.BLUE.toString() + "Score: " + ChatColor.BOLD + blueScore);
			sb.put(2, ChatColor.BLUE.toString() + "Power Cells: " + bluePC);
			sb.put(1, ChatColor.BLUE.toString() + "Endgame: " + blueEndgame);
			sb.put(0, "");
			sb.put(-1, ChatColor.GREEN + "mc.bottone.io");
			break;

		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			sb.setScoreboard(player);

			player.setExp(0);
			player.setLevel(countdown);
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerJoinEvent event) {

		event.getPlayer().teleport(stadium);
		event.getPlayer().setGameMode(GameMode.ADVENTURE);
		event.getPlayer().getInventory().clear();

		if (players.size() < 6 && joinable) {
			players.add(event.getPlayer());
		} else {
			spectators.add(event.getPlayer());
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
		}

	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {

		if (players.contains(event.getPlayer())) {
			// Player was in game
			players.remove(event.getPlayer());

			getServer().broadcastMessage(PREFIX + event.getPlayer().getName() + " has left the game.");

			if (players.size() < 1) {
				getServer().broadcastMessage(PREFIX + "Match aborted due to lack of players.");
				gameState = GameState.LOBBY;
				countdown = 30;
				resetArena();
			}

			return;
		}

		spectators.remove(event.getPlayer());

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getPlayer().hasPermission("bottone.mrc.build"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().hasPermission("bottone.mrc.build"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().getType() == Material.BOW)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupArrow(EntityPickupItemEvent event) {
		if (event.getItem().getItemStack().getType() != Material.ARROW || event.getEntity() instanceof HumanEntity)
			return;

		int arrows = 0;
		for (ItemStack item : ((HumanEntity) event.getEntity()).getInventory().getContents()) {
			if (item != null && item.getType() == Material.ARROW)
				arrows += item.getAmount();
		}

		if (arrows >= 5) {
			event.setCancelled(true);
		} else if (arrows + event.getItem().getItemStack().getAmount() > 5) {
			event.setCancelled(true);

			ItemStack itemStack = event.getItem().getItemStack();
			itemStack.setAmount(event.getItem().getItemStack().getAmount() - (5 - arrows));
			event.getItem().setItemStack(itemStack);

			((HumanEntity) event.getEntity()).getInventory().addItem(new ItemStack(Material.ARROW, 5 - arrows));
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.getPlayer().hasPermission("bottone.mrc.interact"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (!event.getExited().hasPermission("bottone.mrc.interact"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		if (!(event.getAttacker() instanceof Player)
				|| ((Player) event.getAttacker()).getGameMode() == GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		if (!joinable)
			event.setCancelled(true);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		event.getHitBlock().getLocation();
		// TODO: Check if it is scored or out of the arena
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {

			sender.sendMessage(PREFIX + "Must be a player to do that!");
			return true;

		}

		if (label.toLowerCase().startsWith("spectate")) {

			Player player = (Player) sender;

			if (joinable) {

				players.remove(player);
				spectators.add(player);

				player.setGameMode(GameMode.SPECTATOR);
				player.teleport(stadium);

				player.sendMessage(PREFIX + "You are now spectating the upcoming match!");

			} else {

				player.sendMessage(PREFIX + "You can't do that right now!");

			}

		}

		return true;

	}

	private void resetArena() {
		// TODO: Reset the arena at the end of the match
	}

}
