package io.izzel.mesmerize.impl.util.visitor;

import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.cause.ContextKeys;
import io.izzel.mesmerize.api.cause.EventContext;
import io.izzel.mesmerize.api.service.StatsService;
import io.izzel.mesmerize.api.slot.StatsSlot;
import io.izzel.mesmerize.api.visitor.StatsVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsHolder;
import io.izzel.mesmerize.api.visitor.impl.AbstractStatsVisitor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class EntityReader extends AbstractStatsHolder {

    private final Entity entity;

    public EntityReader(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void accept(StatsVisitor visitor, VisitMode mode) {
        AbstractStatsVisitor wrapped = new AbstractStatsVisitor(visitor) {
            @Override
            public void visitEnd() {
            }
        };
        StatsService.instance().newStatsHolder(entity).accept(wrapped, mode);
        if (entity instanceof LivingEntity) {
            EventContext eventContext = CauseManager.instance().currentContext();
            for (StatsSlot slot : StatsService.instance().getRegistry().getSlots()) {
                Optional<ItemStack> optional = slot.get(((LivingEntity) entity));
                if (optional.isPresent() && optional.get().hasItemMeta()) {
                    ItemMeta itemMeta = optional.get().getItemMeta();
                    if (itemMeta != null) {
                        eventContext.add(ContextKeys.SLOT, slot);
                        StatsService.instance().newStatsHolder(itemMeta).accept(wrapped, mode);
                        eventContext.remove(ContextKeys.SLOT, slot);
                    }
                }
            }
        }
        visitor.visitEnd();
    }
}
