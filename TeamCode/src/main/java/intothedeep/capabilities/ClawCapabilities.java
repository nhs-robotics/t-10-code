package intothedeep.capabilities;

import intothedeep.teleop.ClawTest;

import t10.auto.AutoAction;
import t10.motion.hardware.Motor;

public class ClawCapabilities {
	static final double SERVO_MAX_ERROR = 0.05;
	static final double CLAW_OPEN_POSITION = 0.05;
	static final double CLAW_CLOSED_POSITION = 0.55;

	private final ClawTest.Config config;
	private boolean isOpen;
	private ClawPreset clawPreset;

	public ClawCapabilities(ClawTest.Config c) {
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

	public void setPreset(ClawPreset preset) {
		this.clawPreset = preset;
		this.config.clawTwist.setPosition(preset.servoTwistPosition);
		this.config.clawRotate.setPosition(preset.servoRotatePosition);
	}

	private void setRotationAbsolute(double targetAngleClaw, Motor arm) {
		// TODO: Help reimplement this
		// double currentAngleArm = arm.motor.getCurrentPosition() / Constants.TickCounts.LIFT_MOTOR_TICK_COUNT;
		// currentAngleArm *= 4;
		// Linear interpolation between upward position and forward position, determined by correcting angle.
		// double newClawRotation = (targetAngleClaw - currentAngleArm) * (ROTATE_UPWARD_POSITION - ROTATE_FORWARD_POSITION) + ROTATE_FORWARD_POSITION;
		// setRotationRelative(newClawRotation);
	}

	public void initializePosition() {
		this.setOpen(false);
		this.setPreset(ClawPreset.PLACE_SPECIMEN);
	}

	public boolean isAtTargetPosition() {
		double gripTarget = this.isOpen ? CLAW_OPEN_POSITION : CLAW_CLOSED_POSITION;
		double rotateTarget = this.clawPreset.servoRotatePosition;
		double twistTarget = this.clawPreset.servoTwistPosition;

		return Math.abs(gripTarget - this.config.clawGrip.getPosition()) < SERVO_MAX_ERROR &&
				Math.abs(rotateTarget - this.config.clawRotate.getPosition()) < SERVO_MAX_ERROR &&
				Math.abs(twistTarget - this.config.clawTwist.getPosition()) < SERVO_MAX_ERROR;
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
}
