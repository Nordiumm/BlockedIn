package org.nordiumm.blockedin;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.nordiumm.blockedin.command.BlockedInCommand;
import org.nordiumm.blockedin.database.BlockedInDatabase;
import org.nordiumm.blockedin.game.BlockedInGame;
import org.nordiumm.blockedin.listener.BlockedInBlockListener;
import org.nordiumm.blockedin.listener.BlockedInLeaderboardListener;
import org.nordiumm.blockedin.listener.BlockedInPlayerListener;
import org.nordiumm.blockedin.listener.BlockedInRecipeListener;
import org.nordiumm.blockedin.recipe.BlockedInRecipes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BlockedIn extends JavaPlugin {

    private FileConfiguration whitelistConfig;
    private File whitelistFile;

    private GameState gameState = GameState.WAITING;

    private BlockedInGame game;
    private BlockedInRecipes recipes;
    private BlockedInDatabase database;

    private final List<Material> allowedBlocks =
            new ArrayList<>();

    public BlockedInRecipes getRecipes() {
        return recipes;
    }

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

    public BlockedInDatabase getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();

        recipes = new BlockedInRecipes(this);
        recipes.registerRecipes();

        database = new BlockedInDatabase(this);
        database.connect();

        saveResource("whitelist.yml", false);
        reloadWhitelist();

        if (getServer().getWorld("blockedin") == null) {
            getLogger().warning(
                    "World 'blockedin' is not loaded!"
            );
        }

        game = new BlockedInGame(this);

        BlockedInCommand command =
                new BlockedInCommand(this);

        if (getCommand("blockedin") != null) {

            getCommand("blockedin")
                    .setExecutor(command);

            getCommand("blockedin")
                    .setTabCompleter(command);
        }

        getServer().getPluginManager().registerEvents(
                new BlockedInBlockListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new BlockedInPlayerListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new BlockedInRecipeListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new BlockedInLeaderboardListener(),
                this
        );

        getLogger().info(
                "BlockedIn has been enabled!"
        );
    }

    @Override
    public void onDisable() {

        if (game != null) {
            game.reset();
        }

        if (database != null) {
            database.close();
        }

        getLogger().info(
                "BlockedIn has been disabled!"
        );
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

            if (material == null
                    || !material.isBlock()) {

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