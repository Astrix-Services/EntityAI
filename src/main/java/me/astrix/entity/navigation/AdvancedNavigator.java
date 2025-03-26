package me.astrix.entity.navigation;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * Attempts to navigate around an obstacle by exploring alternative movement paths.
     *
     * This method helps an entity avoid getting stuck when its original movement path
     * is blocked. It systematically checks orthogonal and diagonal directions to find
     * a valid alternative route, applying a slight speed reduction to simulate cautious movement.
     *
     * @param originalDirection The initial movement vector attempting to be traversed
     * @throws IllegalStateException if no valid movement can be found after checking all potential paths
     * @see Vector
     * @see Location
     */
    private void navigateAroundObstacle(Vector originalDirection) {
        // Define potential movement offsets covering all primary and diagonal directions
        List<Vector> potentialMoves = List.of(
                new Vector(1, 0, 0),   // Right
                new Vector(-1, 0, 0),  // Left
                new Vector(0, 0, 1),   // Forward
                new Vector(0, 0, -1),  // Backward
                new Vector(1, 0, 1),   // Diagonal right-forward
                new Vector(-1, 0, -1), // Diagonal left-backward
                new Vector(1, 0, -1),  // Diagonal right-backward
                new Vector(-1, 0, 1)   // Diagonal left-forward
        );

        // Find a valid move using streams and Optional for efficient path exploration
        Optional<Vector> safeMove = potentialMoves.stream()
                .map(offset -> {
                    // Create an alternate direction by combining original and offset vectors
                    Vector alternateDirection = originalDirection.clone()
                            .add(offset.normalize());

                    // Calculate the potential new location after applying the alternate direction
                    Location alternateStep = entity.getLocation().clone()
                            .add(alternateDirection.multiply(speed));

                    // Return the alternate direction if the move is valid, otherwise null
                    return isValidMove(alternateStep) ? alternateDirection : null;
                })
                .filter(Objects::nonNull)
                .findFirst();

        // Apply the movement if a safe alternative is found, otherwise potentially throw an exception
        safeMove.ifPresentOrElse(
                move -> {
                    // Reduce speed to 80% to simulate cautious navigation around obstacles
                    entity.setVelocity(move.multiply(speed * 0.8));
                },
                () -> {
                    // Optional: Log or handle the case where no valid move is found
                    throw new IllegalStateException("No valid movement path found around obstacle");
                }
        );
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
