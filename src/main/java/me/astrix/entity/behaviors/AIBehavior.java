package me.astrix.entity.behaviors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Mob;

@Getter
@RequiredArgsConstructor
public abstract class AIBehavior {

    /**
     * The entity that this behavior is associated with.
     */
    protected final Mob entity;

    /**
     * The priority level of this behavior.
     * A higher value may indicate a more important behavior.
     */
    protected final double priority;

    /**
     * Indicates whether the behavior is currently active.
     */
    protected boolean isActive;

    /**
     * Called when the behavior starts.
     * Implementations should define what happens when the behavior is initiated.
     */
    public abstract void start();

    /**
     * Called every tick to update the behavior.
     * Implementations should define what happens during each update cycle.
     */
    public abstract void update();

    /**
     * Called when the behavior stops.
     * Implementations should define what happens when the behavior is terminated.
     */
    public abstract void stop();

    /**
     * Determines whether this behavior can be executed.
     * By default, it always returns {@code true}, but it can be overridden to add conditions.
     *
     * @return {@code true} if the behavior can run, otherwise {@code false}.
     */
    public boolean canRun() {
        return true;
    }
}
