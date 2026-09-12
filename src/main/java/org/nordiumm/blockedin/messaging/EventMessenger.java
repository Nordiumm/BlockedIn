package org.nordiumm.blockedin.messaging;

import com.google.gson.JsonObject;
import org.nordiumm.blockedin.BlockedIn;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class EventMessenger {

    private static final String CHANNEL = "nixon:events";

    private final BlockedIn plugin;

    public EventMessenger(BlockedIn plugin) {
        this.plugin = plugin;
    }

    public void finishEvent(UUID winner, int points) {
        JsonObject data = new JsonObject();

        data.addProperty(
                "winner",
                winner.toString()
        );

        data.addProperty(
                "points",
                points
        );

        JsonObject message = new JsonObject();

        message.addProperty(
                "type",
                "EVENT_FINISHED"
        );

        message.add(
                "data",
                data
        );

        byte[] bytes = message.toString()
                .getBytes(StandardCharsets.UTF_8);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendPluginMessage(
                    plugin,
                    CHANNEL,
                    bytes
            );

            break;
        }
    }
}