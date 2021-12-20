package lee.code.chunks;

import lee.code.cache.CacheAPI;
import lee.code.chunks.commands.adminchunk.AdminCommandManager;
import lee.code.chunks.commands.adminchunk.AdminTabCompletion;
import lee.code.chunks.database.Cache;
import lee.code.chunks.listeners.ChunkCheckerListener;
import lee.code.chunks.listeners.ChunkListener;
import lee.code.chunks.commands.chunk.CommandManager;
import lee.code.chunks.commands.chunk.TabCompletion;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.listeners.JoinQuitListener;
import lee.code.chunks.listeners.MenuListener;
import lee.code.essentials.EssentialsAPI;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class GoldmanChunks extends JavaPlugin {

    @Getter private Data data;
    @Getter private PU pU;
    @Getter private SQLite sqLite;
    @Getter private Cache cache;
    @Getter private CacheAPI cacheAPI;
    @Getter private EssentialsAPI essentialsAPI;
    @Getter private ChunkAPI chunkAPI;

    @Override
    public void onEnable() {
        this.data = new Data();
        this.pU = new PU();
        this.sqLite = new SQLite();
        this.cache = new Cache();
        this.cacheAPI = new CacheAPI();
        this.essentialsAPI = new EssentialsAPI();
        this.chunkAPI = new ChunkAPI();

        sqLite.connect();
        sqLite.loadTables();

        data.cacheDatabase();
        data.loadListData();

        registerCommands();
        registerListeners();

        pU.accruedClaimTimer();

        sqLite.resetMainWorldChunks();
    }

    @Override
    public void onDisable() {
        sqLite.disconnect();
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
        getServer().getPluginManager().registerEvents(new ChunkCheckerListener(), this);
    }

    public static GoldmanChunks getPlugin() {
        return GoldmanChunks.getPlugin(GoldmanChunks.class);
    }
}
