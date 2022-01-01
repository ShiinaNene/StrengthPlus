package top.raven.strengthplus.listener;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import top.raven.strengthplus.hander.StregnthStrackHandler;

/**
 * @ClassName: OnDamageListener
 * @Description: TODO
 * @Author: Raven
 * @Date: 2021/5/21
 * @Version: 1.0
 */
public class OnDamageListener implements Listener {
    private final int SWOAD = 1, BOW = 2, CROSSBOW = 3, MONISTOR = -1;
    private float swoadDamage = 1.5F, bowDamage = 1.2F, crossbowDamage = 1;
    private Player damager, defencer;
    private double defenceDamageValue = 0.2;
    private JavaPlugin plugin;
    private float minDamage;

    public OnDamageListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    double defenceDamage = 0;

    @EventHandler(priority = EventPriority.HIGH)//高优先级
    public void modifyEntityDamage(EntityDamageByEntityEvent attackEvent) {
        defenceDamage = 0;
        if (attackEvent.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (attackEvent.getEntity() instanceof Player) {
                defencer = (Player) attackEvent.getEntity();
                if (hasStrenghtDefence(defencer)) {
                    defenceDamage = onDefenceEvent(defencer);
                }
            }
            if (attackEvent.getDamager() instanceof Player) {
                damager = (Player) attackEvent.getDamager();
                ItemStack stack = damager.getInventory().getItemInMainHand();
                if (stack.getItemMeta().hasLore() && !stack.getType().equals(Material.BOW) && !stack.getType().equals(Material.BOW)) {
                    onDamage(attackEvent, SWOAD, defenceDamage);
                }
            }
        } else if (attackEvent.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (attackEvent.getEntity() instanceof Player) {
                defencer = (Player) attackEvent.getEntity();
                if (hasStrenghtDefence(defencer)) {
                    defenceDamage = onDefenceEvent(defencer);
                }
            }
            if (damager instanceof Player) {
                if (damager.getInventory().getItemInMainHand().getItemMeta().hasLore()) {
                    if (damager.getInventory().getItemInMainHand().getType() == Material.CROSSBOW) {
                        onDamage(attackEvent, CROSSBOW, defenceDamage);
                    } else {
                        onDamage(attackEvent, BOW, defenceDamage);
                    }
                }
            }
        }
        if (!(attackEvent.getDamager() instanceof Player)) {
            if (attackEvent.getDamage() <= defenceDamage && !isPlayerShoter) {
                onDamage(attackEvent, MONISTOR, defenceDamage);
            }
        }
        isPlayerShoter = false;
        damager = null;
    }

    private void onDamage(EntityDamageByEntityEvent attackEvent, int itemValue, double armorDamage) {
        double essentialsDamage = attackEvent.getDamage();
        double damage = Math.round(onDamageEvent(damager, itemValue) + essentialsDamage);
        //debug 用的数据测试
        /*if(damager instanceof Player){
            damager.sendMessage("§a[strengthPlus]"+"基础伤害："+((int)essentialsDamage)+" 强化伤害："+damage+" 最终伤害："+(damage-armorDamage)
                    );
        }*/
        if (damage <= armorDamage) {
            /*if(damager instanceof Player){
                damager.sendMessage("§a[strengthPlus]"+"由于防御过高触发最小damage事件，最小伤害为："+minDamage);
            }*/
            attackEvent.setDamage(minDamage);
        } else {
            attackEvent.setDamage(damage - armorDamage);
        }
    }

    boolean isPlayerShoter = false;

    @EventHandler(priority = EventPriority.HIGHEST)//高优先级
    public void modifyShotDamage(ProjectileLaunchEvent shotEvent) {
        if (shotEvent.getEntity().getShooter() instanceof Player) {
            this.damager = (Player) shotEvent.getEntity().getShooter();
            isPlayerShoter = true;
        }
    }

    private double onDamageEvent(Player player, int itemValue) {
        double damage = 0;
        if (itemValue == -1) {
            return 0;
        }
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getType() != Material.AIR) {
            ItemMeta meta = itemInMainHand.getItemMeta();
            if (meta.hasLore()) {
                if (meta.getLore().get(0).equals(StregnthStrackHandler.STRENGTH_PREFIX) ||
                        meta.getLore().get(0).equals(StregnthStrackHandler.ADMIN_PREFIX)) {
                    int level = meta.getLore().get(1).length() - 2;//获取第一行的lore
                    switch (itemValue) {
                        case SWOAD:
                            damage = level * swoadDamage;
                            break;
                        case BOW:
                            damage = level * bowDamage;
                            break;
                        case CROSSBOW:
                            damage = level * crossbowDamage;
                            break;
                    }
                }
            }
        }
        return damage;
    }

    private double onDefenceEvent(Entity defencer) {
        double defenceValue = 0;
        if (defencer.getType().toString().equals("PLAYER")) {
            Player player = (Player) defencer;
            ItemStack[] stacks;
            stacks = player.getInventory().getArmorContents();
            int level = 0;
            for (ItemStack stack : stacks) {
                if (stack != null) {
                    if (stack.getItemMeta().hasLore()) {
                        if (stack.getLore().get(0).equals(StregnthStrackHandler.STRENGTH_PREFIX) ||
                                stack.getLore().get(0).equals(StregnthStrackHandler.ADMIN_PREFIX)) {
                            level += getLevel(stack.getItemMeta());
                        }
                    }
                }
            }
            defenceValue = defenceDamageValue * level;
        }
        return defenceValue;
    }

    private boolean hasStrenghtDefence(Player player) {
        ItemStack[] stacks = player.getInventory().getArmorContents();
        for (ItemStack stack : stacks) {
            if (stack != null) {
                if (stack.getItemMeta().hasLore()) {
                    if (stack.getLore().get(0).equals(StregnthStrackHandler.STRENGTH_PREFIX) ||
                            stack.getLore().get(0).equals(StregnthStrackHandler.ADMIN_PREFIX)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int getLevel(ItemMeta meta) {
        int level = 0;
        if (meta.hasLore()) {
            level = meta.getLore().get(1).length() - 2;//获取第一行的lore
        }
        return level;
    }

    public void setSwoadDamage(float swoadDamage) {
        this.swoadDamage = swoadDamage;
    }

    public void setBowDamage(float bowDamage) {
        this.bowDamage = bowDamage;
    }

    public void setCrossbowDamage(float crossbowDamage) {
        this.crossbowDamage = crossbowDamage;
    }

    public void setDefenceDamageValue(double defenceDamageValue) {
        this.defenceDamageValue = defenceDamageValue;
    }

    public void setMinDamage(float minDamage) {
        this.minDamage = minDamage;
    }
}
