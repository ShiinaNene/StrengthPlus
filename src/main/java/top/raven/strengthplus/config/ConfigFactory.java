package top.raven.strengthplus.config;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import top.raven.strengthplus.hander.StregnthStrackHandler;
import java.io.*;
import java.util.List;

/**
 * @ClassName: ConfigFactory
 * @Description: TODO
 * @Author: Raven
 * @Date: 2021/5/20
 * @Version: 1.0
 */
public class ConfigFactory {
    private JavaPlugin plugin;
    private File configFile,folder;
    public ConfigFactory(JavaPlugin plugin){
        this.plugin = plugin;
        folder = plugin.getDataFolder();
    }

    public void initDefaultConfigFile(){
        if(!folder.exists()){
            configFile = new File(folder,"config.yml");
            if(!configFile.exists()){
                plugin.saveDefaultConfig();
            }
        }
    }

    public String getConfigData(String key) throws IOException {
        String data = "";
        data = plugin.getConfig().getString(key);
        return data;
    }
}
