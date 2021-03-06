package io.github.thebusybiscuit.slimefun4.testing.tests.geo;

import java.util.Optional;

import org.bukkit.NamespacedKey;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import be.seeseemelk.mockbukkit.MockBukkit;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import io.github.thebusybiscuit.slimefun4.implementation.resources.GEOResourcesSetup;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;

@TestMethodOrder(value = OrderAnnotation.class)
public class TestResourceRegistration {

    private static SlimefunPlugin plugin;

    @BeforeAll
    public static void load() {
        MockBukkit.mock();
        plugin = MockBukkit.load(SlimefunPlugin.class);
    }

    @AfterAll
    public static void unload() {
        MockBukkit.unmock();
    }

    @Test
    @Order(value = 1)
    public void testDefaultResources() {
        Assertions.assertDoesNotThrow(GEOResourcesSetup::setup);
    }

    @Test
    @Order(value = 2)
    public void testDoubleRegistration() {
        Assertions.assertThrows(IllegalArgumentException.class, GEOResourcesSetup::setup);
    }

    private GEOResource testResource(NamespacedKey key, String name, ItemStack item, boolean miner, int deviation) {
        Optional<GEOResource> optional = SlimefunPlugin.getRegistry().getGEOResources().get(key);
        Assertions.assertTrue(optional.isPresent());

        GEOResource resource = optional.get();
        Assertions.assertEquals(name, resource.getName());
        Assertions.assertEquals(miner, resource.isObtainableFromGEOMiner());
        Assertions.assertTrue(SlimefunUtils.isItemSimilar(resource.getItem(), item, true));
        Assertions.assertEquals(deviation, resource.getMaxDeviation());
        return resource;
    }

    @Test
    public void testOilResource() {
        NamespacedKey key = new NamespacedKey(plugin, "oil");
        GEOResource resource = testResource(key, "原油", SlimefunItems.OIL_BUCKET, false, 8);

        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.BEACH));
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.DESERT) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.MOUNTAINS) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.ICE_SPIKES) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.BADLANDS) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.OCEAN) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.SWAMP) > 10);
        Assertions.assertEquals(10, resource.getDefaultSupply(Environment.NORMAL, Biome.SUNFLOWER_PLAINS));
    }

    @Test
    public void testNetherIceResource() {
        NamespacedKey key = new NamespacedKey(plugin, "nether_ice");
        GEOResource resource = testResource(key, "地獄冰", SlimefunItems.NETHER_ICE, true, 6);

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.DESERT));
    }

    @Test
    public void testUraniumResource() {
        NamespacedKey key = new NamespacedKey(plugin, "uranium");
        GEOResource resource = testResource(key, "小塊鈾", SlimefunItems.SMALL_URANIUM, true, 2);

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.MOUNTAINS));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.THE_END, Biome.THE_END));
    }

    @Test
    public void testSaltResource() {
        NamespacedKey key = new NamespacedKey(plugin, "salt");
        GEOResource resource = testResource(key, "鹽", SlimefunItems.SALT, true, 18);

        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.NETHER, Biome.NETHER_WASTES));
        Assertions.assertEquals(0, resource.getDefaultSupply(Environment.THE_END, Biome.THE_END));

        Assertions.assertNotEquals(0, resource.getDefaultSupply(Environment.NORMAL, Biome.MOUNTAINS));
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.BEACH) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.OCEAN) > 10);
        Assertions.assertTrue(resource.getDefaultSupply(Environment.NORMAL, Biome.SWAMP) > 10);
    }
}
