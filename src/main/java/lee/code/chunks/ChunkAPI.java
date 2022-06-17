package lee.code.chunks;

import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.chunksettings.ChunkTrustedGlobalSetting;
import lee.code.chunks.lists.chunksettings.ChunkTrustedSetting;
import org.bukkit.Chunk;

import java.util.List;
import java.util.UUID;

public class ChunkAPI {

    public boolean hasClaims(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        return cacheManager.hasClaimedChunks(uuid);
    }

    public List<UUID> getUserList() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        return cacheManager.getUserList();
    }

    public List<String> getChunks(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        return cacheManager.getChunkClaims(uuid);
    }

    public List<String> getAdminChunks() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        return cacheManager.getAdminChunkClaims();
    }

    public boolean isClaimed(Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        return cacheManager.isChunkClaimed(chunkCord);
    }

    public boolean isAdminChunk(Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        return cacheManager.isAdminChunk(chunkCord);
    }

    public boolean isChunkOwner(Chunk chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        return cacheManager.isChunkOwner(chunkCord, uuid);
    }

    public UUID getChunkOwnerUUID(Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        return cacheManager.getChunkOwnerUUID(chunkCord);
    }


    public boolean isChunkTrusted(Chunk chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        return cacheManager.isChunkTrusted(chunkCord, uuid);
    }

    public boolean isGlobalTrusted(UUID owner, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        return cacheManager.isGlobalTrusted(owner, uuid);
    }

    public boolean canChunkTrustedSetting(ChunkTrustedSetting setting, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        return cacheManager.canChunkTrustedSetting(setting, chunkCord);
    }

    public boolean canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting setting, UUID owner) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        return cacheManager.canChunkTrustedGlobalSetting(setting, owner);
    }

    public boolean canBreakInChunk(UUID uuid, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        Data data = plugin.getData();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        if (cacheManager.isChunkClaimed(chunkCord)) {
            UUID oUUID = cacheManager.getChunkOwnerUUID(chunkCord);
            if (cacheManager.isChunkOwner(chunkCord, uuid)) return true;
            else if (cacheManager.isChunkTrusted(chunkCord, uuid)) return cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BREAK, chunkCord);
            else if (cacheManager.isGlobalTrusted(oUUID, uuid)) return cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.BREAK, oUUID);
            else return data.hasAdminBypass(uuid);
        } else if (cacheManager.isAdminChunk(chunkCord)) return data.hasAdminBypass(uuid);
        return true;
    }

    public boolean canInteractInChunk(UUID uuid, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        Data data = plugin.getData();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        if (cacheManager.isChunkClaimed(chunkCord)) {
            UUID oUUID = cacheManager.getChunkOwnerUUID(chunkCord);
            if (cacheManager.isChunkOwner(chunkCord, uuid)) return true;
            else if (cacheManager.isChunkTrusted(chunkCord, uuid)) return cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord);
            else if (cacheManager.isGlobalTrusted(oUUID, uuid)) return cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.INTERACT, oUUID);
            else return data.hasAdminBypass(uuid);
        } else if (cacheManager.isAdminChunk(chunkCord)) return data.hasAdminBypass(uuid);
        return true;
    }
}
