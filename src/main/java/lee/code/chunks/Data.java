package lee.code.chunks;

import lee.code.chunks.database.SQLite;
import lee.code.chunks.menusystem.PlayerMU;
import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private final ConcurrentHashMap<UUID, PlayerMU> playerMUList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> playerAutoClaimMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Vector> adminSelectionFirstChunk = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<String>> adminSelectedChunks = new ConcurrentHashMap<>();
    private final List<UUID> adminBypassList = new ArrayList<>();
    private final List<UUID> playerClickDelay = new ArrayList<>();
    @Getter private final List<String> whitelistedWorlds = new ArrayList<>();

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

    public boolean hasFirstAdminSelection(UUID uuid) {
        return adminSelectionFirstChunk.containsKey(uuid);
    }
    public void setFirstAdminSelection(UUID uuid, Vector vector) {
        adminSelectionFirstChunk.put(uuid, vector);
    }
    public void removeFirstAdminSelection(UUID uuid) {
        adminSelectionFirstChunk.remove(uuid);
    }
    public Vector getFistAdminSelection(UUID uuid) {
        return adminSelectionFirstChunk.get(uuid);
    }

    public boolean hasAdminSelectedChunks(UUID uuid) { return adminSelectedChunks.containsKey(uuid); }
    public void setAdminSelectedChunks(UUID uuid, List<String> chunks) {
        adminSelectedChunks.put(uuid, chunks);
    }
    public List<String> getAdminSelectedChunks(UUID uuid) {
        return adminSelectedChunks.get(uuid);
    }
    public void removeAdminSelectedChunks(UUID uuid) { adminSelectedChunks.remove(uuid); }

    public void cacheDatabase() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        SQL.loadChunks();
        SQL.loadPlayerData();
        SQL.loadAdminChunks();
    }

    public void loadListData() {
        //whitelisted worlds
        whitelistedWorlds.add("world");
        whitelistedWorlds.add("world_nether");
        whitelistedWorlds.add("world_the_end");
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
