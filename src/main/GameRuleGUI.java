package ltotj.minecraft.donjara;

import ltotj.minecraft.donjara.game.TileList;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GameRuleGUI {
    List<InventoryGUI> yakuGUIs=new ArrayList<>();
    TileList tileList=new TileList();


    GameRuleGUI(){

        yakuGUIs.add(new InventoryGUI(54,"役一覧"));
        InventoryGUI mainGUI=yakuGUIs.get(0);
        for(int i=0;i<9;i++){
            mainGUI.setItem(i, Material.WHITE_STAINED_GLASS_PANE,"");
            mainGUI.setItem(45+i, Material.WHITE_STAINED_GLASS_PANE,"");
        }
        mainGUI.setItem(49,Material.RED_STAINED_GLASS_PANE,"§c参加中のゲームに戻る");
        //役をズラーっと設置 不格好だけどしょうがないね
        mainGUI.setItem(9,tileList.getYakuMaterial(0),"§l§d三色","§l§61000点","§eクリックで詳細を表示");
        mainGUI.setItem(10,tileList.getYakuMaterial(1),"§l§d二色","§l§66000点","§eクリックで詳細を表示");
        mainGUI.setItem(11,tileList.getYakuMaterial(2),"§l§d一色","§l§620000点","§eクリックで詳細を表示");
        mainGUI.setItem(12,tileList.getYakuMaterial(3),"§l§d三種類","§l§66000点","§eクリックで詳細を表示");
        mainGUI.setItem(13,tileList.getYakuMaterial(4),"§l§dハイランダー","§l§63000点","§eクリックで詳細を表示");
        mainGUI.setItem(14,tileList.getYakuMaterial(5),"§l§dいつもの３人(?)","§l§65000点","§eクリックで詳細を表示");
        mainGUI.setItem(15,tileList.getYakuMaterial(6),"§l§dおまんじゅう！","§l§64000点","§eクリックで詳細を表示");
        mainGUI.setItem(16,tileList.getYakuMaterial(7),"§l§dいざ採掘","§l§63000点","§eクリックで詳細を表示");
        mainGUI.setItem(17,tileList.getYakuMaterial(8),"§l§dいざ伐採","§l§63000点","§eクリックで詳細を表示");
        mainGUI.setItem(18,tileList.getYakuMaterial(9),"§l§dさらなる深みへ","§l§620000点","§eクリックで詳細を表示");
        mainGUI.setItem(19,tileList.getYakuMaterial(10),"§l§dモンスターハント","§l§67000点","§eクリックで詳細を表示");
        mainGUI.setItem(20,tileList.getYakuMaterial(11),"§l§d道具の準備は万端","§l§65000点","§eクリックで詳細を表示");
        mainGUI.setItem(21,tileList.getYakuMaterial(12),"§l§d立方体","§l§65000点","§eクリックで詳細を表示");
        mainGUI.setItem(22,tileList.getYakuMaterial(13),"§l§dダイヤまみれ","§l§66000点","§eクリックで詳細を表示");
        mainGUI.setItem(23,tileList.getYakuMaterial(14),"§l§dゴールデン","§l§61つにつき2000点","§eクリックで詳細を表示");

        for(int i=0;i<15;i++) {
            yakuGUIs.add(new InventoryGUI(27, "役"));
            for(int j=0;j<9;j++){
                yakuGUIs.get(i+1).setItem(j,Material.WHITE_STAINED_GLASS_PANE,"");
                yakuGUIs.get(i+1).setItem(18+j,Material.WHITE_STAINED_GLASS_PANE,"");
            }
            yakuGUIs.get(i+1).setItem(22,Material.RED_STAINED_GLASS_PANE,"§c役一覧に戻る");
        }
        setExample(0, new int[]{0,1,6,40,44,45,81,86,87},"§l§d三色","§l§61000点","§9３つのグループで揃えることで成立","§9オールマイティ：§e有効");
        setExample(1,new int[]{10,11,12,13,14,15,50,51,53},"§l§d二色","§l§66000点","§9２つのグループで揃えることで成立","§9オールマイティ：§e有効");
        setExample(2,new int[]{60,61,62,63,64,65,66,67,68},"§l§d一色","§l§620000点","§9１つのグループで揃えることで成立","§9オールマイティ：§e有効");
        setExample(3,new int[]{20,21,22,44,45,46,70,71,72},"§l§d三種類","§l§66000点","§9３種類のアイテムを揃えることで成立","§9オールマイティ：§e有効");
        setExample(4,new int[]{0,4,8,50,53,56,60,63,66},"§l§dハイランダー","§l§63000点","§9９種類のアイテムを揃えることにより成立","§9オールマイティ：§e有効");
        setExample(5,new int[]{10,11,14,20,24,25,80,82,84},"§l§dいつもの３人(?)","§l§65000点","§9ゾンビ・クリーパー・スケルトンを一個ずつ揃えることで成立","§9オールマイティ：§e有効");
        setExample(6,new int[]{30,31,34,40,44,48,70,73,76},"§l§dおまんじゅう！","§l§64000点","§9桃・白・ライムの羊毛を一個ずつ揃えることで成立","§9オールマイティ：§e有効");
        setExample(7,new int[]{10,14,15,30,31,32,60,61,63},"§l§dいざ採掘","§l§63000点","§9ピッケルと鉱石をそれぞれ１セット以上揃えることで成立","§9オールマイティ：§e有効");
        setExample(8,new int[]{20,21,28,50,53,58,63,66,67},"§l§dいざ伐採","§l§63000点","§9オノと原木をそれぞれ１セット以上揃えることで成立","§9オールマイティ：§e有効");
        setExample(9,new int[]{34,35,36,66,67,68,86,87,88},"§l§dさらなる深みへ","§l§620000点","§9ネザーに現れるブロック・MOBを全て揃えることで成立","§9オールマイティ：§e有効");
        setExample(10,new int[]{0,1,8,40,41,44,80,85,87},"§l§dモンスターハント","§l§67000点","§9武器（剣またはオノ）、防具、スポーンエッグをそれぞれ１セット以上揃えることで成立","§9オールマイティ：§e有効");
        setExample(11,new int[]{0,6,7,11,16,18,24,25,26},"§l§d道具の準備は万端","§l§65000点","§9ツルハシ、オノ、剣のみでアガることで成立","§9オールマイティ：§e有効");
        setExample(12,new int[]{30,31,35,64,65,66,72,73,74},"§l§d立方体","§l§65000点","§9ブロックのみでアガることで成立","§9オールマイティ：§e有効");
        setExample(13,new int[]{0,1,2,20,21,22,30,31,32},"§l§dダイヤまみれ","§l§66000点","§9ダイヤ製のアイテムのみでアガることで成立","§9オールマイティ：§e有効");
        setExample(14,new int[]{0,1,8,14,15,18,56,57,58},"§l§dゴールデン","§l§61つにつき2000点","§9金製のアイテム一つにつき2000点加算","§9オールマイティ：§4無効");
    }

    private void setExample(int yakuNum,int[] tiles,String name,String... lore){
        yakuGUIs.get(yakuNum+1).setItem(4,tileList.getYakuMaterial(yakuNum),name,lore);
        for(int i=0;i<9;i++)yakuGUIs.get(yakuNum+1).setTile(i+9,tiles[i],"§a例");
    }

}
