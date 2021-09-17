package ltotj.minecraft.donjara.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {

    Random random=new Random();
    List<Integer> deck;

    public Deck(){
        reset();
    }

    public void debugReset(){
        deck=new ArrayList<>();
    }

    public void reset(){
        deck=new ArrayList<>();
        for(int i=0;i<9;i++){//こっちがrow
            for(int j=0;j<9;j++)deck.add(10*i+j);
        }
        deck.add(100);
    }

    public int size(){
        return deck.size();
    }

    public int draw(){
        int r=random.nextInt(deck.size()),tile;
        tile=deck.get(r);
        deck.remove(r);
        return tile;
    }

}
