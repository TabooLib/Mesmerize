package io.izzel.mesmerize.api.data;

import io.izzel.mesmerize.api.display.DisplayPane;
import io.izzel.mesmerize.api.service.ElementFactory;
import io.izzel.mesmerize.api.visitor.ValueVisitor;
import io.izzel.mesmerize.api.visitor.VisitMode;
import io.izzel.mesmerize.api.visitor.impl.AbstractValue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.function.BiConsumer;

public class UUIDValue extends AbstractValue<UUID> {

    private UUID uuid;

    public UUIDValue() {
    }

    public UUIDValue(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void accept(ValueVisitor visitor, VisitMode mode) {
        visitor.visitString(uuid.toString());
        visitor.visitEnd();
    }

    @Override
    public UUID get() {
        return uuid;
    }

    @Override
    public void visitString(String s) {
        this.uuid = UUID.fromString(s);
    }

    public static BiConsumer<UUIDValue, DisplayPane> displayName(String key) {
        return (uuidValue, pane) -> {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuidValue.get());
            ElementFactory factory = ElementFactory.instance();
            pane.addElement(factory.createLocaleElement(key, player.getName()));
        };
    }
}
