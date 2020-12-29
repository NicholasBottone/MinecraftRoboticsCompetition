/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.eventhandler.BlockEventHandler;
import io.bottone.mc.plugins.mrc.eventhandler.InventoryEventHandler;
import io.bottone.mc.plugins.mrc.eventhandler.ItemEventHandler;
import io.bottone.mc.plugins.mrc.eventhandler.PlayerEventHandler;
import io.bottone.mc.plugins.mrc.eventhandler.ProjectileEventHandler;
import io.bottone.mc.plugins.mrc.eventhandler.RightClickEventHandler;
import io.bottone.mc.plugins.mrc.eventhandler.VehicleEventHandler;

public class MRCEvents {

	public static void registerEvents(MRC plugin) {

		plugin.getServer().getPluginManager().registerEvents(new BlockEventHandler(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new InventoryEventHandler(plugin), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ItemEventHandler(plugin), plugin);
		plugin.getServer().getPluginManager().registerEvents(new PlayerEventHandler(plugin), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ProjectileEventHandler(plugin), plugin);
		plugin.getServer().getPluginManager().registerEvents(new RightClickEventHandler(plugin), plugin);
		plugin.getServer().getPluginManager().registerEvents(new VehicleEventHandler(plugin), plugin);

	}

}
