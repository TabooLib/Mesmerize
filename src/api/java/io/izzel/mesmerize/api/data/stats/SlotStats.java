package io.izzel.mesmerize.api.data.stats;

import com.google.common.collect.Lists;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.cause.ContextKeys;
import io.izzel.mesmerize.api.data.MapValue;
import io.izzel.mesmerize.api.data.MultiValue;
import io.izzel.mesmerize.api.data.StringValue;
import io.izzel.mesmerize.api.data.complex.StatsSetValue;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.Element;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.slot.StatsSlot;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SlotStats extends MapValue {

    public SlotStats(Map<String, Supplier<StatsValue<?>>> dataTypes) {
        super(dataTypes);
    }

    public List<String> getSlots() {
        MultiValue<String, StringValue> value = this.get("slot");
        return Lists.transform(value.get(), StringValue::get);
    }

    public StatsHolder getStats() {
        StatsSetValue statsSetValue = this.get("stats");
        return statsSetValue.get();
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (mode == VisitMode.VALUE) {
            List<StatsSlot> list = visitor.context().get(ContextKeys.SLOT);
            List<String> restrictions = this.getSlots();
            boolean found = false;
            for (StatsSlot slot : list) {
                for (String restriction : restrictions) {
                    if (restriction.equals(slot.getId())) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (found) {
                this.getStats().accept(visitor.visitStats(), mode);
            } else {
                visitor.visitStats().visitEnd();
            }
            visitor.visitEnd();
        } else {
            super.accept(visitor, mode);
        }
    }

    @SuppressWarnings("deprecation")
    public static final Stats<Map<String, StatsValue<?>>> STATS =
        Stats.builder()
            .key(new NamespacedKey("mesmerize", "slot"))
            .supplying(MapValue.builder()
                .put("slot", MultiValue.builder()
                    .allowSingleNonListValue()
                    .supplying(StringValue::new)
                    .buildSupplier())
                .put("stats", StatsSetValue::new)
                .buildSupplier(SlotStats::new)
            )
            .displaying((slotStats, pane) -> {
                DisplayPane subPane = pane.createSubPane();
                ElementFactory factory = ElementFactory.instance();
                Element nodeElement = factory.createLocaleElement("slot_name." + slotStats.getSlots().get(0));
                subPane.addHeader(factory.createLocaleElement("stats.slot", nodeElement));
                factory.displayHolder(slotStats.getStats(), subPane);
                pane.addElement(subPane);
            })
            .build();
}
