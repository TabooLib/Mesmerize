package io.izzel.mesmerize.api.data.stats;

import com.google.common.collect.Lists;
import io.izzel.mesmerize.api.Stats;
import io.izzel.mesmerize.api.cause.ContextKeys;
import io.izzel.mesmerize.api.data.MapValue;
import io.izzel.mesmerize.api.data.MultiValue;
import io.izzel.mesmerize.api.data.complex.StatsSetValue;
import io.izzel.mesmerize.api.data.StringValue;
import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.display.Element;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PermissionStats extends MapValue {

    public PermissionStats(Map<String, Supplier<StatsValue<?>>> dataTypes) {
        super(dataTypes);
    }

    public List<String> getPermissionNodes() {
        MultiValue<String, StringValue> value = this.get("node");
        return Lists.transform(value.get(), StringValue::get);
    }

    public StatsHolder getStats() {
        StatsSetValue statsSetValue = this.get("stats");
        return statsSetValue.get();
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        if (mode == VisitMode.VALUE) {
            List<LivingEntity> list = visitor.context().get(ContextKeys.SOURCE);
            List<String> permissionNodes = this.getPermissionNodes();
            for (LivingEntity entity : list) {
                for (String node : permissionNodes) {
                    if (!entity.hasPermission(node)) {
                        visitor.visitStats().visitEnd();
                        visitor.visitEnd();
                        return;
                    }
                }
            }
            this.getStats().accept(visitor.visitStats(), mode);
            visitor.visitEnd();
        } else {
            super.accept(visitor, mode);
        }
    }

    @SuppressWarnings("deprecation")
    public static final Stats<Map<String, StatsValue<?>>> STATS =
        Stats.builder()
            .key(new NamespacedKey("mesmerize", "permission"))
            .supplying(
                MapValue.builder()
                    .put("node", MultiValue.builder()
                        .allowSingleNonListValue()
                        .supplying(StringValue::new)
                        .buildSupplier()
                    ).put("stats", StatsSetValue::new)
                    .buildSupplier(PermissionStats::new)
            )
            .displaying((permissionStats, pane) -> {
                DisplayPane subPane = pane.createSubPane();
                ElementFactory factory = ElementFactory.instance();
                Element nodeElement = factory.createLocaleElement("permission_name." + permissionStats.getPermissionNodes().get(0));
                subPane.addHeader(factory.createLocaleElement("stats.permission", nodeElement));
                factory.displayHolder(permissionStats.getStats(), subPane);
                pane.addElement(subPane);
            })
            .build();
}
