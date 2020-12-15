package lee.code.mychunks.menusystem;

import lee.code.mychunks.files.defaults.Config;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.xseries.SkullUtils;
import lee.code.mychunks.xseries.XMaterial;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public abstract class Menu implements InventoryHolder {

    //protected values that can be accessed in the menus
    protected PlayerMenuUtility playerMenuUtility;
    protected Inventory inventory;
    protected ItemStack fillerGlass = makeItem(Config.INTERFACE_FILLER_GLASS_ITEM.getConfigValue(null), ChatColor.translateAlternateColorCodes('&',"&r"));
    protected ItemStack backItem = makeItem(Config.INTERFACE_BACK_ITEM.getConfigValue(null), Lang.INTERFACE_ITEM_BACK_NAME.getConfigValue(null));
    protected ItemStack closeItem = makeItem(Config.INTERFACE_CLOSE_ITEM.getConfigValue(null), Lang.INTERFACE_ITEM_CLOSE_NAME.getConfigValue(null));
    protected ItemStack trustedItem = makeItem("PLAYER_HEAD", Lang.ITEM_TRUSTED_CHUNK_SETTINGS_NAME.getConfigValue(null), Lang.ITEM_TRUSTED_CHUNK_SETTINGS_LORE_1.getConfigValue(null) + "%" + Lang.ITEM_TRUSTED_CHUNK_SETTINGS_LORE_2.getConfigValue(null));
    protected ItemStack globalTrustedItem = makeItem("PLAYER_HEAD", Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_NAME.getConfigValue(null), Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_LORE_1.getConfigValue(null) + "%" + Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_LORE_2.getConfigValue(null));
    protected ItemStack generalSettingsItem = makeItem("PLAYER_HEAD", Lang.ITEM_GENERAL_CHUNK_SETTINGS_NAME.getConfigValue(null), Lang.ITEM_GENERAL_CHUNK_SETTINGS_LORE_1.getConfigValue(null) + "%" + Lang.ITEM_GENERAL_CHUNK_SETTINGS_LORE_2.getConfigValue(null));
    protected ItemStack trustedPlayersItem = makeItem("PLAYER_HEAD", Lang.ITEM_TRUSTED_CHUNK_PLAYERS_NAME.getConfigValue(null), Lang.ITEM_TRUSTED_CHUNK_PLAYERS_LORE_1.getConfigValue(null) + "%" + Lang.ITEM_TRUSTED_CHUNK_PLAYERS_LORE_2.getConfigValue(null));
    protected ItemStack permTrueItem = makeItem(Config.INTERFACE_PERMISSION_ITEM_TRUE.getConfigValue(null), "");
    protected ItemStack permFalseItem = makeItem(Config.INTERFACE_PERMISSION_ITEM_FALSE.getConfigValue(null), "");
    protected ItemStack nextPageItem = makeItem(Config.INTERFACE_NEXT_PAGE_ITEM.getConfigValue(null), Lang.INTERFACE_ITEM_NEXT_PAGE_NAME.getConfigValue(null));
    protected ItemStack previousPageItem = makeItem(Config.INTERFACE_PREVIOUS_PAGE_ITEM.getConfigValue(null), Lang.INTERFACE_ITEM_PREVIOUS_PAGE_NAME.getConfigValue(null));

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
    public void setFillerGlass(){
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, fillerGlass);
            }
        }
    }

    //create menu interface item ItemStack
    public ItemStack makeItem(String string, String displayName, String... lore) {

        if (XMaterial.matchXMaterial(string).isPresent()) {
            if (XMaterial.matchXMaterial(string).get().isSupported()) {
                ItemStack item = XMaterial.valueOf(string).parseItem();
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

                if (displayName.equals(Lang.ITEM_TRUSTED_CHUNK_SETTINGS_NAME.getConfigValue(null))) SkullUtils.applySkin(itemMeta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjE5ZTM2YTg3YmFmMGFjNzYzMTQzNTJmNTlhN2Y2M2JkYjNmNGM4NmJkOWJiYTY5Mjc3NzJjMDFkNGQxIn19fQ==");
                else if (displayName.equals(Lang.ITEM_GLOBAL_TRUSTED_CHUNK_SETTINGS_NAME.getConfigValue(null))) SkullUtils.applySkin(itemMeta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0=");
                else if (displayName.equals(Lang.ITEM_GENERAL_CHUNK_SETTINGS_NAME.getConfigValue(null))) SkullUtils.applySkin(itemMeta, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZhNzY0YjNjMWQ0NjJmODEyNDQ3OGZmNTQzYzc2MzNmYTE5YmFmOTkxM2VlMjI4NTEzZTgxYTM2MzNkIn19fQ==");

                item.setItemMeta(itemMeta);
                return item;

            } else {
                Bukkit.getLogger().log(Level.SEVERE,"[OneStopShop] The item " + string + " is not supported on your server version, you need to fix this in your config.yml.");
                return XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
            }
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "[OneStopShop] The item " + string + " is not a item in Minecraft, you need to fix this in your config.yml.");
            return XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
        }
    }
}
