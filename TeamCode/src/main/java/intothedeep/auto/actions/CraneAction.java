package intothedeep.auto.actions;

import intothedeep.capabilities.CraneCapabilities;

import t10.auto.AutoAction;

public class CraneAction implements AutoAction {
    private final CraneCapabilities craneCapabilities;
    private final int position;

    public CraneAction(CraneCapabilities craneCapabilities, int position) {
        this.craneCapabilities = craneCapabilities;
        this.position = position;
    }

    @Override
    public void init() {
        this.craneCapabilities.setTargetPosition(this.position);
    }

    @Override
    public void loop() {
    }

    @Override
    public boolean isComplete() {
        return this.craneCapabilities.isAtTargetPosition();
    }
}
