package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.chunksettings.ChunkSettings;
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

public class GeneralChunkSettings extends Menu {

    public GeneralChunkSettings(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_GENERAL_CHUNK_SETTINGS_TITLE.getComponent(null);
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
            case 11 -> updatePermItem(player, clickedItem, 11, player.getLocation().getChunk());
            case 13 -> updatePermItem(player, clickedItem, 13, player.getLocation().getChunk());
            case 15 -> updatePermItem(player, clickedItem, 15, player.getLocation().getChunk());
            case 31 -> {
                new ChunkManager(pmu).open();
                playClickSound(player);
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
        if (cache.canChunkSetting(ChunkSettings.MONSTERS, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(11, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(11, deny);
        }

        //chunk pvp
        if (cache.canChunkSetting(ChunkSettings.PVP, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(13, allow);

        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(13, deny);
        }

        //chunk explosions
        if (cache.canChunkSetting(ChunkSettings.EXPLOSIONS, chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(15, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(15, deny);
        }

        //back
        inventory.setItem(31, backItem);
    }

    private void updatePermItem(Player player, ItemStack item, int slot, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        ItemStack allow = new ItemStack(permTrueItem);
        ItemStack deny = new ItemStack(permFalseItem);

        ItemMeta allowMeta = allow.getItemMeta();
        ItemMeta denyMeta = deny.getItemMeta();

        //allow
        if (item.getType() == permFalseItem.getType()) {
            switch (slot) {
                case 11 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setChunkSetting(ChunkSettings.MONSTERS, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 13 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setChunkSetting(ChunkSettings.PVP, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
                case 15 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setChunkSetting(ChunkSettings.EXPLOSIONS, chunkCord, true);
                    inventory.setItem(slot, allow);
                    playClickOnSound(player);
                }
            }
            //deny
        } else if (item.getType() == permTrueItem.getType()) {

            switch (slot) {
                case 11 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setChunkSetting(ChunkSettings.MONSTERS, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 13 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setChunkSetting(ChunkSettings.PVP, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
                case 15 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setChunkSetting(ChunkSettings.EXPLOSIONS, chunkCord, false);
                    inventory.setItem(slot, deny);
                    playClickOffSound(player);
                }
            }
        }
    }
}
