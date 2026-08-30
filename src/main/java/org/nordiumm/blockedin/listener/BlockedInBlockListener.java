package org.nordiumm.blockedin.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.GameState;

public class BlockedInBlockListener implements Listener {

    private final BlockedIn plugin;

    public BlockedInBlockListener(BlockedIn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (plugin.getGame() == null) {
            return;
        }

        if (plugin.getGame().getState() != GameState.RUNNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (plugin.getGame() == null) {
            return;
        }

        if (plugin.getGame().getState() != GameState.RUNNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {

        if (!event.getBlock().getWorld().getName().equalsIgnoreCase("blockedin")) {
            return;
        }

        Material type = event.getBlock().getType();

        if (isStaticBlock(type)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {

        if (!event.getBlock().getWorld().getName().equalsIgnoreCase("blockedin")) {
            return;
        }

        Material type = event.getBlock().getType();

        if (type == Material.WATER || type == Material.LAVA) {
            event.setCancelled(true);
        }
    }

    private boolean isStaticBlock(Material material) {

        return material == Material.SAND
                || material == Material.GRAVEL
                || material == Material.WATER
                || material == Material.LAVA;
    }
}