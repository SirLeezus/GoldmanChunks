package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.PaginatedMenu;
import lee.code.chunks.menusystem.PlayerMU;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerChunks extends PaginatedMenu {

    public PlayerChunks(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_PLAYER_CHUNKS_TITLE.getComponent(new String[] { String.valueOf(pmu.getChunkListPage()  +  1) }) ;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        ItemStack clickedItem = e.getCurrentItem();

        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem == null) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        if (clickedItem.equals(previousPageItem)) {
            if (page == 0) {
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_PREVIOUS_PAGE.getString(null));
            } else {
                page = page - 1;
                pmu.setChunkListPage(page);
                super.open();
                playClickSound(player);
            }
        } else if (clickedItem.equals(nextPageItem)) {
            List<String> chunks = cache.getChunkClaims(uuid);
            if (!((index + 1) >= chunks.size())) {
                page = page + 1;
                pmu.setChunkListPage(page);
                super.open();
                playClickSound(player);
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NEXT_PAGE.getString(null));

        } else {
            if (clickedItem.hasItemMeta()) {
                playClickSound(player);
                Location chunkLocation = getItemChunkLocation(clickedItem);
                if (chunkLocation != null) {
                    pu.teleportPlayerToChunk(player, chunkLocation);
                    player.sendActionBar(Lang.TELEPORT.getComponent(null));
                }
                player.getInventory().close();
            }
        }
    }

    @Override
    public void setMenuItems() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        addMenuBorder();
        page = pmu.getChunkListPage();

        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        Chunk playerChunk = player.getLocation().getChunk();
        String playerChunkCord = pu.formatChunkLocation(playerChunk);

        List<String> chunks = cache.getChunkClaims(uuid);

        List<ItemStack> items = new ArrayList<>();
        for (String chunk : chunks) {
            String[] worldString = chunk.split(",", 2);
            ItemStack itemChunk = switch (worldString[0]) {
                case "world_nether" -> new ItemStack(Material.NETHERRACK);
                case "world_the_end" -> new ItemStack(Material.END_STONE);
                default -> new ItemStack(Material.GRASS_BLOCK);
            };

            ItemMeta itemChunkMeta = itemChunk.getItemMeta();
            itemChunkMeta.displayName(Lang.MENU_PLAYER_CHUNKS_ITEM_NAME.getComponent(new String[] { chunk }));

            if (chunk.equals(playerChunkCord)) {
                itemChunkMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
                itemChunkMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            PersistentDataContainer container = itemChunkMeta.getPersistentDataContainer();
            NamespacedKey chunkLocation = new NamespacedKey(plugin, "chunk-location");
            container.set(chunkLocation, PersistentDataType.STRING, chunk);

            itemChunk.setItemMeta(itemChunkMeta);
            items.add(itemChunk);
        }

        if (!items.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= items.size()) break;
                if (items.get(index) != null) {
                    ItemStack theItem = items.get(index);
                    inventory.addItem(theItem);
                }
            }
        }
    }

    private Location getItemChunkLocation(ItemStack item) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "chunk-location");
        String sHome = container.get(key, PersistentDataType.STRING);
        return sHome != null ? plugin.getPU().unFormatChunkLocation(sHome) : null;
    }
}
