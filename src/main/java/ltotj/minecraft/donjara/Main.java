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


//    private void test(){
//        ItemStack item=new ItemStack(Material.STONE,1);
//        ItemMeta meta=item.getItemMeta();
//        meta.lore(mutableListOf(Component.text("§b§l必要数:${data.value.amount}").asComponent(),
//                Component.text("§a§l締切日:${sdf.format(data.value.datetime)}").asComponent(),
//                Component.text("§6§l報酬:${data.value.price}").asComponent(),
//                Component.text("§d§l受け取り:${if (data.value.boolean == 1){"可能"}else{"まだ"}}")))
//    }
}
