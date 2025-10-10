package fr.thefox580.collectEveryBlockDuo.listeners;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;
import java.util.Objects;

public class onPlayerInventoryClickEvent implements Listener {

    private final Main plugin;

    public onPlayerInventoryClickEvent(Main plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        if (event.getWhoClicked() instanceof Player player){
            if (plugin.getMech().hasDuo(player)){
                InventoryType type = event.getView().getType();
                if (type == InventoryType.FURNACE && event.getSlot() == 2){
                    Material item = Objects.requireNonNull(event.getCurrentItem()).getType();
                    if (item.isBlock()){
                        plugin.getMech().addBlock(player, item);
                    }
                } else if (event.getView().title().equals(Component.text("All Blocks List"))){
                    event.setCancelled(true);
                    if (Objects.requireNonNull(event.getCurrentItem()).getType() == Material.EMERALD){
                        plugin.getBlocksGUI().openGUI(player, event.getCurrentItem().getAmount());
                    }
                } else if (List.of(InventoryType.CHEST, InventoryType.BARREL, InventoryType.DISPENSER,
                                   InventoryType.DROPPER, InventoryType.SHULKER_BOX, InventoryType.HOPPER,
                                   InventoryType.STONECUTTER).contains(type)){
                    Material item = Objects.requireNonNull(event.getCurrentItem()).getType();
                    if (item.isBlock()){
                        plugin.getMech().addBlock(player, item);
                    }
                }
            }
        }
    }
}
