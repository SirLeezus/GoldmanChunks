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

            if (!((index + 1) >= cache.getChunkClaims(player.getUniqueId()).size())) {
                page = page + 1;
                pmu.setChunkListPage(page);
                super.open();
                playClickSound(player);
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NEXT_PAGE.getString(null));

        } else {
            playClickSound(player);
            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                Location chunk = pu.unFormatChunkLocation(ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName()));
                pu.teleportPlayerToChunk(player, chunk);
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

        if (!chunks.contains("n")) {

            List<ItemStack> items = new ArrayList<>();
            for (String chunk : chunks) {

                String[] worldString = chunk.split(",", 2);
                World world = Bukkit.getWorld(worldString[0]);
                if (world == null) continue;
                String name = world.getEnvironment().name();

                new ItemStack(Material.GRASS_BLOCK);
                ItemStack itemChunk = switch (name) {
                    case "NETHER" -> new ItemStack(Material.NETHERRACK);
                    case "THE_END" -> new ItemStack(Material.END_STONE);
                    default -> new ItemStack(Material.GRASS_BLOCK);
                };

                ItemMeta itemChunkMeta = itemChunk.getItemMeta();
                itemChunkMeta.displayName(pu.formatC("&b&l&n" + chunk));

                if (chunk.equals(playerChunkCord)) {
                    itemChunkMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
                    itemChunkMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }
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
    }
}
