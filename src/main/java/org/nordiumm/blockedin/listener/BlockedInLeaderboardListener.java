package org.nordiumm.blockedin.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class BlockedInLeaderboardListener implements Listener {

    private static final String TITLE =
            "§eBlockedIn Leaderboards";

    private boolean isLeaderboardGUI(String title) {
        return TITLE.equals(title);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!isLeaderboardGUI(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null
                || event.getClickedInventory()
                != event.getView().getTopInventory()) {
            return;
        }

        if (event.getCurrentItem() == null
                || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        if (event.getCurrentItem().getType() == Material.BARRIER) {

            event.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        if (!isLeaderboardGUI(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);
    }
}