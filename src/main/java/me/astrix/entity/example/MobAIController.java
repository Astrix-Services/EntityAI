package me.astrix.entity.example;

import me.astrix.entity.EntityAI;
import me.astrix.entity.behaviors.HuntingBehavior;
import me.astrix.entity.behaviors.TerritorialBehavior;
import me.astrix.entity.managers.AIManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;

public class MobAIController {

    public void setupAdvancedMobAI(World world) {
        // Create a specific location for spawning and territory
        Location spawnLocation = new Location(world, 100, 64, 100);
        Location villageCenter = new Location(world, 100, 64, 100);

        // Spawn a complex mob with multiple behaviors
        Mob guardEntity = (Mob) world.spawnEntity(spawnLocation, EntityType.IRON_GOLEM);

        AIManager guardAI = EntityAI.getInstance().createAIForEntity(guardEntity);

        // Territorial behavior with high priority
        guardAI.addBehavior(new TerritorialBehavior(
                guardEntity,
                villageCenter,
                50.0  // Large patrol radius
        ));

        // Hunting behavior to protect against hostile mobs
        guardAI.addBehavior(new HuntingBehavior(
                guardEntity,
                Monster.class,  // Target all monster types
                30.0  // Search radius
        ));

        // Custom scared behavior as a fallback
        guardAI.addBehavior(new ScaredBehavior(
                guardEntity,
                Creeper.class,  // Specific threat type
                10.0  // Fear radius
        ));
    }
}
