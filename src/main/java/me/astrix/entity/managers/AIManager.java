package me.astrix.entity.managers;

import lombok.Getter;
import me.astrix.entity.behaviors.AIBehavior;
import org.bukkit.entity.Mob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class AIManager {

    private final Mob entity;
    private final List<AIBehavior> behaviors;
    private AIBehavior currentBehavior;

    /**
     * Creates a new AIManager for the given entity.
     *
     * @param entity The mob entity that this AI manager will control.
     */
    public AIManager(Mob entity) {
        this.entity = entity;
        this.behaviors = new ArrayList<>();
    }

    /**
     * Adds a new AI behavior to the list of behaviors and sorts them by priority.
     * The behaviors are sorted in descending order, with higher priority behaviors being considered first.
     *
     * @param behavior The AI behavior to add.
     */
    public void addBehavior(AIBehavior behavior) {
        behaviors.add(behavior);
        behaviors.sort(Comparator.comparingDouble(AIBehavior::getPriority).reversed());
    }

    /**
     * Updates the AI behaviors each tick.
     * It checks for the first behavior that can run, stops the current behavior if necessary,
     * and starts the selected behavior. Then, it updates the active behavior if there is one.
     */
    public void update() {
        // Find the first behavior that can run based on the 'canRun' method
        AIBehavior selectedBehavior = behaviors.stream()
                .filter(AIBehavior::canRun)
                .findFirst()
                .orElse(null);

        // Switch to the new behavior if it's different from the current one
        if (selectedBehavior != currentBehavior) {
            if (currentBehavior != null) {
                currentBehavior.stop();
            }

            currentBehavior = selectedBehavior;
            if (currentBehavior != null) {
                currentBehavior.start();
            }
        }

        // Update the current behavior if it exists
        if (currentBehavior != null) {
            currentBehavior.update();
        }
    }
}
