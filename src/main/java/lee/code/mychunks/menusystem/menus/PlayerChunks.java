package lee.code.mychunks.menusystem.menus;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.menusystem.PaginatedMenu;
import lee.code.mychunks.menusystem.PlayerMenuUtility;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerChunks extends PaginatedMenu {

    public PlayerChunks(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return Lang.MENU_PLAYER_CHUNKS_TITLE.getConfigValue(null) + (playerMenuUtility.getChunkListPage()  +  1);
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        MyChunks plugin = MyChunks.getPlugin();
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        //click delay
        if (plugin.getData().getPlayerClickDelay(player.getUniqueId())) return;
        else plugin.getUtility().addPlayerClickDelay(player.getUniqueId());

        //return if players inventory
        if (e.getClickedInventory() == player.getInventory()) return;

        //check for air
        if (item.getType().equals(Material.AIR)) return;

        //check for filler glass
        if (item.equals(fillerGlass)) return;

        //previous page
        if (item.equals(previousPageItem)) {
            if (page == 0) {
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_PREVIOUS_PAGE.getConfigValue(null));
            } else {
                page = page - 1;
                playerMenuUtility.setChunkListPage(page);
                super.open();
            }
        } else if (item.equals(nextPageItem)) {

            if (!((index + 1) >= plugin.getSqLite().getPlayerClaimedChunks(player.getUniqueId()).size())) {
                page = page + 1;
                playerMenuUtility.setChunkListPage(page);
                super.open();
            } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NEXT_PAGE.getConfigValue(null));

        } else if (item.equals(closeItem)) {
            player.closeInventory();
        } else {

            String cord = ChatColor.stripColor(item.getItemMeta().getDisplayName());

            String[] splitCord = cord.split(",", 3);
            Chunk chunk = Bukkit.getWorld(splitCord[0]).getChunkAt(Integer.parseInt(splitCord[1]), Integer.parseInt(splitCord[2]));
            Location location = new Location(Bukkit.getWorld(splitCord[0]), chunk.getX() * 16, 100, chunk.getZ() * 16);
            location.getWorld().loadChunk(chunk);

            int y = location.getBlockY();
            double x = location.getX() + 8.5;
            double z = location.getZ() + 8.5;

            for (int i = y ; i > 0; i--) {
                Location loc = new Location(location.getWorld(), x, i, z);
                if (loc.getBlock().getType() == Material.AIR) {
                    Location ground = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ());
                    if (ground.getBlock().getType() != Material.AIR && ground.getBlock().getType() != Material.LAVA) {
                        player.teleport(loc);
                        player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.MESSAGE_CHUNK_TELEPORT.getConfigValue(new String[] { cord }));
                        return;
                    }
                }
            }
            player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_LIST_TELEPORT_UNSAFE.getConfigValue(null));
        }
    }

    @Override
    public void setMenuItems() {

        page = playerMenuUtility.getChunkListPage();

        MyChunks plugin = MyChunks.getPlugin();
        Player player = playerMenuUtility.getOwner();
        Chunk playerChunk = player.getLocation().getChunk();
        String playerChunkCord = plugin.getUtility().formatChunk(playerChunk);

        addMenuBorder();

        List<String> chunks = plugin.getSqLite().getPlayerClaimedChunks(player.getUniqueId());

        if (!chunks.contains("none")) {

            List<ItemStack> items = new ArrayList<>();
            for (String chunk : chunks) {

                String[] world = chunk.split(",", 2);
                String worldName = Bukkit.getWorld(world[0]).getEnvironment().name();
                ItemStack itemChunk = new ItemStack(Material.GRASS_BLOCK);

                switch (worldName) {
                    case "NETHER":
                        itemChunk = new ItemStack(Material.NETHERRACK);
                        break;
                    case "THE_END":
                        itemChunk = new ItemStack(Material.END_STONE);
                        break;
                }

                ItemMeta itemChunkMeta = itemChunk.getItemMeta();
                itemChunkMeta.setDisplayName(plugin.getUtility().format("&b" + chunk));

                if (chunk.equals(playerChunkCord)) {
                    itemChunkMeta.addEnchant(Enchantment.PROTECTION_FALL, 1, false);
                    itemChunkMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                itemChunk.setItemMeta(itemChunkMeta);
                items.add(itemChunk);
            }

            //pagination loop
            if(items != null && !items.isEmpty()) {
                for(int i = 0; i < getMaxItemsPerPage(); i++) {
                    index = getMaxItemsPerPage() * page + i;
                    if(index >= items.size()) break;
                    if (items.get(index) != null) {

                        //create item
                        ItemStack theItem = items.get(index);

                        //add item
                        inventory.addItem(theItem);
                    }
                }
            }
        }
    }
}
