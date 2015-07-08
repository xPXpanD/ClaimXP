package rs.expand.claimxp;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

// Changelog:
// [4/7/'15] 0.1 = First testing version. Did not even run.
// [5/7/'15] 0.2 = Ran! Also worked, but was largely hardcoded and had no support for damage values.
// [5/7/'15] 0.6 = Converted most of the plugin to use config file stuff. Damage values still eluded me.
// [6/7/'15] 0.7 = Got damage values running via config. Sweet. Added a lot of sanity checks.
// [6/7/'15] 1.0 = First version played by other people! Ran exclusively on one.expand.rs, to get some public testing in.
// [8/7/'15] 1.1 = First public version! Added a ton of extra configurable checks, and a few safeguards. Cleaned up code a little.

// TODO: Rewrite coloring code on command script to make it work in more consoles, if possible.

public final class ClaimXP extends JavaPlugin
{
	@Override
	public void onEnable() 
	{		
		// Prints a message showing that we're up and running. Force colors so it works in most consoles.
		Bukkit.getConsoleSender().sendMessage("§6ClaimXP 1.1 loaded and running, like a fridge!");
		
		// Attempt to load the configuration file. If it doesn't exist, spawn a new one.
		this.saveDefaultConfig();
		
		// Sets up DoCommand with new commands. All the big stuff is under there.
		this.getCommand("claimxp").setExecutor(new DoCommand(this));
		this.getCommand("cxp").setExecutor(new DoCommand(this));
		this.getCommand("cxp reload").setExecutor(new DoCommand(this));
	}
	
	// Load a new configuration file if the current one is missing. Stops people having to reboot for a clean file.
	public void tryReloadConfig()
	{
		File file = new File(getDataFolder() + File.separator+"config.yml");
		if(!file.exists())
		{
			Bukkit.getConsoleSender().sendMessage("§cClaimXP: Config file NOT found! Made a new one.");
			this.getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		this.reloadConfig();
	}
	
	@Override
	public void onDisable() 
	{
		Bukkit.getConsoleSender().sendMessage("§6ClaimXP 1.1 unloaded due to shutdown/restart!");
	}
}