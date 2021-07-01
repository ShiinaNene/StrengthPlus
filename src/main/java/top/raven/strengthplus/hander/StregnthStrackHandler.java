package top.raven.strengthplus.hander;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.raven.strengthplus.item.StrengthItemMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @ClassName: StregnthStrackHandler
 * @Description: TODO
 * @Author: Raven
 * @Date: 2021/5/19
 * @Version: 1.0
 */
public class StregnthStrackHandler {
    public static final String STRENGTH_PREFIX = "§b[强化等级]:§6§l";
    public static final String ADMIN_PREFIX = "§b[§c§l管理员§b强化等级]:§6§l";
    private static Material STRENGTH_STONE = Material.SPONGE;
    private String STRENGTH_NORMAL = "§b强化石", STRENGTH_SAFE = "§c保护强化卷";
    private final Material STRENGTH_PAPER = Material.PAPER;
    private String NORMAL_LORE = "§a用于强化武器和防具的好东西",SAFE_LORE= "§e保护强化卷，用于在超过8级之后的强化保护武器不被破坏";
    private Inventory inventory;
    private List<Material> canStrengthItem;
    private Player player;
    private Random random = new Random();

    public StregnthStrackHandler(){

    }

    public StregnthStrackHandler(Player player){
        this.player = player;
        this.inventory = player.getInventory();
    }

    private int[] chanceArray = {100,90,80,70,60,50,40,20,10,5};
    private final String AdminLore = "§c✡✡✡✡✡✡✡✡✡✡";
    public void strengthItem(boolean isSafe,boolean isAdmin){
        ItemStack[] stacks = inventory.getContents();
        ItemStack mainHandStack = player.getInventory().getItemInMainHand();
        if(getLevel(mainHandStack.getItemMeta())==10) {
            player.sendMessage("§a§l[StrengthPlus]§c§l你的武器已强化到最高等级！无法再进行武器的强化！");
        }else {
            if(!isSafe && !isAdmin){
                if(getStrengthStoneCount(stacks,NORMAL_LORE,STRENGTH_STONE)>0 ){
                    onItemStrenght(mainHandStack, false);
                }else {
                    player.sendMessage("§a[StrengthPlus]§c请确定您有足够的强化石以供强化！");
                }
            }else if(isSafe){
                if(getStrengthStoneCount(stacks,SAFE_LORE,STRENGTH_PAPER)>0) {
                    onItemStrenght(mainHandStack, true);
                }else {
                    player.sendMessage("§a[StrengthPlus]§c请确定您有足够的强化石以供强化！");
                }
            }else if(isAdmin && mainHandStack.getType() != Material.AIR){
                //管理员强化，什么都能强化，不需要检测
                List<String> levelList = new ArrayList<>();
                levelList.add(ADMIN_PREFIX);
                levelList.add(AdminLore);
                ItemMeta adminMeta = mainHandStack.getItemMeta();
                adminMeta.setLore(levelList);
                mainHandStack.setItemMeta(adminMeta);
                Bukkit.broadcastMessage("§a§l[公告]:§c§l卑鄙的管理员§6§l"+player.getName()+"§c§l使用作弊指令将他的武器强化到了"+
                        "§c§l[§e§l"+(10)+"§c§l]§6§l级！§c§l真是厚颜无耻！");
            }else {
                player.sendMessage("§a[StrengthPlus]§c请确定您有足够的强化石以供强化！");
            }
        }
    }

    private boolean onItemStrenght(ItemStack mainHandStack,boolean isSafe){
        if(mainHandStack != null && mainHandStack.getType() != Material.AIR){
            if(canBeStrength(mainHandStack,canStrengthItem)){
                ItemMeta meta = mainHandStack.getItemMeta();
                int level = getLevel(meta);
                if(level==10){
                    player.sendMessage("§a§l[StrengthPlus]§c§l你的武器已强化到最高等级！无法再进行武器的强化！");
                }else {
                    int chance = random.nextInt(101);
                    /*if(player.hasPermission("strength.admin")){//当管理员强化时能看到几率，用于调试用
                        player.sendMessage("§achance: §b"+chance+"§a level:§b"+getLevel(meta));
                        player.sendMessage("§aLevelChance: §b"+chanceArray[getLevel(meta)]);
                    }*/
                    if(chance < chanceArray[level]){
                        player.sendMessage("§a[StrengthPlus]§6恭喜你，强化成功！ §b当前武器等级为 [§a"+(level+1)+"§b]");
                        if(level>=5 && level<9){
                            Bukkit.broadcastMessage("§a§l[强化公告]:§b§l恭喜玩家§6§l"+player.getName()+"§b§l将他的武器强化到了"+
                                    "§c§l[§e§l"+(level+1)+"§c§l]§6§l级！");
                        }else if(level==9){
                            Bukkit.broadcastMessage("§a§l[强化公告]:§b§l只见强化炉中一抹金光闪烁！恭喜玩家§6§l"+player.getName()+"§b§l将他的武器强化到了"+
                                    "§c§l[§e§l"+(level+1)+"§c§l]§6§l级，真是可喜可贺！");
                        }
                        mainHandStack.setItemMeta(setLevel(meta,true));
                    }else {
                        player.sendMessage("§a[StrengthPlus]§c很遗憾，你的强化失败了！");
                        if(level>7 && !isSafe){
                            Bukkit.broadcastMessage("§a§l[强化公告]:§c§l玩家§b§l"+player.getName()+"§c§l将他的武器强化到" +
                                    "§c§l[§e§l"+(level+1)+"§c§l]§c§l级时强化炉发生了爆炸！导致武器被炸毁了");
                            mainHandStack.setType(Material.AIR);
                        }else if(level> 7 && isSafe) {
                            Bukkit.broadcastMessage("§a§l[强化公告]:§c§l玩家§a§l"+player.getName()+"§c§l将他的武器强化到" +
                                    "§c§l[§e§l"+(level+1)+"§c§l]§c§l级时强化炉发生了爆炸！但是由于装备保护卷的保护导致武器并没有炸毁！");

                        }
                        mainHandStack.setItemMeta(setLevel(meta,false));
                        return false;
                    }
                }
            }else {
                player.sendMessage("§a[StrengthPlus]§c请查看您的主手是否为可强化武器或不为空！");
            }
        }else {
            player.sendMessage("§a[StrengthPlus]§c请查看您的主手是否为可强化武器或不为空！");
        }
       return true;
    }

    private int getLevel(ItemMeta meta){
        int level = 0;
        if(meta!=null){
            if(meta.hasLore()){
                if(meta.getLore().get(0).equals(STRENGTH_PREFIX) || meta.getLore().get(0).equals(ADMIN_PREFIX)){
                    level = meta.getLore().get(1).length()-2;//获取第一行的lore
                }else{
                    level = 0;
                }
            }
        }
        return level;
    }

    private ItemMeta setLevel(ItemMeta meta,boolean isSuccess){
        int level = getLevel(meta);
        List<String> levelList = new ArrayList<>();
        levelList.add(STRENGTH_PREFIX);
        StringBuffer buffer = new StringBuffer();
        if(level>0){
            if(isSuccess){
                level++;
            }else {
                level--;
            }
            //超过8级时强化颜色变成金色
            if(level>7) {
                buffer.append("§e");
            }else {
                buffer.append("§b");
            }
            for(int i=0;i<level;i++){
                buffer.append("✡");
            }
            levelList.add(buffer.toString());
        }else {
            if(isSuccess){
                levelList.add("§b✡");
            }else {
                return meta;
            }
        }
        meta.setLore(levelList);
        return meta;
    }
    private int getStrengthStoneCount(ItemStack[] stacks,String lore,Material material){
        int count = 0;
        boolean haveSub = false;
        for (ItemStack stack : stacks){
            if(stack != null){
                if(isStrengthStone(stack,material)) {
                    if(stack.getItemMeta().hasLore()){
                        if(stack.getItemMeta().getLore().get(0).equals(lore)){
                            count += stack.getAmount();
                            if(!haveSub) {
                                stack.setAmount(stack.getAmount() - 1);
                                haveSub = true;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    private boolean isStrengthStone(ItemStack stack,Material material){
        if(stack.getType() == material && stack.getItemMeta().hasLore()){
            String info = stack.getItemMeta().getLore().get(0);
            if(info.equals(NORMAL_LORE) || info.equals(SAFE_LORE)){
                return true;
            }
        }
        return false;
    }

    private boolean canBeStrength(ItemStack stack,List<Material> itemMetas){
        Material material = stack.getType();
        for(Object mate : itemMetas){
            if(mate.toString().equals(material.toString())){
                return true;
            }
        }
        return false;
    }

    @Deprecated
    private boolean canBeStrength(ItemStack stack){
        Material material = stack.getType();
        for(int i = 0; i < StrengthItemMeta.DEFAULT_META.length; i++){
            if(material == StrengthItemMeta.DEFAULT_META[i]){
                return true;
            }
        }
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.inventory = player.getInventory();
    }
    List<String> lore;
    public void giveNormalStone(Player player,Material material,int amount){
        ItemStack strengthItem = new ItemStack(material);
        lore = new ArrayList<>();
        lore.add(NORMAL_LORE);
        strengthItem.setLore(lore);
        while (true){
            if(inventory.firstEmpty()<0 || inventory.firstEmpty()>35){
                player.sendMessage("§a[StrengthPlus]请确定你的背包有空位置！");
                return;
            }else {
                if(amount>64){
                    strengthItem.setAmount(64);
                    player.getInventory().setItem(inventory.firstEmpty(),strengthItem);
                    amount-=64;
                }else {
                    strengthItem.setAmount(amount);
                    player.getInventory().setItem(inventory.firstEmpty(),strengthItem);
                    return;
                }
            }
        }
    }

    public void giveSafeStone(Player player,Material material,int amount){
        ItemStack strengthItem = new ItemStack(material);
        strengthItem.getItemMeta().setLocalizedName(STRENGTH_SAFE);
        lore = new ArrayList<>();
        lore.add(SAFE_LORE);
        strengthItem.setLore(lore);
        while (true){
            if(inventory.firstEmpty()<0 || inventory.firstEmpty()>35){
                player.sendMessage("§a[StrengthPlus]请确定你的背包有空位置！");
                return;
            }else {
                if(amount>64){
                    strengthItem.setAmount(64);
                    player.getInventory().setItem(inventory.firstEmpty(),strengthItem);
                    amount-=64;
                }else {
                    strengthItem.setAmount(amount);
                    player.getInventory().setItem(inventory.firstEmpty(),strengthItem);
                    return;
                }
            }
        }
    }

    public void setNORMAL_LORE(String NORMAL_LORE) {
        this.NORMAL_LORE = NORMAL_LORE;
    }

    public void setSAFE_LORE(String SAFE_LORE) {
        this.SAFE_LORE = SAFE_LORE;
    }

    public void setSTRENGTH_NORMAL(String STRENGTH_NORMAL) {
        this.STRENGTH_NORMAL = STRENGTH_NORMAL;
    }

    public void setSTRENGTH_SAFE(String STRENGTH_SAFE) {
        this.STRENGTH_SAFE = STRENGTH_SAFE;
    }

    public void setChanceArray(int[] chanceArray) {
        this.chanceArray = chanceArray;
    }

    public void setCanStrengthItem(List<Material> canStrengthItem) {
        this.canStrengthItem = canStrengthItem;
    }

}
