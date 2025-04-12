package com.wasteofplastic.askyblock.listeners;

import com.wasteofplastic.askyblock.ASkyBlock;
import com.wasteofplastic.askyblock.Settings;
import com.wasteofplastic.askyblock.util.VaultHelper;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionEvents {

    public PermissionEvents() {
        final EventBus eventBus = LuckPermsProvider.get().getEventBus();
        eventBus.subscribe(NodeClearEvent.class, event -> {
            if (!(event.getTarget() instanceof User)) {
                return;
            }
            final Player player = Bukkit.getPlayer(((User) event.getTarget()).getUniqueId());
            if (player == null) {
                return;
            }
            if (event.isUser()) {
                for (Node node : event.getNodes()) {
                    if (node.hasExpired()) {
                        if (node instanceof PermissionNode && ((PermissionNode) node).getPermission().equals(Settings.PERMPREFIX + "islandfly")) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                        } else if (node instanceof InheritanceNode) {
                            Bukkit.getScheduler().runTaskLater(ASkyBlock.getPlugin(), () -> {
                                if (!VaultHelper.checkPerm(player, Settings.PERMPREFIX + "islandfly")) {
                                    player.setFlying(false);
                                    player.setAllowFlight(false);
                                }
                            }, 20L);
                        }
                    }
                }
            }
        });
        eventBus.subscribe(NodeRemoveEvent.class, event -> {
            if (!(event.getTarget() instanceof User)) {
                return;
            }
            final Player player = Bukkit.getPlayer(((User) event.getTarget()).getUniqueId());
            if (player == null) {
                return;
            }
            final Node node = event.getNode();
            if (node.hasExpired()) {
                if (node instanceof PermissionNode && ((PermissionNode) node).getPermission().equals(Settings.PERMPREFIX + "islandfly")) {
                    player.setFlying(false);
                    player.setAllowFlight(false);
                } else if (node instanceof InheritanceNode) {
                    Bukkit.getScheduler().runTaskLater(ASkyBlock.getPlugin(), () -> {
                        if (!VaultHelper.checkPerm(player, Settings.PERMPREFIX + "islandfly")) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                        }
                    }, 20L);
                }
            }
        });
    }
}
