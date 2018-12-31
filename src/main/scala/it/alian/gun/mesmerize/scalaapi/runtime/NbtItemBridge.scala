package it.alian.gun.mesmerize.scalaapi.runtime

import de.tr7zw.itemnbtapi.NBTItem
import org.bukkit.inventory.ItemStack

class NbtItemBridge(private val item: ItemStack) extends NBTItem(item) {
}

object NbtItemBridge {
  implicit def this2item(nbtItemBridge: NbtItemBridge): ItemStack = nbtItemBridge.getItem
}