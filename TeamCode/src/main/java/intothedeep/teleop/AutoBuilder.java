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
import t10.bootstrap.AbstractRobotConfiguration;
import t10.bootstrap.TeleOpOpMode;
import t10.geometry.MovementVector;
import t10.geometry.Pose;
import t10.localizer.odometry.OdometryLocalizer;
import t10.motion.mecanum.MecanumDriver;
import t10.utils.Alliance;

@TeleOp(name = "Auto Builder")
public class AutoBuilder extends TeleOpOpMode {
    private Writer redAutoFileWriter;
    private Writer blueAutoFileWriter;
    private static final String BASE_DIRECTORY_PATH = "GeneratedAutos";
    private static final double motorSpeed = 10;
    double startingTile = 0;

    private MecanumDriver driver;
    private OdometryLocalizer odometry;
    private CommandType commandType;
    private boolean startingTileSet;
    private boolean commandTypeSet;

    @Override
    public void initialize() {
        AbstractRobotConfiguration c = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.odometry = c.createOdometry();
        this.driver = c.createMecanumDriver();

        this.commandType = CommandType.VERTICAL;

        sleep(1);
        print(new String[] {"Initialization Complete",
                "Make sure the clock is turned off before running."});
    }

    @Override
    public void loop() {
        try {
            this.odometry.update();

            if (!startingTileSet) {
                print(new String[]{"Select Starting Tile:",
                        "1: [A]",
                        "2: [B]",
                        "3: [X]",
                        "4: [Y]",
                        "5: [UP]",
                        "6: [DOWN]"});
                if (gamepad1.a) {
                    startingTile = 1;
                } else if (gamepad1.b) {
                    startingTile = 2;
                } else if (gamepad1.x) {
                    startingTile = 3;
                } else if (gamepad1.y) {
                    startingTile = 4;
                } else if (gamepad1.dpad_up) {
                    startingTile = 5;
                } else if (gamepad1.dpad_down) {
                    startingTile = 6;
                }
                if (startingTile != 0) {
                    redAutoFileWriter = initializeFileWriter(Alliance.RED);
                    blueAutoFileWriter = initializeFileWriter(Alliance.BLUE);
                    sleep(1);
                    startingTileSet = true;
                }
            } else if (commandTypeSet) {
                print(new String[]{"Use Joysticks to move",
                        "Press [RB] to save movement"});
                if (gamepad1.right_bumper) {
                    this.driver.halt();
                    String newAutoCode = generateAutoCode(commandType, this.odometry.getFieldCentricPose());
                    redAutoFileWriter.append(newAutoCode);
                    blueAutoFileWriter.append(newAutoCode);
                    this.odometry.setFieldCentricPose(new Pose(0, 0, this.odometry.getFieldCentricPose().getHeading(AngleUnit.DEGREES), AngleUnit.DEGREES));
                    commandTypeSet = false;
                } else {
                    MovementVector movementVector = new MovementVector(0, 0, 0);
                    switch (commandType) {
                        case VERTICAL:
                            movementVector = new MovementVector(this.gamepad1.left_stick_y, 0, 0);
                            break;
                        case HORIZONTAL:
                            movementVector = new MovementVector(0, this.gamepad1.left_stick_x, 0);
                            break;
                        case DIAGONAL:
                            movementVector = new MovementVector(this.gamepad1.left_stick_y, this.gamepad1.left_stick_x, 0);
                            break;
                        case ROTATIONAL:
                            movementVector = new MovementVector(0, 0, this.gamepad1.right_stick_x);
                            break;
                    }
                    this.driver.setVelocity(movementVector.scalarMultiply(motorSpeed));
                }
            } else {
                print(new String[]{"Choose command type:",
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            redAutoFileWriter.append("\t}\n");
            blueAutoFileWriter.append("\t}\n");
            redAutoFileWriter.append("}\n");
            blueAutoFileWriter.append("}\n");
            redAutoFileWriter.close();
            blueAutoFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Writer initializeFileWriter(Alliance alliance) {
        String directoryPath = Environment.getExternalStorageDirectory().getPath()+"/"+BASE_DIRECTORY_PATH;
        File directory = new File(directoryPath);
        if (!directory.mkdir()) {
            print(new String[] {"Could not create directory"});
        }
        String filename = "auto_" + (SimpleDateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()))
                .replaceAll(", |:| ", "_")
                + "_" + (alliance == Alliance.RED ? "red" : "blue");

        try {
            Writer autoFileWriter = new FileWriter(directoryPath+"/"+filename+".java");

            autoFileWriter.append("// Auto-Generated Autonomous Opmode " + filename + " Created by AutoBuilder TeleOp\n");
            autoFileWriter.append("package intothedeep.auto.generatedAutos;\n");
            autoFileWriter.append("\n");
            autoFileWriter.append("import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n");
            autoFileWriter.append("import intothedeep.auto.EasyAuto;\n");
            autoFileWriter.append("import t10.utils.Alliance;\n");
            autoFileWriter.append("\n");
            autoFileWriter.append("@Autonomous(name = \"" + filename + "\")\n");
            autoFileWriter.append("public class " + filename + " extends EasyAuto {\n");
            autoFileWriter.append("\tpublic " + filename + "() {\n");
            autoFileWriter.append("\t\tsuper(" + (alliance == Alliance.RED ? "Alliance.RED, " : "Alliance.BLUE, ") + startingTile + ");\n");
            autoFileWriter.append("\t}\n\n");
            autoFileWriter.append("\t@Override\n");
            autoFileWriter.append("\tpublic void run() {\n");
            return autoFileWriter;
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