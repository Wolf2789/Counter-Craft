package Counter_Craft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public static Main main;
	public Main(){Main.main=this;}
	private Logger log;
	public void log(String msg){this.log.info(msg);}
	YamlConfiguration cfg=YamlConfiguration.loadConfiguration(new File(this.getDataFolder()+"\\config.yml"));
		
	public HashMap<Integer,Integer> WepList=new HashMap<Integer,Integer>();
	public HashMap<Enchantment,Integer> EnchList=new HashMap<Enchantment,Integer>();
	public int WeaponIds[]={257,258,259,261,262,267,268,270,271,272,274,275,276,278,279,283,285,286};
	
	public void onEnable(){
		this.log=this.getServer().getLogger();
		PluginManager pm=this.getServer().getPluginManager();
		pm.registerEvents(new listener(),this);
		
		try{
			cfg.load(this.getDataFolder()+"\\config.yml");
		}catch(FileNotFoundException e){e.printStackTrace();}catch(IOException e){e.printStackTrace();}catch(InvalidConfigurationException e){e.printStackTrace();}
		
		//load cfg
		int id=0,c;
		Set<String> a=null;
		
		//load allowed enchantments
		a=cfg.getConfigurationSection("enchantments").getKeys(false);
		Enchantment e=null;
		for(String s1:a){
			if(e==null)e=getEnchBySName(s1);
			if(e==null)e=getEnchByName(s1);
			if(e==null)e=getEnchById(Integer.parseInt(s1));
			if(e!=null){
				c=cfg.getInt("enchantments."+s1);
				EnchList.put(e,c);
				log(s1+"->"+e+"="+c);
				e=null;
			}else{
				log("Bad enchantment: "+s1);
			}
		}
		
		//load allowed weapons
		a=cfg.getConfigurationSection("items").getKeys(false);
		for(String s1:a){
			for(int i:WeaponIds)
				if(Integer.toString(i)==s1){
					id=i;
					break;
				}
			if(id==0)id=getItemId(s1);
			if(id!=0){
				c=cfg.getInt("items."+s1);
				WepList.put(id,c);
				log(s1+"->"+id+"="+c);
				id=0;
			}else{
				log("Bad item: "+s1);
			}
		}
	}
	public void onDisable(){
		try{
			cfg.save(this.getDataFolder()+"\\config.yml");
		}catch(IOException e){e.printStackTrace();}
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String commandLabel,String[] args){
		if(sender instanceof Player){
			Player p=(Player)sender;
			if(cmd.getName().equalsIgnoreCase("cc")){
				if(p.isOp()){
					p.getWorld().setStorm(!p.getWorld().hasStorm());
					this.log("Rain/Snow: "+(p.getWorld().hasStorm()?"ON":"OFF"));
				}
			}else if(cmd.getName().equalsIgnoreCase("h")){
				if(args.length==0){
					for(String s:"/h - list all commands~/b - buy item (use '/h b' for more info)~/e - buy enchantment (use '/h e' for more info)~~EXP is the payment for new items and upgrades.".split("~"))p.sendMessage(s);
				}else if(args.length==1){
					if(args[0].equalsIgnoreCase("b")){
						for(String s:"<required> [optional]~/b list - List all buyable items and how much EXP do they cost.~/b [id/name] [amount] - buy item".split("~"))p.sendMessage(s);
					}else if(args[0].equalsIgnoreCase("e")){
						for(String s:"<required> [optional]~/e list - List all buyable enchantments and how much EXP do they cost.~/e [id/name/short name <level>] - buy enchantment".split("~"))p.sendMessage(s);
					}
				}
			}else if(cmd.getName().equalsIgnoreCase("b")){
				if(args.length==1){
					if(args[0].equalsIgnoreCase("list"))listWeapons(p,0);
					if(WepList.containsKey(getItemId(args[0])))playerBuyItem(p,getItemId(args[0]));
				}else if(args.length==2){
					if(args[0].equalsIgnoreCase("list"))listWeapons(p,Integer.parseInt(args[1]));
					if(WepList.containsKey(getItemId(args[0])))
						for(int i=0;i<Integer.parseInt(args[1]);i++)
							playerBuyItem(p,getItemId(args[0]));
				}else{
					p.sendMessage("Use '/h b' to see how to use '/b' command.");
				}
			}else if(cmd.getName().equalsIgnoreCase("e"))
				if(args.length==1){
				}else{
					for(int i=0;i<20;i++)
						p.sendMessage(Integer.toString(i));
				}
		}
		return false;
	}
	
	public void listWeapons(Player p,int page){
		int count=0;
		for(int n:WepList.keySet()){
			if(count>=page*10){
				p.sendMessage(Material.getMaterial(n)+" costs "+WepList.get(n));
				count++;
			}
			if(count>page*10+10)break;
		}
	}
	
	public Enchantment getEnchByName(String s){return Enchantment.getByName(s);}
	public Enchantment getEnchById(int i){return Enchantment.getById(i);}
	public Enchantment getEnchBySName(String s){
		if(s.equalsIgnoreCase(""))
			return Enchantment.ARROW_DAMAGE;
		return null;
	}
	public int getItemId(String n){int id=0;
			 if(n.equalsIgnoreCase("ip"))id=257;
		else if(n.equalsIgnoreCase("ia"))id=258;
		else if(n.equalsIgnoreCase("fs"))id=259;
		else if(n.equalsIgnoreCase("b" ))id=261;
		else if(n.equalsIgnoreCase("a" ))id=262;
		else if(n.equalsIgnoreCase("is"))id=267;
		else if(n.equalsIgnoreCase("ws"))id=268;
		else if(n.equalsIgnoreCase("wp"))id=270;
		else if(n.equalsIgnoreCase("wa"))id=271;
		else if(n.equalsIgnoreCase("sw"))id=272;
		else if(n.equalsIgnoreCase("sp"))id=274;
		else if(n.equalsIgnoreCase("sa"))id=275;
		else if(n.equalsIgnoreCase("ds"))id=276;
		else if(n.equalsIgnoreCase("dp"))id=278;
		else if(n.equalsIgnoreCase("da"))id=279;
		else if(n.equalsIgnoreCase("gs"))id=283;
		else if(n.equalsIgnoreCase("gp"))id=285;
		else if(n.equalsIgnoreCase("ga"))id=286;
		if(id==0)id=Material.getMaterial(n).getId();
		boolean isWep=false;for(int i:WeaponIds)if(i==id){isWep=true;break;}
		return(isWep?id:0);
	}
	
	private void playerBuyItem(Player p,int id){
		if(id!=0){
			p.getInventory().addItem(new ItemStack(WepList.get(id),1));
			p.setExp(p.getExp()-WepList.get(id));	
		}else p.sendMessage("Bad item name or id");
	}

	//events listener
	public class listener implements Listener {}
	
	//weapon item
	public class weapon {
		ItemStack i;
		int c;
		public weapon(int id,byte d,int c){this.i=new ItemStack(id,1,(short)0,d);this.c=c;}
		public String wtf(){return this.i+"->"+this.c;}
		public ItemStack get(){return this.i;}
	}
}
