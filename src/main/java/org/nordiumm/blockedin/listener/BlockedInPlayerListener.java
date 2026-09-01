package org.nordiumm.blockedin.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.GameState;
import org.nordiumm.blockedin.game.BlockedInGame;
import org.nordiumm.blockedin.recipe.BlockedInRecipes;

import java.util.Random;

public class BlockedInPlayerListener implements Listener {

    private final Random random = new Random();
    private final BlockedIn plugin;
    private final BlockedInRecipes recipes;

    private final String[] genericDeathMessages = {
            "§c%s has been eliminated!"
    };

    private final String[] playerDeathMessages = {
            "§c%s was eliminated by %s!"
    };

    public BlockedInPlayerListener(BlockedIn plugin) {
        this.plugin = plugin;
        this.recipes = plugin.getRecipes();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        event.joinMessage(
                Component.text(
                        "[+] " + player.getName(),
                        NamedTextColor.GRAY
                )
        );

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        game.addSpectator(player);

        if (game.getState() == GameState.WAITING) {
            game.getPlayers().add(player);
            game.checkStart();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        event.quitMessage(
                Component.text(
                        "[-] " + player.getName(),
                        NamedTextColor.GRAY
                )
        );

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        game.removePlayer(player);

        if (game.getPlayers().isEmpty()
                && game.getState() != GameState.WAITING) {

            game.reset();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (!game.getAlive().contains(player)) {
            return;
        }

        Player killer = player.getKiller();

        if (killer != null
                && game.getAlive().contains(killer)) {

            plugin.getDatabase().addElimination(killer);
        }

        String message;

        if (killer != null) {

            message = String.format(
                    playerDeathMessages[
                            random.nextInt(
                                    playerDeathMessages.length
                            )
                            ],
                    player.getName(),
                    killer.getName()
            );

        } else {

            message = String.format(
                    genericDeathMessages[
                            random.nextInt(
                                    genericDeathMessages.length
                            )
                            ],
                    player.getName()
            );
        }

        event.deathMessage(
                net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
                        .legacySection()
                        .deserialize(message)
        );

        player.getWorld().playSound(
                player.getLocation(),
                Sound.BLOCK_ANVIL_LAND,
                3.0f,
                1.0f
        );

        game.addSpectator(player);

        plugin.getServer().getScheduler().runTask(
                plugin,
                game::checkWinCondition
        );
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        Player player = event.getPlayer();

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (!game.getSpectators().contains(player)) {
            return;
        }

        event.setRespawnLocation(player.getLocation());

        plugin.getServer().getScheduler().runTask(
                plugin,
                () -> player.setGameMode(GameMode.SPECTATOR)
        );
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {

        Player player = event.getPlayer();

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (game.getAlive().contains(player)) {

            event.renderer(
                    (source, sourceDisplayName, message, viewer) ->
                            Component.text(
                                            source.getName(),
                                            NamedTextColor.GREEN
                                    )
                                    .append(
                                            Component.text(
                                                    ": ",
                                                    NamedTextColor.GRAY
                                            )
                                    )
                                    .append(message)
            );

        } else if (game.getSpectators().contains(player)) {

            event.renderer(
                    (source, sourceDisplayName, message, viewer) ->
                            Component.text(
                                            source.getName(),
                                            NamedTextColor.GRAY
                                    )
                                    .append(
                                            Component.text(
                                                    ": ",
                                                    NamedTextColor.GRAY
                                            )
                                    )
                                    .append(message)
            );
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (!game.getSpectators().contains(player)) {
            return;
        }

        if (game.getWorld() == null) {
            return;
        }

        Location to = event.getTo();

        if (to == null) {
            return;
        }

        int width = plugin.getConfig()
                .getInt("arena.width");

        int length = plugin.getConfig()
                .getInt("arena.length");

        double buffer = 10.0;

        double minX = -buffer;
        double maxX = width + buffer;

        double minZ = -buffer;
        double maxZ = length + buffer;

        if (to.getX() < minX
                || to.getX() > maxX
                || to.getZ() < minZ
                || to.getZ() > maxZ) {

            Location center = new Location(
                    game.getWorld(),
                    width / 2.0,
                    game.getWorld().getHighestBlockYAt(
                            width / 2,
                            length / 2
                    ) + 1,
                    length / 2.0
            );

            player.teleport(center);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {

        Player player = event.getPlayer();

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            return;
        }

        Material blockType = event.getBlock().getType();

        if (blockType == Material.WATER) {

            event.setItemStack(
                    recipes.createSpecialBucket(
                            Material.WATER_BUCKET,
                            "blocked_in_water"
                    )
            );

        } else if (blockType == Material.LAVA) {

            event.setItemStack(
                    recipes.createSpecialBucket(
                            Material.LAVA_BUCKET,
                            "blocked_in_lava"
                    )
            );
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {

        Player player = event.getPlayer();

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        if (game.getState() != GameState.RUNNING) {
            return;
        }

        ItemStack item = player.getInventory()
                .getItemInMainHand();

        if (!recipes.isSpecialBucket(item)) {
            return;
        }

        String type = recipes.getBucketType(item);

        if (type == null) {
            return;
        }
    }
}