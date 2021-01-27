package lee.code.mychunks;

import lee.code.mychunks.menusystem.PlayerMenuUtility;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Data {

    private final HashMap<UUID, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    private final HashMap<UUID, Chunk> playerAutoClaimMap = new HashMap<>();
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
