package me.swiftens.loftyDailyRewards.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class YamlAccess {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration modifyFile;

    public YamlAccess(JavaPlugin plugin, String fileName) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
    }


    public void reloadFile() {
        modifyFile = YamlConfiguration.loadConfiguration(this.file);

        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig =YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            modifyFile.setDefaults(defConfig);
        }

    }

    public FileConfiguration getFile() {
        if (modifyFile == null) {
            this.reloadFile();
        }
        return modifyFile;
    }

    public void saveFile() {
        if (modifyFile != null && file != null) {
            try {
                getFile().save(file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save file" + file);
            }
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }
}
