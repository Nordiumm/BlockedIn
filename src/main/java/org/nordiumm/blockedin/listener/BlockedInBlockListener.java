package org.nordiumm.blockedin.listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.GameState;
import org.nordiumm.blockedin.game.BlockedInGame;
import org.nordiumm.blockedin.recipe.BlockedInRecipes;

public class BlockedInBlockListener implements Listener {

    private final BlockedIn plugin;
    private final NamespacedKey specialTntKey;

    public BlockedInBlockListener(BlockedIn plugin) {
        this.plugin = plugin;

        this.specialTntKey = new NamespacedKey(
                plugin,
                "blocked_in_tnt"
        );
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getItemInHand();

        BlockedInRecipes recipes = plugin.getRecipes();

        if (!recipes.isSpecialTNT(item)) {
            return;
        }

        event.getBlockPlaced().setType(Material.AIR);

        TNTPrimed tnt = event.getBlockPlaced()
                .getWorld()
                .spawn(
                        event.getBlockPlaced()
                                .getLocation()
                                .add(0.5, 0, 0.5),
                        TNTPrimed.class
                );

        tnt.getPersistentDataContainer().set(
                specialTntKey,
                PersistentDataType.BYTE,
                (byte) 1
        );

        tnt.setFuseTicks(80);
        tnt.setYield(4.0f);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        if (!(event.getEntity() instanceof TNTPrimed tnt)) {
            return;
        }

        if (!tnt.getPersistentDataContainer().has(
                specialTntKey,
                PersistentDataType.BYTE
        )) {
            return;
        }

        int radius = 4;

        int centerX = tnt.getLocation().getBlockX();
        int centerY = tnt.getLocation().getBlockY();
        int centerZ = tnt.getLocation().getBlockZ();

        for (int x = centerX - radius;
             x <= centerX + radius;
             x++) {

            for (int y = centerY - radius;
                 y <= centerY + radius;
                 y++) {

                for (int z = centerZ - radius;
                     z <= centerZ + radius;
                     z++) {

                    Block block = tnt.getWorld()
                            .getBlockAt(x, y, z);

                    if (block.getType() == Material.OBSIDIAN
                            && !event.blockList().contains(block)) {

                        event.blockList().add(block);
                    }
                }
            }
        }

        event.setYield(0.0f);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (!game.getStaticPhysicsBlocks().contains(
                event.getBlock().getLocation()
        )) {
            return;
        }

        event.setCancelled(true);
    }
}