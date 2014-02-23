package com.sekwah.advancedportals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.sekwah.advancedportals.portalcontrolls.Portal;

public class AdvancedPortalsCommand implements CommandExecutor, TabCompleter {

	private AdvancedPortalsPlugin plugin;

	public AdvancedPortalsCommand(AdvancedPortalsPlugin plugin) {
		this.plugin = plugin;

		plugin.getCommand("advancedportals").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
		Player player = (Player)sender;
		ConfigAccessor config = new ConfigAccessor(plugin, "Config.yml");
		if(sender.hasPermission("advancedportals.portal")){
			if(args.length > 0){
				if(args[0].toLowerCase().equals("wand") || args[0].toLowerCase().equals("selector")){
					PlayerInventory inventory = player.getInventory();

					String ItemID = config.getConfig().getString("AxeItemId");

					Material WandMaterial;

					try
					{
						WandMaterial = Material.getMaterial(Integer.parseInt(ItemID));
					}
					catch(Exception e)
					{
						WandMaterial = Material.getMaterial(ItemID);
					}
					
					if(WandMaterial == null){
						WandMaterial = Material.IRON_AXE;
					}

					ItemStack regionselector = new ItemStack(WandMaterial);
					ItemMeta selectorname = regionselector.getItemMeta();
					selectorname.setDisplayName("�ePortal Region Selector");
					selectorname.setLore(Arrays.asList("�rThis wand with has the power to help"
							, "�r create portals bistowed upon it!"));
					regionselector.setItemMeta(selectorname);

					inventory.addItem(regionselector);
					sender.sendMessage("�a[�eAdvancedPortals�a] You have been given a �ePortal Region Selector�a!");
				}
				else if(args[0].toLowerCase().equals("portal") || args[0].toLowerCase().equals("portalblock")){
					PlayerInventory inventory = player.getInventory();

					ItemStack portalBlock = new ItemStack(Material.PORTAL);

					inventory.addItem(portalBlock);
					
					sender.sendMessage("�a[�eAdvancedPortals�a] You have been given a �ePortal Block�a!");
				}
				else if(args[0].toLowerCase().equals("create")) {
					if(player.hasMetadata("Pos1World") && player.hasMetadata("Pos2World")){
						if(player.getMetadata("Pos1World").get(0).asString().equals(player.getMetadata("Pos2World").get(0).asString()) && player.getMetadata("Pos1World").get(0).asString().equals(player.getLocation().getWorld().getName())){
							if(args.length >= 2){ // may make this next piece of code more efficient, maybe check against a list of available variables or something
								boolean hasName = false;
								boolean hasTriggerBlock = false;
								boolean hasDestination = false;
								boolean isBungeePortal = false;
								String destination = null;
								String portalName = null;
								String triggerBlock = null;
								String serverName = null;
								List<String> addArgs = new ArrayList<String>();
								for(int i = 1; i < args.length; i++){
									if(args[i].toLowerCase().startsWith("name:") && args[i].length() > 5){
										hasName = true;
										portalName = args[i].replaceFirst("name:", "");
									}
									else if(args[i].toLowerCase().startsWith("name:")) {
										player.sendMessage("�c[�7AdvancedPortals�c] You must include a name for the portal that isnt nothing!");
										return true;
									}
									else if(args[i].toLowerCase().startsWith("destination:") && args[i].length() > 12){
										hasDestination = true;
										destination = args[i].toLowerCase().replaceFirst("destination:", "");
									}
									else if(args[i].toLowerCase().startsWith("triggerblock:") && args[i].length() > 13){
										hasTriggerBlock = true;
										triggerBlock = args[i].toLowerCase().replaceFirst("triggerblock:", "");
									}
									else if(args[i].toLowerCase().startsWith("triggerblock:") && args[i].length() > 13){
										hasTriggerBlock = true;
										triggerBlock = args[i].toLowerCase().replaceFirst("triggerblock:", "");
									}
									else if(args[i].toLowerCase().startsWith("bungee:") && args[i].length() > 7){ // not completely implemented
										isBungeePortal = true;
										serverName = args[i].toLowerCase().replaceFirst("bungee:", "");
										addArgs.add(args[i]);
									}
								}
								if(!hasName){
									player.sendMessage("�c[�7AdvancedPortals�c] You must include a name for the portal that you are creating in the variables!");
									return true;
								}

								World world = org.bukkit.Bukkit.getWorld(player.getMetadata("Pos1World").get(0).asString());
								Location pos1 = new Location(world, player.getMetadata("Pos1X").get(0).asInt(), player.getMetadata("Pos1Y").get(0).asInt(), player.getMetadata("Pos1Z").get(0).asInt());
								Location pos2 = new Location(world, player.getMetadata("Pos2X").get(0).asInt(), player.getMetadata("Pos2Y").get(0).asInt(), player.getMetadata("Pos2Z").get(0).asInt());
								
								ConfigAccessor portalconfig = new ConfigAccessor(plugin, "Portals.yml");
								String posX = portalconfig.getConfig().getString(portalName + ".pos1.X");
								
								ConfigAccessor desticonfig = new ConfigAccessor(plugin, "Destinations.yml");
								String destiPosX = desticonfig.getConfig().getString(destination + ".pos1.X");
								
								if(posX == null){
									
									player.sendMessage("");
									player.sendMessage("�a[�eAdvancedPortals�a]�e You have created a new portal with the following details:");
									player.sendMessage("�aname: �e" + portalName);
									if(hasDestination){
										player.sendMessage("�adestination: �e" + destination);
									}
									else if(destiPosX == null){
										player.sendMessage("�cdestination: �e" + destination + " (undefined destination)");
									}
									else{
										player.sendMessage("�cdestination: �eN/A (will not work)");
									}
									
									if(isBungeePortal){
										player.sendMessage("�abungee: �e" + serverName);
									}
									
									Material triggerBlockMat = Material.getMaterial(0);
									if(hasTriggerBlock){

										try
										{
											triggerBlockMat = Material.getMaterial(Integer.parseInt(triggerBlock));
											player.sendMessage("�atriggerBlock: �e" + triggerBlock.toUpperCase());
											player.sendMessage(Portal.create(pos1, pos2, portalName, destination, triggerBlockMat, addArgs));
										}
										catch(Exception e)
										{
											try
											{
												triggerBlockMat = Material.getMaterial(triggerBlock.toUpperCase());
												player.sendMessage("�atriggerBlock: �e" + triggerBlock.toUpperCase());
												player.sendMessage(Portal.create(pos1, pos2, portalName, destination, triggerBlockMat, addArgs));
											}
											catch(Exception exeption)
											{
												hasTriggerBlock = false;
												ConfigAccessor Config = new ConfigAccessor(plugin, "Config.yml");
												player.sendMessage("�ctriggerBlock: �edefault(" + Config.getConfig().getString("DefaultPortalTriggerBlock") + ")");
												
												player.sendMessage("�cThe block " + triggerBlock.toUpperCase() + " is not a valid block name in minecraft so the trigger block has been set to the default!");
												player.sendMessage(Portal.create(pos1, pos2, portalName, destination, addArgs));
											}
										}

									}
									else{
										ConfigAccessor Config = new ConfigAccessor(plugin, "Config.yml");
										player.sendMessage("�atriggerBlock: �edefault(" + Config.getConfig().getString("DefaultPortalTriggerBlock") + ")");
										player.sendMessage(Portal.create(pos1, pos2, portalName, destination, addArgs));
									}
								}
								else{
									sender.sendMessage("�c[�7AdvancedPortals�c] A portal by that name already exists!");
								}

								// add code to save the portal to the portal config and reload the portals

								player.sendMessage("");
							}
							else{
								player.sendMessage("�c[�7AdvancedPortals�c] You need to at least add the name of the portal as a variable, �cType �e/portal variables�c"
										+ " for a full list of currently available variables and an example command!");
							}
						}
						else{
							player.sendMessage("�c[�7AdvancedPortals�c] The points you have selected need to be in the same world!");
						}
					}
					else{
						player.sendMessage("�c[�7AdvancedPortals�c] You need to have two points selected to make a portal!");
					}
				}
				else if(args[0].toLowerCase().equals("variables")) {
					player.sendMessage("�a[�eAdvancedPortals�a] Currently available variables: name, triggerBlock, destination");
					player.sendMessage("");
					player.sendMessage("�aExample command: �e/portal create name:test triggerId:portal");
				}
				else if(args[0].toLowerCase().equals("select")) {
					
					// TODO finish the select command and the hit block to replace!
					
					if(player.hasMetadata("selectingPortal")){
						player.sendMessage("�a[�eAdvancedPortals�a] Hit a block inside the portal region to select the portal!");
						player.setMetadata("selectingPortal", new FixedMetadataValue(plugin, true));
					}
					else{
						player.sendMessage("�c[�7AdvancedPortals�c] You are already selecting a portal!");
					}
				}
				else if(args[0].toLowerCase().equals("remove")) {
					ConfigAccessor portalConfig = new ConfigAccessor(plugin, "Portals.yml");
					if(args.length > 1){
						String posX = portalConfig.getConfig().getString(args[1] + ".pos1.X");
						if(posX != null){
							Portal.remove(args[1]);
							sender.sendMessage("�a[�eAdvancedPortals�a] Portal removed!");
						}
						else{
							sender.sendMessage("�c[�7AdvancedPortals�c] No portal by that name exists!");
						}
					}
				}
				else if(args[0].toLowerCase().equals("bukkitpage")) {
					player.sendMessage("�a[�eAdvancedPortals�a] Bukkit page: (insert bitly link)!");
				}
				else if(args[0].toLowerCase().equals("helppage")) {
					player.sendMessage("�a[�eAdvancedPortals�a] Help page: (insert bitly link)!");
				}
				else if(args[0].toLowerCase().equals("show")){
					if(player.hasMetadata("Pos1World") && player.hasMetadata("Pos2World")){
						if(player.getMetadata("Pos1World").get(0).asString().equals(player.getMetadata("Pos2World").get(0).asString()) && player.getMetadata("Pos1World").get(0).asString().equals(player.getLocation().getWorld().getName())){
							player.sendMessage("�a[�eAdvancedPortals�a] Your currently selected area has been shown, it will dissapear shortly!");
							Selection.Show(player, this.plugin);
						}
						else{
							player.sendMessage("�c[�7AdvancedPortals�c] The points you have selected need to be in the same world!");
						}
					}
					else{
						player.sendMessage("�c[�7AdvancedPortals�c] You need to have both points selected!");
					}
				}
				else if(args[0].toLowerCase().equals("help")) {
					player.sendMessage("�a[�eAdvancedPortals�a] Reloaded values!");
					Listeners.reloadValues(plugin);
					Portal.loadPortals();
				}
				else{
					PluginMessages.UnknownCommand(sender, command);
				}
			}
			else{
				PluginMessages.UnknownCommand(sender, command);
			}

		}
		else{
			PluginMessages.NoPermission(sender, command);
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String command, String[] args) {
		LinkedList<String> autoComplete = new LinkedList<String>();
		if(sender.hasPermission("AdvancedPortals.CreatePortal")){
			if(args.length == 1){
				autoComplete.addAll(Arrays.asList("create", "portal", "portalblock", "select", "selector"
						, "show", "variables", "wand", "remove", "rename", "help", "bukkitpage", "helppage"));
			}
			else if(args[0].toLowerCase().equals("create")){
				
				boolean hasName = false;
				boolean hasTriggerBlock = false;
				boolean hasDestination = false;
				boolean isBungeePortal = false;
				
				for(int i = 1; i < args.length; i++){
					if(args[i].toLowerCase().startsWith("name:") && args[i].length() > 5){
						hasName = true;
					}
					else if(args[i].toLowerCase().startsWith("destination:") && args[i].length() > 12){
						hasDestination = true;
					}
					else if(args[i].toLowerCase().startsWith("triggerblock:") && args[i].length() > 13){
						hasTriggerBlock = true;
					}
					else if(args[i].toLowerCase().startsWith("bungee:") && args[i].length() > 7){
						isBungeePortal = true;
					}
					
				}
				
				if(!hasName){autoComplete.add("name:");}
				if(!hasTriggerBlock){autoComplete.add("triggerblock:");}
				if(!hasDestination){autoComplete.add("destination:");}
				if(!isBungeePortal){autoComplete.add("bungee:");}
			}
		}
		Collections.sort(autoComplete);
		for(Object result: autoComplete.toArray()){
			if(!result.toString().startsWith(args[args.length - 1])){
				autoComplete.remove(result);
			}
		}
		return autoComplete;
	}

}
