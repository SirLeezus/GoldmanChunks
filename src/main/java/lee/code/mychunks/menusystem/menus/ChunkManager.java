package lee.code.mychunks.menusystem.menus;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.menusystem.Menu;
import lee.code.mychunks.menusystem.PlayerMenuUtility;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class ChunkManager extends Menu {

    public ChunkManager(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return Lang.MENU_CHUNK_MANAGER_TITLE.getConfigValue(null);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        MyChunks plugin = MyChunks.getPlugin();

        if (plugin.getData().getPlayerClickDelay(playerMenuUtility.getOwner().getUniqueId())) return;
        else plugin.getUtility().addPlayerClickDelay(playerMenuUtility.getOwner().getUniqueId());

        Player player = playerMenuUtility.getOwner();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        switch (e.getSlot()) {
            case 11:
                if (SQL.isChunkOwner(chunkCord, uuid)) {
                    new TrustedChunkSettings(playerMenuUtility).open();
                } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_MANAGE_NOT_CHUNK_OWNER.getConfigValue(null));
                break;
            case 13:
                if (SQL.isChunkOwner(chunkCord, uuid)) {
                    new GeneralChunkSettings(playerMenuUtility).open();
                } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_MANAGE_NOT_CHUNK_OWNER.getConfigValue(null));
                break;
            case 15:
                new TrustedGlobalSettings(playerMenuUtility).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();

        //trusted item
        inventory.setItem(11, trustedItem);

        //general chunk settings items
        inventory.setItem(13, generalSettingsItem);

        //global trusted item
        inventory.setItem(15, globalTrustedItem);
    }

}
