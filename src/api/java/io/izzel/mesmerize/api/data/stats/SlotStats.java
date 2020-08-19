package io.izzel.mesmerize.api.data.stats;

import com.google.common.collect.Lists;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.data.MapValue;
import io.izzel.mesmerize.api.data.MultiValue;
import io.izzel.mesmerize.api.data.complex.StatsSetValue;
import io.izzel.mesmerize.api.data.StringValue;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.Element;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SlotStats extends MapValue {

    public SlotStats(Map<String, Supplier<StatsValue<?>>> dataTypes) {
        super(dataTypes);
    }

    public List<String> getSlots() {
        MultiValue<String, StringValue> value = this.get("node");
        return Lists.transform(value.get(), StringValue::get);
    }

    public StatsHolder getStats() {
        StatsSetValue statsSetValue = this.get("stats");
        return statsSetValue.get();
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
