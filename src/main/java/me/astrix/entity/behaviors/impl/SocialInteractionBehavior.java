package me.astrix.entity.behaviors.impl;

import me.astrix.entity.behaviors.EnhancedAIBehavior;
import me.astrix.entity.enums.EntityEmotionalState;
import me.astrix.entity.navigation.AdvancedNavigator;
import me.astrix.entity.utils.EntityUtils;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.List;
import java.util.Optional;

public class SocialInteractionBehavior extends EnhancedAIBehavior {

    private final AdvancedNavigator navigator;
    private LivingEntity socialPartner;

    private static final double SEARCH_RADIUS = 10.0;
    private static final double INTERACTION_DISTANCE = 3.0;

    /**
     * Constructs a new SocialInteractionBehavior for an entity.
     *
     * @param entity The mob that will engage in social interactions
     */
    public SocialInteractionBehavior(Mob entity) {
        super(entity, 0.4); // Medium-low priority
        this.navigator = new AdvancedNavigator(entity, 0.5, 2.0);
    }

    @Override
    public void update() {
        // Find nearby entities of the same type
        List<LivingEntity> nearbyEntities = entity.getNearbyEntities(
                        SEARCH_RADIUS, SEARCH_RADIUS, SEARCH_RADIUS).stream()
                .filter(e -> e instanceof Mob && e.getClass().equals(entity.getClass()))
                .map(e -> (LivingEntity) e)
                .toList();

        if (!nearbyEntities.isEmpty()) {
            // Find the nearest entity
            Optional<LivingEntity> nearestEntity = EntityUtils.findNearestEntity(entity, nearbyEntities, SEARCH_RADIUS);

            // Safely assign the social partner
            socialPartner = nearestEntity.orElse(null);

            if (socialPartner != null) {
                // Move towards the social partner
                navigator.moveTo(socialPartner.getLocation());

                // Interact when close enough
                if (entity.getLocation().distance(socialPartner.getLocation()) <= INTERACTION_DISTANCE) {
                    performSocialInteraction();
                }
            }
        }
    }

    /**
     * Performs a simple social interaction between entities.
     * This can include playing sounds, creating visual effects, etc.
     */
    private void performSocialInteraction() {
        // Play a sound to simulate interaction
        entity.getWorld().playSound(
                entity.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                0.5f,
                1.0f
        );

        // Potentially change emotional state
        emotionalState = random.nextBoolean()
                ? EntityEmotionalState.PLAYFUL
                : EntityEmotionalState.CURIOUS;
    }

    @Override
    public void start() {
        isActive = true;
    }

    @Override
    public void stop() {
        isActive = false;
        socialPartner = null;
    }
}
