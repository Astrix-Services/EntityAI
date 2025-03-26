package me.astrix.entity.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class EntityUtils {

    /**
     * Filters entities by a specific type with additional filtering options.
     *
     * @param entities List of entities to filter
     * @param type Target entity type
     * @param additionalFilter Optional additional filtering predicate
     * @param <T> Type of entity
     * @return Filtered list of entities
     */
    public <T extends Entity> List<T> filterEntities(
            List<Entity> entities,
            Class<T> type,
            Predicate<T> additionalFilter
    ) {
        return entities.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(additionalFilter == null ? e -> true : additionalFilter)
                .collect(Collectors.toList());
    }

    /**
     * Finds the nearest entity to a source entity with advanced filtering.
     *
     * @param source Source entity for distance calculation
     * @param entities List of potential target entities
     * @param maxDistance Maximum search distance
     * @param filter Optional additional filtering predicate
     * @param <T> Type of entity
     * @return Optional containing the nearest entity
     */
    public <T extends LivingEntity> Optional<T> findNearestEntity(
            LivingEntity source,
            List<T> entities,
            double maxDistance,
            Predicate<T> filter
    ) {
        return entities.stream()
                .filter(e -> source.getLocation().distance(e.getLocation()) <= maxDistance)
                .filter(filter == null ? e -> true : filter)
                .min(Comparator.comparingDouble(e -> source.getLocation().distance(e.getLocation())));
    }

    /**
     * Convenience method to find nearest entity without additional filtering.
     *
     * @param source Source entity for distance calculation
     * @param entities List of potential target entities
     * @param maxDistance Maximum search distance
     * @param <T> Type of entity
     * @return Optional containing the nearest entity
     */
    public <T extends LivingEntity> Optional<T> findNearestEntity(
            LivingEntity source,
            List<T> entities,
            double maxDistance
    ) {
        return findNearestEntity(source, entities, maxDistance, null);
    }

    /**
     * Calculates the average location of a group of entities.
     *
     * @param entities List of entities
     * @return Average location
     */
    public Location calculateAverageLocation(List<? extends Entity> entities) {
        if (entities.isEmpty()) {
            throw new IllegalArgumentException("Entity list cannot be empty");
        }

        double avgX = entities.stream()
                .mapToDouble(e -> e.getLocation().getX())
                .average()
                .orElse(0.0);

        double avgY = entities.stream()
                .mapToDouble(e -> e.getLocation().getY())
                .average()
                .orElse(0.0);

        double avgZ = entities.stream()
                .mapToDouble(e -> e.getLocation().getZ())
                .average()
                .orElse(0.0);

        return new Location(entities.get(0).getWorld(), avgX, avgY, avgZ);
    }

    /**
     * Checks if entities are within a group/pack radius.
     *
     * @param reference Reference entity
     * @param entities Entities to check
     * @param maxGroupRadius Maximum group radius
     * @return Whether entities are within the group radius
     */
    public boolean areEntitiesInGroup(
            LivingEntity reference,
            List<? extends LivingEntity> entities,
            double maxGroupRadius
    ) {
        return entities.stream()
                .anyMatch(e -> reference.getLocation().distance(e.getLocation()) <= maxGroupRadius);
    }
}
