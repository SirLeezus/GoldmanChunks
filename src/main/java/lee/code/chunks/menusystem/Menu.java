package lee.code.chunks.menusystem;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.lists.Lang;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class Menu implements InventoryHolder {

    //protected values that can be accessed in the menus
    protected PlayerMenuUtility playerMenuUtility;
    protected Inventory inventory;
    protected ItemStack fillerGlass = makeItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.translateAlternateColorCodes('&', "&r"));
    protected ItemStack backItem = makeItem(Material.BARRIER, Lang.INTERFACE_ITEM_BACK_NAME.getString(null));
    protected ItemStack closeItem = makeItem(Material.BARRIER, Lang.INTERFACE_ITEM_CLOSE_NAME.getString(null));
    protected ItemStack trustedItem = makeItem(Material.PLAYER_HEAD, Lang.ITEM_TRUSTED_CHUNK_SETTINGS_NAME.getString(null), Lang.ITEM_TRUSTED_CHUNK_SETTINGS_LORE_1.getString(null) + "%" + Lang.ITEM_TRUSTED_CHUNK_SETTINGS_LORE_2.getString(null));
    protected ItemStack globalTrustedItem = makeItem(Material.PLAYER_HEAD, Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_NAME.getString(null), Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_LORE_1.getString(null) + "%" + Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_LORE_2.getString(null));
    protected ItemStack generalSettingsItem = makeItem(Material.PLAYER_HEAD, Lang.ITEM_GENERAL_CHUNK_SETTINGS_NAME.getString(null), Lang.ITEM_GENERAL_CHUNK_SETTINGS_LORE_1.getString(null) + "%" + Lang.ITEM_GENERAL_CHUNK_SETTINGS_LORE_2.getString(null));
    protected ItemStack permTrueItem = makeItem(Material.LIME_STAINED_GLASS_PANE, "");
    protected ItemStack permFalseItem = makeItem(Material.RED_STAINED_GLASS_PANE, "");
    protected ItemStack nextPageItem = makeItem(Material.PAPER, Lang.INTERFACE_ITEM_NEXT_PAGE_NAME.getString(null));
    protected ItemStack previousPageItem = makeItem(Material.PAPER, Lang.INTERFACE_ITEM_PREVIOUS_PAGE_NAME.getString(null));

    //constructor for Menu. Pass in a PlayerMenuUtility so that
    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
    }

    //let each menu decide their name
    public abstract String getMenuName();

    //let each menu decide their slot amount
    public abstract int getSlots();

    //let each menu decide how the items in the menu will be handled when clicked
    public abstract void handleMenu(InventoryClickEvent e);

    //let each menu decide what items are to be placed in the inventory menu
    public abstract void setMenuItems();

    //when called, an inventory is created and opened for the player
    public void open() {
        //the owner of the inventory created is the Menu itself
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        //grab all the items specified to be used for this menu and add to inventory
        this.setMenuItems();

        //open the inventory for the player
        playerMenuUtility.getOwner().openInventory(inventory);
    }

    //overridden method from the InventoryHolder interface
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    //helpful utility method to fill all remaining slots with "filler glass"
    public void setFillerGlass() {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerGlass);
            }
        }
    }

    //create menu interface item ItemStack
    public ItemStack makeItem(Material material, String displayName, String... lore) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);

        List<String> lines = new ArrayList<>();
        for (String line : lore) {
            if (line.contains("%")) {
                String[] split = StringUtils.split(line, "%");
                lines.addAll(Arrays.asList(split));

            } else lines.add(line);
        }

        itemMeta.setLore(lines);
        item.setItemMeta(itemMeta);

        if (displayName.equals(Lang.ITEM_TRUSTED_CHUNK_SETTINGS_NAME.getString(null))) plugin.getUtility().createCustomPlayerHead(item, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjE5ZTM2YTg3YmFmMGFjNzYzMTQzNTJmNTlhN2Y2M2JkYjNmNGM4NmJkOWJiYTY5Mjc3NzJjMDFkNGQxIn19fQ==");
        else if (displayName.equals(Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_NAME.getString(null))) plugin.getUtility().createCustomPlayerHead(item, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
        else if (displayName.equals(Lang.ITEM_GENERAL_CHUNK_SETTINGS_NAME.getString(null))) plugin.getUtility().createCustomPlayerHead(item,"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZhNzY0YjNjMWQ0NjJmODEyNDQ3OGZmNTQzYzc2MzNmYTE5YmFmOTkxM2VlMjI4NTEzZTgxYTM2MzNkIn19fQ==");

        return item;
    }
}
