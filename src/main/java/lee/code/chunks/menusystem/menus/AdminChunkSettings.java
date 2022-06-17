package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.chunksettings.AdminChunkSetting;
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

public class AdminChunkSettings extends Menu {

    public AdminChunkSettings(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() { return Lang.MENU_ADMIN_CHUNK_SETTINGS_TITLE.getComponent(null); }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = pmu.getOwner();
        ItemStack clickedItem = e.getCurrentItem();

        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem == null) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        int slot = e.getSlot();
        updatePermItem(player, clickedItem, slot, player.getLocation().getChunk());
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

        if (cacheManager.canAdminChunkSetting(AdminChunkSetting.EXPLOSIONS, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(10, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(10, deny);
        }

        if (cacheManager.canAdminChunkSetting(AdminChunkSetting.BUILD, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(11, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(11, deny);
        }

        if (cacheManager.canAdminChunkSetting(AdminChunkSetting.BREAK, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(12, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(12, deny);
        }

        if (cacheManager.canAdminChunkSetting(AdminChunkSetting.INTERACT, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(13, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(13, deny);
        }

        if (cacheManager.canAdminChunkSetting(AdminChunkSetting.MONSTERS, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(14, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(14, deny);
        }

        if (cacheManager.canAdminChunkSetting(AdminChunkSetting.PVE, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(16, allow);

        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(16, deny);
        }
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
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.EXPLOSIONS, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 11 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.BUILD, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 12 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.BREAK, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 13 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.INTERACT, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 14 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.MONSTERS, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 16 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.PVE, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
            }
            //deny
        } else if (item.getType() != permFalseItem.getType()) {

            switch (slot) {
                case 10 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.EXPLOSIONS, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 11 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.BUILD, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 12 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.BREAK, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 13 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.INTERACT, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 14 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.MONSTERS, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 16 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cacheManager.setAdminChunkSetting(AdminChunkSetting.PVE, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
            }
        }
    }
}
