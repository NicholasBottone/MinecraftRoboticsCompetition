[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/NicholasBottone/MinecraftRoboticsCompetition/Java%20CI%20with%20Maven%20MASTER?style=plastic)](https://github.com/NicholasBottone/MinecraftRoboticsCompetition/actions)
[![GitHub release version (latest by date)](https://img.shields.io/github/v/release/NicholasBottone/MinecraftRoboticsCompetition?style=plastic)](https://github.com/NicholasBottone/MinecraftRoboticsCompetition/releases/latest)
[![Repo License](https://img.shields.io/github/license/NicholasBottone/MinecraftRoboticsCompetition?style=plastic)](https://github.com/NicholasBottone/MinecraftRoboticsCompetition/blob/master/LICENSE)
[![Discord](https://img.shields.io/discord/637407041048281098?label=xRC%20Sim%20Discord&style=plastic)](https://discord.gg/mhc9tkB)

# MinecraftRoboticsCompetition
 Spigot plugin for running MRC minigames. Designed and developed by Nicholas Bottone.

 MRC is a simulated version of the [FIRST Robotics Competition](https://www.firstinspires.org/robotics/frc) game [Infinite Recharge](https://www.youtube.com/watch?v=gmiYWTmFRVE) inside of Minecraft. This project is unofficial and not affiliated with FIRST.  Take a look at the MRC trailer video below, or our December MRC Event [here](https://www.youtube.com/watch?v=_SMOkGr4IMI).

[![MRC Trailer](https://img.youtube.com/vi/ZKvIZ7NuOao/0.jpg)](https://www.youtube.com/watch?v=ZKvIZ7NuOao)

 This project is designed for the [Bottone.io MC](https://mc.bottone.io) network, but it is now open source for anyone to use! You can connect to `mc.bottone.io` in Minecraft Java Edition or Bedrock Edition to play or test the MRC minigame online.  MRC is partnered with the [xRC Simulator](https://xrcsimulator.org) project.
 
## Requirements
 * This plugin is designed to be run on a server by itself. Do not attempt to run MRC minigames alongside other worlds/gamemodes on the same server. If you intent to run more than one world/gamemode on your network, you should use Bungeecord or Waterfall.
 
 * This plugin is designed to be run on a specific pre-made world. It uses hardcoded location values, so it is important that you use the proper MRC arena world.  [Download here.](https://github.com/NicholasBottone/MinecraftRoboticsCompetition/raw/master/MRC%20World.zip)
 
 * [Spigot](https://www.spigotmc.org/) (or any of its forks) 1.15.2+
   * [Paper](https://papermc.io/) (the high performance fork of Spigot) is recommended
 
 * [HolographicDisplays](https://dev.bukkit.org/projects/holographic-displays) 2.4.3+
 
 * [Vault](https://dev.bukkit.org/projects/vault) 1.7+
 
 * [WorldEdit](https://dev.bukkit.org/projects/worldedit) 7.1.0+
 
 * OPTIONAL: The Resource Pack improves the MRC experience by providing custom textures and sound effects.  [Download here.](https://github.com/NicholasBottone/MinecraftRoboticsCompetition/raw/master/MRC%20Resource%20Pack.zip)

## Commands
 The plugin mostly is designed to run automated without any input via commands.  There are some commands that players can use instead of the signs, or for admins to do certain tasks.
| Command | Arguments | Description | Permission |
| --- | --- | --- | --- |
| `/pos` | `redleft`, `redcenter`, `redright`, `blueleft`, `bluecenter`, `blueright` | Claim a starting position in the match | (all players) |
| `/class` | `bow`, `crossbow`, `snowball`, `instaclimb` | Select a class in the match | (all players) |
| `/pb` | | Displays the player's personal best score | (all players) |
| `/wr` | | Display a list of the world records | (all players + console) |
| `/reloadrecords` | | Reloads the config.yml | `mrc.fta` |
 
## License
 This project is licensed under GNU AGPLv3.  [Read about it here.](https://choosealicense.com/licenses/agpl-3.0/)  In short, you are free to modify and distribute my repository, under the condition that you provide attribution and disclose your modified source under the same license.  I ask that you link back to this original repository if you choose to do this.

 Feel free to contribute to the project!  Fork this repository, commit your changes, then submit a pull request for your contributions to be reviewed.  Alternatively, you may submit and issue to report a bug or suggestion.