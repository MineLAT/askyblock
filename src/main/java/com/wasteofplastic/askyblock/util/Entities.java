package com.wasteofplastic.askyblock.util;

import org.bukkit.entity.Bat;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.bukkit.entity.ZombieHorse;
import org.jetbrains.annotations.NotNull;

public class Entities {

    private static final boolean DETECT_SHULKER;
    static {
        boolean detectShulker = false;
        try {
            Class.forName("org.bukkit.entity.Shulker");
            detectShulker = true;
        } catch (Throwable ignored) { }
        DETECT_SHULKER = detectShulker;
    }

    Entities() {
    }

    public static boolean isMonster(@NotNull Entity entity) {
        return entity instanceof Monster
                || entity instanceof Bat
                || entity instanceof EnderDragon
                || entity instanceof Ghast
                || entity instanceof SkeletonHorse
                || entity instanceof Squid
                || entity instanceof Slime
                || entity instanceof ZombieHorse
                || (DETECT_SHULKER && entity instanceof Shulker);
    }
}
