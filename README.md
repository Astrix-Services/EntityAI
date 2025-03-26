# Entity AI Documentation
EntityAI is an advanced Bukkit/Spigot library for creating sophisticated, dynamic mob behaviors in Minecraft. This system empowers developers to craft intelligent entity interactions with a flexible, priority-based AI management approach.

## Features
- Dynamic AI behavior management
- Customizable behavior priority system
- Precise entity navigation
- Intelligent behavior switching
- Extensible design for custom AI behaviors

## Installation
1. Download the latest EntityAI library JAR
2. Add to your plugin's build path
3. Import required classes

## Comprehensive Usage Examples

### 1. Basic Setup and Initialization
```java
public class ZombieAIPlugin extends JavaPlugin {
    private EntityAI entityAI;

    @Override
    public void onEnable() {
        // Initialize EntityAI with your plugin instance
        entityAI = new EntityAI(this);
    }

    public void spawnEnhancedZombie(Location spawnLocation) {
        // Spawn a zombie with advanced AI
        Zombie zombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
        
        // Create AI manager for the zombie
        AIManager zombieAI = entityAI.createAIForEntity(zombie);

        // Add multiple behaviors with different priorities
        zombieAI.addBehavior(new HuntingBehavior(zombie, Player.class, 15.0));
        zombieAI.addBehavior(new TerritorialBehavior(zombie, spawnLocation, 20.0));
    }
}
```

### 2. Creating a Custom Behavior
```java
public class ScaredBehavior extends AIBehavior {
    private final double fearRadius;
    private final Class<? extends LivingEntity> threatType;

    public ScaredBehavior(Mob entity, Class<? extends LivingEntity> threatType, double fearRadius) {
        super(entity, 0.9); // High priority
        this.threatType = threatType;
        this.fearRadius = fearRadius;
    }

    @Override
    public boolean canRun() {
        // Only run if a threat is nearby
        return entity.getNearbyEntities(fearRadius, fearRadius, fearRadius)
                .stream().anyMatch(threatType::isInstance);
    }

    @Override
    public void start() {
        isActive = true;
        Bukkit.getLogger().info("Entering scared state!");
    }

    @Override
    public void update() {
        if (!isActive) return;

        // Find the nearest threat
        LivingEntity nearestThreat = EntityUtils.findNearestEntity(
                entity,
                entity.getNearbyEntities(fearRadius, fearRadius, fearRadius)
                        .stream()
                        .filter(threatType::isInstance)
                        .map(e -> (LivingEntity) e)
                        .collect(Collectors.toList()),
                fearRadius
        );

        if (nearestThreat != null) {
            // Run away from the threat
            Location fleeDirection = entity.getLocation().subtract(nearestThreat.getLocation());
            entity.teleport(entity.getLocation().add(fleeDirection.toVector().normalize().multiply(5)));
        }
    }

    @Override
    public void stop() {
        isActive = false;
        Bukkit.getLogger().info("Exiting scared state!");
    }
}
```

### 3. Advanced AI Configuration
```java
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
```

### 4. Cleanup and Disabling
```java
@Override
public void onDisable() {
    // Stop all AI processing and clean up resources
    EntityAI.getInstance().disable();
}
```

## Best Practices
- Always create AI behaviors with appropriate priority levels
- Use `canRun()` to add complex activation conditions
- Minimize computational complexity in `update()` methods
- Test behaviors thoroughly in different scenarios

## Customization Options
- Implement custom `AIBehavior` subclasses
- Fine-tune behavior priorities
- Create complex interaction rules
- Leverage `AdvancedNavigator` for precise movement