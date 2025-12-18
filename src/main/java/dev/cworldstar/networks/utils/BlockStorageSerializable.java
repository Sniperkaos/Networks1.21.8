package dev.cworldstar.networks.utils;

import java.util.Map;

/**
 * This interface denotes that the implementing class
 * is able to be serialized in {@link BlockStorageHelper}.
 * @author cworldstar
 *
 */
public interface BlockStorageSerializable {
	public abstract Map<String, String> serialize();
	public abstract <T extends BlockStorageSerializable> T deserialize(Map<String, String> args);
}
