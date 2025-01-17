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

    public void setOpen(boolean open) {
        this.isOpen = open;

        if (open) {
            this.claw.setPosition(OPEN_POSITION);
        } else {
            this.claw.setPosition(CLOSED_POSITION);
        }
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public void toggle() {
        this.setOpen(!this.isOpen);
    }
}
