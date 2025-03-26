package me.astrix.entity.navigation;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

@Getter
public class AdvancedNavigator {

    private final Mob entity;
    private final double speed;
    private final double precision;
    private Location destination;

    private static final double MAX_CLIMB_HEIGHT = 1.0;
    private static final double MAX_FALL_DISTANCE = 3.0;

    /**
     * Constructs an AdvancedNavigator for a specific entity.
     *
     * @param entity The mob to navigate
     * @param speed Base movement speed
     * @param precision Destination reach precision
     */
    public AdvancedNavigator(Mob entity, double speed, double precision) {
        this.entity = entity;
        this.speed = speed;
        this.precision = precision;
    }

    /**
     * Moves the entity towards the target location with advanced pathfinding.
     *
     * @param target Destination location
     */
    public void moveTo(Location target) {
        this.destination = target;

        // Check if already close enough
        if (entity.getLocation().distance(target) <= precision) {
            return;
        }

        // Calculate direction and normalize
        Vector direction = target.toVector().subtract(entity.getLocation().toVector()).normalize();

        // Perform obstacle and terrain checks
        Location nextStep = entity.getLocation().clone().add(direction.clone().multiply(speed));

        if (isValidMove(nextStep)) {
            // Apply movement with terrain adaptation
            entity.setVelocity(direction.multiply(speed));
        } else {
            // Attempt to navigate around obstacles
            navigateAroundObstacle(direction);
        }
    }

    /**
     * Checks if a proposed movement location is valid.
     *
     * @param location Location to check
     * @return Whether the location is safe to move to
     */
    private boolean isValidMove(Location location) {
        World world = location.getWorld();
        Block feet = location.getBlock();
        Block head = location.clone().add(0, 1, 0).getBlock();
        Block below = location.clone().subtract(0, 1, 0).getBlock();

        // Check for walkable surface
        boolean validSurface = below.getType().isSolid() &&
                !below.getType().toString().contains("WATER") &&
                !below.getType().toString().contains("LAVA");

        // Check for no obstructions
        boolean noObstructions = !feet.getType().isSolid() &&
                !head.getType().isSolid();

        // Check climb and fall limitations
        double heightDifference = location.getY() - entity.getLocation().getY();
        boolean withinClimbLimit = Math.abs(heightDifference) <= MAX_CLIMB_HEIGHT;
        boolean withinFallLimit = heightDifference >= -MAX_FALL_DISTANCE;

        return validSurface && noObstructions && withinClimbLimit && withinFallLimit;
    }

    /**
     * Attempts to navigate around an obstacle by trying alternative paths.
     *
     * @param originalDirection Initial movement direction
     */
    private void navigateAroundObstacle(Vector originalDirection) {
        // Try diagonal movements
        double[][] offsets = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
        };

        for (double[] offset : offsets) {
            Vector alternateDirection = originalDirection.clone()
                    .add(new Vector(offset[0], 0, offset[1]).normalize());

            Location alternateStep = entity.getLocation().clone()
                    .add(alternateDirection.multiply(speed));

            if (isValidMove(alternateStep)) {
                entity.setVelocity(alternateDirection.multiply(speed * 0.8));
                return;
            }
        }
    }

    /**
     * Checks if the entity has reached its destination.
     *
     * @return Whether the destination has been reached
     */
    public boolean hasReachedDestination() {
        return destination != null &&
                entity.getLocation().distance(destination) <= precision;
    }

    /**
     * Stops the entity's movement.
     */
    public void stop() {
        entity.setVelocity(new Vector(0, 0, 0));
        destination = null;
    }
}
