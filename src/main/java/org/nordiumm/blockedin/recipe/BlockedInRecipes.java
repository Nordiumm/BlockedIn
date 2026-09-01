package org.nordiumm.blockedin.recipe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.nordiumm.blockedin.BlockedIn;

import java.util.List;

public class BlockedInRecipes {

    private final BlockedIn plugin;

    public BlockedInRecipes(BlockedIn plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes() {
        registerWaterBucket();
        registerLavaBucket();
        registerTNT();
        registerHayToBread();
    }

    private void registerWaterBucket() {

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_water_bucket"
        );

        ShapedRecipe recipe = new ShapedRecipe(
                key,
                createSpecialBucket(
                        Material.WATER_BUCKET,
                        "blocked_in_water"
                )
        );

        recipe.shape(
                "ISI",
                "SBS",
                "ISI"
        );

        recipe.setIngredient(
                'I',
                Material.IRON_INGOT
        );

        recipe.setIngredient(
                'S',
                Material.SAND
        );

        recipe.setIngredient(
                'B',
                Material.BUCKET
        );

        Bukkit.addRecipe(recipe);
    }

    private void registerLavaBucket() {

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_lava_bucket"
        );

        ShapedRecipe recipe = new ShapedRecipe(
                key,
                createSpecialBucket(
                        Material.LAVA_BUCKET,
                        "blocked_in_lava"
                )
        );

        recipe.shape(
                "DOD",
                "LBL",
                "DOD"
        );

        recipe.setIngredient(
                'D',
                Material.DIAMOND
        );

        recipe.setIngredient(
                'O',
                Material.OBSIDIAN
        );

        recipe.setIngredient(
                'B',
                Material.BUCKET
        );

        recipe.setIngredient(
                'L',
                Material.IRON_INGOT
        );

        Bukkit.addRecipe(recipe);
    }

    private void registerTNT() {

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_tnt_recipe"
        );

        ShapedRecipe recipe = new ShapedRecipe(
                key,
                createSpecialTNT()
        );

        recipe.shape(
                "GIG",
                "IRI",
                "GIG"
        );

        recipe.setIngredient(
                'G',
                Material.GOLD_INGOT
        );

        recipe.setIngredient(
                'I',
                Material.IRON_BLOCK
        );

        recipe.setIngredient(
                'R',
                Material.REDSTONE_BLOCK
        );

        Bukkit.addRecipe(recipe);
    }

    private void registerHayToBread() {

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_hay_to_bread"
        );

        ShapedRecipe recipe = new ShapedRecipe(
                key,
                new ItemStack(
                        Material.BREAD,
                        9
                )
        );

        recipe.shape(
                "HHH"
        );

        recipe.setIngredient(
                'H',
                Material.HAY_BLOCK
        );

        Bukkit.addRecipe(recipe);
    }

    public ItemStack createSpecialBucket(
            Material material,
            String type
    ) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.addEnchant(
                Enchantment.LURE,
                1,
                true
        );

        meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS
        );

        if (type.equals("blocked_in_water")) {

            meta.setDisplayName(
                    "§bWater Bucket"
            );

        } else if (type.equals("blocked_in_lava")) {

            meta.setDisplayName(
                    "§cLava Bucket"
            );
        }

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_bucket"
        );

        meta.getPersistentDataContainer().set(
                key,
                PersistentDataType.STRING,
                type
        );

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createSpecialTNT() {

        ItemStack item = new ItemStack(
                Material.TNT,
                8
        );

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                "§cTNT"
        );

        meta.setLore(
                List.of(
                        "§7Ignites immediately when placed.",
                        "§7Can destroy obsidian.",
                        "§7Deals normal TNT damage."
                )
        );

        meta.addEnchant(
                Enchantment.LURE,
                1,
                true
        );

        meta.addItemFlags(
                ItemFlag.HIDE_ENCHANTS
        );

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_tnt"
        );

        meta.getPersistentDataContainer().set(
                key,
                PersistentDataType.BYTE,
                (byte) 1
        );

        item.setItemMeta(meta);

        return item;
    }

    public boolean isSpecialTNT(ItemStack item) {

        if (item == null
                || item.getType() != Material.TNT
                || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_tnt"
        );

        return meta.getPersistentDataContainer().has(
                key,
                PersistentDataType.BYTE
        );
    }

    public boolean isSpecialBucket(ItemStack item) {

        if (item == null
                || item.getType() == Material.AIR
                || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_bucket"
        );

        return meta.getPersistentDataContainer().has(
                key,
                PersistentDataType.STRING
        );
    }

    public String getBucketType(ItemStack item) {

        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();

        NamespacedKey key = new NamespacedKey(
                plugin,
                "blocked_in_bucket"
        );

        return meta.getPersistentDataContainer().get(
                key,
                PersistentDataType.STRING
        );
    }
}