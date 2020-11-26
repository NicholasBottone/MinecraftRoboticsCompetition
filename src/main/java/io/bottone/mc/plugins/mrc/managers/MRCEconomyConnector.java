package io.bottone.mc.plugins.mrc.managers;

import java.util.logging.Level;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import io.bottone.mc.plugins.mrc.MRC;
import net.milkbowl.vault.economy.Economy;

public class MRCEconomyConnector {

	public static void hookVault(MRC plugin) {

		Plugin vault = plugin.getServer().getPluginManager().getPlugin("Vault");

		// Check for Vault

		if (vault == null || !vault.isEnabled()) {

			plugin.l.log(Level.WARNING, "Unable to hook with Vault, disabling economy based functions!");

		} else {

			// Hook Vault economy

			RegisteredServiceProvider<Economy> rspe = plugin.getServer().getServicesManager()
					.getRegistration(Economy.class);
			plugin.econ = rspe.getProvider();
			if (plugin.econ == null) {
				plugin.l.log(Level.WARNING, "Unable to hook with Vault economy, disabling economy based functions!");
			} else {
				plugin.l.log(Level.INFO,
						"MRC successfully hooked to Vault economy: " + plugin.econ.getName() + " (2/7)");
			}

		}

	}

}
