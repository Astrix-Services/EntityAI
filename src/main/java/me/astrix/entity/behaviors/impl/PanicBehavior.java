package me.astrix.entity.behaviors.impl;

import me.astrix.entity.behaviors.EnhancedAIBehavior;
import me.astrix.entity.enums.EntityEmotionalState;
import me.astrix.entity.navigation.AdvancedNavigator;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.List;

public class PanicBehavior extends EnhancedAIBehavior {

    private final AdvancedNavigator navigator;
    private final double panicSpeedMultiplier;

    /**
     * Constructs a new PanicBehavior for an entity.
     *
     * @param entity The mob experiencing panic
     * @param baseSpeed The base movement speed of the entity
     */
    public PanicBehavior(Mob entity, double baseSpeed) {
        super(entity, 1.0); // High priority behavior
        this.panicSpeedMultiplier = baseSpeed * 1.5;
        this.navigator = new AdvancedNavigator(entity, panicSpeedMultiplier, 1.0);
    }

    @Override
    public void start() {
        isActive = true;
        emotionalState = EntityEmotionalState.SCARED;
    }

    @Override
    public void update() {
        // Stop panicking if no longer in danger
        if (!isInDanger()) {
            stop();
            return;
        }

        // Find nearby threats
        List<LivingEntity> nearbyThreats = entity.getNearbyEntities(5, 5, 5).stream()
                .filter(e -> e instanceof Mob)
                .map(e -> (LivingEntity) e)
                .toList();

        if (!nearbyThreats.isEmpty()) {
            // Get the closest threat
            LivingEntity threat = nearbyThreats.get(0);
            Location threatLocation = threat.getLocation();

            // Calculate panic direction (away from threat)
            Vector panicDirection = entity.getLocation().toVector()
                    .subtract(threatLocation.toVector())
                    .normalize()
                    .multiply(panicSpeedMultiplier);

            // Move to panic destination
            Location panicDestination = entity.getLocation().clone().add(panicDirection);
            navigator.moveTo(panicDestination);
        }
    }

    @Override
    public void stop() {
        isActive = false;
        emotionalState = EntityEmotionalState.NEUTRAL;
        // Return to home/original location
        navigator.moveTo(homeLocation);
    }

    @Override
    public boolean canRun() {
        return isInDanger();
    }
}
