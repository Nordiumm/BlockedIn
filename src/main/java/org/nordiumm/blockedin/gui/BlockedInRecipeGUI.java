package org.nordiumm.blockedin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.recipe.BlockedInRecipes;

public class BlockedInRecipeGUI {

    private final BlockedIn plugin;
    private final BlockedInRecipes recipes;

    public BlockedInRecipeGUI(BlockedIn plugin) {
        this.plugin = plugin;
        this.recipes = plugin.getRecipes();
    }

    public void openRecipeMenu(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                36,
                "§eBlockedIn Recipes"
        );

        // Water bucket
        inventory.setItem(
                10,
                recipes.createSpecialBucket(
                        Material.WATER_BUCKET,
                        "blocked_in_water"
                )
        );

        // Lava bucket
        inventory.setItem(
                12,
                recipes.createSpecialBucket(
                        Material.LAVA_BUCKET,
                        "blocked_in_lava"
                )
        );

        // TNT
        inventory.setItem(
                14,
                recipes.createSpecialTNT()
        );

        inventory.setItem(
                16,
                new ItemStack(
                        Material.BREAD,
                        9
                )
        );

        // Close button
        inventory.setItem(
                31,
                createButton(
                        Material.BARRIER,
                        "§c✖ Close"
                )
        );

        player.openInventory(inventory);
    }

    private ItemStack createButton(
            Material material,
            String name
    ) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }
}