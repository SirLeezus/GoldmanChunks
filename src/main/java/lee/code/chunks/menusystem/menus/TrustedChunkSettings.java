package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.chunksettings.ChunkTrustedSetting;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.Menu;
import lee.code.chunks.menusystem.PlayerMU;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrustedChunkSettings extends Menu {

    public TrustedChunkSettings(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_TRUSTED_TITLE.getComponent(null);
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = pmu.getOwner();
        ItemStack clickedItem = e.getCurrentItem();

        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem == null) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        switch (e.getSlot()) {
            case 10 -> updatePermItem(player, clickedItem, 10, player.getLocation().getChunk());
            case 12 -> updatePermItem(player, clickedItem, 12, player.getLocation().getChunk());
            case 14 -> updatePermItem(player, clickedItem, 14, player.getLocation().getChunk());
            case 16 -> updatePermItem(player, clickedItem, 16, player.getLocation().getChunk());
            case 31 -> {
                new ChunkManager(pmu).open();
                playClickSound(player);
            }
        }
    }

    @Override
    public void setMenuItems() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        setFillerGlass();

        ItemStack allow = new ItemStack(permTrueItem);
        ItemMeta allowMeta = allow.getItemMeta();

        ItemStack deny = new ItemStack(permFalseItem);
        ItemMeta denyMeta = deny.getItemMeta();

        Player player = pmu.getOwner();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        //build
        if (cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BUILD, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(10, allow);

        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(10, deny);
        }

        //break
        if (cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BREAK, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(12, allow);

        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(12, deny);
        }

        //interact
        if (cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(14, allow);

        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(14, deny);
        }

        //pve
        if (cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.PVE, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(16, allow);

        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(16, deny);
        }

        //back
        inventory.setItem(31, backItem);
    }

    private void updatePermItem(Player player, ItemStack item, int slot, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        ItemStack allow = new ItemStack(permTrueItem);
        ItemStack deny = new ItemStack(permFalseItem);

        ItemMeta allowMeta = allow.getItemMeta();
        ItemMeta denyMeta = deny.getItemMeta();

        //allow
        if (item.getType() != permTrueItem.getType()) {

            switch (slot) {
                case 10 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.BUILD, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 12 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.BREAK, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 14 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 16 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.PVE, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
            }
            //deny
        } else if (item.getType() != permFalseItem.getType()) {

            switch (slot) {
                case 10 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.BUILD, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 12 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.BREAK, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 14 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 16 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setChunkTrustedSetting(ChunkTrustedSetting.PVE, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
            }
        }
    }
}
