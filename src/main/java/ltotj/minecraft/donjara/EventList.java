package ltotj.minecraft.donjara;

import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class EventList implements Listener {

    public EventList(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean matchName(ItemStack item, String name) {
        if (item.getItemMeta().displayName() == null) return false;
        return item.getItemMeta().displayName().equals(Component.text(name));
    }

    @EventHandler
    public void ClickGUI(InventoryClickEvent e) {
        if (e.getCurrentItem() != null &&(e.getView().title().equals(Component.text("DonjaraTable"))||e.getView().title().equals(Component.text("DiscardedTiles"))||e.getView().title().equals(Component.text("Result")))) {
            ItemStack clickedItem = e.getCurrentItem();
            e.setCancelled(true);
            if (GlobalClass.currentPlayer.containsKey(e.getWhoClicked().getUniqueId())) {
                Player player = (Player) e.getWhoClicked();
                Donjara donjara = GlobalClass.getTable(player.getUniqueId());
                Donjara.PlayerData playerData = donjara.getPlData(player);
                int slot = e.getSlot();
                if (matchName(clickedItem, "§l§a1ページ目へ")) {
                    player.openInventory(donjara.disTilesGUIS.get(0).inv.inv);
                } else if (matchName(clickedItem, "§l§a2ページ目へ")) {
                    player.openInventory(donjara.disTilesGUIS.get(1).inv.inv);
                } else if (matchName(clickedItem, "§l§4戻る")) {
                    player.openInventory(playerData.playerGUI.inv.inv);
                } else if (playerData.preLi_zhi) {
                    if (clickedItem.containsEnchantment(Enchantment.LUCK)) {
                        playerData.discardedTileNum = slot % 44;
                        donjara.setLi_zhiStick();
                        playerData.li_zhi = true;
                    } else {//リーチのキャンセル処理
                        playerData.playerGUI.setLi_zhiButton();
                        playerData.playerGUI.setDrawnTile(playerData.playerHand.drawnTile);
                        playerData.playerGUI.setTiles(donjara.playerList.get(0).playerHand.hand);
                        playerData.preLi_zhi=false;
                    }
                } else {
                    switch (slot) {
                        case 18:
                            player.openInventory(donjara.disTilesGUIS.get(donjara.seatList.get(player.getUniqueId())).inv.inv);
                            break;
                        case 19:
                        case 28:
                        case 37:
                            donjara.disTilesGUIS.get(donjara.playerList.get((Math.min(1 % 4, donjara.seatList.size()))).disGUINum()).inv.openInventory(player);
                            break;
                        case 11:
                        case 12:
                        case 13:
                            donjara.disTilesGUIS.get(donjara.playerList.get((Math.min(2 % 4, donjara.seatList.size()))).disGUINum()).inv.openInventory(player);
                            break;
                        case 23:
                        case 32:
                        case 41:
                            donjara.disTilesGUIS.get(donjara.playerList.get((Math.min(3 % 4, donjara.seatList.size()))).disGUINum()).inv.openInventory(player);
                            break;
                    }
                    if (donjara.isTurnPl(player.getUniqueId()) && Math.abs(slot - 49) < 5) {
                        playerData.discardedTileNum = slot % 44;
                    }

                    if (matchName(clickedItem, "§l§aロン")) {
                        playerData.playerGUI.removeButton();
                        donjara.ronPSeat.add(donjara.seatList.get(player.getUniqueId()));
                        donjara.canRonPAc += 1;
                    } else if (matchName(clickedItem, "§l§bリーチ")) {
                        playerData.playerGUI.setCancelButton();
                        playerData.setPreLi_zhi();
                        playerData.preLi_zhi = true;
                    } else if (matchName(clickedItem, "§l§cツモ")) {
                        playerData.playerGUI.removeButton();
                        playerData.discardedTileNum = 101;
                        playerData.li_zhi = false;
                    } else if (matchName(clickedItem, "§l§eスキップ")) {
                        playerData.playerGUI.removeButton();
                        donjara.canRon += 1;
                    }
                }

            }
        }
    }
}

