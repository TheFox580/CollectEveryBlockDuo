package fr.thefox580.collectEveryBlockDuo;

import fr.thefox580.collectEveryBlockDuo.commands.BlocksCommand;
import fr.thefox580.collectEveryBlockDuo.commands.DuoCommand;
import fr.thefox580.collectEveryBlockDuo.listeners.*;
import fr.thefox580.collectEveryBlockDuo.utils.BlocksGUI;
import fr.thefox580.collectEveryBlockDuo.utils.DuoMech;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public final class Main extends JavaPlugin {

    private DuoMech duoMech;
    private BlocksGUI blocksGUI;
    private Team noColTeam;

    @Override
    public void onEnable() {
        // Plugin startup logic

        duoMech = new DuoMech(this);
        blocksGUI = new BlocksGUI(this);

        noColTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("no_collision");
        if (noColTeam == null) {
            noColTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam("no_collision");
            noColTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }

        registerCommands();
        registerListeners();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        duoMech.stopTask();
        duoMech.killAllSlimes();

        blocksGUI.stopTask();
    }

    public DuoMech getMech() {
        return duoMech;
    }

    public BlocksGUI getBlocksGUI(){
        return blocksGUI;
    }

    public Team getNoColTeam(){
        return noColTeam;
    }

    public void setNoColTeam(Team team){
        noColTeam = team;
    }

    private void registerCommands(){
        new DuoCommand(this);
        new BlocksCommand(this);
    }

    private void registerListeners(){
        new onPlayerJoinEvent(this);
        new onPlayerLeaveEvent(this);
        new onPlayerItemPickupEvent(this);
        new onPlayerItemCraftEvent(this);
        new onPlayerInventoryClickEvent(this);
    }
}
