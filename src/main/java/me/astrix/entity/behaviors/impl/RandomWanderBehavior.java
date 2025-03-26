package me.astrix.entity.behaviors.impl;

import me.astrix.entity.behaviors.EnhancedAIBehavior;
import me.astrix.entity.navigation.AdvancedNavigator;
import org.bukkit.Location;
import org.bukkit.entity.Mob;

public class RandomWanderBehavior extends EnhancedAIBehavior {

    private final AdvancedNavigator navigator;
    private final double wanderRadius;

    /**
     * Constructs a new RandomWanderBehavior for an entity.
     *
     * @param entity The mob that will wander randomly
     * @param wanderRadius The maximum distance the entity can wander from its home location
     */
    public RandomWanderBehavior(Mob entity, double wanderRadius) {
        super(entity, 0.3); // Low priority behavior
        this.wanderRadius = wanderRadius;
        this.navigator = new AdvancedNavigator(entity, 0.4, 2.0);
    }

    @Override
    public void update() {
        // Move to a new random location when current destination is reached
        // or with a small random chance to introduce variety
        if (navigator.hasReachedDestination() || random.nextDouble() < 0.1) {
            Location randomDestination = generateRandomLocation();
            navigator.moveTo(randomDestination);
        }
    }

    /**
     * Generates a random location within the wander radius.
     *
     * @return A Location representing a random destination
     */
    private Location generateRandomLocation() {
        // Generate a random angle and distance
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * wanderRadius;

        // Calculate x and z offsets
        double dx = Math.cos(angle) * distance;
        double dz = Math.sin(angle) * distance;

        // Return a new location relative to the home/center location
        return homeLocation.clone().add(dx, 0, dz);
    }

    @Override
    public void start() {
        isActive = true;
    }

    @Override
    public void stop() {
        isActive = false;
    }
}
