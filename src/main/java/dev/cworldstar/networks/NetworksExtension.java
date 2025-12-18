package dev.cworldstar.networks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.cworldstar.networks.network.AdvancedNetworkPusher;
import io.github.sefiraat.networks.utils.Theme;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.slimefun.NetworksItemGroups;

public class NetworksExtension {
	public static void setup(Networks networks) {
		new AdvancedNetworkPusher(
				NetworksItemGroups.MAIN,
				Theme.themedSlimefunItemStack(
					"NTW_ADVANCED_PUSHER", 
					new ItemStack(Material.MAGENTA_STAINED_GLASS), 
					Theme.MACHINE, 
			        "Advanced Network Pusher",
			        "The ANP (Advanced Network Pusher)",
			        "functions the exact same as the original,",
			        "with more slots to place filter items."
				),
				RecipeType.ENHANCED_CRAFTING_TABLE,
				new ItemStack[] {
						
				}
		).register(networks);
	}
}
