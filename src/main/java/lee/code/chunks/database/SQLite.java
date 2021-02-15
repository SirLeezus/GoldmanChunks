package lee.code.chunks.database;

import lee.code.chunks.GoldmanChunks;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

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
                "`build` int NOT NULL," +
                "`break` int NOT NULL," +
                "`interact` int NOT NULL," +
                "`pve` int NOT NULL," +
                "`pvp` int NOT NULL," +
                "`monster_spawning` int NOT NULL," +
                "`explosions` int NOT NULL" +
                ");");

        //admin table
        update("CREATE TABLE IF NOT EXISTS admin_chunks (" +
                "`chunk` varchar PRIMARY KEY," +
                "`build` int NOT NULL," +
                "`break` int NOT NULL," +
                "`interact` int NOT NULL," +
                "`pve` int NOT NULL," +
                "`pvp` int NOT NULL," +
                "`monster_spawning` int NOT NULL," +
                "`explosions` int NOT NULL" +
                ");");

        //player table
        update("CREATE TABLE IF NOT EXISTS player_data (" +
                "`player` varchar PRIMARY KEY," +
                "`claimed` int NOT NULL," +
                "`bonus_claims` int NOT NULL," +
                "`accrued_claims` int NOT NULL," +
                "`trusted_global` varchar NOT NULL," +
                "`build` int NOT NULL," +
                "`break` int NOT NULL," +
                "`interact` int NOT NULL," +
                "`pve` int NOT NULL" +
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

    public void setTrustedBuild(String chunk, int canBuild) {
        update("UPDATE chunks SET build ='" + canBuild + "' WHERE chunk ='" + chunk + "';");
    }

    public void setTrustedBreak(String chunk, int canBreak) {
        update("UPDATE chunks SET break ='" + canBreak + "' WHERE chunk ='" + chunk + "';");
    }

    public void setTrustedInteract(String chunk, int canInteract) {
        update("UPDATE chunks SET interact ='" + canInteract + "' WHERE chunk ='" + chunk + "';");
    }

    public void setTrustedPVE(String chunk, int canPVE) {
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

    @SneakyThrows
    public void removeChunkTrusted(String chunk, String untrust) {
        ResultSet rs = getResult("SELECT * FROM chunks WHERE chunk = '" + chunk + "';");
        String players = rs.getString("trusted");
        String[] split = StringUtils.split(players, ',');
        List<String> playerList = new ArrayList<>();

        for (String trusted : split) {
            UUID trustedUUID = UUID.fromString(trusted);
            OfflinePlayer player = Bukkit.getOfflinePlayer(trustedUUID);
            String name = player.getName();
            if (name != null && name.equals(untrust)) playerList.add(trusted);
        }

        if (!playerList.isEmpty()) {
            String trusted = StringUtils.join(playerList, ",");
            update("UPDATE chunks SET trusted ='" + trusted + "' WHERE chunk ='" + chunk + "';");
        } else update("UPDATE chunks SET trusted ='n' WHERE chunk ='" + chunk + "';");
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

    public void claimChunk(String chunk, UUID uuid) {
        update("INSERT INTO chunks (chunk, owner, trusted, build, break, interact, pve, pvp, monster_spawning, explosions) VALUES( '" + chunk + "','" + uuid + "', 'n', '1', '1', '1', '1', '0', '0', '0');");
        addClaimedAmount(uuid);
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

    public void setChunkExplosion(String chunk, int canExplode) {
        update("UPDATE chunks SET explosions ='" + canExplode + "' WHERE chunk ='" + chunk + "';");
    }

    public void unClaimChunk(String chunk, UUID uuid) {
        update("DELETE FROM chunks WHERE chunk = '" + chunk + "';");
        subtractClaimedAmount(uuid);
    }

    public void deleteAllClaimedChunks(UUID uuid) {
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

    public void setAdminChunkBuild(String chunk, int canBuild) {
        update("UPDATE admin_chunks SET build ='" + canBuild + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkBreak(String chunk, int canBreak) {
        update("UPDATE admin_chunks SET break ='" + canBreak + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkInteract(String chunk, int canInteract) {
        update("UPDATE admin_chunks SET interact ='" + canInteract + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkPVE(String chunk, int canPVE) {
        update("UPDATE admin_chunks SET pve ='" + canPVE + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkPVP(String chunk, int canPVP) {
        update("UPDATE admin_chunks SET pvp ='" + canPVP + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkSpawnMonsters(String chunk, int canSpawnMonster) {
        update("UPDATE admin_chunks SET monster_spawning ='" + canSpawnMonster + "' WHERE chunk ='" + chunk + "';");
    }

    public void setAdminChunkExplosion(String chunk, int canExplode) {
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

    public void createPlayerDataTable(UUID uuid) {
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

    public void setAccruedClaims(UUID uuid, int amount) {
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

    public void setBonusClaims(UUID uuid, int amount) {
        update("UPDATE player_data SET bonus_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public void removeBonusClaims(UUID uuid, int amount) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");

        int currentAmount = rs.getInt("bonus_claims");
        if (amount > currentAmount) amount = 0;
        else amount = currentAmount - amount;

        update("UPDATE player_data SET bonus_claims = '" + amount + "' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public int getBonusClaims(UUID uuid) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");
        return rs.getInt("bonus_claims");
    }

    @SneakyThrows
    public void removeGlobalTrustedPlayer(UUID uuid, String untrust) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");

        String players = rs.getString("trusted_global");
        String[] split = StringUtils.split(players, ',');
        List<String> playerList = new ArrayList<>();

        for (String trusted : split) {
            if (!Bukkit.getOfflinePlayer(UUID.fromString(trusted)).getName().equals(untrust)) playerList.add(trusted);
        }

        if (!playerList.isEmpty()) {
            String trusted = StringUtils.join(playerList, ",");
            update("UPDATE player_data SET trusted_global ='" + trusted + "' WHERE player ='" + uuid + "';");
        } else update("UPDATE player_data SET trusted_global ='n' WHERE player ='" + uuid + "';");
    }

    @SneakyThrows
    public void addGlobalTrustedPlayer(UUID uuid, UUID trust) {
        ResultSet rs = getResult("SELECT * FROM player_data WHERE player = '" + uuid + "';");

        if (!rs.getString("trusted_global").equals("n")) {
            String trusted = rs.getString("trusted_global") + "," + trust;
            update("UPDATE player_data SET trusted_global ='" + trusted + "' WHERE player ='" + uuid + "';");

        } else update("UPDATE player_data SET trusted_global ='" + trust + "' WHERE player ='" + uuid + "';");
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

    public void setGlobalTrustedBuild(UUID uuid, int canBuild) {
        update("UPDATE player_data SET build ='" + canBuild + "' WHERE player ='" + uuid + "';");
    }

    public void setGlobalTrustedBreak(UUID uuid, int canBreak) {
        update("UPDATE player_data SET break ='" + canBreak + "' WHERE player ='" + uuid + "';");
    }

    public void setGlobalTrustedInteract(UUID uuid, int canInteract) {
        update("UPDATE player_data SET interact ='" + canInteract + "' WHERE player ='" + uuid + "';");
    }

    public void setGlobalTrustedPVE(UUID uuid, int canPVE) {
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
}