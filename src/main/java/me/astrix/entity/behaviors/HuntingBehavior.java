package me.astrix.entity.behaviors;

import lombok.Getter;
import me.astrix.entity.navigation.AdvancedNavigator;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.List;

@Getter
public class HuntingBehavior extends AIBehavior {

    private final Class<? extends LivingEntity> preyType;
    private final AdvancedNavigator navigator;
    private LivingEntity currentTarget;
    private final double searchRadius;

    /**
     * Creates a new hunting behavior for the given hunter.
     *
     * @param hunter      The mob that will be performing the hunting behavior.
     * @param preyType    The type of entity that this hunter will target.
     * @param searchRadius The radius in which the hunter searches for prey.
     */
    public HuntingBehavior(Mob hunter, Class<? extends LivingEntity> preyType, double searchRadius) {
        super(hunter, 0.8);
        this.preyType = preyType;
        this.searchRadius = searchRadius;
        this.navigator = new AdvancedNavigator(hunter, 0.6, 2.0);
    }

    /**
     * Activates the hunting behavior.
     */
    @Override
    public void start() {
        isActive = true;
    }

    /**
     * Updates the hunting behavior each tick.
     * <p>
     * The hunter will search for nearby entities of the specified prey type and move towards the closest one.
     * If the hunter reaches the prey within a distance of 2 blocks, it will deal damage.
     * </p>
     */
    @Override
    public void update() {
        if (!isActive) return;

        // Find all nearby entities of the specified prey type
        List<LivingEntity> potentialPrey = entity.getNearbyEntities(searchRadius, searchRadius, searchRadius)
                .stream()
                .filter(preyType::isInstance)
                .map(e -> (LivingEntity) e)
                .toList();

        // If there is a valid target, move towards it and attack when close enough
        if (!potentialPrey.isEmpty()) {
            currentTarget = potentialPrey.get(0);
            navigator.moveTo(currentTarget.getLocation());

            if (entity.getLocation().distance(currentTarget.getLocation()) <= 2.0) {
                currentTarget.damage(2.0);
            }
        }
    }

    /**
     * Stops the hunting behavior and resets the target.
     */
    @Override
    public void stop() {
        isActive = false;
        currentTarget = null;
    }
}
