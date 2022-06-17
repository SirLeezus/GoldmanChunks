package lee.code.chunks.database;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.tables.AdminChunkTable;
import lee.code.chunks.database.tables.ChunkTable;
import lee.code.chunks.database.tables.PlayerTable;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class SQLite {

    private Connection connection;
    private Statement statement;

    public void connect() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        connection = null;

        try {
            if (!plugin.getDataFolder().exists()) {
                boolean created = plugin.getDataFolder().mkdir();
            }
            File dbFile = new File(plugin.getDataFolder(), "old_database.db");
            if (!dbFile.exists()) {
                boolean created = dbFile.createNewFile();
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
                "`pve` varchar NOT NULL," +
                "`flying` varchar NOT NULL" +
                ");");
    }

    //CHUNKS TABLE

    public void claimChunk(String chunk, UUID uuid, String amount) {
        update("INSERT OR REPLACE INTO chunks (chunk, owner, trusted, build, break, interact, pve, monster_spawning, explosions, price) VALUES( '" + chunk + "','" + uuid + "', '0', '1', '1', '1', '1', '0', '0', '0');");
        update("UPDATE player_data SET claimed ='" + amount + "' WHERE player ='" + uuid + "';");
    }

    public void unclaimChunk(String chunk, UUID uuid, String claimAmount) {
        update("DELETE FROM chunks WHERE chunk = '" + chunk + "';");
        update("UPDATE player_data SET claimed ='" + claimAmount + "' WHERE player ='" + uuid + "';");
    }

    public void setChunkSetting(String key, String chunk, String value) {
        update("UPDATE chunks SET " + key + " = '" + value + "' WHERE chunk ='" + chunk + "';");
    }

    public void setChunkTrusted(String chunk, String trusted) {
        update("UPDATE chunks SET trusted ='" + trusted + "' WHERE chunk ='" + chunk + "';");
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
        update("INSERT OR REPLACE INTO admin_chunks (chunk, build, break, interact, pve, monster_spawning, explosions) VALUES( '" + chunk + "', '0', '0', '0', '0', '0', '0');");
    }

    public void unclaimBulkAdminChunks(List<String> chunks) {
        for (String chunk : chunks) unclaimAdminChunk(chunk);
    }

    public void unclaimAdminChunk(String chunk) {
        update("DELETE FROM admin_chunks WHERE chunk = '" + chunk + "';");
    }

    public void setAdminChunkSetting(String key, String chunk, String value) {
        update("UPDATE admin_chunks SET " + key + " = '" + value + "' WHERE chunk ='" + chunk + "';");
    }

    //PLAYER DATA TABLE

    public void createPlayerData(String uuid) {
        update("INSERT OR REPLACE INTO player_data (player, claimed, bonus_claims, accrued_claims, trusted_global, build, break, interact, pve, flying) VALUES( '" + uuid + "', '0', '0', '0', '0', '1', '1', '1', '1', '0');");
    }

    public void setChunkFlying(String uuid, String flying) {
        update("UPDATE player_data SET flying = '" + flying + "' WHERE player ='" + uuid + "';");
    }

    public void setClaimedAmount(String uuid, String amount) {
        update("UPDATE player_data SET claimed = '" + amount + "' WHERE player ='" + uuid + "';");
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

    public void setTrustedGlobalSetting(String key, String uuid, String value) {
        update("UPDATE player_data SET " + key + " = '" + value + "' WHERE player ='" + uuid + "';");
    }

    public void transferData() {
        connect();
        transferChunkData();
        transferPlayerData();
        transferAdminChunks();
    }

    public void transferChunkData() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        DatabaseManager databaseManager = plugin.getDatabaseManager();

        List<ChunkTable> chunkTables = new ArrayList<>();
        try {
            ResultSet rs = getResult("SELECT * FROM chunks;");
            while (rs.next()) {
                String chunk = rs.getString("chunk");
                String uuid = rs.getString("owner");
                String trusted = rs.getString("trusted");
                String canTrustedBuild = rs.getString("build");
                String canTrustedBreak = rs.getString("break");
                String canTrustedInteract = rs.getString("interact");
                String canTrustedPvE = rs.getString("pve");
                String canSpawnMonsters = rs.getString("monster_spawning");
                String canExplode = rs.getString("explosions");
                String chunkPrice = rs.getString("price");

                ChunkTable chunkTable = new ChunkTable(chunk, UUID.fromString(uuid));
                chunkTable.setTrusted(trusted);

                boolean trustedBuild = !canTrustedBuild.equals("0");
                chunkTable.setTrustedBuild(trustedBuild);
                boolean trustedBreak = !canTrustedBreak.equals("0");
                chunkTable.setTrustedBreak(trustedBreak);
                boolean trustedInteract = !canTrustedInteract.equals("0");
                chunkTable.setTrustedInteract(trustedInteract);
                boolean trustedPVE = !canTrustedPvE.equals("0");
                chunkTable.setTrustedPVE(trustedPVE);
                boolean chunkMonsters = !canSpawnMonsters.equals("0");
                chunkTable.setChunkMonsterSpawning(chunkMonsters);
                boolean chunkExplosions = !canExplode.equals("0");
                chunkTable.setChunkExplosions(chunkExplosions);

                chunkTable.setChunkPrice(Long.parseLong(chunkPrice));
                chunkTables.add(chunkTable);
            }
            databaseManager.createBulkChunkTable(chunkTables);
            Bukkit.getLogger().log(Level.INFO, BukkitUtils.parseColorString("&6Chunk Data Profiles Transferred: &a" + chunkTables.size()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void transferPlayerData() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        DatabaseManager databaseManager = plugin.getDatabaseManager();

        List<PlayerTable> playerTables = new ArrayList<>();

        try {
            ResultSet rs = getResult("SELECT * FROM player_data;");
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
                String flying = rs.getString("flying");

                PlayerTable playerTable = new PlayerTable(UUID.fromString(uuid));
                playerTable.setClaimed(Integer.parseInt(claimed));
                playerTable.setBonusClaims(Integer.parseInt(bonusClaimed));
                playerTable.setGlobalTrusted(trustedGlobal);
                boolean trustedBuild = !trustedGlobalBuild.equals("0");
                playerTable.setGlobalBuild(trustedBuild);
                boolean trustedBreak = !trustedGlobalBreak.equals("0");
                playerTable.setGlobalBreak(trustedBreak);
                boolean trustedInteract = !trustedGlobalInteract.equals("0");
                playerTable.setGlobalInteract(trustedInteract);
                boolean trustedPVE = !trustedGlobalPvE.equals("0");
                playerTable.setGlobalPVE(trustedPVE);
                boolean chunkFlying = !flying.equals("0");
                playerTable.setChunkFlying(chunkFlying);

                playerTables.add(playerTable);
            }

            databaseManager.createBulkPlayerTable(playerTables);
            Bukkit.getLogger().log(Level.INFO, BukkitUtils.parseColorString("&6Players Data Profiles Transferred: &a" + playerTables.size()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transferAdminChunks() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        DatabaseManager databaseManager = plugin.getDatabaseManager();

        List<AdminChunkTable> adminChunkTables = new ArrayList<>();

        try {
            ResultSet rs = getResult("SELECT * FROM admin_chunks;");

            while (rs.next()) {
                String chunk = rs.getString("chunk");
                String canBuild = rs.getString("build");
                String canBreak = rs.getString("break");
                String canInteract = rs.getString("interact");
                String canPvE = rs.getString("pve");
                String canSpawnMonsters = rs.getString("monster_spawning");
                String canExplode = rs.getString("explosions");

                AdminChunkTable adminChunkTable = new AdminChunkTable(chunk);
                boolean chunkBuild = !canBuild.equals("0");
                adminChunkTable.setChunkBuild(chunkBuild);
                boolean chunkBreak = !canBreak.equals("0");
                adminChunkTable.setChunkBreak(chunkBreak);
                boolean chunkInteract = !canInteract.equals("0");
                adminChunkTable.setChunkInteract(chunkInteract);
                boolean chunkPVE = !canPvE.equals("0");
                adminChunkTable.setChunkPVE(chunkPVE);
                boolean chunkMonsters = !canSpawnMonsters.equals("0");
                adminChunkTable.setChunkMonsterSpawning(chunkMonsters);
                boolean chunkExplode = !canExplode.equals("0");
                adminChunkTable.setChunkExplosions(chunkExplode);

                adminChunkTables.add(adminChunkTable);
            }
            databaseManager.createBulkAdminChunks(adminChunkTables);
            Bukkit.getLogger().log(Level.INFO, BukkitUtils.parseColorString("&6Admin Chunk Data Profiles Transferred: &a" + adminChunkTables.size()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}