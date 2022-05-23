package me.goodgamer123.DungeonsManager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin {
	
	@Override
	public void onEnable() {
		
		getCommand("donjonjoint").setExecutor(this);
		getCommand("back").setExecutor(this);
		getCommand("donjonmanager").setExecutor(this);
		
		getServer().getPluginManager().registerEvents(new DungeonTeleporter(), this);
		getServer().getPluginManager().registerEvents(new WaitingroomSelector(), this);
		getServer().getPluginManager().registerEvents(new GUIClickEvent(), this);
		
		if (!getDataFolder().exists()) DataManager.setup();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!DungeonTeleporter.starting.contains(p) && DungeonTeleporter.requestedWorlds.containsKey(p)) {
						if (DataManager.isInStartRegion(p.getLocation())) {
							DungeonTeleporter.StartCountdown(p);
						}
					}
				}
			}
		}, 20L, 20L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (World world : Bukkit.getWorlds()) {
					if (world.getPlayers().isEmpty() && !DungeonTeleporter.requestedWorlds.containsValue(world) && world.getName().startsWith("AAA-")) {
						DataManager.deleteWorld(world);
					}
				}
			}
		}, 1200L, 1200L);
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do this!");
			return false;
		}
		
		Player p = (Player) sender;
	    
		if (cmd.getName().equalsIgnoreCase("donjonjoint")) {
			
			if (DataManager.getWaitRoomLoc() == null) {
				p.sendMessage(ChatColor.RED + "Veuillez d'abord configurer l'emplacement de la salle d'attente avec: '/donjonmanager setwaitroom'.");
				return false;
			}
			
			if (!DataManager.StartRegionIsSet()) {
				p.sendMessage(ChatColor.RED + "Veuillez d'abord configurer l'emplacement de la salle d'attente avec: '/donjonmanager setstartregion'.");
				return false;
			}
			
			Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_AQUA + "Donjons");
			
			List<String> dungeons = DataManager.getDungeons();
			for (int i = 0; i < dungeons.size(); i++) {
				ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeons.get(i)));
				item.setItemMeta(itemMeta);
				inv.addItem(item);
			}
			
			p.openInventory(inv);
			
		} else if (cmd.getName().equalsIgnoreCase("donjonmanager")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("setwaitroom")) {
					
					DataManager.setWaitRoomLoc(p.getLocation());
					p.sendMessage(ChatColor.GREEN + "L'emplacement du téléporteur de la salle d'attente est mis à jour avec votre position actuelle!");
					
				} else if (args[0].equalsIgnoreCase("setstartregion")) {
					
					ItemStack stick = new ItemStack(Material.STICK);
					ItemMeta stickMeta = stick.getItemMeta();
					stickMeta.setDisplayName(ChatColor.AQUA + "Bâton de sélection");
					stick.setItemMeta(stickMeta);
					
					p.getEquipment().setItemInMainHand(stick);
					p.sendMessage(ChatColor.BLUE + "Sélectionnez une zone en cliquant à droite et à gauche sur un bloc avec ce bâton.");
					
				} else if (args[0].equalsIgnoreCase("setplayeramount")) {
					
					DungeonManager.setplayeramount(p, 1);
					
				} else {
					p.sendMessage(ChatColor.RED + "Mauvais argument! Utilisez '/donjonmanager [setwaitroom | setstartregion | setplayeramount]'.");
				}
			} else {
				p.sendMessage(ChatColor.RED + "Mauvais argument! Utilisez '/donjonmanager [setwaitroom | setstartregion | setplayeramount]'.");
			}
		} else if (cmd.getName().equalsIgnoreCase("back")) {
			p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
		}
		
		return false;
	}
	
}


