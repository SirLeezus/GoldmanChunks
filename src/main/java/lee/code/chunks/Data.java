package lee.code.chunks;

import lee.code.chunks.menusystem.PlayerMenuUtility;
import org.bukkit.Chunk;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Data {

    private final HashMap<UUID, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private final HashMap<UUID, Chunk> playerAutoClaimMap = new HashMap<>();
    private final HashMap<UUID, Vector> adminClaimSelection = new HashMap<>();
    private final List<UUID> adminBypassList = new ArrayList<>();
    private final List<UUID> playerClickDelay = new ArrayList<>();

    public boolean getPlayerClickDelay(UUID uuid) {
        return playerClickDelay.contains(uuid);
    }
    public void addPlayerClickDelay(UUID uuid) {
        playerClickDelay.add(uuid);
    }
    public void removePlayerClickDelay(UUID uuid) {
        playerClickDelay.remove(uuid);
    }
    public void setPlayerAutoClaim(UUID uuid, Chunk chunk) {
        playerAutoClaimMap.put(uuid, chunk);
    }
    public void removePlayerAutoClaim(UUID uuid) {
        playerAutoClaimMap.remove(uuid);
    }
    public Chunk getPlayerLastAutoClaim(UUID uuid) {
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

    public PlayerMenuUtility getPlayerMenuUtil(UUID uuid) {
        PlayerMenuUtility playerMenuUtility;

        if (playerMenuUtilityMap.containsKey(uuid)) {
            return playerMenuUtilityMap.get(uuid);
        } else {
            playerMenuUtility = new PlayerMenuUtility(uuid);
            playerMenuUtilityMap.put(uuid, playerMenuUtility);
            return playerMenuUtility;
        }
    }
}