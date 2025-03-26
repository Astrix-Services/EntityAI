package me.astrix.entity.managers;

import lombok.Getter;
import me.astrix.entity.behaviors.AIBehavior;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class AIManager {

    private final Mob entity;
    private final List<AIBehavior> behaviors;
    private AIBehavior currentBehavior;
    private final List<AIBehavior> concurrentBehaviors;

    private static final int MAX_CONCURRENT_BEHAVIORS = 3;

    /**
     * Constructs an AIManager for a specific entity.
     *
     * @param entity The mob to manage
     */
    public AIManager(Mob entity) {
        this.entity = entity;
        this.behaviors = new ArrayList<>();
        this.concurrentBehaviors = new ArrayList<>();
    }

    /**
     * Adds a new behavior to the manager.
     *
     * @param behavior The behavior to add
     */
    public void addBehavior(AIBehavior behavior) {
        behaviors.add(behavior);
        // Sort behaviors by priority (descending)
        behaviors.sort(Comparator.comparingDouble(AIBehavior::getPriority).reversed());
    }

    /**
     * Updates AI behaviors for the current tick.
     * Implements advanced behavior selection and management.
     */
    public void update() {
        // Clear expired concurrent behaviors
        concurrentBehaviors.removeIf(b -> !b.canRun());

        // Select primary behavior
        AIBehavior selectedBehavior = behaviors.stream()
                .filter(AIBehavior::canRun)
                .findFirst()
                .orElse(null);

        // Manage primary behavior
        if (selectedBehavior != currentBehavior) {
            if (currentBehavior != null) {
                currentBehavior.stop();
            }
            currentBehavior = selectedBehavior;

            if (currentBehavior != null) {
                currentBehavior.start();
                Bukkit.getLogger().info("Switched to behavior: " + currentBehavior.getClass().getSimpleName());
            }
        }

        // Update primary behavior
        if (currentBehavior != null) {
            currentBehavior.update();
        }

        // Manage concurrent behaviors
        manageConcurrentBehaviors();
    }

    /**
     * Manages concurrent behaviors.
     * Allows multiple low-priority behaviors to run simultaneously.
     */
    private void manageConcurrentBehaviors() {
        behaviors.stream()
                .filter(b -> b != currentBehavior && b.canRun())
                .limit(MAX_CONCURRENT_BEHAVIORS)
                .forEach(behavior -> {
                    if (!concurrentBehaviors.contains(behavior)) {
                        behavior.start();
                        concurrentBehaviors.add(behavior);
                    }
                    behavior.update();
                });
    }

    /**
     * Removes a specific behavior from management.
     *
     * @param behavior The behavior to remove
     */
    public void removeBehavior(AIBehavior behavior) {
        behaviors.remove(behavior);
        concurrentBehaviors.remove(behavior);

        if (behavior == currentBehavior) {
            currentBehavior = null;
        }
    }

    /**
     * Clears all behaviors from the manager.
     */
    public void clearBehaviors() {
        behaviors.clear();
        concurrentBehaviors.clear();
        currentBehavior = null;
    }
}
