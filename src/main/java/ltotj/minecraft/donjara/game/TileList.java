package ltotj.minecraft.donjara.game;

import org.bukkit.Material;

import java.util.HashMap;

public class TileList {//番号とMaterialを対応付けまーす！！
    //0 剣 ダイヤ４ー石４ー金
    //1 ピッケル ダイヤ4ー木４ー金
    //2 オノ　ダイヤ４ー鉄４ー金
    //3　鉱石　ダイヤ４ー水晶４ー金
    //4　防具　ダイヤ４ーチェーン４ー金
    //5　食糧　ポテト３ーりんご３ー金りんご３
    //6　原木　オーク３ー白樺３ーネザーのやつ３
    //7　羊毛　桃3ー白3ー緑3（ライム）
    //8 スポーンエッグ　ゾンビ２ークリーパー２ースケルトン２ーガストーブレイズーウィザスケ

    HashMap<Integer, Material> tileList=new HashMap<>();
    HashMap<Integer,Material> yaku=new HashMap<>();

    public Material getMaterial(int rowAndColumn){
        return tileList.get(rowAndColumn);
    }

    public Material getMaterial(int row,int column){
        return tileList.get(10*row+column);
    }

    public Material getYakuMaterial(int i){return yaku.get(i);}

    public TileList(){
        for(int i=0;i<3;i++){
            tileList.put(i,Material.DIAMOND_SWORD);
            tileList.put(i+4,Material.STONE_SWORD);
            tileList.put(i+10,Material.DIAMOND_PICKAXE);
            tileList.put(i+14,Material.WOODEN_PICKAXE);
            tileList.put(i+20,Material.DIAMOND_AXE);
            tileList.put(i+24,Material.IRON_AXE);
            tileList.put(i+30,Material.DIAMOND_ORE);
            tileList.put(i+34,Material.NETHER_QUARTZ_ORE);
            tileList.put(i+40,Material.DIAMOND_CHESTPLATE);
            tileList.put(i+44,Material.CHAINMAIL_CHESTPLATE);
            tileList.put(i+50,Material.BAKED_POTATO);
            tileList.put(i+53,Material.APPLE);
            tileList.put(i+56,Material.GOLDEN_APPLE);
            tileList.put(i+60,Material.OAK_LOG);
            tileList.put(i+63,Material.BIRCH_LOG);
            tileList.put(i+66,Material.CRIMSON_STEM);
            tileList.put(i+70,Material.PINK_WOOL);
            tileList.put(i+73,Material.WHITE_WOOL);
            tileList.put(i+76,Material.LIME_WOOL);
        }
        tileList.put(3,Material.DIAMOND_SWORD);
        tileList.put(7,Material.STONE_SWORD);
        tileList.put(8,Material.GOLDEN_SWORD);
        tileList.put(13,Material.DIAMOND_PICKAXE);
        tileList.put(17,Material.WOODEN_PICKAXE);
        tileList.put(18,Material.GOLDEN_PICKAXE);
        tileList.put(23,Material.DIAMOND_AXE);
        tileList.put(27,Material.IRON_AXE);
        tileList.put(28,Material.GOLDEN_AXE);
        tileList.put(33,Material.DIAMOND_ORE);
        tileList.put(37,Material.NETHER_QUARTZ_ORE);
        tileList.put(38,Material.GOLD_ORE);
        tileList.put(43,Material.DIAMOND_CHESTPLATE);
        tileList.put(47,Material.CHAINMAIL_CHESTPLATE);
        tileList.put(48,Material.GOLDEN_CHESTPLATE);
        tileList.put(80,Material.ZOMBIE_SPAWN_EGG);
        tileList.put(81,Material.ZOMBIE_SPAWN_EGG);
        tileList.put(82,Material.CREEPER_SPAWN_EGG);
        tileList.put(83,Material.CREEPER_SPAWN_EGG);
        tileList.put(84,Material.SKELETON_SPAWN_EGG);
        tileList.put(85,Material.SKELETON_SPAWN_EGG);
        tileList.put(86,Material.GHAST_SPAWN_EGG);
        tileList.put(87,Material.BLAZE_SPAWN_EGG);
        tileList.put(88,Material.WITHER_SKELETON_SPAWN_EGG);
        tileList.put(100,Material.NETHER_STAR);
        tileList.put(-1,Material.STONE);

        yaku.put(0,Material.CYAN_TERRACOTTA);//三色
        yaku.put(1,Material.RED_TERRACOTTA);//二色
        yaku.put(2,Material.RED_GLAZED_TERRACOTTA);//一色
        yaku.put(3,Material.SHROOMLIGHT);//三種類
        yaku.put(4,Material.LODESTONE);//ハイランダー
        yaku.put(5,Material.ZOMBIE_HEAD);//トリニティ
        yaku.put(6,Material.PINK_WOOL);//饅頭
        yaku.put(7,Material.IRON_PICKAXE);//いざ採掘
        yaku.put(8,Material.STONE_AXE);//いざ伐採
        yaku.put(9,Material.NETHERRACK);//ネザー
        yaku.put(10,Material.DIAMOND_SWORD);//モンスターハント
        yaku.put(11,Material.CRAFTING_TABLE);//ツールオンリー
        yaku.put(12,Material.GRASS_BLOCK);//ブロックオンリー
        yaku.put(13,Material.DIAMOND);//ダイアモンド
        yaku.put(14,Material.GOLD_BLOCK);//ゴールデン

        yaku.put(-1,Material.BARRIER);//エラーアイテム
    }


}
