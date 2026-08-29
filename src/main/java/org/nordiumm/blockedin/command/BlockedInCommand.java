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
            sender.sendMessage("Usage: /blockedin <force-start|stop|reload>");
            return true;
        }

        if (args[0].equalsIgnoreCase("force-start")
                || args[0].equalsIgnoreCase("start")) {

            if (plugin.getGame() == null) {
                plugin.setGame(new BlockedInGame(plugin));
            }

            BlockedInGame game = plugin.getGame();

            if (game.getState() != GameState.WAITING) {
                sender.sendMessage("BlockedIn is already running!");
                return true;
            }

            game.getPlayers().clear();

            for (Player player : Bukkit.getOnlinePlayers()) {
                game.getPlayers().add(player);
            }

            game.forceStart();

            sender.sendMessage("BlockedIn has been force started!");

            return true;
        }

        if (args[0].equalsIgnoreCase("stop")) {

            if (plugin.getGame() == null
                    || plugin.getGame().getState() == GameState.WAITING) {

                sender.sendMessage("BlockedIn is not running!");
                return true;
            }

            BlockedInGame game = plugin.getGame();

            for (Player player : new ArrayList<>(game.getPlayers())) {
                game.addSpectator(player);
            }

            game.reset();

            sender.sendMessage("BlockedIn has been stopped!");

            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            plugin.reloadWhitelist();
            plugin.reloadConfig();

            sender.sendMessage("BlockedIn configuration reloaded!");

            return true;
        }

        sender.sendMessage("Unknown subcommand.");
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

            List<String> completions = new ArrayList<>();

            for (String option : List.of(
                    "force-start",
                    "stop",
                    "reload"
            )) {

                if (option.toLowerCase()
                        .startsWith(args[0].toLowerCase())) {

                    completions.add(option);
                }
            }

            return completions;
        }

        return List.of();
    }
}
