package ltotj.minecraft.donjara;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.*;

public class Commands implements CommandExecutor {

    private String getYenString(String money){//１２桁まで対応
        if(money.length()==0){
            return "null";
        }
        StringBuilder yen=new StringBuilder();
        int first=((money.length()+2)%3)+1;
        for(int i=0;i<1+(money.length()-1)/3;i++){
            if(i==0){
                yen.append(money.substring(0,first));
            }
            else{
                yen.append(",").append(money.substring(first+3*(i-1),first+3*i));
            }
        }
        yen.append("円");
        return yen.toString();
    }

    private boolean isCurrentPlayer(Player player){
        return GlobalClass.currentPlayer.containsKey(player.getUniqueId());
    }

    private boolean isStringDouble(String stringToCheck){
        Scanner sc = new Scanner(stringToCheck.trim());
        if(!sc.hasNextDouble()) return false;
        sc.nextDouble();
        return !sc.hasNext();
    }

    private boolean isStringInteger(String stringToCheck, int radix) {
        Scanner sc = new Scanner(stringToCheck.trim());
        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }

    private String[] getPointRanking(){//スレッドで
        String[] strs=new String[11];
        strs[0]="§a§l獲得点数ランキング";
        int rank=1;
        MySQLManager mysql=new MySQLManager(Main.getPlugin(Main.class),"DonjaraCommand");
        ResultSet result=mysql.query("select name,totalPoint from playerData order by totalPoint desc limit 10;");
        if(result!=null){
            while (true){
                try {
                    if (!result.next()) break;
                    if(result.getInt("totalPoint")<=0) break;
                    strs[rank]="§7§l"+ rank +".§b"+ result.getString("name") +" §e§l"+ result.getInt("totalPoint") +"点";
                    rank++;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            try {
                result.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        try {
            mysql.close();
        } catch (NullPointerException throwables) {
            throwables.printStackTrace();
        }
        return strs;
    }

    private String[] getMoneyRanking(){//スレッドで
        String[] strs=new String[11];
        strs[0]="§a§l獲得金額ランキング";
        int rank=1;
        MySQLManager mysql=new MySQLManager(Main.getPlugin(Main.class),"DonjaraCommand");
        ResultSet result=mysql.query("select name,totalMoney from playerData order by totalMoney desc limit 10;");
        if(result!=null){
            while (true){
                try {
                    if (!result.next()) break;
                    if(result.getInt("totalMoney")<=0) break;
                    strs[rank]="§7§l"+ rank +".§b"+ result.getString("name") +" §e§l"+ getYenString(result.getString("totalMoney"));
                    rank++;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
            try {
                result.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        try {
            mysql.close();
        } catch (NullPointerException throwables) {
            throwables.printStackTrace();
        }
        return strs;
    }

    private String[] getPriceRanking(){
        String[] strs=new String[11];
        strs[0]="§a§l一回の獲得金額ランキング";
        for(int i=1;i<11;i++){
            int point=GlobalClass.config.getInt("winningPriceRanking."+ i +".point"),rate=GlobalClass.config.getInt("winningPriceRanking."+ i +".rate");
            strs[i]="§7§l"+ i +".§b"+ GlobalClass.config.getString("winningPriceRanking."+ i +".name") +" "+ point
                    +"点 : §e§l"+getYenString(String.valueOf(rate*(point-24000)));
        }
        return strs;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length!=0){
            if(sender.hasPermission("donjara.op")){
                switch (args[0]){//権限持ち用
                    case "reload":
                        GlobalClass.config.load();
                        return true;
                    case "on":
                        GlobalClass.playable=true;
                        GlobalClass.config.setBoolean("canPlay",true);
                        sender.sendMessage("新規ゲームを開催可能にしました");
                        return true;
                    case "off":
                        GlobalClass.playable=false;
                        GlobalClass.config.setBoolean("canPlay",false);
                        sender.sendMessage("新規ゲームを開催不可能にしました");
                        return true;
                    case "pointranking":
                        GlobalClass.executor.execute(new Runnable(){
                            @Override
                            public void run(){
                                sender.sendMessage(getPointRanking());
                            }
                        });
                        return true;
                    case "moneyranking":
                        GlobalClass.executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                sender.sendMessage(getMoneyRanking());
                            }
                        });
                        return true;
                    case "priceranking":
                        sender.sendMessage(getPriceRanking());
                        return true;
                }
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("プレイヤー以外は実行できません");
                return true;
            }
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
                    else if(!isStringInteger(args[1],10)){
                        player.sendMessage("コマンドが不正です");
                        break;
                    }
                    else if(Integer.parseInt(args[1])<3||Integer.parseInt(args[1])>4){
                        player.sendMessage("募集可能人数は三〜四人です");
                        break;
                    }
                    else if(args.length>2){
                        if(isStringInteger(args[2],10)){
                            int rate=Integer.parseInt(args[2]);
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
                        else{
                            player.sendMessage("賭け金が正しく入力されていません");
                            break;
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
                            ,"/donjara rule |牌・役の一覧を表示します"
                            ,"/donjara cpu |試し打ちができます"});
                    break;
                case "cpu":
                    if(!GlobalClass.playable){
                        player.sendMessage("プラグイン[Donjara]はただいま停止中です");
                        break;
                    }
                    else if(isCurrentPlayer(player)){
                        player.sendMessage(Component.text("あなたは既にゲームに参加しています！/donjara open でゲーム画面を開きましょう！"));
                        break;
                    }
                    else{
                        GlobalClass.DonjaraTable.put(player.getUniqueId(),new DonjaraCPU(player));
                        GlobalClass.currentPlayer.put(player.getUniqueId(),player.getUniqueId());
                    }
                    break;
            }
            return true;
        }

        return false;
    }
}
