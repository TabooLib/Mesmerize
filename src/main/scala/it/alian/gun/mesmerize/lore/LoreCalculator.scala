package it.alian.gun.mesmerize.lore

import it.alian.gun.mesmerize.MesmerizeDelegate._
import it.alian.gun.mesmerize.scalaapi.Prelude._
import javax.script._
import jdk.nashorn.api.scripting.NashornScriptEngineFactory
import org.bukkit.entity.{LivingEntity, Player}

object LoreCalculator {
  private val impl = if (config("advanced.enableCustomAttackExpression", false)) new JavaScriptImpl else new DefaultImpl

  def finalDamage(baseDamage: Double, attack: LoreInfo, defense: LoreInfo, source: LivingEntity, entity: LivingEntity, bow: Boolean): Double = {
    var damage = attack.num("damage") max
      (if (entity.isInstanceOf[Player]) attack.num("playerDamage") else attack.num("entityDamage"))
    if (bow) damage = damage max attack.num("bowDamage")
    if (config("combatMessage.showOnCritical", true) && attack.num("criticalDamage").abs > 1e-6)
      runTaskAsync(source.sendMessage(config("combatMessage.onCritical", "").format(entity.getName, damage * attack.num("criticalDamage"))))
    damage += damage * attack.num("criticalDamage")
    var armor = defense.num("defense") max
      (if (source.isInstanceOf[Player]) defense.num("playerDefense") else defense.num("entityDefense"))
    if (bow) armor = armor max defense.num("bowDefense")
    impl.calculateDamage(baseDamage, damage, armor, attack, defense)
  }

  def finalReflect(baseDamage: Double, defense: LoreInfo): Double = {
    val ref = defense.num("reflect") max defense.num("meleeReflect") max defense.num("rangeReflect")
    impl.calculateReflect(baseDamage, ref, defense)
  }
}

private abstract class LoreCalculator {
  def calculateDamage(baseDamage: Double, damage: Double, armor: Double, attack: LoreInfo, defense: LoreInfo): Double

  def calculateReflect(baseDamage: Double, ref: Double, defense: LoreInfo): Double
}

private class DefaultImpl extends LoreCalculator {
  override def calculateDamage(baseDamage: Double, damage: Double, armor: Double, attack: LoreInfo, defense: LoreInfo): Double =
    Math.max(baseDamage + damage - armor, attack.num("realDamage"))

  override def calculateReflect(baseDamage: Double, ref: Double, defense: LoreInfo): Double = baseDamage * ref
}

private class JavaScriptImpl extends LoreCalculator {

  import scala.collection.JavaConverters._

  val sm = new ScriptEngineManager
  val factory: NashornScriptEngineFactory = (for {
    f <- sm.getEngineFactories.asScala
    if f.getEngineName.equalsIgnoreCase("Oracle Nashorn")
  } yield f).head.asInstanceOf[NashornScriptEngineFactory]
  require(factory != null)
  val scriptEngine: ScriptEngine = factory.getScriptEngine("-doe", "--global-per-engine")
  private val script = scriptEngine.asInstanceOf[Compilable].compile(config("advanced.customAttackExpression", ""))

  override def calculateDamage(baseDamage: Double, damage: Double, armor: Double, attack: LoreInfo, defense: LoreInfo): Double = {
    val bindings = new SimpleBindings
    bindings.put("base", baseDamage)
    bindings.put("damage", damage)
    bindings.put("armor", armor)
    bindings.put("attack", attack)
    bindings.put("defense", defense)
    try script.eval(bindings).toString.toDouble
    catch {
      case _: Exception => 0
    }
  }

  override def calculateReflect(baseDamage: Double, ref: Double, defense: LoreInfo): Double = baseDamage * ref
}