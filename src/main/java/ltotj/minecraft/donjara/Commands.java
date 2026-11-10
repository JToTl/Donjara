package ltotj.minecraft.donjara;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static java.lang.Math.*;

public class Commands implements CommandExecutor, TabCompleter {

    private static final List<String> PLAYER_SUBCOMMANDS=Arrays.asList("start","private","join","invite","open","list","rating","top","rule","help","cpu");
    private static final List<String> OP_SUBCOMMANDS=Arrays.asList("reload","on","off","pointranking","moneyranking","priceranking");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> candidates=new ArrayList<>();
        if(args.length==1){
            candidates.addAll(PLAYER_SUBCOMMANDS);
            if(sender.hasPermission("donjara.op"))candidates.addAll(OP_SUBCOMMANDS);
        }
        else if(args.length==2){
            switch (args[0]){
                case "start":
                case "private":
                    candidates.addAll(Arrays.asList("3","4"));
                    break;
                case "join":
                    // 募集中の卓のホストだけ（鍵付き・CPU 戦・満席は出さない）
                    for(Donjara donjara:GlobalClass.DonjaraTable.values()){
                        if(donjara.isPrivate()||!donjara.isRecruiting())continue;
                        candidates.add(donjara.masterPlayer.getName());
                    }
                    break;
                case "invite":
                case "rating":
                    for(Player online:Bukkit.getOnlinePlayers())candidates.add(online.getName());
                    break;
            }
        }
        else if(args.length==3&&(args[0].equals("start")||args[0].equals("private"))){
            candidates.add(String.valueOf((int)GlobalClass.config.getDouble("minRate")));
        }
        String typing=args[args.length-1].toLowerCase();
        candidates.removeIf(c->!c.toLowerCase().startsWith(typing));
        return candidates;
    }

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


    // /donjara rating [プレイヤー]
    private void showRating(Player sender,String targetName){
        if(!targetName.matches("[A-Za-z0-9_.]{1,16}")){
            sender.sendMessage("§cプレイヤー名が正しくありません");
            return;
        }
        Player online=Bukkit.getPlayerExact(targetName);
        GlobalClass.executor.execute(() -> {
            UUID uuid=online!=null?online.getUniqueId():DonjaraRatingRepository.findUuidByName(targetName);
            DonjaraRatingRepository.Stats stats=uuid!=null?DonjaraRatingRepository.getStats(uuid):null;
            if(stats==null){
                sender.sendMessage("§7"+targetName+" はまだレートの付く卓で遊んでいません §8(初期レート "+DonjaraRatingRepository.initialRating()+")");
                return;
            }
            int rank=DonjaraRatingRepository.getServerRank(stats.rating);
            Long money=DonjaraRatingRepository.getTotalMoney(uuid);
            List<String> lines=new ArrayList<>();
            lines.add("§6=== §f"+stats.name+" §6のドンジャラ レーティング ===");
            lines.add("§eレート: §b§l"+stats.rating+"§7/"+rank+"位");
            lines.add("§e試合数: §f"+stats.games);
            lines.add("§e順位: §f1位 "+stats.places[0]+"§7/§f2位 "+stats.places[1]+"§7/§f3位 "+stats.places[2]+"§7/§f4位 "+stats.places[3]);
            if(money!=null){
                String sign=money>=0?"§a+":"§c-";
                lines.add("§e収支: "+sign+getYenString(String.valueOf(Math.abs(money))));
            }
            sender.sendMessage(lines.toArray(new String[0]));
        });
    }

    // /donjara invite <プレイヤー> — 募集中の自分の卓に誘う（鍵付きの卓はパスワード込みの参加ボタン）
    private void invite(Player player,String[] args){
        Donjara table=GlobalClass.DonjaraTable.get(player.getUniqueId());
        if(table==null||!table.isRecruiting()){
            player.sendMessage("§c募集中の自分の卓がありません");
            return;
        }
        if(args.length<2){
            player.sendMessage("/donjara invite <プレイヤー>");
            return;
        }
        Player target=Bukkit.getPlayerExact(args[1]);
        if(target==null){
            player.sendMessage("§c"+args[1]+" はオンラインではありません");
            return;
        }
        if(table.seatList.containsKey(target.getUniqueId())){
            player.sendMessage("§c"+target.getName()+" はすでにこの卓に参加しています");
            return;
        }
        String rate=table.betting?"一点"+table.rate+"円":"賭けなし";
        target.sendMessage(Component.text("§a"+player.getName()+"§aから"+table.maxSeat+"人ドンジャラ（"+rate+"）に招待されました ").append(table.joinButton()));
        player.sendMessage("§a"+target.getName()+"§aを招待しました");
    }

    // /donjara top
    private void showTop(Player sender){
        GlobalClass.executor.execute(() -> {
            List<DonjaraRatingRepository.Stats> top=DonjaraRatingRepository.getTop(10);
            List<String> lines=new ArrayList<>();
            lines.add("§6======= §eドンジャラ レートランキング §6=======");
            if(top.isEmpty())lines.add("§7まだ記録がありません");
            for(int i=0;i<top.size();i++){
                DonjaraRatingRepository.Stats s=top.get(i);
                String medal=i==0?"§6§l🏆":i==1?"§f§l🥈":i==2?"§e§l🥉":"§7"+(i+1)+"位";
                lines.add(medal+" §f"+s.name+" §7- §b"+s.rating+" §8("+s.games+"戦 "+s.places[0]+"勝)");
            }
            sender.sendMessage(lines.toArray(new String[0]));
        });
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
            String password=null; // private のときだけ入る（start に流れ込む）
            switch (args[0]){//一般ぴーぽー用
                case "private":
                    // /donjara private <人数> <金額> <パスワード> — 募集を流さず、レートも動かない卓。作り方は start と同じ
                    if(args.length<4){
                        player.sendMessage("/donjara private <募集人数:3~4> <一点当たりの金額> <パスワード>");
                        break;
                    }
                    else if(args[3].length()>16){
                        player.sendMessage("パスワードは16文字以内にしてください");
                        break;
                    }
                    password=args[3];
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
                    GlobalClass.DonjaraTable.get(player.getUniqueId()).password=password;
                    if(password!=null){
                        player.sendMessage("§a鍵付きのドンジャラ卓を作成しました §7(レート変動なし)");
                        player.sendMessage("§7募集は全体に流れません。参加する人に伝えてください: §e/donjara join "+player.getName()+" "+password);
                    }
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
                            String tablePassword=GlobalClass.DonjaraTable.get(masP.getUniqueId()).password;
                            if (tablePassword != null && (args.length < 3 || !args[2].equals(tablePassword))) {
                                player.sendMessage("§cパスワードが違います §7(/donjara join " + masP.getName() + " <パスワード>)");
                                return true;
                            }
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
                        if(donjara.isPrivate()||donjara instanceof DonjaraCPU)continue; // 鍵付きの卓・CPU 戦は出さない
                        String fee=donjara.betting?"§4"+getYenString(String.valueOf(donjara.rate*24000)):"§7賭けなし";
                        player.sendMessage(donjara.masterPlayer.getName()+"の卓｜募集人数："+donjara.maxSeat+"人｜必要金額："+fee+"§r｜現在の人数："+donjara.playerList.size()+"人");
                    }
                    break;
                case "invite":
                    invite(player,args);
                    break;
                case "rating":
                    showRating(player,args.length>1?args[1]:player.getName());
                    break;
                case "top":
                    showTop(player);
                    break;
                case "rule":
                    player.openInventory(GlobalClass.gameRuleGUI.ruleGUI.inv);
                    break;
                case "help":
                    player.sendMessage(new String[]{"/donjara start <募集人数:3~4> (一点当たりの金額:"+GlobalClass.config.getDouble("minRate")+"円以上"+GlobalClass.config.getDouble("maxRate")+"円以下,（電子マネー）) |卓を作成します"
                            ,"/donjara private <募集人数:3~4> <一点当たりの金額> <パスワード> |鍵付きの卓を作成します（募集は流れず、レート変動なし）"
                            ,"/donjara join <募集者のID> [パスワード] |卓に参加します（鍵付きの卓はパスワードも）"
                            ,"/donjara invite <プレイヤー> |募集中の自分の卓に招待します"
                            ,"/donjara open |参加中のゲーム画面を開きます"
                            ,"/donjara list |参加可能な卓を表示します"
                            ,"/donjara rating [プレイヤー] |レートを表示します"
                            ,"/donjara top |レートのランキングを表示します"
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
