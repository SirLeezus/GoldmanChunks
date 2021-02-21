package lee.code.chunks.database;

import lee.code.chunks.GoldmanChunks;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class SQLite {

    private Connection connection;
    private Statement statement;

    public void connect() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        connection = null;

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdir();
            }
            File dbFile = new File(plugin.getDataFolder(), "database.db");
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
            String url = "jdbc:sqlite:" + dbFile.getPath();

            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();

        } catch (IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void update(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public ResultSet getResult(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public void loadTables() {
        //chunk table
        update("CREATE TABLE IF NOT EXISTS chunks (" +
                "`chunk` varchar PRIMARY KEY," +
                "`owner` varchar NOT NULL," +
                "`trusted` varchar NOT NULL," +
                "`build` varchar NOT NULL," +
                "`break` varchar NOT NULL," +
                "`interact` varchar NOT NULL," +
                "`pve` varchar NOT NULL," +
                "`pvp` varchar NOT NULL," +
                "`monster_spawning` varchar NOT NULL," +
                "`explosions` varchar NOT NULL" +
                ");");

        //admin table
        update("CREATE TABLE IF NOT EXISTS admin_chunks (" +
                "`chunk` varchar PRIMARY KEY," +
                "`build` varchar NOT NULL," +
                "`break` varchar NOT NULL," +
                "`interact` varchar NOT NULL," +
                "`pve` varchar NOT NULL," +
                "`pvp` varchar NOT NULL," +
                "`monster_spawning` varchar NOT NULL," +
                "`explosions` varchar NOT NULL" +
                ");");

        //player table
        update("CREATE TABLE IF NOT EXISTS player_data (" +
                "`player` varchar PRIMARY KEY," +
                "`claimed` varchar NOT NULL," +
                "`bonus_claims` varchar NOT NULL," +
                "`accrued_claims` varchar NOT NULL," +
                "`trusted_global` varchar NOT NULL," +
                "`build` varchar NOT NULL," +
                "`break` varchar NOT NULL," +
                "`interact` varchar NOT NULL," +
                "`pve` varchar NOT NULL" +
                ");");
    }

    //TRUSTED DATA

    @SneakyThrows
    public boolean canTrustedBuild(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("build") == 1;
    }

    @SneakyThrows
    public boolean canTrustedBreak(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("break") == 1;
    }

    @SneakyThrows
    public boolean canTrustedInteract(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("interact") == 1;
    }

    @SneakyThrows
    public boolean canTrustedPVE(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("pve") == 1;
    }

    public void setChunkTrustedBuild(String chunk, int canBuild) {
        update("UPDATE chunks SET build ='" + canBuild + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrustedBreak(String chunk, int canBreak) {
        update("UPDATE chunks SET break ='" + canBreak + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrustedInteract(String chunk, int canInteract) {
        update("UPDATE chunks SET interact ='" + canInteract + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrustedPVE(String chunk, int canPVE) {
        update("UPDATE chunks SET pve ='" + canPVE + "' WHERE chunk ='" + chunk + "';");
    }

    @SneakyThrows
    public void addChunkTrusted(String chunk, UUID trust) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");

        if (!rs.getString("trusted").equals("n")) {
            String trusted = rs.getString("trusted") + "," + trust;
            update("UPDATE chunks SET trusted ='" + trusted + "' WHERE chunk ='" + chunk + "';");

        } else update("UPDATE chunks SET trusted ='" + trust + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrusted(String chunk, String trusted) {
        update("UPDATE chunks SET trusted ='" + trusted + "' WHERE chunk ='" + chunk + "';");
    }

    @SneakyThrows
    public boolean isChunkTrusted(String chunk, UUID trusted) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        if (rs.next()) {
            if (rs.getString("trusted").equals("n")) {
                return false;
            } else {
                String players = rs.getString("trusted");
                String[] split = StringUtils.split(players, ',');
                for (String player : split) {
                    if (UUID.fromString(player).equals(trusted)) return true;
                }
            }
        }
        return false;
    }

    @SneakyThrows
    public List<String> getTrustedToChunk(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        if (rs.next()) {
            String trusted = rs.getString("trusted");
            if (!trusted.equals("n")) {
                List<String> players = new ArrayList<>();
                String[] splitUUIDs = StringUtils.split(trusted, ',');
                for (String uuid : splitUUIDs) players.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                return players;
            }
        }
        return Collections.singletonList("");
    }

    //PLAYER CHUNK DATA

    public void claimChunk(String chunk, UUID uuid, int amount) {
        update("INSERT INTO chunks (chunk, owner, trusted, build, break, interact, pve, pvp, monster_spawning, explosions) VALUES( '" + chunk + "','" + uuid + "', 'n', '1', '1', '1', '1', '0', '0', '0');");
        update("UPDATE player_data SET claimed ='" + amount + "' WHERE player ='" + uuid + "';");
    }

    public void unclaimChunk(String chunk, UUID uuid, int claimAmount) {
        update("DELETE FROM chunks WHERE chunk = '" + chunk + "';");
        update("UPDATE player_data SET claimed ='" + claimAmount + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public boolean isChunkClaimed(String chunk) {
        ResultSet rs = getResult("SELECT chunk FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.next();
    }

    @SneakyThrows
    public String getChunkOwner(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        if (rs.next()) {
            String owner = rs.getString("owner");
            return Bukkit.getOfflinePlayer(UUID.fromString(owner)).getName();
        } else return "";
    }

    @SneakyThrows
    public UUID getChunkOwnerUUID(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return UUID.fromString(rs.getString("owner"));
    }

    @SneakyThrows
    public boolean isChunkOwner(String chunk, UUID uuid) {
        ResultSet rs = getResult("SELECT chunk FROM chunks WHERE chunk = '" + chunk + "' AND owner = '" + uuid + "';");
        return rs.next();
    }

    @SneakyThrows
    public boolean canChunkSpawnMonsters(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("monster_spawning") == 1;
    }

    @SneakyThrows
    public boolean canChunkExplode(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("explosions") == 1;
    }

    @SneakyThrows
    public boolean canChunkPVP(String chunk) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("pvp") == 1;
    }

    public void setChunkPVP(String chunk, int canPVP) {
        update("UPDATE chunks SET pvp ='" + canPVP + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkSpawnMonsters(String chunk, int canSpawnMonster) {
        update("UPDATE chunks SET monster_spawning ='" + canSpawnMonster + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkExplode(String chunk, int canExplode) {
        update("UPDATE chunks SET explosions ='" + canExplode + "' WHERE chunk ='" + chunk + "';");
    }

    public void unclaimAllChunks(UUID uuid) {
        update("DELETE FROM chunks WHERE owner = '" + uuid + "';");
        update("UPDATE player_data SET claimed ='" + 0 + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public List<String> getPlayerClaimedChunks(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE owner = '" + uuid + "';");

        if (rs.next()) {
            List<String> chunks = new ArrayList<>();
            chunks.add(rs.getString("chunk"));
            while (rs.next()) chunks.add(rs.getString("chunk"));
            return chunks;
        } else return Collections.singletonList("n");
    }

    //ADMIN CHUNK DATA

    public void claimAdminChunk(String chunk) {
        update("INSERT INTO admin_chunks (chunk, build, break, interact, pve, pvp, monster_spawning, explosions) VALUES( '" + chunk + "', '0', '0', '0', '0', '0', '0', '0');");
    }

    public void unClaimAdminChunk(String chunk) {
        update("DELETE FROM admin_chunks WHERE chunk = '" + chunk + "';");
    }

    @SneakyThrows
    public boolean isAdminChunk(String chunk) {
        ResultSet rsa = getResult("SELECT chunk FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rsa.next();
    }

    @SneakyThrows
    public boolean canAdminChunkBuild(String chunk) {
        ResultSet rs = getResult("SELECT * FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("build") == 1;
    }

    @SneakyThrows
    public boolean canAdminChunkBreak(String chunk) {
        ResultSet rs = getResult("SELECT * FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("break") == 1;
    }

    @SneakyThrows
    public boolean canAdminChunkInteract(String chunk) {
        ResultSet rs = getResult("SELECT * FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("interact") == 1;
    }

    @SneakyThrows
    public boolean canAdminChunkPVE(String chunk) {
        ResultSet rs = getResult("SELECT * FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("pve") == 1;
    }

    @SneakyThrows
    public boolean canAdminChunkPVP(String chunk) {
        ResultSet rs = getResult("SELECT * FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("pvp") == 1;
    }

    @SneakyThrows
    public boolean canAdminChunkSpawnMonsters(String chunk) {
        ResultSet rs = getResult("SELECT * FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("monster_spawning") == 1;
    }

    @SneakyThrows
    public boolean canAdminChunkExplode(String chunk) {
        ResultSet rs = getResult("SELECT * FROM admin_chunks WHERE chunk = '" + chunk + "';");
        return rs.getInt("explosions") == 1;
    }

    public void setAdminChunkBuild(String chunk, String canBuild) {
        update("UPDATE admin_chunks SET build ='" + canBuild + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkBreak(String chunk, String canBreak) {
        update("UPDATE admin_chunks SET break ='" + canBreak + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkInteract(String chunk, String canInteract) {
        update("UPDATE admin_chunks SET interact ='" + canInteract + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkPvE(String chunk, String canPVE) {
        update("UPDATE admin_chunks SET pve ='" + canPVE + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkPvP(String chunk, String canPVP) {
        update("UPDATE admin_chunks SET pvp ='" + canPVP + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkSpawnMonsters(String chunk, String canSpawnMonster) {
        update("UPDATE admin_chunks SET monster_spawning ='" + canSpawnMonster + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkExplosion(String chunk, String canExplode) {
        update("UPDATE admin_chunks SET explosions ='" + canExplode + "' WHERE chunk ='" + chunk + "';");
    }

    //PLAYER DATA

    @SneakyThrows
    public boolean hasPlayerData(UUID uuid) {
        ResultSet rs = getResult("SELECT player FROM player_data WHERE player = '" + uuid + "';");
        return rs.next();
    }

    @SneakyThrows
    public boolean hasClaimedChunks(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("claimed") != 0;
    }

    public void createPlayerData(UUID uuid) {
        update("INSERT INTO player_data (player, claimed, bonus_claims, accrued_claims, trusted_global, build, break, interact, pve) VALUES( '" + uuid + "', '0', '0', '0', 'n', '1', '1', '1', '1');");
    }

    @SneakyThrows
    private void addClaimedAmount(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        int claims = rs.getInt("claimed") + 1;
        update("UPDATE player_data SET claimed ='" + claims + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    private void subtractClaimedAmount(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        int claims = rs.getInt("claimed") - 1;
        update("UPDATE player_data SET claimed ='" + claims + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public int getClaimedAmount(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("claimed");
    }

    @SneakyThrows
    public boolean isGlobalTrusted(UUID uuid, UUID trust) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");

        String trusted = rs.getString("trusted_global");
        if (!trusted.equals("n")) {
            String[] splitUUIDs = StringUtils.split(trusted, ',');
            List<String> players = new ArrayList<>(Arrays.asList(splitUUIDs));
            return players.contains(trust.toString());
        } else return false;
    }

    public void setAccruedClaims(UUID uuid, String amount) {
        update("UPDATE player_data SET accrued_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public int getAccruedClaims(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("accrued_claims");
    }

    @SneakyThrows
    public void addBonusClaims(UUID uuid, int amount) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");

        if (rs.getInt("bonus_claims") != 0) {
            int newAmount = rs.getInt("bonus_claims") + amount;
            update("UPDATE player_data SET bonus_claims = '" + newAmount + "' WHERE player ='" + uuid + "';");
        } else update("UPDATE player_data SET bonus_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    public void setBonusClaims(UUID uuid, String amount) {
        update("UPDATE player_data SET bonus_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    public void removeBonusClaims(UUID uuid, int amount) {
        update("UPDATE player_data SET bonus_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public int getBonusClaims(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("bonus_claims");
    }

    public void setTrustedGlobal(String uuid, String trust) {
        update("UPDATE player_data SET trusted_global ='" + trust + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public List<String> getGlobalTrustedPlayers(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");

        String trusted = rs.getString("trusted_global");
        if (!trusted.equals("n")) {
            List<String> players = new ArrayList<>();
            String[] splitUUIDs = StringUtils.split(trusted, ',');
            for (String player : splitUUIDs) players.add(Bukkit.getOfflinePlayer(UUID.fromString(player)).getName());
            return players;
        } else return Collections.singletonList("");
    }

    @SneakyThrows
    public boolean canGlobalTrustedBreak(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("break") == 1;
    }

    @SneakyThrows
    public boolean canGlobalTrustedBuild(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("build") == 1;
    }

    @SneakyThrows
    public boolean canGlobalTrustedInteract(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("interact") == 1;
    }

    @SneakyThrows
    public boolean canGlobalTrustedPVE(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("pve") == 1;
    }

    public void setGlobalTrustedBuild(String uuid, String canBuild) {
        update("UPDATE player_data SET build ='" + canBuild + "' WHERE player ='" + uuid + "';");
    }

    public void setGlobalTrustedBreak(String uuid, String canBreak) {
        update("UPDATE player_data SET break ='" + canBreak + "' WHERE player ='" + uuid + "';");
    }

    public void setGlobalTrustedInteract(String uuid, String canInteract) {
        update("UPDATE player_data SET interact ='" + canInteract + "' WHERE player ='" + uuid + "';");
    }

    public void setGlobalTrustedPvE(String uuid, String canPVE) {
        update("UPDATE player_data SET pve ='" + canPVE + "' WHERE player ='" + uuid + "';");
    }

    public int getPlayerClaims(Player player) {
        int maxClaims = 100000000;
        int defaultClaims = 10;

        if (!player.isOp()) return defaultClaims;
        else return maxClaims;
    }

    public int getMaxPlayerClaims(Player player) {

        UUID uuid = player.getUniqueId();
        int maxClaims = 100000000;
        int defaultClaims = 10;
        int bonusClaims = getBonusClaims(uuid);
        int accruedClaims = getAccruedClaims(uuid);

        if (!player.isOp()) return defaultClaims + bonusClaims + accruedClaims;
        else return maxClaims;
    }

    public void loadChunks() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        try {
            ResultSet rs = getResult("SELECT * FROM chunks;");

            int count = 0;
            while (rs.next()) {
                String chunk = rs.getString("chunk");
                String uuid = rs.getString("owner");
                String trusted = rs.getString("trusted");
                String canTrustedBuild = rs.getString("build");
                String canTrustedBreak = rs.getString("break");
                String canTrustedInteract = rs.getString("interact");
                String canTrustedPvE = rs.getString("pve");
                String canPvP = rs.getString("pvp");
                String canSpawnMonsters = rs.getString("monster_spawning");
                String canExplode = rs.getString("explosions");
                cache.setChunk(chunk, uuid, trusted, canTrustedBuild, canTrustedBreak, canTrustedInteract, canTrustedPvE, canPvP, canSpawnMonsters, canExplode);
                count++;
            }
            System.out.println(plugin.getPU().format("&6Chunk Claims Loaded: &a" + count));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadPlayerData() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        try {
            ResultSet rs = getResult("SELECT * FROM player_data;");

            int count = 0;
            while (rs.next()) {
                String uuid = rs.getString("player");
                String claimed = rs.getString("claimed");
                String bonusClaimed = rs.getString("bonus_claims");
                String accruedClaims = rs.getString("accrued_claims");
                String trustedGlobal = rs.getString("trusted_global");
                String trustedGlobalBuild = rs.getString("build");
                String trustedGlobalBreak = rs.getString("break");
                String trustedGlobalInteract = rs.getString("interact");
                String trustedGlobalPvE = rs.getString("pve");
                cache.setPlayerData(uuid, claimed, bonusClaimed, accruedClaims, trustedGlobal, trustedGlobalBuild, trustedGlobalBreak, trustedGlobalInteract, trustedGlobalPvE);
                count++;
            }
            System.out.println(plugin.getPU().format("&6Players Loaded: &a" + count));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAdminChunks() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        try {
            ResultSet rs = getResult("SELECT * FROM admin_chunks;");

            int count = 0;
            while (rs.next()) {
                String chunk = rs.getString("chunk");
                String canBuild = rs.getString("build");
                String canBreak = rs.getString("break");
                String canInteract = rs.getString("interact");
                String canPvE = rs.getString("pve");
                String canPvP = rs.getString("pvp");
                String canSpawnMonsters = rs.getString("monster_spawning");
                String canExplode = rs.getString("explosions");
                cache.setAdminChunk(chunk, canBuild, canBreak, canInteract, canPvE, canPvP, canSpawnMonsters, canExplode);
                count++;
            }
            System.out.println(plugin.getPU().format("&6Admin Chunk Claims Loaded: &a" + count));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}