package com.adognamedspot.rename;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public final class Rename extends JavaPlugin implements Listener {
	
	int MAX_CHAR;
	int Rename_Cost;
	String Rate_Type;
	float Discount1;
	float Discount2;
	float Discount3;
	float Discount4;
	float Discount5;
	float Discount6;
	float Discount7;
	float Discount8;
	
	private static Rename instance;
	
    @Override
    public void onEnable() {
        instance = this;
        validateFiles();
    	getServer().getPluginManager().registerEvents(this, this);
    	loadConfig();
    	getLogger().info("Item Rename - by A DoG NaMeD SpoT - Enabled");
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("Item Rename - by A DoG NaMeD SpoT - Disabled");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	Player p = null;
    	
    	if (sender instanceof Player) {
    		p = (Player) sender;
    	} else {
    		sender.sendMessage("Command cannot be run from the console.");
    		return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("rename-reload")) {
    		loadConfig();
    		p.sendMessage("Configuration reloaded!");
    		return true;
    	}
    	if (cmd.getName().equalsIgnoreCase("rename")) {
    		if (args.length < 1) {
    			removeName(p);
    		} else {
    			changeName(p, args);
    		}
    		return true;
    	} 
    	if (cmd.getName().equalsIgnoreCase("renameall")) {
    		if (args.length < 1) {
    			removeNames(p);
    		} else {
    			changeNames(p, args);
    		}
    		return true;
    	} 
    	return false;
    }

	private void changeNames(Player p, String[] args) {
		ItemStack item = p.getInventory().getItemInMainHand();
		String str = formatArgs(args);
		if (item.getType().equals(Material.AIR)) {
			p.sendMessage(ChatColor.RED + "Nothing in your hand to rename!");
			return;
		}
		int levels = p.getLevel();
		int count = item.getAmount();
		int cost = calculateCost(count);
		
		
		
		if (levels < cost) {
			p.sendMessage(ChatColor.RED + "You do not have enough levels to rename an item.");
			return;
		}
		item = doName(item, str);
		p.setLevel(levels - cost);
		p.sendMessage(ChatColor.WHITE + "" + count + " items renamed. Cost: " + ChatColor.GREEN + cost + ChatColor.WHITE + " levels.");
	}

	private void removeNames(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item.getType().equals(Material.AIR)) {
			p.sendMessage(ChatColor.RED + "Nothing in your hand to rename!");
			return;
		}
		item = doName(item, null);
		p.sendMessage(ChatColor.WHITE + "" + item.getAmount() + " item names reset to default.");
	}

	private void changeName(Player p, String[] args) {
		ItemStack item = p.getInventory().getItemInMainHand();
		String str = formatArgs(args);
		if (item.getType().equals(Material.AIR)) {
			p.sendMessage(ChatColor.RED + "Nothing in your hand to rename!");
			return;
		}
		int levels = p.getLevel();
		if (levels < Rename_Cost) {
			p.sendMessage(ChatColor.RED + "You do not have enough levels to rename an item.");
			return;
		}
		int count = item.getAmount();
		if (count > 1) {
			item.setAmount(count - 1);
			ItemStack newitem = new ItemStack(item.getType(), 1);
			newitem = doName(newitem, str);
			p.getWorld().dropItem(p.getLocation(), newitem);
		} else {
			item = doName(item, str);
		}
		p.setLevel(levels - Rename_Cost);
		p.sendMessage(ChatColor.WHITE + "Item renamed. Cost: " + ChatColor.GREEN + Rename_Cost + ChatColor.WHITE + " levels.");
	}

	private void removeName(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand();
		if (item.getType().equals(Material.AIR)) {
			p.sendMessage(ChatColor.RED + "Nothing in your hand to rename!");
			return;
		}
		int count = item.getAmount();
		if (count > 1) {
			item.setAmount(count-1);
			ItemStack newitem = new ItemStack(item.getType(), 1);
			newitem = doName(newitem, null);
			p.getWorld().dropItem(p.getLocation(), newitem);
		} else {
			item = doName(item, null);
		}
		p.sendMessage(ChatColor.WHITE + "Item name reset to default.");
	}
    
	private ItemStack doName(ItemStack item, String str) {
		ItemMeta im = item.getItemMeta();
		if (str == null) {
			im.setDisplayName(str);
		} else {
			im.setDisplayName(parseColorString(str));
		}
		item.setItemMeta(im);
		return item;
	}

	private String formatArgs(String[] args) {
		String inputString = String.join(" ", args);
		int maxLength = (inputString.length() < MAX_CHAR)?inputString.length():MAX_CHAR;
		return inputString.substring(0, maxLength);

	}
	
	private int calculateCost(int numberOfItems) {
		switch (String.format(Rate_Type).toUpperCase()) {
		case "SINGLE":
			return numberOfItems * Rename_Cost;
		case "FLAT":
			return calculateFlatRate(numberOfItems);
		case "PRORATE":
			return calculateProRate(numberOfItems);
		}
		
		return 0;
	}
	
	private int calculateFlatRate(int num) {
		int FullPrice = num * Rename_Cost;
		if (num == 1) {
			return FullPrice;
		} else if (num > 1 && num < 9) {
			return (int) (FullPrice - (FullPrice * Discount1));
		} else if (num > 8 && num < 17) {
			return (int) (FullPrice - (FullPrice * Discount2));
		} else if (num > 16 && num < 25) {
			return (int) (FullPrice - (FullPrice * Discount3));
		} else if (num > 24 && num < 33) {
			return (int) (FullPrice - (FullPrice * Discount4));
		} else if (num > 32 && num < 41) {
			return (int) (FullPrice - (FullPrice * Discount5));
		} else if (num > 40 && num < 49) {
			return (int) (FullPrice - (FullPrice * Discount6));
		} else if (num > 48 && num < 57) {
			return (int) (FullPrice - (FullPrice * Discount7));
		} else if (num > 56) {
			return (int) (FullPrice - (FullPrice * Discount8));
		}
		return 0;
	}
	
	private int calculateProRate(int num) {
		int sub = 0;
		float discount = 0;
		int FullPrice = num * Rename_Cost;
		if (num > 56) {
			sub = num - 56;
			discount += sub * Rename_Cost * Discount8;
			num = num - sub;
		}
		if (num > 48 && num < 57) {
			sub = num - 48;
			discount += sub * Rename_Cost * Discount7;
			num = num - sub;			
		}
		if (num > 40 && num < 49) {
			sub = num - 40;
			discount += sub * Rename_Cost * Discount6;
			num = num - sub;
		}
		if (num > 32 && num < 41) {
			sub = num - 32;
			discount += sub * Rename_Cost * Discount5;
			num = num - sub;
		}
		if (num > 24 && num < 33) {
			sub = num - 24;
			discount += sub * Rename_Cost * Discount4;
			num = num - sub;
		}
		if (num > 16 && num < 25) {
			sub = num - 16;
			discount += sub * Rename_Cost * Discount3;
			num = num - sub;
		}
		if (num > 8 && num < 17) {
			sub = num - 8;
			discount += sub * Rename_Cost * Discount2;
			num = num - sub;
		}
		if (num > 1 && num < 9) {
			sub = num - 1;
			discount += sub * Rename_Cost * Discount1;
			num = num - sub;
		}
		sub = (int) (FullPrice - discount);
//		Bukkit.getLogger().info("Full Price: " + FullPrice + "  Discount: " + discount + "  Final: " + sub);
		return sub;
	}
	
	public static String parseColorString(String value) {
		return ChatColor.translateAlternateColorCodes('&', value);
	}
		
	private void loadConfig() {
        File file = new File(Bukkit.getServer().getPluginManager().getPlugin("Rename").getDataFolder(), File.separator + "config.yml");
		FileConfiguration config;
		
		if (!file.exists()) {
			useDefaultConfig();
			return;
		}
		config = YamlConfiguration.loadConfiguration(file);
		MAX_CHAR = config.getInt("Max_Char");
		Rename_Cost = config.getInt("Rename_Cost");
		Rate_Type = config.getString("Multi_Rate");
		Discount1 = (float)config.getDouble("Multi_2-8_Discount"); 
		Discount2 = (float)config.getDouble("Multi_9-16_Discount"); 
		Discount3 = (float)config.getDouble("Multi_17-24_Discount"); 
		Discount4 = (float)config.getDouble("Multi_25-32_Discount"); 
		Discount5 = (float)config.getDouble("Multi_33-40_Discount"); 
		Discount6 = (float)config.getDouble("Multi_41-48_Discount"); 
		Discount7 = (float)config.getDouble("Multi_49-56_Discount"); 
		Discount8 = (float)config.getDouble("Multi_57-64_Discount"); 
	}
	
	private void useDefaultConfig() {
		Bukkit.getLogger().info("[Rename] Configuration file not found. Using Default Values.");
		MAX_CHAR = 122;
		Rename_Cost = 1;
		Rate_Type = "PRORATE";
		Discount1 = 0;
		Discount2 = 0;
		Discount3 = 0;
		Discount4 = 0;
		Discount5 = 0;
		Discount6 = 0;
		Discount7 = 0;
		Discount8 = 0;
	}

    private void validateFiles() {
        getLogger().info("Validating configuration files...");
        FileValidator(new String[] { "config.yml" });
        getLogger().info("Validated config files!");
    }
    
    public static void FileValidator (String... fileNames) {
        for (String name : fileNames) {
          File file = new File(instance.getDataFolder(), name);
          if (!file.exists())
            instance.saveResource(name, false); 
        } 
    }

}



