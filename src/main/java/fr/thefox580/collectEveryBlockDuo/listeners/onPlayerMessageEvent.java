package fr.thefox580.collectEveryBlockDuo.listeners;

import fr.thefox580.collectEveryBlockDuo.Main;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Locale;

public class onPlayerMessageEvent implements Listener {

    private final Main plugin;

    public onPlayerMessageEvent(Main plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onMessageEvent(AsyncChatEvent event){

        Player player = event.getPlayer();

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        if (Boolean.TRUE.equals(pdc.get(new NamespacedKey(plugin, "search"), PersistentDataType.BOOLEAN))){
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    pdc.set(new NamespacedKey(plugin, "searched_word"), PersistentDataType.STRING, PlainTextComponentSerializer.plainText().serialize(event.message()).toLowerCase(Locale.ROOT));
                    plugin.getBlocksGUI().openGUISearch(player, 1, pdc.get(new NamespacedKey(plugin, "searched_word"), PersistentDataType.STRING));
                    player.sendMessage(Component.text("Searching for blocks with name : ").append(event.message()));
                }
            }.runTask(plugin);
        }
        pdc.set(new NamespacedKey(plugin, "search"), PersistentDataType.BOOLEAN, false);
    }

}
