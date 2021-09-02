package ltotj.minecraft.donjara;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {

    private final Plugin plugin;
    private FileConfiguration config=null;

    public Config(Plugin plugin){
        this.plugin=plugin;
        load();
    }
    public void load(){
        plugin.saveDefaultConfig();
        if(config!=null){
            plugin.reloadConfig();
        }
        config=plugin.getConfig();
    }
    public String getString(String string){
        try {
            return config.getString(string);
        }catch(Exception exception){
            System.out.println("コンフィグから"+string+"の値を取るのに失敗しました");
            return "";
        }
    }
    public Boolean getBoolean(String string){
        try {
            return config.getBoolean(string);
        }catch(Exception exception){
            System.out.println("コンフィグから"+string+"の値を取るのに失敗しました");
            return false;
        }
    }
    public int getInt(String string){
        try{
            return config.getInt(string);
        }catch(Exception exception){
            System.out.println("コンフィグから"+string+"の値を取るのに失敗しました");
            return 0;
        }
    }
    public double getDouble(String string){
        try{
            return config.getDouble(string);
        }catch(Exception exception){
            System.out.println("コンフィグから"+string+"の値を取るのに失敗しました");
            return 0;
        }
    }

}