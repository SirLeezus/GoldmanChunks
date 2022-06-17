package lee.code.chunks.database;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.tables.AdminChunkTable;
import lee.code.chunks.database.tables.ChunkTable;
import lee.code.chunks.database.tables.PlayerTable;
import lee.code.chunks.lists.chunksettings.AdminChunkSetting;
import lee.code.core.ormlite.dao.Dao;
import lee.code.core.ormlite.dao.DaoManager;
import lee.code.core.ormlite.jdbc.JdbcConnectionSource;
import lee.code.core.ormlite.jdbc.db.DatabaseTypeUtils;
import lee.code.core.ormlite.logger.LogBackendType;
import lee.code.core.ormlite.logger.LoggerFactory;
import lee.code.core.ormlite.stmt.*;
import lee.code.core.ormlite.support.ConnectionSource;
import lee.code.core.ormlite.table.TableUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class DatabaseManager {

    private Dao<PlayerTable, UUID> playerDao;
    private Dao<ChunkTable, String> chunkDao;
    private Dao<AdminChunkTable, String> adminChunkDao;

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    private String getDatabaseURL() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        return "jdbc:sqlite:" + new File(plugin.getDataFolder(), "database.db");
    }

    public void initialize() {
        LoggerFactory.setLogBackendFactory(LogBackendType.NULL);

        try {
            String databaseURL = getDatabaseURL();
            connectionSource = new JdbcConnectionSource(
                    databaseURL,
                    "test",
                    "test",
                    DatabaseTypeUtils.createDatabaseType(databaseURL));
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        CacheManager cacheManager = GoldmanChunks.getPlugin().getCacheManager();

        //player data
        TableUtils.createTableIfNotExists(connectionSource, PlayerTable.class);
        playerDao = DaoManager.createDao(connectionSource, PlayerTable.class);

        //load player data into cache
        for (PlayerTable playerTable : playerDao.queryForAll()) cacheManager.setPlayerData(playerTable);

        //chunk data
        TableUtils.createTableIfNotExists(connectionSource, ChunkTable.class);
        chunkDao = DaoManager.createDao(connectionSource, ChunkTable.class);

        //load chunk data into cache
        for (ChunkTable chunkTable : chunkDao.queryForAll()) cacheManager.setChunk(chunkTable);

        //admin chunk data
        TableUtils.createTableIfNotExists(connectionSource, AdminChunkTable.class);
        adminChunkDao = DaoManager.createDao(connectionSource, AdminChunkTable.class);

        //load admin chunk data into cache
        for (AdminChunkTable adminChunkTable : adminChunkDao.queryForAll()) cacheManager.setAdminChunk(adminChunkTable);
    }

    public void closeConnection() {
        try {
            connectionSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void createPlayerTable(PlayerTable playerTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                playerDao.create(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createBulkPlayerTable(List<PlayerTable> playerTables) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                playerDao.callBatchTasks((Callable<Void>) () -> {
                    for (PlayerTable playerTable : playerTables) {
                        playerDao.create(playerTable);
                    }
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updatePlayerTable(PlayerTable playerTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                playerDao.update(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createChunkTable(ChunkTable chunkTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                chunkDao.create(chunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createBulkChunkTable(List<ChunkTable> chunkTables) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                chunkDao.callBatchTasks((Callable<Void>) () -> {
                    for (ChunkTable chunkTable : chunkTables) {
                        chunkDao.create(chunkTable);
                    }
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateChunkTable(ChunkTable chunkTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                chunkDao.update(chunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteChunkTable(ChunkTable chunkTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                chunkDao.delete(chunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteAllChunkTables(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                chunkDao.executeRaw("DELETE FROM chunks WHERE owner = '" + uuid + "';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateAdminChunkTable(AdminChunkTable adminChunkTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                adminChunkDao.update(adminChunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateBulkAdminChunks(List<AdminChunkTable> chunks) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                adminChunkDao.callBatchTasks((Callable<Void>) () -> {
                    for (AdminChunkTable chunk : chunks) {
                        adminChunkDao.update(chunk);
                    }
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createBulkAdminChunks(List<AdminChunkTable> chunks) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                adminChunkDao.callBatchTasks((Callable<Void>) () -> {
                    for (AdminChunkTable chunk : chunks) {
                        adminChunkDao.create(chunk);
                    }
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createAdminChunkTable(AdminChunkTable adminChunkTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                adminChunkDao.create(adminChunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteBulkAdminChunks(List<AdminChunkTable> chunks) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                adminChunkDao.callBatchTasks((Callable<Void>) () -> {
                    for (AdminChunkTable chunk : chunks) {
                        adminChunkDao.delete(chunk);
                    }
                    return null;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteAdminChunkTable(AdminChunkTable adminChunkTable) {
        Bukkit.getScheduler().runTaskAsynchronously(GoldmanChunks.getPlugin(), () -> {
            try {
                adminChunkDao.delete(adminChunkTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
