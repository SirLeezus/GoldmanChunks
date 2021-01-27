package lee.code.mychunks.files;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import lee.code.mychunks.MyChunks;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileManager {

    private final Map<String, CustomFile> configs = new HashMap<>();

    public void addConfig(String name) {
        MyChunks plugin = MyChunks.getPlugin();
        configs.put(name, new CustomFile(name + ".yml", "", plugin.getResource(name + ".yml"), plugin));
    }

    public void addConfig(String name, String path) {
        MyChunks plugin = MyChunks.getPlugin();
        configs.put(name, new CustomFile(name + ".yml", path, plugin.getResource(name + ".yml"), plugin));
    }

    public CustomFile getConfig(String name) {
        return configs.get(name);
    }

    public void reloadAll() {
        configs.values().forEach(CustomFile::reload);
    }

    public void loadConfigFolder(String path) {
        MyChunks plugin = MyChunks.getPlugin();
        final File folder = new File(path);
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files)
            if (file.getName().endsWith(".yml"))
                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    String name = file.getName().replace(".yml", "");
                    configs.put(name, new CustomFile(file.getName(), "", inputStream, plugin));
                } catch (FileNotFoundException ignored) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to load configuration file: " + file.getName());
                }
    }
}