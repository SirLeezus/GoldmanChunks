package lee.code.chunks.menusystem;

import lee.code.chunks.lists.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements InventoryHolder {

    protected PlayerMU pmu;
    protected Inventory inventory;
    protected ItemStack fillerGlass = MenuItems.FILLER_GLASS.getItem();
    protected ItemStack backItem = MenuItems.BACK_MENU.getItem();
    protected ItemStack closeItem = MenuItems.CLOSE_MENU.getItem();
    protected ItemStack trustedItem = MenuItems.TRUSTED.getItem();
    protected ItemStack globalTrustedItem = MenuItems.GLOBAL_TRUSTED.getItem();
    protected ItemStack generalSettingsItem = MenuItems.GENERAL.getItem();
    protected ItemStack permTrueItem = MenuItems.PERM_TRUE.getItem();
    protected ItemStack permFalseItem = MenuItems.PERM_FALSE.getItem();
    protected ItemStack nextPageItem = MenuItems.NEXT_PAGE.getItem();
    protected ItemStack previousPageItem = MenuItems.PREVIOUS_PAGE.getItem();

    public Menu(PlayerMU playerMenuUtility) {
        this.pmu = playerMenuUtility;
    }

    public abstract String getMenuName();
    public abstract int getSlots();
    public abstract void handleMenu(InventoryClickEvent e);
    public abstract void setMenuItems();
    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems();
        pmu.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setFillerGlass() {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerGlass);
            }
        }
    }
}
