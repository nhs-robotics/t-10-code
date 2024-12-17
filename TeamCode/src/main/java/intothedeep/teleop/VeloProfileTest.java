package intothedeep.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import t10.bootstrap.TeleOpOpMode;
import t10.gamepad.GController;
import t10.metrics.Metric;
import t10.motion.profile.TrapezoidalMotionProfile;

@TeleOp
public class VeloProfileTest extends TeleOpOpMode {
    private GController gamepadController;
    private Telemetry.Item x;
    private Telemetry.Item y;
    private Telemetry.Item r;
    private Telemetry.Item vert, hor, state, changeDir, veloProfile, profileDone, vInitVal, pos;
    private TrapezoidalMotionProfile profile;
    private double dist = 20, lookAhead = 2, vFinal = 0, acceleration = 10, vInit = 0, position = 0;

    @Metric
    public int microMovement;

    @Override
    public void initialize() {
        profile = new TrapezoidalMotionProfile(0, 15,vFinal,acceleration,dist,lookAhead);
        this.x = this.telemetry.addData("Distance: ", "0");
        this.y = this.telemetry.addData("lookAhead: ", "0");
        this.r = this.telemetry.addData("vFinal: ", "0");
        this.state = this.telemetry.addData("",profile.state);
        this.vInitVal = this.telemetry.addData("vInit: ", "0");
        this.veloProfile = this.telemetry.addData("velocity profile: ", "0");
        this.pos = this.telemetry.addData("position: ","0");
        this.changeDir = this.telemetry.addData("changedDirection: ","0");
        this.profileDone = this.telemetry.addData("profile done: ","0");
        /*
        this.c = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = this.c.createMecanumDriver();
        this.odometry = this.c.createOdometry();
        */
        this.gamepadController = new GController(this.gamepad1)
                .x.initialToggleState(true).ok()  // micro-movement
                .dpadUp.onPress(() -> dist += 10).ok()
                .dpadDown.onPress(() -> dist -= 10).ok()
                .y.onPress(() -> position += 1).ok()
                .a.onPress(() -> position -= 1).ok()
                .rightBumper.onPress(() -> vFinal += 1).ok()
                .leftBumper.onPress(() -> vFinal -= 1).ok()
                .dpadRight.onPress(() -> vInit += 5).ok()
                .dpadLeft.onPress(() -> vInit -= 5).ok()
                .b.onPress(() -> profile = new TrapezoidalMotionProfile(vInit, 15,vFinal,acceleration,dist,lookAhead)).ok();
    }

    @Override
    public void loop() {
        this.microMovement = this.gamepadController.x.isToggled() ? 1 : 0;
        this.x.setValue(dist);
        this.y.setValue(lookAhead);
        this.r.setValue(vFinal);
        this.vInitVal.setValue(vInit);
        this.pos.setValue(position);
        this.state.setValue(profile.state);

        this.veloProfile.setValue(this.profile.getVelocity(position));
        this.changeDir.setValue(this.profile.changedDirection);
        this.profileDone.setValue(this.profile.isDone());

        this.gamepadController.update();
        this.telemetry.update();
    }
}
