package me.goodgamer123.DungeonsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DungeonTeleporter implements Listener {

	static HashMap<Player, World> requestedWorlds = new HashMap<Player, World>();
	static List<Player> starting = new ArrayList<Player>();
	
	public static void StartSingleplayerCountdown(Player p) {
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
	
	public static void StartMultiplayerCountdown(Player p) {
		starting.add(p);
		for (int i = 0; i < 11; i++) {
			int i0 = i;
			new BukkitRunnable() { 
				@Override
				public void run() {
					String game = DataManager.getGameOfPlayer(p);
					if (game != null) {
						String[] players = DataManager.getPlayers(game);
						int requiered = DataManager.getGamesRequiredPlayers(game);
						if (players.length == requiered) {
							boolean ableToStart = true;
							for (String player : players) {
								Player foundPlayer = Bukkit.getPlayer(player);
								if (!DataManager.isInStartRegion(foundPlayer.getLocation())) ableToStart = false;
							}
							if (ableToStart) {
								if (i0 == 10) {
									for (String player : players) {
										Player foundPlayer = Bukkit.getPlayer(player);
										foundPlayer.sendTitle(ChatColor.DARK_AQUA + "Téléportation au donjon...", "", 1, 40, 1);
										foundPlayer.teleport(DungeonTeleporter.requestedWorlds.get(foundPlayer).getSpawnLocation());
										requestedWorlds.remove(foundPlayer);
										starting.remove(foundPlayer);
										DataManager.games.remove(game);
									}
								} else {
									p.sendTitle(ChatColor.DARK_AQUA + "À partir de:", ChatColor.AQUA + "" + (10 - i0) + " secondes", 1, 40, 1);
								}
							} else {
								starting.remove(p);
							}
						} else {
							p.sendTitle(ChatColor.RED + "Pas assez de joueurs", ChatColor.DARK_RED + "(" + players.length + "/" + requiered + ")", 1, 40, 1);
							starting.remove(p);
						}
					} else {
						starting.remove(p);
						p.sendTitle("", "", 1, 20, 1);
					}
				}
			}.runTaskLater(MainClass.getPlugin(MainClass.class), i * 20);
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		String game = DataManager.getGameOfPlayer(e.getPlayer());
		if (game != null) DataManager.leaveGame(e.getPlayer(), game);
		if (requestedWorlds.containsKey(e.getPlayer())) requestedWorlds.remove(e.getPlayer());
	}
	
}
