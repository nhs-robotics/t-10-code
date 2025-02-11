package t10.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;
import intothedeep.Constants;

import t10.Loop;
import t10.gamepad.input.types.GButton;
import t10.gamepad.input.types.GJoystick;
import t10.gamepad.input.types.GTrigger;

/**
 * Declarative gamepad input framework.
 */
public class GController implements Loop {
	/**
	 * Access to the X button.
	 */
	public final GButton x;

	/**
	 * Access to the Y button.
	 */
	public final GButton y;

	/**
	 * Access to the A button.
	 */
	public final GButton a;

	/**
	 * Access to the B button.
	 */
	public final GButton b;

	/**
	 * Access to the right bumper.
	 */
	public final GButton rightBumper;

	/**
	 * Access to the left bumper.
	 */
	public final GButton leftBumper;

	/**
	 * Access to the D-Pad up button.
	 */
	public final GButton dpadUp;

	/**
	 * Access to the D-Pad down button.
	 */
	public final GButton dpadDown;

	/**
	 * Access to the D-Pad right button.
	 */
	public final GButton dpadRight;

	/**
	 * Access to the D-Pad left button.
	 */
	public final GButton dpadLeft;

	/**
	 * Access the button on the left joystick (you can press the joysticks down).
	 */
	public final GButton leftJoystickButton;

	/**
	 * Access the button on the right joystick (you can press the joysticks down).
	 */
	public final GButton rightJoystickButton;

	/**
	 * Access the movable left joystick.
	 */
	public final GJoystick leftJoystick;

	/**
	 * Access the movable right joystick.
	 */
	public final GJoystick rightJoystick;

	/**
	 * Access the right trigger.
	 */
	public final GTrigger rightTrigger;

	/**
	 * Access the left trigger.
	 */
	public final GTrigger leftTrigger;

	/**
	 * Allows a gamepad's inputs to be mapped declaratively.
	 *
	 * @param gamepad The gamepad to use from the op-mode.
	 */
	public GController(Gamepad gamepad) {
		this.x = new GButton(this, () -> gamepad.x);
		this.y = new GButton(this, () -> gamepad.y);
		this.a = new GButton(this, () -> gamepad.a);
		this.b = new GButton(this, () -> gamepad.b);

		this.rightBumper = new GButton(this, () -> gamepad.right_bumper);
		this.leftBumper = new GButton(this, () -> gamepad.left_bumper);

		this.dpadUp = new GButton(this, () -> gamepad.dpad_up);
		this.dpadDown = new GButton(this, () -> gamepad.dpad_down);
		this.dpadLeft = new GButton(this, () -> gamepad.dpad_left);
		this.dpadRight = new GButton(this, () -> gamepad.dpad_right);

		this.leftJoystickButton = new GButton(this, () -> gamepad.left_stick_button);
		this.rightJoystickButton = new GButton(this, () -> gamepad.right_stick_button);

		this.leftJoystick = new GJoystick(this, () -> gamepad.left_stick_x, () -> gamepad.left_stick_y * Constants.GAMEPAD_JOYSTICK_Y_COEFFICIENT);
		this.rightJoystick = new GJoystick(this, () -> gamepad.right_stick_x, () -> gamepad.right_stick_y * Constants.GAMEPAD_JOYSTICK_Y_COEFFICIENT);

		this.leftTrigger = new GTrigger(this, () -> gamepad.left_trigger);
		this.rightTrigger = new GTrigger(this, () -> gamepad.right_trigger);
	}

	@Override
	public void loop() {
		this.x.loop();
		this.y.loop();
		this.a.loop();
		this.b.loop();

		this.leftBumper.loop();
		this.rightBumper.loop();

		this.dpadUp.loop();
		this.dpadDown.loop();
		this.dpadLeft.loop();
		this.dpadRight.loop();

		this.leftJoystickButton.loop();
		this.rightJoystickButton.loop();

		this.leftJoystick.loop();
		this.rightJoystick.loop();

		this.leftTrigger.loop();
		this.rightTrigger.loop();
	}
}
