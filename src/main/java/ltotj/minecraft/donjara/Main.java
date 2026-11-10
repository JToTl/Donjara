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
        Commands commands=new Commands();
        getCommand("donjara").setExecutor(commands);
        getCommand("donjara").setTabCompleter(commands);
        new EventList(this);
        // レート用のテーブル（無ければ作る）。DB 待ちでサーバーを止めないよう別スレッドで
        GlobalClass.executor.execute(DonjaraRatingRepository::createTable);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
