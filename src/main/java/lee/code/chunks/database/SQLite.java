package lee.code.chunks.database;

import lee.code.chunks.GoldmanChunks;
import lombok.SneakyThrows;

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
            statement.executeUpdate(sql);
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
                "`explosions` varchar NOT NULL," +
                "`price` varchar NOT NULL" +
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

    //CHUNKS TABLE

    public void claimChunk(String chunk, UUID uuid, String amount) {
        update("INSERT OR REPLACE INTO chunks (chunk, owner, trusted, build, break, interact, pve, pvp, monster_spawning, explosions, price) VALUES( '" + chunk + "','" + uuid + "', 'n', '1', '1', '1', '1', '0', '0', '0', '0');");
        update("UPDATE player_data SET claimed ='" + amount + "' WHERE player ='" + uuid + "';");
    }

    public void unclaimChunk(String chunk, UUID uuid, String claimAmount) {
        update("DELETE FROM chunks WHERE chunk = '" + chunk + "';");
        update("UPDATE player_data SET claimed ='" + claimAmount + "' WHERE player ='" + uuid + "';");
    }

    public void setChunkTrustedBuild(String chunk, String canBuild) {
        update("UPDATE chunks SET build ='" + canBuild + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrustedBreak(String chunk, String canBreak) {
        update("UPDATE chunks SET break ='" + canBreak + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrustedInteract(String chunk, String canInteract) {
        update("UPDATE chunks SET interact ='" + canInteract + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrustedPVE(String chunk, String canPVE) {
        update("UPDATE chunks SET pve ='" + canPVE + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrusted(String chunk, String trusted) {
        update("UPDATE chunks SET trusted ='" + trusted + "' WHERE chunk ='" + chunk + "';");
    }
    public void setChunkPVP(String chunk, String canPVP) {
        update("UPDATE chunks SET pvp ='" + canPVP + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkSpawnMonsters(String chunk, String canSpawnMonster) {
        update("UPDATE chunks SET monster_spawning ='" + canSpawnMonster + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkExplode(String chunk, String canExplode) {
        update("UPDATE chunks SET explosions ='" + canExplode + "' WHERE chunk ='" + chunk + "';");
    }

    public void unclaimAllChunks(String uuid) {
        update("DELETE FROM chunks WHERE owner = '" + uuid + "';");
        update("UPDATE player_data SET claimed ='" + 0 + "' WHERE player ='" + uuid + "';");
    }

    public void setChunkPrice(String chunk, String price) {
        update("UPDATE chunks SET price ='" + price + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkOwner(String chunk, String uuid) {
        update("UPDATE chunks SET owner ='" + uuid + "' WHERE chunk ='" + chunk + "';");
    }

    //ADMIN CHUNKS TABLE

    public void claimAdminChunk(String chunk) {
        update("INSERT OR REPLACE INTO admin_chunks (chunk, build, break, interact, pve, pvp, monster_spawning, explosions) VALUES( '" + chunk + "', '0', '0', '0', '0', '0', '0', '0');");
    }

    public void unclaimAdminChunk(String chunk) {
        update("DELETE FROM admin_chunks WHERE chunk = '" + chunk + "';");
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

    //PLAYER DATA TABLE

    public void createPlayerData(String uuid) {
        update("INSERT OR REPLACE INTO player_data (player, claimed, bonus_claims, accrued_claims, trusted_global, build, break, interact, pve) VALUES( '" + uuid + "', '0', '0', '0', 'n', '1', '1', '1', '1');");
    }

    public void setAccruedClaims(String uuid, String amount) {
        update("UPDATE player_data SET accrued_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    public void setBonusClaims(String uuid, String amount) {
        update("UPDATE player_data SET bonus_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    public void setTrustedGlobal(String uuid, String trust) {
        update("UPDATE player_data SET trusted_global ='" + trust + "' WHERE player ='" + uuid + "';");
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
                String chunkPrice = rs.getString("price");
                cache.setChunk(chunk, uuid, trusted, canTrustedBuild, canTrustedBreak, canTrustedInteract, canTrustedPvE, canPvP, canSpawnMonsters, canExplode, chunkPrice);
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