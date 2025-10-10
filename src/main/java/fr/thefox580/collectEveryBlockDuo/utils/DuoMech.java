package fr.thefox580.collectEveryBlockDuo.utils;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class DuoMech implements Runnable{

    private final Map<Player, Player> duos = new HashMap<>();
    private final Map<Player, Slime> chainPoint = new HashMap<>();
    private final BukkitTask slimeTpTask;
    private final Main plugin;

    public DuoMech(Main plugin){
        this.slimeTpTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 1L);
        this.plugin = plugin;
    }

    public void stopTask(){
        slimeTpTask.cancel();
    }

    public Map<Player, Slime> getSlimes(){
        return chainPoint;
    }

    public boolean hasDuo(Player player){
        return duos.containsKey(player);
    }

    public void killAllSlimes(){
        for (Player player : chainPoint.keySet()){
            chainPoint.get(player).remove();
            chainPoint.remove(player);
        }
    }

    public void addDuo(Player player1, Player player2){

        if (player1.getLocation().distance(player2.getLocation()) <  12){
            duos.put(player1, player2);
            duos.put(player2, player1);

            createLink(player1);
        } else {
            player1.sendMessage(Component.text("You're too far away from " + player2.getName() + "! Get within 12 blocks to be chained to them.", NamedTextColor.RED));
            player2.sendMessage(Component.text("You're too far away from " + player1.getName() + "! Get within 12 blocks to be chained to them.", NamedTextColor.RED));
        }
    }

    public void removeDuo(Player removed){
        Player duo = duos.get(removed);

        chainPoint.get(duo).remove();
        chainPoint.get(removed).remove();

        chainPoint.remove(duo);
        chainPoint.remove(removed);

        duos.remove(duo);
        duos.remove(removed);

        removed.sendMessage(Component.text("You've successfully left your duo!", NamedTextColor.GREEN));
        duo.sendMessage(Component.text("Your duo has left your party!", NamedTextColor.GREEN));
    }

    public boolean wasFound(Player player, Material block){
        Player duo = duos.get(player);
        return Objects.requireNonNull(plugin.getConfig().getStringList("blocks_"+player.getUniqueId()+"_"+duo.getUniqueId())).contains(block.getKey().getKey());
    }

    public void addBlock(Player player, Material block){
        if (!wasFound(player, block)){

            if (getAllBlocks().contains(block)){

                Player duo = duos.get(player);

                List<String> completionList = plugin.getConfig().getStringList("blocks_"+player.getUniqueId()+"_"+duo.getUniqueId());
                completionList.add(block.getKey().getKey());

                plugin.getConfig().set("blocks_"+player.getUniqueId()+"_"+duo.getUniqueId(), completionList);
                plugin.getConfig().set("blocks_"+duo.getUniqueId()+"_"+player.getUniqueId(), completionList);

                plugin.saveConfig();

                ItemStack item = new ItemStack(block);

                Bukkit.broadcast(Component.text(player.getName() + " and " + duo.getName() + " found ", NamedTextColor.AQUA)
                        .append(item.displayName().color(NamedTextColor.AQUA))
                        .append(Component.text(". (" + completionList.size() + "/" + getAllBlocks().size() + ")")));
            }
            }
    }

    public List<Material> getAllBlocks(){
        List<Material> blocks = new ArrayList<>();
        for (Material item : Material.values()){
            if (!item.isLegacy() && !item.isAir() && item.isBlock() &&
                    !List.of(Material.WALL_TORCH, Material.ACACIA_WALL_HANGING_SIGN, Material.ACACIA_WALL_SIGN,
                            Material.BAMBOO_WALL_HANGING_SIGN, Material.BAMBOO_WALL_SIGN, Material.BIRCH_WALL_HANGING_SIGN,
                            Material.BIRCH_WALL_SIGN, Material.CHERRY_WALL_HANGING_SIGN, Material.CHERRY_WALL_SIGN,
                            Material.CRIMSON_WALL_HANGING_SIGN, Material.CRIMSON_WALL_SIGN, Material.DARK_OAK_WALL_HANGING_SIGN,
                            Material.DARK_OAK_WALL_SIGN, Material.JUNGLE_WALL_HANGING_SIGN, Material.JUNGLE_WALL_SIGN,
                            Material.MANGROVE_WALL_HANGING_SIGN, Material.MANGROVE_WALL_SIGN, Material.OAK_WALL_HANGING_SIGN,
                            Material.OAK_WALL_SIGN, Material.WARPED_WALL_HANGING_SIGN, Material.WARPED_WALL_SIGN,
                            Material.PALE_OAK_WALL_HANGING_SIGN, Material.PALE_OAK_WALL_SIGN, Material.SPRUCE_WALL_HANGING_SIGN,
                            Material.SPRUCE_WALL_SIGN, Material.CREEPER_WALL_HEAD, Material.PLAYER_WALL_HEAD,
                            Material.ZOMBIE_WALL_HEAD, Material.SKELETON_WALL_SKULL, Material.PIGLIN_WALL_HEAD,
                            Material.DEAD_BRAIN_CORAL_WALL_FAN, Material.DEAD_BUBBLE_CORAL_WALL_FAN, Material.DEAD_FIRE_CORAL_WALL_FAN,
                            Material.DEAD_HORN_CORAL_WALL_FAN, Material.DEAD_TUBE_CORAL_WALL_FAN, Material.BRAIN_CORAL_WALL_FAN,
                            Material.BUBBLE_CORAL_WALL_FAN, Material.FIRE_CORAL_WALL_FAN, Material.HORN_CORAL_WALL_FAN,
                            Material.TUBE_CORAL_WALL_FAN, Material.BROWN_WALL_BANNER, Material.WHITE_WALL_BANNER,
                            Material.GRAY_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER, Material.BLACK_WALL_BANNER,
                            Material.RED_WALL_BANNER, Material.ORANGE_WALL_BANNER, Material.YELLOW_WALL_BANNER,
                            Material.LIME_WALL_BANNER, Material.GREEN_WALL_BANNER, Material.CYAN_WALL_BANNER,
                            Material.LIGHT_BLUE_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.PURPLE_WALL_BANNER,
                            Material.MAGENTA_WALL_BANNER, Material.PINK_WALL_BANNER, Material.BAMBOO_SAPLING,
                            Material.BEETROOTS, Material.WHEAT, Material.BIG_DRIPLEAF_STEM, Material.CRIMSON_STEM,
                            Material.MELON_STEM, Material.PUMPKIN_STEM, Material.WHITE_CANDLE_CAKE,
                            Material.LIGHT_GRAY_CANDLE_CAKE, Material.GRAY_CANDLE_CAKE, Material.BLACK_CANDLE_CAKE,
                            Material.RED_CANDLE_CAKE, Material.ORANGE_CANDLE_CAKE, Material.YELLOW_CANDLE_CAKE,
                            Material.LIME_CANDLE_CAKE, Material.GREEN_CANDLE_CAKE, Material.CYAN_CANDLE_CAKE,
                            Material.LIGHT_BLUE_CANDLE_CAKE, Material.BLUE_CANDLE_CAKE, Material.PURPLE_CANDLE_CAKE,
                            Material.MAGENTA_CANDLE_CAKE, Material.PINK_CANDLE_CAKE, Material.BROWN_CANDLE_CAKE,
                            Material.BUBBLE_COLUMN, Material.CANDLE_CAKE, Material.CARROTS, Material.POTATOES
                            ).contains(item) && item != Material.ATTACHED_MELON_STEM && item != Material.ATTACHED_PUMPKIN_STEM){
                blocks.add(item);
            }
        }
        return blocks;
    }

    public List<Material> getCollectedBlocks(Player player){
        List<Material> blocks = new ArrayList<>();
        for (Material item : getAllBlocks()){
            if (wasFound(player, item)){
                blocks.add(item);
            }
        }
        return blocks;
    }

    public List<Material> getNotCollectedBlocks(Player player){
        List<Material> blocks = new ArrayList<>();
        for (Material item : getAllBlocks()){
            if (!wasFound(player, item)){
                blocks.add(item);
            }
        }
        return blocks;
    }

    private void createLink(Player request){
        World world = request.getWorld();
        createSlime(request, world);

        Player otherPlayer = duos.get(request);
        createSlime(otherPlayer, world);

        Slime requestSlime = chainPoint.get(request);
        Slime otherSlime = chainPoint.get(otherPlayer);

        requestSlime.setLeashHolder(otherSlime);
        otherSlime.setLeashHolder(requestSlime);

        if (!plugin.getConfig().contains("blocks_"+request.getUniqueId()+"_"+otherPlayer.getUniqueId())){
            plugin.getConfig().set("blocks_"+request.getUniqueId()+"_"+otherPlayer.getUniqueId(), List.of());
        }

        if (!plugin.getConfig().contains("blocks_"+otherPlayer.getUniqueId()+"_"+request.getUniqueId())){
            plugin.getConfig().set("blocks_"+otherPlayer.getUniqueId()+"_"+request.getUniqueId(), List.of());
        }

        plugin.saveConfig();

        request.sendMessage(Component.text("You successfully were linked to " + otherPlayer.getName() + "!", NamedTextColor.GREEN));
        otherPlayer.sendMessage(Component.text("You successfully were linked to " + request.getName() + "!", NamedTextColor.GREEN));
    }

    private void createSlime(Player player, World world) {
        Slime requestSlime = world.spawn(player.getLocation().clone().add(0, 0.5f, 0), Slime.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
        requestSlime.setSize(0);
        requestSlime.setAI(false);
        requestSlime.setInvisible(true);
        requestSlime.setInvulnerable(true);
        requestSlime.setCollidable(false);
        //player.hideEntity(plugin, requestSlime);
        chainPoint.put(player, requestSlime);
    }

    @Override
    public void run() {

        for (Player player : duos.keySet()){
            if (player.getLocation().distanceSquared(duos.get(player).getLocation()) >= 90){
                Location playerLoc = player.getLocation();
                Location linkedLoc = duos.get(player).getLocation();

                player.setVelocity(linkedLoc.toVector().subtract(playerLoc.toVector()).normalize().multiply(0.2f));
            }
        }

        for (Player player : chainPoint.keySet()){
            Slime slime = chainPoint.get(player);

            if (!slime.isLeashed()){
                Player duo = duos.get(player);
                player.teleport(duo);
                slime.teleport(player.getLocation().clone().add(0, 0.5f, 0));

                Slime slimeDuo = chainPoint.get(duo);

                slime.setLeashHolder(slimeDuo);
                slimeDuo.setLeashHolder(slime);

                player.sendMessage(Component.text("Your chain broke, but you got teleported to your duo and are linked back together!", NamedTextColor.RED));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().remove(Material.LEAD);
                        for (Item item : player.getWorld().getNearbyEntitiesByType(Item.class, player.getLocation(), 10)){
                            if (item.getItemStack().getType() == Material.LEAD){
                                item.remove();
                            }
                        }
                    }
                }.runTaskLater(plugin, 20L);
            }
            slime.teleport(player.getLocation().clone().add(0, 0.5f, 0));
        }

    }
}
