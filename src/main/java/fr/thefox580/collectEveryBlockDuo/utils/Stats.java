package fr.thefox580.collectEveryBlockDuo.utils;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stats implements Runnable{

    private final BukkitTask addTimeTask;
    private final Main plugin;


    public Stats(Main plugin){
        this.plugin = plugin;
        addTimeTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 0, 20L);
    }


    public List<Component> get(Player player){
        Player duo = plugin.getMech().getDuo(player);
        List<Component> stats = new ArrayList<>();

        stats.add(Component.newline());

        stats.add(Component.text("While playing with " + duo.getName() + ", you've:", NamedTextColor.AQUA));

        int blocksCollected = Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "blocks_collected_with_"+duo.getUniqueId()), PersistentDataType.INTEGER));
        int totalCollected = plugin.getMech().getCollectedBlocks(player).size();
        int duoBlocksCollected = Objects.requireNonNull(duo.getPersistentDataContainer().get(new NamespacedKey(plugin, "blocks_collected_with_"+player.getUniqueId()), PersistentDataType.INTEGER));
        Component hoverBlocks = getHoverBlocks(blocksCollected, duoBlocksCollected, duo);
        stats.add(Component.text("  → Collected " + blocksCollected + "/" + totalCollected + " blocks!", NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(hoverBlocks)));

        int deaths = Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "deaths_with_"+duo.getUniqueId()), PersistentDataType.INTEGER));
        int duoDeaths = Objects.requireNonNull(duo.getPersistentDataContainer().get(new NamespacedKey(plugin, "deaths_with_"+player.getUniqueId()), PersistentDataType.INTEGER));
        Component hoverDeaths = getHoverDeaths(deaths, duoDeaths, duo);
        stats.add(Component.text("  → Died " + deaths + " times!", NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(hoverDeaths)));

        int time = Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "time_played_with_"+duo.getUniqueId()), PersistentDataType.INTEGER));
        Component hoverTime = getHoverTime(player);
        stats.add(Component.text("  → Played for " + Conversions.secondsToFormattedTime(time), NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(hoverTime)));

        stats.add(Component.newline());

        return stats;
    }

    private @NotNull Component getHoverBlocks(int blocksCollected, int duoBlocksCollected, Player duo) {
        Component hoverBlocks;
        if (blocksCollected > duoBlocksCollected){
            hoverBlocks = Component.text("That's " + (blocksCollected - duoBlocksCollected) + " more block(s) than " + duo.getName() + "!", NamedTextColor.GREEN);
        } else if (blocksCollected == duoBlocksCollected){
            hoverBlocks = Component.text("That's the exact same amount of block(s) as " + duo.getName() + "...", NamedTextColor.YELLOW);
        } else {
            hoverBlocks = Component.text("That's " + (duoBlocksCollected - blocksCollected) + " less block(s) than " + duo.getName() + ".", NamedTextColor.RED);
        }
        return hoverBlocks;
    }

    private @NotNull Component getHoverDeaths(int deaths, int duoDeaths, Player duo) {
        Component hoverDeaths;
        if (deaths > duoDeaths){
            hoverDeaths = Component.text("That's " + (deaths - duoDeaths) + " more death(s) than " + duo.getName() + "!", NamedTextColor.GREEN);
        } else if (deaths == duoDeaths){
            hoverDeaths = Component.text("That's the exact same amount of death(s) as " + duo.getName() + "...", NamedTextColor.YELLOW);
        } else {
            hoverDeaths = Component.text("That's " + (deaths - duoDeaths) + " less death(s) than " + duo.getName() + ".", NamedTextColor.RED);
        }
        return hoverDeaths;
    }

    private @NotNull Component getHoverTime(Player player) {
        Component hoverTime;
        float progression = (float) plugin.getMech().getCollectedBlocks(player).size() / plugin.getMech().getAllBlocks().size();
        if (progression < 0.25){
            hoverTime = Component.text("And yet you've barely started...", NamedTextColor.RED);
        } else if (progression < 0.5){
            hoverTime = Component.text("Hey, almost halfway through though!", NamedTextColor.GOLD);
        } else if (progression < 0.75){
            hoverTime = Component.text("The hard part is done, just do what you've just done, but again!", NamedTextColor.YELLOW);
        } else if (progression < 1) {
            hoverTime = Component.text("Ok I lied, this is the hard part... Good luck!", NamedTextColor.GREEN);
        } else {
            hoverTime = Component.text("Oh wow, you've actually done it!", NamedTextColor.DARK_GREEN);
        }
        return hoverTime;
    }

    public void createStats(Player player){
        Player duo = plugin.getMech().getDuo(player);

        if (!player.getPersistentDataContainer().has(new NamespacedKey(plugin, "blocks_collected_with_"+duo.getUniqueId()))){
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "blocks_collected_with_"+duo.getUniqueId()), PersistentDataType.INTEGER, 0);
        }

        if (!player.getPersistentDataContainer().has(new NamespacedKey(plugin, "deaths_with_"+duo.getUniqueId()))){
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "deaths_with_"+duo.getUniqueId()), PersistentDataType.INTEGER, 0);
        }

        if (!player.getPersistentDataContainer().has(new NamespacedKey(plugin, "time_played_with_"+duo.getUniqueId()))){
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "time_played_with_"+duo.getUniqueId()), PersistentDataType.INTEGER, 0);
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()){
            if (plugin.getMech().hasDuo(player)){
                Player duo = plugin.getMech().getDuo(player);
                int currentTime = Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "time_played_with_"+duo.getUniqueId()), PersistentDataType.INTEGER));
                player.getPersistentDataContainer().set(new NamespacedKey(plugin, "time_played_with_"+duo.getUniqueId()), PersistentDataType.INTEGER, currentTime+1);
            }
        }
    }

    public void stopTask(){
        addTimeTask.cancel();
    }
}
