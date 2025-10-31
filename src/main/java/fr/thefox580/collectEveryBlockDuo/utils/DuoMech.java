package fr.thefox580.collectEveryBlockDuo.utils;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
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

    public Player getDuo(Player player){
        return duos.get(player);
    }

    public void removeDuo(Player removed){
        Player duo = getDuo(removed);

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
        Player duo = getDuo(player);
        return Objects.requireNonNull(plugin.getConfig().getStringList("blocks_"+player.getUniqueId()+"_"+duo.getUniqueId())).contains(block.getKey().getKey());
    }

    public void addBlock(Player player, Material block){
        if (!wasFound(player, block)){

            if (getAllBlocks().contains(block)){

                Player duo = getDuo(player);

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
                            Material.SPRUCE_WALL_SIGN, Material.CREEPER_WALL_HEAD, Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD,
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
                            Material.BUBBLE_COLUMN, Material.CANDLE_CAKE, Material.CARROTS, Material.POTATOES,
                            Material.CAVE_VINES, Material.CAVE_VINES_PLANT, Material.COCOA, Material.DRAGON_WALL_HEAD,
                            Material.BEDROCK, Material.END_GATEWAY, Material.END_PORTAL,
                            Material.END_PORTAL_FRAME, Material.NETHER_PORTAL, Material.FARMLAND,
                            Material.FIRE, Material.DIRT_PATH, Material.POWDER_SNOW,
                            Material.FROSTED_ICE, Material.FROGSPAWN, Material.KELP_PLANT,
                            Material.JIGSAW, Material.LIGHT, Material.COMMAND_BLOCK,
                            Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK, Material.BARRIER,
                            Material.LAVA, Material.WATER, Material.LAVA_CAULDRON,
                            Material.WATER_CAULDRON, Material.POWDER_SNOW_CAULDRON, Material.SPAWNER,
                            Material.MOVING_PISTON, Material.PETRIFIED_OAK_SLAB, Material.PISTON_HEAD,
                            Material.PITCHER_CROP, Material.POTTED_ACACIA_SAPLING, Material.POTTED_ALLIUM,
                            Material.POTTED_AZALEA_BUSH, Material.POTTED_AZURE_BLUET, Material.POTTED_BAMBOO,
                            Material.POTTED_BIRCH_SAPLING, Material.POTTED_BLUE_ORCHID, Material.POTTED_BROWN_MUSHROOM,
                            Material.POTTED_CACTUS, Material.POTTED_CHERRY_SAPLING, Material.POTTED_CLOSED_EYEBLOSSOM,
                            Material.POTTED_CORNFLOWER, Material.POTTED_CRIMSON_FUNGUS, Material.POTTED_CRIMSON_ROOTS,
                            Material.POTTED_DANDELION, Material.POTTED_DARK_OAK_SAPLING, Material.POTTED_DEAD_BUSH,
                            Material.POTTED_FERN, Material.POTTED_FLOWERING_AZALEA_BUSH, Material.POTTED_JUNGLE_SAPLING,
                            Material.POTTED_LILY_OF_THE_VALLEY, Material.POTTED_MANGROVE_PROPAGULE, Material.POTTED_OAK_SAPLING,
                            Material.POTTED_OPEN_EYEBLOSSOM, Material.POTTED_ORANGE_TULIP, Material.POTTED_OXEYE_DAISY,
                            Material.POTTED_PALE_OAK_SAPLING, Material.POTTED_PINK_TULIP, Material.POTTED_POPPY,
                            Material.POTTED_RED_MUSHROOM, Material.POTTED_RED_TULIP, Material.POTTED_SPRUCE_SAPLING,
                            Material.POTTED_TORCHFLOWER, Material.POTTED_WARPED_FUNGUS, Material.POTTED_WARPED_ROOTS,
                            Material.POTTED_WHITE_TULIP, Material.POTTED_WITHER_ROSE, Material.REDSTONE_WALL_TORCH,
                            Material.REDSTONE_WIRE, Material.SOUL_FIRE, Material.SOUL_WALL_TORCH,
                            Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID, Material.SWEET_BERRY_BUSH,
                            Material.TALL_SEAGRASS, Material.TEST_BLOCK, Material.TEST_INSTANCE_BLOCK,
                            Material.TORCHFLOWER_CROP, Material.TRIAL_SPAWNER, Material.TRIPWIRE,
                            Material.TWISTING_VINES_PLANT, Material.WEEPING_VINES_PLANT, Material.VAULT,
                            Material.WITHER_SKELETON_WALL_SKULL
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

        Player otherPlayer = getDuo(request);
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

        Slime requestSlime = world.spawn(player.getLocation().clone().add(0, 0.6f, 0.1f), Slime.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
        requestSlime.setSize(0);

        Objects.requireNonNull(requestSlime.getAttribute(Attribute.SCALE)).setBaseValue(0.2f);

        requestSlime.setAI(false);
        requestSlime.setInvisible(true);
        requestSlime.setInvulnerable(true);
        requestSlime.setCollidable(false);
        chainPoint.put(player, requestSlime);
    }

    private void noLongerShowWarnMessage(Player player){

        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "slimeHiddenMessage"), PersistentDataType.BOOLEAN, true);
        player.sendMessage(Component.text("We will no longer warn you about your and your partner's slimes disappearing / reappearing!", NamedTextColor.GREEN));

    }

    @Override
    public void run() {

        for (Player player : duos.keySet()){
            if (player.getLocation().distanceSquared(getDuo(player).getLocation()) >= 90){
                Location playerLoc = player.getLocation();
                Location linkedLoc = getDuo(player).getLocation();

                player.setVelocity(linkedLoc.toVector().subtract(playerLoc.toVector()).normalize().multiply(0.2f));
            }
        }

        for (Player player : chainPoint.keySet()){
            Slime slime = chainPoint.get(player);

            if (!slime.isLeashed()){
                Player duo = getDuo(player);
                player.teleport(duo);

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

            if (player.getLocation().getPitch() > 60){
                if (player.canSee(slime)){
                    player.hideEntity(plugin, slime);
                    Player duo = getDuo(player);
                    Slime slimeDuo = chainPoint.get(duo);
                    player.hideEntity(plugin, slimeDuo);

                    if (Boolean.FALSE.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "slimeHiddenMessage"), PersistentDataType.BOOLEAN))){
                        player.sendMessage(Component.text("Your and your partner's slimes have been hidden for better visibility. Click ", NamedTextColor.RED)
                                .append(Component.text("[HERE]", NamedTextColor.RED, TextDecoration.BOLD).clickEvent(ClickEvent.callback(audience -> noLongerShowWarnMessage(player))))
                                .append(Component.text(" to no longer show warning message about this.", NamedTextColor.RED)));
                    }
                }
            } else if (!player.canSee(slime)){
                player.showEntity(plugin, slime);
                Player duo = getDuo(player);
                Slime slimeDuo = chainPoint.get(duo);
                player.showEntity(plugin, slimeDuo);

                if (Boolean.FALSE.equals(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "slimeHiddenMessage"), PersistentDataType.BOOLEAN))){
                    player.sendMessage(Component.text("Your and your partner's slimes have been shown again. Click ", NamedTextColor.RED)
                            .append(Component.text("[HERE]", NamedTextColor.RED, TextDecoration.BOLD).clickEvent(ClickEvent.callback(audience -> noLongerShowWarnMessage(player))))
                            .append(Component.text(" to no longer show warning message about this.", NamedTextColor.RED)));
                }
            }
            slime.teleport(player.getLocation().clone().add(0, 0.65f, 0.0f));
        }
    }
}
