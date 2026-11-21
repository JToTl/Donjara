package ltotj.minecraft.donjara;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalClass {
    public static CustomConfig config;
    // ゲームスレッドが終了時に消すのと、メインスレッドの補完・list の走査が重なるので並行対応の Map にする
    public static ConcurrentHashMap<UUID,Donjara> DonjaraTable=new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID,UUID> currentPlayer=new ConcurrentHashMap<>();
    public static VaultManager vaultManager;
    public static boolean playable;
    public static MySQLManager mySQLManager;
    public static GameRuleGUI gameRuleGUI=new GameRuleGUI();
    public static ExecutorService executor= Executors.newCachedThreadPool();

    public static Donjara getTable(UUID uuid){
        UUID master=currentPlayer.get(uuid);
        return master!=null?DonjaraTable.get(master):null; // ConcurrentHashMap は get(null) で落ちる
    }
}
