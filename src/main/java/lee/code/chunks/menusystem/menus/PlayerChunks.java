package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
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

        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        if (plugin.getData().hasPlayerClickDelay(player.getUniqueId())) return;
        else plugin.getPU().addPlayerClickDelay(player.getUniqueId());

        if (item == null) return;
        if (e.getClickedInventory() == player.getInventory()) return;
        if (item.getType().equals(Material.AIR)) return;
        if (item.equals(fillerGlass)) return;

        if (item.equals(previousPageItem)) {
            if (page == 0) {
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_PREVIOUS_PAGE.getString(null));
            } else {
                page = page - 1;
                pmu.setChunkListPage(page);
                super.open();
                playClickSound(player);
            }
        } else if (item.equals(nextPageItem)) {

            if (!((index + 1) >= cache.getChunkClaims(player.getUniqueId()).size())) {
                page = page + 1;
                pmu.setChunkListPage(page);
                super.open();
                playClickSound(player);
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NEXT_PAGE.getString(null));

        } else if (item.equals(closeItem)) {
            player.closeInventory();
            playClickSound(player);
        } else {
            playClickSound(player);
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                Location chunk = plugin.getPU().unFormatChunkLocation(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                plugin.getPU().teleportPlayerToChunk(player, chunk);
            }
        }
    }

    @Override
    public void setMenuItems() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        addMenuBorder();

        page = pmu.getChunkListPage();

        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        Chunk playerChunk = player.getLocation().getChunk();
        String playerChunkCord = plugin.getPU().formatChunkLocation(playerChunk);

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
                itemChunkMeta.displayName(Component.text(plugin.getPU().format("&b&l&n" + chunk)));

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
