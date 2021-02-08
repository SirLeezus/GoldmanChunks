package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.Menu;
import lee.code.chunks.menusystem.PlayerMenuUtility;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TrustedGlobalSettings extends Menu {

    public TrustedGlobalSettings(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return Lang.MENU_GLOBAL_TRUSTED_TITLE.getString(null);
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = playerMenuUtility.getOwner();

        //click delay
        if (plugin.getData().getPlayerClickDelay(player.getUniqueId())) return;
        else plugin.getUtility().addPlayerClickDelay(player.getUniqueId());

        if (e.getClickedInventory() == player.getInventory()) return;

        switch (e.getSlot()) {
            case 10:
                updatePermItem(e.getCurrentItem(), 10, player.getLocation().getChunk());
                break;
            case 12:
                updatePermItem(e.getCurrentItem(), 12, player.getLocation().getChunk());
                break;
            case 14:
                updatePermItem(e.getCurrentItem(), 14, player.getLocation().getChunk());
                break;
            case 16:
                updatePermItem(e.getCurrentItem(), 16, player.getLocation().getChunk());
                break;
            case 31:
                new ChunkManager(playerMenuUtility).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        setFillerGlass();

        ItemStack allow = new ItemStack(permTrueItem);
        ItemMeta allowMeta = allow.getItemMeta();

        ItemStack deny = new ItemStack(permFalseItem);
        ItemMeta denyMeta = deny.getItemMeta();

        Player player = playerMenuUtility.getOwner();
        UUID uuid = player.getUniqueId();

        //build
        if (plugin.getSqLite().canGlobalTrustedBuild(uuid)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(10, allow);

        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(10, deny);
        }

        //break
        if (plugin.getSqLite().canGlobalTrustedBreak(uuid)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(12, allow);

        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(12, deny);
        }

        //interact
        if (plugin.getSqLite().canGlobalTrustedInteract(uuid)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(14, allow);

        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(14, deny);
        }

        //pve
        if (plugin.getSqLite().canGlobalTrustedPVE(uuid)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(16, allow);

        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(16, deny);
        }

        //back
        inventory.setItem(31, backItem);
    }

    private void updatePermItem(ItemStack item, int slot, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = playerMenuUtility.getOwner();
        UUID uuid = player.getUniqueId();
        ItemStack allow = new ItemStack(permTrueItem);
        ItemStack deny = new ItemStack(permFalseItem);
        ItemMeta allowMeta = allow.getItemMeta();
        ItemMeta denyMeta = deny.getItemMeta();

        //allow
        if (item.getType() != permTrueItem.getType()) {

            switch (slot) {
                case 10:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
                    allow.setItemMeta(allowMeta);
                    plugin.getSqLite().setGlobalTrustedBuild(uuid, 1);
                    inventory.setItem(slot, allow);
                    break;
                case 12:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
                    allow.setItemMeta(allowMeta);
                    plugin.getSqLite().setGlobalTrustedBreak(uuid, 1);
                    inventory.setItem(slot, allow);
                    break;
                case 14:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
                    allow.setItemMeta(allowMeta);
                    plugin.getSqLite().setGlobalTrustedInteract(uuid, 1);
                    inventory.setItem(slot, allow);
                    break;
                case 16:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { plugin.getUtility().format("&atrue") }));
                    allow.setItemMeta(allowMeta);
                    plugin.getSqLite().setGlobalTrustedPVE(uuid, 1);
                    inventory.setItem(slot, allow);
                    break;


            }
            //deny
        } else if (item.getType() != permFalseItem.getType()) {

            switch (slot) {
                case 10:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
                    deny.setItemMeta(denyMeta);
                    plugin.getSqLite().setGlobalTrustedBuild(uuid, 0);
                    inventory.setItem(slot, deny);
                    break;
                case 12:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
                    deny.setItemMeta(denyMeta);
                    plugin.getSqLite().setGlobalTrustedBreak(uuid, 0);
                    inventory.setItem(slot, deny);
                    break;
                case 14:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
                    deny.setItemMeta(denyMeta);
                    plugin.getSqLite().setGlobalTrustedInteract(uuid, 0);
                    inventory.setItem(slot, deny);
                    break;
                case 16:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { plugin.getUtility().format("&cfalse") }));
                    deny.setItemMeta(denyMeta);
                    plugin.getSqLite().setGlobalTrustedPVE(uuid, 0);
                    inventory.setItem(slot, deny);
                    break;
            }
        }
    }
}
