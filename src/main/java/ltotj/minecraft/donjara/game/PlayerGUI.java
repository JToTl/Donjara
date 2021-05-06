package ltotj.minecraft.donjara.game;

import ltotj.minecraft.donjara.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class PlayerGUI {

    public InventoryGUI inv= new InventoryGUI(54,"DonjaraTable");

    public void setTiles(int[][] hand) {
        int count = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (hand[i][j] == 1) {
                    inv.setTile(45 + count, i,j);
                    count=count+1;
                }
            }
        }
        if(count==7)inv.setItem(52,inv.tileList.getMaterial(100),"§l§fオールマイティー");
        inv.removeItem(53);
    }

    public void reset(){
        inv.removeItem(27);
        inv.removeItem(36);
        inv.removeItem(19);
        inv.removeItem(28);
        inv.removeItem(37);
        inv.removeItem(2);
        inv.removeItem(3);
        inv.removeItem(11);
        inv.removeItem(12);
        inv.removeItem(13);
        inv.removeItem(33);
        inv.removeItem(24);
        inv.removeItem(41);
        inv.removeItem(32);
        inv.removeItem(23);
        inv.removeItem(14);
        inv.removeItem(21);
        inv.removeItem(29);
        inv.removeItem(31);
        inv.removeItem(39);
        for(int i=0;i<9;i++){
            inv.removeItem(45+i);
        }
        removeButton();
    }

    public void setGlassPane(){
        inv.setItem(8,new ItemStack(Material.WHITE_STAINED_GLASS_PANE,1));
        inv.setItem(44,new ItemStack(Material.WHITE_STAINED_GLASS_PANE,1));
        for(int i=0;i<5;i++){
            inv.setItem(9*i+7,new ItemStack(Material.WHITE_STAINED_GLASS_PANE,1));
        }
    }

    public void setDrawnTile(int rowAndColumn){
        inv.setTile(53,rowAndColumn/10,rowAndColumn-(rowAndColumn/10)*10);
    }

    public void setRonButton(){
        inv.setItem(35,Material.END_CRYSTAL,"§l§aロン");
        inv.setItem(26,Material.RED_STAINED_GLASS_PANE,"§l§eスキップ");
    }

    public void setLi_zhiButton(){
        inv.setItem(26,Material.END_CRYSTAL,"§l§bリーチ");
    }

    public void setTsumoButton(){inv.setItem(35,Material.END_CRYSTAL,"§l§cツモ");}

    public void setCancelButton(){inv.setItem(26,Material.RED_STAINED_GLASS_PANE,"§l§dキャンセル");}


    public void removeButton() {
        inv.removeItem(35);
        inv.removeItem(26);
    }

    public void preLi_zhi(PlayerHand playerHand){//handは9*9 canDisうんぬんは9
        int count=0;
        List<Integer> canDisTiles=playerHand.li_zhi();
        System.out.println(canDisTiles);
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                if(playerHand.hand[i][j]==1){
                    count+=1;
                    if(canDisTiles.contains(i)){
                        inv.enchantItem(44+count);
                    }
                }
            }
        }
        if(count!=8&&canDisTiles.contains(100))inv.enchantItem(52);
        if(canDisTiles.contains(playerHand.drawnTile/10))inv.enchantItem(53);
    }

    private int headPosition(int i){//0-左 1-正面 2-右 どの向きなのかはゲームの方のクラスで（3*自分＋相手＋３）%4で出せる
        int r=0;
        switch (i){
            case 0:
                r=18;
                break;
            case 1:
                r=4;
                break;
            case 2:
                r=42;
                break;
        }
        return r;
    }

    public void putOthersPane_Lime(int direction){
        switch(direction){
            case 0:
                inv.setItem(27,Material.LIME_STAINED_GLASS_PANE,"§a待機中");
                inv.setItem(36,Material.LIME_STAINED_GLASS_PANE,"§a待機中");
                break;
            case 1:
                inv.setItem(2,Material.LIME_STAINED_GLASS_PANE,"§a待機中");
                inv.setItem(3,Material.LIME_STAINED_GLASS_PANE,"§a待機中");
                break;
            case 2:
                inv.setItem(33,Material.LIME_STAINED_GLASS_PANE,"§a待機中");
                inv.setItem(24,Material.LIME_STAINED_GLASS_PANE,"§a待機中");
                break;
        }
    }

    public void putOthersPane_Red(int direction){
        switch(direction){
            case 0:
                inv.setItem(27,Material.RED_STAINED_GLASS_PANE,"§c手番");
                inv.setItem(36,Material.RED_STAINED_GLASS_PANE,"§c手番");
                break;
            case 1:
                inv.setItem(2,Material.RED_STAINED_GLASS_PANE,"§c手番");
                inv.setItem(3,Material.RED_STAINED_GLASS_PANE,"§c手番");
                break;
            case 2:
                inv.setItem(33,Material.RED_STAINED_GLASS_PANE,"§c手番");
                inv.setItem(24,Material.RED_STAINED_GLASS_PANE,"§c手番");
                break;
        }
    }

    public void setOthersDisTile(int rowAndColumn,int direction){
        switch (direction){
            case 0:
                inv.cloneItem(37,28);
                inv.cloneItem(28,19);
                inv.setTile(19,rowAndColumn);
                break;
            case 1:
                inv.cloneItem(11,12);
                inv.cloneItem(12,13);
                inv.setTile(13,rowAndColumn);
                break;
            case 2:
                inv.cloneItem(23,32);
                inv.cloneItem(32,41);
                inv.setTile(41,rowAndColumn);
        }
    }

    public void setOthersHead(ItemStack head,int direction){//0-左 1-正面 2-右 どの向きなのかはゲームの方のクラスで(相手の場所-自分の場所-1)%4で出せる
        inv.setItem(headPosition(direction),head);
    }

    public void setOthersLi_zhi(int direction) {
        switch (direction) {
            case 0:
                inv.setItem(29,Material.BLAZE_ROD,"§l§6リーチ");
                break;
            case 1:
                inv.setItem(21,Material.BLAZE_ROD,"§l§6リーチ");
                break;
            case 2:
                inv.setItem(31,Material.BLAZE_ROD,"§l§6リーチ");
                break;
        }
    }

    public void setLi_zhiStick(){
        inv.setItem(39,Material.BLAZE_ROD,"§l§6リーチ");
    }

    public void setRestTiles(int rest){
        if(rest<=0){
            inv.removeItem(30);
            return;
        }
        inv.setItem(30,rest,Material.QUARTZ_BLOCK,"§l山","§l§6§l残り"+rest+"枚");
    }

    public void setClock(int time){//秒
        inv.setItem(6,time,Material.CLOCK,"§6残り秒数");
    }

}
