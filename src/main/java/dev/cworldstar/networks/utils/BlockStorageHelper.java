package dev.cworldstar.networks.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class BlockStorageHelper {
	
	public static String stringFromJSON(String bs, String pos) {
		return JsonParser.parseString(bs).getAsJsonObject().get(pos).getAsString();
	}
		
	public static int intFromJSON(String bs, String pos) {
		return JsonParser.parseString(bs).getAsJsonObject().get(pos).getAsInt();
	}
	
	public static boolean booleanFromJSON(String bs, String pos) {
		return JsonParser.parseString(bs).getAsJsonObject().get(pos).getAsBoolean();
	}
	
	public static double doubleFromJSON(String bs, String pos) {
		return JsonParser.parseString(bs).getAsJsonObject().get(pos).getAsDouble();
	}	
	public static JsonObject getBlockStorage(Block b) {
		return (JsonObject) JsonParser.parseString(BlockStorage.getBlockInfoAsJson(b));
	}
	
	public static JsonObject fromString(String s) {
		return (JsonObject) JsonParser.parseString(s).getAsJsonObject();
	}
	
	private static float floatFromJSON(String bs, String pos) {
		return JsonParser.parseString(bs).getAsJsonObject().get(pos).getAsFloat();
	}
	
	/** 
	 * @param {@link String} blockstorage
	 * @return A {@link Map} containing every key and value of the given blockstorage.
	 */
	public static Map<String, String> asMap(String blockstorage) {
		JsonObject bs = fromString(blockstorage);
		return bs.asMap().entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), value-> value.getValue().getAsString()));
	}

	public static void set(Location at, String pos, Object value) {
		if(value instanceof Vector) {
			addVectorToBlockStorage(pos, at.getBlock(), (Vector) value);
			return;
		}
		if(
				value instanceof BlockStorageSerializable
		) {
			Map<String, String> serialized = ((BlockStorageSerializable) value).serialize();
			for(Entry<String, String> entry : serialized.entrySet()) {
				BlockStorage.addBlockInfo(at, pos, entry.getKey() + "_" + entry.getValue());
			}
		}
		
		BlockStorage.addBlockInfo(at.getBlock(), pos, value.toString());
	}
	
	/**
	 * 
	 * This method is a generic method to grabbing stored BlockStorageSerializable items.
	 * It's probably better to hard code this, but in the case that you're lazy as hell this is 
	 * a pretty solid way to do it. It's also really expensive due to having to iterate through the entire blockstorage, 
	 * hence why specific grabbing functions are better. For an example of specific grabbing functions, see
	 * {@link #getSlimefunItemEntry(Block, String, ItemStack)}
	 * 
	 * 
	 * @author cworldstar
	 * 
	 * @param <T> Any class which implements BlockStorageSerializable.
	 * @param at The block to locate.
	 * @param pos The string position of the entry.
	 * @param clazz The class object of the serializable class.
	 * @return The object, or null if an error occured.
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends BlockStorageSerializable> T getSerializable(Block at, String pos, Class<T> clazz) {
		HashMap<String, String> args = new HashMap<String, String>();
		JsonObject blockStorage = getBlockStorage(at);
		for(String object : blockStorage.keySet()) {
			if(!object.equalsIgnoreCase(pos)) continue;
			String truncated = object.replace(pos, "");
			String[] split = truncated.split("_");
			String p1 = split[0];
			ArrayList<String> list = new ArrayList<String>();
			for(String s : split) {
				if(s.equalsIgnoreCase(p1)) continue;
				list.add(s);
			}
			String p2 = String.join("_", list);
			args.put(p1, p2);
		}
		try {
			Method deserialize = clazz.getDeclaredMethod("deserialize", Map.class);
			return (T) deserialize.invoke(deserialize, args);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void store(Location at, String pos, Object value) {
		set(at, pos, value);
	}
	
	public static void store(Block at, String pos, Object value) {
		set(at, pos, value);
	}
	
	public static void set(Block at, String pos, Object value) {
		set(at.getLocation(), pos, value);
	}

	public static Vector getVectorFromJSON(String bs, String pos) {
		JsonObject blockStorage = fromString(bs);
		if(blockStorage.get(pos+"X") ==null) {
			return null;
		}
		int x = blockStorage.get(pos+"X").getAsInt();
		int y = blockStorage.get(pos+"Y").getAsInt();
		int z = blockStorage.get(pos+"Z").getAsInt();
		return new Vector(x, y, z);
	}
	
	public static Location locationFromJSON(String bs, String pos) {
		JsonObject blockStorage = fromString(bs);
		if(blockStorage.get(pos+"X") == null) {
			return null;
		}
		World world = Bukkit.getWorld(blockStorage.get(pos+"_world").getAsString());
		int x = blockStorage.get(pos+"X").getAsInt();
		int y = blockStorage.get(pos+"Y").getAsInt();
		int z = blockStorage.get(pos+"Z").getAsInt();
		return new Location(world, x, y, z);
	}
	
	public static void addLocationToBlockStorage(String key, Block block, Location loc) {
		BlockStorage.addBlockInfo(block, key+"_world", loc.getWorld().getName().toString());
		BlockStorage.addBlockInfo(block, key+"Y", new JsonPrimitive(loc.getBlockY()).toString());
		BlockStorage.addBlockInfo(block, key+"X", new JsonPrimitive(loc.getBlockX()).toString());
		BlockStorage.addBlockInfo(block, key+"Z", new JsonPrimitive(loc.getBlockZ()).toString());
	}
	
	public static void addVectorToBlockStorage(String key, Block block, Vector vector) {
		BlockStorage.addBlockInfo(block, key+"Y", new JsonPrimitive(vector.getBlockY()).toString());
		BlockStorage.addBlockInfo(block, key+"X", new JsonPrimitive(vector.getBlockX()).toString());
		BlockStorage.addBlockInfo(block, key+"Z", new JsonPrimitive(vector.getBlockZ()).toString());
	}

	public static boolean getBoolean(Location l, String key) {
		return booleanFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}
	
	public static boolean getBoolean(Block l, String key) {
		return booleanFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}

	public static @NotNull String getString(@NotNull Location l, String key) {
		if(!exists(l.getBlock(), key)) return "";
		return stringFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}

	public static @NotNull String getString(@NotNull Block l, String key) {
		if(!exists(l, key)) return "";
		return stringFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}
	
	public static Location getLocation(@NotNull Block b, String key) {
		return locationFromJSON(BlockStorage.getBlockInfoAsJson(b), key);
	}
	
	public static int getInteger(@NotNull Location l, String key) {
		return intFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}
	
	public static double getDouble(@NotNull Block b, String key) {
		return doubleFromJSON(BlockStorage.getBlockInfoAsJson(b), key);
	}
	
	public static double getDouble(@NotNull Location b, String key) {
		return doubleFromJSON(BlockStorage.getBlockInfoAsJson(b), key);
	}
	
	public static World getWorld(@NotNull Block l, String key) {
		return Bukkit.getWorld(stringFromJSON(BlockStorage.getBlockInfoAsJson(l), key));
	}
	
	public static int getInteger(@NotNull Block l, String key) {
		return intFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}
	
	public static Vector getVector(@NotNull Location l, String key) {
		return getVectorFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}
	
	public static Vector getVector(@NotNull Block l, String key) {
		return getVectorFromJSON(BlockStorage.getBlockInfoAsJson(l), key);
	}

	public static float getFloat(Block at, String string) {
		return floatFromJSON(BlockStorage.getBlockInfoAsJson(at), string);
	}

	//TODO: change this to use BlockStorageSerializable instead of hard coding 
	public static @NotNull SlimefunItemEntry getSlimefunItemEntry(Block at, String where, String item) {
		String name;
		SlimefunItem sfitem = SlimefunItem.getById(item);
		if(sfitem != null) {
			name = sfitem.getId();
		} else {
			name = item;
		}
		String entryClass = getString(at, where + "_" + name + "_class");
		if(entryClass == null || !entryClass.equalsIgnoreCase(SlimefunItemEntry.class.getCanonicalName())) return SlimefunItemEntry.empty();
		int amount = getInteger(at, where + "_" + name  + "_amount");
		String slimefunItem = getString(at, where + "_" + name  + "_itemId");
		return new SlimefunItemEntry(slimefunItem, amount);
	}
	
	public static @NotNull SlimefunItemEntry getSlimefunItemEntry(Block at, String where, ItemStack item) {
		String name;
		SlimefunItem sfitem = SlimefunItem.getByItem(item);
		if(sfitem != null) {
			name = sfitem.getId();
		} else {
			name = item.getType().toString();
		}
		String entryClass = getString(at, where + "_" + name + "_class");
		if(entryClass == null || !entryClass.equalsIgnoreCase(SlimefunItemEntry.class.getCanonicalName())) return SlimefunItemEntry.empty();
		int amount = getInteger(at, where + "_" + name  + "_amount");
		String slimefunItem = getString(at, where + "_" + name  + "_itemId");
		return new SlimefunItemEntry(slimefunItem, amount);
	}
	
	public static void storeSlimefunEntry(Block at, String where, SlimefunItemEntry entry) {
		store(at, where, "SlimefunItemEntry");
		store(at, where + "_" + entry.getSlimefunItemId() + "_amount", entry.getAmount());
		store(at, where + "_" + entry.getSlimefunItemId() + "_class", SlimefunItemEntry.class.getCanonicalName());
		store(at, where + "_" + entry.getSlimefunItemId() + "_itemId", entry.getSlimefunItemId());
	}
	
	public static boolean exists(Block at, String key) {
		return fromString(BlockStorage.getBlockInfoAsJson(at)).has(key);
	}

	/**
	 * Returns an ordered list of SlimefunItemEntry.
	 * If the size is 0, there are no entries.
	 * @param core The block to check
	 * @param string Where the entry is located
	 * @return A list of item entries.
	 */
	public static @NotNull ArrayList<SlimefunItemEntry> getSlimefunItemEntries(Block core, String string) {
		ArrayList<SlimefunItemEntry> list = new ArrayList<SlimefunItemEntry>();
		JsonObject blockStorage = getBlockStorage(core);
		for(String object : blockStorage.keySet()) {
			if(!object.contains("_itemId")) continue;
			list.add(new SlimefunItemEntry(blockStorage.get(object).getAsString(), blockStorage.get(object.replace("_itemId", "_amount")).getAsLong()));
		}
		return list;
	}

	public static void updateSlimefunEntry(Block at, String where, String item, Consumer<SlimefunItemEntry> editor) {
		SlimefunItemEntry entry = getSlimefunItemEntry(at, where, item);
		editor.accept(entry);
		storeSlimefunEntry(at, where, entry);
	}

	public static List<Location> getLocations(Block at, String key) {
		// check if the key is a location list
		ArrayList<Location> list = new ArrayList<>();

		JsonObject blockStorage = getBlockStorage(at);
		for(String object : blockStorage.keySet()) {
			if(!object.contains("_world")) continue;
			object = object.replace("_world", "");
			list.add(new Location(Bukkit.getWorld(blockStorage.get(object + "_world").getAsString()), blockStorage.get(object + "X").getAsDouble(),blockStorage.get(object + "Y").getAsDouble(),blockStorage.get(object + "Z").getAsDouble()));
		}
		return list;
	}

	
	/**
	 * This method is used to determine if the given key is a list.
	 * @param string
	 * @return
	 */
	public static int size(Block at, String key) {
		JsonObject blockStorage = getBlockStorage(at);
		int size = 0;
		for(String object : blockStorage.keySet()) {
			if(object.contains(key + "_")) size += 1;
		}
		return size;
	}


	
}
