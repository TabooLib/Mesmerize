package io.izzel.mesmerize.impl.util;

import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.slot.StatsSlot;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsHolder;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class EntityReader extends AbstractStatsHolder {

    private final LivingEntity entity;

    public EntityReader(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void accept(StatsVisitor visitor, VisitMode mode) {
        AbstractStatsVisitor wrapped = new AbstractStatsVisitor(visitor) {
            @Override
            public void visitEnd() {
            }
        };
        StatsService.instance().newPersistentHolder(entity).accept(wrapped, mode);
        for (StatsSlot slot : StatsService.instance().getRegistry().getSlots()) {
            Optional<ItemStack> optional = slot.get(entity);
            if (optional.isPresent() && optional.get().hasItemMeta()) {
                ItemMeta itemMeta = optional.get().getItemMeta();
                if (itemMeta != null) {
                    StatsService.instance().newPersistentHolder(itemMeta).accept(wrapped, mode);
                }
            }
        }
        visitor.visitEnd();
    }
}
