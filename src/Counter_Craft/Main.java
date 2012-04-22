package Counter_Craft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
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

	public HashMap<Enchantment,Integer> EnchList=new HashMap<Enchantment,Integer>();
	public Enchantment EnchNotAllowed[]={Enchantment.ARROW_INFINITE,Enchantment.DIG_SPEED,Enchantment.LOOT_BONUS_BLOCKS};
	public HashMap<Integer,Integer> WepList=new HashMap<Integer,Integer>();
	public int WeaponIds[]={257,258,259,261,262,267,268,270,271,272,274,275,276,278,279,283,285,286};
	
	public void onEnable(){
		this.log=this.getServer().getLogger();
		PluginManager pm=this.getServer().getPluginManager();
		pm.registerEvents(new listener(),this);
		
		try{
			cfg.load(this.getDataFolder()+"\\config.yml");
		}catch(FileNotFoundException e){e.printStackTrace();}catch(IOException e){e.printStackTrace();}catch(InvalidConfigurationException e){e.printStackTrace();}
		
		//load cfg
		Set<String> a=null;
		Enchantment e=null;
		int c,id=0;
		
		log("Enchantments");
		a=cfg.getConfigurationSection("enchantments").getKeys(false);
		for(String s1:a){
			e=getEnch(s1);
			if(e!=null){
				c=cfg.getInt("enchantments."+s1);
				EnchList.put(e,c);
				log(" "+s1+"->"+e+"="+c);
				e=null;
			}else log(" Bad enchantment: "+s1);
		}
		
		log("Items");
		a=cfg.getConfigurationSection("items").getKeys(false);
		for(String s1:a){
			if((id=getItemId(s1))==0)
				for(int i:WeaponIds)
					if(Integer.toString(i)==s1){
						id=i;
						break;
					}
			if(id!=0){
				c=cfg.getInt("items."+s1);
				WepList.put(id,c);
				log(" "+s1+"->"+id+"="+c);
				id=0;
			}else log(" Bad item: "+s1);
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
					else{
						int id=getItemId(args[0]);
						if(id!=0)playerBuyItem(p,id);
						else p.sendMessage("Bad weapon id or name or is not allowed.");
					}
				}else if(args.length==2){
					if(args[0].equalsIgnoreCase("list"))listWeapons(p,Integer.parseInt(args[1]));
					else{
						int id=getItemId(args[0]);
						if(id!=0){
							for(int i=0;i<Integer.parseInt(args[1]);i++)
								playerBuyItem(p,id);
						}else p.sendMessage("Bad weapon id or name or is not allowed.");
					}
				}else{
					p.sendMessage("Use '/h b' to see how to use '/b' command.");
				}
			}else if(cmd.getName().equalsIgnoreCase("e"))
				if(args.length==1){
					if(args[0].equalsIgnoreCase("list"))listEnchs(p,0);
					else{
						Enchantment e=getEnch(args[0]);
						if(e!=null)playerBuyEnch(p,e,1);
						else p.sendMessage("Bad enchantment id, name or shortname or is not allowed.");
					}
				}else if(args.length==2){
					if(args[0].equalsIgnoreCase("list"))listEnchs(p,Integer.parseInt(args[1]));
					else{
						Enchantment e=getEnch(args[0]);
						if(e!=null)playerBuyEnch(p,e,Integer.parseInt(args[1]));
						else p.sendMessage("Bad enchantment id, name or shortname or is not allowed.");
					}
				}else{
					p.sendMessage("Use '/h e' to see how to use '/e' command.");
				}
		}
		return false;
	}

	public void listEnchs(Player p,int page){
		int count=0;
		for(Enchantment e:EnchList.keySet()){
			if(count>=page*10)p.sendMessage(e.toString()+" costs "+EnchList.get(e));count++;
			if(count>page*10+10)break;
		}
	}
	public void listWeapons(Player p,int page){
		int count=0;
		for(int n:WepList.keySet()){
			if(count>=page*10)p.sendMessage(Material.getMaterial(n).toString()+" costs "+WepList.get(n));count++;
			if(count>page*10+10)break;
		}
	}
	
	public Enchantment getEnch(String s){Enchantment e=null;
			 if(s.equalsIgnoreCase("a" ))e=Enchantment.ARROW_DAMAGE;
		else if(s.equalsIgnoreCase("af"))e=Enchantment.ARROW_FIRE;
		else if(s.equalsIgnoreCase("ak"))e=Enchantment.ARROW_KNOCKBACK;
		else if(s.equalsIgnoreCase("da"))e=Enchantment.DAMAGE_ALL;
		else if(s.equalsIgnoreCase("ds"))e=Enchantment.DAMAGE_ARTHROPODS;
		else if(s.equalsIgnoreCase("du"))e=Enchantment.DAMAGE_UNDEAD;
		else if(s.equalsIgnoreCase("d" ))e=Enchantment.DURABILITY;
		else if(s.equalsIgnoreCase("f" ))e=Enchantment.FIRE_ASPECT;
		else if(s.equalsIgnoreCase("k" ))e=Enchantment.KNOCKBACK;
		else if(s.equalsIgnoreCase("l" ))e=Enchantment.LOOT_BONUS_MOBS;
		else if(s.equalsIgnoreCase("o" ))e=Enchantment.OXYGEN;
		else if(s.equalsIgnoreCase("pe"))e=Enchantment.PROTECTION_ENVIRONMENTAL;
		else if(s.equalsIgnoreCase("p" ))e=Enchantment.PROTECTION_EXPLOSIONS;
		else if(s.equalsIgnoreCase("pd"))e=Enchantment.PROTECTION_FALL;
		else if(s.equalsIgnoreCase("pf"))e=Enchantment.PROTECTION_FIRE;
		else if(s.equalsIgnoreCase("pp"))e=Enchantment.PROTECTION_PROJECTILE;
		else if(s.equalsIgnoreCase("s" ))e=Enchantment.SILK_TOUCH;
		else if(s.equalsIgnoreCase("w" ))e=Enchantment.WATER_WORKER;
		else if((e=Enchantment.getByName(s))==null)e=Enchantment.getById(Integer.parseInt(s));
		if(e==null)return null;
		boolean notEnch=false;for(Enchantment i:EnchNotAllowed)if(i==e){notEnch=true;break;}
		return(notEnch?null:e);
	}
	public int getItemId(String s){int id=0;
			 if(s.equalsIgnoreCase("ip"))id=257;
		else if(s.equalsIgnoreCase("ia"))id=258;
		else if(s.equalsIgnoreCase("fs"))id=259;
		else if(s.equalsIgnoreCase("b" ))id=261;
		else if(s.equalsIgnoreCase("a" ))id=262;
		else if(s.equalsIgnoreCase("is"))id=267;
		else if(s.equalsIgnoreCase("ws"))id=268;
		else if(s.equalsIgnoreCase("wp"))id=270;
		else if(s.equalsIgnoreCase("wa"))id=271;
		else if(s.equalsIgnoreCase("ss"))id=272;
		else if(s.equalsIgnoreCase("sp"))id=274;
		else if(s.equalsIgnoreCase("sa"))id=275;
		else if(s.equalsIgnoreCase("ds"))id=276;
		else if(s.equalsIgnoreCase("dp"))id=278;
		else if(s.equalsIgnoreCase("da"))id=279;
		else if(s.equalsIgnoreCase("gs"))id=283;
		else if(s.equalsIgnoreCase("gp"))id=285;
		else if(s.equalsIgnoreCase("ga"))id=286;
		else if((id=Material.getMaterial(s).getId())==0)id=Integer.parseInt(s);
	    if(id==0)return 0;
		boolean isWep=false;for(int i:WeaponIds)if(i==id){isWep=true;break;}
		return(isWep?id:0);
	}

	private void playerBuyEnch(Player p,Enchantment e,int l){
		if(e!=null){
			p.getItemInHand().addEnchantment(e,l);
			p.setExp(p.getExp()-(EnchList.get(e)*l));	
		}else p.sendMessage("Bad enchantment name or id.");
	}
	private void playerBuyItem(Player p,int id){
		if(id!=0){
			p.getInventory().addItem(new ItemStack(id,1));
			p.setExp(p.getExp()-WepList.get(id));	
		}else p.sendMessage("Bad weapon name or id.");
	}

	//events listener
	public class listener implements Listener{}
	
	//weapon item
	public class weapon{
		ItemStack i;
		int c;
		public weapon(int id,byte d,int c){this.i=new ItemStack(id,1,(short)0,d);this.c=c;}
		public String wtf(){return this.i+"->"+this.c;}
		public ItemStack get(){return this.i;}
	}
}
