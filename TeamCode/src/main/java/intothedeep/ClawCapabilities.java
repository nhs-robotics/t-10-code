package intothedeep;

import com.qualcomm.robotcore.hardware.Servo;

public class ClawCapabilities {
    private final SnowballConfig c;
    private final Servo claw;
    private boolean isOpen;
    private final double OPEN_POSITION = 0.05;
    private final double CLOSED_POSITION = 0.55;

    public ClawCapabilities(SnowballConfig c) {
        this.c = c;
        this.claw = this.c.claw;
        this.isOpen = false;
    }

    public void open() {
        if (isOpen) return;
        claw.setPosition(OPEN_POSITION);
        isOpen = true;
    }

    public void close() {
        if (!isOpen) return;
        claw.setPosition(CLOSED_POSITION);
        isOpen = false;
    }

    public void setPosition(boolean open) {
        if (open)
            open();
        else
            close();
    }

    public void toggle() {
        setPosition(!isOpen);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isClosed() {
        return !isOpen;
    }
}
