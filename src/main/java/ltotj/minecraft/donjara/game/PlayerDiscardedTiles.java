package ltotj.minecraft.donjara.game;

import java.util.ArrayList;
import java.util.List;

public class PlayerDiscardedTiles {

    List<Integer> tiles;

    public PlayerDiscardedTiles(){
        reset();
    }

    public void reset(){
        tiles=new ArrayList<>();
    }

    public void addTile(int i){
        tiles.add(i);
    }

    public int tile(int i){
        return tiles.get(i);
    }

    public int size(){
        return tiles.size();
    }

    public List<Integer> getTiles(){
        return tiles;
    }

}
