package org.nordiumm.blockedin.game;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.nordiumm.blockedin.BlockedIn;
import org.nordiumm.blockedin.GameState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BlockedInGame {

    private final BlockedIn plugin;
    private final Set<Player> players = new HashSet<>();
    private final List<Location> spawnLocations = new ArrayList<>();
    private final Random random = new Random();

    private GameTimer timer;
    private BukkitTask startDelayTask;
    private BukkitTask playerRequirementTask;
    private BukkitTask generationTask;

    private GameState state = GameState.WAITING;
    private World world;

    private final List<Player> alive = new ArrayList<>();
    private final List<Player> spectators = new ArrayList<>();

    public BlockedInGame(BlockedIn plugin) {
        this.plugin = plugin;
        world = plugin.getServer().getWorld("blockedin");
        startPlayerRequirementDisplay();
    }

    public List<Player> getAlive() {
        return alive;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public void addSpectator(Player player) {
        alive.remove(player);

        if (!spectators.contains(player)) {
            spectators.add(player);
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);

        player.setGameMode(GameMode.SPECTATOR);
        player.setPlayerListName("§7" + player.getName());
        player.setDisplayName("§7" + player.getName());
    }

    public void addAlive(Player player) {
        spectators.remove(player);

        if (!alive.contains(player)) {
            alive.add(player);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setPlayerListName("§a" + player.getName());
        player.setDisplayName("§a" + player.getName());
    }

    public BlockedIn getPlugin() {
        return plugin;
    }

    public World getWorld() {
        return world;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void checkStart() {

        if (state != GameState.WAITING) {
            return;
        }

        if (generationTask != null) {
            return;
        }

        int minimumPlayers = plugin.getConfig()
                .getInt("game.minimum-players");

        if (players.size() < minimumPlayers) {
            cancelStartDelay();
            updatePlayerRequirement();
            return;
        }

        stopPlayerRequirementDisplay();

        if (startDelayTask != null) {
            return;
        }

        startDelay();
    }

    public void updatePlayerRequirement() {

        if (state != GameState.WAITING) {
            return;
        }

        int minimumPlayers = plugin.getConfig()
                .getInt("game.minimum-players");

        int needed = minimumPlayers - players.size();

        if (needed <= 0) {
            return;
        }

        String message = "§eBlockedIn §7» §f"
                + needed
                + " more player"
                + (needed == 1 ? "" : "s")
                + " needed to start!";

        for (Player player : players) {
            player.sendActionBar(message);
        }
    }

    public void startPlayerRequirementDisplay() {

        if (playerRequirementTask != null) {
            return;
        }

        playerRequirementTask = new BukkitRunnable() {

            @Override
            public void run() {

                if (state != GameState.WAITING) {
                    cancel();
                    playerRequirementTask = null;
                    return;
                }

                int minimumPlayers = plugin.getConfig()
                        .getInt("game.minimum-players");

                if (players.size() >= minimumPlayers) {
                    cancel();
                    playerRequirementTask = null;
                    return;
                }

                updatePlayerRequirement();
            }

        }.runTaskTimer(plugin, 0L, 40L);
    }

    public void stopPlayerRequirementDisplay() {

        if (playerRequirementTask != null) {
            playerRequirementTask.cancel();
            playerRequirementTask = null;
        }
    }

    public void startGame() {

        if (state != GameState.WAITING) {
            return;
        }

        int minimumPlayers = plugin.getConfig()
                .getInt("game.minimum-players");

        if (players.size() < minimumPlayers) {
            return;
        }

        cancelStartDelay();
        stopPlayerRequirementDisplay();

        prepareArena();
    }

    public void forceStart() {

        if (state != GameState.WAITING) {
            return;
        }

        cancelStartDelay();
        stopPlayerRequirementDisplay();

        prepareArena();
    }

    private void prepareArena() {

        if (generationTask != null) {
            return;
        }

        resetArena();
        createArena();
        createSpawnHoles();
        setupWorldBorder();

        alive.clear();

        generateBlocks();
    }

    private void finishArenaPreparation() {

        if (state != GameState.WAITING) {
            return;
        }

        if (players.isEmpty()) {
            return;
        }

        for (Player player : new ArrayList<>(players)) {
            addAlive(player);
        }

        teleportPlayers();

        state = GameState.RUNNING;

        timer = new GameTimer(this);
        timer.startCountdown();
    }

    private void startDelay() {

        int delay = plugin.getConfig()
                .getInt("game.start-delay");

        if (delay <= 0) {
            startGame();
            return;
        }

        startDelayTask = new BukkitRunnable() {

            int seconds = delay;

            @Override
            public void run() {

                if (state != GameState.WAITING) {
                    cancel();
                    startDelayTask = null;
                    return;
                }

                int minimumPlayers = plugin.getConfig()
                        .getInt("game.minimum-players");

                if (players.size() < minimumPlayers) {
                    cancel();
                    startDelayTask = null;
                    startPlayerRequirementDisplay();
                    updatePlayerRequirement();
                    return;
                }

                if (seconds <= 0) {
                    cancel();
                    startDelayTask = null;
                    startGame();
                    return;
                }

                String message = "§eBlockedIn §7» §fStarting in §e"
                        + seconds
                        + "§f second"
                        + (seconds == 1 ? "" : "s")
                        + "!";

                for (Player player : players) {
                    player.sendActionBar(message);
                }

                seconds--;
            }

        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void cancelStartDelay() {

        if (startDelayTask != null) {
            startDelayTask.cancel();
            startDelayTask = null;
        }
    }

    public void resetArena() {

        if (world == null) {
            world = plugin.getServer().getWorld("blockedin");
        }

        if (world == null) {
            plugin.getLogger().warning(
                    "Could not reset BlockedIn arena because world 'blockedin' does not exist."
            );
            return;
        }

        int width = plugin.getConfig()
                .getInt("arena.width");

        int height = plugin.getConfig()
                .getInt("arena.height");

        int length = plugin.getConfig()
                .getInt("arena.length");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {

                    boolean isWall =
                            x == 0 || x == width - 1 ||
                                    y == 0 || y == height - 1 ||
                                    z == 0 || z == length - 1;

                    if (isWall) {
                        world.getBlockAt(x, y, z)
                                .setType(Material.BEDROCK);
                    } else {
                        world.getBlockAt(x, y, z)
                                .setType(Material.AIR);
                    }
                }
            }
        }

        spawnLocations.clear();
    }

    public void reset() {

        cancelStartDelay();
        stopPlayerRequirementDisplay();

        if (generationTask != null) {
            generationTask.cancel();
            generationTask = null;
        }

        if (timer != null) {
            timer.stop();
            timer = null;
        }

        spawnLocations.clear();
        alive.clear();
        spectators.clear();
        players.clear();

        state = GameState.WAITING;

        resetArena();

        startPlayerRequirementDisplay();
    }

    public void createArena() {

        if (world == null) {
            return;
        }

        int width = plugin.getConfig()
                .getInt("arena.width");

        int height = plugin.getConfig()
                .getInt("arena.height");

        int length = plugin.getConfig()
                .getInt("arena.length");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {

                    boolean isWall =
                            x == 0 || x == width - 1 ||
                                    y == 0 || y == height - 1 ||
                                    z == 0 || z == length - 1;

                    if (isWall) {
                        world.getBlockAt(x, y, z)
                                .setType(Material.BEDROCK);
                    }
                }
            }
        }
    }

    public void setupWorldBorder() {

        if (world == null) {
            return;
        }

        int width = plugin.getConfig()
                .getInt("arena.width");

        int length = plugin.getConfig()
                .getInt("arena.length");

        double centerX = width / 2.0;
        double centerZ = length / 2.0;

        double size = Math.max(width, length);

        world.getWorldBorder().setCenter(centerX, centerZ);
        world.getWorldBorder().setSize(size);
    }

    public void createSpawnHoles() {

        spawnLocations.clear();

        if (world == null) {
            return;
        }

        int width = plugin.getConfig()
                .getInt("arena.width");

        int height = plugin.getConfig()
                .getInt("arena.height");

        int length = plugin.getConfig()
                .getInt("arena.length");

        double minimumDistance = plugin.getConfig()
                .getDouble("players.minimum-spawn-distance");

        double spawnRadius = plugin.getConfig()
                .getDouble("players.spawn-radius");

        int spawnHeight = plugin.getConfig()
                .getInt("players.spawn-height");

        double centerX = width / 2.0;
        double centerZ = length / 2.0;

        for (Player player : players) {

            Location spawn = null;

            for (int attempts = 0; attempts < 1000; attempts++) {

                double angle = random.nextDouble() * Math.PI * 2;

                double x = centerX +
                        Math.cos(angle) * spawnRadius;

                double z = centerZ +
                        Math.sin(angle) * spawnRadius;

                Location candidate = new Location(
                        world,
                        x + 0.5,
                        spawnHeight,
                        z + 0.5
                );

                boolean valid = true;

                for (Location existing : spawnLocations) {

                    if (candidate.distance(existing)
                            < minimumDistance) {

                        valid = false;
                        break;
                    }
                }

                if (valid) {
                    spawn = candidate;
                    break;
                }
            }

            if (spawn == null) {

                plugin.getLogger().warning(
                        "Could not find a valid spawn location for "
                                + player.getName()
                );

                continue;
            }

            spawnLocations.add(spawn);

            int x = spawn.getBlockX();
            int y = spawn.getBlockY();
            int z = spawn.getBlockZ();

            world.getBlockAt(x, y, z)
                    .setType(Material.AIR);

            world.getBlockAt(x, y + 1, z)
                    .setType(Material.AIR);
        }
    }

    public void teleportPlayers() {

        int index = 0;

        for (Player player : players) {

            if (index >= spawnLocations.size()) {
                break;
            }

            player.teleport(
                    spawnLocations.get(index)
            );

            index++;
        }
    }

    public void generateBlocks() {

        if (world == null) {
            return;
        }

        if (generationTask != null) {
            return;
        }

        int width = plugin.getConfig()
                .getInt("arena.width");

        int height = plugin.getConfig()
                .getInt("arena.height");

        int length = plugin.getConfig()
                .getInt("arena.length");

        generationTask = new BukkitRunnable() {

            int y = 1;

            @Override
            public void run() {

                if (state != GameState.WAITING) {
                    cancel();
                    generationTask = null;
                    return;
                }

                if (players.isEmpty()) {
                    cancel();
                    generationTask = null;
                    return;
                }

                if (y >= height - 1) {
                    cancel();
                    generationTask = null;

                    finishArenaPreparation();
                    return;
                }

                for (int x = 1; x < width - 1; x++) {
                    for (int z = 1; z < length - 1; z++) {

                        if (isSpawnHoleBlock(x, y, z)) {
                            continue;
                        }

                        world.getBlockAt(x, y, z)
                                .setType(getRandomAllowedBlock());
                    }
                }

                y++;
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }

    private boolean isSpawnHoleBlock(
            int x,
            int y,
            int z
    ) {

        for (Location spawn : spawnLocations) {

            int spawnX = spawn.getBlockX();
            int spawnY = spawn.getBlockY();
            int spawnZ = spawn.getBlockZ();

            if (x == spawnX &&
                    z == spawnZ &&
                    (y == spawnY ||
                            y == spawnY + 1)) {

                return true;
            }
        }

        return false;
    }

    private Material getRandomAllowedBlock() {

        List<Material> allowedBlocks =
                plugin.getAllowedBlocks();

        if (allowedBlocks.isEmpty()) {
            return Material.STONE;
        }

        return allowedBlocks.get(
                random.nextInt(
                        allowedBlocks.size()
                )
        );
    }
}