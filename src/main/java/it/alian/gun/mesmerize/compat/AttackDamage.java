package it.alian.gun.mesmerize.compat;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.nbt.NBTTagCompound;
import org.bukkit.inventory.ItemStack;

public class AttackDamage {

    public static double getAttackDamage(ItemStack itemStack) {
        try {
            NBTCompound compound = PowerNBT.getApi().read(itemStack);
            if (compound != null) {
                NBTList modifiers = compound.getList("AttributeModifiers");
                if (modifiers != null) {
                    for (Object object : modifiers) {
                        if (object != null && object instanceof NBTTagCompound) {
                            NBTTagCompound modifier = ((NBTTagCompound) object);
                            if (modifier.containsKey("AttributeName") && modifier.getString("AttributeName")
                                    .equals("generic.attackDamage")) {
                                return modifier.getInt("Amount");
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            return 2;
        }
        return 2;
    }

}
