package ltotj.minecraft.donjara;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class EventList implements Listener {

    public EventList(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean matchName(ItemStack item, String name) {
        return item.getItemMeta()!=null&&item.getItemMeta().displayName() != null&&item.getItemMeta().displayName().equals(Component.text(name));
    }

    @EventHandler
    public void ClickGUI(InventoryClickEvent e) {
        Player player=(Player)e.getWhoClicked();
        if ((e.getView().title().equals(Component.text("DonjaraTable"))||e.getView().title().equals(Component.text("DiscardedTiles"))||e.getView().title().equals(Component.text("Result")))) {
            e.setCancelled(true);
            if(e.getCurrentItem() == null)return;
            ItemStack clickedItem = e.getCurrentItem();
            if (GlobalClass.currentPlayer.containsKey(e.getWhoClicked().getUniqueId())) {
                Donjara donjara = GlobalClass.getTable(player.getUniqueId());
                Donjara.PlayerData playerData = donjara.getPlData(player);
                int slot = e.getSlot();
                if (matchName(clickedItem, "§l§a1ページ目へ")) {
                    player.openInventory(donjara.disTilesGUIS.get(0).inv.inv);
                } else if (matchName(clickedItem, "§l§a2ページ目へ")) {
                    player.openInventory(donjara.disTilesGUIS.get(1).inv.inv);
                } else if (matchName(clickedItem, "§l§4戻る")) {
                    player.openInventory(playerData.playerGUI.inv.inv);
                } else if (playerData.preLi_zhi&&!playerData.li_zhi) {
                    if (clickedItem.containsEnchantment(Enchantment.LUCK)) {
                        playerData.li_zhi = true;
                        playerData.discardedTileNum = slot % 44;
                        donjara.setLi_zhiStick();
                    } else {//リーチのキャンセル処理
                        playerData.playerGUI.setLi_zhiButton();
                        playerData.playerGUI.setTiles(donjara.playerList.get(donjara.turnSeat).playerHand.hand);
                        playerData.playerGUI.setDrawnTile(playerData.playerHand.drawnTile);
                        playerData.preLi_zhi=false;
                    }
                } else {
                    if(e.getView().title().equals(Component.text("DonjaraTable"))){
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
                    }
                    if (donjara.isTurnPl(player.getUniqueId()) && Math.abs(slot - 49) < 5&&playerData.discardedTileNum!=101) {
                        playerData.discardedTileNum = slot % 44;
                    }

                    if (matchName(clickedItem, "§l§aドンジャラ！（ロン）")) {
                        playerData.playerGUI.removeButton();
                        donjara.ronPSeat.add(donjara.seatList.get(player.getUniqueId()));
                        donjara.canRonPAc += 1;
                    } else if (matchName(clickedItem, "§l§bリーチ")) {
                        playerData.playerGUI.setCancelButton();
                        playerData.setPreLi_zhi();
                        playerData.preLi_zhi = true;
                    } else if (matchName(clickedItem, "§l§cドンジャラ！（ツモ）")) {
                        playerData.playerGUI.removeButton();
                        playerData.discardedTileNum = 101;
                        playerData.li_zhi = false;
                    } else if (matchName(clickedItem, "§l§eスキップ")) {
                        playerData.playerGUI.removeButton();
                        donjara.canRonPAc += 1;
                    }
                    else if(matchName(clickedItem,"§l§a牌・役を確認する"))player.openInventory(GlobalClass.gameRuleGUI.ruleGUI.inv);
                }
            }
        }
        else if(e.getView().title().equals(Component.text("役"))){
            e.setCancelled(true);
            if(e.getCurrentItem() == null)return;
            ItemStack clickedItem = e.getCurrentItem();
            if(matchName(clickedItem,"§c役一覧に戻る"))player.openInventory(GlobalClass.gameRuleGUI.yakuGUIs.get(0).inv);
        }
        else if(e.getView().title().equals(Component.text("役一覧"))){
            e.setCancelled(true);
            if(e.getSlot()>=9&&e.getSlot()<=23) {
                player.openInventory(GlobalClass.gameRuleGUI.yakuGUIs.get(e.getSlot() - 8).inv);
            }
        }
        else if(e.getView().title().equals(Component.text("牌一覧"))){
            e.setCancelled(true);
            if(e.getCurrentItem() == null)return;
            ItemStack clickedItem = e.getCurrentItem();
            if(matchName(clickedItem,"§a２ページ目へ"))player.openInventory(GlobalClass.gameRuleGUI.tileGUIs.get(1).inv);
            else if(matchName(clickedItem,"§a１ページ目へ"))player.openInventory(GlobalClass.gameRuleGUI.tileGUIs.get(0).inv);
        }
        else if(e.getView().title().equals(Component.text("牌・役確認"))){
            e.setCancelled(true);
            if(e.getCurrentItem() == null)return;
            ItemStack clickedItem = e.getCurrentItem();
            if(matchName(clickedItem,"§a牌を確認する"))player.openInventory(GlobalClass.gameRuleGUI.tileGUIs.get(0).inv);
            else if(matchName(clickedItem,"§a役を確認する"))player.openInventory(GlobalClass.gameRuleGUI.yakuGUIs.get(0).inv);
            else if(matchName(clickedItem,"§c参加中のゲームに戻る")&&GlobalClass.currentPlayer.containsKey(player.getUniqueId()))player.openInventory(GlobalClass.getTable(player.getUniqueId()).getPlData(player).playerGUI.inv.inv);
        }
        if(e.getCurrentItem()!= null&&matchName(e.getCurrentItem(),"§c前の画面に戻る")){
            e.setCancelled(true);
            player.openInventory(GlobalClass.gameRuleGUI.ruleGUI.inv);
        }
    }
}

