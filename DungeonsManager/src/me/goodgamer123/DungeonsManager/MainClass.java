package me.goodgamer123.DungeonsManager;

import java.util.ArrayList;
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
		getCommand("donjonmanager").setExecutor(this);
		
		getServer().getPluginManager().registerEvents(new DungeonTeleporter(), this);
		getServer().getPluginManager().registerEvents(new WaitingroomSelector(), this);
		getServer().getPluginManager().registerEvents(new GUIClickEvent(), this);
		
		if (!getDataFolder().exists()) DataManager.setup();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				List<Player> checked = new ArrayList<Player>();
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!DungeonTeleporter.starting.contains(p) && DungeonTeleporter.requestedWorlds.containsKey(p) && !checked.contains(p)) {
						String game = DataManager.getGameOfPlayer(p);
						if (game != null) {
							String[] players = DataManager.getPlayers(game);
							int requiered = DataManager.getGamesRequiredPlayers(game);
							if (players.length == requiered) {
								boolean ableToStart = true;
								for (String player : players) {
									Player foundPlayer = Bukkit.getPlayer(player);
									checked.add(foundPlayer);
									if (!DataManager.isInStartRegion(foundPlayer.getLocation())) ableToStart = false;
								}
								if (ableToStart) {
									for (String player : players) {
										DungeonTeleporter.StartMultiplayerCountdown(Bukkit.getPlayer(player));
									}
								}
							} else {
								if (DataManager.isInStartRegion(p.getLocation())) p.sendTitle(ChatColor.RED + "Pas assez de joueurs", ChatColor.DARK_RED + "(" + players.length + "/" + requiered + ")", 1, 40, 1);
							}
						} else {
							if (DataManager.isInStartRegion(p.getLocation())) DungeonTeleporter.StartSingleplayerCountdown(p);
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
					
				} else if (args[0].equalsIgnoreCase("setmusic")) {
					if (p.getEquipment().getItemInMainHand() == null) {
						p.sendMessage(ChatColor.RED + "Veuillez tenir un disque de musique dans votre main.");
						return false;
					}
					
					if (!p.getEquipment().getItemInMainHand().getType().isRecord()) {
						p.sendMessage(ChatColor.RED + "Veuillez tenir un disque de musique dans votre main.");
						return false;
					}
					
					ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
					ItemMeta fillingMeta = filling.getItemMeta();
					fillingMeta.setDisplayName(" ");
					filling.setItemMeta(fillingMeta);
					
					ItemStack close = new ItemStack(Material.BARRIER);
					ItemMeta closeMeta = filling.getItemMeta();
					closeMeta.setDisplayName(ChatColor.RED + "Fermer");
					close.setItemMeta(closeMeta);
					
					ItemStack next = new ItemStack(Material.ARROW);
					ItemMeta nextMeta = next.getItemMeta();
					nextMeta.setDisplayName(ChatColor.AQUA + "Page suivante");
					next.setItemMeta(nextMeta);
					
					int itemCount = 0;
					List<ItemStack> items = new ArrayList<ItemStack>(); 
					List<String> dungeons = DataManager.getDungeons();
					
					for (int i = 0; i < dungeons.size(); i++) {
						ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeons.get(i)));
						List<String> itemLore = new ArrayList<String>();
						itemLore.add(" ");
						if (DataManager.getMusic(dungeons.get(i)) == null) itemLore.add(ChatColor.RED + "Not set.");
						else itemLore.add(ChatColor.DARK_AQUA + DataManager.getMusic(dungeons.get(i)).getType().name().toLowerCase().replace('_', ' '));
						itemMeta.setLore(itemLore);
						item.setItemMeta(itemMeta);
						items.add(item);
						itemCount++;
					}
					
					Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");
					if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");
					else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");
					else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Pour quel donjon? - Page 1");
					
					for (int i = 0; i < items.size(); i++) {
						inv.addItem(items.get(i));
					}
					
					for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
						inv.setItem(i, filling);
					}
					
					inv.setItem(inv.getSize() - 5, close);
					if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);
					
					p.openInventory(inv);
					
				} else {
					p.sendMessage(ChatColor.RED + "Mauvais argument! Utilisez '/donjonmanager [setwaitroom | setstartregion | setplayeramount | setmusic]'.");
				}
				
			} else {
				p.sendMessage(ChatColor.RED + "Mauvais argument! Utilisez '/donjonmanager [setwaitroom | setstartregion | setplayeramount | setmusic]'.");
			}
			
		}
		
		return false;
	}
	
}


