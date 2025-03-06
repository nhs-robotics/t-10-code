package intothedeep.capabilities;

import intothedeep.Constants;
import intothedeep.SnowballConfig;

import t10.Loop;
import t10.auto.AutoAction;

public class ClawCapabilities implements Loop {
	private static final double CLAW_OPEN_POSITION = -0.9;
	private static final double CLAW_CLOSED_POSITION = 0.65;
	private final SnowballConfig config;
	private boolean isAbsoluteMode = false;
	private boolean isOpen = false;
	private double positionRotate = 0;
	private boolean isRotateDirty = true;
	private boolean isGripDirty = true;
	private long operationCompletionTime;
	private final int maxExtensionPosition = -1230;

	public ClawCapabilities(SnowballConfig c) {
		this.config = c;
	}

	@Override
	public void loop() {
		if(config.armExtension.encoder.getCurrentTicks() < maxExtensionPosition) {
			if (this.isAbsoluteMode) {
				this.config.clawRotate.setPosition(calculateAbsolutePosition(this.positionRotate));
			} else if (this.isRotateDirty) {
				this.config.clawRotate.setPosition(this.positionRotate);
				this.isRotateDirty = false;
			}
		}
		else if(this.isAbsoluteMode) {
			if(calculateAbsolutePosition(positionRotate) < 0.7) {
				this.config.clawRotate.setPosition(calculateAbsolutePosition(this.positionRotate));
			}
			else {
				this.config.clawRotate.setPosition(0.7);
			}
		}
		else {
			if(positionRotate < 0.7) {
				this.config.clawRotate.setPosition(positionRotate);
			}
			else {
				this.config.clawRotate.setPosition(0.7);
			}
		}

		if (this.isGripDirty) {
			this.config.clawGrip.setPosition(this.isOpen ? CLAW_OPEN_POSITION : CLAW_CLOSED_POSITION);
			this.isGripDirty = false;
		}
	}

	/**
	 * @param position The desired position to stay locked to. This position is the position you desire when the arm is zeroed.
	 */
	private double calculateAbsolutePosition(double position) {
		// There are 90 degrees between the FORWARD and UP positions. If we divide by 90 degrees, we get the change in position
		// per degree of the servo.
		final double SERVO_DELTA_PER_DEGREE = (ClawPreset.UP.servoRotatePosition - ClawPreset.FORWARD.servoRotatePosition) / 90d;

		// Because we zero the armRotation motor, the current position will be in ticks from the zeroed position.
		// We know that there are LIFT_MOTOR_TICK_COUNT ticks in a full, 360-degree rotation.
		// Therefore, the current angle of the arm from the zero position is the below.
		double currentAngleArmDegrees = (this.config.armRotation.motor.getCurrentPosition() / Constants.TickCounts.LIFT_MOTOR_TICK_COUNT) * 360d;

		// Linear interpolation between upward position and forward position, determined by correcting angle.
		// currentAngleArmDegrees / SERVO_DELTA_PER_DEGREE is in the following units:
		// (degrees) * (position delta / 1 degree)
		// The degrees cancel out, and we're left with the position delta.
		return position - currentAngleArmDegrees * SERVO_DELTA_PER_DEGREE;
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public void toggleClaw() {
		this.setOpen(!this.isOpen);
	}

	public void setAbsoluteMode(boolean absoluteMode) {
		this.isAbsoluteMode = absoluteMode;
	}

	public void setOpen(boolean isOpen) {
		if (isOpen == this.isOpen) {
			return;
		}

		this.isOpen = isOpen;
		this.isGripDirty = true;
		this.operationCompletionTime = System.currentTimeMillis() + 350;
	}

	public void setRotation(double position) {
		if (this.positionRotate == position) {
			return;
		}
		setOpen(false);
		this.positionRotate = position;
		this.isRotateDirty = true;
		this.operationCompletionTime = System.currentTimeMillis() + 750;
	}

	public void setPreset(ClawPreset preset, boolean isAbsoluteMode) {
		this.setOpen(false);
		this.isAbsoluteMode = isAbsoluteMode;
		this.setRotation(preset.servoRotatePosition);
	}

	public void setPreset(ClawPreset preset, boolean isOpen, boolean isAbsoluteMode) {
		this.setOpen(isOpen);
		this.isAbsoluteMode = isAbsoluteMode;
		this.setRotation(preset.servoRotatePosition);
	}

	public boolean isAtTargetPosition() {
		return System.currentTimeMillis() > this.operationCompletionTime;
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
			this.clawCapabilities.setPreset(this.clawPreset, this.isOpen, this.isAbsoluteRotation);
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
		UP(0.98),
		FORWARD(0.67),
		DOWN(0.32);

		public final double servoRotatePosition;
		public static final int minExtensionWhenNeeded = -565;

		ClawPreset(double servoRotatePosition) {
			this.servoRotatePosition = servoRotatePosition;
		}
	}
}
