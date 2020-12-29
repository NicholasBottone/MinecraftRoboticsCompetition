/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.commands.ClassCommand;
import io.bottone.mc.plugins.mrc.commands.PositionCommand;
import io.bottone.mc.plugins.mrc.commands.SpectateCommand;
import io.bottone.mc.plugins.mrc.commands.TeamChatCommand;

public class MRCCommands {

	public static void registerCommands(MRC plugin) {

		plugin.getCommand("class").setExecutor(new ClassCommand(plugin));
		plugin.getCommand("pos").setExecutor(new PositionCommand(plugin));
		plugin.getCommand("spectate").setExecutor(new SpectateCommand(plugin));
		plugin.getCommand("teamchat").setExecutor(new TeamChatCommand(plugin));

	}

}
