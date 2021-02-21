package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.Menu;
import lee.code.chunks.menusystem.PlayerMU;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AdminChunkSettings extends Menu {

    public AdminChunkSettings(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public String getMenuName() {
        return Lang.MENU_ADMIN_CHUNK_SETTINGS_TITLE.getString(null);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = pmu.getOwner();

        if (plugin.getData().getPlayerClickDelay(player.getUniqueId())) return;
        else plugin.getPU().addPlayerClickDelay(player.getUniqueId());
        if (e.getClickedInventory() == player.getInventory()) return;

        ItemStack item = e.getCurrentItem();

        if (item != null) {
            switch (e.getSlot()) {
                case 10:
                    updatePermItem(item, 10, player.getLocation().getChunk());
                    break;
                case 11:
                    updatePermItem(item, 11, player.getLocation().getChunk());
                    break;
                case 12:
                    updatePermItem(item, 12, player.getLocation().getChunk());
                    break;
                case 13:
                    updatePermItem(item, 13, player.getLocation().getChunk());
                    break;
                case 14:
                    updatePermItem(item, 14, player.getLocation().getChunk());
                    break;
                case 15:
                    updatePermItem(item, 15, player.getLocation().getChunk());
                    break;
                case 16:
                    updatePermItem(item, 16, player.getLocation().getChunk());
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        setFillerGlass();

        ItemStack allow = new ItemStack(permTrueItem);
        ItemMeta allowMeta = allow.getItemMeta();

        ItemStack deny = new ItemStack(permFalseItem);
        ItemMeta denyMeta = deny.getItemMeta();

        Player player = pmu.getOwner();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.canAdminChunkExplode(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(10, allow);
        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(10, deny);
        }

        if (cache.canAdminChunkBuild(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(11, allow);
        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(11, deny);
        }

        if (cache.canAdminChunkBreak(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(12, allow);
        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(12, deny);
        }

        if (cache.canAdminChunkInteract(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(13, allow);
        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(13, deny);
        }

        if (cache.canAdminChunkSpawnMonsters(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(14, allow);
        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(14, deny);
        }

        if (cache.canAdminChunkPvP(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(15, allow);

        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(15, deny);
        }

        if (cache.canAdminChunkPvE(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(16, allow);

        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(16, deny);
        }
    }

    private void updatePermItem(ItemStack item, int slot, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        ItemStack allow = new ItemStack(permTrueItem);
        ItemStack deny = new ItemStack(permFalseItem);

        ItemMeta allowMeta = allow.getItemMeta();
        ItemMeta denyMeta = deny.getItemMeta();

        //allow
        if (item.getType() != permTrueItem.getType()) {
            switch (slot) {
                case 10:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.TRUE.getString(null)}));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkExplode(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 11:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.TRUE.getString(null)}));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkBuild(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 12:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.TRUE.getString(null)}));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkBreak(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 13:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.TRUE.getString(null)}));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkInteract(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 14:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.TRUE.getString(null)}));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkSpawnMonsters(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 15:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkPvP(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 16:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkPvE(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
            }
            //deny
        } else if (item.getType() != permFalseItem.getType()) {

            switch (slot) {
                case 10:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.FALSE.getString(null)}));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkExplode(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 11:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.FALSE.getString(null)}));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkBuild(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 12:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.FALSE.getString(null)}));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkBreak(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 13:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.FALSE.getString(null)}));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkInteract(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 14:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.FALSE.getString(null)}));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkSpawnMonsters(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 15:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkPvP(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 16:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkPvE(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
            }
        }
    }
}
