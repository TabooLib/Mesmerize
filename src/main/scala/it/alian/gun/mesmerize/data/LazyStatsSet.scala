package it.alian.gun.mesmerize.data

import java.util
import java.util.Optional

import io.izzel.mesmerize.api.visitor.{InfoKey, StatsVisitor}
import io.izzel.mesmerize.api.visitor.StatsVisitor
import io.izzel.mesmerize.api.{Stats, StatsSet}

class LazyStatsSet(private val id: String) extends StatsSet {

  private lazy val actualSet = Mesmerize.dataManager.get(id).get()

  override def keySet(): util.Set[Stats[_]] = actualSet.keySet()

  override def entrySet(): util.Set[util.Map.Entry[Stats[_], _]] = actualSet.entrySet()

  override def get[T](stats: Stats[T]): Optional[T] = actualSet.get(stats)

  override def containsKey(stats: Stats[_]): Boolean = actualSet.containsKey(stats)

  override def remove(stats: Stats[_]): Unit = actualSet.remove(stats)

  override def clear(): Unit = actualSet.clear()

  override def accept(visitor: StatsVisitor): Unit = actualSet.accept(visitor)

  override def visitInfo[T](info: InfoKey[T], infoValue: T): Unit = actualSet.visitInfo(info, infoValue)

  override def visitInfoEnd[T](info: InfoKey[T]): Unit = actualSet.visitInfoEnd(info)

  override def getInfo[T](key: InfoKey[T]): util.List[T] = actualSet.getInfo(key)

  override def visitBegin(): Unit = actualSet.visitBegin()

  override def visit[T](stats: Stats[T], value: T): Unit = actualSet.visit(stats, value)

  override def visitEnd(): Unit = actualSet.visitEnd()
}
