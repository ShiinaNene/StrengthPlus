package top.raven.strengthplus.hander;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import top.raven.strengthplus.menu.InfoMenu;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: CommandHandler
 * @Description: TODO
 * @Author: Raven
 * @Date: 2021/5/17
 * @Version: 1.0
 */
public class CommandHandler implements CommandExecutor {
    private JavaPlugin plugin;
    StregnthStrackHandler ssh = new StregnthStrackHandler();

    public CommandHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String firstCommand, @NotNull String[] sonCommand) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            ssh.setPlayer(p);
            if (sonCommand.length == 0) {
                InfoMenu.outPutPluginInfo(p);
            } else {
                //当为普通强化时
                if (sonCommand[0].equals("normal")) {
                    ssh.strengthItem(false, false);
                }
                //当为安全强化时
                else if (sonCommand[0].equals("safe")) {
                    ssh.strengthItem(true, false);
                }
                //当为给普通强化石时
                else if (sonCommand[0].equals("sponge") && p.hasPermission("strengths.admin")) {
                    if (sonCommand.length < 2) {
                        p.sendMessage("§a[StrengthPlus] §b请输入个数！");
                    } else {
                        if (sonCommand[1].matches("^[0-9_]+$")) {//判断是否为数字的正则表达式
                            ssh.giveNormalStone(p, Material.SPONGE, Integer.parseInt(sonCommand[1]));
                        }
                    }
                } else if (sonCommand[0].equals("paper") && p.hasPermission("strengths.admin")) {
                    if (sonCommand.length < 2) {
                        p.sendMessage("§a[StrengthPlus] §b请输入个数！");
                    } else {
                        if (sonCommand[1].matches("^[0-9_]+$")) {//判断是否为数字的正则表达式
                            ssh.giveSafeStone(p, Material.PAPER, Integer.parseInt(sonCommand[1]));
                        }
                    }
                }
                //当为admin强化时
                else if (sonCommand[0].equals("admin") && p.hasPermission("strengths.admin")) {
                    ssh.strengthItem(false, true);
                }
                //当为reload文件配置时
                else if (sonCommand[0].equals("reload") && p.hasPermission("strengths.admin")) {
                    plugin.reloadConfig();
                    p.sendMessage("§a[StrengthPlus] §b插件重载成功！");
                } else {
                    p.sendMessage("§a[StrengthPlus]§c请输入正确的指令！");
                }
            }
            return true;
        } else {
            plugin.getServer().getConsoleSender().sendMessage("§a[StrengthPlus] §c此指令不能在控制台调用！");
        }
        return false;
    }

    public void loadConfigData() {
        readStrength();
    }

    private List damageChance, itemList;

    private void readStrength() {
        try {
            ssh.setSTRENGTH_NORMAL(getConfigData("stone_normal"));
            ssh.setSTRENGTH_NORMAL(getConfigData("stone_safe"));
            itemList = readItemMeta("stone_normal");
            ssh.setSTRENGTH_NORMAL(itemList.get(0).toString());
            ssh.setNORMAL_LORE(itemList.get(1).toString());
            itemList = null;
            itemList = readItemMeta("stone_safe");
            ssh.setSTRENGTH_SAFE(itemList.get(0).toString());
            ssh.setSAFE_LORE(itemList.get(1).toString());
            itemList = readItemMeta("itemName");
            ssh.setCanStrengthItem(itemList);
            int[] chanceArray = new int[10];
            damageChance = readConfig("strength_chance");
            for (int i = 0; i < damageChance.size(); i++) {
                chanceArray[i] = Integer.parseInt(damageChance.get(i).toString());
            }
            ssh.setChanceArray(chanceArray);
        } catch (IOException e) {
            Bukkit.broadcastMessage("§a[StrengthPlus] 插件本地文件读取出错！请检查插件config.yml文件！");
            ssh.setSTRENGTH_NORMAL("§b强化石");
            ssh.setSTRENGTH_SAFE("§c保护强化卷");
            ssh.setChanceArray(new int[]{100, 90, 80, 70, 60, 50, 40, 20, 10, 5});
            e.printStackTrace();
        }
    }

    private String getConfigData(String key) throws IOException {
        String data = "";
        data = plugin.getConfig().getString(key);
        return data;
    }

    private List<String> readConfig(String key) throws IOException {
        List<String> dataList;
        dataList = plugin.getConfig().getStringList(key);
        return dataList;
    }

    private List<Material> readItemMeta(String key) throws IOException {
        List<Material> metaList;
        metaList = (List<Material>) plugin.getConfig().getList(key);
        return metaList;
    }
}
