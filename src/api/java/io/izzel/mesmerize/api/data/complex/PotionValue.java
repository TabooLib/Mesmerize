package io.izzel.mesmerize.api.data.complex;

import com.google.common.base.Preconditions;
import io.izzel.mesmerize.api.util.Coerce;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionValue extends AbstractValue<PotionEffect> {

    private String expr;
    private PotionEffect potionEffect;

    @Override
    public PotionEffect get() {
        return potionEffect;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        visitor.visitString(expr);
        visitor.visitEnd();
    }

    @Override
    public void visitString(String s) {
        this.expr = s;
    }

    @Override
    public void visitEnd() {
        Preconditions.checkNotNull(expr, "expr");
        String[] split = expr.split(",");
        Preconditions.checkArgument(split.length >= 3, "PotionEffect: type,duration,amplifier[,ambient,particles,icon]");
        PotionEffectType effectType = PotionEffectType.getByName(split[0]);
        Preconditions.checkNotNull(effectType, "Unknown effect type " + split[0]);
        int duration = Coerce.toInteger(split[1]);
        int amplifier = Coerce.toInteger(split[2]);
        boolean ambient = split.length <= 3 || Coerce.toBoolean(split[3]);
        boolean particle = split.length <= 4 || Coerce.toBoolean(split[4]);
        boolean icon = split.length <= 5 || Coerce.toBoolean(split[5]);
        this.potionEffect = new PotionEffect(effectType, duration, amplifier, ambient, particle, icon);
    }
}
