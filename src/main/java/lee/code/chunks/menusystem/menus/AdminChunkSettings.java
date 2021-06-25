package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.Menu;
import lee.code.chunks.menusystem.PlayerMU;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AdminChunkSettings extends Menu {

    public AdminChunkSettings(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_ADMIN_CHUNK_SETTINGS_TITLE.getComponent(null);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = pmu.getOwner();

        if (plugin.getData().hasPlayerClickDelay(player.getUniqueId())) return;
        else plugin.getPU().addPlayerClickDelay(player.getUniqueId());
        if (e.getClickedInventory() == player.getInventory()) return;

        ItemStack item = e.getCurrentItem();

        if (item != null && item != fillerGlass) {
            int slot = e.getSlot();
            updatePermItem(player, item, slot, player.getLocation().getChunk());
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
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(10, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(10, deny);
        }

        if (cache.canAdminChunkBuild(chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(11, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(11, deny);
        }

        if (cache.canAdminChunkBreak(chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(12, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(12, deny);
        }

        if (cache.canAdminChunkInteract(chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(13, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(13, deny);
        }

        if (cache.canAdminChunkSpawnMonsters(chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(14, allow);
        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(14, deny);
        }

        if (cache.canAdminChunkPvP(chunkCord)) {
            allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.TRUE.getString(null) })));
            allow.setItemMeta(allowMeta);
            inventory.setItem(15, allow);

        } else {
            denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[] { Lang.FALSE.getString(null) })));
            deny.setItemMeta(denyMeta);
            inventory.setItem(15, deny);
        }

        if (cache.canAdminChunkPvE(chunkCord)) {
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
        Cache cache = plugin.getCache();

        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
                    cache.setAdminChunkExplode(chunkCord, true);
                    inventory.setItem(slot, allow);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                }
                case 11 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkBuild(chunkCord, true);
                    inventory.setItem(slot, allow);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                }
                case 12 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkBreak(chunkCord, true);
                    inventory.setItem(slot, allow);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                }
                case 13 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkInteract(chunkCord, true);
                    inventory.setItem(slot, allow);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                }
                case 14 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkSpawnMonsters(chunkCord, true);
                    inventory.setItem(slot, allow);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                }
                case 15 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkPvP(chunkCord, true);
                    inventory.setItem(slot, allow);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                }
                case 16 -> {
                    allowMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[]{Lang.TRUE.getString(null)})));
                    allow.setItemMeta(allowMeta);
                    cache.setAdminChunkPvE(chunkCord, true);
                    inventory.setItem(slot, allow);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                }
            }
            //deny
        } else if (item.getType() != permFalseItem.getType()) {

            switch (slot) {
                case 10 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_EXPLOSIONS_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkExplode(chunkCord, false);
                    inventory.setItem(slot, deny);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                }
                case 11 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkBuild(chunkCord, false);
                    inventory.setItem(slot, deny);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                }
                case 12 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkBreak(chunkCord, false);
                    inventory.setItem(slot, deny);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                }
                case 13 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkInteract(chunkCord, false);
                    inventory.setItem(slot, deny);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                }
                case 14 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_MONSTER_SPAWNING_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkSpawnMonsters(chunkCord, false);
                    inventory.setItem(slot, deny);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                }
                case 15 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVP_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkPvP(chunkCord, false);
                    inventory.setItem(slot, deny);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                }
                case 16 -> {
                    denyMeta.displayName(Component.text(Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[]{Lang.FALSE.getString(null)})));
                    deny.setItemMeta(denyMeta);
                    cache.setAdminChunkPvE(chunkCord, false);
                    inventory.setItem(slot, deny);
                    player.playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
                }
            }
        }
    }
}
