package ltotj.minecraft.donjara.game;

import ltotj.minecraft.donjara.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DisTilesGUI {
    public InventoryGUI inv=new InventoryGUI(54,"DiscardedTiles");

    public DisTilesGUI(int i){//1か2
        inv.setItem(8, Material.LIME_STAINED_GLASS_PANE,"§l§a"+(i%2+1)+"ページ目へ");
        inv.setItem(4,Material.RED_STAINED_GLASS_PANE,"§l§4戻る");
        for(int j=0;j<5;j++){
            inv.setItem(9*j+13,new ItemStack(Material.WHITE_STAINED_GLASS_PANE,1));
        }
    }

    private int headPosition(int i) {//iは席の番号
        switch (i%2) {
            case 0:
                return 0;
            case 1:
                return 5;
        }
        return 2;
    }

    public void setPlayerHead(int i, ItemStack head){
        inv.setItem(headPosition(i),head);
    }//なんでわざわざメソッドにしたんだ

    public void setTiles(int i,List<Integer> tiles){
        inv.setTile(headPosition(i)+9+((tiles.size()-1)/4)*9+(tiles.size()-1)%4,tiles.get((tiles.size()-1)));
    }

    public void resetTiles(){
        for(int i=0;i<20;i++){
            inv.removeItem(9+(i/4)*9+i%4);
            inv.removeItem(14+(i/4)*9+i%4);
        }
    }



}
