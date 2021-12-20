package lee.code.chunks.menusystem;

import lombok.Getter;

public abstract class PaginatedMenu extends Menu {

    @Getter protected int maxItemsPerPage = 28;
    protected int page = 0;
    protected int index = 0;

    public PaginatedMenu(PlayerMU playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuBorder(){
        inventory.setItem(48, super.previousPageItem);
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
