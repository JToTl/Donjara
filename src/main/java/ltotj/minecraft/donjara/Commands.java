package ltotj.minecraft.donjara;


import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

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
            switch (args[0]){//一般ぴーぽー用
                case "start":
                    if(args.length<2){
                        player.sendMessage("コマンドが不正です");
                        break;
                    }
                    else if(!GlobalClass.playable){
                        player.sendMessage("プラグイン[Donjara]はただいま停止中です");
                        break;
                    }
                    else if(isCurrentPlayer(player)){
                        player.sendMessage(Component.text("あなたは既にゲームに参加しています！/donjara open でゲーム画面を開きましょう！"));
                        break;
                    }
                    else if(!args[1].matches("\"-?\\\\d+\"")){
                        player.sendMessage("コマンドが不正です");
                        break;
                    }
                    else if(Integer.parseInt(args[1])<3||Integer.parseInt(args[1])>4){
                        player.sendMessage("募集可能人数は三〜四人です");
                        break;
                    }
                    else if(args.length>2){
                        if(args[2].matches("[+-]?\\d*(\\.\\d+)?")){
                            double rate=Double.parseDouble(args[2]);
                            if(rate<GlobalClass.config.getDouble("minRate")||rate>GlobalClass.config.getDouble("maxRate")){
                                player.sendMessage("賭け金は"+GlobalClass.config.getDouble("minRate")+"以上"+GlobalClass.config.getDouble("maxRate")+"以下の額で設定してください");
                                break;
                            }
                            else if(GlobalClass.vaultManager.getBalance(player.getUniqueId())<rate*24000) {
                                player.sendMessage("所持金が不足しています");
                                break;
                            }
                            GlobalClass.DonjaraTable.put(player.getUniqueId(),new Donjara(player,Integer.parseInt(args[1])));
                            GlobalClass.DonjaraTable.get(player.getUniqueId()).rate=rate;
                            GlobalClass.DonjaraTable.get(player.getUniqueId()).betting=true;
                            GlobalClass.DonjaraTable.get(player.getUniqueId()).addPlayer(player);
                        }
                    }
                    else {
                        GlobalClass.DonjaraTable.put(player.getUniqueId(),new Donjara(player,Integer.parseInt(args[1])));
                        GlobalClass.DonjaraTable.get(player.getUniqueId()).addPlayer(player);
                    }
                    GlobalClass.currentPlayer.put(player.getUniqueId(),player.getUniqueId());
                    GlobalClass.DonjaraTable.get(player.getUniqueId()).start();
                    break;
                case "join":
                    if(isCurrentPlayer(player)){
                        player.sendMessage(Component.text("あなたは既にゲームに参加しています！/donjara open でゲーム画面を開きましょう！"));
                        return true;
                    }
                    else if(args.length>1) {
                        Player masP = Bukkit.getPlayer(args[1]);
                        if (masP != null && GlobalClass.DonjaraTable.containsKey(masP.getUniqueId())) {
                            if (!GlobalClass.DonjaraTable.get(masP.getUniqueId()).addPlayer(player)) {
                                player.sendMessage(Component.text("所持金が不足しているか、人数がいっぱいです"));
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
                case "list":
                    player.sendMessage("立っている卓は以下の通りです");
                    for(Donjara donjara:GlobalClass.DonjaraTable.values()){
                        player.sendMessage(donjara.masterPlayer.getName()+"の卓|募集人数："+donjara.maxSeat+"人｜必要金額：§4"+donjara.rate*24000+"$§｜r現在の人数:"+donjara.playerList.size()+"人");
                    }
                    break;
                case "rule":
                    player.openInventory(GlobalClass.gameRuleGUI.ruleGUI.inv);
                    break;
                case "help":
                    player.sendMessage(new String[]{"/donjara start <募集人数:3~4> (一点当たりの金額:"+GlobalClass.config.getDouble("minRate")+"以上"+GlobalClass.config.getDouble("maxRate")+"以下) |卓を作成します"
                            ,"/donjara join <募集者のID> |卓に参加します"
                            ,"/donjara open |参加中のゲーム画面を開きます"
                            ,"/donjara list |参加可能な卓を表示します"
                            ,"/donjara rule |牌・役の一覧を表示します"});
                    break;
            }
            if(player.hasPermission("donjara.op")){
                switch (args[0]){//権限持ち用
                    case "reloadconfig":
                        GlobalClass.config.load();
                        break;
                    case "switch":
                        GlobalClass.playable=!GlobalClass.playable;
                        if(!GlobalClass.playable)player.sendMessage("新規ゲームを開催不可能にしました");
                        else player.sendMessage("新規ゲームを開催可能にしました");
                        break;
//                    case "dp"://デバッグ用
//                        GlobalClass.getTable(Bukkit.getPlayer(args[1]).getUniqueId()).addDummyPlayer();
//                        break;
                }
            }
            return true;
        }

        return false;
    }
}
