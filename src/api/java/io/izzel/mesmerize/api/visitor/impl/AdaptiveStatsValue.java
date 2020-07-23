package io.izzel.mesmerize.api.visitor.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import io.izzel.mesmerize.api.visitor.StatsHolder;
import io.izzel.mesmerize.api.visitor.StatsValue;
import io.izzel.mesmerize.api.visitor.StatsValueVisitor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AdaptiveStatsValue implements StatsValue<Object> {

    private boolean empty = true;
    private ListMultimap<String, Object> map;
    private List<Object> list;
    private String lastKey;

    @Override
    public void visitKey(String key) {
        if (!empty) {
            if (list != null) {
                throw new IllegalStateException("List is initialized while visiting key " + key);
            } else {
                this.lastKey = key;
            }
        } else {
            this.empty = false;
            this.map = MultimapBuilder.linkedHashKeys().arrayListValues().build();
            this.lastKey = key;
        }
    }

    private void visit(Object o) {
        if (empty) {
            empty = false;
            list = new LinkedList<>();
            list.add(o);
        } else {
            if (map != null) {
                if (lastKey == null) {
                    throw new IllegalStateException("Visiting value " + o + " before visiting key");
                } else {
                    map.put(lastKey, o);
                }
            } else {
                list.add(o);
            }
        }
    }

    @Override
    public void visitBoolean(boolean b) {
        visit(b);
    }

    @Override
    public void visitInt(int i) {
        visit(i);
    }

    @Override
    public void visitLong(long l) {
        visit(l);
    }

    @Override
    public void visitFloat(float f) {
        visit(f);
    }

    @Override
    public void visitDouble(double d) {
        visit(d);
    }

    @Override
    public void visitString(String s) {
        visit(s);
    }

    @Override
    public void visitStatsHolder(StatsHolder holder) {
        visit(holder);
    }

    @Override
    public void visitEnd() {
        this.lastKey = null;
    }

    private void visit(StatsValueVisitor visitor, Object o) {
        if (o instanceof Boolean) {
            visitor.visitBoolean((Boolean) o);
        } else if (o instanceof Integer) {
            visitor.visitInt((Integer) o);
        } else if (o instanceof Long) {
            visitor.visitLong((Long) o);
        } else if (o instanceof Float) {
            visitor.visitFloat((Float) o);
        } else if (o instanceof Double) {
            visitor.visitDouble((Double) o);
        } else if (o instanceof String) {
            visitor.visitString(o.toString());
        } else if (o instanceof StatsHolder) {
            visitor.visitStatsHolder((StatsHolder) o);
        }
    }

    @Override
    public Object get() {
        return map == null ? list : map;
    }

    @Override
    public void accept(StatsValueVisitor visitor) {
        if (!empty) {
            if (list != null) {
                for (Object o : list) {
                    visit(visitor, o);
                }
            } else {
                for (Map.Entry<String, Collection<Object>> entry : map.asMap().entrySet()) {
                    visitor.visitKey(entry.getKey());
                    for (Object o : entry.getValue()) {
                        visit(visitor, o);
                    }
                }
            }
        }
        visitor.visitEnd();
    }
}
