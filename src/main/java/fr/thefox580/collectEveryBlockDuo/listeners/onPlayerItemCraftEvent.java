package fr.thefox580.collectEveryBlockDuo.listeners;

import fr.thefox580.collectEveryBlockDuo.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.Objects;

public class onPlayerItemCraftEvent implements Listener {

    private final Main plugin;

    public onPlayerItemCraftEvent(Main plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraftEvent(CraftItemEvent event){
        if (event.getWhoClicked() instanceof Player player){
            Material item = Objects.requireNonNull(event.getCurrentItem()).getType();
            if (item.isBlock()){
                plugin.getMech().addBlock(player, item);
            }
        }
    }

}
