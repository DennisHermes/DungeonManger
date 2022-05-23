package me.goodgamer123.DungeonsManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataManager {

	public static void setup() {
		if (!MainClass.getPlugin(MainClass.class).getDataFolder().exists()) MainClass.getPlugin(MainClass.class).getDataFolder().mkdir();
		
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		if (!settingsFile.exists()) {
			FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
			
			try {
				settings.save(settingsFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//==================================================================================================================================================//
	
	public static List<String> getDungeons() {
		File[] files = MainClass.getPlugin(MainClass.class).getDataFolder().listFiles();
		List<String> worlds = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (new File(files[i] + "/level.dat").exists()) worlds.add(files[i].getName());
		}
		return worlds;
	}
	
	public static World loadMap(File file) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder stringB = new StringBuilder();
        Random rnd = new Random();
        while (stringB.length() < 6) {
            int index = (int) (rnd.nextFloat() * chars.length());
            stringB.append(chars.charAt(index));
        }
        String id = "AAA-" + stringB.toString();
        
        File dest = new File(MainClass.getPlugin(MainClass.class).getServer().getWorldContainer() + "/" + id);
        copyDirectory(file, dest);
		
		World world = new WorldCreator(id).createWorld();
		world.loadChunk(world.getSpawnLocation().getChunk());
		
        return world;
	}
	
	public static void copyDirectory(File sourceLocation , File targetLocation) {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            }
        } else {
            InputStream in;
			try {
				in = new FileInputStream(sourceLocation);
				OutputStream out = new FileOutputStream(targetLocation);
	            
	            byte[] buf = new byte[1024];
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	            in.close();
	            out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}
	
	public static void deleteWorld(World world) {
		File worldFolder = world.getWorldFolder();
		Bukkit.getServer().unloadWorld(world, false);
		deleteDir(worldFolder);
	}
	
	static void deleteDir(File file) {
	    File[] contents = file.listFiles();
	    if (contents != null) {
	        for (File f : contents) {
	            if (! Files.isSymbolicLink(f.toPath())) {
	                deleteDir(f);
	            }
	        }
	    }
	    file.delete();
	}
	
	//==================================================================================================================================================//
	
	public static void setWaitRoomLoc(Location loc) {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		settings.set("Waitingroom Location", loc);
		
		try {
			settings.save(settingsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setStartRegion(Location loc1, Location loc2) {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		settings.set("Start region pos1", loc1);
		settings.set("Start region pos2", loc2);
		
		try {
			settings.save(settingsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isInStartRegion(Location loc) {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		Location loc1 = settings.getLocation("Start region pos1");
		Location loc2 = settings.getLocation("Start region pos2");
		if (loc1 != null && loc2 != null) {
			double maxX = Math.max(loc1.getX(), loc2.getX());
	        double maxZ = Math.max(loc1.getZ(), loc2.getZ());

	        double minX = Math.min(loc1.getX(), loc2.getX());
	        double minZ = Math.min(loc1.getZ(), loc2.getZ());
	     
	        return ((loc.getX() > minX) && (loc.getZ() > minZ) && (loc.getX() < maxX) && (loc.getZ() < maxZ));
		} else {
			return false;
		}
	}
	
	public static boolean StartRegionIsSet() {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		Location loc1 = settings.getLocation("Start region pos1");
		Location loc2 = settings.getLocation("Start region pos2");
		return (loc1 != null && loc2 != null);
	}
	
	public static Location getWaitRoomLoc() {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		return settings.getLocation("Waitingroom Location");
	}
	
	public static boolean isPlayableWith(String dungeon, String players) {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		if (settings.isSet(dungeon)) {
			List<String> list = settings.getStringList(dungeon);
			return list.contains(players);
		} else {
			return true;
		}
	}
	
	public static void addPlayableWith(String dungeon, String players) {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		List<String> list = null;
		if (settings.isSet(dungeon)) {
			list = settings.getStringList(dungeon);
		} else {
			list = new ArrayList<String>();
			list.add("1");
			list.add("2");
			list.add("3");
			list.add("4");
		}
		
		list.add(players);
		
		settings.set(dungeon, list);
		try {
			settings.save(settingsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void removePlayableWith(String dungeon, String players) {
		File settingsFile = new File(MainClass.getPlugin(MainClass.class).getDataFolder() + "/Settings.yml");
		FileConfiguration settings = YamlConfiguration.loadConfiguration(settingsFile);
		
		List<String> list = null;
		if (settings.isSet(dungeon)) {
			list = settings.getStringList(dungeon);
		} else {
			list = new ArrayList<String>();
			list.add("1");
			list.add("2");
			list.add("3");
			list.add("4");
		}
		
		list.remove(players);
		
		settings.set(dungeon, list);
		try {
			settings.save(settingsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}