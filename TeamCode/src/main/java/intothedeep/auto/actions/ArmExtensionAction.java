package intothedeep.auto.actions;

import intothedeep.capabilities.ArmExtensionCapabilities;

import t10.auto.AutoAction;

public class ArmExtensionAction implements AutoAction {
	private final ArmExtensionCapabilities armExtensionCapabilities;
	private final int position;

	public ArmExtensionAction(ArmExtensionCapabilities armExtensionCapabilities, int position) {
		this.armExtensionCapabilities = armExtensionCapabilities;
		this.position = position;
	}

	@Override
	public void init() {
		this.armExtensionCapabilities.setTargetPosition(this.position);
	}

	@Override
	public void loop() {
	}

	@Override
	public boolean isComplete() {
		return this.armExtensionCapabilities.isAtTargetPosition();
	}
}
