package me.goodgamer123.DungeonsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DungeonTeleporter implements Listener {

	static HashMap<Player, World> requestedWorlds = new HashMap<Player, World>();
	static List<Player> starting = new ArrayList<Player>();
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(ChatColor.DARK_AQUA + "Donjons")) {
			
			if (e.getCurrentItem() == null) return;
			if (!e.getCurrentItem().getType().equals(Material.CHISELED_STONE_BRICKS)) return;
			
			e.setCancelled(true);
			e.getWhoClicked().closeInventory();
			e.getWhoClicked().teleport(DataManager.getWaitRoomLoc());
			e.getWhoClicked().sendMessage(ChatColor.AQUA + "Le monde est en train de se charger, veuillez patienter...");
			
			String worldName = e.getCurrentItem().getItemMeta().getDisplayName().replace('§', '&');
			File worldFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/" + worldName);
			
			requestedWorlds.put((Player) e.getWhoClicked(), DataManager.loadMap(worldFile));
		}
	}
	
	public static void StartCountdown(Player p) {
		starting.add(p);
		for (int i = 0; i < 11; i++) {
			int i0 = i;
			new BukkitRunnable() { 
				@Override
				public void run() {
					if (DataManager.isInStartRegion(p.getLocation())) {
						if (i0 == 10) {
							p.sendTitle(ChatColor.DARK_AQUA + "Téléportation au donjon...", "", 1, 40, 1);
							p.teleport(DungeonTeleporter.requestedWorlds.get(p).getSpawnLocation());
							requestedWorlds.remove(p);
							starting.remove(p);
						} else {
							p.sendTitle(ChatColor.DARK_AQUA + "À partir de:", ChatColor.AQUA + "" + (10 - i0) + " secondes", 1, 40, 1);
						}
					} else {
						starting.remove(p);
						p.sendTitle("", "", 1, 20, 1);
					}
				}
			}.runTaskLater(MainClass.getPlugin(MainClass.class), i * 20);
		}
	}
	
}
