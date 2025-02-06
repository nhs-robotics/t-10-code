package intothedeep.capabilities;

import intothedeep.SnowballConfig;

import t10.motion.hardware.Motor;

import t10.auto.AutoAction;

public class ClawCapabilities {
	static final double SERVO_MAX_ERROR = 0.05;
	static final double CLAW_OPEN_POSITION = 0.05;
	static final double CLAW_CLOSED_POSITION = 0.55;

	private final SnowballConfig config;
	private boolean isOpen;
	private boolean isAbsoluteRotation = false;
	private ClawPreset clawPreset;

	public ClawCapabilities(SnowballConfig c) {
		this.config = c;
	}

	public void setOpen(boolean open) {
		this.isOpen = open;

		if (open) {
			this.config.clawGrip.setPosition(CLAW_OPEN_POSITION);
		} else {
			this.config.clawGrip.setPosition(CLAW_CLOSED_POSITION);
		}
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public void toggleClaw() {
		this.setOpen(!this.isOpen);
	}

	public void setPresetRelative(ClawPreset preset) {
		this.clawPreset = preset;
		this.config.clawTwist.setPosition(preset.servoTwistPosition);
		this.config.clawRotate.setPosition(preset.servoRotatePosition);
		this.isAbsoluteRotation = false;
	}

	private void setPresetAbsolute(double targetAngleClaw, Motor arm) {
		// TODO: Help reimplement this
		// double currentAngleArm = arm.motor.getCurrentPosition() / Constants.TickCounts.LIFT_MOTOR_TICK_COUNT;
		// currentAngleArm *= 4;
		// Linear interpolation between upward position and forward position, determined by correcting angle.
		// double newClawRotation = (targetAngleClaw - currentAngleArm) * (ROTATE_UPWARD_POSITION - ROTATE_FORWARD_POSITION) + ROTATE_FORWARD_POSITION;
		// setRotationRelative(newClawRotation);
		this.isAbsoluteRotation = true;
	}

	public void initializePosition() {
		this.setOpen(false);
		this.setPresetRelative(ClawPreset.FORWARD);
	}

	public boolean isAtTargetPosition() {
		double gripTarget = this.isOpen ? CLAW_OPEN_POSITION : CLAW_CLOSED_POSITION;
		double rotateTarget = this.clawPreset.servoRotatePosition;
		double twistTarget = this.clawPreset.servoTwistPosition;

		return Math.abs(gripTarget - this.config.clawGrip.getPosition()) < SERVO_MAX_ERROR &&
				Math.abs(rotateTarget - this.config.clawRotate.getPosition()) < SERVO_MAX_ERROR &&
				Math.abs(twistTarget - this.config.clawTwist.getPosition()) < SERVO_MAX_ERROR;
	}

	public void loop() {
		if (isAbsoluteRotation) {
			setPresetAbsolute(0,null); // TODO reimplement
		}
	}

	public static class ClawAction implements AutoAction {
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
	//All the way back: 0.03
	// All the way up: 1.00
	//Forward: 0.72
	//Down: 0.36


	//Mandible_Servo_Out: 0.29
	//Mandible_Servo_In: 1.00
	//Vertical: 0.67

	public enum ClawPreset {
		UP(1, 1),
		FORWARD(0.72, 1),
		DOWN(0.36, 1);

		public final double servoRotatePosition;
		public final double servoTwistPosition;

		ClawPreset(double servoRotatePosition, double servoTwistPosition) {
			this.servoRotatePosition = servoRotatePosition;
			this.servoTwistPosition = servoTwistPosition;
		}
	}
}
