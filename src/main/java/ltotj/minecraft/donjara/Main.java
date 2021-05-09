package ltotj.minecraft.donjara;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        GlobalClass.config=new Config(this);
        GlobalClass.vaultManager=new VaultManager(this);
        GlobalClass.playable=false;
        GlobalClass.mySQLManager=new MySQLManager(this,"ドンジャラ");
        getCommand("donjara").setExecutor(new Commands());
        new EventList(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
