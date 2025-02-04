package intothedeep.capabilities;

//Up: 0.83
//Forward: 0.5
//Down: 0.16

public enum ClawPreset {
	UP(0.83, 0),
	FORWARD(0.5, 0),
	DOWN(0.16, 0);

	public final double servoRotatePosition;
	public final double servoTwistPosition;

	ClawPreset(double servoRotatePosition, double servoTwistPosition) {
		this.servoRotatePosition = servoRotatePosition;
		this.servoTwistPosition = servoTwistPosition;
	}
}
