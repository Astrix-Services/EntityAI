package me.astrix.entity.example.custom;

import me.astrix.entity.EntityAI;
import me.astrix.entity.behaviors.impl.*;
import me.astrix.entity.managers.AIManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Zombie;

public class CustomMobs {

    /**
     * Creates a Forest Guardian mob - a territorial, weather-reactive creature
     * that patrols its woodland territory and seeks shelter during storms.
     *
     * @param location Spawn location for the Forest Guardian
     * @return The spawned Forest Guardian mob
     */
    public Mob spawnForestGuardian(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("The location or the world cannot be null.");
        }

        World world = location.getWorld();
        Zombie forestGuardian = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);

        // Customize mob appearance
        forestGuardian.setCustomName("Forest Guardian");
        forestGuardian.setCustomNameVisible(true);

        // Create AI Manager
        AIManager aiManager = EntityAI.getInstance().createAIForEntity(forestGuardian);

        // Add behaviors
        aiManager.addBehavior(new TerritorialBehavior(forestGuardian, location, 20.0));
        aiManager.addBehavior(new WeatherReactiveBehavior(forestGuardian));
        aiManager.addBehavior(new RandomWanderBehavior(forestGuardian, 15.0));

        return forestGuardian;
    }

    /**
     * Creates a Pack Hunter mob - a social, hunting creature that works in groups
     * and targets specific prey.
     *
     * @param location Spawn location for the Pack Hunter
     */
    public void spawnPackHunter(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("The location or the world cannot be null.");
        }

        World world = location.getWorld();
        Zombie packHunter = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);

        // Customize mob appearance
        packHunter.setCustomName("Pack Hunter");
        packHunter.setCustomNameVisible(true);

        // Create AI Manager
        AIManager aiManager = EntityAI.getInstance().createAIForEntity(packHunter);

        // Add behaviors
        aiManager.addBehavior(new HuntingBehavior(packHunter, Zombie.class, 15.0));
        aiManager.addBehavior(new SocialInteractionBehavior(packHunter));
        aiManager.addBehavior(new RandomWanderBehavior(packHunter, 10.0));
    }


    /**
     * Creates a Skittish Scout mob - a cautious creature that panics easily
     * and has complex social and survival behaviors.
     *
     * @param location Spawn location for the Skittish Scout
     * @return The spawned Skittish Scout mob
     */
    public Mob spawnSkittishScout(Location location) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("The location or the world cannot be null.");
        }

        World world = location.getWorld();
        Zombie skittishScout = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);

        // Customize mob appearance
        skittishScout.setCustomName("Skittish Scout");
        skittishScout.setCustomNameVisible(true);

        // Create AI Manager
        AIManager aiManager = EntityAI.getInstance().createAIForEntity(skittishScout);

        // Add behaviors
        aiManager.addBehavior(new PanicBehavior(skittishScout, 0.6));
        aiManager.addBehavior(new SocialInteractionBehavior(skittishScout));
        aiManager.addBehavior(new WeatherReactiveBehavior(skittishScout));
        aiManager.addBehavior(new RandomWanderBehavior(skittishScout, 8.0));

        return skittishScout;
    }

    /**
     * Spawns multiple Pack Hunters to demonstrate group behavior.
     *
     * @param centerLocation Center location for spawning pack
     * @param packSize Number of pack hunters to spawn
     */
    public void spawnPackHunterPack(Location centerLocation, int packSize) {
        for (int i = 0; i < packSize; i++) {
            // Spread pack members around the center location
            double angle = 2 * Math.PI * i / packSize;
            double radius = 5.0;

            Location memberLocation = centerLocation.clone().add(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
            );

            spawnPackHunter(memberLocation);
        }
    }
}
