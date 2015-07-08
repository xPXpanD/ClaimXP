package rs.expand.claimxp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DoCommand implements CommandExecutor 
{
	private final ClaimXP plugin;
	
	// Link everything up with the master class, ClaimXP.
	public DoCommand(ClaimXP plugin) { this.plugin = plugin; }
	
	// Parse and show any incoming debug messages cleanly. Unused values are passed as null, and ignored. The entire thing won't show anything if print-debug-messages is off.
	public void tryDebug(ChatColor color, Integer num, Integer intargs, ItemStack stackargs)
	{
		int selectThis = num;
		String printThis = null;
		switch (selectThis)
		{
			case 0: printThis = "CXP Debug: Noticed somebody failed a permission check on /cxp reload."; break;
			case 1: printThis = "CXP Debug: Player has the right permissions. Moving on..."; break;
			case 2: printThis = "CXP Debug: Survival gamemode use is disallowed. Stopping."; break;
			case 3: printThis = "CXP Debug: Creative gamemode use is disallowed. Stopping."; break;
			case 4: printThis = "CXP Debug: Adventure gamemode use is disallowed. Stopping."; break;
			case 5: printThis = "CXP Debug: Spectator gamemode use is disallowed. Stopping."; break;
			case 6: printThis = "CXP Debug: Determining quantity set in config. Quantity: " + intargs; break;
			case 7: printThis = "CXP Debug: Using default quantity. Changes made may be invalid?"; break;
			case 8: printThis = "CXP Debug: Passed through quantity check. Quantity is now: " + intargs; break;
			case 9: printThis = "CXP Debug: freeSpace = " + intargs + ". addThis = " + stackargs + "."; break;
			case 10: printThis = "CXP Debug: Inventory is full, overfill is off. That's all, folks!"; break;
			case 11: printThis = "CXP Debug: Player had enough space, successfully gave it all!"; break;
			case 12: printThis = "CXP Debug: Overfill is on, items given out and excess dropped."; break;
			case 13: printThis = "CXP Debug: Unexpected 'else' called during giveouts. Please report!"; break;
			case 14: printThis = "CXP Debug: Not enough levels to exchange. Need " + String.valueOf(intargs) + " more."; break;
			case 15: printThis = "CXP Debug: Noticed somebody failed a permission check on /cxp. (exchange)"; break;
		}
		if (plugin.getConfig().getBoolean("print-debug-messages") == true)
			{ Bukkit.getConsoleSender().sendMessage(color + printThis); }
	}

	
	//if(player.getGameMode() == GameMode.SURVIVAL)
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("reload"))
			{
				// Shorten some colors here so we don't get massive lines later on. Convert everything to UPPERCASE.
				ChatColor debugColor = ChatColor.valueOf(plugin.getConfig().getString("debug-color").toUpperCase());
				
				// Does our player have the right permission? If so, reload the plugin's config from disk.
				if (sender.hasPermission("claimxp.reload"))
				{
					// Reloads the config. If the config is not there, generate a new one with the defaults and load that.
					plugin.tryReloadConfig();
					
					// Just a heads up on this message and later messages -- we use a regular expression (regexp) to replace all &-codes with valid ones.
					// This makes configuration a ton easier, as the &-codes are very common and easier to remember. For example, &k becomes \u00A7k.
					sender.sendMessage(plugin.getConfig().getString("succesfully-reloaded-config").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
					
					// Command completed! End the execution.
					return true;
				}
				
				// Failing the above, throw a custom no-permissions error. Not sure why, but I couldn't get the default message to return here.
				else 
				{
					tryDebug(debugColor, 0, null, null);
					sender.sendMessage(plugin.getConfig().getString("no-permissions-reload").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
					return false;
				}
			}
			else
			{
				sender.sendMessage(plugin.getConfig().getString("too-many-arguments").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
				return true;
			}
		}
		
		if (cmd.getName().equalsIgnoreCase("cxp") || cmd.getName().equalsIgnoreCase("claimxp"))
		{
			// Shorten some colors here so we don't get massive lines later on. Convert everything to UPPERCASE.
			ChatColor debugColor = ChatColor.valueOf(plugin.getConfig().getString("debug-color").toUpperCase());
			ChatColor errorColor = ChatColor.valueOf(plugin.getConfig().getString("error-color").toUpperCase());
			
			// Is the person typing /cxp (or /claimxp) an actual in-game player, or are we being called from the console?
			if (sender instanceof Player)
			{
				// At this point we'll define a player so the scripty bits further down the chain know who we're on about.
				Player player = (Player) sender;
				
				// Does our player have the right permission?
				if (player.hasPermission("claimxp.exchange"))
				{
					tryDebug(debugColor, 1, null, null);
					
					// Do a few gamemode checks. This should be fairly versatile, and it's a lot easier to implement than a big world checking loop.
					if (player.getGameMode() == GameMode.SURVIVAL && plugin.getConfig().getBoolean("allow-use-in-survival") == false)
					{
						player.sendMessage(plugin.getConfig().getString("survival-use-disallowed").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
						tryDebug(debugColor, 2, null, null);
						return true;
					}
					else if (player.getGameMode() == GameMode.CREATIVE && plugin.getConfig().getBoolean("allow-use-in-creative") == false)
					{
						player.sendMessage(plugin.getConfig().getString("creative-use-disallowed").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
						tryDebug(debugColor, 3, null, null);
						return true;
					}
					else if (player.getGameMode() == GameMode.ADVENTURE && plugin.getConfig().getBoolean("allow-use-in-adventure") == false)
					{
						player.sendMessage(plugin.getConfig().getString("adventure-use-disallowed").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
						tryDebug(debugColor, 4, null, null);
						return true;
					}
					else if (player.getGameMode() == GameMode.SPECTATOR && plugin.getConfig().getBoolean("allow-use-in-spectator") == false)
					{
						player.sendMessage(plugin.getConfig().getString("spectator-use-disallowed").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
						tryDebug(debugColor, 5, null, null);
						return true;
					}
					
					// See if the player's current level equals or exceeds the required level from the config.
					if (player.getLevel() >= plugin.getConfig().getInt("required-level"))
					{
						// Determine the configured quantity. Cap it out to reasonable values if out of range, or warn the user if it matches the default.
						Integer quantity = plugin.getConfig().getInt("quantity");
						tryDebug(debugColor, 6, quantity, null);
						if (quantity > 2304)
						{
							Bukkit.getConsoleSender().sendMessage(errorColor + "ClaimXP: Invalid quantity >2304! Config value: " + quantity + ". Using safe value of 64.");
							quantity = 64; 
						}
						if (quantity == 1) tryDebug(debugColor, 7, null, null);
						if (quantity < 1)
						{
							Bukkit.getConsoleSender().sendMessage(errorColor + "ClaimXP: Invalid quantity <1! Config value: " + quantity + ". Using safe minimum of 1.");
							quantity = 1; 
						}
						
						tryDebug(debugColor, 8, quantity, null);
						
						// Set up an ItemStack formatted with the config-set item and the now-cleaned quantity, as well as the damage value. Set the damage value to a safe value if it's null, too.
						Integer damageInt = Integer.valueOf(plugin.getConfig().getString("damage-value"));
						if (damageInt == null) damageInt = 0;
						ItemStack addThis = new ItemStack(Material.getMaterial(plugin.getConfig().getString("config-item")), quantity, damageInt.shortValue());
											
						// Credits to Barinade for a fair bit of the below loop! https://bukkit.org/threads/check-if-there-is-enough-space-in-inventory.134923/
						// Start searching the player's inventory for free space and items we can stack onto.
						int freeSpace = 0;
						for (ItemStack i : player.getInventory()) 
						{
							// The player has none of our defined item.
							if (i == null) freeSpace += addThis.getType().getMaxStackSize();

							// The player is already carrying one or more of our defined item.
							else if (i.getType() == addThis.getType()) freeSpace+=i.getType().getMaxStackSize() - i.getAmount();
						}
							
						tryDebug(debugColor, 9, freeSpace, addThis);
						
						// Here, we error out if somebody doesn't have any space at all AND overfill is off.
						if (freeSpace == 0 && plugin.getConfig().getBoolean("allow-and-drop-overfill") == false)
						{
						  	// Grab the "inventory-full" string from the config.yml, and pass it on. Stop the command.
						   	player.sendMessage(plugin.getConfig().getString("inventory-full").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
						   	tryDebug(debugColor, 10, null, null);
						   	return true;
						}
						
						// Moving on! The player has space, or overfill is on.
						else
						{
							// Subtract the level set in the config from the player's actual level, now that we've confirmed we're good to go.
							player.setLevel(player.getLevel() - (plugin.getConfig().getInt("required-level")));
								
							// Do we have enough space to start adding items freely? If so; do it!
							if (addThis.getAmount() <= freeSpace) 
							{
								// Actually add the items to a player's inventory. They have the space.
								player.getInventory().addItem(addThis);
								player.sendMessage(plugin.getConfig().getString("exchange-successful").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
								tryDebug(debugColor, 11, null, null);
							}
								
							// Is overfill allowed? If so, just drop everything we can't give. Another Barinade bit of scripting. Somebody give this dude a medal. Or a beer.
							else if (plugin.getConfig().getBoolean("allow-and-drop-overfill") == true)
							{
								// Give the player what he/she can hold. Write everything that's in excess away, and then explode from the player.
								HashMap<Integer, ItemStack> excess = player.getInventory().addItem(addThis);
								for (Map.Entry<Integer, ItemStack> me : excess.entrySet()) 
									{ player.getWorld().dropItem(player.getLocation(), me.getValue()); }
								
								// Let's have the player know they made an exchange, and some stuff dropped.
								player.sendMessage(plugin.getConfig().getString("exchange-successful-overfill").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
								
								tryDebug(debugColor, 12, null, null);
							}
							
							// Try to catch some issues here. Not sure what'd trigger this, if anything.
							else
							{
								// Show the player something went wrong, and tell them to contact an admin.
								player.sendMessage(plugin.getConfig().getString("unspecified-error").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
								
								// Set the player's level back up. This may seem a bit wasteful, but this whole statement is not supposed to be called anyways.
								player.setLevel(player.getLevel() + (plugin.getConfig().getInt("required-level")));
									
								tryDebug(debugColor, 13, null, null);
							}
								
							// Tell whatever's listening that this was a triumph; we're making some code here -- huge success.
							return true;
						}
					}
					else 
					{
						// Not enough experience! Format and toss the matching error, complete with support for special tags that show how many more levels are needed.
						Integer levelInt = (plugin.getConfig().getInt("required-level") - player.getLevel());
						Integer baseInt = plugin.getConfig().getInt("required-level");
						
						tryDebug(debugColor, 14, levelInt, null);
						
						// Not clean, but Java is annoying like that. Will clean up a little if I get better at this.
						if (baseInt - player.getLevel() == 1)
						{
							String levelString = plugin.getConfig().getString("lacking-levels-single").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2");
							player.sendMessage(levelString.replaceAll("\\{levels\\}", String.valueOf(levelInt)));
						}
						else
						{
							String levelString = plugin.getConfig().getString("lacking-levels-multiple").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2");
							player.sendMessage(levelString.replaceAll("\\{levels\\}", String.valueOf(levelInt)));
						}

						return true;
					}
				}
				
				// No permissions for /cxp (or /claimxp). Stop.
				else 
				{
					sender.sendMessage(plugin.getConfig().getString("no-permissions-exchange").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
					tryDebug(debugColor, 15, null, null);
					return true;
				}
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(errorColor + plugin.getConfig().getString("called-from-console").replaceAll("(&([a-f0-9klmnor]))", "\u00A7$2"));
				return true;
			}
		}
		
		// Required. Should not be called, though.
		return false;
	}
}