package ltotj.minecraft.donjara.game;


import java.util.ArrayList;
import java.util.List;

public class PlayerHand {
    //0-ピッケル 石-鉄-ダイヤ
    //1-オノ 木-石-ダイヤ
    //2-剣 木-鉄-ダイヤ
    //3-防具 皮-チェイン-ダイヤ
    //4-鉱石 エメラルド-赤石-ネザー金
    //5-木ブロック オーク-白樺-ネザーのやつ
    //6-食糧 生豚-生牛-金りんご
    //7-かまど系 溶鉱炉-かまど-薫製
    //8-スポーンエッグ ゾンビ-クリーパー-スケルトン-ネザー３種

    //役
    //かまどで焼こう
    //ダイヤ
    //ツールのみ
    //モンスターハント
    //ネザー
    //いつもの三人
    //いざ採掘
    //いざ伐採
    //３色
    //２色
    //１色

    public int[][] hand=new int[9][9];
    public int almighty,drawnTile=0;

    public void reset(){
        hand=new int[9][9];
        almighty=0;
    }

    public void addTile(int row,int column){
        hand[row][column]=1;
    }

    public void drawTile(int rowAndColumn) {
        drawnTile=rowAndColumn;
//        if (rowAndColumn == 100) {
//            almighty = 1;
//        } else {
//            hand[rowAndColumn / 10][rowAndColumn - ((rowAndColumn / 10) * 10)] = 1;
//        }
    }

    public void addTile(int rowAndColumn) {
        if (rowAndColumn == 100) {
            almighty = 1;
        } else {
            hand[rowAndColumn / 10][rowAndColumn - ((rowAndColumn / 10) * 10)] = 1;
        }
    }

    public void drawnToHand() {
        if(drawnTile<=0)return;
        if (drawnTile == 100) {
            almighty = 1;
        } else {
            hand[drawnTile / 10][drawnTile - ((drawnTile / 10) * 10)] = 1;
        }
        drawnTile=0;
    }

    public List<Integer> getWinningTiles(){//リーチ前提
        List<Integer> list=new ArrayList<>();
        for(int i=0;i<9;i++){
            if(rowSum(i)%3!=0)list.add(i);
        }
        return list;
    }

    public int getTile(int number){//1から
        int r=0;
        for(int i=0;i<9;i++) {
            for(int j=0;j<9;j++){
              r+=hand[i][j];
              if(r==number)return 10*i+j;
            }
        }
        return 100;
    }

    public void removeTile_num(int number){
        removeTile(getTile(number));
    }

    public void removeTile(int row,int column){
        hand[row][column]=0;
    }

    public void removeTile(int rowAndColumn) {
        if (rowAndColumn == 100) {
            almighty = 0;
        } else {
            hand[rowAndColumn / 10][rowAndColumn - ((rowAndColumn / 10) * 10)] = 0;
        }
    }

    private int rowSum(int row) {
        int re = 0;
        for (int i = 0; i < 9; i++) re = re + hand[row][i];
        return re;
    }

    private int rowPartSum(int row,int start,int end){
        int re=0;
        for(int i=start;i<end;i++)re=re+hand[row][i];
        return re;
    }

    public boolean canLi_zhi(){
        int judge=0;
        addTile(drawnTile);
        for(int i=0;i<9;i++)judge=judge+rowSum(i)%3;
        if(judge>5){
            removeTile(drawnTile);
            return false;
        }
        judge=0;
        for(int i=0;i<9;i++)judge=judge+(rowSum(i)%3)%2;
        removeTile(drawnTile);
        return judge != 3;
    }

    public boolean canTsumo(){
        int judge=0;
        addTile(drawnTile);
        for(int i=0;i<9;i++){
            judge+=rowSum(i)%3;
        }
        removeTile(drawnTile);
        return judge==0||(judge==2&&almighty==1);
    }

    public List<Integer> li_zhi(){//捨てることができる列を返す canLi_zhiを通っていること前提
        int judge=0;
        List<Integer> ret=new ArrayList<>();
        addTile(drawnTile);
        for(int i=0;i<9;i++){
            judge=judge+rowSum(i)%3%2;
            if(rowSum(i)%3%2!=0)ret.add(i);
        }
        if(judge==0){
            for(int i=0;i<9;i++)ret.add(i);
            ret.add(100);
        }
        removeTile(drawnTile);
        return ret;
    }

    public List<Integer> winningTile() {//聴牌前提 アガれる列を返す
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (rowSum(i) % 3 != 0) {
                list.add(i);
            }
        }
        return list;
    }

    //役判定は手が出来上がっていること前提

    public int point(){
        int r=0;
        if(isOnlyTools())r=r+3000;
        if(isMonsterHunt())r=r+5000;
        if(isNether())r=r+24000;
        if(isDiamond())r=r+10000;
        if(isTrinity())r=r+5000;
        if(isMining())r=r+3000;
        if(isLogging())r=r+3000;
        if(isSmelting())r=r+2000;
        switch (colors()){
            case 3:
                r=r+20000;
                break;
            case 2:
                r=r+6000;
                break;
            case 1:
                r=r+1000;
                break;
        }
        return r;
    }

    public List<Yaku> yaku(){
        List<Yaku> list=new ArrayList<>();
        if(isOnlyTools())list.add(new Yaku("§l§8ツールだらけ",7,300));
        if(isMonsterHunt())list.add(new Yaku("§l§8モンスターハント",5,5000));
        if(isNether())list.add(new Yaku("§l§8ネザー！",2,24000));
        if(isDiamond())list.add(new Yaku("§l§8ダイヤラッシュ",1,10000));
        if(isTrinity())list.add(new Yaku("§l§8おなじみの３人（？）",0,5000));
        if(isMining())list.add(new Yaku("§l§8いざ採掘！",4,3000));
        if(isLogging())list.add(new Yaku("§l§8いざ伐採！",3,3000));
        if(isSmelting())list.add(new Yaku("§l§8かまどで焼きましょう",6,2000));
        switch (colors()){
            case 3:
                list.add(new Yaku("§l§8三色", 8, 1000));
                break;
            case 2:
                list.add(new Yaku("§l§8二色",9,6000));
                break;
            case 1:
                list.add(new Yaku("§l§8一色",10,20000));
                break;
        }
        return list;
    }

    private int colors(){
        int judge=0;
        for(int i=0;i<9;i++)if(rowSum(i)!=0)judge=judge+1;
        return judge;
    }

    private boolean isOnlyTools(){//剣も含む
        return rowSum(0) + rowSum(1) + rowSum(2) >= 8;
    }

    private boolean isMonsterHunt(){
        return rowSum(2)*rowSum(3)*rowSum(8)>=18;
    }

    private boolean isNether(){
        return rowPartSum(4,6,9)+rowPartSum(5,6,9)+rowPartSum(8,6,9)>=8;
    }

    private boolean isDiamond(){
        return rowPartSum(0,6,9)+rowPartSum(1,6,9)+rowPartSum(2,6,9)+rowPartSum(3,6,9)>=8;
    }

    private boolean isTrinity(){
        return Math.min(rowPartSum(8,0,2),1)+Math.min(rowPartSum(8,2,4),1)+Math.min(rowPartSum(8,4,6),1)+almighty>=3;
    }

    private boolean isMining(){
        return rowSum(0)+rowSum(4)>=5;
    }

    private boolean isLogging(){
        return rowSum(1)+rowSum(5)>=5;
    }

    private boolean isSmelting(){
        return (rowPartSum(1,0,3)+rowPartSum(2,0,3)+rowSum(5))//燃料
                *(rowSum(4)*rowPartSum(7,0,6)+rowSum(6)*rowPartSum(7,6,9)+(rowPartSum(0,3,6)+rowPartSum(2,3,6))*rowPartSum(7,0,3)+Math.max(0,rowSum(5)+(rowPartSum(1,0,3)+rowPartSum(2,0,3))%5-3)*rowPartSum(7,3,6))>0;
    }



}
