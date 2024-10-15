package intothedeep.teleop;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class ServoTesterRobotConfiguration {
    public List<CRServo> servos;

    public ServoTesterRobotConfiguration(HardwareMap hardwareMap) {
        servos = hardwareMap.getAll(CRServo.class);
    }
}
