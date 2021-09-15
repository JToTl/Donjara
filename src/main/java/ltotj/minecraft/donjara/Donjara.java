package ltotj.minecraft.donjara;

import ltotj.minecraft.donjara.game.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import ltotj.minecraft.donjara.GlobalClass.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Donjara extends Thread{

    private int gameId;
    Date startTime,endTime;
    public int maxSeat;
    protected int turnSeat = 0;
    protected int leaderSeat;
    protected int firstLeader;
    protected int canRon=0;
    protected int canRonPAc;
    protected int firstRemTime;
    public int rate=0;
    public HashMap<Integer,PlayerData> playerList=new HashMap<>();
    protected HashMap<UUID,Integer> seatList=new HashMap<>();
    protected Deck deck=new Deck();
    protected ResultGUI resultGUI=new ResultGUI();
    public Player masterPlayer;
    Random random=new Random();
    protected List<DisTilesGUI> disTilesGUIS=new ArrayList<>();
    protected List<Integer> ronTiles=new ArrayList<>(),ronPSeat=new ArrayList<>();
    protected boolean canContinue=true;
    public boolean betting=false;
    private MySQLManager mysql;

    public Donjara(Player player,int maxSeat){
        masterPlayer=player;
        this.maxSeat=maxSeat;
        disTilesGUIS.add(new DisTilesGUI(1));
        disTilesGUIS.add(new DisTilesGUI(2));
        startTime=new Date();
        firstRemTime=GlobalClass.config.getInt("firstRemainingTime");
    }

    protected class PlayerData{
        public UUID playerUUID;
        public String name;
        public PlayerGUI playerGUI=new PlayerGUI();;
        PlayerDiscardedTiles playerDisTiles=new PlayerDiscardedTiles();;
        public PlayerHand playerHand=new PlayerHand();;
        ItemStack head;
        public boolean li_zhi=false,preLi_zhi=false;
        public int discardedTileNum=0;//101でツモの意
        List<Integer> winningTiles=new ArrayList<>();

        int remTime=firstRemTime,point=24000;//コンフィグから設定できたらいいかもしれない

        public PlayerData(Player player){
            if(player==null){//デバッグ用
                return;
            }
            playerUUID=player.getUniqueId();
            name=player.getName();
            head=getHead(player);
            setUpPlGUI();
        }

        protected void setUpPlGUI(){
            playerGUI.inv.setItem(17,head);
            playerGUI.setGlassPane();
        }

        public int disGUINum(){
            if(seatList.get(playerUUID)<2)return 0;
            return 1;
        }

        public void setPreLi_zhi(){
            playerGUI.preLi_zhi(playerHand);
        }

        private void playSound(Sound sound){
            Player player=Bukkit.getPlayer(playerUUID);
            if(player!=null)player.playSound(player.getLocation(),sound,2,2);
        }

        protected void drawTile(){
            int tile=deck.draw();
            playerHand.drawTile(tile);
            playerGUI.setDrawnTile(tile);
            playSound(Sound.BLOCK_WOODEN_BUTTON_CLICK_OFF);
        }

        protected int removeTile() {//捨てられた牌を返す
            int ins;
            if(discardedTileNum==9)ins=playerHand.drawnTile;
            else ins= playerHand.getTile(discardedTileNum);
            playerDisTiles.addTile(ins);
            playerHand.drawnToHand();
            playerHand.removeTile(ins);
            disTilesGUIS.get(disGUINum()).setTiles(seatList.get(playerUUID)%2,playerDisTiles.getTiles());
            for(int i=0;i<maxSeat;i++){
                if(i==seatList.get(playerUUID))continue;
                playerList.get(i).playerGUI.setOthersDisTile(ins,(3*i+seatList.get(playerUUID)+3)%4);
            }
            return ins;
        }

        protected void setButtons(){
            playerGUI.removeButton();
            if(playerHand.canTsumo())playerGUI.setTsumoButton();
            if(playerHand.canLi_zhi()&&!li_zhi)playerGUI.setLi_zhiButton();
        }
    }

    //デバッグ用
    public void addDummyPlayer(){
        playerList.put(playerList.size(),new PlayerData(null));
        playerList.get(playerList.size()-1).playerUUID=UUID.randomUUID();
        seatList.put(playerList.get(playerList.size()-1).playerUUID,seatList.size());
    }

    public void openInventory(Player player, Inventory inv){//非同期用
        if(player==null)return;
        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class),new Runnable(){
            @Override
            public void run(){
                player.openInventory(inv);
            }
        });
    }

    public boolean isTurnPl(UUID uuid){
        if(!seatList.containsKey(uuid))return false;
        return seatList.get(uuid)==turnSeat;
    }

    private ItemStack getHead(Player player){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) head.getItemMeta();
        skull.setOwningPlayer(player);
        head.setItemMeta(skull);
        return head;

    }

    public boolean addPlayer(Player player){//メインスレッドからしか動かさないでね
        if(playerList.size()>=maxSeat||(betting&&GlobalClass.vaultManager.getBalance(player.getUniqueId())<24000*rate))return false;
        playerList.put(playerList.size(),new PlayerData(player));
        if(betting)GlobalClass.vaultManager.withdraw(player,24000*rate);
        seatList.put(player.getUniqueId(),seatList.size());
        PlayerData playerData=playerList.get(seatList.get(player.getUniqueId()));
        for(int i=0;i<playerList.size()-1;i++){
            playerData.playerGUI.setOthersHead(playerList.get(i).head,(3*seatList.get(player.getUniqueId())+i+3)%4);
            playerList.get(i).playerGUI.setOthersHead(playerData.head,(3*i+seatList.get(player.getUniqueId())+3)%4);
        }
        if(seatList.get(player.getUniqueId())<2){
            disTilesGUIS.get(0).setPlayerHead(seatList.get(player.getUniqueId()),getHead(player));
        }
        else{disTilesGUIS.get(1).setPlayerHead(seatList.get(player.getUniqueId()),getHead(player));}
        GlobalClass.currentPlayer.put(player.getUniqueId(),masterPlayer.getUniqueId());
        player.openInventory(getPlData(player).playerGUI.inv.inv);
        return true;
    }

    protected void setRestTiles(int rest){
        for(PlayerData playerData:playerList.values()){
            playerData.playerGUI.setRestTiles(rest);
        }
    }

    protected void setPlayersPane() {
        for (int i=0;i<maxSeat;i++) {
            for (int j = 0; j < maxSeat; j++) {
                if(i==j)continue;
                if (turnSeat == j)
                    playerList.get(i).playerGUI.putOthersPane_Red((3*i+j +3)%4);
                else playerList.get(i).playerGUI.putOthersPane_Lime((3*i+j+3)%4);
            }
        }
    }

    protected void reset(){
        deck.reset();
        disTilesGUIS.get(0).resetTiles();
        disTilesGUIS.get(1).resetTiles();
        ronTiles=new ArrayList<>();
        canRon=0;
        canRonPAc=0;
        ronPSeat=new ArrayList<>();
        for(PlayerData playerData:playerList.values()){
            playerData.playerDisTiles.reset();
            playerData.playerGUI.reset();
            playerData.playerHand.reset();
            playerData.remTime=firstRemTime;
            playerData.li_zhi=false;
            playerData.winningTiles=new ArrayList<>();
        }
    }

    protected void setClock(int time){
        for(PlayerData playerData:playerList.values()){
            playerData.playerGUI.setClock(time);
            playerData.playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON);
        }
    }

    protected void dealFirstTiles(){
        for(PlayerData playerData:playerList.values()) {
            for (int i = 0; i < 8; i++) {
                playerData.playerHand.addTile(deck.draw());
            }
        }
    }

    protected void playSoundAlPl(Sound sound){
        for(PlayerData playerData:playerList.values()){
            playerData.playSound(sound);
        }
    }

    protected void playSoundAlPl(Sound sound,float pitch){
        for(PlayerData playerData:playerList.values()){
            Player player= Bukkit.getPlayer(playerData.playerUUID);
            if(player!=null)player.playSound(player.getLocation(),sound,2,pitch);
        }
    }

    protected void setFirstTiles(){
        for(int i=0;i<maxSeat;i++){
            for(int j=0;j<maxSeat;j++){
                if(i!=j){
                    playerList.get(j).playSound(Sound.BLOCK_IRON_DOOR_CLOSE);
                }
                playerList.get(i).playerGUI.setTiles(playerList.get(i).playerHand.hand);
                playerList.get(j).playSound(Sound.BLOCK_IRON_DOOR_CLOSE);
                threadSleep(100);
            }
        }
    }

    protected int setYakuAndPoint(PlayerData playerData){
        resultGUI.clear();
        List<Yaku> list=playerData.playerHand.yaku();
        if(playerData.head!=null)resultGUI.inv.setItem(40,playerData.head);//if文はデバッグ用
        int count=0,point=0;
        playerData.playerHand.drawnToHand();
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(playerData.playerHand.hand[i][j]==1){
                    resultGUI.inv.setTile(count+45,i,j);
                    playSoundAlPl(Sound.BLOCK_CROP_BREAK);
                    threadSleep(300);
                    count+=1;
                }
            }
        }
        if(count!=9)resultGUI.inv.setTile(53,100);
        for(int i=0;i<list.size();i++){
            threadSleep(1000);
            resultGUI.setYaku(20+(i/5)*9+i%5,list.get(i).number,list.get(i).name,list.get(i).point);
            playSoundAlPl(Sound.BLOCK_ANVIL_FALL);
        }
        if(seatList.get(playerData.playerUUID)==leaderSeat%maxSeat){
            threadSleep(1000);
            resultGUI.inv.setItem(20+(list.size()/5)*9+list.size()%5,Material.PLAYER_HEAD,"§l§d役：親孝行","§l§6×1.5倍");
            playSoundAlPl(Sound.BLOCK_ANVIL_FALL);
        }
        else if(turnSeat==leaderSeat%maxSeat){
            threadSleep(1000);
            resultGUI.inv.setItem(20+(list.size()/5)*9+list.size()%5,Material.PLAYER_HEAD,"§l§d役：親不幸","§l§6×1.5倍");
            playSoundAlPl(Sound.BLOCK_ANVIL_FALL);
        }
        threadSleep(1000);
        for (Yaku yaku : list) point += yaku.point;
        if(seatList.get(playerData.playerUUID)==leaderSeat%maxSeat||turnSeat==leaderSeat%maxSeat)
        resultGUI.setPoint(15,point*3/2);
        else resultGUI.setPoint(15,point);
        playSoundAlPl(Sound.ITEM_TOTEM_USE);
        threadSleep(3000);
        saveHandsData(list,point,playerData.name,leaderSeat%maxSeat==seatList.get(playerData.playerUUID),turnSeat==leaderSeat%maxSeat);
        return point;
    }

    protected void setPlayerHeadsData(){
        for(PlayerData playerData:playerList.values()){
            if(playerData.head==null)continue;//デバッグ用だけど残してもいい
            ItemMeta meta=playerData.head.getItemMeta();
            if(leaderSeat%maxSeat==seatList.get(playerData.playerUUID))meta.lore(Arrays.asList(Component.text("§e持ち点:"+playerData.point),Component.text("§4親")));
            else meta.lore(Arrays.asList(Component.text("§e持ち点:"+playerData.point),Component.text("§4子")));
            playerData.head.setItemMeta(meta);
        }
        for(int i=0;i<maxSeat;i++){
            for(int j=0;j<maxSeat;j++){
                if(i==j) playerList.get(j).playerGUI.inv.setItem(17,playerList.get(i).head);
                else playerList.get(j).playerGUI.setOthersHead(playerList.get(i).head,(3*j+i+3)%4);
            }
        }
    }

    protected void kankankan(){
        threadSleep(300);
        playSoundAlPl(Sound.BLOCK_ANVIL_USE,0);
        threadSleep(2000);
    }

    protected void setRonButton(int turnS,int row){
        if(!ronTiles.contains(row))return;
        for(int i=0;i<maxSeat;i++){
            if(i==turnS)continue;
            if(playerList.get(i).winningTiles.contains(row)){
                playerList.get(i).playerGUI.setRonButton();
                canRon+=1;
            }
        }
    }

    protected void setPlayerPoint(int seat){
        resultGUI.inv.setItem(10*seat+9,playerList.get(seat).head);
        resultGUI.setPoint(10*seat+14,playerList.get(seat).point);
    }

    protected void removePlayerPoint(int seat){
        resultGUI.inv.removeItem(10*seat+10);
        resultGUI.inv.removeItem(10*seat+11);
        resultGUI.inv.removeItem(10*seat+12);
        resultGUI.inv.removeItem(10*seat+13);
        resultGUI.inv.removeItem(10*seat+14);
    }

    protected void setAllPlPoint(){
        for(int i=0;i<maxSeat;i++){
            setPlayerPoint(i);
        }
    }

    protected void threadSleep(int time){
        try {
            sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected String getDateForMySQL(Date date){
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HHH:mm:ss");
        return df.format(date);
    }

    public PlayerData getPlData(Player player){
        return playerList.get(seatList.get(player.getUniqueId()));
    }

    private void cancelGame(){
        GlobalClass.DonjaraTable.remove(masterPlayer.getUniqueId());
        threadSleep(1000);
        for(int i=0;i<playerList.size();i++){
            PlayerData playerData=playerList.get(i);
            Player player=Bukkit.getPlayer(playerData.playerUUID);
            if(player!=null) {
                GlobalClass.vaultManager.deposit(player,playerData.point*rate);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), new Runnable() {
                    @Override
                    public void run() {
                        player.closeInventory();
                    }
                });
            }
            GlobalClass.currentPlayer.remove(playerData.playerUUID);
        }
    }

    protected void endGame() {
        GlobalClass.DonjaraTable.remove(masterPlayer.getUniqueId());
        threadSleep(1000);
        int sum = 0;
        endTime = new Date();
        StringBuilder query = new StringBuilder("update gameLog set endTime='" + getDateForMySQL(endTime) + "'");
        for (int i = 0; i < maxSeat; i++) {
            PlayerData playerData = playerList.get(i);
            Player player = Bukkit.getPlayer(playerData.playerUUID);
            sum += playerData.point;
            query.append(",P").append(i + 1).append("Point=").append(playerData.point);
            if (player != null) {
                GlobalClass.vaultManager.deposit(player, playerData.point * rate);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), new Runnable() {
                    @Override
                    public void run() {
                        player.closeInventory();
                    }
                });
            }
            GlobalClass.currentPlayer.remove(playerData.playerUUID);
        }
        query.append(" where id=").append(gameId).append(";");
        int finalSum = sum;
        if (!mysql.execute(query.toString()) || finalSum != 24000 * playerList.size()) {
            System.out.println("ドンジャラのゲームデータをMySQLに保存できませんでした　安全のため、新規ゲームを開催不能にします");
            GlobalClass.playable = false;
        }
        if (rate!=0) {
            for (int i = 0; i < playerList.size(); i++) {
                updatePlayerData(i);
            }
        }
    }

    private void updatePlayerData(int seat){
        PlayerData playerData=playerList.get(seat);
        ResultSet result=mysql.query("select totalWinningPoint,totalWinningMoney from playerData where uuid='"+ playerData.playerUUID +"';");
        int winningPoint=playerData.point-24000,winningMoney=winningPoint*rate;
        if(result!=null){
            try {
                if(!result.next()){
                    mysql.execute("insert into playerData(name,uuid) values('"+ playerData.name +"','"+ playerData.playerUUID +"');");
                    result.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        StringBuilder query=new StringBuilder("update playerData set ");
        if(winningPoint>0){
            query.append("totalWinningPoint=totalWinningPoint+").append(winningPoint).append(",totalWinningMoney=totalWinningMoney+")
                    .append(winningPoint).append(",");
        }
        query.append("totalPoint=totalPoint+").append(winningPoint).append(",totalMoney=totalMoney+").append(winningMoney)
                .append(" where uuid='").append(playerData.playerUUID).append("';");
        mysql.execute(query.toString());
        mysql.close();
    }

    protected boolean pointMovement_Ron(int receiver,int sender,int point){//点が0以下になったらtrueを返す
        if(sender==leaderSeat%maxSeat||receiver==leaderSeat%maxSeat)point*=1.5;
        point=Math.min(point,playerList.get(sender).point);
        playerList.get(receiver).point+=point;
        playerList.get(sender).point-=point;
        for(int i=0;i<maxSeat;i++){
            if(i!=receiver&&i!=sender)continue;
            threadSleep(500);
            removePlayerPoint(i);
            threadSleep(500);
            setPlayerPoint(i);
            playSoundAlPl(Sound.BLOCK_BAMBOO_BREAK);
            threadSleep(300);
        }
        return playerList.get(sender).point == 0;
    }

    public void setLi_zhiStick(){
        for(int i=0;i<maxSeat;i++){
            if(i==turnSeat)playerList.get(i).playerGUI.setLi_zhiStick();
            else playerList.get(i).playerGUI.setOthersLi_zhi((3*i+turnSeat+3)%4);
        }
        playSoundAlPl(Sound.BLOCK_BEACON_ACTIVATE);
    }

    private void saveHandsData(List<Yaku> yakuList,int point,String plname,boolean parent,boolean childrenToParent){
        StringBuilder hands=new StringBuilder();
        int finalPoint=point;
        for(int i=0;i<yakuList.size();i++){
            hands.append(yakuList.get(i).number);
            if(i!=yakuList.size()-1){
                hands.append(",");
            }
        }
        if(parent){
            finalPoint=point*3/2;
            hands.append(",15");
        }
        else if(childrenToParent){
            finalPoint=point*3/2;
            hands.append(",16");
        }
        mysql.execute("insert into handsLog(gameid,player,point,hands) values("+ gameId +", '"+ plname +"'," + finalPoint +",'"+ hands.toString() +"');");
    }

    private boolean registerGameLog(){
        //mysqlの接続準備とゲームの開始ログ入れ
        mysql= new MySQLManager(Main.getPlugin(Main.class),"Donjara");
        startTime= new Date();
        StringBuilder query=new StringBuilder();
        query.append("insert into gameLog(startTime,P1,P2,P3,P4,Rate) values('").append(getDateForMySQL(startTime)).append("'");
        for(int i=0;i<playerList.size();i++){
            query.append(",'").append(playerList.get(i).name).append("'");
        }
        for(int i=playerList.size();i<4;i++){
            query.append(",null");
        }
        query.append(",").append(rate).append(");");
        mysql.execute(query.toString());

        //gameID取得
        ResultSet result=mysql.query("select id from gameLog where P1='"+ playerList.get(0).name +"' order by startTime desc limit 1 ;");
        if(result!=null){
            try {
                if(result.next()){
                    if(result.getRow()!=0){
                        gameId=result.getInt("id");
                    }
                }
                result.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        try{
            mysql.close();
            return true;
        }
        catch (NullPointerException e) {
            System.out.println("[Donjara]ゲーム開始時のDB接続に失敗しました");
            return false;
        }
    }

    private void updatePriceRanking(PlayerData playerData){
        int i;
        for(i=6;i>1;i--){
            if(GlobalClass.config.getInt("winningPriceRanking."+ (i-1) +".rate")*(GlobalClass.config.getInt("winningPriceRanking."+ (i-1) +".point")-24000)>(playerData.point-24000)*rate)break;
        }
        if(i!=6){
            for(int j=5;j>i;j--){
                GlobalClass.config.setInt("winningPriceRanking."+ j +".rate",GlobalClass.config.getInt("winningPriceRanking."+ (j-1) +".rate"));
                GlobalClass.config.setInt("winningPriceRanking."+ j +".point",GlobalClass.config.getInt("winningPriceRanking."+ (j-1) +".point"));
                GlobalClass.config.setString("winningPriceRanking."+ j +".name",GlobalClass.config.getString("winningPriceRanking."+ (j-1) +".name"));
            }
            GlobalClass.config.setInt("winningPriceRanking."+ i +".rate",rate);
            GlobalClass.config.setInt("winningPriceRanking."+ i +".point",playerData.point);
            GlobalClass.config.setString("winningPriceRanking."+ i +".name",playerData.name);
            Player player=Bukkit.getPlayer(playerData.playerUUID);
            if(player!=null){
                player.sendMessage("§e§lおめでとうございます！一回の獲得金額ランキング"+ i +"位に登録されました！");
            }
        }
    }

    protected void sendResult(){
        String[] message=new String[6];
        message[0]="==========結果==========";
        for(int i=0;i<maxSeat;i++){
            PlayerData playerData=playerList.get(i);
            if(playerData.name==null)continue;//デバッグ用
            message[i+1]=playerData.name+"："+playerData.point+"点";
        }
        message[maxSeat+1]="========================";
        for(PlayerData playerData:playerList.values()){
            Player player=Bukkit.getPlayer(playerData.playerUUID);
            if(player==null)continue;
            player.sendMessage(message);
            updatePriceRanking(playerData);
        }
    }

    protected boolean pointMovement_Tsumo(int receiver,int point){//点が0以下になったらtrueを返す 親と子のツモで処理を変えてる
        int insPoint=0;
        boolean r=false;
        if(receiver==leaderSeat%maxSeat){
            point*=1.5;
            for(int i=0;i<maxSeat;i++){//innnsPoint/3は100の位までで必ず割り切れる
                if(i==receiver)continue;
                insPoint+=Math.min(point/(maxSeat-1),playerList.get(i).point);
                playerList.get(i).point-=Math.min(point/(maxSeat-1),playerList.get(i).point);
                if(playerList.get(i).point==0)r=true;
            }
            playerList.get(receiver).point+=insPoint;
        }
        else{
            int childPoint=(point/(100*(2*maxSeat+1)))*200;//子から引かれる予定の点
            for(int i=0;i<maxSeat;i++){
                if(i==receiver)continue;
                if(i==leaderSeat%maxSeat){
                    insPoint+=Math.min(point-(maxSeat-2)*childPoint,playerList.get(i).point);
                    playerList.get(i).point-=Math.min(point-(maxSeat-2)*childPoint,playerList.get(i).point);
                    if(playerList.get(i).point==0)r=true;
                }
                else{
                    insPoint+=Math.min(childPoint,playerList.get(i).point);
                    playerList.get(i).point-=Math.min(childPoint,playerList.get(i).point);
                    if(playerList.get(i).point==0)r=true;
                }
            }
            playerList.get(receiver).point+=insPoint;
        }
        for(int i=0;i<maxSeat;i++){
            threadSleep(500);
            removePlayerPoint(i);
            threadSleep(500);
            setPlayerPoint(i);
            playSoundAlPl(Sound.BLOCK_BAMBOO_BREAK);
            threadSleep(800);
        }
        return r;
    }

    @Override
    public void run() {
        for (int i = 120; i > 0 && playerList.size() < maxSeat; i--) {
            if (i % 20 == 0) Bukkit.getServer().broadcast(Component.text("§l§w"+masterPlayer.getName() + "§l§aが一点あたり§r§e" + rate + "$§l§aで§e" + maxSeat + "§a人ドンジャラを募集中・・・残り§c" + i + "§a秒  §4必要金額："+rate*24000+"$"), Server.BROADCAST_CHANNEL_USERS);
            threadSleep(1000);
        }
        if (playerList.size() != maxSeat) {
            Bukkit.getServer().broadcast(Component.text(masterPlayer.getName() + "の卓は人が集まらなかったので解散しました"), Server.BROADCAST_CHANNEL_USERS);
            cancelGame();
            return;
        }
        if(!registerGameLog()){
            for(int i=0;i<playerList.size();i++){
                Player player=Bukkit.getPlayer(playerList.get(i).playerUUID);
                if(player!=null){
                    player.sendMessage("ゲームの情報を取得できませんでした ゲームがキャンセルされます");
                }
            }
            cancelGame();
            return;
        }
        leaderSeat = random.nextInt(maxSeat);
        firstLeader=leaderSeat;
        while (canContinue){//一回のゲームの流れ
            int removedTile=0;
            turnSeat=leaderSeat%maxSeat;
            setPlayerHeadsData();
            dealFirstTiles();
            setFirstTiles();
            while (deck.size()>0) {
                setPlayersPane();
                setRestTiles(deck.size());
                PlayerData turnPData=playerList.get(turnSeat);
                turnPData.drawTile();
                turnPData.setButtons();
                turnPData.discardedTileNum=0;
                for (int i = turnPData.remTime * 10; i > 0; i--) {
                    if(turnPData.li_zhi&&turnPData.discardedTileNum==0){
                        threadSleep(500);
                        if(turnPData.playerHand.canTsumo()){
                            for(int j =0;j<20&&turnPData.li_zhi;j++){
                                threadSleep(1000);
                            }
                            turnPData.li_zhi=true;
                        }
                        if(turnPData.discardedTileNum!=101)turnPData.discardedTileNum=9;
                        break;
                    }
                    if (i % 10 == 0) {
                        setClock(i/10);
                        turnPData.remTime = turnPData.remTime - 1;
                    }
                    if(turnPData.discardedTileNum!=0)break;
                    threadSleep(100);
                }
                playSoundAlPl(Sound.BLOCK_CHAIN_STEP);
                turnPData.playerGUI.removeButton();
                if(turnPData.discardedTileNum==101){//ツモした場合
                    kankankan();
                    break;
                }
                else if(turnPData.discardedTileNum==0)turnPData.discardedTileNum=9;
                removedTile=turnPData.removeTile();//ここで手が入れ替わる
                setRonButton(turnSeat,removedTile/10);
                if(turnPData.li_zhi&&turnPData.preLi_zhi){//リーチ時にロンできる牌を登録
                    turnPData.preLi_zhi=false;
                    turnPData.winningTiles=turnPData.playerHand.getWinningTiles();
                    ronTiles.addAll(turnPData.winningTiles);
                }
                turnPData.remTime = Math.max(10, Math.min(turnPData.remTime+5,firstRemTime));//10と+5
                for(int i=0;i<10&&canRonPAc<canRon;i++){
                    threadSleep(1000);
                }
                for(int i=0;i<maxSeat;i++)playerList.get(i).playerGUI.removeButton();
                if(ronPSeat.size()!=0){//ロンされた場合
                    kankankan();
                    break;
                }
                turnPData.playerGUI.setTiles(turnPData.playerHand.hand);
                turnPData.preLi_zhi=false;
                threadSleep(500);
                turnSeat=(turnSeat+1)%maxSeat;
                canRonPAc=0;
                canRon=0;
                if(deck.size()==0){
                    leaderSeat+=1;
                    threadSleep(2000);
                }
            }
            playerList.get(turnSeat).playerGUI.removeButton();
            //結果表示
            for(PlayerData playerData:playerList.values()){
                Player player=Bukkit.getPlayer(playerData.playerUUID);
                if(player!=null){openInventory(player,resultGUI.inv.inv);}
            }
            PlayerData playerData=playerList.get(turnSeat);
            if(playerData.discardedTileNum==101){//ツモ
                int movePoint=setYakuAndPoint(playerData);//int帰ってくるからそれ使って点数の配分 この中でinv操作の処理
                threadSleep(3000);
                resultGUI.clear();
                threadSleep(2000);
                setAllPlPoint();
                canContinue=!pointMovement_Tsumo(turnSeat,movePoint);
                if(turnSeat!=leaderSeat%maxSeat)leaderSeat+=1;
            }
            else if(ronPSeat.size()!=0){//ロン
                List<Integer> integerList=new ArrayList<>();
                for(Integer seat:ronPSeat){
                    playerList.get(seat).playerHand.addTile(removedTile);
                    integerList.add(setYakuAndPoint(playerList.get(seat)));
                    threadSleep(3000);
                    resultGUI.clear();
                }
                threadSleep(100);
                setAllPlPoint();
                threadSleep(2000);
                for(int i=0;i<integerList.size();i++){
                    if(canContinue)canContinue=!pointMovement_Ron(ronPSeat.get(i),turnSeat,integerList.get(i));
                    threadSleep(500);
                }
                if(!ronPSeat.contains(leaderSeat%maxSeat))leaderSeat+=1;
            }
            else {
                setAllPlPoint();
                threadSleep(2000);
            }
            for(PlayerData plData:playerList.values()){
                if(Bukkit.getPlayer(plData.playerUUID)!=null)openInventory(Bukkit.getPlayer(plData.playerUUID),plData.playerGUI.inv.inv);
            }
            threadSleep(4000);
            if(canContinue)canContinue=leaderSeat<maxSeat+firstLeader;
            reset();
        }
        //ゲーム終了処理
        sendResult();
        endGame();
    }
}
