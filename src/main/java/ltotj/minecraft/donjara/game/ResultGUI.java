package ltotj.minecraft.donjara.game;

import dev.dbassett.skullcreator.SkullCreator;
import javafx.beans.property.Property;
import ltotj.minecraft.donjara.InventoryGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public class ResultGUI {


    TileList tileList=new TileList();
    public InventoryGUI inv=new InventoryGUI(54,"Result");

    public void clear(){
        inv.clear();
    }

    public void setYaku(int slot,int yaku,String name,int point){
        inv.setItem(slot,tileList.yaku.get(yaku),name,"§l§6"+point+"点");
    }

    public void setPoint(int slot,int point){
        for(int i=0;point/Math.pow(10,i)>=1;i++){
            inv.setItem(slot-i,getNumHead((int)(Math.floor(point/Math.pow(10,i))-Math.floor((point/Math.pow(10,i+1)))*10)));
        }
    }

    private ItemStack getNumHead(int number){
        String str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY4ODZkOWM0MGVmN2Y1MGMyMzg4MjQ3OTJjNDFmYmZiNTRmNjY1ZjE1OWJmMWJjYjBiMjdiM2VhZDM3M2IifX19";//エラー怖いから0で初期化
        switch (number){
            case 0:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY4ODZkOWM0MGVmN2Y1MGMyMzg4MjQ3OTJjNDFmYmZiNTRmNjY1ZjE1OWJmMWJjYjBiMjdiM2VhZDM3M2IifX19";
                break;
            case 1:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBhMTllMjNkMjFmMmRiMDYzY2M1NWU5OWFlODc0ZGM4YjIzYmU3NzliZTM0ZTUyZTdjN2I5YTI1In19fQ==";
                break;
            case 2:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M1OTZhNDFkYWVhNTFiZTJlOWZlYzdkZTJkODkwNjhlMmZhNjFjOWQ1N2E4YmRkZTQ0YjU1OTM3YjYwMzcifX19";
                break;
            case 3:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg1ZDRmZGE1NmJmZWI4NTEyNDQ2MGZmNzJiMjUxZGNhOGQxZGViNjU3ODA3MGQ2MTJiMmQzYWRiZjVhOCJ9fX0=";
                break;
            case 4:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg1MmEyNWZlNjljYTg2ZmI5ODJmYjNjYzdhYzk3OTNmNzM1NmI1MGI5MmNiMGUxOTNkNmI0NjMyYTliZDYyOSJ9fX0=";
                break;
            case 5:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlZTdkOTU0ZWIxNGE1Y2NkMzQ2MjY2MjMxYmY5YTY3MTY1MjdiNTliYmNkNzk1NmNlZjA0YTlkOWIifX19";
                break;
            case 6:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjY4MmEzYWU5NDgzNzRlMDM3ZTNkN2RkNjg3ZDU5ZDE4NWRkMmNjOGZjMDlkZmViNDJmOThmOGQyNTllNWMzIn19fQ==";
                break;
            case 7:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGVhMzBjMjRjNjBiM2JjMWFmNjU4ZWY2NjFiNzcxYzQ4ZDViOWM5ZTI4MTg4Y2Y5ZGU5ZjgzMjQyMmU1MTAifX19";
                break;
            case 8:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjZhYmFmZDAyM2YyMzBlNDQ4NWFhZjI2ZTE5MzY4ZjU5ODBkNGYxNGE1OWZjYzZkMTFhNDQ2Njk5NDg5MiJ9fX0=";
                break;
            case 9:
                str="eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQ3OTEwZTEwMzM0Zjg5MGE2MjU0ODNhYzBjODI0YjVlNGExYTRiMTVhOTU2MzI3YTNlM2FlNDU4ZDllYTQifX19";
                break;
        }
        return SkullCreator.itemFromBase64(str);
    }

}
