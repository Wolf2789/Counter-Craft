package Counter_Craft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public static Main main;
	public Main(){Main.main=this;}
	private Logger log;
	public void log(String msg){this.log.info(msg);}
	YamlConfiguration cfg=YamlConfiguration.loadConfiguration(new File(this.getDataFolder()+"\\config.yml"));
		
	public HashMap<weapon,Integer> WepList=new HashMap<weapon,Integer>();
	
	public void onEnable(){
		this.log=this.getServer().getLogger();
		PluginManager pm=this.getServer().getPluginManager();
		pm.registerEvents(new listener(),this);
		try{
			cfg.load(this.getDataFolder()+"\\config.yml");
		}catch(FileNotFoundException e){e.printStackTrace();}catch(IOException e){e.printStackTrace();}catch(InvalidConfigurationException e){e.printStackTrace();}
	}
	public void onDisable(){
		try{
			cfg.save(this.getDataFolder()+"\\config.yml");
		}catch(IOException e){e.printStackTrace();}
	}
	
	public boolean onCommand(CommandSender sender,Command cmd,String commandLabel,String[] args){
		if(sender instanceof Player){
			Player p=(Player)sender;
			if(cmd.getName().equalsIgnoreCase("b"))
				if(p.isOp()){
					p.getWorld().setStorm(!p.getWorld().hasStorm());
					this.log("Rain/Snow: "+(p.getWorld().hasStorm()?"ON":"OFF"));
				}
		}
		return false;
	}
	
	//events listener
	public class listener implements Listener {}
	
	//weapon item
	public class weapon {
		String n;
		int id,ug[];
		public weapon(String n,int id,int ug[]){
			this.n=n;
			this.id=id;
			this.ug=ug;
		}
	}
}
