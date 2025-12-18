package dev.cworldstar.networks.network.barrel;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.sefiraat.networks.network.barrel.BarrelType;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;

public class NetworkIdentity extends BarrelIdentity {
	protected NetworkIdentity(Location location, ItemStack itemStack, int amount, BarrelType type) {
		super(location, itemStack, amount, type);
	}

	@Override
	public ItemStack requestItem(ItemRequest itemRequest) {
		return null;
	}

	@Override
	public void depositItemStack(ItemStack[] itemsToDeposit) {
		
	}

	@Override
	public int getInputSlot() {
		return 0;
	}

	@Override
	public int getOutputSlot() {
		return 0;
	}
}
