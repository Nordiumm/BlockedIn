package org.nordiumm.blockedin.database;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BlockedInDatabase {

    private final JavaPlugin plugin;
    private Connection connection;

    public BlockedInDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {

        try {
            File databaseFile = new File(
                    plugin.getDataFolder(),
                    "blockedin.db"
            );

            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            connection = DriverManager.getConnection(
                    "jdbc:sqlite:" + databaseFile.getAbsolutePath()
            );

            createTables();

            plugin.getLogger().info(
                    "BlockedIn database connected!"
            );

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not connect to BlockedIn database!"
            );

            e.printStackTrace();
        }
    }

    private void createTables() {

        String sql = """
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    wins INTEGER NOT NULL DEFAULT 0,
                    games_played INTEGER NOT NULL DEFAULT 0,
                    eliminations INTEGER NOT NULL DEFAULT 0
                )
                """;

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not create database tables!"
            );

            e.printStackTrace();
        }
    }

    /*
     * PLAYER
     */

    public void createPlayer(Player player) {

        String sql = """
                INSERT OR IGNORE INTO player_stats
                (uuid, name)
                VALUES (?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    player.getUniqueId().toString()
            );

            statement.setString(
                    2,
                    player.getName()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not create player in database!"
            );

            e.printStackTrace();
        }
    }

    /*
     * STATS
     */

    public void addWin(Player player) {

        createPlayer(player);

        String sql = """
                UPDATE player_stats
                SET wins = wins + 1
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    player.getUniqueId().toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not add win to player!"
            );

            e.printStackTrace();
        }
    }

    public void addGamePlayed(Player player) {

        createPlayer(player);

        String sql = """
                UPDATE player_stats
                SET games_played = games_played + 1
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    player.getUniqueId().toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not add game played to player!"
            );

            e.printStackTrace();
        }
    }

    public void addElimination(Player player) {

        createPlayer(player);

        String sql = """
                UPDATE player_stats
                SET eliminations = eliminations + 1
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    player.getUniqueId().toString()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not add elimination to player!"
            );

            e.printStackTrace();
        }
    }

    /*
     * LEADERBOARD
     */

    public List<LeaderboardEntry> getTopWins() {
        return getTop("wins");
    }

    public List<LeaderboardEntry> getTopEliminations() {
        return getTop("eliminations");
    }

    public List<LeaderboardEntry> getTopGamesPlayed() {
        return getTop("games_played");
    }

    private List<LeaderboardEntry> getTop(String column) {

        List<LeaderboardEntry> entries = new ArrayList<>();

        String sql = """
                SELECT name, %s
                FROM player_stats
                ORDER BY %s DESC
                LIMIT 10
                """.formatted(column, column);

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                entries.add(
                        new LeaderboardEntry(
                                resultSet.getString("name"),
                                resultSet.getInt(column)
                        )
                );
            }

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not get leaderboard data!"
            );

            e.printStackTrace();
        }

        return entries;
    }

    /*
     * PLAYER POSITION
     */

    public int getPlayerPosition(
            Player player,
            String column
    ) {

        createPlayer(player);

        String sql = """
                SELECT COUNT(*) + 1
                FROM player_stats
                WHERE %s > (
                    SELECT %s
                    FROM player_stats
                    WHERE uuid = ?
                )
                """.formatted(column, column);

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    player.getUniqueId().toString()
            );

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not get player leaderboard position!"
            );

            e.printStackTrace();
        }

        return -1;
    }

    /*
     * PLAYER STAT
     */

    public int getPlayerStat(
            Player player,
            String column
    ) {

        createPlayer(player);

        String sql = """
                SELECT %s
                FROM player_stats
                WHERE uuid = ?
                """.formatted(column);

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    player.getUniqueId().toString()
            );

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(column);
            }

        } catch (SQLException e) {

            plugin.getLogger().severe(
                    "Could not get player stat!"
            );

            e.printStackTrace();
        }

        return 0;
    }

    /*
     * DATABASE CONNECTION
     */

    public Connection getConnection() {
        return connection;
    }

    public void close() {

        if (connection == null) {
            return;
        }

        try {
            connection.close();

            plugin.getLogger().info(
                    "BlockedIn database closed!"
            );

        } catch (SQLException e) {

            plugin.getLogger().warning(
                    "Could not close BlockedIn database!"
            );

            e.printStackTrace();
        }
    }
}