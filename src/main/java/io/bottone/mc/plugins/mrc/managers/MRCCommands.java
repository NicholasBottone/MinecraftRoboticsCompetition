/**
 * Copyright (C) 2020 Nicholas Bottone.
 * Licensed under the GNU Affero General Public License.
 * <https://www.gnu.org/licenses/>
 */

package io.bottone.mc.plugins.mrc.managers;

import io.bottone.mc.plugins.mrc.MRC;
import io.bottone.mc.plugins.mrc.commands.ClassCommand;
import io.bottone.mc.plugins.mrc.commands.PersonalBestCommand;
import io.bottone.mc.plugins.mrc.commands.PositionCommand;
import io.bottone.mc.plugins.mrc.commands.ReloadCommand;
import io.bottone.mc.plugins.mrc.commands.SpectateCommand;
import io.bottone.mc.plugins.mrc.commands.WorldRecordCommand;
import io.bottone.mc.plugins.mrc.commands.TeamChatCommand;

import java.util.Objects;

public class MRCCommands {

	public static void registerCommands(MRC plugin) {

		Objects.requireNonNull(plugin.getCommand("class")).setExecutor(new ClassCommand(plugin));
		Objects.requireNonNull(plugin.getCommand("pb")).setExecutor(new PersonalBestCommand(plugin));
		Objects.requireNonNull(plugin.getCommand("pos")).setExecutor(new PositionCommand(plugin));
		Objects.requireNonNull(plugin.getCommand("reloadrecords")).setExecutor(new ReloadCommand(plugin));
		Objects.requireNonNull(plugin.getCommand("spectate")).setExecutor(new SpectateCommand(plugin));
		Objects.requireNonNull(plugin.getCommand("wr")).setExecutor(new WorldRecordCommand(plugin));
		Objects.requireNonNull(plugin.getCommand("teamchat")).setExecutor(new TeamChatCommand(plugin));

	}

}
