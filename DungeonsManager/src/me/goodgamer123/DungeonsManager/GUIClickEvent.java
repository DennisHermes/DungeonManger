package me.goodgamer123.DungeonsManager;

import java.util.ArrayList;
import java.util.List;

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
				
				e.getWhoClicked().openInventory(inv);
				
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
				
				e.getWhoClicked().openInventory(inv);
				
			}
		}
	}
	
}
