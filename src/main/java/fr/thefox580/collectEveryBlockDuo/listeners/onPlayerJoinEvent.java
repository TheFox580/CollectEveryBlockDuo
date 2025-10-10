package fr.thefox580.collectEveryBlockDuo.listeners;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

public class onPlayerJoinEvent implements Listener {

    private final Main plugin;

    public onPlayerJoinEvent(Main plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Team noColTeam = plugin.getNoColTeam();
        if (noColTeam == null){
            noColTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("no_collision");
            if (noColTeam == null){
                noColTeam = player.getScoreboard().registerNewTeam("no_collision");
                noColTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                plugin.setNoColTeam(noColTeam);
            }
        }
        noColTeam.addPlayer(player);

        event.joinMessage(Component.text(">> " + player.getName(), NamedTextColor.GREEN));
    }
}
