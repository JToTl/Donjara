package ltotj.minecraft.donjara;

import ltotj.minecraft.donjara.game.TileList;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryGUI {

    Inventory inv;
    public TileList tileList=new TileList();

    public InventoryGUI(int i, String name){
        inv=Bukkit.createInventory(null, i, Component.text(name));
    }

    public void clear(){
        inv.clear();
    }

    public void openInventory(Player player){
        player.openInventory(inv);
    }

    public void setItem(int slot,ItemStack item){
        inv.setItem(slot,item);
    }

    public void setItem(int slot,Material material,String name,String...lore){
        this.setItem(slot,createGuiItem(material,name,lore));
    }

    public void setItem(int slot,int amount,Material material,String name,String...lore){
        this.setItem(slot,createGuiItem(amount,material,name,lore));
    }

    public void setTile(int slot,int row,int column){
        inv.setItem(slot,createTile(row,column));
    }

    public void setTile(int slot,int rowAndColumn){
        setTile(slot,rowAndColumn/10,rowAndColumn-((rowAndColumn/10)*10));
    }

    public void removeItem(int slot){
        inv.setItem(slot,new ItemStack(Material.STONE,0));
    }

    private List<Component> listToComponent(String... lore){
        List<Component> components=new ArrayList<>();
        for (String s : lore) components.add(Component.text(s));
        return components;
    }

    public void cloneItem(int slot,int clonedSlot){
        inv.setItem(slot,inv.getItem(clonedSlot));
    }

    public void enchantItem(int slot){
        ItemStack item=inv.getItem(slot);
        if(item==null)return;
        item.addUnsafeEnchantment(Enchantment.LUCK,1);
        inv.setItem(slot,item);
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.displayName(Component.text(name));

        // Set the lore of the item
        meta.lore(listToComponent(lore));

        item.setItemMeta(meta);

        return item;
    }

    protected ItemStack createGuiItem(int amount,final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.displayName(Component.text(name));

        // Set the lore of the item
        meta.lore(listToComponent(lore));

        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createTile(int row,int column){
        return new ItemStack(tileList.getMaterial(row,column),1);
    }
}
