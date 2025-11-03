package fr.thefox580.collectEveryBlockDuo.listeners;

import fr.thefox580.collectEveryBlockDuo.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class onPlayerDeathEvent implements Listener {

    private final Main plugin;

    public onPlayerDeathEvent(Main plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event){
        Player player = event.getPlayer();

        if (plugin.getMech().hasDuo(player)){
            Player duo = plugin.getMech().getDuo(player);

            int deaths = Objects.requireNonNull(player.getPersistentDataContainer().get(new NamespacedKey(plugin, "deaths_with_"+duo.getUniqueId()), PersistentDataType.INTEGER));
            player.getPersistentDataContainer().set(new NamespacedKey(plugin, "deaths_with_"+duo.getUniqueId()), PersistentDataType.INTEGER, deaths+1);
        }
    }

}
