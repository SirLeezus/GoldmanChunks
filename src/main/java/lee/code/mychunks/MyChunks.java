package lee.code.mychunks;

import lee.code.mychunks.softdependency.WorldGuardAPI;
import lee.code.mychunks.files.defaults.Config;
import lee.code.mychunks.files.defaults.Settings;
import lee.code.mychunks.files.defaults.Values;
import lee.code.mychunks.listeners.ChunkCheckerListener;
import lee.code.mychunks.listeners.ChunkListener;
import lee.code.mychunks.commands.CommandManager;
import lee.code.mychunks.commands.TabCompletion;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.FileManager;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.listeners.JoinQuitListener;
import lee.code.mychunks.listeners.MenuListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class MyChunks extends JavaPlugin {

    @Getter private Data data;
    @Getter private FileManager fileManager;
    @Getter private Utility utility;
    @Getter private SQLite sqLite;
    @Getter private WorldGuardAPI worldGuardAPI;

    @Override
    public void onEnable() {

        this.data = new Data();
        this.fileManager = new FileManager();
        this.utility = new Utility();
        this.sqLite = new SQLite();

        fileManager.addConfig("config");
        fileManager.addConfig("lang");

        sqLite.connect();
        sqLite.loadTables();
        loadDefaults();

        registerCommands();
        registerListeners();

        if (Settings.ACCRUED_CLAIMS_ENABLED.getConfigValue()) utility.accruedClaimTimer();

        if (Settings.WORLD_GUARD_SUPPORT.getConfigValue()) {
            this.worldGuardAPI = new WorldGuardAPI();
        }

    }

    @Override
    public void onDisable() {
        sqLite.disconnect();
    }

    private void registerCommands() {
        getCommand("chunk").setExecutor(new CommandManager());
        getCommand("chunk").setTabCompleter(new TabCompletion());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkCheckerListener(), this);
    }

    public void loadDefaults() {

        //config
        Config.setFile(fileManager.getConfig("config").getData());
        for (Config value : Config.values()) fileManager.getConfig("config").getData().addDefault(value.getPath(), value.getDefault());
        fileManager.getConfig("config").getData().options().copyDefaults(true);
        fileManager.getConfig("config").save();

        //config settings
        Settings.setFile(fileManager.getConfig("config").getData());
        for (Settings value : Settings.values()) fileManager.getConfig("config").getData().addDefault(value.getPath(), value.getDefault());
        fileManager.getConfig("config").getData().options().copyDefaults(true);
        fileManager.getConfig("config").save();

        //config values
        Values.setFile(fileManager.getConfig("config").getData());
        for (Values value : Values.values()) fileManager.getConfig("config").getData().addDefault(value.getPath(), value.getDefault());
        fileManager.getConfig("config").getData().options().copyDefaults(true);
        fileManager.getConfig("config").save();

        //lang
        Lang.setFile(fileManager.getConfig("lang").getData());
        for (Lang value : Lang.values()) fileManager.getConfig("lang").getData().addDefault(value.getPath(), value.getDefault());
        fileManager.getConfig("lang").getData().options().copyDefaults(true);
        fileManager.getConfig("lang").save();
    }

    public void reloadFiles() {
        fileManager.reloadAll();
    }

    public static MyChunks getPlugin() {
        return MyChunks.getPlugin(MyChunks.class);
    }
}
