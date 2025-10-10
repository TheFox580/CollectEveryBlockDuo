package fr.thefox580.collectEveryBlockDuo.utils;

import fr.thefox580.collectEveryBlockDuo.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlocksGUI {

    private final Main plugin;

    public BlocksGUI(Main plugin){
        this.plugin = plugin;
    }

    public void openGUI(Player player, int page){
        Inventory gui = Bukkit.createInventory(null, 9*6, Component.text("All Blocks List"));

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        if (!pdc.has(new NamespacedKey(plugin,"blocks_filter"))){
            pdc.set(new NamespacedKey(plugin,"blocks_filter"),
                    PersistentDataType.STRING,
                    "all");
        }

        if (pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING).equals("all")){
            showAllBlocks(gui, player, page);
        } else if (pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING).equals("collected")){
            //showCollectedBlocks(gui, player, page);
        } else if (pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING).equals("not_collected")){
            //showNotCollectedBlocks(gui, player, page);
        } else {
            player.sendMessage(Component.text("There was an error initializing the GUI, contact TheFox580 if that error persists.\nERROR_CODE:NO_FILTER_SETTING_FOUND", NamedTextColor.RED));
        }

        player.openInventory(gui);
    }

    private void showAllBlocks(Inventory gui, Player player, int page){
        List<Material> allBlocks = plugin.getMech().getAllBlocks();

        List<ItemStack> blocks = new ArrayList<>();
        int index = (page-1)*35;
        while (blocks.size() < 9*5){
            ItemStack item = ItemStack.of(allBlocks.get(index));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.customName(item.effectiveName().color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            if (plugin.getMech().wasFound(player, item.getType())){
                itemMeta.addEnchant(Enchantment.MENDING, 0, true);
                itemMeta.customName(Objects.requireNonNull(itemMeta.customName()).append(Component.text(" (Completed)")));
                itemMeta.customName(Objects.requireNonNull(itemMeta.customName()).color(NamedTextColor.GREEN));
            }

            item.setItemMeta(itemMeta);

            blocks.add(item);
            index++;
        }
        for (ItemStack itemStack : blocks){
            gui.addItem(itemStack);
        }

        if (page > 1){
            createPrevPage(gui, page);
        }
        createNextPage(gui, page);

    }

    private void createNextPage(Inventory gui, int currentPage){

        ItemStack next = ItemStack.of(Material.EMERALD, currentPage+1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(Component.text("Next Page", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        next.setItemMeta(nextMeta);

        gui.setItem(gui.getSize()-4, next);
    }

    private void createPrevPage(Inventory gui, int currentPage){

        ItemStack next = ItemStack.of(Material.REDSTONE, currentPage-1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(Component.text("Previous Page", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        next.setItemMeta(nextMeta);

        gui.setItem(gui.getSize()-6, next);
    }
}
