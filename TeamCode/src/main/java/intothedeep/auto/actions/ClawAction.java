package intothedeep.auto.actions;

import intothedeep.capabilities.ClawCapabilities;

import t10.auto.AutoAction;

public class ClawAction implements AutoAction {
	private final ClawCapabilities clawCapabilities;
	private final boolean isOpen;

	public ClawAction(ClawCapabilities clawCapabilities, boolean isOpen) {
		this.clawCapabilities = clawCapabilities;
		this.isOpen = isOpen;
	}

	@Override
	public void init() {
		this.clawCapabilities.setOpen(this.isOpen);
	}

	@Override
	public void loop() {
	}

	@Override
	public boolean isComplete() {
		return this.clawCapabilities.isAtTargetPosition();
	}
}
