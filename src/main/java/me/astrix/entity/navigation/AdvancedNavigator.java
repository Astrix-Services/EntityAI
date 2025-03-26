package me.astrix.entity.navigation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

@Getter
@RequiredArgsConstructor
public class AdvancedNavigator {

    private final Mob entity;
    private final double speed;
    private final double precision;
    private Location destination;

    /**
     * Moves the mob towards the specified target location.
     * If the mob is not within the precision range of the destination, it will move towards it.
     *
     * @param target The location to which the mob should move.
     */
    public void moveTo(Location target) {
        this.destination = target;

        // If the mob is outside the precision range, move it towards the target
        if (entity.getLocation().distance(target) > precision) {
            Vector direction = target.toVector().subtract(entity.getLocation().toVector()).normalize();
            entity.setVelocity(direction.multiply(speed));
        }
    }

    /**
     * Checks whether the mob has reached the destination within the specified precision.
     *
     * @return true if the mob is within the precision distance of the destination, otherwise false.
     */
    public boolean hasReachedDestination() {
        return destination != null &&
                entity.getLocation().distance(destination) <= precision;
    }
}
