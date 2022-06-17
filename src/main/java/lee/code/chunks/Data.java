package lee.code.chunks;

import lee.code.chunks.lists.chunksettings.AdminChunkSetting;
import lee.code.chunks.lists.chunksettings.HostileEntitie;
import lee.code.chunks.menusystem.PlayerMU;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private final ConcurrentHashMap<UUID, PlayerMU> playerMUList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, String> playerAutoClaimMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Vector> adminSelectionFirstChunk = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<String>> adminSelectedChunks = new ConcurrentHashMap<>();
    private final List<UUID> adminBypassList = new ArrayList<>();
    @Getter private final List<String> whitelistedWorlds = new ArrayList<>();
    @Getter private final List<String> adminChunkSettings = new ArrayList<>();
    @Getter private final List<EntityType> hostileEntities = new ArrayList<>();

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
    public boolean hasAdminBypass(UUID uuid) { return adminBypassList.contains(uuid); }

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

    public void loadData() {
        //whitelisted worlds
        whitelistedWorlds.add("world");
        whitelistedWorlds.add("world_nether");
        whitelistedWorlds.add("world_the_end");

        //admin chunk settings
        adminChunkSettings.addAll(EnumSet.allOf(AdminChunkSetting.class).stream().map(AdminChunkSetting::name).toList());

        //hostile entities
        hostileEntities.addAll(EnumSet.allOf(HostileEntitie.class).stream().map(HostileEntitie::getType).toList());
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
