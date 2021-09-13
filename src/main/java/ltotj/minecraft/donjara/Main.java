package ltotj.minecraft.donjara;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        GlobalClass.config=new CustomConfig(this);
        GlobalClass.vaultManager=new VaultManager(this);
        GlobalClass.playable=false;
        GlobalClass.mySQLManager=new MySQLManager(this,"ドンジャラ");
        GlobalClass.playable=GlobalClass.config.getBoolean("canPlay");
        getCommand("donjara").setExecutor(new Commands());
        new EventList(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
