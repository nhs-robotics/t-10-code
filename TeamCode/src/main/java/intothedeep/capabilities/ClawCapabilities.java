package intothedeep.capabilities;

import com.qualcomm.robotcore.hardware.Servo;

import intothedeep.SnowballConfig;

public class ClawCapabilities {
    private static final double OPEN_POSITION = 0.05;
    private static final double CLOSED_POSITION = 0.55;
    private final Servo claw;
    private boolean isOpen;

    public ClawCapabilities(SnowballConfig c) {
        this.claw = c.claw;
    }

    public void setPosition(boolean open) {
        this.isOpen = open;

        if (open) {
            claw.setPosition(OPEN_POSITION);
        } else {
            claw.setPosition(CLOSED_POSITION);
        }
    }

    public void toggle() {
        setPosition(!this.isOpen);
    }
}
