package it.alian.gun.mesmerize.data

import java.util.{Optional, function}

import com.google.common.reflect.TypeToken
import io.izzel.mesmerize.api.visitor.StatsVisitor
import io.izzel.mesmerize.api.service.StatsRegistry
import io.izzel.mesmerize.api.{Stats, StatsSerializer}

class SimpleRegistry extends StatsRegistry {

  private val stats: mutable.Map[String, Stats[_]] = mutable.Map()
  private val serializers: mutable.Map[TypeToken[_], StatsSerializer[_]] = mutable.Map()
  var visitorFactory: StatsVisitor => StatsVisitor = a => a

  override def registerStats(stats: Stats[_]): Unit = {
    this.stats(stats.getId) = stats
  }

  override def registerSerializer[T](token: TypeToken[T], serializer: StatsSerializer[T]): Unit = {
    serializers(token) = serializer
  }

  override def registerVisitorFactory(transformer: function.Function[StatsVisitor, StatsVisitor]): Unit = {
    visitorFactory = visitorFactory.compose(transformer(_))
  }

  override def getStat(id: String): Optional[Stats[_]] = ???
}
