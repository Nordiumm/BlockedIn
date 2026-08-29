package org.nordiumm.blockedin;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.nordiumm.blockedin.command.BlockedInCommand;
import org.nordiumm.blockedin.game.BlockedInGame;
import org.nordiumm.blockedin.listener.BlockedInBlockListener;
import org.nordiumm.blockedin.listener.BlockedInPlayerListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BlockedIn extends JavaPlugin {

    private FileConfiguration whitelistConfig;
    private File whitelistFile;

    private GameState gameState = GameState.WAITING;
    private BlockedInGame game;

    private final List<Material> allowedBlocks = new ArrayList<>();

    public List<Material> getAllowedBlocks() {
        return allowedBlocks;
    }

    public BlockedInGame getGame() {
        return game;
    }

    public void setGame(BlockedInGame game) {
        this.game = game;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void onEnable() {

        getLogger().info("BlockedIn has been enabled!");

        saveDefaultConfig();

        saveResource("whitelist.yml", false);
        reloadWhitelist();

        // Get the permanent BlockedIn world.
        if (getServer().getWorld("blockedin") == null) {
            getLogger().warning(
                    "World 'blockedin' is not loaded!"
            );
        }

        // Create the game once when the server starts.
        game = new BlockedInGame(this);

        BlockedInCommand command = new BlockedInCommand(this);

        getCommand("blockedin").setExecutor(command);
        getCommand("blockedin").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(
                new BlockedInBlockListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new BlockedInPlayerListener(this),
                this
        );

        getLogger().info("BlockedIn game initialized!");
    }

    @Override
    public void onDisable() {

        if (game != null) {
            game.reset();
        }

        getLogger().info("BlockedIn has been disabled!");
    }

    public void reloadWhitelist() {

        whitelistFile = new File(
                getDataFolder(),
                "whitelist.yml"
        );

        whitelistConfig =
                YamlConfiguration.loadConfiguration(
                        whitelistFile
                );

        allowedBlocks.clear();

        for (String blockName :
                whitelistConfig.getStringList("blocks")) {

            Material material =
                    Material.matchMaterial(blockName);

            if (material == null || !material.isBlock()) {

                getLogger().warning(
                        "Invalid block in whitelist.yml: "
                                + blockName
                );

                continue;
            }

            allowedBlocks.add(material);
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
    }
}