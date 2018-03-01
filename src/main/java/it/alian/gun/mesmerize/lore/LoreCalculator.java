package it.alian.gun.mesmerize.lore;

import it.alian.gun.mesmerize.MConfig;
import it.alian.gun.mesmerize.MLocale;
import it.alian.gun.mesmerize.compat.EntityName;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.script.*;
import java.util.Objects;

public abstract class LoreCalculator {

    private static LoreCalculator impl;

    public abstract double calculateDamage(double baseDamage, double damage, double armor, LoreInfo attack, LoreInfo defense);

    public abstract double calculateReflect(double baseDamage, double ref, LoreInfo defense);

    public static double finalDamage(double baseDamage, LoreInfo attack, LoreInfo defense, LivingEntity source, LivingEntity entity, boolean bow) {
        double damage;
        if (entity instanceof Player)
            damage = attack.damage > attack.playerDamage ? attack.damage : attack.playerDamage;
        else
            damage = attack.damage > attack.entityDamage ? attack.damage : attack.entityDamage;
        if (bow)
            damage = damage > attack.bowDamage ? damage : attack.bowDamage;
        if (MConfig.CombatMessage.showOnCritical && Math.abs(attack.criticalDamage) > 1E-6) {
            source.sendMessage(String.format(MConfig.CombatMessage.onCritical, EntityName.get(entity), damage * attack.criticalDamage));
        }
        damage += damage * attack.criticalDamage;
        double armor;
        if (source instanceof Player)
            armor = defense.defense > defense.playerDefense ? defense.defense : defense.playerDefense;
        else
            armor = defense.defense > defense.entityDefense ? defense.defense : defense.entityDefense;
        armor = armor > defense.bowDefense ? armor : defense.bowDefense;
        return impl.calculateDamage(baseDamage, damage, armor, attack, defense);
    }

    public static double finalReflect(double baseDamage, LoreInfo defense) {
        double ref = defense.reflect > defense.meleeReflect ? defense.reflect : defense.meleeReflect;
        ref = ref > defense.rangeReflect ? ref : defense.rangeReflect;
        return impl.calculateReflect(baseDamage, ref, defense);
    }

    public static void init() {
        impl = new DefaultImpl();
        if (MConfig.Advanced.enableCustomAttackExpression) {
            try {
                impl = new JavaScriptImpl();
                MLocale.GENERAL_CUSTOM_SCRIPT_LOAD.console();
            } catch (Exception ignored) {
                MLocale.ERROR_LOADING_CUSTOM_SCRIPT.console();
                impl = new DefaultImpl();
            }
        }
    }

    public static class JavaScriptImpl extends LoreCalculator {

        private CompiledScript script;

        private JavaScriptImpl() throws Exception {
            ScriptEngineManager sm = new ScriptEngineManager();
            NashornScriptEngineFactory factory = null;
            for (ScriptEngineFactory f : sm.getEngineFactories()) {
                if (f.getEngineName().equalsIgnoreCase("Oracle Nashorn")) {
                    factory = (NashornScriptEngineFactory) f;
                    break;
                }
            }
            String[] stringArray = new String[]{"-doe", "--global-per-engine"};
            ScriptEngine scriptEngine = Objects.requireNonNull(factory).getScriptEngine(stringArray);
            script = ((Compilable) scriptEngine).compile(MConfig.Advanced.customAttackExpression);
        }

        @Override
        public double calculateDamage(double baseDamage, double damage, double armor, LoreInfo attack, LoreInfo defense) {
            SimpleBindings bindings = new SimpleBindings();
            Double d = 0d;
            bindings.put("base", baseDamage);
            bindings.put("damage", damage);
            bindings.put("armor", armor);
            bindings.put("attack", attack);
            bindings.put("defense", defense);
            try {
                d = Double.parseDouble(String.valueOf(script.eval(bindings)));
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            return d;
        }

        @Override
        public double calculateReflect(double baseDamage, double ref, LoreInfo defense) {
            return baseDamage * ref;
        }
    }

    public static class DefaultImpl extends LoreCalculator {

        @Override
        public double calculateDamage(double baseDamage, double damage, double armor, LoreInfo attack, LoreInfo defense) {
            return Math.max(baseDamage + damage - armor, attack.realDamage);
        }

        @Override
        public double calculateReflect(double baseDamage, double ref, LoreInfo defense) {
            return baseDamage * ref;
        }
    }

}
