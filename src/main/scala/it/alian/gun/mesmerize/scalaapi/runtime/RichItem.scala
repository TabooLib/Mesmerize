package it.alian.gun.mesmerize.scalaapi.runtime

import me.dpohvar.powernbt.PowerNBT
import me.dpohvar.powernbt.nbt.NBTTagCompound
import org.bukkit.inventory.ItemStack

import scala.collection.JavaConverters._

class RichItem(private val item: ItemStack) {

  def getAttackDamage: Int =
    try {
      val compound = PowerNBT.getApi.read(item)
      val modifiers = compound.getList("AttributeModifiers")
      for (obj <- modifiers.asScala if obj != null && obj.isInstanceOf[NBTTagCompound];
           modifier = obj.asInstanceOf[NBTTagCompound]) {
        if (modifier.containsKey("AttributeName") && modifier.getString("AttributeName") == "generic.attackDamage")
          modifier.getInt("Amount")
      }
      2
    } catch {
      case _: Throwable => 2
    }

  def hasLore: Boolean = item != null && item.hasItemMeta && item.getItemMeta.hasLore

  def getLore: Seq[String] = item.getItemMeta.getLore.asScala

}

object RichItem {
  implicit def item2rich(x: ItemStack): RichItem = new RichItem(x)

  implicit def rich2item(x: RichItem): ItemStack = x.item
}
