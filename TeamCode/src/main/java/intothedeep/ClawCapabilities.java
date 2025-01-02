package intothedeep;

import com.qualcomm.robotcore.hardware.Servo;

public class ClawCapabilities {
    private final SnowballConfig c;
    private final Servo claw;
    private boolean isOpen;

    public ClawCapabilities(SnowballConfig c) {
        this.c = c;
        this.claw = this.c.claw;
    }

    public void open() {
        if (isOpen) return;
        claw.setPosition(0.1);
        isOpen = true;
    }

    public void close() {
        if (!isOpen) return;
        claw.setPosition(0.9);
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isClosed() {
        return !isOpen;
    }
}
