package ltotj.minecraft.donjara;

import ltotj.minecraft.donjara.game.Yaku;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DonjaraCPU extends Donjara {

    public DonjaraCPU(Player player) {
        super(player,3);
        addPlayer(player);
        addCUP();
        addCUP();
        start();
    }

    private void addCUP(){
        playerList.put(playerList.size(),new PlayerData(null));
        playerList.get(playerList.size()-1).playerUUID= UUID.randomUUID();
        seatList.put(playerList.get(playerList.size()-1).playerUUID,seatList.size());
        playerList.get(playerList.size()-1).name="CPU";
        playerList.get(playerList.size()-1).head=new ItemStack(Material.PLAYER_HEAD);
    }

    @Override
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
        return point;
    }

    @Override
    protected void endGame(){
        GlobalClass.DonjaraTable.remove(masterPlayer.getUniqueId());
        threadSleep(1000);
        Player p=Bukkit.getPlayer(playerList.get(0).playerUUID);
        if(p!=null){
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), new Runnable() {
                    @Override
                    public void run() {
                        p.closeInventory();
                    }
                });
            }
        GlobalClass.currentPlayer.remove(playerList.get(0).playerUUID);
    }

    @Override
    public void run(){
        leaderSeat=0;
        //一回のゲームの流れ
            int removedTile=0;
            turnSeat=leaderSeat;
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
                if(turnSeat!=0)turnPData.remTime=1;
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
                turnPData.remTime = Math.max(10, Math.min(turnPData.remTime+5,firstRemTime));
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
                Player player= Bukkit.getPlayer(playerData.playerUUID);
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
        //ゲーム終了処理
        sendResult();
        endGame();
    }
}
