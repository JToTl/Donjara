package ltotj.minecraft.donjara;

import ltotj.minecraft.donjara.game.TileList;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GameRuleGUI {
    InventoryGUI ruleGUI=new InventoryGUI(27,"牌・役確認");
    List<InventoryGUI> yakuGUIs=new ArrayList<>(),tileGUIs=new ArrayList<>();
    TileList tileList=new TileList();


    GameRuleGUI(){

        tileGUIs.add(new InventoryGUI(54,"牌一覧"));
        tileGUIs.add(new InventoryGUI(54,"牌一覧"));
        yakuGUIs.add(new InventoryGUI(54,"役一覧"));
        InventoryGUI mainGUI=yakuGUIs.get(0);
        for(int i=0;i<9;i++){
            ruleGUI.setItem(i, Material.WHITE_STAINED_GLASS_PANE,"");
            ruleGUI.setItem(i+18, Material.WHITE_STAINED_GLASS_PANE,"");
            mainGUI.setItem(i, Material.WHITE_STAINED_GLASS_PANE,"");
            mainGUI.setItem(45+i, Material.WHITE_STAINED_GLASS_PANE,"");
            tileGUIs.get(0).setItem(45+i, Material.WHITE_STAINED_GLASS_PANE,"");
            tileGUIs.get(1).setItem(45+i, Material.WHITE_STAINED_GLASS_PANE,"");
        }
        ruleGUI.setItem(9,Material.DIAMOND_PICKAXE,"§a牌を確認する");
        ruleGUI.setItem(13,Material.CRAFTING_TABLE,"§a役を確認する");
        ruleGUI.setItem(22,Material.RED_STAINED_GLASS_PANE,"§c参加中のゲームに戻る");

        for(int i=0;i<4;i++){
            for(int j=0;j<9;j++){
                tileGUIs.get(0).setTile(9*i+j,i,j);
                tileGUIs.get(1).setTile(9*i+j,i+5,j);
            }
        }
        tileGUIs.get(1).setItem(36,Material.NETHER_STAR,"オールマイティー");
        for(int i=0;i<9;i++)tileGUIs.get(0).setTile(36+i,4,i);
        tileGUIs.get(0).setItem(53,Material.LIME_STAINED_GLASS_PANE,"§a２ページ目へ");
        tileGUIs.get(1).setItem(53,Material.LIME_STAINED_GLASS_PANE,"§a１ページ目へ");
        tileGUIs.get(0).setItem(49,Material.RED_STAINED_GLASS_PANE,"§c前の画面に戻る");
        tileGUIs.get(1).setItem(49,Material.RED_STAINED_GLASS_PANE,"§c前の画面に戻る");


        mainGUI.setItem(49,Material.RED_STAINED_GLASS_PANE,"§c前の画面に戻る");
        //役をズラーっと設置 不格好だけどしょうがないね
        mainGUI.setItem(9,tileList.getYakuMaterial(0),"§l§d役：三色","§l§61000点","§eクリックで詳細を表示");
        mainGUI.setItem(10,tileList.getYakuMaterial(1),"§l§d役：二色","§l§66000点","§eクリックで詳細を表示");
        mainGUI.setItem(11,tileList.getYakuMaterial(2),"§l§5役：一色","§l§620000点","§eクリックで詳細を表示");
        mainGUI.setItem(12,tileList.getYakuMaterial(3),"§l§d役：三種類","§l§66000点","§eクリックで詳細を表示");
        mainGUI.setItem(13,tileList.getYakuMaterial(4),"§l§d役：ハイランダー","§l§63000点","§eクリックで詳細を表示");
        mainGUI.setItem(14,tileList.getYakuMaterial(5),"§l§d役：いつもの３人(?)","§l§65000点","§eクリックで詳細を表示");
        mainGUI.setItem(15,tileList.getYakuMaterial(6),"§l§d役：おまんじゅう！","§l§64000点","§eクリックで詳細を表示");
        mainGUI.setItem(16,tileList.getYakuMaterial(7),"§l§d役：いざ採掘","§l§63000点","§eクリックで詳細を表示");
        mainGUI.setItem(17,tileList.getYakuMaterial(8),"§l§d役：いざ伐採","§l§63000点","§eクリックで詳細を表示");
        mainGUI.setItem(18,tileList.getYakuMaterial(9),"§l§5役：さらなる深みへ","§l§620000点","§eクリックで詳細を表示");
        mainGUI.setItem(19,tileList.getYakuMaterial(10),"§l§d役：モンスターハント","§l§67000点","§eクリックで詳細を表示");
        mainGUI.setItem(20,tileList.getYakuMaterial(11),"§l§d役：道具の準備は万端","§l§65000点","§eクリックで詳細を表示");
        mainGUI.setItem(21,tileList.getYakuMaterial(12),"§l§d役：立方体","§l§65000点","§eクリックで詳細を表示");
        mainGUI.setItem(22,tileList.getYakuMaterial(13),"§l§5役：ダイヤまみれ","§l§612000点","§eクリックで詳細を表示");
        mainGUI.setItem(23,tileList.getYakuMaterial(14),"§l§d役：ゴールデン","§l§61つにつき2000点","§eクリックで詳細を表示");

        for(int i=0;i<15;i++) {
            yakuGUIs.add(new InventoryGUI(27, "役"));
            for(int j=0;j<9;j++){
                yakuGUIs.get(i+1).setItem(j,Material.WHITE_STAINED_GLASS_PANE,"");
                yakuGUIs.get(i+1).setItem(18+j,Material.WHITE_STAINED_GLASS_PANE,"");
            }
            yakuGUIs.get(i+1).setItem(22,Material.RED_STAINED_GLASS_PANE,"§c役一覧に戻る");
        }
        setExample(0, new int[]{0,1,6,40,44,45,81,86,87},"§l§d役：三色","§l§61000点","§9３つのグループで揃えることで成立","§9オールマイティ：§e有効");
        setExample(1,new int[]{10,11,12,13,14,15,50,51,53},"§l§d役：二色","§l§66000点","§9２つのグループで揃えることで成立","§9オールマイティ：§e有効");
        setExample(2,new int[]{60,61,62,63,64,65,66,67,68},"§l§5役：一色","§l§620000点","§9１つのグループで揃えることで成立","§9オールマイティ：§e有効");
        setExample(3,new int[]{20,21,22,44,45,46,70,71,72},"§l§d役：三種類","§l§66000点","§9３種類のアイテムを揃えることで成立","§9オールマイティ：§e有効");
        setExample(4,new int[]{0,4,8,50,53,56,60,63,66},"§l§d役：ハイランダー","§l§63000点","§9９種類のアイテムを揃えることにより成立","§9オールマイティ：§e有効");
        setExample(5,new int[]{10,11,14,20,24,25,80,82,84},"§l§d役：いつもの３人(?)","§l§65000点","§9ゾンビ・クリーパー・スケルトンを一個ずつ揃えることで成立","§9オールマイティ：§e有効");
        setExample(6,new int[]{30,31,34,40,44,48,70,73,76},"§l§d役：おまんじゅう！","§l§64000点","§9桃・白・ライムの羊毛を一個ずつ揃えることで成立","§9オールマイティ：§e有効");
        setExample(7,new int[]{10,14,15,30,31,32,60,61,63},"§l§d役：いざ採掘","§l§63000点","§9ピッケルと鉱石をそれぞれ１セット以上揃えることで成立","§9オールマイティ：§e有効");
        setExample(8,new int[]{20,21,28,50,53,58,63,66,67},"§l§d役：いざ伐採","§l§63000点","§9オノと原木をそれぞれ１セット以上揃えることで成立","§9オールマイティ：§e有効");
        setExample(9,new int[]{34,35,36,66,67,68,86,87,88},"§l§5役：さらなる深みへ","§l§620000点","§9ネザーに現れるブロック・MOBを全て揃えることで成立","§9オールマイティ：§e有効");
        setExample(10,new int[]{0,1,8,40,41,44,80,85,87},"§l§d役：モンスターハント","§l§67000点","§9武器（剣またはオノ）、防具、スポーンエッグをそれぞれ１セット以上揃えることで成立","§9オールマイティ：§e有効");
        setExample(11,new int[]{0,6,7,11,16,18,24,25,26},"§l§d役：道具の準備は万端","§l§65000点","§9ツルハシ、オノ、剣のみでアガることで成立","§9オールマイティ：§e有効");
        setExample(12,new int[]{30,31,35,64,65,66,72,73,74},"§l§d役：立方体","§l§65000点","§9ブロックのみでアガることで成立","§9オールマイティ：§e有効");
        setExample(13,new int[]{0,1,2,20,21,22,30,31,32},"§l§5役：ダイヤまみれ","§l§612000点","§9ダイヤ製のアイテムのみでアガることで成立","§9オールマイティ：§e有効");
        setExample(14,new int[]{0,1,8,14,15,18,56,57,58},"§l§d役：ゴールデン","§l§61つにつき2000点","§9金製のアイテム一つにつき2000点加算","§9オールマイティ：§4無効");
    }

    private void setExample(int yakuNum,int[] tiles,String name,String... lore){
        yakuGUIs.get(yakuNum+1).setItem(4,tileList.getYakuMaterial(yakuNum),name,lore);
        for(int i=0;i<9;i++)yakuGUIs.get(yakuNum+1).setTile(i+9,tiles[i],"§a例");
    }

}
