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
        Inventory gui = Bukkit.createInventory(null, 9*6, Component.text("Blocks List"));

        PersistentDataContainer pdc = player.getPersistentDataContainer();

        if (!pdc.has(new NamespacedKey(plugin,"blocks_filter"))){
            pdc.set(new NamespacedKey(plugin,"blocks_filter"),
                    PersistentDataType.STRING,
                    "all");
        }

        if (Objects.equals(pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING), "all")){
            showAllBlocks(gui, player, page);
        } else if (Objects.equals(pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING), "collected")){
            showCollectedBlocks(gui, player, page);
        } else if (Objects.equals(pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING), "not_collected")){
            showNotCollectedBlocks(gui, player, page);
        } else {
            player.sendMessage(Component.text("There was an error initializing the GUI, contact TheFox580 if that error persists.\nERROR_CODE:NO_FILTER_SETTING_FOUND", NamedTextColor.RED));
        }

        player.openInventory(gui);
    }

    private void showAllBlocks(Inventory gui, Player player, int page){
        List<Material> allBlocks = plugin.getMech().getAllBlocks();

        List<ItemStack> blocks = new ArrayList<>();
        int index = 45*(page-1);
        while (index < allBlocks.size() && blocks.size() < 9*5){
            ItemStack item = ItemStack.of(allBlocks.get(index));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.customName(item.effectiveName().color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            if (plugin.getMech().wasFound(player, item.getType())){
                itemMeta.addEnchant(Enchantment.MENDING, 1, true);
                itemMeta.customName(Objects.requireNonNull(itemMeta.customName()).append(Component.text(" (Completed)")));
                itemMeta.customName(Objects.requireNonNull(itemMeta.customName()).color(NamedTextColor.GREEN));
            }

            item.setItemMeta(itemMeta);

            blocks.add(item);
            index++;
        }

        createFinalGUIPart(gui, player, page, blocks, index, allBlocks.size());

    }

    private void showCollectedBlocks(Inventory gui, Player player, int page){
        List<Material> collectedBlocks = plugin.getMech().getCollectedBlocks(player);

        List<ItemStack> blocks = new ArrayList<>();
        int index = 45*(page-1);
        while (index < collectedBlocks.size() && blocks.size() < 9*5){
            ItemStack item = ItemStack.of(collectedBlocks.get(index));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.customName(item.effectiveName().color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            itemMeta.addEnchant(Enchantment.MENDING, 1, true);
            itemMeta.customName(Objects.requireNonNull(itemMeta.customName()).append(Component.text(" (Completed)")));
            itemMeta.customName(Objects.requireNonNull(itemMeta.customName()).color(NamedTextColor.GREEN));

            item.setItemMeta(itemMeta);

            blocks.add(item);
            index++;
        }

        createFinalGUIPart(gui, player, page, blocks, index, collectedBlocks.size());

    }

    private void showNotCollectedBlocks(Inventory gui, Player player, int page){
        List<Material> notCollectedBlocks = plugin.getMech().getNotCollectedBlocks(player);

        List<ItemStack> blocks = new ArrayList<>();
        int index = 45*(page-1);
        while (index < notCollectedBlocks.size() && blocks.size() < 9*5){
            ItemStack item = ItemStack.of(notCollectedBlocks.get(index));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.customName(item.effectiveName().color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));

            item.setItemMeta(itemMeta);

            blocks.add(item);
            index++;
        }

        createFinalGUIPart(gui, player, page, blocks, index, notCollectedBlocks.size());

    }

    private void createFinalGUIPart(Inventory gui, Player player, int page, List<ItemStack> shownBlocks, int finalIndex, int sizeList){
        for (ItemStack itemStack : shownBlocks){
            gui.addItem(itemStack);
        }

        if (page > 1){
            createPrevPage(gui, page);
        }
        createFilter(gui, player);
        if (finalIndex < sizeList){
            createNextPage(gui, page);
        }
    }

    private void createNextPage(Inventory gui, int currentPage){

        ItemStack next = ItemStack.of(Material.EMERALD, currentPage+1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(Component.text("Next Page", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        next.setItemMeta(nextMeta);

        gui.setItem(gui.getSize()-4, next);
    }

    private void createFilter(Inventory gui, Player player){

        ItemStack filter = ItemStack.of(Material.HOPPER);
        ItemMeta filterMeta = filter.getItemMeta();
        filterMeta.displayName(Component.text("Previous Page", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        filterMeta.lore(createFilterLore(player));

        filter.setItemMeta(filterMeta);

        gui.setItem(gui.getSize()-5, filter);
    }

    private List<Component> createFilterLore(Player player){
        List<Component> lore = new ArrayList<>();

        String filter = "all";

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (pdc.has(new NamespacedKey(plugin, "blocks_filter"))){
            filter = pdc.get(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING);
        } else {
            pdc.set(new NamespacedKey(plugin, "blocks_filter"), PersistentDataType.STRING, "all");
        }

        if (filter != null){

            Component filterComp = switch (filter){
                case "collected" -> Component.text("Blocks collected", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false);
                case "not_collected" -> Component.text("Blocks not collected", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false);
                default -> Component.text("All Blocks", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false);
            };

            lore.add(Component.text("Current Filter : ", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                    .append(filterComp));

            switch (filter){
                case "collected": {
                    lore.add(Component.text("All Blocks", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    lore.add(Component.text("> Blocks collected", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                    lore.add(Component.text("Blocks not collected", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    break;
                }
                case "not_collected": {
                    lore.add(Component.text("All Blocks", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    lore.add(Component.text("Blocks collected", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    lore.add(Component.text("> Blocks not collected", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                    break;
                }
                default: {
                    lore.add(Component.text("> All Blocks", NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                    lore.add(Component.text("Blocks collected", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    lore.add(Component.text("Blocks not collected", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                    break;
                }
            }
        }

        return lore;
    }

    private void createPrevPage(Inventory gui, int currentPage){

        ItemStack next = ItemStack.of(Material.REDSTONE, currentPage-1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(Component.text("Previous Page", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

        next.setItemMeta(nextMeta);

        gui.setItem(gui.getSize()-6, next);
    }
}
