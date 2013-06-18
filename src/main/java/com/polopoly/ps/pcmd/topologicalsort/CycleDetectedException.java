package com.polopoly.ps.pcmd.topologicalsort;

public class CycleDetectedException extends Exception {
    private Vertex<?> detectedAt;

    public CycleDetectedException(Vertex<?> detectedAt) {
        this.detectedAt = detectedAt;
    }

    public Vertex<?> getDetectedAt() {
        return detectedAt;
    }

}
