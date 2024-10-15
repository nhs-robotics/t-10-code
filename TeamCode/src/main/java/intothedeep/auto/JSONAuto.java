package intothedeep.auto;

import android.content.Context;

import t10.bootstrap.AutonomousOpMode;
import t10.novel.mecanum.MecanumDriver;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import javax.json.*;

import intothedeep.Constants;
import intothedeep.IntoTheDeepRobotConfiguration;

public class JSONAuto extends AutonomousOpMode {
    private final String completeJSONFilePath;
    private JsonArray jsonArray;
    private IntoTheDeepRobotConfiguration config;
    private MecanumDriver driver;


    public JSONAuto(String uniqueJSONFilePath) {
        this.completeJSONFilePath = Constants.BASE_JSON_FILE_PATH + uniqueJSONFilePath;
    }

    @Override
    public void initialize() {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream(completeJSONFilePath);
            if (inputStream == null) System.out.println("Input steam is null");
            this.jsonArray = Json.createReader(inputStream).readArray();
        } catch (Exception e) {
            telemetry.addLine("JSON File Not Found!");
            e.printStackTrace();
        }
        this.config = new IntoTheDeepRobotConfiguration(this.hardwareMap);
        this.driver = new MecanumDriver(
                this.config.fl,
                this.config.fr,
                this.config.bl,
                this.config.br,
                this.config.imu,
                Constants.Coefficients.PRODUCTION_COEFFICIENTS
        );

    }

    @Override
    public void run() {
        for (int i = 0; i < this.jsonArray.size(); i++) {
            runJSONCommand(this.jsonArray.getJsonObject(i));
        }
    }

    private void runJSONCommand(JsonObject jsonCommand) {
        switch (jsonCommand.getString("Action Type")) {
            case "Lateral Movement":
                lateralMovement(jsonCommand);
                break;
            case "Rotational Movement":
                break;
            case "Manipulator Movement":
                break;
            case "Sleep":
                try {
                    sleep((long) (jsonCommand.getJsonNumber("Time").doubleValue() * 1000L));
                } catch (InterruptedException ignored) {}
                break;
        }
    }
    private void lateralMovement(JsonObject jsonObject) {
        double distX = jsonObject.getJsonNumber("Distance X").doubleValue();
        double distY = jsonObject.getJsonNumber("Distance Y").doubleValue();
        double time = jsonObject.getJsonNumber("Time").doubleValue();

        this.driver.setVelocity(new Vector3D(distX / time, distY / time, 0));

        try {
            sleep((long) (time * 1000L));
        } catch (InterruptedException ignored) {}

        this.driver.halt();
    }

    private void rotationalMovement(JsonObject jsonObject) {

    }
}
