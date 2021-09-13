package ltotj.minecraft.donjara;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class CustomConfig {

    private FileConfiguration config=null;
    private final File configFile;
    private final String file;
    private final Plugin plugin;

    public CustomConfig(Plugin plugin){
        this(plugin,"config.yml");
    }

    public CustomConfig(Plugin plugin,String fileName){
        this.plugin=plugin;
        this.file=fileName;
        configFile=new File(plugin.getDataFolder(),file);
        load();
    }

    public void load(){
        saveDefaultConfig();
        if(config!=null){
            reloadConfig();
        }
        config=getConfig();
    }

    public void saveDefaultConfig(){
        if(!configFile.exists()){
            plugin.saveResource(file,false);
        }
    }

    public void reloadConfig(){
        config= YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream=plugin.getResource(file);
        if(defConfigStream==null){
            return;
        }
        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
    }

    public FileConfiguration getConfig(){
        if(config==null){
            reloadConfig();
        }
        return config;
    }

    public void setBoolean(String key,boolean value){
        config.set(key,value);
        saveConfig();
    }

    public void setInt(String key,int value){
        config.set(key,value);
        saveConfig();
    }

    public void setString(String key,String value){
        config.set(key,value);
        saveConfig();
    }

    public void saveConfig(){
        if(config==null){
            return;
        }
        try{
            getConfig().save(configFile);
        }catch(IOException ex){
            plugin.getLogger().log(Level.SEVERE,"コンフィグをセーブできませんでした");

        }
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