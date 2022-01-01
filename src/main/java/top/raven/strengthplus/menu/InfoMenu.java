package top.raven.strengthplus.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @ClassName: InfoMenu
 * @Description: TODO
 * @Author: Raven
 * @Date: 2021/5/17
 * @Version: 1.0
 */
public class InfoMenu {
    public static void outPutPluginInfo(Player player) {
        player.sendMessage("§4§l===-------§6§l[StrengthPlus]§4§l--------===");
        player.sendMessage("§c/sp 或 qh 打开此帮助菜单");
        player.sendMessage("§a/sp 或 qh normal 进行一次强化");
        player.sendMessage("§b/sp 或 qh safe 保护强化 (强化失败不降级)");
        if (player.hasPermission("strength.admin")) {
            player.sendMessage("§6/sp admin 管理员强化，直接满级");
            player.sendMessage("§6/sp reload 管理员专用，重载配置");
            player.sendMessage("§a/sp sponge 管理员权限可用，给与普通强化石");
            player.sendMessage("§b/sp paper 管理员权限可用，给与保护强化石");
        }
        player.sendMessage("§4§l===-------§6§l[StrengthPlus]§4§l--------===");
    }

    public static void outputAuthor(JavaPlugin plugin) {
        plugin.getServer().getConsoleSender().sendMessage("§4§l===-------§6§l[StrengthPlus]§4§l--------===");
        plugin.getServer().getConsoleSender().sendMessage("§b      制作者： Raven       ");
        plugin.getServer().getConsoleSender().sendMessage("§b      QQ ： 740585947     ");
        plugin.getServer().getConsoleSender().sendMessage("§b如有bug可以加我反馈也可以在bbs论坛下留言，蟹蟹！");
        plugin.getServer().getConsoleSender().sendMessage("§4§l===-------§6§l[StrengthPlus]§4§l--------===");
    }
}
