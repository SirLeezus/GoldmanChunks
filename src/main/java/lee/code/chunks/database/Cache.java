package lee.code.chunks.database;

import lee.code.cache.jedis.*;
import lee.code.chunks.GoldmanChunks;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Cache {

    //CHUNK TABLE METHODS

    public void claimChunk(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            Pipeline pipe = jedis.pipelined();
            pipe.hset("chunk", chunk, sUUID);
            pipe.hset("chunkTrusted", chunk, "n");
            pipe.hset("chunkTrustedBuild", chunk, "1");
            pipe.hset("chunkTrustedBreak", chunk, "1");
            pipe.hset("chunkTrustedInteract", chunk, "1");
            pipe.hset("chunkTrustedPvE", chunk, "1");
            pipe.hset("chunkMonsters", chunk, "0");
            pipe.hset("chunkExplode", chunk, "0");
            pipe.hset("chunkPvP", chunk, "0");
            pipe.sync();

            int newClaimAmount = Integer.parseInt(jedis.hget("claimed", sUUID)) + 1;
            String sNewClaimAmount = String.valueOf(newClaimAmount);
            jedis.hset("claimed", sUUID, sNewClaimAmount);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.claimChunk(chunk, uuid, sNewClaimAmount));
        }
    }

    public void setChunk(String chunk, String uuid, String trusted, String canTrustedBuild, String canTrustedBreak, String canTrustedInteract, String canTrustedPvE, String canPvP, String canSpawnMonsters, String canExplode) {
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
            pipe.sync();
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
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.unclaimChunk(chunk, uuid, sNewClaimAmount));
        }
    }

    public boolean isChunkClaimed(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            return jedis.hexists("chunk", chunk) || jedis.hexists("adminChunkBuild", chunk) ;
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

    //TODO test when get dedicated
    public void unclaimAllChunks(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            List<Map.Entry<String, String>> chunks = scanHSet("chunk", sUUID);
            System.out.println(chunks);
        }
    }

    //TODO test when get dedicated
    private List<Map.Entry<String, String>> scanHSet(String hashMap, String match) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        try (Jedis jedis = pool.getResource()) {
            int cursor = 0;

            ScanParams scanParams = new ScanParams();
            scanParams.match(match);
            ScanResult<Map.Entry<String, String>> scanResult;
            List<Map.Entry<String, String>> list = new ArrayList<>();
            do {
                scanResult = jedis.hscan(hashMap, String.valueOf(cursor), scanParams);
                list.addAll(scanResult.getResult());
                cursor = Integer.parseInt(scanResult.getCursor());
            } while (cursor > 0);
            return list;
        }
    }

    //TODO get chunk claims when able to use scan
    public List<String> getChunkClaims(UUID uuid) {
        return null;
    }

    public boolean isChunkTrusted(String chunk, UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("chunkTrusted", chunk);
            if (!players.equals("n")) {
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
            if (!players.equals("n")) {
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
            if (!players.equals("n")) {
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
            if (players.equals("n")) trusted = sUUID;
            else trusted = players + "," + sUUID;

            jedis.hset("chunkTrusted", chunk, trusted);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrusted(chunk, trusted));
        }
    }

    public boolean canChunkTrustedBuild(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkTrustedBuild", chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkTrustedBuild(String chunk, boolean canBuild) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canBuild) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedBuild", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedBuild(chunk, result));
        }
    }

    public boolean canChunkTrustedBreak(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkTrustedBreak", chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkTrustedBreak(String chunk, boolean canBreak) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canBreak) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedBreak", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedBreak(chunk, result));
        }
    }

    public boolean canChunkTrustedInteract(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkTrustedInteract", chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkTrustedInteract(String chunk, boolean canInteract) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canInteract) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedInteract", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedInteract(chunk, result));
        }
    }

    public boolean canChunkTrustedPvE(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkTrustedPvE", chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkTrustedPvE(String chunk, boolean canPvE) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canPvE) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedPvE", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedPVE(chunk, result));
        }
    }

    public boolean canChunkSpawnMonsters(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkMonsters", chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkSpawnMonsters(String chunk, boolean canSpawnMonsters) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canSpawnMonsters) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkMonsters", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkSpawnMonsters(chunk, result));
        }
    }

    public boolean canChunkExplode(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkExplode", chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkExplode(String chunk, boolean canExplode) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canExplode) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkExplode", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkExplode(chunk, result));
        }
    }

    public boolean canChunkPvP(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("chunkPvP", chunk);
            return !flag.equals("0");
        }
    }

    public void setChunkPvP(String chunk, boolean canPvP) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canPvP) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkPvP", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkPVP(chunk, result));
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
            pipe.hset("trustedGlobal", sUUID, "n");
            pipe.hset("trustedGlobalBuild", sUUID, "1");
            pipe.hset("trustedGlobalBreak", sUUID, "1");
            pipe.hset("trustedGlobalInteract", sUUID, "1");
            pipe.hset("trustedGlobalPvE", sUUID, "1");
            pipe.sync();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.createPlayerData(sUUID));
        }
    }

    public void setPlayerData(String uuid, String claimed, String bonusClaims, String accruedClaims, String trustedGlobal, String trustedGlobalBuild, String trustedGlobalBreak, String trustedGlobalInteract, String trustedGlobalPvE) {
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

    public int getPlayerMaxClaimAmount(Player player) {
        UUID uuid = player.getUniqueId();
        int maxClaims = 100000000;
        int defaultClaims = 10;
        int bonusClaims = getBonusClaimsAmount(uuid);
        int accruedClaims = getAccruedClaimsAmount(uuid);

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
            if (!players.equals("n")) {
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
            if (players.equals("n")) newTrusted = sTrusted;
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

            if (!trusted.equals("n")) {
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
            if (!players.equals("n")) {

                List<String> trustedList = new ArrayList<>();
                String[] split = StringUtils.split(players, ',');
                for (String player : split) if (!player.equals(sTrusted)) trustedList.add(player);
                String newTrusted = StringUtils.join(trustedList, ",");
                jedis.hset("trustedGlobal", sUUID, newTrusted);

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setTrustedGlobal(sUUID, newTrusted));
            }
        }
    }

    public boolean canGlobalTrustedBuild(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("trustedGlobalBuild", sUUID);
            return !flag.equals("0");
        }
    }

    public void setGlobalTrustedBuild(UUID uuid, boolean canBuild) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (canBuild) result = "1"; else result = "0";
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("trustedGlobalBuild", sUUID, result);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setGlobalTrustedBuild(sUUID, result));
        }
    }

    public boolean canGlobalTrustedBreak(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("trustedGlobalBreak", sUUID);
            return !flag.equals("0");
        }
    }

    public void setGlobalTrustedBreak(UUID uuid, boolean canBreak) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (canBreak) result = "1"; else result = "0";
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("trustedGlobalBreak", sUUID, result);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setGlobalTrustedBreak(sUUID, result));
        }
    }

    public boolean canGlobalTrustedInteract(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("trustedGlobalInteract", sUUID);
            return !flag.equals("0");
        }
    }

    public void setGlobalTrustedInteract(UUID uuid, boolean canInteract) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (canInteract) result = "1"; else result = "0";
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("trustedGlobalInteract", sUUID, result);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setGlobalTrustedInteract(sUUID, result));
        }
    }

    public boolean canGlobalTrustedPvE(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("trustedGlobalPvE", sUUID);
            return !flag.equals("0");
        }
    }

    public void setGlobalTrustedPvE(UUID uuid, boolean canPvE) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String result; if (canPvE) result = "1"; else result = "0";
        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("trustedGlobalPvE", sUUID, result);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setGlobalTrustedPvE(sUUID, result));
        }
    }

    //ADMIN CHUNK METHODS


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

    public boolean canAdminChunkBuild(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("adminChunkBuild", chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkBuild(String chunk, boolean canBuild) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canBuild) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("adminChunkBuild", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkBuild(chunk, result));
        }
    }

    public boolean canAdminChunkBreak(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("adminChunkBreak", chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkBreak(String chunk, boolean canBreak) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canBreak) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("adminChunkBreak", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkBreak(chunk, result));
        }
    }

    public boolean canAdminChunkInteract(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("adminChunkInteract", chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkInteract(String chunk, boolean canInteract) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canInteract) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("adminChunkInteract", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkInteract(chunk, result));
        }
    }

    public boolean canAdminChunkPvE(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("adminChunkPvE", chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkPvE(String chunk, boolean canPvE) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canPvE) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("adminChunkPvE", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkPvE(chunk, result));
        }
    }

    public boolean canAdminChunkPvP(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("adminChunkPvP", chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkPvP(String chunk, boolean canPvP) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canPvP) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("adminChunkPvP", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkPvP(chunk, result));
        }
    }

    public boolean canAdminChunkSpawnMonsters(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("adminChunkMonsters", chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkSpawnMonsters(String chunk, boolean canSpawnMonsters) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canSpawnMonsters) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("adminChunkPvP", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkSpawnMonsters(chunk, result));
        }
    }

    public boolean canAdminChunkExplode(String chunk) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        try (Jedis jedis = pool.getResource()) {
            String flag = jedis.hget("adminChunkExplode", chunk);
            return !flag.equals("0");
        }
    }

    public void setAdminChunkExplode(String chunk, boolean canExplode) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canExplode) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("adminChunkExplode", chunk, result);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setAdminChunkExplosion(chunk, result));
        }
    }
}
