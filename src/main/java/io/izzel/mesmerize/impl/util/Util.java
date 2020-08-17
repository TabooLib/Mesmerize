package io.izzel.mesmerize.impl.util;

import io.izzel.taboolib.Version;
import io.izzel.taboolib.util.Ref;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Util {

    @SuppressWarnings("deprecation")
    public static final NamespacedKey ARRAY_LENGTH = new NamespacedKey("mesmerize", "array_length");
    @SuppressWarnings("deprecation")
    public static final NamespacedKey STATS_STORE = new NamespacedKey("mesmerize", "stats");
    @SuppressWarnings("deprecation")
    public static final NamespacedKey EXTERNAL_STORE = new NamespacedKey("mesmerize", "externals");
    private static final long RAW_OFFSET;
    private static final Map<Class<?>, Integer> NBT_TYPE;

    static {
        try {
            Class<?> cl = Class.forName("org.bukkit.craftbukkit." + Version.getBukkitVersion() + ".persistence.CraftPersistentDataContainer");
            Field field = cl.getDeclaredField("customDataTags");
            RAW_OFFSET = Ref.getUnsafe().objectFieldOffset(field);
            NBT_TYPE = new HashMap<>();
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagByte"), 1);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagShort"), 2);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagInt"), 3);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagLong"), 4);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagFloat"), 5);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagDouble"), 6);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagByteArray"), 7);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagString"), 8);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagCompound"), 10);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagIntArray"), 11);
            NBT_TYPE.put(Class.forName("net.minecraft.server." + Version.getBukkitVersion() + ".NBTTagLongArray"), 12);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, ?> mapOfContainer(PersistentDataContainer container) {
        return (Map<String, ?>) Ref.getUnsafe().getObject(container, RAW_OFFSET);
    }

    public static int typeOfKey(PersistentDataContainer container, NamespacedKey key) {
        Object o = mapOfContainer(container).get(key.toString());
        if (o != null) {
            Integer integer = NBT_TYPE.get(o.getClass());
            return integer == null ? 0 : integer;
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    public static NamespacedKey fromString(String str) {
        int i = str.indexOf(':');
        if (i == -1) {
            return NamespacedKey.minecraft(str);
        } else {
            return new NamespacedKey(str.substring(0, i), str.substring(i + 1));
        }
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        else return Math.min(value, max);
    }
}
