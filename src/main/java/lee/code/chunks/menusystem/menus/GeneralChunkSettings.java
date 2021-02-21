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

public class GeneralChunkSettings extends Menu {

    public GeneralChunkSettings(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public String getMenuName() {
        return Lang.MENU_GENERAL_CHUNK_SETTINGS_TITLE.getString(null);
    }

    @Override
    public int getSlots() {
        return 36;
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
                case 11:
                    updatePermItem(item, 11, player.getLocation().getChunk());
                    break;
                case 13:
                    updatePermItem(item, 13, player.getLocation().getChunk());
                    break;
                case 15:
                    updatePermItem(item, 15, player.getLocation().getChunk());
                    break;
                case 31:
                    new ChunkManager(pmu).open();
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

        //chunk monster spawning
        if (cache.canChunkSpawnMonsters(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(11, allow);
        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(11, deny);
        }

        //chunk pvp
        if (cache.canChunkPvP(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(13, allow);

        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(13, deny);
        }

        //chunk explosions
        if (cache.canChunkExplode(chunkCord)) {
            allowMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
            allow.setItemMeta(allowMeta);
            inventory.setItem(15, allow);
        } else {
            denyMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
            deny.setItemMeta(denyMeta);
            inventory.setItem(15, deny);
        }

        //back
        inventory.setItem(31, backItem);
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
                case 11:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.TRUE.getString(null)}));
                    allow.setItemMeta(allowMeta);
                    cache.setChunkSpawnMonsters(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 13:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.TRUE.getString(null) }));
                    allow.setItemMeta(allowMeta);
                    cache.setChunkPvP(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
                case 15:
                    allowMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.TRUE.getString(null)}));
                    allow.setItemMeta(allowMeta);
                    cache.setChunkExplode(chunkCord, true);
                    inventory.setItem(slot, allow);
                    break;
            }
            //deny
        } else if (item.getType() != permFalseItem.getType()) {

            switch (slot) {
                case 11:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.FALSE.getString(null)}));
                    deny.setItemMeta(denyMeta);
                    cache.setChunkSpawnMonsters(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 13:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.FALSE.getString(null) }));
                    deny.setItemMeta(denyMeta);
                    cache.setChunkPvP(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
                case 15:
                    denyMeta.setDisplayName(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.FALSE.getString(null)}));
                    deny.setItemMeta(denyMeta);
                    cache.setChunkExplode(chunkCord, false);
                    inventory.setItem(slot, deny);
                    break;
            }
        }
    }
}
