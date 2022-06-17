package lee.code.chunks.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.tables.AdminChunkTable;
import lee.code.chunks.database.tables.ChunkTable;
import lee.code.chunks.database.tables.PlayerTable;
import lee.code.chunks.lists.Settings;
import lee.code.chunks.lists.chunksettings.AdminChunkSetting;
import lee.code.chunks.lists.chunksettings.ChunkSetting;
import lee.code.chunks.lists.chunksettings.ChunkTrustedGlobalSetting;
import lee.code.chunks.lists.chunksettings.ChunkTrustedSetting;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.*;

public class CacheManager {

    @Getter
    private final Cache<UUID, PlayerTable> playerCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .build();

    @Getter
    private final Cache<String, ChunkTable> chunkCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .build();

    @Getter
    private final Cache<UUID, String> playerChunkCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .build();

    @Getter
    private final Cache<String, AdminChunkTable> adminChunkCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .build();

    //CHUNK DATA

    private ChunkTable getChunkTable(String chunk) {
        return getChunkCache().getIfPresent(chunk);
    }

    private String getPlayerChunksString(UUID uuid) { return getPlayerChunkCache().getIfPresent(uuid); }

    public void claimChunk(String chunk, UUID owner) {
        ChunkTable chunkTable = new ChunkTable(chunk, owner);
        getChunkCache().put(chunkTable.getChunk(), chunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().createChunkTable(chunkTable);
        addClaim(owner, chunk);
    }

    public void setChunk(ChunkTable chunkTable) {
        getChunkCache().put(chunkTable.getChunk(), chunkTable);

        //player chunk cache
        String chunks = getPlayerChunksString(chunkTable.getOwner()) != null ? getPlayerChunksString(chunkTable.getOwner()) + "%" + chunkTable.getChunk() : chunkTable.getChunk();
        getPlayerChunkCache().put(chunkTable.getOwner(), chunks);
    }

    public boolean isChunkForSale(String chunk) {
        return getChunkTable(chunk).getChunkPrice() > 0;
    }

    public void setChunkPrice(String chunk, long price) {
        ChunkTable chunkTable = getChunkTable(chunk);
        chunkTable.setChunkPrice(price);
        getChunkCache().put(chunkTable.getChunk(), chunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updateChunkTable(chunkTable);
    }

    public void setChunkOwner(String chunk, UUID oldOwner, UUID newOwner) {
        ChunkTable chunkTable = getChunkTable(chunk);
        chunkTable.setOwner(newOwner);
        chunkTable.setChunkPrice(0);
        addClaim(newOwner, chunk);
        subtractClaim(oldOwner, chunk);
        getChunkCache().put(chunkTable.getChunk(), chunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updateChunkTable(chunkTable);
    }

    public long getChunkPrice(String chunk) {
        return getChunkTable(chunk).getChunkPrice();
    }

    public List<String> getChunkClaims(UUID uuid) {
        if (getPlayerChunkCache().getIfPresent(uuid) != null) {
            String[] split = StringUtils.split(getPlayerChunksString(uuid), '%');
            return new ArrayList<>(Arrays.asList(split));
        } else return new ArrayList<>();
    }

    public void unclaimChunk(String chunk, UUID uuid) {
        subtractClaim(uuid, chunk);
        GoldmanChunks.getPlugin().getDatabaseManager().deleteChunkTable(getChunkTable(chunk));
        getChunkCache().invalidate(chunk);
    }

    public boolean isChunkClaimed(String chunk) {
        return getChunkCache().getIfPresent(chunk) != null;
    }

    public boolean isChunkOwner(String chunk, UUID uuid) {
        return getChunkTable(chunk).getOwner().equals(uuid);
    }

    public String getChunkOwnerName(String chunk) {
        return Bukkit.getOfflinePlayer(getChunkTable(chunk).getOwner()).getName();
    }

    public UUID getChunkOwnerUUID(String chunk) {
        return getChunkTable(chunk).getOwner();
    }

    public void unclaimAllChunks(UUID uuid) {
        DatabaseManager databaseManager = GoldmanChunks.getPlugin().getDatabaseManager();
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setClaimed(0);
        getPlayerCache().put(uuid, playerTable);
        getChunkCache().invalidateAll(new ArrayList<>(Arrays.asList(StringUtils.split(getPlayerChunksString(uuid), '%'))));
        getPlayerChunkCache().invalidate(uuid);
        databaseManager.updatePlayerTable(playerTable);
        databaseManager.deleteAllChunkTables(uuid);
    }

    public boolean isChunkTrusted(String chunk, UUID uuid) {
        return getChunkTable(chunk).getTrusted().contains(uuid.toString());
    }

    public List<String> getChunkTrustedNames(String chunk) {
        String trustedPlayers = getChunkTable(chunk).getTrusted();
        List<String> trustedNames = new ArrayList<>();
        if (!trustedPlayers.equals("0")) {
            for (String player : StringUtils.split(trustedPlayers, ',')) {
                trustedNames.add(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName());
            }
        }
        return trustedNames;
    }

    public void removeChunkTrusted(String chunk, UUID uuid) {
        ChunkTable chunkTable = getChunkTable(chunk);
        List<String> trusted = new ArrayList<>(Arrays.asList(StringUtils.split(chunkTable.getTrusted(), ',')));
        trusted.remove(uuid.toString());
        String newTrusted = trusted.isEmpty() ? "0" : StringUtils.join(trusted, ",");
        chunkTable.setTrusted(newTrusted);
        getChunkCache().put(chunkTable.getChunk(), chunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updateChunkTable(chunkTable);
    }

    public void addChunkTrusted(String chunk, UUID uuid) {
        ChunkTable chunkTable = getChunkTable(chunk);
        String trusted = chunkTable.getTrusted();
        trusted = trusted.equals("0") ? uuid.toString() : trusted + "," + uuid;
        chunkTable.setTrusted(trusted);
        getChunkCache().put(chunkTable.getChunk(), chunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updateChunkTable(chunkTable);
    }

    public boolean canChunkTrustedSetting(ChunkTrustedSetting setting, String chunk) {
        ChunkTable chunkTable = getChunkTable(chunk);
        switch (setting) {
            case BUILD -> { return chunkTable.isTrustedBuild(); }
            case BREAK -> { return chunkTable.isTrustedBreak(); }
            case PVE -> { return chunkTable.isTrustedPVE(); }
            case INTERACT -> { return chunkTable.isTrustedInteract(); }
            default -> { return false; }
        }
    }

    public void setChunkTrustedSetting(ChunkTrustedSetting setting, String chunk, boolean result) {
        ChunkTable chunkTable = getChunkTable(chunk);
        switch (setting) {
            case BUILD -> chunkTable.setTrustedBuild(result);
            case BREAK -> chunkTable.setTrustedBreak(result);
            case PVE -> chunkTable.setTrustedPVE(result);
            case INTERACT -> chunkTable.setTrustedInteract(result);
        }
        getChunkCache().put(chunkTable.getChunk(), chunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updateChunkTable(chunkTable);
    }

    public boolean canChunkSetting(ChunkSetting setting, String chunk) {
        ChunkTable chunkTable = getChunkTable(chunk);
        switch (setting) {
            case MONSTERS -> { return chunkTable.isChunkMonsterSpawning(); }
            case EXPLOSIONS -> { return chunkTable.isChunkExplosions(); }
            default -> { return false; }
        }
    }

    public void setChunkSetting(ChunkSetting setting, String chunk, boolean result) {
        ChunkTable chunkTable = getChunkTable(chunk);
        switch (setting) {
            case MONSTERS -> chunkTable.setChunkMonsterSpawning(result);
            case EXPLOSIONS -> chunkTable.setChunkExplosions(result);
        }
        getChunkCache().put(chunkTable.getChunk(), chunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updateChunkTable(chunkTable);
    }

    //PLAYER DATA

    private PlayerTable getPlayerTable(UUID uuid) {
        return getPlayerCache().getIfPresent(uuid);
    }

    public void createPlayerData(UUID uuid) {
        PlayerTable playerTable = new PlayerTable(uuid);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().createPlayerTable(playerTable);
    }

    public void setPlayerData(PlayerTable playerTable) {
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
    }

    public boolean hasPlayerData(UUID uuid) {
        return getPlayerCache().getIfPresent(uuid) != null;
    }

    public boolean hasClaimedChunks(UUID uuid) {
        return getPlayerTable(uuid).getClaimed() != 0;
    }

    public int getPlayerMaxClaimAmount(UUID uuid) {
        PlayerTable playerTable = getPlayerTable(uuid);

        int maxClaims = Settings.CLAIMS_MAX.getValue();
        int defaultClaims = 10;
        int bonusClaims = playerTable.getBonusClaims();
        int accruedClaims = getAccruedClaimsAmount(uuid);

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (!player.isOp()) return defaultClaims + bonusClaims + accruedClaims;
        else return maxClaims;
    }

    public int getPlayerDefaultClaimAmount(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        int maxClaims = Settings.CLAIMS_MAX.getValue();
        int defaultClaims = 10;

        if (!player.isOp()) return defaultClaims;
        else return maxClaims;
    }

    public int getBonusClaimsAmount(UUID uuid) {
        return getPlayerTable(uuid).getBonusClaims();
    }

    public void removeBonusClaimsAmount(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        int newAmount = playerTable.getBonusClaims() - amount;
        if (newAmount < 0) newAmount = 0;
        playerTable.setBonusClaims(newAmount);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public void addBonusClaimsAmount(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        int newAmount = playerTable.getBonusClaims() + amount;
        if (newAmount > Settings.CLAIMS_MAX.getValue()) newAmount = Settings.CLAIMS_MAX.getValue();
        playerTable.setBonusClaims(newAmount);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public void setBonusClaimsAmount(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setBonusClaims(Math.max(amount, 0));
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public int getAccruedClaimsAmount(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        int maxAccruedClaims = Settings.ACCRUED_CLAIMS_MAX.getValue();
        int baseTimeRequired = Settings.ACCRUED_CLAIMS_BASE_TIME_REQUIRED.getValue();
        int claimAmountGiven = Settings.ACCRUED_CLAIMS_AMOUNT_GIVEN.getValue();

        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int accruedClaims = time / baseTimeRequired * claimAmountGiven;
        if (accruedClaims > maxAccruedClaims) accruedClaims = maxAccruedClaims;
        return accruedClaims;
    }

    public int getClaimedAmount(UUID uuid) {
        return getPlayerTable(uuid).getClaimed();
    }

    private void addClaim(UUID uuid, String chunk) {
        //player cache
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setClaimed(playerTable.getClaimed() + 1);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);

        //player chunk cache
        String chunks = getPlayerChunksString(uuid) != null ? getPlayerChunksString(uuid) + "%" + chunk : chunk;
        getPlayerChunkCache().put(uuid, chunks);
    }

    private void subtractClaim(UUID uuid, String chunk) {
        //player cache
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setClaimed(playerTable.getClaimed() - 1);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);

        //player chunk cache
        List<String> newChunks = new ArrayList<>();
        String[] split = StringUtils.split(getPlayerChunksString(uuid), '%');
        for (String chunkCord : split) if (!chunkCord.equals(chunk)) newChunks.add(chunkCord);
        if (newChunks.isEmpty()) getPlayerChunkCache().invalidate(uuid);
        else getPlayerChunkCache().put(uuid, StringUtils.join(newChunks, "%"));
    }

    public boolean isGlobalTrusted(UUID uuid, UUID trusted) {
        return getPlayerTable(uuid).getGlobalTrusted().contains(trusted.toString());
    }

    public void addGlobalTrusted(UUID uuid, UUID trusted) {
        PlayerTable playerTable = getPlayerTable(uuid);
        String newGlobalTrustedList = playerTable.getGlobalTrusted().equals("0") ? trusted.toString() : playerTable.getGlobalTrusted() + "," + trusted;
        playerTable.setGlobalTrusted(newGlobalTrustedList);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public List<String> getGlobalTrustedNames(UUID uuid) {
        PlayerTable playerTable = getPlayerTable(uuid);
        List<String> globalTrustedNames = new ArrayList<>();
        if (!playerTable.getGlobalTrusted().equals("0")) {
            String[] split = StringUtils.split(playerTable.getGlobalTrusted(), ',');
            for (String player : split) globalTrustedNames.add(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName());
        }
        return globalTrustedNames;
    }

    public List<String> getGlobalTrusted(UUID uuid) {
        PlayerTable playerTable = getPlayerTable(uuid);
        List<String> globalTrusted = new ArrayList<>();
        if (!playerTable.getGlobalTrusted().equals("0")) {
            globalTrusted.addAll(Arrays.asList(StringUtils.split(playerTable.getGlobalTrusted(), ',')));
        }
        return globalTrusted;
    }

    public void removeGlobalTrusted(UUID uuid, UUID trusted) {
        PlayerTable playerTable = getPlayerTable(uuid);
        List<String> globalTrusted = new ArrayList<>(Arrays.asList(StringUtils.split(playerTable.getGlobalTrusted(), ',')));
        globalTrusted.remove(trusted.toString());
        String newTrusted = globalTrusted.isEmpty() ? "0" : StringUtils.join(globalTrusted, ",");
        playerTable.setGlobalTrusted(newTrusted);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public boolean canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting setting, UUID uuid) {
        PlayerTable playerTable = getPlayerTable(uuid);
        switch (setting) {
            case BUILD -> { return playerTable.isGlobalBuild(); }
            case BREAK -> { return playerTable.isGlobalBreak(); }
            case PVE -> { return playerTable.isGlobalPVE(); }
            case INTERACT -> { return playerTable.isGlobalInteract(); }
            default -> { return false; }
        }
    }

    public void setChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting setting, UUID uuid, boolean result) {
        PlayerTable playerTable = getPlayerTable(uuid);
        switch (setting) {
            case BUILD -> playerTable.setGlobalBuild(result);
            case BREAK -> playerTable.setGlobalBreak(result);
            case PVE -> playerTable.setGlobalPVE(result);
            case INTERACT -> playerTable.setGlobalInteract(result);
        }
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public List<UUID> getUserList() {
        return new ArrayList<>(getPlayerCache().asMap().keySet());
    }

    public boolean isChunkFlying(UUID uuid) {
        return getPlayerTable(uuid).isChunkFlying();
    }

    public void setChunkFlying(UUID uuid, boolean canFly) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setChunkFlying(canFly);
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    //ADMIN CHUNK DATA

    private AdminChunkTable getAdminChunkTable(String chunk) {
        return getAdminChunkCache().getIfPresent(chunk);
    }

    public List<String> getAdminChunkClaims() {
        return new ArrayList<>(getAdminChunkCache().asMap().keySet());
    }

    public int updateBulkAdminChunks(List<String> chunks, AdminChunkSetting setting, boolean result) {
        List<AdminChunkTable> adminChunks = new ArrayList<>();
        for (String chunk : chunks) {
            if (isAdminChunk(chunk)) {
                AdminChunkTable adminChunkTable = getAdminChunkTable(chunk);
                switch (setting) {
                    case BUILD -> adminChunkTable.setChunkBuild(result);
                    case BREAK -> adminChunkTable.setChunkBreak(result);
                    case PVE -> adminChunkTable.setChunkPVE(result);
                    case INTERACT -> adminChunkTable.setChunkInteract(result);
                    case EXPLOSIONS -> adminChunkTable.setChunkExplosions(result);
                    case MONSTERS -> adminChunkTable.setChunkMonsterSpawning(result);
                }
                getAdminChunkCache().put(adminChunkTable.getChunk(), adminChunkTable);
                adminChunks.add(adminChunkTable);
            }
        }
        GoldmanChunks.getPlugin().getDatabaseManager().updateBulkAdminChunks(adminChunks);
        return adminChunks.size();
    }

    public void claimBulkAdminChunks(List<String> chunks) {
        HashMap<String, AdminChunkTable> adminChunks = new HashMap<>();
        for (String chunk : chunks) adminChunks.put(chunk, new AdminChunkTable(chunk));
        getAdminChunkCache().putAll(adminChunks);
        GoldmanChunks.getPlugin().getDatabaseManager().createBulkAdminChunks(new ArrayList<>(adminChunks.values()));
    }


    public void claimAdminChunk(String chunk) {
        AdminChunkTable adminChunkTable = new AdminChunkTable(chunk);
        getAdminChunkCache().put(adminChunkTable.getChunk(), adminChunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().createAdminChunkTable(adminChunkTable);
    }

    public void setAdminChunk(AdminChunkTable adminChunkTable) {
        getAdminChunkCache().put(adminChunkTable.getChunk(), adminChunkTable);
    }

    public void unclaimBulkAdminChunk(List<String> chunks) {
        HashMap<String, AdminChunkTable> adminChunks = new HashMap<>();
        for (String chunk : chunks) adminChunks.put(chunk, new AdminChunkTable(chunk));
        getAdminChunkCache().invalidateAll(adminChunks.entrySet());
        GoldmanChunks.getPlugin().getDatabaseManager().deleteBulkAdminChunks(new ArrayList<>(adminChunks.values()));
    }

    public void unclaimAdminChunk(String chunk) {
        AdminChunkTable adminChunkTable = getAdminChunkTable(chunk);
        getAdminChunkCache().invalidate(adminChunkTable.getChunk());
        GoldmanChunks.getPlugin().getDatabaseManager().deleteAdminChunkTable(adminChunkTable);
    }

    public boolean isAdminChunk(String chunk) {
        return getAdminChunkCache().getIfPresent(chunk) != null;
    }

    public boolean canAdminChunkSetting(AdminChunkSetting setting, String chunk) {
        AdminChunkTable adminChunkTable = getAdminChunkTable(chunk);
        switch (setting) {
            case BUILD -> { return adminChunkTable.isChunkBuild(); }
            case BREAK -> { return adminChunkTable.isChunkBreak(); }
            case INTERACT -> { return adminChunkTable.isChunkInteract(); }
            case PVE -> { return adminChunkTable.isChunkPVE(); }
            case MONSTERS -> { return adminChunkTable.isChunkMonsterSpawning(); }
            case EXPLOSIONS -> { return adminChunkTable.isChunkExplosions(); }
            default -> { return false; }
        }
    }

    public void setAdminChunkSetting(AdminChunkSetting setting, String chunk, boolean result) {
        AdminChunkTable adminChunkTable = getAdminChunkTable(chunk);

        switch (setting) {
            case BUILD -> adminChunkTable.setChunkBuild(result);
            case BREAK -> adminChunkTable.setChunkBreak(result);
            case INTERACT -> adminChunkTable.setChunkInteract(result);
            case PVE -> adminChunkTable.setChunkPVE(result);
            case MONSTERS -> adminChunkTable.setChunkMonsterSpawning(result);
            case EXPLOSIONS -> adminChunkTable.setChunkExplosions(result);
        }
        getAdminChunkCache().put(adminChunkTable.getChunk(), adminChunkTable);
        GoldmanChunks.getPlugin().getDatabaseManager().updateAdminChunkTable(adminChunkTable);
    }
}
