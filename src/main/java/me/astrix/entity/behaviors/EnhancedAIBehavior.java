package me.astrix.entity.behaviors;

import lombok.Getter;
import lombok.Setter;
import me.astrix.entity.enums.EntityEmotionalState;
import org.bukkit.Location;
import org.bukkit.entity.Mob;

import java.util.Random;

@Getter
@Setter
public abstract class EnhancedAIBehavior extends AIBehavior {

    /** Random instance for behavior calculations */
    protected final Random random = new Random();

    /** The emotional state of the entity, default is NEUTRAL */
    protected EntityEmotionalState emotionalState = EntityEmotionalState.NEUTRAL;

    /** The home or starting location of the entity */
    protected Location homeLocation;

    /**
     * Constructor for the enhanced AI behavior.
     *
     * @param entity The mob this behavior is associated with
     * @param priority The priority level of this behavior
     */
    public EnhancedAIBehavior(Mob entity, double priority) {
        super(entity, priority);
        this.homeLocation = entity.getLocation().clone();
    }

    /**
     * Updates the emotional state of the entity.
     * Can be overridden by specific behaviors to implement custom state logic.
     */
    public void updateEmotionalState() {
        // Default implementation - can be extended by subclasses
    }

    /**
     * Determines if the entity is currently in danger.
     *
     * @return true if the entity is threatened, false otherwise
     */
    protected boolean isInDanger() {
        return entity.getNearbyEntities(5, 5, 5).stream()
                .filter(e -> e instanceof Mob)
                .anyMatch(e -> {
                    Mob nearbyMob = (Mob) e;
                    return nearbyMob.getHealth() > entity.getHealth() &&
                            nearbyMob.getLocation().distance(entity.getLocation()) < 3;
                });
    }
}
