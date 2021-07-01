package top.raven.strengthplus;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.raven.strengthplus.config.ConfigFactory;
import top.raven.strengthplus.hander.CommandHandler;
import top.raven.strengthplus.listener.OnDamageListener;
import top.raven.strengthplus.menu.InfoMenu;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: StrengthPlus
 * @Description: TODO
 * @Author: Raven
 * @Date: 2021/5/16
 * @Version: 1.0
 */
public class StrengthPlus extends JavaPlugin{
    private final String DEFAULT_COMMAND = "sp";
    @Override
    public void onLoad() {
        getServer().getConsoleSender().sendMessage(Color.GREEN+"[StrengthPlus]plugin have been load");
    }
    private OnDamageListener damageListener;
    private ConfigFactory factory;
    private CommandHandler handler;
    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(Color.GREEN+"[StrengthPlus]plugin have been enable");
        InfoMenu.outputAuthor(this);
        Bukkit.getPluginCommand(DEFAULT_COMMAND).setExecutor(handler = new CommandHandler(this));
        Bukkit.getPluginCommand(DEFAULT_COMMAND).setTabCompleter(this);
        factory = new ConfigFactory(this);
        damageListener = new OnDamageListener(this);
        reloadConfig();
        getServer().getPluginManager().registerEvents(damageListener,StrengthPlus.this);
    }

    private void readDamage(){
        try {
            damageListener.setSwoadDamage(Float.parseFloat(factory.getConfigData("sword")));
            damageListener.setBowDamage(Float.parseFloat(factory.getConfigData("bow")));
            damageListener.setCrossbowDamage(Float.parseFloat(factory.getConfigData("crossbow")));
            damageListener.setDefenceDamageValue(Float.parseFloat(factory.getConfigData("defence")));
            damageListener.setMinDamage(Float.parseFloat(factory.getConfigData("min_damage")));
        } catch (IOException e) {
            Bukkit.broadcastMessage("§a[StrengthPlus] 插件本地文件读取出错！请检查插件config.yml文件！");
            damageListener.setSwoadDamage(1.5F);
            damageListener.setBowDamage(1.2F);
            damageListener.setCrossbowDamage(1);
            damageListener.setDefenceDamageValue(0.2);
            damageListener.setMinDamage(0.2F);
            e.printStackTrace();
        }
    }

    private String[] subCommands = {"normal", "safe", "admin", "reload", "paper", "sponge"};//子命令
    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(Color.GREEN+"[StrengthPlus]plugin have been disable");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }
        if (args.length == 0) {
            return Arrays.asList(subCommands);
        }
        return Arrays.stream(subCommands).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        readDamage();
        factory.initDefaultConfigFile();
        handler.loadConfigData();
    }
}
