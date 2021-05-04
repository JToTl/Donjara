package ltotj.minecraft.donjara.game;

import org.bukkit.Material;

import java.util.HashMap;

public class TileList {//番号とMaterialを対応付けまーす！！
    //0-ピッケル 石-鉄-ダイヤ
    //1-オノ 木-石-ダイヤ
    //2-剣 木-鉄-ダイヤ
    //3-防具 皮-チェイン-ダイヤ
    //4-鉱石 エメラルド-赤石-ネザークオーツ
    //5-木ブロック オーク-白樺-ネザーのやつ
    //6-食糧 生豚-生牛-金りんご
    //7-かまど系 溶鉱炉-かまど-薫製
    //8-スポーンエッグ ゾンビ-クリーパー-スケルトン-ネザー３種

    HashMap<Integer, Material> tileList=new HashMap<>();
    HashMap<Integer,Material> yaku=new HashMap<>();

    public Material getMaterial(int rowAndColumn){
        return tileList.get(rowAndColumn);
    }

    public Material getMaterial(int row,int column){
        return tileList.get(10*row+column);
    }

    public TileList(){
        for(int i=0;i<3;i++){
            tileList.put(i,Material.STONE_PICKAXE);
            tileList.put(i+3,Material.IRON_PICKAXE);
            tileList.put(i+6,Material.DIAMOND_PICKAXE);
            tileList.put(i+10,Material.WOODEN_AXE);
            tileList.put(i+13,Material.STONE_AXE);
            tileList.put(i+16,Material.DIAMOND_AXE);
            tileList.put(i+20,Material.WOODEN_SWORD);
            tileList.put(i+23,Material.IRON_SWORD);
            tileList.put(i+26,Material.DIAMOND_SWORD);
            tileList.put(i+30,Material.LEATHER_CHESTPLATE);
            tileList.put(i+33,Material.CHAINMAIL_CHESTPLATE);
            tileList.put(i+36,Material.DIAMOND_CHESTPLATE);
            tileList.put(i+40,Material.EMERALD_ORE);
            tileList.put(i+43,Material.REDSTONE_ORE);
            tileList.put(i+46,Material.NETHER_QUARTZ_ORE);
            tileList.put(i+50,Material.OAK_LOG);
            tileList.put(i+53,Material.BIRCH_LOG);
            tileList.put(i+56,Material.CRIMSON_STEM);
            tileList.put(i+60,Material.PORKCHOP);
            tileList.put(i+63,Material.BEEF);
            tileList.put(i+66,Material.GOLDEN_APPLE);
            tileList.put(i+70,Material.BLAST_FURNACE);
            tileList.put(i+73,Material.FURNACE);
            tileList.put(i+76,Material.SMOKER);
        }
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

        yaku.put(0,Material.ZOMBIE_HEAD);//トリニティ
        yaku.put(1,Material.DIAMOND);//ダイヤ
        yaku.put(2,Material.NETHERRACK);//ネザー
        yaku.put(3,Material.IRON_PICKAXE);//採掘
        yaku.put(4,Material.GOLDEN_AXE);//伐採
        yaku.put(5,Material.DIAMOND_SWORD);//モンスターハンター
        yaku.put(6,Material.FURNACE);//精錬
        yaku.put(7,Material.STICK);//ツール
        yaku.put(8,Material.WHITE_WOOL);//三色
        yaku.put(9,Material.LIME_WOOL);//二色
        yaku.put(10,Material.PINK_WOOL);//一色
    }


}
