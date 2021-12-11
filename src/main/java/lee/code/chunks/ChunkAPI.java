package lee.code.chunks;

import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.chunksettings.ChunkTrustedGlobalSettings;
import lee.code.chunks.lists.chunksettings.ChunkTrustedSettings;
import org.bukkit.Chunk;

import java.util.List;
import java.util.UUID;

public class ChunkAPI {

    public List<UUID> getUserList() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        return cache.getUserList();
    }

    public List<String> getChunks(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        return cache.getChunkClaims(uuid);
    }

    public boolean isClaimed(Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        return cache.isChunkClaimed(chunkCord);
    }

    public boolean isAdminChunk(Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        return cache.isAdminChunk(chunkCord);
    }

    public boolean isChunkOwner(Chunk chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        return cache.isChunkOwner(chunkCord, uuid);
    }

    public UUID getChunkOwnerUUID(Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        return cache.getChunkOwnerUUID(chunkCord);
    }


    public boolean isChunkTrusted(Chunk chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        return cache.isChunkTrusted(chunkCord, uuid);
    }

    public boolean isGlobalTrusted(UUID owner, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        return cache.isGlobalTrusted(owner, uuid);
    }

    public boolean canChunkTrustedSetting(ChunkTrustedSettings setting, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        return cache.canChunkTrustedSetting(setting, chunkCord);
    }

    public boolean canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings setting, UUID owner) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        return cache.canChunkTrustedGlobalSetting(setting, owner);
    }

    public boolean canBreakInChunk(UUID uuid, Chunk chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        Data data = plugin.getData();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        if (cache.isChunkClaimed(chunkCord)) {
            UUID oUUID = cache.getChunkOwnerUUID(chunkCord);
            if (cache.isChunkOwner(chunkCord, uuid)) return true;
            else if (cache.isChunkTrusted(chunkCord, uuid)) return cache.canChunkTrustedSetting(ChunkTrustedSettings.BREAK, chunkCord);
            else if (cache.isGlobalTrusted(oUUID, uuid)) return cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.BREAK, oUUID);
            else return data.hasAdminBypass(uuid);
        } else if (cache.isAdminChunk(chunkCord)) return data.hasAdminBypass(uuid);
        return true;
    }
}
