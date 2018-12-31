package it.alian.gun.mesmerize.scalaapi.runtime

import it.alian.gun.mesmerize.listener.ItemView
import it.alian.gun.mesmerize.lore.{LoreInfo, LoreParser}
import me.dpohvar.powernbt.PowerNBT
import me.dpohvar.powernbt.nbt.NBTTagCompound
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

import scala.collection.JavaConverters._

class RichItem(private val item: ItemStack) {

  def nbt: NbtItemBridge = new NbtItemBridge(item)

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

  def setLore(lore: Seq[String]): Unit = {
    val meta = item.getItemMeta
    meta.setLore(lore.asJava)
    item.setItemMeta(meta)
  }

  def getDisplayName: String = item.getItemMeta.getDisplayName

  def getInlayItems: Seq[ItemStack] = ItemView.getInlayingItems(item)

  def setInlayItems(items: Seq[ItemStack]): ItemStack = ItemView.setInlayingItems(item, items)

  def inlay(item: ItemStack): ItemStack = ItemView.inlayInto(item, this.item)

  def takeInlay(idx: Int): (ItemStack, ItemStack) = ItemView.takeInlaying(idx, item)

  def loreInfo(): LoreInfo = LoreParser.parse(item)

  def empty: Boolean = item == null || item.getType == Material.AIR

}

object RichItem {
  implicit def item2rich(x: ItemStack): RichItem = new RichItem(x)

  implicit def rich2item(x: RichItem): ItemStack = x.item
}
