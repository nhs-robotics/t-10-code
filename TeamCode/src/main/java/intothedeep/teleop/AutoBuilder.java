package intothedeep.teleop;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import intothedeep.IntoTheDeepRobotConfiguration;
import intothedeep.KevinRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.novel.mecanum.MecanumDriver;
import t10.novel.odometry.NovelOdometry;
import t10.reconstructor.Pose;
import t10.utils.MovementVector;

@TeleOp(name = "Auto Builder")
public class AutoBuilder extends TeleOpOpMode {
    private Writer autoFileWriter;
    private static final String BASE_DIRECTORY_PATH = "GeneratedAutos";
    private static final double motorSpeed = 10;

    private MecanumDriver driver;
    private IntoTheDeepRobotConfiguration c;
    private NovelOdometry odometry;
    private CommandType commandType;
    private boolean commandTypeSet;

    @Override
    public void initialize() {
        print("Initializing...");
        this.c = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.odometry = c.createOdometry();
        this.driver = c.createMecanumDriver();

        this.commandType = CommandType.VERTICAL;

        String directoryPath = Environment.getExternalStorageDirectory().getPath()+"/"+BASE_DIRECTORY_PATH;
        File directory = new File(directoryPath);
        directory.mkdir();
        String filename = "auto_" + (SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()))
                .replaceAll(", |:| ", "_");

        try {
            autoFileWriter = new FileWriter(directoryPath+"/"+filename+".java");

            autoFileWriter.append("// Auto-Generated Autonomous Opmode " + filename + " Created by AutoBuilder TeleOp\n");
            autoFileWriter.append("package intothedeep.auto;\n");
            autoFileWriter.append("\n");
            autoFileWriter.append("import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n");
            autoFileWriter.append("\n");
            autoFileWriter.append("@Autonomous(name = \"" + filename + "\")\n");
            autoFileWriter.append("public class " + filename + " extends EasyAuto {\n");
            autoFileWriter.append("\t@Override\n");
            autoFileWriter.append("\tpublic void run() {\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sleep(1);
        print(new String[] {"Initialization Complete",
                "Make sure the clock is turned off before running."});
    }

    @Override
    public void loop() {
        this.odometry.update();

         if (commandTypeSet) {
            print(new String[] {"Use Joysticks to move",
            "Press [RB] to save movement",
            "ODOMETRY: " + odometry.getRelativePose().getY()});
            if (gamepad1.right_bumper) {
                this.driver.halt();
                try {
                    autoFileWriter.append(generateAutoCode(commandType, this.odometry.getRelativePose()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                commandTypeSet = false;
            } else {
                MovementVector movementVector = new MovementVector(0, 0, 0);
                switch (commandType) {
                    case VERTICAL:
                        movementVector = new MovementVector(this.gamepad1.left_stick_y * motorSpeed, 0, 0);
                        break;
                    case HORIZONTAL:
                        movementVector = new MovementVector(0, this.gamepad1.left_stick_x * motorSpeed, 0);
                        break;
                    case DIAGONAL:
                        movementVector = new MovementVector(this.gamepad1.left_stick_y * motorSpeed, this.gamepad1.left_stick_x * motorSpeed, 0);
                        break;
                    case ROTATIONAL:
                        movementVector = new MovementVector(0, 0, this.gamepad1.right_stick_x * motorSpeed);
                        break;
                }
                this.driver.setVelocity(movementVector);
            }
        } else {
            print(new String[] {"Choose command type:",
            "[A]: Vertical",
            "[B]: Horizontal",
            "[X]: Diagonal",
            "[Y]: Rotational",
            "[LB]: Finish & Save"});
            if (gamepad1.a) {
                commandType = CommandType.VERTICAL;
                commandTypeSet = true;
            } else if (gamepad1.b) {
                commandType = CommandType.HORIZONTAL;
                commandTypeSet = true;
            } else if (gamepad1.x) {
                commandType = CommandType.DIAGONAL;
                commandTypeSet = true;
            } else if (gamepad1.y) {
                commandType = CommandType.ROTATIONAL;
                commandTypeSet = true;
            } else if (gamepad1.left_bumper) {
                stop();
            }
        }
    }

    @Override
    public void stop() {
        try {
            autoFileWriter.append("\t}\n");
            autoFileWriter.append("}\n");
            autoFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateAutoCode(CommandType commandType, Pose pose) {
        switch (commandType) {
            case VERTICAL:
                return "\t\tverticalMovement(" + pose.getY() + ");\n";
            case HORIZONTAL:
                return "\t\thorizontalMovement(" + pose.getX() + ");\n";
            case DIAGONAL:
                return "\t\tdiagonalMovement(" + pose.getX() + ", " + pose.getY() + ");\n";
            case ROTATIONAL:
                return "\t\tturnTo(" + pose.getHeading(AngleUnit.DEGREES) + ");\n";
        }
        return "";
    }

    private void print(String line) {
        telemetry.clear();
        telemetry.addLine(line);
        telemetry.update();
    }

    private void print(String[] lines) {
        telemetry.clear();
        for (String line : lines) {
            telemetry.addLine(line);
        }
        telemetry.update();
    }
}

enum CommandType {
    VERTICAL,
    HORIZONTAL,
    DIAGONAL,
    ROTATIONAL
}