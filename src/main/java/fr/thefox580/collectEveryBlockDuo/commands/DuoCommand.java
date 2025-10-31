package fr.thefox580.collectEveryBlockDuo.commands;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DuoCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public DuoCommand(Main plugin){
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("duo")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (sender instanceof Player player){

            if (args.length == 0){

                player.sendMessage(Component.text("You must enter at least another argument!", NamedTextColor.RED));

                return false;
            }

            if (args.length == 1){
                NamespacedKey key = new NamespacedKey(plugin, "request");
                switch (args[0]) {
                    case "accept" -> {
                        if (player.getPersistentDataContainer().has(key)) {
                            String name = Objects.requireNonNull(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));
                            OfflinePlayer requester = Objects.requireNonNull(Bukkit.getOfflinePlayer(UUID.fromString(name)));
                            if (requester.isOnline()) {
                                Player requesterOnline = Objects.requireNonNull(requester.getPlayer());
                                plugin.getMech().addDuo(player, requesterOnline);
                            } else {
                                player.sendMessage(Component.text(requester.getName() + " is offline, try again later.", NamedTextColor.RED));
                                return false;
                            }
                            player.getPersistentDataContainer().remove(key);
                        }
                    }
                    case "deny", "refuse" -> {
                        if (player.getPersistentDataContainer().has(key)) {
                            String name = Objects.requireNonNull(player.getPersistentDataContainer().get(key, PersistentDataType.STRING));
                            OfflinePlayer requester = Objects.requireNonNull(Bukkit.getOfflinePlayer(UUID.fromString(name)));
                            player.getPersistentDataContainer().remove(key);
                            player.sendMessage(Component.text("You refused " + requester.getName() + "'s request.", NamedTextColor.RED));
                            return false;
                        }
                    }
                    case "leave", "quit" -> {
                        if (plugin.getMech().hasDuo(player)) {
                            plugin.getMech().removeDuo(player);
                        } else {
                            return false;
                        }
                    }
                }
            } else if (args.length == 2 && (args[0].equals("join") || args[0].equals("request"))){
                if (!args[1].equals(player.getName())){
                    OfflinePlayer requested = Objects.requireNonNull(Bukkit.getOfflinePlayer(args[1]));

                    if (requested.isOnline()){
                        Player requestedOnline = Objects.requireNonNull(requested.getPlayer());

                        if (plugin.getMech().hasDuo(requestedOnline)){
                            player.sendMessage(Component.text(requested.getName() + " already is in a duo with " + plugin.getMech().getDuo(requestedOnline).getName() + ".", NamedTextColor.RED));
                            return false;
                        }

                        NamespacedKey key = new NamespacedKey(plugin, "request");
                        requestedOnline.getPersistentDataContainer().set(key, PersistentDataType.STRING, player.getUniqueId().toString());
                        player.sendMessage(Component.text("You successfully sent a duo request to " + requested.getName() + "!", NamedTextColor.GREEN));
                        requestedOnline.sendMessage(
                                Component.text(player.getName() + " has invited you to join their duo! ")
                                        .append(Component.text("[ACCEPT]", NamedTextColor.GREEN, TextDecoration.BOLD)
                                                .clickEvent(ClickEvent.runCommand("duo accept")))
                                        .append(Component.text(" "))
                                        .append(Component.text("[REFUSE]", NamedTextColor.RED, TextDecoration.BOLD)
                                                .clickEvent(ClickEvent.runCommand("duo refuse")))
                        );
                    } else {
                        player.sendMessage(Component.text(requested.getName() + " is offline, please invite a player who's online.", NamedTextColor.RED));
                        return false;
                    }
                } else {
                    player.sendMessage(Component.text("You cannot invite yourself to be in a duo.", NamedTextColor.RED));
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> tab = new ArrayList<>();

        switch (args.length) {
            case 1:
                tab.add("accept");
                tab.add("refuse");
                tab.add("quit");
                tab.add("join");

                break;
            case 2:
                if (args[0].equals("join"))
                    for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
                        tab.add(loopPlayer.getName());
                    }

                break;
        }
        return tab.stream().filter(completion -> completion.startsWith(args[args.length - 1])).toList();
    }
}
