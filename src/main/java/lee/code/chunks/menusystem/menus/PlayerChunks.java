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
    public String getMenuName() {
        return Lang.MENU_PLAYER_CHUNKS_TITLE.getString(new String[] { String.valueOf(pmu.getChunkListPage()  +  1) }) ;
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
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        } else if (item.equals(nextPageItem)) {

            if (!((index + 1) >= cache.getChunkClaims(player.getUniqueId()).size())) {
                page = page + 1;
                pmu.setChunkListPage(page);
                super.open();
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NEXT_PAGE.getString(null));

        } else if (item.equals(closeItem)) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        } else {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                String cord = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                String[] splitCord = cord.split(",", 3);
                World world = Bukkit.getWorld(splitCord[0]);
                if (world == null) return;

                Chunk chunk = world.getChunkAt(Integer.parseInt(splitCord[1]), Integer.parseInt(splitCord[2]));
                Location location = new Location(Bukkit.getWorld(splitCord[0]), chunk.getX() * 16, 100, chunk.getZ() * 16);
                location.getWorld().loadChunk(chunk);

                int y = location.getBlockY();
                int x = location.getBlockX() + 8;
                int z = location.getBlockZ() + 8;

                for (int i = y ; i > 0; i--) {
                    Location loc = new Location(location.getWorld(), x, i, z);
                    if (loc.getBlock().getType() == Material.AIR) {
                        Location ground = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ());
                        if (ground.getBlock().getType() != Material.AIR && ground.getBlock().getType() != Material.LAVA) {
                            player.teleportAsync(loc);
                            player.sendActionBar(Component.text(Lang.TELEPORT.getString(null)));
                            return;
                        }
                    }
                }
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_LIST_TELEPORT_UNSAFE.getString(null));
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

                ItemStack itemChunk = new ItemStack(Material.GRASS_BLOCK);

                switch (name) {
                    case "NETHER":
                        itemChunk = new ItemStack(Material.NETHERRACK);
                        break;
                    case "THE_END":
                        itemChunk = new ItemStack(Material.END_STONE);
                        break;
                }

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
