package intothedeep.auto.actions;

import intothedeep.capabilities.ArmRotationCapabilities;

import t10.auto.AutoAction;

public class ArmRotationAction implements AutoAction {
	private final ArmRotationCapabilities armRotationCapabilities;
	private final int position;

	public ArmRotationAction(ArmRotationCapabilities armRotationCapabilities, int position) {
		this.armRotationCapabilities = armRotationCapabilities;
		this.position = position;
	}

	@Override
	public void init() {
		this.armRotationCapabilities.setTargetPosition(this.position);
	}

	@Override
	public void loop() {
	}

	@Override
	public boolean isComplete() {
		return this.armRotationCapabilities.isAtTargetPosition();
	}
}
