package org.nordiumm.blockedin.game;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.nordiumm.blockedin.GameState;

public class GameTimer {

    private BossBar bossBar;
    private final BlockedInGame game;

    private BukkitTask countdownTask;
    private BukkitTask gameTimerTask;

    public GameTimer(BlockedInGame game) {
        this.game = game;
    }

    public void startCountdown() {
        game.setState(GameState.COUNTDOWN);
        int countdown = game.getPlugin()
                .getConfig()
                .getInt("game.countdown");

        countdownTask = new BukkitRunnable() {

            int seconds = countdown;

            @Override
            public void run() {

                if (seconds <= 0) {
                    for (Player player : game.getPlayers()) {
                        player.sendMessage("§eBlockedIn §7» §aGO!");
                    }

                    game.setState(GameState.RUNNING);

                    cancel();
                    startGameTimer();
                    startBorderCountdown();
                    return;
                }

                for (Player player : game.getPlayers()) {
                    player.sendMessage("§eBlockedIn §7» §e" + seconds);
                }

                seconds--;
            }

        }.runTaskTimer(game.getPlugin(), 0L, 20L);
    }

    private void startGameTimer() {
        bossBar = Bukkit.createBossBar(
                "BlockedIn • 00:00",
                BarColor.WHITE,
                BarStyle.SOLID
        );

        for (Player player : game.getPlayers()) {
            bossBar.addPlayer(player);
        }

        gameTimerTask = new BukkitRunnable() {

            int seconds = 0;

            @Override
            public void run() {

                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;

                bossBar.setTitle(
                        String.format(
                                "BlockedIn • %02d:%02d",
                                minutes,
                                remainingSeconds
                        )
                );

                seconds++;
            }

        }.runTaskTimer(game.getPlugin(), 0L, 20L);
    }

    public void stop() {

        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }

        if (gameTimerTask != null) {
            gameTimerTask.cancel();
            gameTimerTask = null;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
    }

    private void startBorderCountdown() {

        long delaySeconds = game.getPlugin()
                .getConfig()
                .getLong("world-border.start-delay");

        for (Player player : game.getPlayers()) {
            player.sendMessage(
                    "§eBlockedIn §7» §fThe world border will start shrinking in §e"
                            + delaySeconds
                            + " §fseconds!"
            );
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                if (game.getState() != GameState.RUNNING) {
                    cancel();
                    return;
                }

                for (Player player : game.getPlayers()) {
                    player.sendMessage(
                            "§eBlockedIn §7» §cThe world border is shrinking!"
                    );
                }

                game.startBorder();
            }

        }.runTaskLater(
                game.getPlugin(),
                delaySeconds * 20L
        );
    }
}