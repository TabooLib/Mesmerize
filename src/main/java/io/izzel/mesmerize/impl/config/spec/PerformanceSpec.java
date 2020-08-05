package io.izzel.mesmerize.impl.config.spec;

public class PerformanceSpec {

    private long entityStatsCacheMs = 50L;

    private int maxTracingDistance = 128;

    private boolean tracingVisibleCheck = true;

    private double maxTracingAngle = 30D;

    public long entityStatsCacheMs() {
        return entityStatsCacheMs;
    }

    public void setEntityStatsCacheMs(long entityStatsCacheMs) {
        this.entityStatsCacheMs = entityStatsCacheMs;
    }

    public int maxTracingDistance() {
        return maxTracingDistance;
    }

    public void setMaxTracingDistance(int maxTracingDistance) {
        this.maxTracingDistance = maxTracingDistance;
    }

    public boolean tracingVisibleCheck() {
        return tracingVisibleCheck;
    }

    public void setTracingVisibleCheck(boolean tracingVisibleCheck) {
        this.tracingVisibleCheck = tracingVisibleCheck;
    }

    public double maxTracingAngle() {
        return maxTracingAngle;
    }

    public void setMaxTracingAngle(double maxTracingAngle) {
        this.maxTracingAngle = maxTracingAngle;
    }
}
