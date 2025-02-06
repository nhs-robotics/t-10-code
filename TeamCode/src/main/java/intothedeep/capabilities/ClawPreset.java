package intothedeep.capabilities;

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
