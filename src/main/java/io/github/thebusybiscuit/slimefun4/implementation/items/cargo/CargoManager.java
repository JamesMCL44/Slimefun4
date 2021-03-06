package io.github.thebusybiscuit.slimefun4.implementation.items.cargo;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.core.attributes.HologramOwner;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockUseHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.cargo.CargoNet;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;

public class CargoManager extends SlimefunItem implements HologramOwner {

    @ParametersAreNonnullByDefault
    public CargoManager(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        registerBlockHandler(getId(), (p, b, tool, reason) -> {
            removeHologram(b);
            return true;
        });
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem item, Config data) {
                CargoNet.getNetworkFromLocationOrCreate(b.getLocation()).tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }

        }, new BlockUseHandler() {

            @Override
            public void onRightClick(PlayerRightClickEvent e) {
                Optional<Block> block = e.getClickedBlock();

                if (block.isPresent()) {
                    Player p = e.getPlayer();
                    Block b = block.get();

                    if (BlockStorage.getLocationInfo(b.getLocation(), "visualizer") == null) {
                        BlockStorage.addBlockInfo(b, "visualizer", "disabled");
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c貨運網絡可視化: " + "&4\u2718"));
                    } else {
                        BlockStorage.addBlockInfo(b, "visualizer", null);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c貨運網絡可視化: " + "&2\u2714"));
                    }
                }
            }
        });
    }

}
