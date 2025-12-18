package dev.cworldstar.networks.network.barrel;

import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import io.github.sefiraat.networks.network.barrel.BarrelType;
import io.github.sefiraat.networks.network.stackcaches.BarrelIdentity;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.utils.StackUtils;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import lombok.Getter;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * Implementation of FluffyBarrels in Networks.
 * Bridges will work.<p>
 * In the future, {@link #depositItemStack(ItemStack)} MUST change the amount of the ItemStack to ZERO, or leave
 * amounts if something does not fit. To create a new {@link BarrelIdentity}, it must be put as an if statement under
 * {@link NetworkRoot}, otherwise it will not function.
 */
public class FluffyBarrel extends BarrelIdentity {

	@Getter
	private Barrel barrel;
	
	public static BlockMenu getMenu(Location loc) {
		return BlockStorage.getInventory(loc);
	}
	
	public static int getMenuSlotItems(Location loc) {
		final BlockMenu menu = getMenu(loc);
		int amount = 0;
		
		final ItemStack i1 = menu.getItemInSlot(24);
		if(i1 != null) amount += i1.getAmount();
		final ItemStack i2 = menu.getItemInSlot(25);
		if(i2 != null) amount += i2.getAmount();
		
		return amount;
	}
	
	public FluffyBarrel(Location location, ItemStack itemStack, int amount, BarrelType type, Barrel barrel) {
		super(location, itemStack, amount + getMenuSlotItems(location), BarrelType.FLUFFY);
		this.barrel = barrel;
	}

	public @Nullable ItemStack getItemStack(BlockMenu menu, int amount) {
		@Nonnull final ItemStack storedItem = barrel.getStoredItem(menu.getBlock());
		if(storedItem.getType().equals(Material.BARRIER)) return menu.getItemInSlot(24) != null ? menu.getItemInSlot(24) : menu.getItemInSlot(25);
		
		int barrelAmount = barrel.getStored(menu.getBlock());
		if(barrelAmount <= 0) {
			// edge case for when the barrel has nothing stored and the barrier doesn't exist yet.
			return menu.getItemInSlot(24) != null ? menu.getItemInSlot(24) : menu.getItemInSlot(25);
		}
		else if(barrelAmount < amount) {
			amount = barrelAmount;
			barrel.setStored(menu.getBlock(), 0);
		} else {
			barrel.setStored(menu.getBlock(), barrelAmount - amount);
		}

		return storedItem.asQuantity(amount);
	}
	
	@Nullable
	@Override
	public ItemStack requestItem(@Nonnull ItemRequest itemRequest) {
		final BlockMenu menu = BlockStorage.getInventory(this.getLocation());
		if(menu == null) return null;
		ItemStack stack = getItemStack(menu, itemRequest.getAmount());
		return stack;
	}

	@Override
	public void depositItemStack(ItemStack[] itemsToDeposit) {
		Block block = getLocation().getBlock();
		for(ItemStack item : itemsToDeposit) {
			if(StackUtils.itemsMatch(barrel.getStoredItem(block), item)) {
				// check if the barrel has room
				boolean hasRoom = barrel.getCapacity(block) - barrel.getStored(block) > item.getAmount();
				if(!hasRoom) return;
				barrel.setStored(block, barrel.getStored(block) + item.getAmount());
				item.setAmount(0);
			}
		}
	} 

	@Override
	public int getInputSlot() {
		return 19;
	}

	@Override
	public int getOutputSlot() {
		return 24;
	}

}
