package org.nordiumm.blockedin.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.GameState;
import org.nordiumm.blockedin.game.BlockedInGame;
import org.nordiumm.blockedin.gui.BlockedInLeaderboardGUI;
import org.nordiumm.blockedin.gui.BlockedInRecipeGUI;

import java.util.ArrayList;
import java.util.List;

public class BlockedInCommand implements CommandExecutor, TabCompleter {

    private final BlockedIn plugin;

    public BlockedInCommand(BlockedIn plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (args.length == 0) {

            sender.sendMessage(
                    "Usage: /blockedin <force-start|stop|force-reset|reload|recipe|leaderboard>"
            );

            return true;
        }

        String subcommand =
                args[0].toLowerCase();

        /*
         * Operator-only commands
         */
        if (subcommand.equals("force-start")
                || subcommand.equals("stop")
                || subcommand.equals("force-reset")
                || subcommand.equals("reload")) {

            if (!sender.isOp()) {

                sender.sendMessage(
                        "§cYou must be an operator to use this command."
                );

                return true;
            }
        }

        /*
         * FORCE START
         *
         * Starts a new game immediately.
         * The arena will be reset as part of starting
         * the new game.
         */
        if (subcommand.equals("force-start")) {

            if (plugin.getGame() == null) {

                plugin.setGame(
                        new BlockedInGame(plugin)
                );
            }

            BlockedInGame game =
                    plugin.getGame();

            if (game.getState() != GameState.WAITING) {

                sender.sendMessage(
                        "§cBlockedIn is already running!"
                );

                return true;
            }

            game.getPlayers().clear();

            for (Player player :
                    Bukkit.getOnlinePlayers()) {

                game.getPlayers().add(player);
            }

            if (game.getPlayers().isEmpty()) {

                sender.sendMessage(
                        "§cThere are no players available to start the game."
                );

                return true;
            }

            game.forceStart();

            sender.sendMessage(
                    "§aBlockedIn has been force started!"
            );

            return true;
        }

        /*
         * STOP
         *
         * Stops the current game WITHOUT resetting
         * the arena.
         */
        if (subcommand.equals("stop")) {

            if (plugin.getGame() == null) {

                sender.sendMessage(
                        "§cBlockedIn game is not initialized."
                );

                return true;
            }

            BlockedInGame game =
                    plugin.getGame();

            if (game.getState() == GameState.WAITING) {

                sender.sendMessage(
                        "§cBlockedIn is not running."
                );

                return true;
            }

            game.reset();

            sender.sendMessage(
                    "§aBlockedIn has been stopped."
            );

            return true;
        }

        /*
         * FORCE RESET
         *
         * Stops the current game AND resets the arena.
         */
        if (subcommand.equals("force-reset")) {

            if (plugin.getGame() == null) {

                sender.sendMessage(
                        "§cBlockedIn game is not initialized."
                );

                return true;
            }

            BlockedInGame game =
                    plugin.getGame();

            game.forceReset();

            sender.sendMessage(
                    "§aBlockedIn arena reset started!"
            );

            return true;
        }

        /*
         * RELOAD
         */
        if (subcommand.equals("reload")) {

            plugin.reloadWhitelist();
            plugin.reloadConfig();

            sender.sendMessage(
                    "§aBlockedIn configuration reloaded!"
            );

            return true;
        }

        /*
         * RECIPE
         */
        if (subcommand.equals("recipe")) {

            if (!(sender instanceof Player player)) {

                sender.sendMessage(
                        "§cOnly players can use this command."
                );

                return true;
            }

            BlockedInRecipeGUI gui =
                    new BlockedInRecipeGUI(plugin);

            gui.openRecipeMenu(player);

            return true;
        }

        /*
         * LEADERBOARD
         */
        if (subcommand.equals("leaderboard")) {

            if (!(sender instanceof Player player)) {

                sender.sendMessage(
                        "§cOnly players can use this command."
                );

                return true;
            }

            BlockedInLeaderboardGUI gui =
                    new BlockedInLeaderboardGUI(plugin);

            gui.openLeaderboard(player);

            return true;
        }

        sender.sendMessage(
                "§cUnknown subcommand."
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (args.length == 1) {

            List<String> completions =
                    new ArrayList<>();

            for (String option : List.of(
                    "force-start",
                    "stop",
                    "force-reset",
                    "reload",
                    "recipe",
                    "leaderboard"
            )) {

                if (option.toLowerCase()
                        .startsWith(
                                args[0].toLowerCase()
                        )) {

                    completions.add(option);
                }
            }

            return completions;
        }

        return List.of();
    }
}