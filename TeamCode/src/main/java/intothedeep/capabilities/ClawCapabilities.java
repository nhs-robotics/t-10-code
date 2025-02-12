package intothedeep.capabilities;

import intothedeep.Constants;
import intothedeep.SnowballConfig;

import t10.auto.AutoAction;

public class ClawCapabilities {
	static final double SERVO_MAX_ERROR = 0.05;
	static final double CLAW_OPEN_POSITION = 0.05;
	static final double CLAW_CLOSED_POSITION = 0.55;
	private static final long MS_DURATION_COMPLETE_SERVO_ACTION = 250;

	private final SnowballConfig config;
	private boolean isOpen, isAbsolute;
	private ClawPreset clawPreset;
	private long timeCompletePresetMovement;
	boolean isDone = false;

	public ClawCapabilities(SnowballConfig c) {
		this.config = c;
		this.setPreset(ClawPreset.DOWN, false);
	}

	public void loop() {
		if(this.timeCompletePresetMovement < System.currentTimeMillis()) {
			if (isAbsolute) {
				setAbsolutePreset(clawPreset);
			} else {
				this.setPreset(this.clawPreset, false);
			}
		}
	}

	public void setOpen(boolean open) {
		this.isOpen = open;

		if (open) {
			this.config.clawGrip.setPosition(CLAW_OPEN_POSITION);
		} else {
			this.config.clawGrip.setPosition(CLAW_CLOSED_POSITION);
		}
	}

	public void setAbsoluteMode(boolean state) {
		isAbsolute = state;
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public ClawPreset clawPreset() {
		return this.clawPreset;
	}

	public void toggleClaw() {
		this.setOpen(!this.isOpen);
	}

	public void setPreset(ClawPreset preset, boolean absoluteRotation) {
		this.clawPreset = preset;

		if (!preset.needsToBeExtended || config.armExtension.motor.getCurrentPosition() < preset.minExtensionWhenNeeded) {
			if (absoluteRotation) {
				isAbsolute = true;
			} else {
				isAbsolute = false;
			}

			if (this.isOpen || config.clawTwist.getPosition() != 1) {
				this.setOpen(false);
				this.config.clawTwist.setPosition(1);
				this.timeCompletePresetMovement = System.currentTimeMillis() + ClawCapabilities.MS_DURATION_COMPLETE_SERVO_ACTION;
			}
			else {
				config.clawRotate.setPosition(preset.servoRotatePosition);
				isDone = true;
			}
		}
	}

	/*
	This isn't being currently used, but it would orient the claw to always face a  given position
	relative to the field, e.g. always down to pick up blocks.
*/
	private void setAbsolutePreset(ClawPreset targetPreset) {
		double currentAngleArm = (config.armRotation.motor.getCurrentPosition() / Constants.TickCounts.LIFT_MOTOR_TICK_COUNT) * 360;
		double servoDistPerDegree = (ClawPreset.UP.servoRotatePosition - ClawPreset.FORWARD.servoRotatePosition) / 90;
		//Linear interpolation between upward position and forward position, determined by correcting angle.
		double newClawRotation = targetPreset.servoRotatePosition - currentAngleArm * servoDistPerDegree;
		config.clawRotate.setPosition(newClawRotation);
	}


	public void initializePosition() {
		this.setOpen(false);
		this.setPreset(ClawPreset.FORWARD, false);
	}

	public boolean isAtTargetPosition() {
//		double gripTarget = this.isOpen ? CLAW_OPEN_POSITION : CLAW_CLOSED_POSITION;
//		double rotateTarget = this.clawPreset.servoRotatePosition;
//		double twistTarget = this.clawPreset.defaultTwistPosition;
//
//		return Math.abs(gripTarget - this.config.clawGrip.getPosition()) < SERVO_MAX_ERROR &&
//				Math.abs(rotateTarget - this.config.clawRotate.getPosition()) < SERVO_MAX_ERROR &&
//				Math.abs(twistTarget - this.config.clawTwist.getPosition()) < SERVO_MAX_ERROR;

		return isDone;
	}

	public static class ClawAction implements AutoAction {
		private final ClawCapabilities clawCapabilities;
		private final ClawPreset clawPreset;
		private final boolean isOpen;
		private final boolean isAbsoluteRotation;

		public ClawAction(ClawCapabilities clawCapabilities, ClawPreset clawPreset, boolean isOpen, boolean isAbsoluteRotation) {
			this.clawCapabilities = clawCapabilities;
			this.clawPreset = clawPreset;
			this.isOpen = isOpen;
			this.isAbsoluteRotation = isAbsoluteRotation;
		}

		@Override
		public void init() {
			this.clawCapabilities.setOpen(this.isOpen);

			if (this.clawPreset != null) {
				this.clawCapabilities.setPreset(this.clawPreset, this.isAbsoluteRotation);
			}
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
	//Vertical: 0.67getPosition

	//Min extension when needs to be extended: -565
	//Min rotation past which must be at default: 0.65

	public enum ClawPreset {
		UP(1, true),
		FORWARD(0.72, true),
		DOWN(0.36, false);

		public final double servoRotatePosition;
		public final double defaultTwistPosition = 1;
		public final boolean needsToBeExtended;
		public final int minExtensionWhenNeeded = -565;
		public final double maxRotationNotDefaultTwist = 0.65;

		ClawPreset(double servoRotatePosition, boolean needsToBeExtended) {
			this.servoRotatePosition = servoRotatePosition;
			this.needsToBeExtended = needsToBeExtended;
		}
	}
}
