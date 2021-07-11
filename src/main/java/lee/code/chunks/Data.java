package lee.code.chunks;

import lee.code.chunks.database.SQLite;
import lee.code.chunks.menusystem.PlayerMU;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Data {

    private final HashMap<UUID, PlayerMU> playerMUList = new HashMap<>();
    private final HashMap<UUID, String> playerAutoClaimMap = new HashMap<>();
    private final HashMap<UUID, Vector> adminClaimSelection = new HashMap<>();
    private final List<UUID> adminBypassList = new ArrayList<>();
    private final List<UUID> playerFlyList = new ArrayList<>();
    private final List<UUID> playerClickDelay = new ArrayList<>();

    public boolean hasPlayerClickDelay(UUID uuid) {
        return playerClickDelay.contains(uuid);
    }
    public void addPlayerClickDelay(UUID uuid) {
        playerClickDelay.add(uuid);
    }
    public void removePlayerClickDelay(UUID uuid) {
        playerClickDelay.remove(uuid);
    }

    public void setPlayerAutoClaim(UUID uuid, String chunk) {
        playerAutoClaimMap.put(uuid, chunk);
    }
    public void removePlayerAutoClaim(UUID uuid) {
        playerAutoClaimMap.remove(uuid);
    }
    public String getPlayerLastAutoClaim(UUID uuid) {
        return playerAutoClaimMap.get(uuid);
    }
    public boolean isPlayerAutoClaiming(UUID uuid) {
        return playerAutoClaimMap.containsKey(uuid);
    }

    public void addAdminBypass(UUID uuid) {
        adminBypassList.add(uuid);
    }
    public void removeAdminBypass(UUID uuid) {
        adminBypassList.remove(uuid);
    }
    public boolean hasAdminBypass(UUID uuid) {
        return adminBypassList.contains(uuid);
    }

    public void addChunkFlying(UUID uuid) {
        playerFlyList.add(uuid);
    }
    public void removeChunkFlying(UUID uuid) {
        playerFlyList.remove(uuid);
    }
    public boolean isChunkFlying(UUID uuid) {
        return playerFlyList.contains(uuid);
    }

    public boolean hasAdminClaimSelection(UUID uuid) {
        return adminClaimSelection.containsKey(uuid);
    }
    public void addAdminClaimSelection(UUID uuid, Vector vector) {
        adminClaimSelection.put(uuid, vector);
    }
    public void removeAdminClaimSelection(UUID uuid) {
        adminClaimSelection.remove(uuid);
    }
    public Vector getAdminClaimSelection(UUID uuid) {
        return adminClaimSelection.get(uuid);
    }

    public void cacheDatabase() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        SQL.loadChunks();
        SQL.loadPlayerData();
        SQL.loadAdminChunks();
    }

    public PlayerMU getPlayerMU(UUID uuid) {
        if (playerMUList.containsKey(uuid)) {
            return playerMUList.get(uuid);
        } else {
            PlayerMU pmu = new PlayerMU(uuid);
            playerMUList.put(uuid, pmu);
            return pmu;
        }
    }
}
