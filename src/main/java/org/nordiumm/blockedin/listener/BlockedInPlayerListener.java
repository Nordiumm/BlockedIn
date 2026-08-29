package org.nordiumm.blockedin.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.GameState;
import org.nordiumm.blockedin.game.BlockedInGame;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlockedInPlayerListener implements Listener {

    private final BlockedIn plugin;

    public BlockedInPlayerListener(BlockedIn plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        Player player = event.getPlayer();

        game.getPlayers().add(player);

        if (game.getState() == GameState.WAITING) {
            game.addSpectator(player);
            game.checkStart();
        } else {
            game.addSpectator(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        Player player = event.getPlayer();

        game.getPlayers().remove(player);
        game.getAlive().remove(player);
        game.getSpectators().remove(player);

        if (game.getPlayers().isEmpty()) {
            game.reset();
            return;
        }

        game.checkStart();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        Player player = event.getEntity();

        if (!game.getAlive().contains(player)) {
            return;
        }

        game.addSpectator(player);
        game.checkWinCondition();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        Player player = event.getPlayer();

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

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        Player player = event.getPlayer();

        if (game.getAlive().contains(player)) {
            event.renderer((source, sourceDisplayName, message, viewer) ->
                    Component.text("§a" + source.getName() + "§7: ")
                            .append(message)
            );
        } else if (game.getSpectators().contains(player)) {
            event.renderer((source, sourceDisplayName, message, viewer) ->
                    Component.text("§7" + source.getName() + "§7: ")
                            .append(message)
            );
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        BlockedInGame game = plugin.getGame();

        if (game == null) {
            return;
        }

        Player player = event.getPlayer();

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

        int width = plugin.getConfig().getInt("arena.width");
        int length = plugin.getConfig().getInt("arena.length");

        double buffer = 10.0;

        double minX = -buffer;
        double maxX = width + buffer;

        double minZ = -buffer;
        double maxZ = length + buffer;

        if (to.getX() < minX ||
                to.getX() > maxX ||
                to.getZ() < minZ ||
                to.getZ() > maxZ) {

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
}