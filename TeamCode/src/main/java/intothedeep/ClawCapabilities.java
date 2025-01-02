package intothedeep;

import com.qualcomm.robotcore.hardware.Servo;

public class ClawCapabilities {
    private final SnowballConfig c;
    private final Servo claw;
    private boolean isOpen;
    private final double openPosition = 0.1;
    private final double closedPosition = 0.9;

    public ClawCapabilities(SnowballConfig c) {
        this.c = c;
        this.claw = this.c.claw;
        this.isOpen = false;
    }

    public void open() {
        if (isOpen) return;
        claw.setPosition(openPosition);
        isOpen = true;
    }

    public void close() {
        if (!isOpen) return;
        claw.setPosition(closedPosition);
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isClosed() {
        return !isOpen;
    }
}
