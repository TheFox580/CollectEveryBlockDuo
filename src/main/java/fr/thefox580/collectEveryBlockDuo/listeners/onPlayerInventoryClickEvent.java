package fr.thefox580.collectEveryBlockDuo.listeners;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
                } else if (PlainTextComponentSerializer.plainText().serialize(event.getView().title()).contains("Blocks List")){
                    event.setCancelled(true);
                    ItemStack item = event.getCurrentItem();
                    if (item != null){
                        if (item.getType() == Material.EMERALD || item.getType() == Material.REDSTONE){
                            if (PlainTextComponentSerializer.plainText().serialize(event.getView().title()).contains("Search")){
                                plugin.getBlocksGUI().openGUISearch(player, item.getAmount(), player.getPersistentDataContainer().get(new NamespacedKey(plugin, "searched_word"), PersistentDataType.STRING));
                            } else {
                                plugin.getBlocksGUI().openGUI(player, item.getAmount());
                            }
                        } else if (item.getType() == Material.COMPASS){
                            PersistentDataContainer pdc = player.getPersistentDataContainer();
                            pdc.set(new NamespacedKey(plugin, "search"), PersistentDataType.BOOLEAN, true);
                            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                            player.sendMessage(Component.text("Type in chat to search for blocks with a specific name", NamedTextColor.WHITE)
                                            .appendNewline()
                                            .append(Component.text("(or type /blocks (or click on this message) to clear the search result)", NamedTextColor.GRAY)
                                                    .clickEvent(ClickEvent.callback(clicker -> plugin.getBlocksGUI().openGUI(player, 1)))));
                        } else if (event.getSlot() == event.getView().getTopInventory().getSize() - 5){

                            String filter = "all";

                            PersistentDataContainer pdc = player.getPersistentDataContainer();
                            if (pdc.has(new NamespacedKey(plugin, "blocks_filter"))){
                                filter = pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING);
                            }

                            if (filter != null){

                                switch (filter){
                                    case "collected" -> pdc.set(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING, "not_collected");
                                    case "not_collected" -> pdc.set(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING, "all");
                                    default -> pdc.set(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING, "collected");
                                }
                            }

                            if (PlainTextComponentSerializer.plainText().serialize(event.getView().title()).contains("Search")){
                                plugin.getBlocksGUI().openGUISearch(player, 1, player.getPersistentDataContainer().get(new NamespacedKey(plugin, "searched_word"), PersistentDataType.STRING));
                            } else {
                                plugin.getBlocksGUI().openGUI(player, 1);
                            }
                        }
                    }
                } else if (List.of(InventoryType.CHEST, InventoryType.BARREL, InventoryType.DISPENSER,
                                   InventoryType.DROPPER, InventoryType.SHULKER_BOX, InventoryType.HOPPER,
                                   InventoryType.STONECUTTER).contains(type)){
                    ItemStack currentItem = event.getCurrentItem();
                    if (currentItem != null){
                        Material item = currentItem.getType();
                        if (item.isBlock()){
                            plugin.getMech().addBlock(player, item);
                        }
                    }
                }
            }
        }
    }
}
