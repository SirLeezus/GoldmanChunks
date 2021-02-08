package lee.code.chunks;

import lee.code.chunks.commands.adminchunk.AdminCommandManager;
import lee.code.chunks.commands.adminchunk.AdminTabCompletion;
import lee.code.chunks.listeners.ChunkCheckerListener;
import lee.code.chunks.listeners.ChunkListener;
import lee.code.chunks.commands.chunk.CommandManager;
import lee.code.chunks.commands.chunk.TabCompletion;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.listeners.JoinQuitListener;
import lee.code.chunks.listeners.MenuListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class GoldmanChunks extends JavaPlugin {

    @Getter private Data data;
    @Getter private PU pU;
    @Getter private SQLite sqLite;

    @Override
    public void onEnable() {
        this.data = new Data();
        this.pU = new PU();
        this.sqLite = new SQLite();

        sqLite.connect();
        sqLite.loadTables();

        registerCommands();
        registerListeners();

        pU.accruedClaimTimer();
    }

    @Override
    public void onDisable() {
        sqLite.disconnect();
    }

    private void registerCommands() {
        getCommand("chunk").setExecutor(new CommandManager());
        getCommand("adminchunk").setExecutor(new AdminCommandManager());
        getCommand("chunk").setTabCompleter(new TabCompletion());
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
