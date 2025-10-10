package fr.thefox580.collectEveryBlockDuo.listeners;

import fr.thefox580.collectEveryBlockDuo.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class onPlayerItemPickupEvent implements Listener {

    private final Main plugin;

    public onPlayerItemPickupEvent(Main plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPickupEvent(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player player){
            if (plugin.getMech().hasDuo(player)){
                Material item = event.getItem().getItemStack().getType();
                if (item.isBlock()){
                    plugin.getMech().addBlock(player, item);
                }
            }
        }
    }

}
