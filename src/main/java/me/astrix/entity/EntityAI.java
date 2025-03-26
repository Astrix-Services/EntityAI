package me.astrix.entity;

import lombok.Getter;
import me.astrix.entity.managers.AIManager;
import org.bukkit.entity.Mob;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EntityAI {

    @Getter
    private static EntityAI instance;
    private final Plugin plugin;
    private final List<AIManager> managedEntities;
    private BukkitTask aiUpdateTask;

    /**
     * Constructor for the EntityAI class.
     *
     * @param plugin The instance of the plugin using this AI library.
     */
    public EntityAI(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.managedEntities = new ArrayList<>();

        // Optional: Start AI update loop
        startAIUpdateLoop();
    }

    /**
     * Adds an entity to be managed by the AI system.
     *
     * @param entity The mob entity to be managed.
     * @return The created AIManager for the entity.
     */
    public AIManager createAIForEntity(Mob entity) {
        AIManager aiManager = new AIManager(entity);
        managedEntities.add(aiManager);
        return aiManager;
    }

    /**
     * Removes an entity from AI management.
     *
     * @param aiManager The AIManager to remove.
     */
    public void removeAIManager(AIManager aiManager) {
        managedEntities.remove(aiManager);
    }

    /**
     * Starts the AI update loop that runs every tick.
     */
    private void startAIUpdateLoop() {
        aiUpdateTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            // Create a copy to avoid concurrent modification
            List<AIManager> currentManagers = new ArrayList<>(managedEntities);
            currentManagers.forEach(AIManager::update);
        }, 0L, 1L); // Run every tick
    }

    /**
     * Stops the AI update loop.
     */
    public void stopAIUpdateLoop() {
        if (aiUpdateTask != null) {
            aiUpdateTask.cancel();
            aiUpdateTask = null;
        }
    }

    /**
     * Cleanup method to stop AI processing.
     */
    public void disable() {
        stopAIUpdateLoop();
        managedEntities.clear();
    }
}
