package ltotj.minecraft.donjara;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalClass {
    public static CustomConfig config;
    public static HashMap<UUID,Donjara> DonjaraTable=new HashMap<>();
    public static HashMap<UUID,UUID> currentPlayer=new HashMap<>();
    public static VaultManager vaultManager;
    public static boolean playable;
    public static MySQLManager mySQLManager;
    public static GameRuleGUI gameRuleGUI=new GameRuleGUI();
    public static ExecutorService executor= Executors.newCachedThreadPool();

    public static Donjara getTable(UUID uuid){
        return DonjaraTable.get(currentPlayer.get(uuid));
    }
}
