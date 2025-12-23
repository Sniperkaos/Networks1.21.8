package dev.cworldstar.networks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.cworldstar.networks.network.AdvancedNetworkPusher;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.NetworksItemGroups;
import io.github.sefiraat.networks.slimefun.NetworksSlimefunItemStacks;

public class NetworksExtension {
	public static void setup(Networks networks) {
		new AdvancedNetworkPusher(
				NetworksItemGroups.NETWORK_ITEMS,
				Theme.themedSlimefunItemStack(
					"NTW_ADVANCED_PUSHER", 
					new ItemStack(Material.BROWN_STAINED_GLASS), 
					Theme.MACHINE, 
			        "Advanced Network Pusher",
			        "The ANP (Advanced Network Pusher)",
			        "functions the exact same as the original,",
			        "with more slots to place filter items."
				),
				RecipeType.ENHANCED_CRAFTING_TABLE,
				new ItemStack[] {
					NetworksSlimefunItemStacks.ADVANCED_NANOBOTS.item(), NetworksSlimefunItemStacks.OPTIC_CABLE.item(), NetworksSlimefunItemStacks.OPTIC_CABLE.item(),
					SlimefunItems.CARGO_OUTPUT_NODE_2.item(), NetworksSlimefunItemStacks.NETWORK_PUSHER.item(), NetworksSlimefunItemStacks.ADVANCED_NANOBOTS.item(),
					NetworksSlimefunItemStacks.PRISTINE_AI_CORE.item(), NetworksSlimefunItemStacks.OPTIC_CABLE.item(), NetworksSlimefunItemStacks.OPTIC_GLASS.item()
				}
		).register(networks);
	}
}
