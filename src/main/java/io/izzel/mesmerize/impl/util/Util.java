package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.taboolib.Version;
import io.izzel.taboolib.util.UNSAFE;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Util {

    private static final long RAW_OFFSET;
    private static final Map<Class<?>, Integer> NBT_TYPE;

    static {
        try {
            Class<?> cl = Class.forName("org.bukkit.craftbukkit." + Version.getBukkitVersion() + ".persistence.CraftPersistentDataContainer");
            Field field = cl.getDeclaredField("customDataTags");
            RAW_OFFSET = UNSAFE.objectFieldOffset(field);
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
        return (Map<String, ?>) UNSAFE.getObject(container, RAW_OFFSET);
    }

    public static int typeOfKey(PersistentDataContainer container, NamespacedKey key) {
        Object o = mapOfContainer(container).get(key.toString());
        if (o != null) {
            Integer integer = NBT_TYPE.get(o.getClass());
            return integer == null ? 0 : integer;
        }
        return 0;
    }

    @SuppressWarnings("ConstantConditions")
    public static void dump(PersistentDataContainer container, NamespacedKey key, ValueVisitor visitor) {
        switch (Util.typeOfKey(container, key)) {
            case 1:
                visitor.visitBoolean(container.get(key, PersistentDataType.BYTE) != 0);
                break;
            case 2:
                visitor.visitInt(container.get(key, PersistentDataType.SHORT));
                break;
            case 3:
                visitor.visitInt(container.get(key, PersistentDataType.INTEGER));
                break;
            case 4:
                visitor.visitLong(container.get(key, PersistentDataType.LONG));
                break;
            case 5:
                visitor.visitFloat(container.get(key, PersistentDataType.FLOAT));
                break;
            case 6:
                visitor.visitDouble(container.get(key, PersistentDataType.DOUBLE));
                break;
            case 8:
                visitor.visitString(container.get(key, PersistentDataType.STRING));
                break;
            case 10:
                new PersistentValueNode(container.get(key, PersistentDataType.TAG_CONTAINER)).accept(visitor);
                break;
        }
    }

    @SuppressWarnings("deprecation")
    public static NamespacedKey fromString(String str) {
        int i = str.indexOf(':');
        return new NamespacedKey(str.substring(0, i), str.substring(i + 1));
    }
}
