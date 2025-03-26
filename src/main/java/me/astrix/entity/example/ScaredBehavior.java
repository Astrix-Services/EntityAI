package me.astrix.entity.example;

import me.astrix.entity.behaviors.AIBehavior;
import me.astrix.entity.utils.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.stream.Collectors;

public class ScaredBehavior extends AIBehavior {

    private final double fearRadius;
    private final Class<? extends LivingEntity> threatType;

    public ScaredBehavior(Mob entity, Class<? extends LivingEntity> threatType, double fearRadius) {
        super(entity, 0.9); // High priority
        this.threatType = threatType;
        this.fearRadius = fearRadius;
    }

    @Override
    public boolean canRun() {
        // Only run if a threat is nearby
        return entity.getNearbyEntities(fearRadius, fearRadius, fearRadius)
                .stream().anyMatch(threatType::isInstance);
    }

    @Override
    public void start() {
        isActive = true;
        Bukkit.getLogger().info("Entering scared state!");
    }

    @Override
    public void update() {
        if (!isActive) return;

        // Find the nearest threat
        LivingEntity nearestThreat = EntityUtils.findNearestEntity(
                entity,
                entity.getNearbyEntities(fearRadius, fearRadius, fearRadius)
                        .stream()
                        .filter(threatType::isInstance)
                        .map(e -> (LivingEntity) e)
                        .collect(Collectors.toList()),
                fearRadius
        );

        if (nearestThreat != null) {
            // Run away from the threat
            Location fleeDirection = entity.getLocation().subtract(nearestThreat.getLocation());
            entity.teleport(entity.getLocation().add(fleeDirection.toVector().normalize().multiply(5)));
        }
    }

    @Override
    public void stop() {
        isActive = false;
        Bukkit.getLogger().info("Exiting scared state!");
    }
}
