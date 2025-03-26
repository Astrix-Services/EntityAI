package me.astrix.entity.behaviors;

import me.astrix.entity.enums.WeatherType;
import me.astrix.entity.navigation.AdvancedNavigator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Mob;

public class WeatherReactiveBehavior extends EnhancedAIBehavior {

    private final AdvancedNavigator navigator;

    /**
     * Constructs a new WeatherReactiveBehavior for an entity.
     *
     * @param entity The mob that will react to weather conditions
     */
    public WeatherReactiveBehavior(Mob entity) {
        super(entity, 0.5); // Medium priority
        this.navigator = new AdvancedNavigator(entity, 0.4, 2.0);
    }

    @Override
    public void update() {
        World world = entity.getWorld();
        WeatherType currentWeather = determineWeatherType(world);
        Biome currentBiome = entity.getLocation().getBlock().getBiome();

        // React based on weather type and biome
        switch (currentWeather) {
            case RAIN -> handleRainWeather(currentBiome);
            case THUNDERSTORM -> handleThunderstorm(currentBiome);
            case CLEAR -> handleClearWeather(currentBiome);
        }
    }

    /**
     * Determines the current weather type for the entity's world.
     *
     * @param world The world to check weather conditions
     * @return The current WeatherType
     */
    private WeatherType determineWeatherType(World world) {
        if (world.isThundering()) return WeatherType.THUNDERSTORM;
        if (world.hasStorm()) return WeatherType.RAIN;
        return WeatherType.CLEAR;
    }

    /**
     * Handles behavior during rainy conditions.
     *
     * @param biome The current biome of the entity
     */
    private void handleRainWeather(Biome biome) {
        Location shelterLocation = findNearestShelter();
        if (shelterLocation != null) {
            navigator.moveTo(shelterLocation);
        }
    }

    /**
     * Finds the nearest shelter location for the entity.
     *
     * @return A Location representing the nearest shelter, or null if no shelter is found
     */
    private Location findNearestShelter() {
        // TODO: Implement sophisticated shelter finding logic
        // This could involve checking for:
        // - Nearby trees
        // - Caves
        // - Structures
        // - Overhangs
        return null;
    }

    /**
     * Handles behavior during thunderstorms.
     *
     * @param biome The current biome of the entity
     */
    private void handleThunderstorm(Biome biome) {
        // Trigger panic behavior during severe storms
        new PanicBehavior(entity, 1.2).start();
    }

    /**
     * Handles behavior during clear weather.
     *
     * @param biome The current biome of the entity
     */
    private void handleClearWeather(Biome biome) {
        // Default behavior or optional exploration
    }

    @Override
    public void start() {
        isActive = true;
    }

    @Override
    public void stop() {
        isActive = false;
    }
}
