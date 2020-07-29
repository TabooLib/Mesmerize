package it.alian.gun.mesmerize.data

import java.util.Optional

import io.izzel.mesmerize.api.StatsSet
import io.izzel.mesmerize.api.service.StatsManager
import it.alian.gun.mesmerize.Mesmerize
import org.bukkit.configuration.file.YamlConfiguration

import scala.jdk.OptionConverters._

class DataManager extends StatsManager {

  private var map: Map[String, StatsSet] = _

  def loadAll(): Unit = {
    val builder = Map.newBuilder[String, StatsSet]
    val path = Mesmerize.getDataFolder.toPath.resolve("data").toFile
    for (file <- path.listFiles(); name = file.getName; strip = name.substring(0, name.lastIndexOf('.'))) {
      val conf = YamlConfiguration.loadConfiguration(file)
      //for ((key, value: Map[String, AnyRef]) <- conf.getValues(false).asScala) {

      //}
    }
    map = builder.result()
  }

  override def get(id: String): Optional[StatsSet] = map.get(id).toJava
}
