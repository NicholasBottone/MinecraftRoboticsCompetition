package io.bottone.mc.plugins.mrc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.block.Sign;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

import net.milkbowl.vault.economy.Economy;

public final class MRC extends JavaPlugin implements Listener {

	public static enum GameState {
		LOBBY, COUNTDOWN, INGAME, FINISHED
	}

	public static enum PlayerClass {
		BOW, CROSSBOW, SNOWBALL, INSTACLIMB
	}

	public static final String PREFIX = ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "MRC"
			+ ChatColor.DARK_PURPLE + "] " + ChatColor.AQUA;

	public static final int WIN_REWARD = 10;
	public static final int TIE_REWARD = 2;

	private Logger l;
	private static Random rand = new Random();

	private Location stadium;
	private Location redRight;
	private Location redCenter;
	private Location redLeft;
	private Location blueRight;
	private Location blueCenter;
	private Location blueLeft;

	private Sign redRightSign;
	private Sign redCenterSign;
	private Sign redLeftSign;
	private Sign blueRightSign;
	private Sign blueCenterSign;
	private Sign blueLeftSign;

	private List<Location> powerCellSpots = new ArrayList<>();

	private World world;

	private Location redBayLoc;
	private Location blueBayLoc;
	private Hologram redBayHolo;
	private Hologram blueBayHolo;
	private TextLine redBayLine;
	private TextLine blueBayLine;

	private List<Player> players = new ArrayList<>();
	private List<Player> spectators = new ArrayList<>();
	private List<Player> tempSpectators = new ArrayList<>();

	private List<Player> redPlayers = new ArrayList<>();
	private List<Player> bluePlayers = new ArrayList<>();

	private HashMap<Player, PlayerData> playerData = new HashMap<>();
	private HashMap<Player, Location> playerPositions = new HashMap<>();
	private HashMap<Player, PlayerClass> playerClasses = new HashMap<>();

	private List<Player> hungPlayers = new ArrayList<>();

	private GameState gameState = GameState.LOBBY;
	private int countdown = 0;
	private boolean joinable = true;

	private int redScore = 0;
	private int redPC = 0;
	private int redEndgame = 0;
	private int redBay = 5;

	private int blueScore = 0;
	private int bluePC = 0;
	private int blueEndgame = 0;
	private int blueBay = 5;

	private Economy econ;

	private MRC plugin;

	@Override
	public void onEnable() {
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		l = getLogger();
		plugin = this;

		// Check for Vault
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			l.log(Level.WARNING, "Unable to hook with Vault, disabling economy based functions!");
		} else {
			// Hook Vault economy
			RegisteredServiceProvider<Economy> rspe = getServer().getServicesManager().getRegistration(Economy.class);
			econ = rspe.getProvider();
			if (econ == null) {
				l.log(Level.WARNING, "Unable to hook with Vault economy, disabling economy based functions!");
			} else {
				l.log(Level.INFO, "MRC successfully hooked to Vault economy: " + econ.getName());
			}
		}

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
						countdown = 20;

						redScore = 0;
						redPC = 0;
						redEndgame = 0;
						redBay = 5;
						blueScore = 0;
						bluePC = 0;
						blueEndgame = 0;
						blueBay = 5;

						getServer().broadcastMessage(PREFIX + "Match initiating in 20 seconds!");
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
						// Game no longer joinable, moving to the arena.

						for (Player player : playerPositions.keySet()) {
							Location position = playerPositions.get(player);

							// Clear inventories
							player.getInventory().clear();

							// Give players their power cell shooters
							switch (playerClasses.get(player)) {
							case INSTACLIMB:
							case BOW:
								ItemStack bow = new ItemStack(Material.BOW, 1);
								ItemMeta bowMeta = bow.getItemMeta();
								bowMeta.setUnbreakable(true);
								bowMeta.setDisplayName("Power Cell Shooter");
								bow.setItemMeta(bowMeta);
								player.getInventory().addItem(bow);
								break;
							case CROSSBOW:
								ItemStack crossbow = new ItemStack(Material.CROSSBOW, 1);
								ItemMeta crossbowMeta = crossbow.getItemMeta();
								crossbowMeta.setUnbreakable(true);
								crossbowMeta.setDisplayName("Power Cell Shooter");
								crossbow.setItemMeta(crossbowMeta);
								player.getInventory().addItem(crossbow);
								break;
							case SNOWBALL: // intentionally blank
								break;
							}

							// Teleport players to their positions
							player.teleport(position);
							world.spawnEntity(position, EntityType.BOAT).addPassenger(player);
							redPlayers.add(player);

							Color team;

							if (redPlayers.contains(player)) {
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

						}

						// Play sound
						world.playSound(redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);

						// Announce teams
						String redString = "";
						for (Player player : redPlayers) {
							redString += player.getName() + " ";
						}
						String blueString = "";
						for (Player player : bluePlayers) {
							blueString += player.getName() + " ";
						}
						getServer().broadcastMessage(PREFIX + ChatColor.RED + redString + ChatColor.WHITE + "VS "
								+ ChatColor.BLUE + blueString);

						joinable = false;
						countdown = 10;

					}
					if (countdown > 0 && !joinable) {
						// Final countdown.

						// Show title
						showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + countdown, " ");

						// Play sound
						world.playSound(redRight, Sound.BLOCK_NOTE_BLOCK_BASS, 100, 1);
					}
					if (countdown <= 0 && !joinable) {
						// Match starts.

						// Show title
						showInstantTitle(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "GO",
								ChatColor.LIGHT_PURPLE + "Good luck!");

						// Play sound
						world.playSound(redRight, Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1);

						// Time to start the match!
						gameState = GameState.INGAME;
						countdown = 150;
						getServer().broadcastMessage(PREFIX + "Let the match begin!");
						clearEntities();

						for (Player player : playerPositions.keySet()) {
							Location position = playerPositions.get(player);

							// Init player data
							playerData.put(player, new PlayerData(player.getName()));

							// Give players their 3 starting power cells
							if (playerClasses.get(player) == PlayerClass.SNOWBALL)
								givePowerCells(player, 3, Material.SNOWBALL);
							else
								givePowerCells(player, 3, Material.ARROW);

							// Teleport players to their positions
							player.teleport(position);
							world.spawnEntity(position, EntityType.BOAT).addPassenger(player);
						}

						// Spawn initial power cells on the field
						ItemStack arrowStack = new ItemStack(Material.ARROW);
						ItemMeta meta = arrowStack.getItemMeta();
						meta.setDisplayName("Power Cell");
						arrowStack.setItemMeta(meta);
						for (Location loc : powerCellSpots) {
							world.dropItem(loc, arrowStack).setVelocity(new Vector(0, 0, 0));
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

						// Calculate parks in the rendezvous zone.
						for (Player player : redPlayers) {
							Location loc = player.getLocation();
							loc.setY(71.5);
							if (loc.getBlock().getType() == Material.SMOOTH_RED_SANDSTONE
									|| loc.getBlock().getType() == Material.OAK_WOOD) {
								// Parked!
								redScore += 5;
								redEndgame += 5;
								playerData.get(player).addPoints(5);
							}
						}
						for (Player player : bluePlayers) {
							Location loc = player.getLocation();
							loc.setY(71.5);
							if (loc.getBlock().getType() == Material.SMOOTH_SANDSTONE
									|| loc.getBlock().getType() == Material.OAK_WOOD) {
								// Parked!
								blueScore += 5;
								blueEndgame += 5;
								playerData.get(player).addPoints(5);
							}
						}

						// Match is over.
						gameState = GameState.FINISHED;
						countdown = 10;

						// Remove all boats and arrows
						clearEntities();

						// Clear all game items from inventory (other than armor)
						for (Player player : players) {
							player.getInventory().remove(Material.BOW);
							player.getInventory().remove(Material.CROSSBOW);
							player.getInventory().remove(Material.SNOWBALL);
							player.getInventory().remove(Material.ARROW);
						}

						// Announce the winner based on final score
						if (redScore > blueScore) {
							getServer().broadcastMessage(
									PREFIX + ChatColor.RED.toString() + ChatColor.BOLD + "RED ALLIANCE WINS!");
							showTitle(ChatColor.RED.toString() + ChatColor.BOLD + "RED ALLIANCE WINS!", " ");

							if (econ != null && players.size() >= 2) {
								// Give economy reward
								for (Player player : redPlayers) {
									econ.depositPlayer(player, WIN_REWARD);
								}
							}

						} else if (blueScore > redScore) {
							getServer().broadcastMessage(
									PREFIX + ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE ALLIANCE WINS!");
							showTitle(ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE ALLIANCE WINS!", " ");

							if (econ != null && players.size() >= 2) {
								// Give economy reward
								for (Player player : redPlayers) {
									econ.depositPlayer(player, WIN_REWARD);
								}
							}
						} else {
							getServer().broadcastMessage(
									PREFIX + ChatColor.WHITE.toString() + ChatColor.BOLD + "IT'S A TIE!");
							showTitle(ChatColor.WHITE.toString() + ChatColor.BOLD + "IT'S A TIE!", " ");

							if (econ != null && players.size() >= 2) {
								// Give economy reward
								for (Player player : players) {
									econ.depositPlayer(player, TIE_REWARD);
								}
							}
						}

						// Publish final score to chat
						getServer().broadcastMessage(PREFIX + "Final Score: " + ChatColor.RED + ChatColor.BOLD
								+ redScore + ChatColor.AQUA + " to " + ChatColor.BLUE + ChatColor.BOLD + blueScore);

						// Play sound
						world.playSound(redRight, Sound.ENTITY_PLAYER_LEVELUP, 100, 1);

						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								getServer().broadcastMessage(PREFIX + ChatColor.BOLD + "Player Contributions");
								for (Player p : playerData.keySet()) {
									getServer().broadcastMessage(PREFIX + playerData.get(p));
								}
							}
						}, 60);

						break;
					}

					if (countdown == 30) { // Endgame period starts
						// Give acacia door for dismounting and climbing
						for (Player player : players) {
							ItemStack item = new ItemStack(Material.ACACIA_DOOR, 1);
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Begin Climbing");
							item.setItemMeta(meta);
							player.getInventory().setItem(8, item);
						}

						// Paste vines for climbing
						pasteVines();

						// Play sound
						world.playSound(redRight, Sound.BLOCK_NOTE_BLOCK_BIT, 100, 1);

						// Send message
						getServer()
								.broadcastMessage(PREFIX + "We're in the endgame now. 30 seconds left in the match!");
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
						for (Player player : tempSpectators) {
							player.getInventory().clear();
							sendToBungeeServer(player, "Hub");
						}

						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								resetArena();
							}
						}, 20);

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
				world = getServer().getWorld("MRC");

				stadium = new Location(world, 12.5, 94, 1, -90, 0);
				redRight = new Location(world, -38.5, 74, 15.5, 180, 0);
				redCenter = new Location(world, -32.0, 74, 15.5, 180, 0);
				redLeft = new Location(world, -25.5, 74, 15.5, 180, 0);
				blueRight = new Location(world, -25.5, 74, -12.5, 0, 0);
				blueCenter = new Location(world, -32.0, 74, -12.5, 0, 0);
				blueLeft = new Location(world, -38.5, 74, -12.5, 0, 0);

				redRightSign = (Sign) new Location(world, -14, 95, 0).getBlock();
				redCenterSign = (Sign) new Location(world, -14, 95, -1).getBlock();
				redLeftSign = (Sign) new Location(world, -14, 95, -2).getBlock();
				blueRightSign = (Sign) new Location(world, -14, 95, 3).getBlock();
				blueCenterSign = (Sign) new Location(world, -14, 95, 2).getBlock();
				blueLeftSign = (Sign) new Location(world, -14, 95, 1).getBlock();

				redBayLoc = new Location(world, -37.5, 76, -22.5);
				blueBayLoc = new Location(world, -26.5, 76, 25.5);

				powerCellSpots.add(new Location(world, -43, 73, 6.5));
				powerCellSpots.add(new Location(world, -43, 73, 4.5));
				powerCellSpots.add(new Location(world, -43, 73, 2.5));
				powerCellSpots.add(new Location(world, -43.5, 73, -4.5));
				powerCellSpots.add(new Location(world, -42.5, 73, -4.5));
				powerCellSpots.add(new Location(world, -21, 73, 0.5));
				powerCellSpots.add(new Location(world, -21, 73, -1.5));
				powerCellSpots.add(new Location(world, -21, 73, -3.5));
				powerCellSpots.add(new Location(world, -20.5, 73, 7.5));
				powerCellSpots.add(new Location(world, -21.5, 73, 7.5));
				powerCellSpots.add(new Location(world, -33.5, 73, 9.5));
				powerCellSpots.add(new Location(world, -32, 73, 8.5));
				powerCellSpots.add(new Location(world, -30, 73, 7.5));
				powerCellSpots.add(new Location(world, -36.5, 73, 7));
				powerCellSpots.add(new Location(world, -37.5, 73, 5));
				powerCellSpots.add(new Location(world, -30.5, 73, -6.5));
				powerCellSpots.add(new Location(world, -32, 73, -5.5));
				powerCellSpots.add(new Location(world, -34, 73, -4.5));
				powerCellSpots.add(new Location(world, -27.5, 73, -4));
				powerCellSpots.add(new Location(world, -26.5, 73, -2));

				getServer().getPluginManager().registerEvents(plugin, plugin);
				l.log(Level.INFO, "Locations loaded and MRC activated.");
			}

		}, 100);

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

		for (Player player : spectators) {
			sb.setScoreboard(player);

			player.setExp(0);
			player.setLevel(countdown);
		}

		for (Player player : players) {
			sb = new MRCScoreboard(PREFIX + gameState.toString());

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
				PlayerData pd = playerData.get(player);
				sb.put(12, ChatColor.BOLD + "Timer: " + countdown);
				sb.put(11, " ");
				sb.put(10, ChatColor.AQUA.toString() + pd.getPointsContributed() + " pts contributed");
				sb.put(9, ChatColor.AQUA.toString() + pd.getAccuracyPercent() + "% acc, " + pd.getInnersPercent()
						+ "% inners");
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
				PlayerData pdata = playerData.get(player);
				if (redScore > blueScore) {
					sb.put(15, ChatColor.RED.toString() + ChatColor.BOLD + "RED WINS!");
				} else if (blueScore > redScore) {
					sb.put(15, ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE WINS!");
				} else {
					sb.put(15, ChatColor.BOLD + "TIE!");
				}
				sb.put(14, " ");
				sb.put(13, ChatColor.AQUA.toString() + pdata.getPointsContributed() + " pts contributed");
				sb.put(12, ChatColor.AQUA.toString() + pdata.getAccuracyPercent() + "% acc, " + pdata.getInnersPercent()
						+ "% inners");
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

		ItemStack item = new ItemStack(Material.IRON_DOOR, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Return to Hub");
		item.setItemMeta(meta);
		event.getPlayer().getInventory().setItem(8, item);

		spectators.add(event.getPlayer());
		tempSpectators.add(event.getPlayer());
		event.getPlayer().setAllowFlight(true);
		event.getPlayer().sendMessage(PREFIX + "Welcome to MRC! Choose a position and class to compete!");

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
			playerClasses.remove(event.getPlayer());

			Location l = playerPositions.remove(event.getPlayer());
			if (l != null) {
				if (l.equals(redLeft))
					redLeftSign.setLine(3, "Click to claim");
				if (l.equals(redCenter))
					redCenterSign.setLine(3, "Click to claim");
				if (l.equals(redRight))
					redRightSign.setLine(3, "Click to claim");
				if (l.equals(blueLeft))
					blueLeftSign.setLine(3, "Click to claim");
				if (l.equals(blueCenter))
					blueCenterSign.setLine(3, "Click to claim");
				if (l.equals(blueRight))
					blueRightSign.setLine(3, "Click to claim");
			}

			getServer().broadcastMessage(PREFIX + event.getPlayer().getName() + " has left the game.");

			if (players.size() < 1) {
				getServer().broadcastMessage(PREFIX + "Match aborted due to lack of players.");
				gameState = GameState.LOBBY;
				countdown = 20;
				resetArena();
			}

			return;
		}

		spectators.remove(event.getPlayer());
		tempSpectators.remove(event.getPlayer());

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
			if (item != null && (item.getType() == Material.ARROW || item.getType() == Material.SNOWBALL))
				arrows += item.getAmount();
		}

		if (arrows >= 5) {
			event.setCancelled(true);
		} else if (arrows + event.getItem().getItemStack().getAmount() > 5) {
			event.setCancelled(true);
			if (playerClasses.get(event.getEntity()) == PlayerClass.SNOWBALL)
				givePowerCells((HumanEntity) event.getEntity(),
						event.getItem().getItemStack().getAmount() - (5 - arrows), Material.SNOWBALL);
			else
				givePowerCells((HumanEntity) event.getEntity(),
						event.getItem().getItemStack().getAmount() - (5 - arrows), Material.ARROW);
		} else if (playerClasses.get(event.getEntity()) == PlayerClass.SNOWBALL) {
			event.setCancelled(true);
			givePowerCells((HumanEntity) event.getEntity(), event.getItem().getItemStack().getAmount(),
					Material.SNOWBALL);
		}

		if (playerClasses.get(event.getEntity()) == PlayerClass.SNOWBALL) {
			((HumanEntity) event.getEntity()).getInventory().remove(Material.ARROW);
			givePowerCells((HumanEntity) event.getEntity(), arrows, Material.SNOWBALL);
		}
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
			if (item != null && item.getType() == Material.ARROW)
				arrows += item.getAmount();
		}

		if (arrows >= 5) {
			return;
		}

		if (event.getInventory().contains(Material.RED_WOOL) && redPlayers.contains(event.getPlayer()) && redBay > 0) {
			redBay--;
			if (playerClasses.get(event.getPlayer()) == PlayerClass.SNOWBALL)
				givePowerCells(event.getPlayer(), 1, Material.SNOWBALL);
			else
				givePowerCells(event.getPlayer(), 1, Material.ARROW);
		}

		if (event.getInventory().contains(Material.BLUE_WOOL) && bluePlayers.contains(event.getPlayer())
				&& blueBay > 0) {
			blueBay--;
			if (playerClasses.get(event.getPlayer()) == PlayerClass.SNOWBALL)
				givePowerCells(event.getPlayer(), 1, Material.SNOWBALL);
			else
				givePowerCells(event.getPlayer(), 1, Material.ARROW);
		}

		redBayLine.setText(redBay + " Power Cells");
		blueBayLine.setText(blueBay + " Power Cells");
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {

		if (event.getMaterial() == Material.IRON_DOOR) {
			// Send back to hub bungee server
			sendToBungeeServer(event.getPlayer(), "Hub");
			return;
		}

		if (event.getMaterial() == Material.ACACIA_DOOR) {
			if (gameState != GameState.INGAME)
				return;
			// Dismount from boat to begin climb
			Entity vehicle = event.getPlayer().getVehicle();
			if (vehicle != null) {
				vehicle.remove();
			}
			event.getPlayer().getInventory().remove(Material.ACACIA_DOOR);
			event.getPlayer().getInventory().remove(Material.BOW);
			if (playerClasses.get(event.getPlayer()) == PlayerClass.INSTACLIMB) {
				// Instant hang, award points for hang
				if (!hungPlayers.contains(event.getPlayer())) {
					playerData.get(event.getPlayer()).addPoints(20);
					hungPlayers.add(event.getPlayer());
					if (redPlayers.contains(event.getPlayer())) {
						redScore += 20;
						redEndgame += 20;
					} else if (bluePlayers.contains(event.getPlayer())) {
						blueScore += 20;
						blueEndgame += 20;
					}
					event.getPlayer().sendMessage(PREFIX + "You have hung.");
				}
			}
			return;
		}

		if (event.getClickedBlock() != null) {

			if (event.getClickedBlock().getType() == Material.BELL) {
				if (gameState != GameState.INGAME)
					return;

				if (redPlayers.contains(event.getPlayer())) {
					// if they are on the red alliance
					Location loc = event.getClickedBlock().getLocation();
					loc.setY(loc.getY() + 1);
					if (loc.getBlock().getType() != Material.RED_CONCRETE)
						return;
				} else if (bluePlayers.contains(event.getPlayer())) {
					// if they are on the blue alliance
					Location loc = event.getClickedBlock().getLocation();
					loc.setY(loc.getY() + 1);
					if (loc.getBlock().getType() != Material.BLUE_CONCRETE)
						return;
				} else {
					return;
				}

				// Fully hung, award points for hang
				if (!hungPlayers.contains(event.getPlayer())) {
					playerData.get(event.getPlayer()).addPoints(20);
					hungPlayers.add(event.getPlayer());
					if (redPlayers.contains(event.getPlayer())) {
						redScore += 20;
						redEndgame += 20;
					} else if (bluePlayers.contains(event.getPlayer())) {
						blueScore += 20;
						blueEndgame += 20;
					}
					event.getPlayer().sendMessage(PREFIX + "You have hung.");
				}
			}

		}
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

	@EventHandler
	public void onInventoryInteract(InventoryInteractEvent event) {
		if (event.getWhoClicked().getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (gameState != GameState.INGAME)
			return;

		Location loc = event.getEntity().getLocation();

		if (event.getHitBlock() != null) {

			// Check if scored for red alliance
			if (event.getHitBlock().getType() == Material.RED_TERRACOTTA) {
				// OUTER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)
					playerData.get(event.getEntity().getShooter()).addOuter(countdown > 135);

				redScore += (countdown > 135) ? 4 : 2;
				world.playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 100, 1);
				redPC++;

				if (blueBay < 15) {
					blueBay++;
				} else {
					spawnRandomPC();
				}
				return;
			}
			if (event.getHitBlock().getType() == Material.RED_CONCRETE_POWDER) {
				// INNER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)
					playerData.get(event.getEntity().getShooter()).addInner(countdown > 135);

				redScore += (countdown > 135) ? 6 : 3;
				world.playSound(loc, Sound.BLOCK_ANVIL_LAND, 100, 1);
				redPC++;

				if (blueBay < 15) {
					blueBay++;
				} else {
					spawnRandomPC();
				}
				return;
			}

			// Check if scored for blue alliance
			if (event.getHitBlock().getType() == Material.BLUE_TERRACOTTA) {
				// OUTER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)
					playerData.get(event.getEntity().getShooter()).addOuter(countdown > 135);

				blueScore += (countdown > 135) ? 4 : 2;
				world.playSound(loc, Sound.ENTITY_ARROW_HIT_PLAYER, 100, 1);
				bluePC++;

				if (redBay < 15) {
					redBay++;
				} else {
					spawnRandomPC();
				}
				return;
			}
			if (event.getHitBlock().getType() == Material.BLUE_CONCRETE_POWDER) {
				// INNER PORT

				if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)
					playerData.get(event.getEntity().getShooter()).addInner(countdown > 135);

				blueScore += (countdown > 135) ? 6 : 3;
				world.playSound(loc, Sound.BLOCK_ANVIL_LAND, 100, 1);
				bluePC++;

				if (redBay < 15) {
					redBay++;
				} else {
					spawnRandomPC();
				}
				return;
			}

		}

		// MISSED SHOT
		spawnRandomPC();
		event.getEntity().remove();
		if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player)
			playerData.get(event.getEntity().getShooter()).addMiss();

	}

	private void spawnRandomPC() {
		// Respawn the power cell in a random spot
		ItemStack arrowStack = new ItemStack(Material.ARROW);

		ItemMeta meta = arrowStack.getItemMeta();
		meta.setDisplayName("Power Cell");
		arrowStack.setItemMeta(meta);

		world.dropItemNaturally(new Location(world, random(-40, -24), 76, random(-19, 22)), arrowStack);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {

			sender.sendMessage(PREFIX + "Must be a player to do that!");
			return true;

		}

		if (label.toLowerCase().startsWith("spectate")) {

			Player player = (Player) sender;

			if (!players.contains(player)) {

				if (!spectators.contains(player))
					spectators.add(player);
				tempSpectators.remove(player);

				player.setAllowFlight(true);

				player.sendMessage(PREFIX + "You are now in spectator mode!");
				return true;

			} else {

				player.sendMessage(PREFIX + "You can't do that right now!");
				return true;

			}

		}

		if (label.toLowerCase().startsWith("pos")) {

			Player player = (Player) sender;

			if (joinable) {

				if (args.length == 0)
					return false;

				Location pos;

				switch (args[0].toLowerCase()) {
				case "redleft":
					pos = redLeft;
					redPlayers.add(player);
					break;
				case "redcenter":
					pos = redCenter;
					redPlayers.add(player);
					break;
				case "redright":
					pos = redRight;
					redPlayers.add(player);
					break;
				case "blueleft":
					pos = blueLeft;
					bluePlayers.add(player);
					break;
				case "bluecenter":
					pos = blueCenter;
					bluePlayers.add(player);
					break;
				case "blueright":
					pos = blueRight;
					bluePlayers.add(player);
					break;
				default:
					return false;
				}

				if (playerPositions.containsValue(pos)) {
					player.sendMessage(PREFIX + "That spot is already taken!");
					return true;
				}
				removeOldPosSel(player);
				playerPositions.put(player, pos);
				playerClasses.put(player, PlayerClass.BOW);

				players.add(player);
				spectators.remove(player);
				tempSpectators.remove(player);

				player.setAllowFlight(false);

				player.sendMessage(PREFIX + "Claimed a position in the upcoming match.");
				return true;

			} else {

				player.sendMessage(PREFIX + "You can't do that right now!");
				return true;

			}

		}

		if (label.toLowerCase().startsWith("class")) {

			Player player = (Player) sender;

			if (joinable) {

				if (!players.contains(player)) {
					player.sendMessage(PREFIX + "You must claim a position before selecting a class.");
					return true;
				}

				if (args.length == 0) {
					return false;
				}

				switch (args[0].toLowerCase()) {
				case "instaclimb":
					playerClasses.put(player, PlayerClass.INSTACLIMB);
					break;
				case "bow":
					playerClasses.put(player, PlayerClass.BOW);
					break;
				case "crossbow":
					playerClasses.put(player, PlayerClass.CROSSBOW);
					break;
				case "snowball":
					playerClasses.put(player, PlayerClass.SNOWBALL);
					break;
				default:
					return false;
				}

				player.sendMessage(PREFIX + "You have been given the " + args[0] + " class.");
				return true;

			} else {

				player.sendMessage(PREFIX + "You can't do that right now!");
				return true;

			}

		}

		return false;

	}

	private void removeOldPosSel(Player player) {
		if (playerPositions.containsKey(player)) {
			Location loc = playerPositions.get(player);
			if (loc.equals(redLeft)) {
				playerPositions.remove(player);
				redLeftSign.setLine(3, "Click to claim");
			} else if (loc.equals(redCenter)) {
				playerPositions.remove(player);
				redCenterSign.setLine(3, "Click to claim");
			} else if (loc.equals(redRight)) {
				playerPositions.remove(player);
				redRightSign.setLine(3, "Click to claim");
			} else if (loc.equals(blueLeft)) {
				playerPositions.remove(player);
				blueLeftSign.setLine(3, "Click to claim");
			} else if (loc.equals(blueCenter)) {
				playerPositions.remove(player);
				blueCenterSign.setLine(3, "Click to claim");
			} else if (loc.equals(blueRight)) {
				playerPositions.remove(player);
				blueRightSign.setLine(3, "Click to claim");
			}
		}
	}

	private void resetArena() {
		// Clear all lists
		players.clear();
		spectators.clear();
		tempSpectators.clear();
		redPlayers.clear();
		bluePlayers.clear();
		hungPlayers.clear();
		playerData.clear();
		playerPositions.clear();
		playerClasses.clear();

		// Get rid of the holograms
		if (redBayHolo != null) {
			redBayHolo.delete();
			blueBayHolo.delete();
		}

		// Delete the vines
		pasteBlank();

		// Set the ingame time to a random time
		world.setTime(rand.nextInt(24000));

		// Clear entities
		clearEntities();

		// Reset position signs
		redLeftSign.setLine(3, "Click to claim");
		redCenterSign.setLine(3, "Click to claim");
		redRightSign.setLine(3, "Click to claim");
		blueLeftSign.setLine(3, "Click to claim");
		blueCenterSign.setLine(3, "Click to claim");
		blueRightSign.setLine(3, "Click to claim");
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
		for (Entity e : world.getEntities()) {
			if (e instanceof Player)
				continue;
			e.remove();
		}
	}

	private static int random(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}

	private void givePowerCells(HumanEntity player, int number, Material material) {
		ItemStack powercellStack = new ItemStack(material, number);

		ItemMeta meta = powercellStack.getItemMeta();
		meta.setDisplayName("Power Cell");
		powercellStack.setItemMeta(meta);

		player.getInventory().addItem(powercellStack);
	}

	private void pasteBlank() {

		CuboidRegion region = new CuboidRegion(BlockVector3.at(-78, 91, 50), BlockVector3.at(-85, 75, 57));
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(world), -1)) {
			ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
					region.getMinimumPoint());
			Operations.complete(forwardExtentCopy);
		} catch (WorldEditException ex) {
			l.severe("Could not WorldEdit copy blank!");
			ex.printStackTrace();
			return;
		}

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(world), -1)) {
			Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
					.to(BlockVector3.at(-36, 74, -3)).ignoreAirBlocks(false).copyEntities(false).copyBiomes(false)
					.build();
			Operations.complete(operation);
		} catch (WorldEditException ex) {
			l.severe("Could not WorldEdit paste blank!");
			ex.printStackTrace();
			return;
		}

	}

	private void pasteVines() { // FIXME: Look into fixing on Bedrock (low priority)

		CuboidRegion region = new CuboidRegion(BlockVector3.at(18, 91, 58), BlockVector3.at(11, 75, 65));
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(world), -1)) {
			ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard,
					region.getMinimumPoint());
			Operations.complete(forwardExtentCopy);
		} catch (WorldEditException ex) {
			l.severe("Could not WorldEdit copy vines!");
			ex.printStackTrace();
			return;
		}

		try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory()
				.getEditSession(BukkitAdapter.adapt(world), -1)) {
			Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
					.to(BlockVector3.at(-36, 74, -3)).ignoreAirBlocks(true).copyEntities(false).copyBiomes(false)
					.build();
			Operations.complete(operation);
		} catch (WorldEditException ex) {
			l.severe("Could not WorldEdit paste vines!");
			ex.printStackTrace();
			return;
		}

	}

}
