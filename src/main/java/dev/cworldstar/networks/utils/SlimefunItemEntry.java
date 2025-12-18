package dev.cworldstar.networks.utils;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.commons.lang.Validate;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

public class SlimefunItemEntry {
	
	@Deprecated
	public static enum SlimefunItemEntryType {
		MATERIAL,
		SLIMEFUN_ITEM
	}
	
	@Getter
	private String slimefunItemId;
	@Getter
	@Setter
	private long amount;
	
	public void addAmount(long amount) {
		this.amount += amount;
	}
	
	public static SlimefunItemEntry empty() {
		return new SlimefunItemEntry("AIR", 0);
	}
	
	public SlimefunItemEntry(String itemId, long amount) {
		slimefunItemId = itemId;
		this.amount = amount;
	}
	
	public SlimefunItemEntry(ItemStack item, long amount) {
		slimefunItemId = item.getType().toString();
		this.amount = amount;
	}
	
	public boolean isNull() {
		return (slimefunItemId == null || slimefunItemId == "AIR");
	}

	public boolean isEmpty() {
		return amount <= 0;
	}

	/**
	 * Creates a new SlimefunItemEntry with a given
	 * SlimefunItem and an amount.
	 * @param item
	 * @param amount
	 * @return
	 */
	public static SlimefunItemEntry of(@Nonnull final SlimefunItem item, long amount) {
		Validate.notNull(item, "You cannot create a SlimefunItemEntry of null.");
		return new SlimefunItemEntry(item.getId(), amount);
	}

	public boolean matches(ItemStack matcher) {
		Validate.notNull(matcher);
		SlimefunItem item = SlimefunItem.getByItem(matcher);
		if(item == null) return matcher.getType().toString().contentEquals(slimefunItemId);
		return item.getId().equalsIgnoreCase(slimefunItemId);
	}

	public static SlimefunItemEntry of(SlimefunItemStack item, int amount) {
		return new SlimefunItemEntry(item.getItemId(), amount);
	}
	
	public static SlimefunItemEntry of(ItemStack item, int amount) {
		return new SlimefunItemEntry(item, amount);
	}
	
	public static SlimefunItemEntry of(Material item, int amount) {
		return new SlimefunItemEntry(item.toString(), amount);
	}
	
	@Nonnull
	public ItemStack toDisplayItem() {
		ItemStack item = itemStack();
		item.editMeta(meta -> {
			Component displayName = meta.displayName();
			if(displayName == null) {
				displayName = item.effectiveName();
			}
			meta.displayName(displayName.append(FormatUtils.mm(" <gray>x <aqua>" + String.valueOf(amount))));
		});
		return item;
	}

	@Nonnull
	public ItemStack itemStack() {
		Bukkit.getLogger().log(Level.INFO, slimefunItemId);
		if(this.isNull()) {
			Bukkit.getLogger().log(Level.WARNING, "Malformed SlimefunItemId");
			return new ItemStack(Material.BARRIER);
		}
		SlimefunItem item = SlimefunItem.getById(slimefunItemId);
		if(item == null) {
			return new ItemStack(Material.valueOf(slimefunItemId));
		}
		return item.getItem();
	}

	public void subtract(long amount) {
		addAmount(-amount);
	}
}
