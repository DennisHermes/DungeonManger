package me.goodgamer123.DungeonsManager;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WaitingroomSelector implements Listener {

	HashMap<Player, Location> pos1 = new HashMap<Player, Location>();
	HashMap<Player, Location> pos2 = new HashMap<Player, Location>();
	
	@EventHandler
	public void posSelection(PlayerInteractEvent e) {
		if (e.getItem() == null) return;
		
		if (e.getItem().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "B�ton de s�lection")) {
			
			e.setCancelled(true);
			
			if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) pos1.put(e.getPlayer(), e.getClickedBlock().getLocation());
			else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) pos2.put(e.getPlayer(), e.getClickedBlock().getLocation());
			
			if (pos1.get(e.getPlayer()) != null && pos2.get(e.getPlayer()) != null) {
				DataManager.setStartRegion(pos1.get(e.getPlayer()), pos2.get(e.getPlayer()));
				pos1.put(e.getPlayer(), null);
				pos2.put(e.getPlayer(), null);
				e.getPlayer().getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
				e.getPlayer().sendMessage(ChatColor.GREEN + "R�gion de d�part d�finie avec succ�s!");
			}
			
		}
	}
	
}
