package org.nordiumm.blockedin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.database.BlockedInDatabase;
import org.nordiumm.blockedin.database.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;

public class BlockedInLeaderboardGUI {

    private static final String TITLE =
            "§eBlockedIn Leaderboards";

    private final BlockedIn plugin;
    private final BlockedInDatabase database;

    public BlockedInLeaderboardGUI(BlockedIn plugin) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
    }

    public void openLeaderboard(Player player) {

        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                TITLE
        );

        /*
         * WINS
         */

        inventory.setItem(
                10,
                createLeaderboardItem(
                        Material.GOLD_INGOT,
                        "§6🏆 Wins",
                        database.getTopWins(),
                        database.getPlayerPosition(
                                player,
                                "wins"
                        ),
                        database.getPlayerStat(
                                player,
                                "wins"
                        )
                )
        );

        /*
         * ELIMINATIONS
         */

        inventory.setItem(
                13,
                createLeaderboardItem(
                        Material.IRON_SWORD,
                        "§c⚔ Eliminations",
                        database.getTopEliminations(),
                        database.getPlayerPosition(
                                player,
                                "eliminations"
                        ),
                        database.getPlayerStat(
                                player,
                                "eliminations"
                        )
                )
        );

        /*
         * GAMES PLAYED
         */

        inventory.setItem(
                16,
                createLeaderboardItem(
                        Material.CLOCK,
                        "§a🎮 Games Played",
                        database.getTopGamesPlayed(),
                        database.getPlayerPosition(
                                player,
                                "games_played"
                        ),
                        database.getPlayerStat(
                                player,
                                "games_played"
                        )
                )
        );

        /*
         * CLOSE
         */

        inventory.setItem(
                22,
                createButton(
                        Material.BARRIER,
                        "§c✖ Close"
                )
        );

        player.openInventory(inventory);
    }

    private ItemStack createLeaderboardItem(
            Material material,
            String name,
            List<LeaderboardEntry> entries,
            int playerPosition,
            int playerValue
    ) {

        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(name);

        List<String> lore = new ArrayList<>();

        lore.add("§7");

        /*
         * TOP 10
         */

        if (entries.isEmpty()) {

            lore.add("§7No players yet.");

        } else {

            for (int i = 0; i < entries.size(); i++) {

                LeaderboardEntry entry = entries.get(i);

                lore.add(
                        "§f"
                                + (i + 1)
                                + ". §e"
                                + entry.name()
                                + " §7— §f"
                                + entry.value()
                );
            }
        }

        /*
         * PLAYER'S OWN POSITION
         */

        lore.add("§7");
        lore.add("§8━━━━━━━━━━━━━━");

        if (playerPosition > 0) {

            lore.add(
                    "§7Your position: §f#"
                            + playerPosition
            );

        } else {

            lore.add(
                    "§7Your position: §fUnknown"
            );
        }

        lore.add(
                "§7Your score: §f"
                        + playerValue
        );

        meta.setLore(lore);

        item.setItemMeta(meta);

        return item;
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