package org.nordiumm.blockedin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
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
}
