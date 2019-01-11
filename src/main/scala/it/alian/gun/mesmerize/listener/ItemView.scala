package it.alian.gun.mesmerize.listener

import com.ilummc.tlib.resources.TLocale
import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.lore.LoreParser
import it.alian.gun.mesmerize.scalaapi.Prelude._
import me.skymc.taboolib.inventory.builder.{ItemBuilder, MenuBuilder}
import me.skymc.taboolib.playerdata.DataUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryCloseEvent}
import org.bukkit.event.{HandlerList, Listener}
import org.bukkit.inventory.ItemStack

import scala.collection.JavaConverters._
import scala.collection.mutable

object ItemView extends Listener {
  private val data = DataUtils.addPluginData("inlay", instance)

  runTaskAsync(20 * 60 * 10, 20 * 60 * 10) {
    DataUtils.saveAllCaches(instance)
  }

  def openView(item: ItemStack, player: Player): Unit = {
    val inventory = new MenuBuilder().rows(6).name(TLocale.asString("item-view", item.getDisplayName))
      .item(item, slot(1, 5))
      .item(
        new ItemBuilder(Material.DIAMOND).name(TLocale.asString("inlay.name"))
          .lore(TLocale.asString("inlay.desc")).build(),
        _ => openInlayView(item, player), slot(4, 5))
      .build()
    player.openInventory(inventory)
  }

  private val unavailable =
    try {
      new ItemBuilder(Material.valueOf("STAINED_GLASS_PANE"), 1, 12).name(TLocale.asString("inlay.unavailable-slot")).build()
    } catch {
      case _: Throwable =>
        new ItemBuilder(Material.valueOf("RED_STAINED_GLASS_PANE")).name(TLocale.asString("inlay.unavailable-slot")).build()
    }

  private val empty = new ItemStack(Material.AIR)

  def openInlayView(item: ItemStack, player: Player): Unit = {
    val info = LoreParser.parse(item)
    val max = info.num("maxInlay").toInt
    val list = item.getInlayItems.map(_.clone)
    list.foreach(item =>
      if (item.hasLore) item.setLore(TLocale.asString("inlay.click-unmount") +: item.getLore)
    )
    val rows = list.size / 9 + 2

    val builder = new MenuBuilder(true).rows(rows)
      .name(TLocale.asString("inlay.ui-name", item.getDisplayName, (max - list.size).toString, max.toString))
      .item(item, slot(1, 5)).item(unavailable, 0, 1, 2, 3, 5, 6, 7, 8)

    0 until ((rows - 1) * 9) foreach { idx =>
      val elem = list.applyOrElse(idx, (_: Int) => empty)
      builder.item(if (idx < max) elem else unavailable,
        // handle the un-inlaying action
        event => if (idx < max && event.getParentEvent.isShiftClick && event.getParentEvent.isLeftClick && !event.getClickItem.empty) {
          val (stack, take) = item.takeInlay(idx)
          player.getInventory.setItemInHand(stack)
          runTaskAsync(LoreParser.update(stack): Unit)
          runTask {
            player.closeInventory()
            player.getInventory.addItem(take)
            openInlayView(stack, player)
            player.locale("inlay.unmount-notify")
          }
        }, idx + 9)
    }
    val inv = builder.build()

    // handle the inlaying action
    val listener = listen(classOf[InventoryClickEvent]) { event =>
      if (event.getClickedInventory == player.getInventory) {
        if (canInlay(event.getCurrentItem, item) && event.isShiftClick && event.isLeftClick) {
          val stack = item.inlay(event.getCurrentItem)
          player.getInventory.setItemInHand(stack)
          player.getInventory.remove(event.getCurrentItem)
          runTaskAsync(LoreParser.update(stack): Unit)
          runTask {
            player.closeInventory()
            player.updateInventory()
            openInlayView(stack, player)
          }
        }
        event.setCancelled(true)
      }
    }

    // return to father menu
    listen(classOf[InventoryCloseEvent]) { event =>
      if (event.getInventory == inv) {
        HandlerList.unregisterAll(listener)
        unlisten()
      }
    }
    player.openInventory(inv)
  }

  def canInlay(inlaying: ItemStack, to: ItemStack): Boolean = inlaying.loreInfo().has("canInlay") && to.loreInfo().num("maxInlay").toInt > getInlayingItems(to).size

  def inlayInto(inlaying: ItemStack, to: ItemStack): ItemStack = {
    if (canInlay(inlaying, to))
      setInlayingItems(to, getInlayingItems(to) += inlaying)
    else to
  }

  /**
    * @param idx  index of the inlaying item
    * @param item the source item
    * @return (the source item, the taken off inlaying item)
    */
  def takeInlaying(idx: Int, item: ItemStack): (ItemStack, ItemStack) = {
    val stacks = getInlayingItems(item)
    val take = stacks.remove(idx)
    (setInlayingItems(item, stacks), take)
  }

  def setInlayingItems(item: ItemStack, items: Seq[ItemStack]): ItemStack = {
    val nbt = item.nbt
    val id = {
      val id = nbt.getLong("mesmerize_inlay")
      if (id == null) {
        val next = data.getLong("inlay_id", 0) + 1
        data.set("inlay_id", next)
        nbt.setLong("mesmerize_inlay", next)
        next
      } else id
    }
    data.set(s"inlay.$id", items.asJava)
    nbt
  }

  def getInlayingItems(item: ItemStack): mutable.Buffer[ItemStack] = {
    val nbt = item.nbt
    val id = {
      val id = nbt.getLong("mesmerize_inlay")
      if (id == null) return mutable.Buffer() else id
    }
    val list = {
      val list = data.getList(s"inlay.$id")
      if (list == null) mutable.Buffer[ItemStack]()
      else list.asInstanceOf[java.util.List[ItemStack]].asScala
    }
    list
  }

  def slot(line: Int, col: Int): Int = (line - 1) * 9 + col - 1

}
