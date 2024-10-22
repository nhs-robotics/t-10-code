package t10.gamepad;

import intothedeep.Constants;
import t10.gamepad.input.types.GButton;
import t10.gamepad.input.types.GJoystick;
import t10.gamepad.input.types.GTrigger;
import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Declarative gamepad input framework.
 */
public class GController {
    // Gamepad buttons
    public final GButton x;
    public final GButton y;
    public final GButton a;
    public final GButton b;
    public final GButton rightBumper;
    public final GButton leftBumper;

    // Gamepad D-Pad buttons
    public final GButton dpadUp;
    public final GButton dpadDown;
    public final GButton dpadRight;
    public final GButton dpadLeft;

    // Gamepad joysticks
    public final GButton leftJoystickButton;
    public final GButton rightJoystickButton;

    public final GJoystick leftJoystick;
    public final GJoystick rightJoystick;

    // Gamepad triggers
    public final GTrigger rightTrigger;
    public final GTrigger leftTrigger;

    /**
     * Allows a gamepad's inputs to be mapped declaratively.
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

    /**
     * Updates the underlying state of the gamepad.
     * IMPORTANT: Call this method in loop.
     */
    public void update() {
        this.x.update();
        this.y.update();
        this.a.update();
        this.b.update();

        this.leftBumper.update();
        this.rightBumper.update();

        this.dpadUp.update();
        this.dpadDown.update();
        this.dpadLeft.update();
        this.dpadRight.update();

        this.leftJoystickButton.update();
        this.rightJoystickButton.update();

        this.leftJoystick.update();
        this.rightJoystick.update();

        this.leftTrigger.update();
        this.rightTrigger.update();
    }
}
