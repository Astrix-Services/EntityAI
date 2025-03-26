package me.astrix.entity.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EntityUtils {

    /**
     * Filters a list of entities and returns only those of the specified type.
     *
     * @param entities A list of entities to filter.
     * @param type     The type of entity to filter by.
     * @param <T>      The type of entity to filter.
     * @return A list of entities that match the specified type.
     */
    public <T extends Entity> List<T> filterEntitiesByType(List<Entity> entities, Class<T> type) {
        return entities.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    /**
     * Finds the nearest entity of the specified type to the given source entity, within a maximum distance.
     * The nearest entity is determined based on the shortest distance to the source.
     *
     * @param source       The source entity from which to calculate distances.
     * @param entities     The list of entities to search through.
     * @param maxDistance  The maximum distance within which to consider entities.
     * @param <T>          The type of entities to search for.
     * @return The nearest entity of the specified type within the maximum distance, or {@code null} if none is found.
     */
    public <T extends LivingEntity> T findNearestEntity(
            LivingEntity source,
            List<T> entities,
            double maxDistance
    ) {
        return entities.stream()
                .filter(e -> source.getLocation().distance(e.getLocation()) <= maxDistance)
                .min(Comparator.comparingDouble(e -> source.getLocation().distance(e.getLocation())))
                .orElse(null);
    }
}
