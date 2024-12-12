package intothedeep.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import intothedeep.SnowballConfig;
import t10.bootstrap.AutonomousOpMode;
import t10.geometry.MovementVector;
import t10.motion.mecanum.MecanumDriver;

@Autonomous
public class CompetitionAuto extends AutonomousOpMode {
    private SnowballConfig config;
    private MecanumDriver driver;

    @Override
    public void initialize() {
        this.config = new SnowballConfig(this.hardwareMap);
        this.driver = this.config.createMecanumDriver();
    }

    @Override
    public void run() {
        this.driver.setVelocity(new MovementVector(0, 10, 0));
        try {
            Thread.sleep(2400);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.driver.setVelocity(new MovementVector(0, 0, 0));

        // 2
        this.driver.setVelocity(new MovementVector(10, 0, 0));
        try {
            Thread.sleep(4800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.driver.setVelocity(new MovementVector(0, 0, 0));

        this.driver.setVelocity(new MovementVector(0, 10, 0));
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.driver.setVelocity(new MovementVector(0, 0, 0));

        this.driver.setVelocity(new MovementVector(-10, 0, 0));
        try {
            Thread.sleep(4800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.driver.setVelocity(new MovementVector(0, 0, 0));
    }
}
