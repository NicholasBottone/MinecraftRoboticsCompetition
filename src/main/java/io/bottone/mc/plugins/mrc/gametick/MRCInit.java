/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.gametick;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.managers.MRCArenaManager;
import io.bottone.mc.plugins.mrc.managers.MRCCommands;
import io.bottone.mc.plugins.mrc.managers.MRCEconomyConnector;
import io.bottone.mc.plugins.mrc.managers.MRCEvents;
import io.bottone.mc.plugins.mrc.managers.MRCScoreboardManager;

public class MRCInit {

	private MRC plugin;

	public MRCInit(MRC plugin) {

		this.plugin = plugin;

		// Waits 5 seconds / 100 ticks for worlds to load before init.
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {

				loadLocations();
				plugin.l.log(Level.INFO, "Locations loaded (1/6)");

				MRCEconomyConnector.hookVault(plugin);
				// Vault economy hooked (2/6)

				MRCEvents.registerEvents(plugin);
				plugin.l.log(Level.INFO, "Events registered (3/6)");

				MRCCommands.registerCommands(plugin);
				plugin.l.log(Level.INFO, "Commands registered (4/6)");

				plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
				plugin.l.log(Level.INFO, "Bungeecord hooked (5/6)");

				plugin.arena = new MRCArenaManager(plugin);
				plugin.scoreboard = new MRCScoreboardManager(plugin);
				plugin.l.log(Level.INFO, "Arena and Scoreboard managers initialized (6/6)");

				plugin.l.log(Level.INFO, "Setup completed and MRC activated! Starting game tick.");
				plugin.l.log(Level.INFO, "*** MRC IS RUNNING IN EVENT PITS MODE ***");
				new MRCGameTick(plugin);

			}

		}, 100);

	}

	private void loadLocations() {

		plugin.world = plugin.getServer().getWorld("MRC");
		World world = plugin.world;

		plugin.positionSelect = new Location(world, 12.5, 94, 1, -90, 0);
		plugin.stadiumStands = new Location(world, -1, 81, 0, 90, 0);

		plugin.redRight = new Location(world, -38.5, 74, 15.5, 180, 0);
		plugin.redCenter = new Location(world, -32.0, 74, 15.5, 180, 0);
		plugin.redLeft = new Location(world, -25.5, 74, 15.5, 180, 0);
		plugin.blueRight = new Location(world, -25.5, 74, -12.5, 0, 0);
		plugin.blueCenter = new Location(world, -32.0, 74, -12.5, 0, 0);
		plugin.blueLeft = new Location(world, -38.5, 74, -12.5, 0, 0);

		plugin.redBayLoc = new Location(world, -37.5, 75, -22.5);
		plugin.blueBayLoc = new Location(world, -26.5, 75, 25.5);

		plugin.powerCellSpots.add(new Location(world, -43, 73, 6.5));
		plugin.powerCellSpots.add(new Location(world, -43, 73, 4.5));
		plugin.powerCellSpots.add(new Location(world, -43, 73, 2.5));
		plugin.powerCellSpots.add(new Location(world, -43.5, 73, -4.5));
		plugin.powerCellSpots.add(new Location(world, -42.5, 73, -4.5));
		plugin.powerCellSpots.add(new Location(world, -21, 73, 0.5));
		plugin.powerCellSpots.add(new Location(world, -21, 73, -1.5));
		plugin.powerCellSpots.add(new Location(world, -21, 73, -3.5));
		plugin.powerCellSpots.add(new Location(world, -20.5, 73, 7.5));
		plugin.powerCellSpots.add(new Location(world, -21.5, 73, 7.5));
		plugin.powerCellSpots.add(new Location(world, -33.5, 73, 9.5));
		plugin.powerCellSpots.add(new Location(world, -32, 73, 8.5));
		plugin.powerCellSpots.add(new Location(world, -30, 73, 7.5));
		plugin.powerCellSpots.add(new Location(world, -36.5, 73, 7));
		plugin.powerCellSpots.add(new Location(world, -37.5, 73, 5));
		plugin.powerCellSpots.add(new Location(world, -30.5, 73, -6.5));
		plugin.powerCellSpots.add(new Location(world, -32, 73, -5.5));
		plugin.powerCellSpots.add(new Location(world, -34, 73, -4.5));
		plugin.powerCellSpots.add(new Location(world, -27.5, 73, -4));
		plugin.powerCellSpots.add(new Location(world, -26.5, 73, -2));

		plugin.redRightSign = (Sign) new Location(world, 14.5, 95, 0.5).getBlock().getState();
		plugin.redCenterSign = (Sign) new Location(world, 14.5, 95, -0.5).getBlock().getState();
		plugin.redLeftSign = (Sign) new Location(world, 14.5, 95, -1.5).getBlock().getState();
		plugin.blueRightSign = (Sign) new Location(world, 14.5, 95, 3.5).getBlock().getState();
		plugin.blueCenterSign = (Sign) new Location(world, 14.5, 95, 2.5).getBlock().getState();
		plugin.blueLeftSign = (Sign) new Location(world, 14.5, 95, 1.5).getBlock().getState();

	}

}
