package lee.code.chunks.database;

import jedis.Jedis;
import jedis.JedisPool;
import jedis.Pipeline;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.lists.chunksettings.ChunkAdminSettings;
import lee.code.chunks.lists.chunksettings.ChunkSettings;
import lee.code.chunks.lists.chunksettings.ChunkTrustedGlobalSettings;
import lee.code.chunks.lists.chunksettings.ChunkTrustedSettings;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class Cache {

    //CHUNK DATA

    public void claimChunk(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            Pipeline pipe = jedis.pipelined();
            pipe.hset("chunk", chunk, sUUID);
            pipe.hset("chunkTrusted", chunk, "0");
            pipe.hset("chunkTrustedBuild", chunk, "1");
            pipe.hset("chunkTrustedBreak", chunk, "1");
            pipe.hset("chunkTrustedInteract", chunk, "1");
            pipe.hset("chunkTrustedPvE", chunk, "1");
            pipe.hset("chunkMonsters", chunk, "0");
            pipe.hset("chunkExplode", chunk, "0");
            pipe.hset("chunkPvP", chunk, "0");
            pipe.hset("chunkPrice", chunk, "0");
            pipe.sync();

            int newClaimAmount = Integer.parseInt(jedis.hget("claimed", sUUID)) + 1;
            String sNewClaimAmount = String.valueOf(newClaimAmount);
            jedis.hset("claimed", sUUID, sNewClaimAmount);
            addToChunkClaims(sUUID, chunk);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.claimChunk(chunk, uuid, sNewClaimAmount));
        }
    }

    public void setChunk(String chunk, String uuid, String trusted, String canTrustedBuild, String canTrustedBreak, String canTrustedInteract, String canTrustedPvE, String canPvP, String canSpawnMonsters, String canExplode, String chunkPrice) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            Pipeline pipe = jedis.pipelined();
            pipe.hset("chunk", chunk, uuid);
            pipe.hset("chunkTrusted", chunk, trusted);
            pipe.hset("chunkTrustedBuild", chunk, canTrustedBuild);
            pipe.hset("chunkTrustedBreak", chunk, canTrustedBreak);
            pipe.hset("chunkTrustedInteract", chunk, canTrustedInteract);
            pipe.hset("chunkTrustedPvE", chunk, canTrustedPvE);
            pipe.hset("chunkPvP", chunk, canPvP);
            pipe.hset("chunkMonsters", chunk, canSpawnMonsters);
            pipe.hset("chunkExplode", chunk, canExplode);
            pipe.hset("chunkPrice", chunk, chunkPrice);
            pipe.sync();

            addToChunkClaims(uuid, chunk);
        }
    }

    public boolean isChunkForSale(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            return !jedis.hget("chunkPrice", chunk).equals("0");
        }
    }

    public void setChunkPrice(String chunk, long price) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sPrice = String.valueOf(price);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkPrice", chunk, sPrice);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkPrice(chunk, sPrice));
        }
    }

    public void setChunkOwner(String chunk, UUID oldOwner, UUID newOwner) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sNewUUID = String.valueOf(newOwner);
        String sOldUUID = String.valueOf(oldOwner);

        try (Jedis jedis = pool.getResource()) {

            int newClaimAmount = Integer.parseInt(jedis.hget("claimed", sNewUUID)) + 1;
            String sNewClaimAmount = String.valueOf(newClaimAmount);
            jedis.hset("claimed", sNewUUID, sNewClaimAmount);
            addToChunkClaims(sNewUUID, chunk);

            int oldClaimAmount = Integer.parseInt(jedis.hget("claimed", sOldUUID)) - 1;
            String sOldClaimAmount = String.valueOf(oldClaimAmount);
            jedis.hset("claimed", sOldUUID, sOldClaimAmount);
            removeFromChunkClaims(sOldUUID, chunk);

            jedis.hset("chunk", chunk, sNewUUID);
            jedis.hset("chunkPrice", chunk, "0");
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                SQL.setChunkOwner(chunk, sNewUUID);
                SQL.setClaimedAmount(sNewUUID, sNewClaimAmount);
                SQL.setClaimedAmount(sOldUUID, sOldClaimAmount);
            });
        }
    }

    public long getChunkPrice(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            return Long.parseLong(jedis.hget("chunkPrice", chunk));
        }
    }

    public List<String> getChunkClaims(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            if (jedis.hexists("playerChunkList", sUUID)) {
                String[] split = StringUtils.split(jedis.hget("playerChunkList", sUUID), '%');
                return new ArrayList<>(Arrays.asList(split));
            } else return Collections.singletonList("");
        }
    }

    private void addToChunkClaims(String uuid, String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {

            if (jedis.hexists("playerChunkList", sUUID)) {
                String newChunkList = jedis.hget("playerChunkList", sUUID) + "%" + chunk;
                jedis.hset("playerChunkList", sUUID, newChunkList);
            } else jedis.hset("playerChunkList", sUUID, chunk);
        }
    }

    private void removeFromChunkClaims(String uuid, String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            List<String> newList = new ArrayList<>();
            String[] split = StringUtils.split(jedis.hget("playerChunkList", sUUID), '%');
            for (String playerChunk : split) if (!playerChunk.equals(chunk)) newList.add(playerChunk);
            String newChunkList = StringUtils.join(newList, "%");
            jedis.hset("playerChunkList", sUUID, newChunkList);
        }
    }

    public void unclaimChunk(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hdel("chunk", chunk);
            int newClaimAmount = Integer.parseInt(jedis.hget("claimed", sUUID)) - 1;
            String sNewClaimAmount = String.valueOf(newClaimAmount);
            jedis.hset("claimed", sUUID, sNewClaimAmount);

            removeFromChunkClaims(sUUID, chunk);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.unclaimChunk(chunk, uuid, sNewClaimAmount));
        }
    }

    public boolean isChunkClaimed(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            return jedis.hexists("chunk", chunk);
        }
    }

    public boolean isChunkOwner(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            return jedis.hget("chunk", chunk).equals(sUUID);
        }
    }

    public String getChunkOwnerName(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            UUID uuid = UUID.fromString(jedis.hget("chunk", chunk));
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }

    public UUID getChunkOwnerUUID(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            return UUID.fromString(jedis.hget("chunk", chunk));
        }
    }

    public void unclaimAllChunks(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("claimed", sUUID, "0");

            List<String> chunks = getChunkClaims(uuid);
            for (String chunk : chunks) jedis.hdel("chunk", chunk);

            jedis.hdel("playerChunkList", sUUID);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.unclaimAllChunks(sUUID));
        }
    }

    public boolean isChunkTrusted(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("chunkTrusted", chunk);
            if (!players.equals("0")) {
                String[] split = StringUtils.split(players, ',');
                for (String player : split) if (player.equals(sUUID)) return true;
            }
            return false;
        }
    }

    public List<String> getChunkTrustedNames(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("chunkTrusted", chunk);
            if (!players.equals("0")) {
                List<String> trusted = new ArrayList<>();
                String[] split = StringUtils.split(players, ',');
                for (String uuid : split) trusted.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                return trusted;
            } else return Collections.singletonList("");
        }
    }

    public void removeChunkTrusted(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("chunkTrusted", chunk);
            if (!players.equals("0")) {
                List<String> trusted = new ArrayList<>();
                String[] split = StringUtils.split(players, ',');
                for (String player : split) if (!player.equals(sUUID)) trusted.add(player);
                String newTrusted = StringUtils.join(trusted, ",");
                jedis.hset("chunkTrusted", chunk, newTrusted);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrusted(chunk, newTrusted));
            }
        }
    }

    public void addChunkTrusted(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("chunkTrusted", chunk);

            String trusted;
            if (players.equals("0")) trusted = sUUID;
            else trusted = players + "," + sUUID;

            jedis.hset("chunkTrusted", chunk, trusted);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrusted(chunk, trusted));
        }
    }

    public boolean canChunkTrustedSetting(ChunkTrustedSettings setting, String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget(setting.getRedisKey(), chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkTrustedSetting(ChunkTrustedSettings setting, String chunk, boolean value) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (value) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset(setting.getRedisKey(), chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkSetting(setting.getSqliteKey(), chunk, result));
        }
    }

    public boolean canChunkSetting(ChunkSettings setting, String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget(setting.getRedisKey(), chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkSetting(ChunkSettings setting, String chunk, boolean value) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (value) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset(setting.getRedisKey(), chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkSetting(setting.getSqliteKey(), chunk, result));
        }
    }

    public boolean isChunkFlying(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = uuid.toString();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkFlying", sUUID);
            return !flag.equals("0");
        }
    }

    public void setChunkFlying(UUID uuid, boolean canFly) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canFly) result = "1"; else result = "0";

        String sUUID = uuid.toString();

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkFlying", sUUID, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkFlying(sUUID, result));
        }
    }

    //PLAYER TABLE DATA

    public void createPlayerData(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {

            Pipeline pipe = jedis.pipelined();
            pipe.hset("claimed", sUUID, "0");
            pipe.hset("bonusClaims", sUUID, "0");
            pipe.hset("accruedClaims", sUUID, "0");
            pipe.hset("trustedGlobal", sUUID, "0");
            pipe.hset("trustedGlobalBuild", sUUID, "1");
            pipe.hset("trustedGlobalBreak", sUUID, "1");
            pipe.hset("trustedGlobalInteract", sUUID, "1");
            pipe.hset("trustedGlobalPvE", sUUID, "1");
            pipe.hset("chunkFlying", sUUID, "0");
            pipe.sync();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.createPlayerData(sUUID));
        }
    }

    public void setPlayerData(String uuid, String claimed, String bonusClaims, String accruedClaims, String trustedGlobal, String trustedGlobalBuild, String trustedGlobalBreak, String trustedGlobalInteract, String trustedGlobalPvE, String flying) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            Pipeline pipe = jedis.pipelined();
            pipe.hset("claimed", uuid, claimed);
            pipe.hset("bonusClaims", uuid, bonusClaims);
            pipe.hset("accruedClaims", uuid, accruedClaims);
            pipe.hset("trustedGlobal", uuid, trustedGlobal);
            pipe.hset("trustedGlobalBuild", uuid, trustedGlobalBuild);
            pipe.hset("trustedGlobalBreak", uuid, trustedGlobalBreak);
            pipe.hset("trustedGlobalInteract", uuid, trustedGlobalInteract);
            pipe.hset("trustedGlobalPvE", uuid, trustedGlobalPvE);
            pipe.hset("chunkFlying", uuid, flying);
            pipe.sync();
        }
    }

    public boolean hasPlayerData(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            return jedis.hexists("claimed", sUUID);
        }
    }

    public boolean hasClaimedChunks(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            int amount = Integer.parseInt(jedis.hget("claimed", sUUID));
            return amount != 0;
        }
    }

    public int getPlayerMaxClaimAmount(UUID uuid) {
        int maxClaims = 100000000;
        int defaultClaims = 10;
        int bonusClaims = getBonusClaimsAmount(uuid);
        int accruedClaims = getAccruedClaimsAmount(uuid);

        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (!player.isOp()) return defaultClaims + bonusClaims + accruedClaims;
        else return maxClaims;
    }

    public int getPlayerDefaultClaimAmount(Player player) {
        int maxClaims = 100000000;
        int defaultClaims = 10;

        if (!player.isOp()) return defaultClaims;
        else return maxClaims;
    }

    public int getBonusClaimsAmount(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            return Integer.parseInt(jedis.hget("bonusClaims", sUUID));
        }
    }

    public void removeBonusClaimsAmount(UUID uuid, int amount) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String newAmount = String.valueOf(Integer.parseInt(jedis.hget("bonusClaims", sUUID)) - amount);
            jedis.hset("bonusClaims", sUUID, newAmount);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setBonusClaims(sUUID, newAmount));
        }
    }

    public void addBonusClaimsAmount(UUID uuid, int amount) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String newAmount = String.valueOf(Integer.parseInt(jedis.hget("bonusClaims", sUUID)) + amount);
            jedis.hset("bonusClaims", sUUID, newAmount);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setBonusClaims(sUUID, newAmount));
        }
    }

    public void setBonusClaimsAmount(UUID uuid, int amount) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);
        String sAmount = String.valueOf(amount);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("bonusClaims", sUUID, sAmount);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setBonusClaims(sUUID, sAmount));
        }
    }

    public int getAccruedClaimsAmount(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            return Integer.parseInt(jedis.hget("accruedClaims", sUUID));
        }
    }

    public void setAccruedClaimsAmount(UUID uuid, int amount) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);
        String sAmount = String.valueOf(amount);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("accruedClaims", sUUID, sAmount);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAccruedClaims(sUUID, sAmount));
        }
    }

    public int getClaimedAmount(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            return Integer.parseInt(jedis.hget("claimed", sUUID));
        }
    }

    public boolean isGlobalTrusted(UUID uuid, UUID trusted) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);
        String sTrusted = String.valueOf(trusted);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("trustedGlobal", sUUID);
            if (!players.equals("0")) {
                String[] split = StringUtils.split(players, ',');
                for (String player : split) if (player.equals(sTrusted)) return true;
            }
            return false;
        }
    }

    public void addGlobalTrusted(UUID uuid, UUID trusted) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);
        String sTrusted = String.valueOf(trusted);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("trustedGlobal", sUUID);

            String newTrusted;
            if (players.equals("0")) newTrusted = sTrusted;
            else newTrusted = players + "," + sTrusted;

            jedis.hset("trustedGlobal", sUUID, newTrusted);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setTrustedGlobal(sUUID, newTrusted));
        }
    }

    public List<String> getGlobalTrustedNames(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String trusted = jedis.hget("trustedGlobal", sUUID);

            if (!trusted.equals("0")) {
                List<String> players = new ArrayList<>();
                String[] split = StringUtils.split(trusted, ',');
                for (String player : split) players.add(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName());
                return players;
            } else return Collections.singletonList("");
        }
    }

    public void removeGlobalTrusted(UUID uuid, UUID trusted) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);
        String sTrusted = String.valueOf(trusted);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("trustedGlobal", sUUID);
            if (!players.equals("0")) {

                List<String> trustedList = new ArrayList<>();
                String[] split = StringUtils.split(players, ',');
                for (String player : split) if (!player.equals(sTrusted)) trustedList.add(player);
                String newTrusted = StringUtils.join(trustedList, ",");
                jedis.hset("trustedGlobal", sUUID, newTrusted);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setTrustedGlobal(sUUID, newTrusted));
            }
        }
    }

    public boolean canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings setting, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);
        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget(setting.getRedisKey(), sUUID);
            return !flag.equals("0");
        }
    }

    public void setChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings setting, UUID uuid, boolean value) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (value) result = "1"; else result = "0";
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset(setting.getRedisKey(), sUUID, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setTrustedGlobalSetting(setting.getSqliteKey(), sUUID, result));
        }
    }

    //ADMIN CHUNK DATA

    public int updateBulkAdminChunks(List<String> chunks, ChunkAdminSettings setting, boolean value) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String result; if (value) result = "1"; else result = "0";

        List<String> updatedChunks = new ArrayList<>();
        try (Jedis jedis = pool.getResource()) {
            for (String chunk : chunks) {
                if (jedis.hexists(setting.getRedisKey(), chunk)) {
                    jedis.hset(setting.getRedisKey(), chunk, result);
                    updatedChunks.add(chunk);
                }
            }
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.updateBulkAdminChunksSetting(updatedChunks, setting.getSqliteKey(), result));
        }
        return updatedChunks.size();
    }

    public void claimBulkAdminChunks(List<String> chunks) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {

            for (String chunk : chunks) {
                Pipeline pipe = jedis.pipelined();
                pipe.hset("adminChunkBuild", chunk, "0");
                pipe.hset("adminChunkBreak", chunk, "0");
                pipe.hset("adminChunkInteract", chunk, "0");
                pipe.hset("adminChunkPvE", chunk, "0");
                pipe.hset("adminChunkPvP", chunk, "0");
                pipe.hset("adminChunkMonsters", chunk, "0");
                pipe.hset("adminChunkExplode", chunk, "0");
                pipe.sync();
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.claimBulkAdminChunks(chunks));
        }
    }


    public void claimAdminChunk(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            Pipeline pipe = jedis.pipelined();
            pipe.hset("adminChunkBuild", chunk, "0");
            pipe.hset("adminChunkBreak", chunk, "0");
            pipe.hset("adminChunkInteract", chunk, "0");
            pipe.hset("adminChunkPvE", chunk, "0");
            pipe.hset("adminChunkPvP", chunk, "0");
            pipe.hset("adminChunkMonsters", chunk, "0");
            pipe.hset("adminChunkExplode", chunk, "0");
            pipe.sync();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.claimAdminChunk(chunk));
        }
    }

    public void setAdminChunk(String chunk, String canBuild, String canBreak, String canInteract, String canPvE, String canPvP, String canSpawnMonsters, String canExplode) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            Pipeline pipe = jedis.pipelined();
            pipe.hset("adminChunkBuild", chunk, canBuild);
            pipe.hset("adminChunkBreak", chunk, canBreak);
            pipe.hset("adminChunkInteract", chunk, canInteract);
            pipe.hset("adminChunkPvE", chunk, canPvE);
            pipe.hset("adminChunkPvP", chunk, canPvP);
            pipe.hset("adminChunkMonsters", chunk, canSpawnMonsters);
            pipe.hset("adminChunkExplode", chunk, canExplode);
            pipe.sync();
        }
    }

    public void unclaimBulkAdminChunk(List<String> chunks) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {

            for (String chunk : chunks) {
                jedis.hdel("adminChunkBuild", chunk);
                jedis.hdel("adminChunkBreak", chunk);
                jedis.hdel("adminChunkInteract", chunk);
                jedis.hdel("adminChunkPvE", chunk);
                jedis.hdel("adminChunkPvP", chunk);
                jedis.hdel("adminChunkMonsters", chunk);
                jedis.hdel("adminChunkExplode", chunk);
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.unclaimBulkAdminChunks(chunks));
        }
    }

    public void unclaimAdminChunk(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            jedis.hdel("adminChunk", chunk);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.unclaimAdminChunk(chunk));
        }
    }

    public boolean isAdminChunk(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            return jedis.hexists("adminChunkBuild", chunk);
        }
    }

    public boolean canAdminChunkSetting(ChunkAdminSettings setting, String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget(setting.getRedisKey(), chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkSetting(ChunkAdminSettings setting, String chunk, boolean value) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (value) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset(setting.getRedisKey(), chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkSetting(setting.getSqliteKey(), chunk, result));
        }
    }
}
