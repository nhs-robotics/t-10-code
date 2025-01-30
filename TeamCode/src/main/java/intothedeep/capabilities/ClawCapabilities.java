package intothedeep.capabilities;

import intothedeep.Constants;
import intothedeep.SnowballConfig;
import t10.motion.hardware.Motor;

public class ClawCapabilities {
    private static final double CLAW_OPEN_POSITION = 0.05;
    private static final double CLAW_CLOSED_POSITION = 0.55;

    private static final double TWIST_FORWARD_POSITION = 0.0; // TODO CONFIRM
    private static final double TWIST_SIDEWAYS_POSITION = 0.5; // TODO CONFIRM
    private static final double TWIST_BACKWARD_POSITION = 1.0; // TODO CONFIRM
    private static final double TWIST_INITIALIZED_POSITION = -1.0; // TODO SET

    private static final double ROTATE_UPWARD_POSITION = 0.0; // TODO CONFIRM
    private static final double ROTATE_FORWARD_POSITION = 0.5; // TODO CONFIRM
    private static final double ROTATE_DOWNWARD_POSITION = 1.0; // TODO CONFIRM
    private static final double ROTATE_INITIALIZED_POSITION = -1.0; // TODO SET

    private final SnowballConfig config;

    private boolean isOpen;
    private double twist;
    private double rotation;
    private TargetRotation targetRotation;

    public ClawCapabilities(SnowballConfig c) {
        this.config = c;
    }

    private void setOpen(boolean open) {
        this.isOpen = open;

        if (open) {
            this.config.clawServo.setPosition(CLAW_OPEN_POSITION);
        } else {
            this.config.clawServo.setPosition(CLAW_CLOSED_POSITION);
        }
    }

    private void setTwist(double twist) {
        this.twist = twist;
        this.config.twistServo.setPosition(twist);
    }

    private void setRotationRelative(double rotation) {
        this.rotation = rotation;
        this.config.leftRotateServo.setPosition(rotation);
        this.config.rightRotateServo.setPosition(1 - rotation); // Servos are oriented opposite to each other.
        this.targetRotation = null;
    }

    private void setRotationAbsolute(double targetAngleClaw, Motor arm) {
        double currentAngleArm = arm.motor.getCurrentPosition() / Constants.TickCounts.LIFT_MOTOR_TICK_COUNT;
        currentAngleArm *= 4;
        // Linear interpolation between upward position and forward position, determined by correcting angle.
        double newClawRotation = (targetAngleClaw - currentAngleArm) * (ROTATE_UPWARD_POSITION - ROTATE_FORWARD_POSITION) + ROTATE_FORWARD_POSITION;
        setRotationRelative(newClawRotation);
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public double getTwist() {
        return this.twist;
    }

    public double getRotation() {
        return this.rotation;
    }

    public void openClaw() {
        setOpen(true);
    }

    public void closeClaw() {
        setOpen(false);
    }

    public void toggleClaw() {
        this.setOpen(!this.isOpen);
    }

    public void twistForward() {
        setTwist(TWIST_FORWARD_POSITION);
    }

    public void twistSideways() {
        setTwist(TWIST_SIDEWAYS_POSITION);
    }

    public void twistBackward() {
        setTwist(TWIST_BACKWARD_POSITION);
    }

    public void rotateRelativeUpward() {
        setRotationRelative(ROTATE_UPWARD_POSITION);
    }

    public void rotateRelativeForward() {
        setRotationRelative(ROTATE_FORWARD_POSITION);
    }

    public void rotateRelativeDownward() {
        setRotationRelative(ROTATE_DOWNWARD_POSITION);
    }

    public void rotateAbsoluteDownward(Motor arm) {
        setRotationAbsolute(-1, arm);
        this.targetRotation = TargetRotation.DOWNWARD;
    }

    public void rotateAbsoluteForward(Motor arm) {
        setRotationAbsolute(0, arm);
        this.targetRotation = TargetRotation.FORWARD;
    }

    public void initializePosition() {
        openClaw();
        setTwist(TWIST_INITIALIZED_POSITION);
        setRotationRelative(ROTATE_INITIALIZED_POSITION);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
            // Do nothing.
        } finally {
            closeClaw();
        }
    }

    public void update() {
        switch (this.targetRotation) {
            case FORWARD:
                rotateAbsoluteForward(config.armRotation);
                break;
            case DOWNWARD:
                rotateAbsoluteDownward(config.armRotation);
                break;
        }
    }
}

enum TargetRotation {
    FORWARD,
    DOWNWARD
}