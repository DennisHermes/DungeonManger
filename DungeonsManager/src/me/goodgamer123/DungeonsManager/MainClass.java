package me.goodgamer123.DungeonsManager;

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
							if (DataManager.getGameOfPlayer(p) != null) {
								
							}
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
			
			Inventory inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Combien de joueurs?");
			
			ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
			ItemMeta fillingMeta = filling.getItemMeta();
			fillingMeta.setDisplayName(" ");
			filling.setItemMeta(fillingMeta);
			
			ItemStack players = new ItemStack(Material.TOTEM_OF_UNDYING);
			ItemMeta playersMeta = players.getItemMeta();
			playersMeta.setDisplayName(ChatColor.AQUA + "Jouer avec 1 joueur");
			players.setItemMeta(playersMeta);
			
			ItemStack single = new ItemStack(Material.POPPY);
			ItemMeta singleMeta = single.getItemMeta();
			singleMeta.setDisplayName(ChatColor.AQUA + "Solo");
			single.setItemMeta(singleMeta);
			
			ItemStack multi = new ItemStack(Material.ROSE_BUSH);
			ItemMeta multiMeta = multi.getItemMeta();
			multiMeta.setDisplayName(ChatColor.AQUA + "Multijoueur");
			multi.setItemMeta(multiMeta);
			
			ItemStack close = new ItemStack(Material.BARRIER);
			ItemMeta closeMeta = filling.getItemMeta();
			closeMeta.setDisplayName(ChatColor.RED + "Fermer");
			close.setItemMeta(closeMeta);
			
			for (int i = 0; i < inv.getSize(); i++) {
				inv.setItem(i, filling);
			}
			
			inv.setItem(11, single);
			inv.setItem(14, multi);
			inv.setItem(40, close);
			
			for (int i = 2; i < 6; i++) {
				if (i == 2) inv.setItem(20, players);
				else inv.setItem(19 + i, players);
				playersMeta.setDisplayName(ChatColor.AQUA + "" + i + " Joueurs");
				players.setItemMeta(playersMeta);
				players.setAmount(i);
			}
			
			p.openInventory(inv);
			
		} else if (cmd.getName().equalsIgnoreCase("donjonmanager")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("setwaitroom")) {
					
					DataManager.setWaitRoomLoc(p.getLocation());
					p.sendMessage(ChatColor.GREEN + "L'emplacement du t�l�porteur de la salle d'attente est mis � jour avec votre position actuelle!");
					
				} else if (args[0].equalsIgnoreCase("setstartregion")) {
					
					ItemStack stick = new ItemStack(Material.STICK);
					ItemMeta stickMeta = stick.getItemMeta();
					stickMeta.setDisplayName(ChatColor.AQUA + "B�ton de s�lection");
					stick.setItemMeta(stickMeta);
					
					p.getEquipment().setItemInMainHand(stick);
					p.sendMessage(ChatColor.BLUE + "S�lectionnez une zone en cliquant � droite et � gauche sur un bloc avec ce b�ton.");
					
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


