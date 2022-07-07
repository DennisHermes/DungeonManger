package me.goodgamer123.DungeonsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class GUIClickEvent implements Listener {

	Map<Player, Integer> selectedPlayers = new HashMap<Player, Integer>();
	
	@EventHandler
	public void dungeonPlayers(InventoryClickEvent e) {
		if (e.getSlot() > e.getView().getTopInventory().getSize()) return;
		if (e.getCurrentItem() == null) return;
		
		if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "DonjonManager: Donjons - Page ")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.GREEN_TERRACOTTA) || e.getCurrentItem().getType().equals(Material.RED_TERRACOTTA)) {
				
				int players = 1;
				if (e.getCurrentItem().getItemMeta().getDisplayName().contains("2")) players = 2;
				else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("3")) players = 3;
				else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("4")) players = 4;
				
				String mapName = e.getInventory().getItem(e.getSlot() - (players + 1)).getItemMeta().getDisplayName().replace('§', '&');
				int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("DonjonManager: Donjons - Page ", "")));
				
				if (e.getCurrentItem().getType().equals(Material.GREEN_TERRACOTTA))  DataManager.removePlayableWith(mapName, players + "");
				else DataManager.addPlayableWith(mapName, players + "");
				
				new BukkitRunnable() { 
					@Override
					public void run() {
						DungeonManager.setplayeramount((Player) e.getWhoClicked(), currentPage);
					}
				}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
				
			} else if (e.getCurrentItem().getType().equals(Material.ARROW)) {
				
				int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("DonjonManager: Donjons - Page ", "")));
				
				new BukkitRunnable() { 
					@Override
					public void run() {
						if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Page suivante")) DungeonManager.setplayeramount((Player) e.getWhoClicked(), currentPage + 1);
						else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Page précédente")) DungeonManager.setplayeramount((Player) e.getWhoClicked(), currentPage - 1);
					}
				}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
					
			}
		}
		
		
		
		else if (e.getView().getTitle().equals(ChatColor.DARK_AQUA + "Combien de joueurs?")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.TOTEM_OF_UNDYING)) {
				
				selectedPlayers.put((Player) e.getWhoClicked(), e.getCurrentItem().getAmount());
				
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
					if (DataManager.isPlayableWith(dungeons.get(i), e.getCurrentItem().getAmount() + "")) {
						ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeons.get(i)));
						item.setItemMeta(itemMeta);
						items.add(item);
						itemCount++;
					}
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Donjons - Page 1");
				if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Donjons - Page 1");
				else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Donjons - Page 1");
				else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Donjons - Page 1");
				
				for (int i = 0; i < items.size(); i++) {
					inv.addItem(items.get(i));
				}
				
				for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}
				
				inv.setItem(inv.getSize() - 5, close);
				if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);

				Inventory inv0 = inv;
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getWhoClicked().openInventory(inv0);
					}
				}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
				
			}
			
		} 
		
		
		
		else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Donjons - Page ")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.ARROW)) {
				
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
				
				ItemStack previous = new ItemStack(Material.ARROW);
				ItemMeta previousMeta = previous.getItemMeta();
				previousMeta.setDisplayName(ChatColor.AQUA + "Page précédente");
				previous.setItemMeta(previousMeta);
				
				int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("Donjons - Page ", "")));
				int newPage = currentPage + 1;
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Page précédente")) newPage = currentPage - 1;
				
				int itemCount = 0;
				List<ItemStack> items = new ArrayList<ItemStack>(); 
				List<String> dungeons = DataManager.getDungeons();
				
				for (int i = (newPage - 1) * 36; i < dungeons.size(); i++) {
					if (DataManager.isPlayableWith(dungeons.get(i), e.getCurrentItem().getAmount() + "")) {
						ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', dungeons.get(i)));
						item.setItemMeta(itemMeta);
						items.add(item);
						itemCount++;
					}
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);
				if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);
				else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);
				else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Donjons - Page " + newPage);
				
				for (int i = 0; i < items.size(); i++) {
					inv.addItem(items.get(i));
				}
				
				for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}
				
				inv.setItem(inv.getSize() - 5, close);
				if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);
				if (newPage > 1) inv.setItem(inv.getSize() - 6, previous);
				
				Inventory inv0 = inv;
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getWhoClicked().openInventory(inv0);
					}
				}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
				
			} else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {
				
				if (selectedPlayers.get(e.getWhoClicked()) == 1) {
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().teleport(DataManager.getWaitRoomLoc());
					e.getWhoClicked().sendMessage(ChatColor.AQUA + "Le monde est en train de se charger, veuillez patienter...");
					
					String game = DataManager.getGameOfPlayer((Player) e.getWhoClicked());
					if (game != null) DataManager.leaveGame((Player) e.getWhoClicked(), game);
					if (DungeonTeleporter.requestedWorlds.containsKey(e.getWhoClicked())) DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());
					
					String worldName = e.getCurrentItem().getItemMeta().getDisplayName().replace('§', '&');
					File worldFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/" + worldName);
					
					DungeonTeleporter.requestedWorlds.put((Player) e.getWhoClicked(), DataManager.loadMap(worldFile));
					DungeonTeleporter.requestedDonjon.put((Player) e.getWhoClicked(), worldName);
				} else {
					Inventory inv = Bukkit.createInventory(null, 27, ChatColor.AQUA + "Donjon: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', e.getCurrentItem().getItemMeta().getDisplayName()));
					
					ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
					ItemMeta fillingMeta = filling.getItemMeta();
					fillingMeta.setDisplayName(" ");
					filling.setItemMeta(fillingMeta);
					
					ItemStack newGame = new ItemStack(Material.ARROW);
					ItemMeta newGameMeta = newGame.getItemMeta();
					newGameMeta.setDisplayName(ChatColor.AQUA + "Créer un nouveau jeu");
					newGame.setItemMeta(newGameMeta);
					
					ItemStack joinGame = new ItemStack(Material.TIPPED_ARROW);
					ItemMeta joinGameMeta = joinGame.getItemMeta();
					joinGameMeta.setDisplayName(ChatColor.AQUA + "Rejoindre une jeu existant");
					joinGame.setItemMeta(joinGameMeta);
					
					ItemStack close = new ItemStack(Material.BARRIER);
					ItemMeta closeMeta = close.getItemMeta();
					closeMeta.setDisplayName(ChatColor.RED + "Fermer");
					close.setItemMeta(closeMeta);
					
					for (int i = 0; i < inv.getSize(); i++) {
						inv.setItem(i, filling);
					}
					
					inv.setItem(11, newGame);
					inv.setItem(15, joinGame);
					inv.setItem(22, close);
					
					Inventory inv0 = inv;
					new BukkitRunnable() {
						@Override
						public void run() {
							e.getWhoClicked().openInventory(inv0);
						}
					}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
				}
				
			}
		}
		
		
		
		else if (e.getView().getTitle().startsWith(ChatColor.AQUA + "Donjon: ")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.ARROW)) {
				
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().teleport(DataManager.getWaitRoomLoc());
				e.getWhoClicked().sendMessage(ChatColor.AQUA + "Le monde est en train de se charger, veuillez patienter...");
				
				String game = DataManager.getGameOfPlayer((Player) e.getWhoClicked());
				if (game != null) DataManager.leaveGame((Player) e.getWhoClicked(), game);
				if (DungeonTeleporter.requestedWorlds.containsKey(e.getWhoClicked())) DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());
				
				DataManager.createGame((Player) e.getWhoClicked(), e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: " + ChatColor.RESET, "").replace('§', '&'), selectedPlayers.get(e.getWhoClicked()));
				
			} else if (e.getCurrentItem().getType().equals(Material.TIPPED_ARROW)) {
				
				ItemStack close = new ItemStack(Material.BARRIER);
				ItemMeta closeMeta = close.getItemMeta();
				closeMeta.setDisplayName(ChatColor.RED + "Fermer");
				close.setItemMeta(closeMeta);
				
				ItemStack filling = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
				ItemMeta fillingMeta = filling.getItemMeta();
				fillingMeta.setDisplayName(" ");
				filling.setItemMeta(fillingMeta);
				
				int itemCount = 0;
				List<ItemStack> items = new ArrayList<ItemStack>(); 
				List<String> dungeons = DataManager.getGames(selectedPlayers.get(e.getWhoClicked()), e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: " + ChatColor.RESET, ""));
				
				for (int i = 0; i < dungeons.size(); i++) {
					ItemStack item = new ItemStack(Material.CHISELED_STONE_BRICKS);
					ItemMeta itemMeta = item.getItemMeta();
					itemMeta.setDisplayName(ChatColor.AQUA + "Game: " + dungeons.get(i));
					List<String> itemLore = new ArrayList<String>();
					String[] players = DataManager.getPlayers("AAA-" + dungeons.get(i));
					itemLore.add(" ");
					itemLore.add(ChatColor.DARK_AQUA + "Joueurs en attente (" + players.length + "/" + selectedPlayers.get(e.getWhoClicked()) + "):");
					for (int i0 = 0; i0 < players.length; i0++) {
						itemLore.add(ChatColor.AQUA + " - " + players[i0]);
					}
					itemMeta.setLore(itemLore);
					item.setItemMeta(itemMeta);
					items.add(item);
					itemCount++;
				}
				
				Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));
				if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));
				else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));
				else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Donjon join: " + e.getView().getTitle().replace(ChatColor.AQUA + "Donjon: ", ""));
				
				for (int i = 0; i < items.size(); i++) {
					inv.addItem(items.get(i));
				}
				
				for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}
				inv.setItem(inv.getSize() - 5, close);
				
				Inventory inv0 = inv;
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getWhoClicked().openInventory(inv0);
					}
				}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
				
			}
		} 
		
		
		
		else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Donjon join: ")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {
				
				if (DataManager.getPlayers("AAA-" + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.AQUA + "Game: ", "")).length < selectedPlayers.get(e.getWhoClicked())) {
					
					String game = DataManager.getGameOfPlayer((Player) e.getWhoClicked());
					if (game != null) {
						if (game.equals("AAA-" + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.AQUA + "Game: ", ""))) {
							e.getWhoClicked().sendMessage(ChatColor.RED + "Vous êtes déjà dans ce jeu");
							return;
						} else {
							DataManager.leaveGame((Player) e.getWhoClicked(), game);
						}
					}
					if (DungeonTeleporter.requestedWorlds.containsKey(e.getWhoClicked())) DungeonTeleporter.requestedWorlds.remove(e.getWhoClicked());
					
					e.getWhoClicked().closeInventory();
					e.getWhoClicked().teleport(DataManager.getWaitRoomLoc());
					DataManager.joinGame((Player) e.getWhoClicked(), "AAA-" + e.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.AQUA + "Game: ", ""));
				} else {
					e.getWhoClicked().sendMessage(ChatColor.RED + "Le jeu est complet!");
				}
				
			}
		}
		
		
		
		else if (e.getView().getTitle().startsWith(ChatColor.DARK_AQUA + "Pour quel donjon?")) {
			e.setCancelled(true);
			
			if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
				
				e.getWhoClicked().closeInventory();
				
			} else if (e.getCurrentItem().getType().equals(Material.ARROW)) {
				
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
				
				ItemStack previous = new ItemStack(Material.ARROW);
				ItemMeta previousMeta = previous.getItemMeta();
				previousMeta.setDisplayName(ChatColor.AQUA + "Page précédente");
				previous.setItemMeta(previousMeta);
				
				int currentPage = Integer.parseInt(ChatColor.stripColor(e.getView().getTitle().replace("Pour quel donjon? - Page ", "")));
				int newPage = currentPage + 1;
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Page précédente")) newPage = currentPage - 1;
				
				int itemCount = 0;
				List<ItemStack> items = new ArrayList<ItemStack>(); 
				List<String> dungeons = DataManager.getDungeons();
				
				for (int i = (newPage - 1) * 36; i < dungeons.size(); i++) {
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
				
				Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);
				if (itemCount <= 9) inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);
				else if (itemCount <= 18) inv = Bukkit.createInventory(null, 36, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);
				else if (itemCount <= 27) inv = Bukkit.createInventory(null, 45, ChatColor.DARK_AQUA + "Pour quel donjon? - Page " + newPage);
				
				for (int i = 0; i < items.size(); i++) {
					inv.addItem(items.get(i));
				}
				
				for (int i = (inv.getSize() - 18); i < inv.getSize(); i++) {
					inv.setItem(i, filling);
				}
				
				inv.setItem(inv.getSize() - 5, close);
				if (itemCount > 36) inv.setItem(inv.getSize() - 4, next);
				if (newPage > 1) inv.setItem(inv.getSize() - 6, previous);
				
				Inventory inv0 = inv;
				new BukkitRunnable() {
					@Override
					public void run() {
						e.getWhoClicked().openInventory(inv0);
					}
				}.runTaskLater(MainClass.getPlugin(MainClass.class), 1);
				
			} else if (e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) {
				
				DataManager.setMusic(e.getWhoClicked().getEquipment().getItemInMainHand(), e.getCurrentItem().getItemMeta().getDisplayName().replace('§', '&'));
				e.getWhoClicked().closeInventory();
				e.getWhoClicked().sendMessage(ChatColor.GREEN + "Mise en place réussie de la musique!");
				
			}
		}
	}
	
}
