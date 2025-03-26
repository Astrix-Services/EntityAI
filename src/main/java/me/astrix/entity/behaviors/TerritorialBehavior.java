package me.astrix.entity.behaviors;

import me.astrix.entity.navigation.AdvancedNavigator;
import org.bukkit.Location;
import org.bukkit.entity.Mob;

public class TerritorialBehavior extends AIBehavior {

    private final Location territoryCenter;
    private final double territoryRadius;

    private final AdvancedNavigator navigator;

    /**
     * Creates a new territorial behavior for the given defender.
     *
     * @param defender        The mob that will be performing the territorial behavior.
     * @param territoryCenter The center point of the territory.
     * @param territoryRadius The radius of the territory to patrol.
     */
    public TerritorialBehavior(Mob defender, Location territoryCenter, double territoryRadius) {
        super(defender, 0.7);
        this.territoryCenter = territoryCenter;
        this.territoryRadius = territoryRadius;
        this.navigator = new AdvancedNavigator(defender, 0.5, 1.0);
    }

    /**
     * Activates the territorial behavior.
     * This allows the mob to start patrolling and defending its territory.
     */
    @Override
    public void start() {
        isActive = true;
    }

    /**
     * Updates the territorial behavior each tick.
     * <p>
     * The mob will check if it is within its territory. If it is outside the territory, it will move towards the center.
     * If any intruders (other mobs) are nearby, they will be damaged by the defender.
     * </p>
     */
    @Override
    public void update() {
        if (!isActive) return;

        // Move the mob towards the center if it's outside the territory
        if (entity.getLocation().distance(territoryCenter) > territoryRadius) {
            navigator.moveTo(territoryCenter);
        }

        // If any intruders are nearby, damage them
        entity.getNearbyEntities(5, 5, 5).stream()
                .filter(e -> e instanceof Mob && e != entity)
                .findFirst()
                .ifPresent(intruder -> {
                    if (intruder instanceof Mob) {
                        ((Mob)intruder).damage(1.5);
                    }
                });
    }

    /**
     * Stops the territorial behavior, deactivating it and preventing further actions.
     */
    @Override
    public void stop() {
        isActive = false;
    }
}
