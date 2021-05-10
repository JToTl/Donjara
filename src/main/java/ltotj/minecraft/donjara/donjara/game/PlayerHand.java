package ltotj.minecraft.donjara.game;


import java.util.ArrayList;
import java.util.List;

public class PlayerHand {
    //0 剣 ダイヤ４ー石４ー金
    //1 ピッケル ダイヤ4ー木４ー金
    //2 オノ　ダイヤ４ー鉄４ー金
    //3　鉱石　ダイヤ４ー水晶４ー金
    //4　防具　ダイヤ４ーチェーン４ー金
    //5　食糧　ポテト３ーりんご３ー金りんご３
    //6　原木　オーク３ー白樺３ーネザーのやつ３
    //7　羊毛　桃3ー白3ー緑3（ライム）
    //8 スポーンエッグ　ゾンビ２ークリーパー２ースケルトン２ーガストーブレイズーウィザスケ

    /*役
    三色
    二色
    一色
    三種類
    ハイランダー
    トリニティ
    饅頭
    いざ採掘
    いざ伐採
    ネザー
    モンスターハント
    ツールオンリー
    ブロックオンリー
    ダイヤまみれ
    ゴールデン
    */

    public int[][] hand=new int[9][9];
    public int almighty,drawnTile=-1;

    public void reset(){
        hand=new int[9][9];
        almighty=0;
    }

    private int isHaving(int i){//0で0を、それ以外で1を返す
        if(i==0)return 0;
        return 1;
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
        } else if(rowAndColumn!=-1){
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
        drawnTile=-1;
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
        } else if(rowAndColumn!=-1){
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
        int judge=0,judgee=0;
        addTile(drawnTile);
        for(int i=0;i<9;i++){
            judge=judge+rowSum(i)%3;
            judgee+=rowSum(i)%3%2;
        }
        if(almighty==0){
            removeTile(drawnTile);
            return judge<=3&&judgee<=1;
        }
        //以下オールマイティを持っている場合
        judgee=1;
        for(int i=0;i<9;i++) if(rowSum(i)!=0)judgee*=rowSum(i);
        removeTile(drawnTile);
        return judge<3||judgee==12;
    }

    public boolean canTsumo(){
        int judge=0;
        addTile(drawnTile);
        if(almighty==0)
            for(int i=0;i<9;i++) {
                judge += rowSum(i) % 3;
            }
        else {
            for (int i = 0; i < 9; i++) {
                judge += isHaving(rowSum(i) % 3);
            }
            judge-=1;
        }
        removeTile(drawnTile);
        return judge==0;
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

    //役判定は手が出来上がっていること前提

    public List<Yaku> yaku(){
        List<Yaku> list=new ArrayList<>();
        addTile(drawnTile);
        switch (colors()){
            case 3:
                list.add(new Yaku("§l§d役：三色", 0, 1000));
                break;
            case 2:
                list.add(new Yaku("§l§d役：二色",1,6000));
                break;
            case 1:
                list.add(new Yaku("§l§5役：一色",2,20000));
                break;
        }
        if(isThreeTypes())list.add(new Yaku("§l§d役：三種類",3,6000));
        if(isHighlander())list.add(new Yaku("§l§d役：ハイランダー",4,3000));
        if(isTrinity())list.add(new Yaku("§l§d役：いつもの３人(?)",5,5000));
        if(isMan10())list.add(new Yaku("§l§d役：おまんじゅう！",6,4000));
        if(isMining())list.add(new Yaku("§l§d役：いざ採掘",7,3000));
        if(isLogging())list.add(new Yaku("§l§d役：いざ伐採",8,3000));
        if(isNether())list.add(new Yaku("§l§5役：さらなる深みへ",9,20000));
        if(isMonsterHunt())list.add(new Yaku("§l§d役：モンスターハント",10,7000));
        if(isOnlyTools())list.add(new Yaku("§l§d役：道具の準備は万端",11,5000));
        if(isOnlyBlocks())list.add(new Yaku("§l§d役：立方体",12,5000));
        if(isDiamond())list.add(new Yaku("§l§5役：ダイヤまみれ",13,12000));
        if(golden()!=0)list.add(new Yaku("§l§d役：ゴールデン§g"+golden(),14,2000*golden()));
        removeTile(drawnTile);
        return list;
    }

    private int colors(){
        int judge=0;
        for(int i=0;i<9;i++)judge+=isHaving(rowSum(i));
        return judge;
    }

    private boolean isThreeTypes() {
        int judge=isHaving(rowPartSum(8,0,2))+isHaving(rowPartSum(8,2,4))+isHaving(rowPartSum(8,4,6))+rowPartSum(8,6,9);
        for(int i=0;i<5;i++)judge+=isHaving(rowPartSum(i,0,4))+isHaving(rowPartSum(i,4,8))+hand[i][8];
        for(int i=5;i<8;i++)judge+=isHaving(rowPartSum(i,0,3))+isHaving(rowPartSum(i,3,6))+isHaving(rowPartSum(i,6,9));
        return judge==3;
    }

    private boolean isHighlander(){
        int judge=almighty+isHaving(rowPartSum(8,0,2))+isHaving(rowPartSum(8,2,4))+isHaving(rowPartSum(8,4,6))+rowPartSum(8,6,9);
        for(int i=0;i<5;i++)judge+=isHaving(rowPartSum(i,0,4))+isHaving(rowPartSum(i,4,8))+hand[i][8];
        for(int i=5;i<8;i++)judge+=isHaving(rowPartSum(i,0,3))+isHaving(rowPartSum(i,3,6))+isHaving(rowPartSum(i,6,9));
        return judge==9;
    }

    private boolean isTrinity(){
        if(rowSum(8)%3==0)return isHaving(rowPartSum(8,0,2))+isHaving(rowPartSum(8,2,4))+isHaving(rowPartSum(8,4,6))==3;
        return isHaving(rowPartSum(8,0,2))+isHaving(rowPartSum(8,2,4))+isHaving(rowPartSum(8,4,6))+almighty>=3;
    }

    private boolean isMan10(){
        if(rowSum(7)%3==0)return isHaving(rowPartSum(7,0,3))+isHaving(rowPartSum(7,3,6))+isHaving(rowPartSum(7,6,9))==3;
        return isHaving(rowPartSum(7,0,3))+isHaving(rowPartSum(7,3,6))+isHaving(rowPartSum(7,6,9))+almighty>=3;
    }

    private boolean isMining(){
        return rowSum(1)*rowSum(3)!=0;
    }

    private boolean isLogging(){
        return rowSum(2)*rowSum(6)!=0;
    }

    private boolean isNether(){
        return rowPartSum(3,6,9)+rowPartSum(6,6,9)+rowPartSum(8,6,9)>=8;
    }

    private boolean isMonsterHunt(){
        return (rowSum(0)+rowSum(2))*rowSum(4)*rowSum(8)!=0;
    }

    private boolean isOnlyTools(){
        return rowSum(0)+rowSum(1)+rowSum(2)>=8;
    }

    private boolean isOnlyBlocks(){
        return rowSum(3)+rowSum(6)+rowSum(7)>=8;
    }

    private boolean isDiamond(){
        return rowPartSum(0,0,3)+rowPartSum(1,0,3)+rowPartSum(2,0,3)+rowPartSum(3,0,3)+rowPartSum(4,0,3)>=8;
    }

    private int golden() {
        int judge=rowPartSum(5,6,9);
        for(int i=0;i<5;i++){
            judge+=hand[i][8];
        }
        return judge;
    }


}
