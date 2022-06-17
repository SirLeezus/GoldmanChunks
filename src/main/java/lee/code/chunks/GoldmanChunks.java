package lee.code.chunks;

import lee.code.chunks.commands.adminchunk.AdminCommandManager;
import lee.code.chunks.commands.adminchunk.AdminTabCompletion;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.database.DatabaseManager;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.listeners.ChunkListener;
import lee.code.chunks.commands.chunk.CommandManager;
import lee.code.chunks.commands.chunk.TabCompletion;
import lee.code.chunks.listeners.JoinQuitListener;
import lee.code.chunks.listeners.MenuListener;
import lee.code.essentials.EssentialsAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class GoldmanChunks extends JavaPlugin {

    @Getter private Data data;
    @Getter private PU pU;
    @Getter private CacheManager cacheManager;
    @Getter private DatabaseManager databaseManager;
    @Getter private EssentialsAPI essentialsAPI;
    @Getter private ChunkAPI chunkAPI;


    @Getter private SQLite oldSqLite;

    @Override
    public void onEnable() {
        this.data = new Data();
        this.pU = new PU();
        this.cacheManager = new CacheManager();
        this.essentialsAPI = new EssentialsAPI();
        this.chunkAPI = new ChunkAPI();
        this.databaseManager = new DatabaseManager();

        this.oldSqLite = new SQLite();

        databaseManager.initialize();
        //oldSqLite.transferData();

        data.loadData();

        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
    }

    private void registerCommands() {
        getCommand("chunk").setExecutor(new CommandManager());
        getCommand("chunk").setTabCompleter(new TabCompletion());

        getCommand("adminchunk").setExecutor(new AdminCommandManager());
        getCommand("adminchunk").setTabCompleter(new AdminTabCompletion());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);
    }

    public static GoldmanChunks getPlugin() {
        return GoldmanChunks.getPlugin(GoldmanChunks.class);
    }
}
