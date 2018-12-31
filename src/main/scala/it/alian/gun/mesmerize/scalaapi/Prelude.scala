package it.alian.gun.mesmerize.scalaapi

import com.ilummc.tlib.resources.TLocale.Logger
import it.alian.gun.mesmerize.MesmerizeDelegate
import it.alian.gun.mesmerize.MesmerizeDelegate._
import me.skymc.taboolib.common.configuration.TConfiguration
import org.bukkit.Bukkit
import org.bukkit.event._
import org.bukkit.plugin.{EventExecutor, Plugin}

object Prelude extends Implicits {

  def listen[T <: Event](clazz: Class[T],
                         ignoreCancelled: Boolean = true,
                         priority: EventPriority = EventPriority.NORMAL)
                        (handler: T => Unit)
                        (implicit plugin: Plugin): Listener = {
    val listener = new SingleListener(handler)
    Bukkit.getPluginManager.registerEvent(clazz, listener, priority, listener, plugin, ignoreCancelled)
    listener
  }

  def listenOnce[T <: Event](clazz: Class[T],
                             ignoreCancelled: Boolean = true,
                             priority: EventPriority = EventPriority.NORMAL)
                            (handler: T => Unit)
                            (implicit plugin: Plugin): Unit = {
    val listener = new OnceListener(handler)
    Bukkit.getPluginManager.registerEvent(clazz, listener, priority, listener, plugin, ignoreCancelled)
  }

  def listen(listener: Listener)(implicit plugin: Plugin): Unit = Bukkit.getPluginManager.registerEvents(listener, plugin)

  def unlisten(): Unit = throw new UnlistenException

  def runTask(task: => Unit)(implicit plugin: Plugin): Unit = Task(task)

  def runTask(delay: Long)(task: => Unit)(implicit plugin: Plugin): Unit = Task(delay)(task)

  def runTask(init: Long, period: Long)(task: => Unit)(implicit plugin: Plugin): Unit = Task(init, period)(task)

  def runTaskAsync(task: => Unit)(implicit plugin: Plugin): Unit = AsyncTask(task)

  def runTaskAsync(delay: Long)(task: => Unit)(implicit plugin: Plugin): Unit = AsyncTask(delay)(task)

  def runTaskAsync(init: Long, period: Long)(task: => Unit)(implicit plugin: Plugin): Unit = AsyncTask(init, period)(task)

  def info(node: String, params: String*): Unit = Logger.info(node, params: _*)

  def error(node: String, params: String*): Unit = Logger.error(node, params: _*)

  def fine(node: String, params: String*): Unit = Logger.fine(node, params: _*)

  def debug(node: String, params: String*): Unit = Logger.verbose(node, params: _*)

  def config: TConfiguration = MesmerizeDelegate.conf

  def updateConfig(): Unit = conf = config.getValues(true)

  private[this] var conf = MesmerizeDelegate.conf.getValues(true)

  def config(node: String): String = conf.get(node).toString

  def config[T](node: String, default: T): T = conf.getOrDefault(node, default.asInstanceOf[AnyRef]).asInstanceOf[T]

  def config[T](node: String): T = conf.get(node).asInstanceOf[T]

}

private[this] class UnlistenException extends Exception

private[this] class SingleListener[T <: Event](handler: T => Any) extends Listener with EventExecutor {
  override def execute(listener: Listener, event: Event): Unit = {
    try handler(event.asInstanceOf[T]) catch {
      case _: UnlistenException => Prelude.runTask(HandlerList.unregisterAll(this))
      case e: Throwable => throw new EventException(e)
    }
  }
}

private[this] class OnceListener[T <: Event](handler: T => Any) extends Listener with EventExecutor {
  override def execute(listener: Listener, event: Event): Unit = {
    try handler(event.asInstanceOf[T]) catch {
      case e: Throwable => throw new EventException(e)
    } finally Prelude.runTask(HandlerList.unregisterAll(this))
  }
}