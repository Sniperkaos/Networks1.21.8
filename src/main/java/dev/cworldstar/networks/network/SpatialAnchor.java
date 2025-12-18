package dev.cworldstar.networks.network;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import dev.cworldstar.networks.utils.BlockStorageHelper;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

public class SpatialAnchor extends NetworkDirectional {
	
	protected SpatialAnchor(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe,
			NodeType type) {
		super(itemGroup, item, recipeType, recipe, NodeType.POWER_OUTLET);
	}

	private int getEnergyDrain(int chunks) {
		return 80 + (chunks*(chunks+1))/2;
	}
	
	public boolean isChunkLoaded(Chunk c) {
		return c.isForceLoaded();
	}
	
	@Override
	protected void onTick(BlockMenu menu, Block b) {
		super.onTick(menu, b);

		boolean isActive = BlockStorageHelper.getBoolean(b, "ntw_active");
		
		if(!isActive) {
			if(isChunkLoaded(b.getChunk())) {
				b.getChunk().setForceLoaded(false);
			}
		} else {
			if(!isChunkLoaded(b.getChunk())) {
				b.getChunk().setForceLoaded(true);
			}
		}
		
		NodeDefinition nd = NetworkStorage.getAllNetworkObjects().get(b.getLocation());
		if(nd.getCharge() < getEnergyDrain(1)) {
			BlockStorageHelper.set(b, "ntw_active", false);
		}
	}
}
