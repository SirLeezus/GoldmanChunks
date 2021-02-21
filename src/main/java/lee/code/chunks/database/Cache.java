package lee.code.chunks.database;

import lee.code.cache.jedis.*;
import lee.code.chunks.GoldmanChunks;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.*;

public class Cache {

    //CHUNK TABLE METHODS

    public void claimChunk(String chunk, UUID uuid, boolean sql) {
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

            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.claimChunk(chunk, uuid));
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

    public void unclaimChunk(String chunk, UUID uuid, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            jedis.hdel("chunk", chunk);
            int newClaimAmount = Integer.parseInt(jedis.hget("claimAmount", sUUID)) - 1;
            jedis.hset("claimAmount", sUUID, String.valueOf(newClaimAmount));

            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.unclaimChunk(chunk, uuid, newClaimAmount));
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

    //TODO test when get dedicated
    public void removeAllChunks(UUID uuid, boolean sql) {
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

    public void removeChunkTrusted(String chunk, UUID uuid, boolean sql) {
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

                if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrusted(chunk, newTrusted));
            }
        }
    }

    public void addChunkTrusted(String chunk, UUID uuid, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();

        String sUUID = String.valueOf(uuid);

        try (Jedis jedis = pool.getResource()) {
            String players = jedis.hget("chunkTrusted", chunk);

            String trusted;
            if (players.equals("n")) {
                trusted = sUUID;
            } else {
                String[] split = StringUtils.split(players, ',');
                trusted = StringUtils.join(split, ",") + "," + sUUID;
            }

            jedis.hset("chunkTrusted", chunk, trusted);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrusted(chunk, trusted));
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

    public void setChunkTrustedBuild(String chunk, boolean canBuild, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canBuild) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedBuild", chunk, result);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedBuild(chunk, Integer.parseInt(result)));
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

    public void setChunkTrustedBreak(String chunk, boolean canBreak, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canBreak) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedBreak", chunk, result);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedBreak(chunk, Integer.parseInt(result)));
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

    public void setChunkTrustedInteract(String chunk, boolean canInteract, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canInteract) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedInteract", chunk, result);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedInteract(chunk, Integer.parseInt(result)));
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

    public void setChunkTrustedPvE(String chunk, boolean canPvE, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canPvE) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkTrustedPvE", chunk, result);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkTrustedPVE(chunk, Integer.parseInt(result)));
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

    public void setChunkSpawnMonsters(String chunk, boolean canSpawnMonsters, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canSpawnMonsters) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkMonsters", chunk, result);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkSpawnMonsters(chunk, Integer.parseInt(result)));
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

    public void setChunkExplode(String chunk, boolean canExplode, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canExplode) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkExplode", chunk, result);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkExplode(chunk, Integer.parseInt(result)));
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

    public void setChunkPvP(String chunk, boolean canPvP, boolean sql) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        JedisPool pool = plugin.getCacheAPI().getChunksPool();
        SQLite SQL = plugin.getSqLite();
        String result; if (canPvP) result = "1"; else result = "0";

        try (Jedis jedis = pool.getResource()) {
            jedis.hset("chunkPvP", chunk, result);
            if (sql) Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> SQL.setChunkPVP(chunk, Integer.parseInt(result)));
        }
    }

    //PLAYER TABLE DATA

}
