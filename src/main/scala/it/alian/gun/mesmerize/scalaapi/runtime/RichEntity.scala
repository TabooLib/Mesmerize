package it.alian.gun.mesmerize.scalaapi.runtime

import it.alian.gun.mesmerize.compat.{AttackSpeed, EntityName, ShieldBlocking}
import it.alian.gun.mesmerize.lore.LoreInfo
import org.bukkit.entity.{Entity, Player}

class RichEntity(private val entity: Entity) {
  def ignoreShieldBlocking: Boolean = entity match {
    case x: Player => ShieldBlocking(x)
    case _ => false
  }

  def getDisplayName: String = EntityName.get(entity)

  /**
    * @return true if attacker can attack now
    */
  def checkAttackSpeed(lore: LoreInfo): Boolean = entity match {
    case x: Player => AttackSpeed.check(x, lore)
    case _ => true
  }

  def setAttackSpeed(v: Double): Unit = entity match {
    case x: Player => AttackSpeed.set(x, v)
    case _ =>
  }

  def updateAttackSpeed(): Unit = entity match {
    case x: Player => AttackSpeed.update(x)
    case _ =>
  }

}

object RichEntity {
  implicit def entity2rich(entity: Entity): RichEntity = new RichEntity(entity)

  implicit def rich2entity(richEntity: RichEntity): Entity = richEntity.entity
}
