package ltotj.minecraft.donjara;

import dev.dbassett.skullcreator.SkullCreator;
import ltotj.minecraft.donjara.game.PlayerGUI;
import ltotj.minecraft.donjara.game.ResultGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Commands implements CommandExecutor {

    //デバッグ用
    ResultGUI resultGUI=new ResultGUI();

    //デバック用
    private Player dummyPlayer(String name){
        return (Player)Bukkit.getOfflinePlayerIfCached(name);
    }

    private boolean isCurrentPlayer(Player player){
        return GlobalClass.currentPlayer.containsKey(player.getUniqueId());
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("プレイヤー以外は実行できません");
            return true;
        }
        else if(args.length!=0){
            Player player=(Player)sender;
            switch (args[0]){
                case "start":
                    if(isCurrentPlayer(player)){
                        player.sendMessage(Component.text("あなたは既にゲームに参加しています！/donjara open でゲーム画面を開きましょう！"));
                        return true;
                    }
                    GlobalClass.DonjaraTable.put(player.getUniqueId(),new Donjara(player,Integer.parseInt(args[1])));
                    GlobalClass.currentPlayer.put(player.getUniqueId(),player.getUniqueId());
                    GlobalClass.DonjaraTable.get(player.getUniqueId()).start();
                    break;
                case "join":
                    //ちゃんと書く
                    if(isCurrentPlayer(player)){
                        player.sendMessage(Component.text("あなたは既にゲームに参加しています！/donjara open でゲーム画面を開きましょう！"));
                        return true;
                    }
                    else if(args.length>1) {
                        Player masP = Bukkit.getPlayer(args[1]);
                        if (masP != null && GlobalClass.DonjaraTable.containsKey(masP.getUniqueId())) {
                            if (!GlobalClass.DonjaraTable.get(masP.getUniqueId()).addPlayer(player)) {
                                player.sendMessage(Component.text("参加できませんでした"));
                            }
                            return true;
                        }
                    }
                    player.sendMessage(Component.text("/donjara join <募集者のID>"));
                    break;
                case "open":
                    if(!isCurrentPlayer(player)){
                        player.sendMessage(Component.text("参加しているゲームがありません"));
                        return true;
                    }
                    player.openInventory(GlobalClass.getTable(player.getUniqueId()).getPlData(player).playerGUI.inv.inv);
                    break;
                case "test":
                    GlobalClass.DonjaraTable.get(Bukkit.getPlayer(args[1]).getUniqueId()).addPlayer(player);
                    GlobalClass.DonjaraTable.get(Bukkit.getPlayer(args[1]).getUniqueId()).addDummyPlayer();
                    GlobalClass.DonjaraTable.get(Bukkit.getPlayer(args[1]).getUniqueId()).addDummyPlayer();
                    break;
                case "test2":
                    GlobalClass.DonjaraTable.get(Bukkit.getPlayer(args[1]).getUniqueId()).addDummyPlayer();
                    GlobalClass.DonjaraTable.get(Bukkit.getPlayer(args[1]).getUniqueId()).addDummyPlayer();
                    GlobalClass.DonjaraTable.get(Bukkit.getPlayer(args[1]).getUniqueId()).addDummyPlayer();
                    break;
                case "aaa":
                    GlobalClass.DonjaraTable.get(player.getUniqueId()).interrupt();
            }
            return true;
        }

        return false;
    }
}
