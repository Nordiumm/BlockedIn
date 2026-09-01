package org.nordiumm.blockedin.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.recipe.BlockedInRecipes;

public class BlockedInRecipeListener implements Listener {

    private final BlockedIn plugin;
    private final BlockedInRecipes recipes;

    public BlockedInRecipeListener(BlockedIn plugin) {
        this.plugin = plugin;
        this.recipes = plugin.getRecipes();
    }

    private boolean isRecipeGUI(String title) {
        return title.startsWith("§eBlockedIn");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!isRecipeGUI(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null
                || event.getClickedInventory()
                != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        if (clicked == null
                || clicked.getType() == Material.AIR) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals("§eBlockedIn Recipes")) {

            if (clicked.getType() == Material.WATER_BUCKET) {
                openWaterRecipe(player);
                return;
            }

            if (clicked.getType() == Material.LAVA_BUCKET) {
                openLavaRecipe(player);
                return;
            }

            if (clicked.getType() == Material.TNT
                    && recipes.isSpecialTNT(clicked)) {

                openTNTRecipe(player);
                return;
            }

            if (clicked.getType() == Material.BREAD) {
                openHayToBreadRecipe(player);
                return;
            }

            if (clicked.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
        }

        if (title.equals("§eBlockedIn • Water Recipe")) {

            if (clicked.getType() == Material.ARROW) {
                openMainRecipeMenu(player);
                return;
            }
        }

        if (title.equals("§eBlockedIn • Lava Recipe")) {

            if (clicked.getType() == Material.ARROW) {
                openMainRecipeMenu(player);
                return;
            }
        }

        if (title.equals("§eBlockedIn • TNT Recipe")) {

            if (clicked.getType() == Material.ARROW) {
                openMainRecipeMenu(player);
                return;
            }
        }

        if (title.equals("§eBlockedIn • Hay to Bread Recipe")) {

            if (clicked.getType() == Material.ARROW) {
                openMainRecipeMenu(player);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!isRecipeGUI(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);
    }

    public void openMainRecipeMenu(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                36,
                "§eBlockedIn Recipes"
        );

        inventory.setItem(
                10,
                recipes.createSpecialBucket(
                        Material.WATER_BUCKET,
                        "blocked_in_water"
                )
        );

        inventory.setItem(
                12,
                recipes.createSpecialBucket(
                        Material.LAVA_BUCKET,
                        "blocked_in_lava"
                )
        );

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

        inventory.setItem(
                31,
                createButton(
                        Material.BARRIER,
                        "§c✖ Close"
                )
        );

        player.openInventory(inventory);
    }

    private void openWaterRecipe(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                45,
                "§eBlockedIn • Water Recipe"
        );

        inventory.setItem(
                10,
                new ItemStack(Material.IRON_INGOT)
        );

        inventory.setItem(
                11,
                new ItemStack(Material.SAND)
        );

        inventory.setItem(
                12,
                new ItemStack(Material.IRON_INGOT)
        );

        inventory.setItem(
                19,
                new ItemStack(Material.SAND)
        );

        inventory.setItem(
                20,
                new ItemStack(Material.BUCKET)
        );

        inventory.setItem(
                21,
                new ItemStack(Material.SAND)
        );

        inventory.setItem(
                28,
                new ItemStack(Material.IRON_INGOT)
        );

        inventory.setItem(
                29,
                new ItemStack(Material.SAND)
        );

        inventory.setItem(
                30,
                new ItemStack(Material.IRON_INGOT)
        );

        inventory.setItem(
                24,
                recipes.createSpecialBucket(
                        Material.WATER_BUCKET,
                        "blocked_in_water"
                )
        );

        inventory.setItem(
                36,
                createButton(
                        Material.ARROW,
                        "§e← Back"
                )
        );

        player.openInventory(inventory);
    }

    private void openLavaRecipe(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                45,
                "§eBlockedIn • Lava Recipe"
        );

        inventory.setItem(
                10,
                new ItemStack(Material.DIAMOND)
        );

        inventory.setItem(
                11,
                new ItemStack(Material.OBSIDIAN)
        );

        inventory.setItem(
                12,
                new ItemStack(Material.DIAMOND)
        );

        inventory.setItem(
                19,
                new ItemStack(Material.IRON_INGOT)
        );

        inventory.setItem(
                20,
                new ItemStack(Material.BUCKET)
        );

        inventory.setItem(
                21,
                new ItemStack(Material.IRON_INGOT)
        );

        inventory.setItem(
                28,
                new ItemStack(Material.DIAMOND)
        );

        inventory.setItem(
                29,
                new ItemStack(Material.OBSIDIAN)
        );

        inventory.setItem(
                30,
                new ItemStack(Material.DIAMOND)
        );

        inventory.setItem(
                24,
                recipes.createSpecialBucket(
                        Material.LAVA_BUCKET,
                        "blocked_in_lava"
                )
        );

        inventory.setItem(
                36,
                createButton(
                        Material.ARROW,
                        "§e← Back"
                )
        );

        player.openInventory(inventory);
    }

    private void openTNTRecipe(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                45,
                "§eBlockedIn • TNT Recipe"
        );

        inventory.setItem(
                10,
                new ItemStack(Material.GOLD_INGOT)
        );

        inventory.setItem(
                11,
                new ItemStack(Material.IRON_BLOCK)
        );

        inventory.setItem(
                12,
                new ItemStack(Material.GOLD_INGOT)
        );

        inventory.setItem(
                19,
                new ItemStack(Material.IRON_BLOCK)
        );

        inventory.setItem(
                20,
                new ItemStack(Material.REDSTONE_BLOCK)
        );

        inventory.setItem(
                21,
                new ItemStack(Material.IRON_BLOCK)
        );

        inventory.setItem(
                28,
                new ItemStack(Material.GOLD_INGOT)
        );

        inventory.setItem(
                29,
                new ItemStack(Material.IRON_BLOCK)
        );

        inventory.setItem(
                30,
                new ItemStack(Material.GOLD_INGOT)
        );

        inventory.setItem(
                24,
                recipes.createSpecialTNT()
        );

        inventory.setItem(
                36,
                createButton(
                        Material.ARROW,
                        "§e← Back"
                )
        );

        player.openInventory(inventory);
    }

    private void openHayToBreadRecipe(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                45,
                "§eBlockedIn • Hay to Bread Recipe"
        );

        inventory.setItem(
                10,
                new ItemStack(Material.HAY_BLOCK)
        );

        inventory.setItem(
                11,
                new ItemStack(Material.HAY_BLOCK)
        );

        inventory.setItem(
                12,
                new ItemStack(Material.HAY_BLOCK)
        );

        inventory.setItem(
                24,
                new ItemStack(
                        Material.BREAD,
                        9
                )
        );

        inventory.setItem(
                36,
                createButton(
                        Material.ARROW,
                        "§e← Back"
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