package io.bottone.mc.plugins.mrc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

public final class MRC extends JavaPlugin implements Listener {

	public static enum GameState {
		LOBBY, COUNTDOWN, INGAME, FINISHED
	}

	public static final String PREFIX = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MRC"
			+ ChatColor.DARK_PURPLE + "] " + ChatColor.AQUA;

	private Logger l;
	private static Random rand = new Random();

	private Location stadium;
	private Location red1;
	private Location red2;
	private Location red3;
	private Location blue1;
	private Location blue2;
	private Location blue3;

	private Location redBayLoc;
	private Location blueBayLoc;
	private Hologram redBayHolo;
	private Hologram blueBayHolo;
	private TextLine redBayLine;
	private TextLine blueBayLine;

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
	private int redBay = 5;

	private int blueScore = 0;
	private int bluePC = 0;
	private int blueEndgame = 0;
	private int blueBay = 5;

//	private Economy econ;

	private MRC plugin;

	@Override
	public void onEnable() {
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
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
					countdown = 0;

					if (players.size() >= 1) {
						// We have player(s) ... start the countdown!
						gameState = GameState.COUNTDOWN;
						countdown = 5;

						redScore = 0;
						redPC = 0;
						redEndgame = 0;
						redBay = 5;
						blueScore = 0;
						bluePC = 0;
						blueEndgame = 0;
						blueBay = 5;

						getServer().broadcastMessage(PREFIX + "Match starting in 5 seconds!");
					}
					break;

				case COUNTDOWN:
					// Pre-game state, starting soon.

					if (players.size() < 1) {
						// We don't have enough players ... abort the countdown!
						gameState = GameState.LOBBY;
						countdown = 0;
						joinable = true;

						getServer().broadcastMessage(PREFIX + "Match countdown aborted due to lack of players.");
						break;
					}
					if (countdown <= 0 && joinable) {
						// Game no longer joinable, assigning teams and moving to the arena.
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
									player.getWorld().spawnEntity(red1, EntityType.BOAT).addPassenger(player);
									redPlayers.add(player);
								} else {
									player.teleport(blue1);
									player.getWorld().spawnEntity(blue1, EntityType.BOAT).addPassenger(player);
									bluePlayers.add(player);
								}
								break;
							case 2:
								if (red) {
									player.teleport(red2);
									player.getWorld().spawnEntity(red2, EntityType.BOAT).addPassenger(player);
									redPlayers.add(player);
								} else {
									player.teleport(blue2);
									player.getWorld().spawnEntity(blue2, EntityType.BOAT).addPassenger(player);
									bluePlayers.add(player);
								}
								break;
							case 3:
								if (red) {
									player.teleport(red3);
									player.getWorld().spawnEntity(red3, EntityType.BOAT).addPassenger(player);
									redPlayers.add(player);
								} else {
									player.teleport(blue3);
									player.getWorld().spawnEntity(blue3, EntityType.BOAT).addPassenger(player);
									bluePlayers.add(player);
								}
								break;
							}

							Color team;

							if (red) {
								team = Color.RED;
								player.sendMessage(PREFIX + "You are competing on the " + ChatColor.RED + ChatColor.BOLD
										+ "RED ALLIANCE");
							} else {
								team = Color.BLUE;
								player.sendMessage(PREFIX + "You are competing on the " + ChatColor.BLUE
										+ ChatColor.BOLD + "BLUE ALLIANCE");
							}

							// Give players their colored armor
							ItemStack armor = new ItemStack(Material.LEATHER_BOOTS);
							LeatherArmorMeta lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(team);
							armor.setItemMeta(lameta);
							player.getInventory().setBoots(armor);

							armor = new ItemStack(Material.LEATHER_LEGGINGS);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(team);
							armor.setItemMeta(lameta);
							player.getInventory().setLeggings(armor);

							armor = new ItemStack(Material.LEATHER_HELMET);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(team);
							armor.setItemMeta(lameta);
							player.getInventory().setHelmet(armor);

							armor = new ItemStack(Material.LEATHER_CHESTPLATE);
							lameta = (LeatherArmorMeta) armor.getItemMeta();
							lameta.setColor(team);
							armor.setItemMeta(lameta);
							player.getInventory().setChestplate(armor);

							red = !red;
							if (red)
								position++;
						}

						joinable = false;
						countdown = 10;

					}
					if (countdown > 0 && !joinable) {
						// Final countdown.
						// Show title
						showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + (countdown - 1), "");
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
						clearEntities();

						for (int position = 1; position <= redPlayers.size(); position++) {
							Player player = redPlayers.get(position - 1);

							// Give players their 3 starting arrows
							player.getInventory().addItem(new ItemStack(Material.ARROW, 3));

							// Teleport players to their positions
							switch (position) {
							case 1:
								player.teleport(red1);
								player.getWorld().spawnEntity(red1, EntityType.BOAT).addPassenger(player);
								break;
							case 2:
								player.teleport(red2);
								player.getWorld().spawnEntity(red2, EntityType.BOAT).addPassenger(player);
								break;
							case 3:
								player.teleport(red3);
								player.getWorld().spawnEntity(red3, EntityType.BOAT).addPassenger(player);
								break;
							}
						}

						for (int position = 1; position <= bluePlayers.size(); position++) {
							Player player = bluePlayers.get(position - 1);

							// Give players their 3 starting arrows
							player.getInventory().addItem(new ItemStack(Material.ARROW, 3));

							// Teleport players to their positions
							switch (position) {
							case 1:
								player.teleport(blue1);
								player.getWorld().spawnEntity(blue1, EntityType.BOAT).addPassenger(player);
								break;
							case 2:
								player.teleport(blue2);
								player.getWorld().spawnEntity(blue2, EntityType.BOAT).addPassenger(player);
								break;
							case 3:
								player.teleport(blue3);
								player.getWorld().spawnEntity(blue3, EntityType.BOAT).addPassenger(player);
								break;
							}
						}

						// Setup the loading bay chest holograms
						redBayHolo = HologramsAPI.createHologram(plugin, redBayLoc);
						blueBayHolo = HologramsAPI.createHologram(plugin, blueBayLoc);

						redBayLine = redBayHolo.appendTextLine(redBay + " Power Cells");
						blueBayLine = blueBayHolo.appendTextLine(blueBay + " Power Cells");

						redBayHolo.appendItemLine(new ItemStack(Material.ARROW));
						blueBayHolo.appendItemLine(new ItemStack(Material.ARROW));

						break;
					}

					countdown--;

					break;

				case INGAME:
					joinable = false;

					redBayLine.setText(redBay + " Power Cells");
					blueBayLine.setText(blueBay + " Power Cells");

					if (countdown <= 0) {
						// Match is over.
						gameState = GameState.FINISHED;
						countdown = 10;

						clearEntities();

						for (Player player : players) {
							player.getInventory().remove(Material.BOW);
							player.getInventory().remove(Material.ARROW);
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

					countdown--;
					break;

				case FINISHED:
					joinable = false;

					if (countdown <= 0) {

						gameState = GameState.LOBBY;
						joinable = true;

						for (Player player : players) {
							player.getInventory().clear();
							sendToBungeeServer(player, "Hub");
						}

						for (Player player : spectators) {
							sendToBungeeServer(player, "Hub");
						}

						resetArena();
						break;

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
				stadium = new Location(getServer().getWorld("MRC"), -0.5, 82, 1, 90, 0);
				red1 = new Location(getServer().getWorld("MRC"), -38.5, 74, 15.5, 180, 0);
				red2 = new Location(getServer().getWorld("MRC"), -32.0, 74, 15.5, 180, 0);
				red3 = new Location(getServer().getWorld("MRC"), -25.5, 74, 15.5, 180, 0);
				blue1 = new Location(getServer().getWorld("MRC"), -25.5, 74, -12.5, 0, 0);
				blue2 = new Location(getServer().getWorld("MRC"), -32.0, 74, -12.5, 0, 0);
				blue3 = new Location(getServer().getWorld("MRC"), -38.5, 74, -12.5, 0, 0);

				redBayLoc = new Location(getServer().getWorld("MRC"), -37.5, 76, -22.5);
				blueBayLoc = new Location(getServer().getWorld("MRC"), -26.5, 76, 25.5);

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
			sb.put(3, "Waiting to start the game!");
			sb.put(2, players.size() + " players");
			sb.put(1, " ");
			sb.put(0, ChatColor.GREEN + "mc.bottone.io");
			break;

		case COUNTDOWN:
			if (joinable) {
				sb.put(3, "Match initiating in " + countdown);
			} else {
				sb.put(3, ChatColor.BOLD + "Here we go in " + countdown);
			}
			sb.put(2, players.size() + " players");
			sb.put(1, " ");
			sb.put(0, ChatColor.GREEN + "mc.bottone.io");
			break;

		case INGAME:
			sb.put(9, ChatColor.BOLD + "Timer: " + countdown);
			sb.put(8, " ");
			sb.put(7, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(6, ChatColor.RED.toString() + "Score: " + ChatColor.BOLD + redScore);
			sb.put(5, ChatColor.RED.toString() + "Power Cells: " + redPC);
			sb.put(4, "  ");
			sb.put(3, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(2, ChatColor.BLUE.toString() + "Score: " + ChatColor.BOLD + blueScore);
			sb.put(1, ChatColor.BLUE.toString() + "Power Cells: " + bluePC);
			break;

		case FINISHED:
			if (redScore > blueScore) {
				sb.put(12, ChatColor.RED.toString() + ChatColor.BOLD + "RED WINS!");
			} else if (blueScore > redScore) {
				sb.put(12, ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE WINS!");
			} else {
				sb.put(12, ChatColor.BOLD + "TIE!");
			}
			sb.put(11, " ");
			sb.put(10, ChatColor.RED.toString() + ChatColor.BOLD + "Red Alliance");
			sb.put(9, ChatColor.RED.toString() + "Score: " + ChatColor.BOLD + redScore);
			sb.put(8, ChatColor.RED.toString() + "Power Cells: " + redPC);
			sb.put(7, ChatColor.RED.toString() + "Endgame: " + redEndgame);
			sb.put(6, "  ");
			sb.put(5, ChatColor.BLUE.toString() + ChatColor.BOLD + "Blue Alliance");
			sb.put(4, ChatColor.BLUE.toString() + "Score: " + ChatColor.BOLD + blueScore);
			sb.put(3, ChatColor.BLUE.toString() + "Power Cells: " + bluePC);
			sb.put(2, ChatColor.BLUE.toString() + "Endgame: " + blueEndgame);
			sb.put(1, "   ");
			sb.put(0, ChatColor.GREEN + "mc.bottone.io");
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
			event.getPlayer().setAllowFlight(false);
		} else {
			spectators.add(event.getPlayer());
			event.getPlayer().setAllowFlight(true);
		}

	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {

		Entity vehicle = event.getPlayer().getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}

		if (players.contains(event.getPlayer())) {
			// Player was in game
			players.remove(event.getPlayer());
			redPlayers.remove(event.getPlayer());
			bluePlayers.remove(event.getPlayer());

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
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
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
	public void onInventoryEvent(InventoryClickEvent event) {
		if (!joinable)
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);

		int arrows = 0;
		for (ItemStack item : event.getPlayer().getInventory().getContents()) {
			if (item != null && item.getType() == Material.ARROW)
				arrows += item.getAmount();
		}

		if (arrows >= 5) {
			return;
		}

		if (event.getInventory().contains(Material.RED_WOOL) && redPlayers.contains(event.getPlayer()) && redBay > 0) {
			redBay--;
			event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
		}

		if (event.getInventory().contains(Material.BLUE_WOOL) && bluePlayers.contains(event.getPlayer())
				&& blueBay > 0) {
			blueBay--;
			event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW, 1));
		}
	}

	@EventHandler
	public void onEntityEnter(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.BOAT)
			event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) { // FIXME
		if (event.getVehicle().isDead() || !event.getVehicle().isValid())
			return;

		if (event.getExited() instanceof Player && ((Player) event.getExited()).getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
			event.getVehicle().addPassenger(event.getExited());
		}
	}

	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		if (!joinable)
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		if (!joinable)
			event.setCancelled(true);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Location loc = event.getEntity().getLocation();

		if (event.getHitBlock() != null) {

			// Check if scored for red alliance
			if (event.getHitBlock().getType() == Material.RED_TERRACOTTA) {
				// OUTER PORT
				redScore += 2;
				loc.getWorld().playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
				redPC++;
				blueBay++;
				return;
			}
			if (event.getHitBlock().getType() == Material.RED_CONCRETE_POWDER) {
				// INNER PORT
				redScore += 3;
				loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_LAND, 1, 1);
				redPC++;
				blueBay++;
				return;
			}

			// Check if scored for blue alliance
			if (event.getHitBlock().getType() == Material.BLUE_TERRACOTTA) {
				// OUTER PORT
				blueScore += 2;
				loc.getWorld().playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
				bluePC++;
				redBay++;
				return;
			}
			if (event.getHitBlock().getType() == Material.BLUE_CONCRETE_POWDER) {
				// INNER PORT
				blueScore += 3;
				loc.getWorld().playSound(loc, Sound.BLOCK_ANVIL_LAND, 1, 1);
				bluePC++;
				redBay++;
				return;
			}

		}

		// Miss - outside arena - respawn arrow
		loc.getWorld().dropItemNaturally(new Location(loc.getWorld(), random(-40, -24), 83, random(-19, 22)),
				new ItemStack(Material.ARROW));
		event.getEntity().remove();

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

				player.teleport(stadium);
				player.setAllowFlight(true);

				player.sendMessage(PREFIX + "You are now spectating the upcoming match!");

			} else {

				player.sendMessage(PREFIX + "You can't do that right now!");

			}

		}

		return true;

	}

	private void resetArena() {
		players.clear();
		spectators.clear();
		redPlayers.clear();
		bluePlayers.clear();

		if (redBayHolo != null) {
			redBayHolo.delete();
			blueBayHolo.delete();
		}

		clearEntities();
	}

	private void sendToBungeeServer(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}

	private void clearEntities() {
		for (World w : getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e instanceof Player)
					continue;
				e.remove();
			}
		}
	}

	private static int random(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}

}
