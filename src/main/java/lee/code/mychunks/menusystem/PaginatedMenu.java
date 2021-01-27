package lee.code.mychunks.menusystem;

import lombok.Getter;

public abstract class PaginatedMenu extends Menu {

    //keep track of what page the menu is on
    protected int page = 0;
    //28 is max items because with the border set below,
    //28 empty slots are remaining.
    @Getter protected int maxItemsPerPage = 28;
    //the index represents the index of the slot
    //that the loop is on
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    //set the border and menu buttons for the menu
    public void addMenuBorder(){
        inventory.setItem(48, super.previousPageItem);
        inventory.setItem(49, super.closeItem);
        inventory.setItem(50, super.nextPageItem);

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, super.fillerGlass);
        }

        inventory.setItem(17, super.fillerGlass);
        inventory.setItem(18, super.fillerGlass);
        inventory.setItem(26, super.fillerGlass);
        inventory.setItem(27, super.fillerGlass);
        inventory.setItem(35, super.fillerGlass);
        inventory.setItem(36, super.fillerGlass);

        for (int i = 44; i < 54; i++) {
            if (inventory.getItem(i) == null) inventory.setItem(i, super.fillerGlass);
        }
    }
}
