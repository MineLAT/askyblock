package com.wasteofplastic.askyblock.listeners;

import com.wasteofplastic.askyblock.Settings;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionEvents {

    public PermissionEvents() {
        final EventBus eventBus = LuckPermsProvider.get().getEventBus();
        eventBus.subscribe(NodeClearEvent.class, event -> {
            final Player player = Bukkit.getPlayer(((User) event.getTarget()).getUniqueId());
            if (player == null) {
                return;
            }
            if (event.isUser()) {
                for (Node node : event.getNodes()) {
                    if (node.hasExpired() && node instanceof PermissionNode && ((PermissionNode) node).getPermission().equals(Settings.PERMPREFIX + "islandfly")) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                    }
                }
            }
        });
        eventBus.subscribe(NodeRemoveEvent.class, event -> {
            final Player player = Bukkit.getPlayer(((User) event.getTarget()).getUniqueId());
            if (player == null) {
                return;
            }
            final Node node = event.getNode();
            if (node.hasExpired() && node instanceof PermissionNode && ((PermissionNode) node).getPermission().equals(Settings.PERMPREFIX + "islandfly")) {
                player.setFlying(false);
                player.setAllowFlight(false);
            }
        });
    }
}
